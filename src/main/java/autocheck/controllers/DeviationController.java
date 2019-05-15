package autocheck.controllers;

import autocheck.models.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/api/deviation")
public class DeviationController {
    private static final Logger logger= LogManager.getLogger(DeviationController.class);

    @Autowired
    private DeviationRepository deviationRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private SentenceRepository sentenceRepository;

//    @Autowired
//    private TypeRepository typeRepository;

    @GetMapping(path = "/getDevs")
    public Iterable<Deviation> getDevs() {
        logger.info("Return all the deviation records");
        return deviationRepository.findAll();
//        if (typeid == 0) {
//            logger.info("Return all the deviation records");
//            return deviationRepository.findAll();
//        } else {
//            Type type = typeRepository.findById(typeid).get();
//            logger.info("Return deviation records with type " + type.getName());
//            return deviationRepository.findByType(type.getName());
//        }
    }

    @DeleteMapping
    public Message deleteDevs() {
        // delete devs and sents
        deviationRepository.deleteAll();
        sentenceRepository.deleteAll();

        documentRepository.deleteAll(documentRepository.findByFiletype(0));
        documentRepository.deleteAll(documentRepository.findByFiletype(2));

        Message message = new Message();
        message.setStatus_code(200);
        message.setMessage("All the deviation and sentences records are removed.");
        logger.info("Remove all the deviation and sentences records");
        return message;
    }
}
