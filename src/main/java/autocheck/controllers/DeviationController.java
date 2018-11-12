package autocheck.controllers;

import autocheck.models.Deviation;
import autocheck.models.DeviationRepository;
import autocheck.models.Type;
import autocheck.models.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(path = "/api/deviation")
public class DeviationController {
    @Autowired
    private DeviationRepository deviationRepository;

    @Autowired
    private TypeRepository typeRepository;

    @GetMapping(path = "/getDevs/{typeid}")
    public Iterable<Deviation> getDevs(@PathVariable Long typeid) {
        if (typeid == 0) {
            return deviationRepository.findAll();
        } else {
            Type type = typeRepository.findById(typeid).get();
            return deviationRepository.findByType(type.getName());
        }
    }
}
