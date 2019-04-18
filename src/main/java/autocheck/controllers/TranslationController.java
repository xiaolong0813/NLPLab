package autocheck.controllers;

import autocheck.models.*;
import autocheck.services.FileProcessService;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
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

//    @PostMapping(path = "/update")
//    public Message updateTranslation(HttpServletRequest request) {
//        Message mes = new Message();
//        logger.info(request.getParameterValues("tagId"));
//        String tagId = request.getParameter("tagId");
//        String updateTrans = request.getParameter("updateTrans");
//        logger.info("the tag " + tagId + " translation updated to " + updateTrans);
//        mes.setStatus_code(200);
//        mes.setMessage("The tag " + tagId + " has been update to " + updateTrans);
//
//        return mes;
//    }

    @GetMapping(path = "/getTags/{xml_id}")
    public Iterable<XmlTagContent> getTranslation(@PathVariable String xml_id) {
        logger.info("Return all translated tag contents");
        Long xmlId = Long.parseLong(xml_id);
        return xmlTagContentRepository.findByXmlIdOrderById(xmlId);
    }

    @GetMapping(path = "/checkProcessingXML")
    public boolean checkProcessingXML() {
        boolean found = false;
        logger.info("Check if there are processing files");
        Iterable<Xml> translating = xmlRepository.findAllByStatus(3);
        Iterable<Xml> generating = xmlRepository.findAllByStatus(5);
        if (translating.iterator().hasNext() || generating.iterator().hasNext()) {
            found = true;
        }
        return found;
    }


    @DeleteMapping(path = "/deleteTag/{tag_Id}")
    public Message deleteTag(@PathVariable String tag_Id) {
        Long tagId = Long.parseLong(tag_Id);
        XmlTagContent xmlTagToDelete = xmlTagContentRepository.findById(tagId).get();
        xmlTagToDelete.setTagTranslation(xmlTagToDelete.getTagContent());
        xmlTagContentRepository.save(xmlTagToDelete);

        Xml xml = xmlRepository.findById(xmlTagToDelete.getXmlId()).get();
        xml.setStatus(4);
        xmlRepository.save(xml);

//        xmlTagContentRepository.deleteById(tagId);
        logger.info("The tag " + tag_Id + " 's translation has been deleted, original content would be kept");

        Message mes = new Message();
        mes.setStatus_code(200);
        mes.setMessage("Delete Successfully");
        return mes;
    }

    @PostMapping(path = "/update")
    public Message updateTranslation(@RequestBody XmlTagContent xmlTagContent) {
//        logger.info(xmlTagContent.getId() + "|" + xmlTagContent.getTagTranslation());
        XmlTagContent xmlTagToUpdate = xmlTagContentRepository.findById(xmlTagContent.getId()).get();
        xmlTagToUpdate.setTagTranslation(xmlTagContent.getTagTranslation());
        xmlTagContentRepository.save(xmlTagToUpdate);

        Xml xml = xmlRepository.findById(xmlTagContent.getXmlId()).get();
        xml.setStatus(4);
        xmlRepository.save(xml);

        logger.info("Updated xmlTag is: " + ReflectionToStringBuilder.toString(xmlTagToUpdate));

        Message mes = new Message();
        mes.setStatus_code(200);
        mes.setMessage("Update Successfully!");

        return mes;
    }

    @GetMapping(path = "generateXML/{xmlId}")
    public Message generateXML(@PathVariable Long xmlId) throws IOException, TemplateException {
        Xml xml = xmlRepository.findById(xmlId).get();

        Message mes = new Message();
        if (xml.getStatus() == 4) {
            xml.setStatus(5);
            xmlRepository.save(xml);
            logger.info("Start generating xml file");
            fileProcessService.generateXML(xml);
        }
        mes.setStatus_code(200);
        mes.setMessage("Start generating xml file");
        return mes;
    }


    @GetMapping(path = "/processXml/")
    public Message processXml(@RequestParam("xml_id") String xml_id_str,
                              @RequestParam("transParam") String transParam) throws Exception {
        Long xmlId = Long.parseLong(xml_id_str);
        int tp = Integer.parseInt(transParam);

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
                fileProcessService.processXml(xml, tp);
            }
//            mes.setData(xmlRepository.findById(xmlId));
            mes.setStatus_code(200);
            mes.setMessage("Start processing xml file");
            return mes;
        }
    }




}
