package autocheck.controllers;

import autocheck.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
//@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(path = "/api/parameters")
public class ParameterController {
    @Autowired
    private ParameterRepository paraRepository;

    // Get type list
    @GetMapping
    public Iterable<Parameter> getParams() {
        return paraRepository.findAll();
    }

    // Add new type
    @PostMapping
    public Message addParam(@RequestBody Parameter parameter) {
        Message message = new Message();
        if (paraRepository.findByName(parameter.getName()).size() > 0) {
            message.setStatus_code(-1);
            message.setMessage("Parameter " + parameter.getName() + " already existed.");
        } else {
            paraRepository.save(parameter);
            message.setStatus_code(200);
            message.setMessage("Parameter " + parameter.getName() + " added.");
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
        } else {
            message.setStatus_code(-1);
            message.setMessage("Parameter " + parameter.getName() + " does not exist.");
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