package autocheck.services;

import autocheck.models.*;
//import edu.stanford.nlp.pipeline.CoreDocument;
//import edu.stanford.nlp.pipeline.CoreSentence;
//import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.xm.similarity.text.CosineSimilarity;
import org.xm.similarity.text.TextSimilarity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

@Service
public class FileProcessService {
    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DeviationRepository deviationRepository;

    @Autowired
    private SentenceRepository sentenceRepository;

    private String path = "/Users/sefer/Documents/FDU/Lab/Project/Siemens/autocheck/file/";

    @Async
    public void processDev(Document doc) throws IOException {
        System.out.println("Start processing deviation file " + doc.getFilename());

        String filepath = path + doc.getFilepath();

        // Process
        File file = new File(filepath);
        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file));

        // TODO: 新建 Deviation 表；确定表格格式；为每行记录1. 插入到 Devation 2. 确定相似匹配
        XSSFSheet sheet = wb.getSheetAt(0);
        XSSFRow row;
        Integer rowNum = sheet.getPhysicalNumberOfRows();
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
    }

    @Async
    public void processDoc(Document doc) throws IOException {
        System.out.println("Start processing document file " + doc.getFilename());

        String filepath = path + doc.getFilepath();
        String resultpath = filepath + "_deviation.xlsx";

        // Create Deviation Excel file
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("Deviation");
        XSSFRow row;
        XSSFCell cell;
        Integer rowNum = 0;

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

        // Initialize Similarity
        TextSimilarity similarity = new CosineSimilarity();
        Double simValue, maxValue;
        Integer startRow, endRow, maxStartRow;
        String maxString, heading;

        for (Deviation dev: devs) {
            System.out.println(dev.getId());
            String dev_text = dev.getContent();
            if (dev_text.length() == 0) continue;
            StringBuilder doc_text = new StringBuilder();
            startRow = 0;
            endRow = 0;

            maxStartRow = -1;
            maxValue = 0.0;
            maxString = "";
            heading = "";

            while (startRow < paragraphs.size()) {
                while (endRow < paragraphs.size() && doc_text.toString().length() < dev_text.length()) {
                    doc_text.append(paragraphs.get(endRow).getText());
                    endRow += 1;
                }
                simValue = similarity.getSimilarity(doc_text.toString(), dev_text);
                if (simValue > maxValue) {
                    maxValue = simValue;
                    maxStartRow = startRow;
                    maxString = doc_text.toString();
                }
                startRow += 1;
                endRow = startRow;
                doc_text = new StringBuilder();
            }

            if (maxValue > doc.getThreshold()) {
                // Get Heading
                for (int i = maxStartRow; i > 0; --i) {
                    if (paragraphs.get(i).getStyle() != null && paragraphs.get(i).getStyle().contains("Heading")) {
                        heading = paragraphs.get(i).getText();
                        break;
                    }
                }

                System.out.println(rowNum);
                System.out.println("DEV TEXT" + dev_text);
                System.out.println("DOC TEXT" + maxString);
                System.out.println("Heading" + heading);

                // Add to Excel
                row = sheet.createRow(rowNum);
                cell = row.createCell(0); // ID
                cell.setCellValue(rowNum+1);
                cell = row.createCell(1); // chapter
                cell.setCellValue(heading);
                cell = row.createCell(2); // content
                cell.setCellValue(maxString);
                cell = row.createCell(3); // source_content
                cell.setCellValue(dev_text);
                cell = row.createCell(4); // Dev_content
                cell.setCellValue(dev.getD_content());
                cell = row.createCell(5); // E/C
                cell.setCellValue(dev.getDev_type());
                rowNum += 1;
            }
        }

        // process new content
        List<Sentence> new_sentences = sentenceRepository.findByDoc_id(doc.getId());
        List<Sentence> old_sentences = sentenceRepository.findByTypeAndDoc_idIsNot(doc.getType(), doc.getId());

        boolean sen_flag;
        for (Sentence sentence: new_sentences) {
            sen_flag = false;
            for (Sentence sentence1: old_sentences) {
                simValue = similarity.getSimilarity(sentence.getText(), sentence1.getText());
                if (simValue > 0.9) {
                    sen_flag = true;
                }
            }
            if (!sen_flag) {
                System.out.println(rowNum);
                System.out.println("DEV TEXT: empty");
                System.out.println("DOC TEXT" + sentence.getText());
                System.out.println("Heading: empty");

                // Add to Excel
                row = sheet.createRow(rowNum);
                cell = row.createCell(0); // ID
                cell.setCellValue(rowNum+1);
                cell = row.createCell(1); // chapter
                cell.setCellValue("");
                cell = row.createCell(2); // content
                cell.setCellValue("");
                cell = row.createCell(3); // source_content
                cell.setCellValue(sentence.getText());
                cell = row.createCell(4); // Dev_content
                cell.setCellValue("");
                cell = row.createCell(5); // E/C
                cell.setCellValue("");
                rowNum += 1;
            }
        }

        wb.write(new FileOutputStream(resultpath));

        doc.setStatus(4);
        documentRepository.save(doc);
    }

    @Async
    public void processDocSentence(Document doc) throws IOException {
        System.out.println("Start splitting document file " + doc.getFilename());

//        Properties props = new Properties();
//        props.setProperty("annotators", "tokenize,ssplit");
//        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        String filepath = path + doc.getFilepath();
        // Process Document
        File file = new File(filepath);
        XWPFDocument document = new XWPFDocument(new FileInputStream(file));
        List<XWPFParagraph> paragraphs = document.getParagraphs();

        int tot = 0;

        for (XWPFParagraph paragraph: paragraphs) {
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
        System.out.println("Finish splitting, the document has " + tot + " sentences.");
        doc.setStatus(2);
        documentRepository.save(doc);
    }
}
