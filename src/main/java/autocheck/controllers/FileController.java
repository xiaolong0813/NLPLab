package autocheck.controllers;

import autocheck.models.*;
import autocheck.services.FileProcessService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.sql.Timestamp;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/file")
public class FileController {
    private static final Logger logger= LogManager.getLogger(FileController.class);

    @Value("${file.path}")
    private String path;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private XmlRepository xmlRepository;

    @Autowired
    private XmlTagContentRepository xmlTagContentRepository;

//    @Autowired
//    private TypeRepository typeRepository;

    @Autowired
    private FileProcessService fileProcessService;

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return headers;
    }

//    @GetMapping(path = "/testurl/")
//    public Message testurl(@RequestParam("xml_id") String xml_id) {
//        Message mes = new Message();
//        mes.setMessage("response are" + xml_id);
//        return mes;
//    }

    @GetMapping(path = "/getProcessing/{fileType}")
    public Iterable<Document> getProcessing(@PathVariable Integer fileType) {
        logger.info("Return documents under processing");
//        return documentRepository.findByStatusAndFiletype(1, Integer.parseInt(fileType));
        return documentRepository.findByStatus(1);
    }

    @GetMapping(path = "/checkProcessingFiles/{fileType}")
    public boolean checkProcessingFiles(@PathVariable Integer fileType) {
        boolean found = false;
        Iterable<Document> processing;
        switch (fileType){
            case 0:
            case 2:
                logger.info("Check if there are processing devs or doc sents");
                processing = documentRepository.findByStatusAndFiletype(1, fileType);
                if (processing.iterator().hasNext()) {
                    found = true;
                }
                break;
            case 1:
                logger.info("Check if there are processing rfqs");
                processing = documentRepository.findByStatusAndFiletype(3, fileType);
                if (processing.iterator().hasNext()) {
                    found = true;
                }
                break;
            case 20:
                logger.info("Check if there are processing devs and doc sents");
                processing = documentRepository.findByStatusAndFiletypeOrFiletype();
                logger.info("found results: " + processing);
                if (processing.iterator().hasNext()) {
                    found = true;
                }
                break;
        }
        return found;
    }

    @GetMapping(path="/getAll/{fileType}")
    public Iterable<Document> getAll(@PathVariable String fileType) {
        logger.info("Return all the documents");
        return documentRepository.findByFiletypeOrderByIdDesc(Integer.parseInt(fileType));
    }

    @GetMapping(path = "/getOneXml/{xmlId}")
    public Xml getOneXml(@PathVariable String xmlId) {
        Long xmlid = Long.parseLong(xmlId);
        Optional<Xml> xml = xmlRepository.findById(xmlid);
        logger.info("Return xml " + xmlId);
        if (!xml.isPresent()) {
            logger.info("Cannot find xml " + xmlId);
            return null;
        }
        return xml.get();
    }

    @GetMapping(path = "/getAllXMLs")
    public Iterable<Xml> getAllXMLs() {
        logger.info("Return all the xml files");
        return xmlRepository.findAllByOrderByIdDesc();
    }

    @DeleteMapping
    public Message deleteRFQ() {
        documentRepository.deleteAll();
        Message message = new Message();
        message.setStatus_code(200);
        message.setMessage("All the documents are removed.");
        logger.info("Remove all the documents");
        return message;
    }

    @DeleteMapping(path = "/allXmls")
    public Message deleteXML() {
        xmlRepository.deleteAll();
//        logger.info("before: " + xmlTagContentRepository.findAll().toString());
        xmlTagContentRepository.deleteAll();
        documentRepository.deleteAll(documentRepository.findByFiletype(3));
//        logger.info("after: " + xmlTagContentRepository.findAll().toString());
        Message mes = new Message();
        mes.setStatus_code(200);
        mes.setMessage("All the Xml files and translation records are removed");
        logger.info("Remove all the xml files and translation records");
        return mes;
    }

    @GetMapping(path = "/downloadXlsx/{docId}")
    public ResponseEntity<Resource> downloadXlsx(@PathVariable Long docId) throws IOException{
        Document doc = documentRepository.findById(docId).get();
        String filepath = path + doc.getFilepath();
        File file = new File(filepath+"_deviation.xlsx");
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        logger.info("Return downloadable xlsx document");
        return ResponseEntity.ok()
                .headers(getHeaders())
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }

    @GetMapping(path = "/downloadDocx/{docId}")
    public ResponseEntity<Resource> downloadDocx(@PathVariable Long docId) throws IOException{
        Document doc = documentRepository.findById(docId).get();
        String filepath = path + doc.getFilepath();
        File file = new File(filepath+"_new.docx");
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        logger.info("Return downloadable docx document");
        return ResponseEntity.ok()
                .headers(getHeaders())
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .body(resource);
    }

    @GetMapping(path = "/downloadXml/{xmlId}")
    public ResponseEntity<Resource> downloadXml(@PathVariable Long xmlId) throws IOException {
//        Optional<Xml> xmlOpt = xmlRepository.findById(xmlId);
//        Xml xml = xmlOpt.get();
        Xml xml = xmlRepository.findById(xmlId).get();

        String xmlpath = path + xml.getFilepath();
        File newFile = new File(xmlpath + "_translation.xml");
        InputStreamResource resource = new InputStreamResource(new FileInputStream(newFile));

        logger.info("Return downloadable xml file");

        return ResponseEntity.ok()
                .headers(getHeaders())
                .contentLength(newFile.length())
                .contentType(MediaType.parseMediaType("text/xml"))
                .body(resource);
    }

//    @GetMapping(path = "/downloadXml/{xmlId}")
//    public Xml downloadXml(@PathVariable Long xmlId) {
//        Xml xml = xmlRepository.findById(xmlId).get();
////        fileProcessService.processXmlString(xml);
//        logger.info("Return downloadable xml file: " + xml.getFilename());
////        Blob blob = new SerialBlob(xml.getXmlString().getBytes());
//        return xml;
//    }

    @GetMapping(path = "/processDoc/")
    public Message processDoc(@RequestParam("doc_id") String doc_id_str,
                              @RequestParam("model") String model_str,
                              @RequestParam("rfqvar") String rfqvar_str,
                              @RequestParam("simalgo") String simalgo_str,
                              @RequestParam("level") String level_str) throws Exception {
        Long docId = Long.parseLong(doc_id_str);
        Integer model = Integer.parseInt(model_str);
        Integer rfqVar = Integer.parseInt(rfqvar_str);
        Integer simAlgo = Integer.parseInt(simalgo_str);
        Integer level = Integer.parseInt(level_str);

        Message message = new Message();
        Optional<Document> doc_opt = documentRepository.findById(docId);
        if (!doc_opt.isPresent()) {
            message.setData(documentRepository.findByFiletypeOrderByIdDesc(1));
            message.setStatus_code(-1);
            message.setMessage("Document does not exist.");
            logger.error("Document does not exist.");
            return message;
        } else {
            Document doc = doc_opt.get();
            if (doc.getStatus() == 2) {
                doc.setStatus(3);
                documentRepository.save(doc);
                logger.info("Start processing document");
                fileProcessService.processDoc(doc, model, rfqVar, simAlgo, level);
            }
            message.setData(documentRepository.findByFiletypeOrderByIdDesc(1));
            message.setStatus_code(200);
            message.setMessage("Start processing document");
            return message;
        }
    }

    @PostMapping(path = "/upload")
    public Message upload(HttpServletRequest request) {
        Message message = new Message();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        MultipartHttpServletRequest params = ((MultipartHttpServletRequest) request);
        MultipartFile file = params.getFile("file");
        String filename = file.getOriginalFilename();
        String fileUrl = timestamp.getTime() + "_" + filename;
        String filepath = path + fileUrl;

        Integer fileType = Integer.parseInt(params.getParameter("fileType"));
//        Long typeid = Long.parseLong(params.getParameter("type"));
//        Double threshold = Double.parseDouble(params.getParameter("threshold"));

        if (!file.isEmpty()) {
            try {
                logger.info("Start uploading document");
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(filepath)));
                stream.write(bytes);
                stream.close();
                message.setStatus_code(200);
                message.setMessage("File uploaded.");

                Document doc = new Document();
                doc.setFilename(filename);
                doc.setFilepath(fileUrl);
                doc.setFiletype(fileType);
//                Type type = typeRepository.findById(typeid).get();
//                doc.setType(type.getName());
//                doc.setStatus(0);
//                doc.setThreshold(threshold);
//                documentRepository.save(doc);

                doc.setStatus(1);
                documentRepository.save(doc);
                if (fileType == 0) {
                    logger.info("Start processing deviation document");
                    fileProcessService.processDev(doc);
                } else if (fileType == 1) {
//                    logger.info("Start splitting RFQ sentences");
//                    fileProcessService.processDocSentence(doc);
                    logger.info("Upload RFQ Document");
                    doc.setStatus(2);
                    documentRepository.save(doc);
                } else if (fileType == 2) {
                    logger.info("Upload Deviation Source Document");
                    logger.info("Start splitting deviation paragraphs");
                    fileProcessService.processDocSentence(doc);
//                    doc.setStatus(2);
//                    documentRepository.save(doc);
                } else if (fileType == 3) {
                    logger.info("Upload XML file");
//                    logger.info("Start achieving tag contents");
                    Xml xml = new Xml();
                    xml.setFilename(filename);
                    xml.setFilepath(fileUrl);
//                    fileProcessService.processXml(xml);
                    xml.setStatus(2);
                    xmlRepository.save(xml);

                    // Set xml document to status 4
                    doc.setStatus(4);
                    documentRepository.save(doc);
                }

                Iterable<Document> docs = documentRepository.findByStatusAndFiletype(1, fileType);
                message.setData(docs);
                logger.info("File was uploaded successfully");

            } catch (Exception e) {
                message.setStatus_code(-1);
                message.setMessage("Failed! => " + e.getMessage());
                logger.error(e.getMessage());
            }
        } else {
            message.setStatus_code(-1);
            message.setMessage("You failed to upload " + filename + " because the file was empty.");
            logger.error("File was empty");
        }

//        System.out.println(filename);
//        System.out.println(fileUrl);
//        System.out.println(filepath);
//        System.out.println(fileType);
//        System.out.println(typeid);
//        System.out.println(threshold);

        return message;
    }
}
