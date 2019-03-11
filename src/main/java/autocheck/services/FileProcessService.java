package autocheck.services;

import autocheck.models.*;
import com.google.common.collect.Iterables;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.xpath.operations.Bool;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.impl.STAlignHImpl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHighlightColor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy.CREATE_NULL_AS_BLANK;

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
    private TeamRepository teamRepository;

    @Autowired
    private ItemProcessService itemProcessService;

    @Value("${file.path}")
    private String path;

    private List<STHighlightColor.Enum> colorArray = Arrays.asList(
            STHighlightColor.YELLOW,
            STHighlightColor.BLUE,
            STHighlightColor.CYAN,
            STHighlightColor.GREEN,
            STHighlightColor.RED,
            STHighlightColor.MAGENTA,
//            STHighlightColor.LIGHT_GRAY,
            STHighlightColor.DARK_CYAN,
            STHighlightColor.DARK_YELLOW,
            STHighlightColor.DARK_BLUE
            );

    @Async
    public void processDev(Document doc) throws IOException {
        logger.info("Start processing deviation file " + doc.getFilename());

        String filepath = path + doc.getFilepath();

        // Process
        File file = new File(filepath);
        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file));

        XSSFSheet sheet = wb.getSheetAt(0);
        XSSFRow row;
        int rowNum = sheet.getPhysicalNumberOfRows();
        Deviation dev;
        String team_name;

        for (int i = 1; i < rowNum; ++i) {
            row = sheet.getRow(i);
            dev = new Deviation();
            dev.setDoc_id(doc.getId());

            if(row.getCell(1, CREATE_NULL_AS_BLANK).getCellTypeEnum() == CellType.NUMERIC) {
                dev.setChapter_var_d(NumberToTextConverter.toText(row.getCell(1, CREATE_NULL_AS_BLANK).getNumericCellValue()));
            } else {
                dev.setChapter_var_d(row.getCell(1, CREATE_NULL_AS_BLANK).getStringCellValue());
            }

            if(row.getCell(2, CREATE_NULL_AS_BLANK).getCellTypeEnum() == CellType.NUMERIC) {
                dev.setChapter_var_d(NumberToTextConverter.toText(row.getCell(2, CREATE_NULL_AS_BLANK).getNumericCellValue()));
            } else {
                dev.setChapter_var_d(row.getCell(2, CREATE_NULL_AS_BLANK).getStringCellValue());
            }

            if(row.getCell(3, CREATE_NULL_AS_BLANK).getCellTypeEnum() == CellType.NUMERIC) {
                dev.setChapter_var_d(NumberToTextConverter.toText(row.getCell(3, CREATE_NULL_AS_BLANK).getNumericCellValue()));
            } else {
                dev.setChapter_var_d(row.getCell(3, CREATE_NULL_AS_BLANK).getStringCellValue());
            }

            if(row.getCell(4, CREATE_NULL_AS_BLANK).getCellTypeEnum() == CellType.NUMERIC) {
                dev.setChapter_var_d(NumberToTextConverter.toText(row.getCell(4, CREATE_NULL_AS_BLANK).getNumericCellValue()));
            } else {
                dev.setChapter_var_d(row.getCell(4, CREATE_NULL_AS_BLANK).getStringCellValue());
            }

            dev.setRfq_content_cn(row.getCell(5, CREATE_NULL_AS_BLANK).getStringCellValue());
            dev.setRfq_keysent_cn(row.getCell(6, CREATE_NULL_AS_BLANK).getStringCellValue());


//            dev.setDev_content_cn(row.getCell(7, CREATE_NULL_AS_BLANK).getStringCellValue());
            XSSFRichTextString dev_text = row.getCell(7, CREATE_NULL_AS_BLANK).getRichStringCellValue();
            String source_dev_text = row.getCell(7, CREATE_NULL_AS_BLANK).getStringCellValue();
            StringBuilder real_dev_text = new StringBuilder();
            boolean is_strike = false;
            for (int pos = 0; pos < dev_text.length(); ++pos) {
                if (dev_text.getFontAtIndex(pos) != null && dev_text.getFontAtIndex(pos).getStrikeout()) {
                    is_strike = true;
                } else {
                    real_dev_text.append(source_dev_text.charAt(pos));
                }
            }
            if (is_strike) {
                dev.setDev_content_cn("删除。\n" + real_dev_text.toString());
            } else {
                dev.setDev_content_cn(real_dev_text.toString());
            }

            dev.setContract_wording_cn(row.getCell(8, CREATE_NULL_AS_BLANK).getStringCellValue());
            dev.setRfq_content_en(row.getCell(9, CREATE_NULL_AS_BLANK).getStringCellValue());
            dev.setCategory(row.getCell(10, CREATE_NULL_AS_BLANK).getStringCellValue());

            dev.setCost_1(row.getCell(11, CREATE_NULL_AS_BLANK).getNumericCellValue());
            dev.setCost_2(row.getCell(12, CREATE_NULL_AS_BLANK).getNumericCellValue());
            dev.setCost_3(row.getCell(13, CREATE_NULL_AS_BLANK).getNumericCellValue());
            dev.setCost_4(row.getCell(14, CREATE_NULL_AS_BLANK).getNumericCellValue());
            dev.setCost_5(row.getCell(15, CREATE_NULL_AS_BLANK).getNumericCellValue());

            team_name = row.getCell(16, CREATE_NULL_AS_BLANK).getStringCellValue();
            List<Team> this_team = teamRepository.findByName(team_name);
            if (this_team.size() == 0) {
                Team team = new Team();
                team.setName(team_name);
                teamRepository.save(team);
            }
            dev.setTeam(team_name);
            dev.setLink_support_doc(row.getCell(17, CREATE_NULL_AS_BLANK).getStringCellValue());

            dev.setSpare_1(row.getCell(18, CREATE_NULL_AS_BLANK).getStringCellValue());
            dev.setSpare_2(row.getCell(19, CREATE_NULL_AS_BLANK).getStringCellValue());
            dev.setSpare_3(row.getCell(20, CREATE_NULL_AS_BLANK).getStringCellValue());
            dev.setSpare_4(row.getCell(21, CREATE_NULL_AS_BLANK).getStringCellValue());
            dev.setSpare_5(row.getCell(22, CREATE_NULL_AS_BLANK).getStringCellValue());

            deviationRepository.save(dev);
        }

        doc.setStatus(2);
        documentRepository.save(doc);
        logger.info("Inserted " + (rowNum-1) + " deviation records");
    }

    @Async
    public void processDoc(Document doc, Integer model, Integer rfqVar, Integer simAlgo, Integer level) throws IOException {
        logger.info("Start processing RFQ document file " + doc.getFilename());

        // Heading Values
        String[] values = {"Heading1","Heading2","Heading3","Heading 1","Heading 2","Heading 3","heading 1","heading 2","heading 3","heading1","heading2","heading3"};

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        String filepath = path + doc.getFilepath();
        String resultpath = filepath + "_deviation.xlsx";
        String resultDocPath = filepath + "_new.docx";

        File newDocFile = new File(filepath);
        XWPFDocument newDoc = new XWPFDocument(new FileInputStream(newDocFile));
//        newDoc.setTrackRevisions();
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

        // Add Headers
        row = sheet.createRow(rowNum);
        cell = row.createCell(0); // ID
        cell.setCellValue("ID");
        cell = row.createCell(1); // chapter
        cell.setCellValue("Chapter");
        cell = row.createCell(2); // source_content
        cell.setCellValue("Old RFQ content");
        cell = row.createCell(3); // new RFQ
        cell.setCellValue("New RFQ content");
        cell = row.createCell(4); // Dev_content
        cell.setCellValue("Deviation");
        cell = row.createCell(5); // Category
        cell.setCellValue("Category");
        cell = row.createCell(6); // Cost
        cell.setCellValue("Cost impact in kEUR for 2 units");
        cell = row.createCell(7); // Team
        cell.setCellValue("Team");
        cell = row.createCell(8); // link
        cell.setCellValue("Supporting document link");
        cell = row.createCell(9); // Match Value
        cell.setCellValue("Match value");
        rowNum += 1;

        // Process Document
//        File file = new File(filepath);
//        XWPFDocument document = new XWPFDocument(new FileInputStream(file));
//        List<XWPFParagraph> paragraphs = document.getParagraphs();

        List<CoreSentence> sentences = new ArrayList<>();
        List<CoreSentence> cur_sentences;
//        List<String> sentences = new ArrayList<>();
//        List<String> cur_sentences;
        List<Integer> p_ids = new ArrayList<>();
        List<String> headings = new ArrayList<>();
        String heading = "", paragraph_text;
        XWPFStyles styles = newDoc.getStyles();
        Integer pos = 0;
        for (XWPFParagraph paragraph:newParagraphs) {
            if (paragraph.getStyleID() != null) {
                boolean contains = Arrays.stream(values).anyMatch(styles.getStyle(paragraph.getStyleID()).getName()::equals);
                if (contains) {
                    heading = paragraph.getText().split(" ")[0];
                }
            }

            paragraph_text = paragraph.getText();
            CoreDocument para_doc = new CoreDocument(paragraph_text);
            pipeline.annotate(para_doc);
            cur_sentences = para_doc.sentences();
//            cur_sentences = Arrays.asList(paragraph_text.split("。"));
            sentences.addAll(cur_sentences);
            headings.addAll(Collections.nCopies(cur_sentences.size(), heading));
            p_ids.addAll(Collections.nCopies(cur_sentences.size(), pos));
            ++pos;
        }

        // Get type devations and calculate TF-IDF
        Iterable<Deviation> devs = deviationRepository.findAll();

        int dev_tot = Iterables.size(devs);
        Collection<Future<List<String>>> results_dev = new ArrayList<>(dev_tot);
        for (Deviation dev: devs) {
            if (level == 0 && dev.getRfq_content_cn().length() == 0) continue;
            if (level == 1 && dev.getRfq_keysent_cn().length() == 0) continue;
            results_dev.add(itemProcessService.findSimilarDev(dev, sentences, headings, model, rfqVar, simAlgo, level));
        }

        // wait for all threads
        for (Future<List<String>> result: results_dev) {
            try {
                List<String> text_result = result.get();
                if (text_result.size() != 0) {
                    // Add to Excel
                    row = sheet.createRow(rowNum);
                    cell = row.createCell(0); // ID
                    cell.setCellValue(rowNum);
                    cell = row.createCell(1);
                    cell.setCellValue(text_result.get(0));
                    cell = row.createCell(2);
                    cell.setCellValue(text_result.get(1));
                    cell = row.createCell(3);
                    cell.setCellValue(text_result.get(2));
                    cell = row.createCell(4);
                    cell.setCellValue(text_result.get(3));
                    cell = row.createCell(5);
                    cell.setCellValue(text_result.get(4));
                    cell = row.createCell(6);
                    cell.setCellValue(text_result.get(5));
                    cell = row.createCell(7);
                    cell.setCellValue(text_result.get(6));
                    cell = row.createCell(8);
                    cell.setCellValue(text_result.get(7));
                    cell = row.createCell(9);
                    cell.setCellValue(text_result.get(8));

                    startRow = Integer.parseInt(text_result.get(9));
                    endRow = Integer.parseInt(text_result.get(10));

                    Long team_id = teamRepository.findByName(text_result.get(6)).get(0).getId();

                    for (int p_idx = p_ids.get(startRow); p_idx < p_ids.get(endRow); ++p_idx) {
                        newParagraph = newParagraphs.get(p_idx);
                        for (XWPFRun pRun: newParagraph.getRuns()) {
                            pRun.getCTR().addNewRPr().addNewHighlight().setVal(colorArray.get(team_id.intValue()));
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
            int para_tot = newParagraphs.size();
            // Get old paragraphs from database
            Iterable<Sentence> paras = sentenceRepository.findAll();
            Collection<Future<Boolean>> results_para = new ArrayList<>(para_tot);
            for (XWPFParagraph paragraph: newParagraphs) {
                results_para.add(itemProcessService.checkSimilarPara(paragraph.getText(), paras));
            }

            // wait for all threads
            int para_num = 0;
            for (Future<Boolean> result: results_para) {
                try {
                    Boolean checkSim = result.get();
                    if (!checkSim) {
                        newParagraph = newParagraphs.get(para_num);
                        for (XWPFRun pRun: newParagraph.getRuns()) {
                            pRun.getCTR().addNewRPr().addNewHighlight().setVal(STHighlightColor.LIGHT_GRAY);
                        }
                    }
                    para_num += 1;
                } catch (InterruptedException | ExecutionException e) {
                    //handle thread error
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
        logger.info("Start splitting deviation document file " + doc.getFilename());

//        Properties props = new Properties();
//        props.setProperty("annotators", "tokenize,ssplit");
//        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        String filepath = path + doc.getFilepath();
        // Process Document
        File file = new File(filepath);
        XWPFDocument document = new XWPFDocument(new FileInputStream(file));
        List<XWPFParagraph> paragraphs = document.getParagraphs();
//        List<XWPFRun> pruns;

        int tot = 0;

        for (XWPFParagraph paragraph: paragraphs) {
//            pruns = paragraph.getRuns();
//            if (pruns.size() == 0 || pruns.get(0).isHighlighted()) continue;
            String p_text = paragraph.getText();
            Sentence sentence = new Sentence();
            sentence.setDoc_id(doc.getId());
//            sentence1.setType(doc.getType());
            sentence.setText(p_text);
            sentenceRepository.save(sentence);
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
        logger.info("Finish splitting, the document has " + tot + " paragraphs.");
    }
}
