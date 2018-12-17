package autocheck.services;

import autocheck.models.*;
import com.google.common.collect.Iterables;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHighlightColor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class FileProcessService {
    private static final Logger logger= LogManager.getLogger(FileProcessService.class);

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DeviationRepository deviationRepository;

    @Autowired
    private ParameterRepository parameterRepository;

    @Autowired
    private SentenceRepository sentenceRepository;

    @Autowired
    private ItemProcessService itemProcessService;

    @Value("${file.path}")
    private String path;

    @Async
    public void processDev(Document doc) throws IOException {
        logger.info("Start processing deviation file " + doc.getFilename());

        String filepath = path + doc.getFilepath();

        // Process
        File file = new File(filepath);
        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file));

        // TODO: 新建 Deviation 表；确定表格格式；为每行记录1. 插入到 Devation 2. 确定相似匹配
        XSSFSheet sheet = wb.getSheetAt(0);
        XSSFRow row;
        int rowNum = sheet.getPhysicalNumberOfRows();
        Deviation dev;

        for (int i = 3; i < rowNum; ++i) {
            row = sheet.getRow(i);
            dev = new Deviation();
            dev.setDoc_id(doc.getId());
            dev.setChapter(row.getCell(2).getStringCellValue());
            dev.setType(doc.getType());
            dev.setDev_type(row.getCell(3).getStringCellValue());
            dev.setContent(row.getCell(4).getStringCellValue());
            dev.setD_content(row.getCell(5).getStringCellValue());
            // TODO 确定相似匹配
            dev.setGroup_id(0L);
            dev.setStatus(0);
            deviationRepository.save(dev);
        }

        doc.setStatus(2);
        documentRepository.save(doc);
        logger.info("Inserted " + (rowNum-3) + " deviation records");
    }

    @Async
    public void processDoc(Document doc) throws IOException {
        logger.info("Start processing RFQ document file " + doc.getFilename());

        String filepath = path + doc.getFilepath();
        String resultpath = filepath + "_deviation.xlsx";
        String resultDocPath = filepath + "_new.docx";

        File newDocFile = new File(filepath);
        XWPFDocument newDoc = new XWPFDocument(new FileInputStream(newDocFile));
        newDoc.setTrackRevisions(true);
        List<XWPFParagraph> newParagraphs = newDoc.getParagraphs();
        XWPFParagraph newParagraph;
        int startRow, endRow;

        int new_sentence_flag = parameterRepository.findByName("Check New Sentences").get(0).getValue().intValue();

        // Create Deviation Excel file
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("Deviation");
        XSSFRow row;
        XSSFCell cell;
        int rowNum = 0;

        //TODO 加上相似度得分
        // Add Headers
        row = sheet.createRow(rowNum);
        cell = row.createCell(0); // ID
        cell.setCellValue("ID");
        cell = row.createCell(1); // chapter
        cell.setCellValue("Chapter");
        cell = row.createCell(2); // content
        cell.setCellValue("New content");
        cell = row.createCell(3); // source_content
        cell.setCellValue("Source content");
        cell = row.createCell(4); // Dev_content
        cell.setCellValue("Source deviation");
        cell = row.createCell(5); // E/C
        cell.setCellValue("E/C");
        rowNum += 1;

        // Process Document
        File file = new File(filepath);
        XWPFDocument document = new XWPFDocument(new FileInputStream(file));
        List<XWPFParagraph> paragraphs = document.getParagraphs();

        // Get type devations and calculate TF-IDF
        Iterable<Deviation> devs = deviationRepository.findByType(doc.getType());

        int dev_tot = Iterables.size(devs);
        Collection<Future<List<String>>> results_dev = new ArrayList<>(dev_tot);
        for (Deviation dev: devs) {
            String dev_text = dev.getContent();
            if (dev_text.length() == 0) continue;
            results_dev.add(itemProcessService.findSimilarDev(dev.getContent(), dev.getD_content(), dev.getDev_type(), doc.getThreshold(), paragraphs));
        }

        // wait for all threads
        String newParagraphText;
        for (Future<List<String>> result: results_dev) {
            try {
                List<String> text_result = result.get();
                if (text_result.size() != 0) {
                    // Add to Excel
                    row = sheet.createRow(rowNum);
                    cell = row.createCell(0); // ID
                    cell.setCellValue(rowNum);
                    cell = row.createCell(1); // chapter
                    cell.setCellValue(text_result.get(0));
                    cell = row.createCell(2); // content
                    cell.setCellValue(text_result.get(1));
                    cell = row.createCell(3); // source_content
                    cell.setCellValue(text_result.get(2));
                    cell = row.createCell(4); // Dev_content
                    cell.setCellValue(text_result.get(3));
                    cell = row.createCell(5); // E/C
                    cell.setCellValue(text_result.get(4));
                    startRow = Integer.parseInt(text_result.get(5));
                    endRow = Integer.parseInt(text_result.get(6));

                    for (int p_idx = startRow; p_idx < endRow; ++p_idx) {
                        newParagraph = newParagraphs.get(p_idx);
                        for (XWPFRun pRun: newParagraph.getRuns()) {
                            pRun.getCTR().addNewRPr().addNewHighlight().setVal(STHighlightColor.YELLOW);
                        }
                    }
                    rowNum += 1;
                    logger.info("Match a deviation record, total: " + (rowNum-1));
                }
            } catch (InterruptedException | ExecutionException e) {
                //handle thread error
            }
        }

        logger.info("Finished deviation processing");

        if (new_sentence_flag == 1) {
            // process new content
            logger.info("Start finding new sentences");
            List<Sentence> new_sentences = sentenceRepository.findByDoc_id(doc.getId());
            List<Sentence> old_sentences = sentenceRepository.findByTypeAndDoc_idIsNot(doc.getType(), doc.getId());

            int sentence_tot = new_sentences.size();
            Collection<Future<String>> results = new ArrayList<>(sentence_tot);
            for (Sentence new_sentence : new_sentences) {
                results.add(itemProcessService.checkSentence(new_sentence.getText(), old_sentences));
            }

            // wait for all threads
            for (Future<String> result: results) {
                try {
                    String new_text = result.get();
                    if (!new_text.isEmpty()) {
                        row = sheet.createRow(rowNum);
                        cell = row.createCell(0); // ID
                        cell.setCellValue(rowNum);
                        cell = row.createCell(1); // chapter
                        cell.setCellValue("");
                        cell = row.createCell(2); // content
                        cell.setCellValue("");
                        cell = row.createCell(3); // source_content
                        cell.setCellValue(new_text);
                        cell = row.createCell(4); // Dev_content
                        cell.setCellValue("");
                        cell = row.createCell(5); // E/C
                        cell.setCellValue("");
                        rowNum += 1;
                        logger.info("Find new sentence, total: " + (rowNum-1));
                    }
                } catch (InterruptedException | ExecutionException e) {
                    //handle thread error
                    logger.error(e.getMessage());
                }
            }
        }

        wb.write(new FileOutputStream(resultpath));
        newDoc.write(new FileOutputStream(resultDocPath));
        doc.setStatus(4);
        documentRepository.save(doc);
        logger.info("Finished RFQ document processing");
    }

    @Async
    public void processDocSentence(Document doc) throws IOException {
        logger.info("Start splitting RFQ ocument file " + doc.getFilename());

//        Properties props = new Properties();
//        props.setProperty("annotators", "tokenize,ssplit");
//        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        String filepath = path + doc.getFilepath();
        // Process Document
        File file = new File(filepath);
        XWPFDocument document = new XWPFDocument(new FileInputStream(file));
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        List<XWPFRun> pruns;

        int tot = 0;

        for (XWPFParagraph paragraph: paragraphs) {
            pruns = paragraph.getRuns();
            if (pruns.size() == 0 || pruns.get(0).isHighlighted()) continue;
            String p_text = paragraph.getText();
            Sentence sentence1 = new Sentence();
            sentence1.setDoc_id(doc.getId());
            sentence1.setType(doc.getType());
            sentence1.setText(p_text);
            sentenceRepository.save(sentence1);
            tot++;

//            CoreDocument para_doc = new CoreDocument(p_text);
//            pipeline.annotate(para_doc);
//            for (CoreSentence sentence:para_doc.sentences()) {
//                Sentence sentence1 = new Sentence();
//                sentence1.setDoc_id(doc.getId());
//                sentence1.setType(doc.getType());
//                sentence1.setText(sentence.text());
//                sentenceRepository.save(sentence1);
//                tot++;
//            }
        }
        doc.setStatus(2);
        documentRepository.save(doc);
        logger.info("Finish splitting, the document has " + tot + " sentences.");
    }
}
