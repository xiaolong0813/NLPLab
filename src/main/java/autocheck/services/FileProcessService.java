package autocheck.services;

import autocheck.models.*;
import com.google.common.collect.Iterables;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import freemarker.template.Template;
import freemarker.template.TemplateException;
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
import org.checkerframework.checker.units.qual.A;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.impl.STAlignHImpl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHighlightColor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerTemplateAvailabilityProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.xm.similarity.util.StringUtil;
//import sun.util.locale.LocaleObjectCache;

import javax.xml.bind.annotation.XmlID;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy.CREATE_NULL_AS_BLANK;

//@Configuration
@Service
public class FileProcessService {
    private static final Logger logger= LogManager.getLogger(FileProcessService.class);

    private static boolean timeCount = false;

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

    @Autowired
    private XmlRepository xmlRepository;

    @Autowired
    private XmlTagContentRepository xmlTagContentRepository;

    @Autowired
    private TranslationService translationService;

    @Value("${file.path}")
    private String path;

    @Value("${dict.path}")
    private String dictFile;

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

//    @Bean
    public String testBean() {
        return "Bean from FileProcessService";
    }

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

                    logger.info("newParagraph length: " + newParagraphs.size());
                    logger.info("start row :" + startRow);
                    logger.info("end row :" + endRow);

                    logger.info("p_idx length: " + p_ids.size());
                    logger.info("start p_idx :" + p_ids.get(startRow));
                    logger.info("end p_idx :" + p_ids.get(endRow));

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
            Collection<Future<List<String>>> results_para = new ArrayList<>(para_tot);

            List<XWPFRun> pruns;

            for (XWPFParagraph paragraph: newParagraphs) {
                // Check Highlight
                pruns = paragraph.getRuns();
                if (pruns.size() == 0 || !pruns.get(0).isHighlighted()) continue;
                results_para.add(itemProcessService.checkSimilarPara(paragraph.getText(), paras));
            }

            // wait for all threads
//            int para_num = 0;
            for (Future<List<String>> result: results_para) {
                try {
                    List<String> checkSim = result.get();
                    if (checkSim.get(1).equals("True")) {
//                        To Determine
//                        newParagraph = newParagraphs.get(para_num);
//                        for (XWPFRun pRun: newParagraph.getRuns()) {
//                            pRun.getCTR().addNewRPr().addNewHighlight().setVal(STHighlightColor.LIGHT_GRAY);
//                        }
                        logger.info(checkSim.get(0));
                    }
//                    para_num += 1;
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

    @Async
    public void processXml(Xml xml, Integer transParam) throws IOException, JSONException {
        logger.info("Start get string of xml file " + xml.getFilename());

        String filepath = path + xml.getFilepath();
        String transpath = filepath + "_translation.xml";
        String tempStr;
        XmlTagContent xmltag;
        Long xml_id = xml.getId();

        Map<String, Map> dictMap;
        Map<String, String> keyIndexMap = null;
        Map<String, String> indexValueMap = null;
        
        boolean keyFound = false;
        boolean connected = false;

//        logger.info(transParam);

        if (transParam == 1) {
            dictMap = getDictMap();
            keyIndexMap = (Map) dictMap.get("key_index");
            indexValueMap = (Map) dictMap.get("index_value");
        }


        FileSystemResource res = new FileSystemResource(filepath);
        InputStream in = res.getInputStream();

        byte[] bdata = FileCopyUtils.copyToByteArray(in);
        String xmlStr = new String(bdata, StandardCharsets.UTF_8);

        List<String> tagList = new ArrayList<String>();

        Pattern pattern = Pattern.compile("(<(\\w+)[^</>]*>)([^<>]+)(</(\\w+)>)");
        Pattern chiPat = Pattern.compile("[\u4e00-\u9fa5]+");
        Matcher matcher = pattern.matcher(xmlStr);

        Pattern pTrim = Pattern.compile("[ \\f\\n\\r\\t\\v]{1,}");

        StringBuffer stringBuffer = new StringBuffer();

        while (matcher.find()) {
            String label1 = matcher.group(1);
            String tag1 = matcher.group(2);
            String content = matcher.group(3);
            String label2 = matcher.group(4);
            String tag2 = matcher.group(5);

            Matcher mTrim = pTrim.matcher(content);
            String updateContent= mTrim.replaceAll(" ").replaceAll("^(\\s)*|(\\s)*$", "").toLowerCase();

            if (!tag1.equals(tag2) || updateContent.length() < 2) {
                continue;
            }

            if (transParam == 1) {
                for (String key : keyIndexMap.keySet()) {
                    if (updateContent.contains(key)) {
                        keyFound = true;
                        updateContent = updateContent.replace(key, keyIndexMap.get(key));
                    }
                }
            }

            String newTrans;
//            int connectCount = 0;

            long startTime = System.currentTimeMillis();
            long SecCount = 0;

            while (SecCount <= 10) {
                long currentTime = System.currentTimeMillis();
                SecCount = (currentTime - startTime) / 1000;
                newTrans = translationService.translate(updateContent);
                if (newTrans != null) {
                    connected = true;
                    break;
                }
                if ((System.currentTimeMillis() - startTime) / 1000 > SecCount) {
                    System.out.println("fail to connect at " + SecCount + " seconds");
                }
//                connectCount++;
            }

            if (!connected) {
                logger.info("fail to connect for " + SecCount + " seconds");
                break;
            }

            String transResults = translationService.translate(updateContent);
            if (transResults.contains("error_msg") || transResults.contains("error_code")) {continue;}

            JSONObject dataJson = new JSONObject(transResults);
//            if (dataJson.has("error_msg")) {continue;}
//            logger.info(tag1 + "|" + content + "|" + translationService.translate(content));

            JSONArray dataArray = dataJson.getJSONArray("trans_result");
            JSONObject info = dataArray.getJSONObject(0);
            String translation = info.getString("dst");
            
            if (keyFound) {
                Pattern keyPat = Pattern.compile("TBR\\d+");
                Matcher keyMat = keyPat.matcher(translation);
                while (keyMat.find()) {
                    String tempFlag = keyMat.group();
                    translation = translation.replaceFirst(tempFlag, indexValueMap.get(tempFlag));
                }
            }

            Matcher chiMatch = chiPat.matcher(translation);

            if (!chiMatch.find()) {
                translation = content;}

            xmltag = new XmlTagContent();
            if (!xmlRepository.findById(xml_id).isPresent()) {
                logger.info("xml " + xml_id.toString() + " cannot be found!");
                break;
            }
            xmltag.setXmlId(xml_id);
            xmltag.setTag(tag1);
            xmltag.setTagContent(content);
            xmltag.setTagTranslation(translation);
            xmlTagContentRepository.save(xmltag);

            tagList.add(Long.toString(xmltag.getId()));

            tempStr = String.format("%s\\${tag%s}%s", label1, xmltag.getId().toString(), label2);
            matcher.appendReplacement(stringBuffer, tempStr);

//            logger.info("finish: " + xmltag.getId()+ "|" + xmltag.getTag()+ "|" +
//                    xmltag.getTagContent()+ "|" + xmltag.getTagTranslation());
        }

        matcher.appendTail(stringBuffer);

        if (!connected) {
            logger.info("fail to translate, cannot translate this file");

            xml.setStatus(2);
            xmlRepository.save(xml);

            Iterator<XmlTagContent> xmlTagIt = xmlTagContentRepository.findByXmlId(xml_id).iterator();
            while (xmlTagIt.hasNext()) {
                XmlTagContent xtc = xmlTagIt.next();
                xmlTagContentRepository.delete(xtc);
            }
        }
        else if (xmlRepository.findById(xml_id).isPresent()) {
            xml.setXmlString(stringBuffer.toString());
            xml.setXmlTagIdArray(String.join(",", tagList));
            xml.setStatus(4);
            xmlRepository.save(xml);
            logger.info("Finish processing xml file");
        }
        else {
            logger.info("cannot find xml " + xml_id.toString());
        }
    }


//    异步线程，不阻塞主线程
    @Async
    public void generateXML(Xml xml) throws IOException, TemplateException {
        logger.info("Start create xml file " + xml.getFilename());

        String filepath = path + xml.getFilepath();
        String transpath = filepath + "_translation.xml";

        String xmlTempStr = xml.getXmlString();
//        List<String> tagIdList = Arrays.asList(xml.getXmlTagIdArray().split(","));

        Pattern pattern = Pattern.compile("\\$\\{tag(\\d+)\\}");
        Matcher matcher = pattern.matcher(xmlTempStr);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            Long tagId = Long.parseLong(matcher.group(1));
            String translation = xmlTagContentRepository.findById(tagId).get().getTagTranslation();
            matcher.appendReplacement(sb, translation);
        }
        matcher.appendTail(sb);

        FileOutputStream fileOutputStream = new FileOutputStream(transpath, false);
        fileOutputStream.write(sb.toString().getBytes());
        fileOutputStream.close();

        xml.setStatus(6);
        xmlRepository.save(xml);

        logger.info("Finish creating xml file");

//        Map<String, String> tempMap = new HashMap<>();
//        List<Long> tagIdList = xml.getXmlTagIdArray();

//        for (Long tagId : tagIdList) {
//            String translation = xmlTagContentRepository.findById(tagId).get().getTagTranslation();
//            tempMap.put(String.format("tag%s", tagId.toString()), translation);
//        }
//
//        StringWriter translationXml = new StringWriter();
//        Configuration configuration = new Configuration();
//        new Template("template", new StringReader(xmlTempStr), configuration).process(tempMap, translationXml);
    }

    public Map getDictMap() throws IOException {

        InputStreamReader inReader = new InputStreamReader(new FileInputStream(dictFile));
        BufferedReader bf = new BufferedReader(inReader);
        String str;

        Map<String, Map> mapBox = new HashMap<>();
        Map<String, String> key_index = new HashMap<>(500);
        Map<String, String> index_value = new HashMap<>(500);

        int tempIndex = 1;

        while ((str = bf.readLine()) != null) {
//            System.out.println(str.split("@")[1]);
            key_index.put(str.split("@")[0].toLowerCase(), "TBR" + tempIndex);
            index_value.put("TBR" + tempIndex, str.split("@")[1]);

            tempIndex ++;
        }

        mapBox.put("key_index", key_index);
        mapBox.put("index_value", index_value);

        return mapBox;
    }

}
