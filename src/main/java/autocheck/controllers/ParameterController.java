package autocheck.controllers;

import autocheck.models.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api/parameters")
public class ParameterController {
    private static final Logger logger= LogManager.getLogger(ParameterController.class);

    @Autowired
    private ParameterRepository paraRepository;

    // Get type list
    @GetMapping
    public Iterable<Parameter> getParams() {
        logger.info("Return parameters");
        return paraRepository.findAll();
    }

    // Add new type
    @PostMapping
    public Message addParam(@RequestBody Parameter parameter) {
        Message message = new Message();
        if (paraRepository.findByName(parameter.getName()).size() > 0) {
            message.setStatus_code(-1);
            message.setMessage("Parameter " + parameter.getName() + " already existed.");
            logger.error("Parameter already existed");
        } else {
            paraRepository.save(parameter);
            message.setStatus_code(200);
            message.setMessage("Parameter " + parameter.getName() + " added.");
            logger.info("New parameter added");
        }
        message.setData(paraRepository.findAll());
        return message;
    }

    //    // Update type
    @PutMapping
    public Message updatePara(@RequestBody Parameter parameter) {
        Message message = new Message();
        Optional<Parameter> this_param = paraRepository.findById(parameter.getId());
        if (this_param.isPresent()) {
            this_param.get().setValue(parameter.getValue());
            paraRepository.save(this_param.get());
            message.setStatus_code(200);
            message.setMessage("Parameter " + parameter.getName() + " updated.");
            logger.info("Update a parameter");
        } else {
            message.setStatus_code(-1);
            message.setMessage("Parameter " + parameter.getName() + " does not exist.");
            logger.error("Parameter does not exist");
        }
        message.setData(paraRepository.findAll());
        return message;
    }
    //
////    // Delete type TODO remove relevant deviation records
//    @DeleteMapping(path = "/delete/{id}")
//    public Message deleteType(@PathVariable String id) {
//        Message message = new Message();
//        Optional<Type> this_type = typeRepository.findById(Long.valueOf(id));
//        if (this_type.isPresent()) {
//            typeRepository.delete(this_type.get());
//            message.setStatus_code(200);
//            message.setMessage("Type removed");
//        } else {
//            message.setStatus_code(-1);
//            message.setMessage("Type does not exist.");
//        }
//        message.setData(typeRepository.findAll());
//        return message;
//    }
}