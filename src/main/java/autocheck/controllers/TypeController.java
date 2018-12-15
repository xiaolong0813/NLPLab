package autocheck.controllers;

import autocheck.models.Message;
import autocheck.models.Type;
import autocheck.models.TypeRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(path = "/api/types")
public class TypeController {
    private static final Logger logger= LogManager.getLogger(TypeController.class);

    @Autowired
    private TypeRepository typeRepository;

    // Get type list
    @GetMapping
    public Iterable<Type> getTypes() {
        logger.info("Return type list");
        return typeRepository.findAll();
    }

    // Add new type
    @PostMapping
    public Message addType(@RequestBody Type type) {
        Message message = new Message();
        if (typeRepository.findByName(type.getName()).size() > 0) {
            message.setStatus_code(-1);
            message.setMessage("Type " + type.getName() + " already existed.");
            logger.error("Type already existed");
        } else {
            typeRepository.save(type);
            message.setStatus_code(200);
            message.setMessage("Type " + type.getName() + " added.");
            logger.info("New type added");
        }
        message.setData(typeRepository.findAll());
        return message;
    }

    // Update type
    @PutMapping
    public Message updateType(@RequestBody Type type) {
        Message message = new Message();
        Optional<Type> this_type = typeRepository.findById(type.getId());
        if (this_type.isPresent()) {
            this_type.get().setName(type.getName());
            typeRepository.save(this_type.get());
            message.setStatus_code(200);
            message.setMessage("Type updated.");
            logger.info("Update a type");
        } else {
            message.setStatus_code(-1);
            message.setMessage("Type " + type.getName() + " does not exist.");
            logger.error("Type does not exist");
        }
        message.setData(typeRepository.findAll());
        return message;
    }
//
//    // Delete type TODO remove relevant deviation records
    @DeleteMapping(path = "/delete/{id}")
    public Message deleteType(@PathVariable String id) {
        Message message = new Message();
        Optional<Type> this_type = typeRepository.findById(Long.valueOf(id));
        if (this_type.isPresent()) {
            typeRepository.delete(this_type.get());
            message.setStatus_code(200);
            message.setMessage("Type removed");
            logger.info("Type removed");
        } else {
            message.setStatus_code(-1);
            message.setMessage("Type does not exist.");
            logger.error("Type does not exist");
        }
        message.setData(typeRepository.findAll());
        return message;
    }
}