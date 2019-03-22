package autocheck.controllers;

import autocheck.models.*;
import autocheck.services.FileProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.io.*;
import java.sql.Timestamp;
import javax.servlet.http.HttpServletRequest;


@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(path = "/api/translation")
public class TranslationController {
    public static final Logger logger = LogManager.getLogger(TranslationController.class);

    @Autowired
    private XmlRepository xmlRepository;

    @Autowired
    private XmlTagContentRepository xmlTagContentRepository;

    @Autowired
    private FileProcessService fileProcessService;

    @GetMapping(path = "/testurl/")
    public Message testurl(@RequestParam("xml_id") String xml_id) {
        Message mes = new Message();
        mes.setMessage("response is : " + xml_id);
        return mes;
    }

    @GetMapping(path = "/getTags/{xml_id}")
    public Iterable<XmlTagContent> getTranslation(@PathVariable String xml_id) {
        logger.info("Return all translated tag contents");
        Long xmlId = Long.parseLong(xml_id);
        return xmlTagContentRepository.findByXmlIdOrderById(xmlId);
    }


    @GetMapping(path = "/processXml/")
    public Message processXml(@RequestParam("xml_id") String xml_id_str) throws Exception {
        Long xmlId = Long.parseLong(xml_id_str);

        Message mes = new Message();
        Optional<Xml> xml_opt = xmlRepository.findById(xmlId);

        if (!xml_opt.isPresent()) {
//            mes.setData(xml_opt);
            mes.setStatus_code(-1);
            mes.setMessage("Xml file does not exist.");
            logger.error("Xml file does not exist.");
            return mes;
        }else {
            Xml xml = xml_opt.get();
            if (xml.getStatus() == 2) {
                xml.setStatus(3);
                xmlRepository.save(xml);
                logger.info("Start processing xml file");
                fileProcessService.processXml(xml);
            }
//            mes.setData();
            mes.setData(xmlRepository.findById(xmlId));
            mes.setStatus_code(200);
            mes.setMessage("Start processing xml file");
            return mes;
        }
    }




}
