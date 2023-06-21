package com.example.onto_generator.controller;

import com.example.onto_generator.service.GenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.List;

@Controller
@CrossOrigin(origins = {"http://localhost:4200"})
public class GenerationController {
    @Autowired
    private GenerationService sevice;

    @PostMapping("/generate/{apikey}")
    public ResponseEntity<List<String>> generate(@PathVariable String apikey, @RequestBody String prompt) throws Exception {
        return ResponseEntity.ok(Collections.singletonList(sevice.gptRequest(apikey, prompt)));
    }

    @PostMapping("/ontology")
    public ResponseEntity<List<String>> ontology(@RequestBody String triplets) throws Exception {
        return ResponseEntity.ok(Collections.singletonList(sevice.tripleToOntology(triplets)));
    }

    @PostMapping("/stateful/{apikey}")
    public ResponseEntity<List<String>> stateful(@PathVariable String apikey, @RequestBody String prompt) throws Exception {
        return ResponseEntity.ok(Collections.singletonList(sevice.chat(apikey, prompt)));
    }
}
