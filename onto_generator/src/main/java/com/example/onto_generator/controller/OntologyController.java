package com.example.onto_generator.controller;

import com.example.onto_generator.service.OntologyService;
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
public class OntologyController {
    @Autowired
    private OntologyService ontologyService;

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/convert/{syntax}")
    public ResponseEntity<List<String>> convert(@PathVariable String syntax, @RequestBody String ontology) {
        return ResponseEntity.ok(Collections.singletonList(ontologyService.convert(syntax, ontology)));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/validator")
    public ResponseEntity<List<String>> validator(@RequestBody String ontology) {
        return ResponseEntity.ok(Collections.singletonList(ontologyService.validateOntology(ontology)));
    }

}