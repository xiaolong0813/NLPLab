package autocheck.controllers;

import autocheck.models.Deviation;
import autocheck.models.DeviationRepository;
import autocheck.models.Type;
import autocheck.models.TypeRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(path = "/api/deviation")
public class DeviationController {
    private static final Logger logger= LogManager.getLogger(DeviationController.class);

    @Autowired
    private DeviationRepository deviationRepository;

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
}
