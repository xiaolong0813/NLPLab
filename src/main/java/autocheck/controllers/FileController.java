package autocheck.controllers;

import autocheck.models.*;
import autocheck.services.FileProcessService;
import org.springframework.beans.factory.annotation.Autowired;
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
//@CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin(origins = "*")
@RequestMapping(path = "/api/file")
public class FileController {

    private String path = "/Users/sefer/Documents/FDU/Lab/Project/Siemens/autocheck/file/";

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private FileProcessService fileProcessService;

    @GetMapping(path = "/getProcessing/{fileType}")
    public Iterable<Document> getProcessing(@PathVariable String fileType) {
        return documentRepository.findByStatusAndFiletype(1, Integer.parseInt(fileType));
    }

    @GetMapping(path="/getAll/{fileType}")
    public Iterable<Document> getAll(@PathVariable String fileType) {
        return documentRepository.findByFiletype(Integer.parseInt(fileType));
    }

    @GetMapping(path = "/downloadDoc/{docId}")
    public ResponseEntity<Resource> downloadDoc(@PathVariable Long docId) throws IOException{
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        System.out.println(docId);
        Document doc = documentRepository.findById(docId).get();
        String filepath = path + doc.getFilepath();
        System.out.println(filepath);
        File file = new File(filepath+"_deviation.xlsx");
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }

    @GetMapping(path = "/processDoc/{docId}")
    public Message processDoc(@PathVariable Long docId) throws IOException {
        Message message = new Message();
        Optional<Document> doc_opt = documentRepository.findById(docId);
        if (!doc_opt.isPresent()) {
            message.setData(documentRepository.findByFiletype(1));
            message.setStatus_code(-1);
            message.setMessage("Document does not exist.");
            return message;
        } else {
            Document doc = doc_opt.get();
            if (doc.getStatus() == 0) {
                doc.setStatus(1);
                documentRepository.save(doc);
                fileProcessService.processDoc(doc);
            }
            message.setData(documentRepository.findByFiletype(1));
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
        Long typeid = Long.parseLong(params.getParameter("type"));
        Double threshold = Double.parseDouble(params.getParameter("threshold"));

        if (!file.isEmpty()) {
            try {
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
                Type type = typeRepository.findById(typeid).get();
                doc.setType(type.getName());
                doc.setStatus(0);
                doc.setThreshold(threshold);
                documentRepository.save(doc);

                if (fileType == 0) {
                    doc.setStatus(1);
                    documentRepository.save(doc);
                    fileProcessService.processDev(doc);
                }

                Iterable<Document> docs = documentRepository.findByStatusAndFiletype(1, fileType);
                message.setData(docs);

            } catch (Exception e) {
                message.setStatus_code(-1);
                message.setMessage("Failed! => " + e.getMessage());
            }
        } else {
            message.setStatus_code(-1);
            message.setMessage("You failed to upload " + filename + " because the file was empty.");
        }

        System.out.println(filename);
        System.out.println(fileUrl);
        System.out.println(filepath);
        System.out.println(fileType);
        System.out.println(typeid);
        System.out.println(threshold);

        return message;
    }
}
