package com.example.onto_generator.controller;


import com.example.onto_generator.model.*;
import com.example.onto_generator.service.MetricsService;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@CrossOrigin(origins = {"http://localhost:4200"})
public class MetricsController {

    @Autowired
    private MetricsService metricsService;

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/metrics")
    public ResponseEntity<BaseMetrics> metrics(@RequestBody String onto) throws OWLOntologyCreationException {
        System.out.println("here is the controller");
        System.out.println(onto);
        return ResponseEntity.ok(metricsService.baseMetrics(onto));
    }

    @PostMapping("/data-property-metrics")
    public ResponseEntity<DataPropertyAxiomsMetrics> dataPropertyMetrics(@RequestBody String ontology) throws OWLOntologyCreationException {
        System.out.println("Data Property Metrics:");
        System.out.println(ontology);
        DataPropertyAxiomsMetrics metrics = metricsService.dataPropertyAxiomsMetrics(ontology);
        return ResponseEntity.ok(metrics);
    }

    @PostMapping("/class-axioms-metrics")
    public ResponseEntity<ClassAxiomsMetrics> classAxiomsMetrics(@RequestBody String ontology) throws OWLOntologyCreationException {
        System.out.println("Class Axioms Metrics:");
        System.out.println(ontology);
        ClassAxiomsMetrics metrics = metricsService.classAxiomsMetrics(ontology);
        return ResponseEntity.ok(metrics);
    }

    @PostMapping("/individual-axioms-metrics")
    public ResponseEntity<IndividualAxiomsMetrics> individualAxiomsMetrics(@RequestBody String ontology) throws OWLOntologyCreationException {
        System.out.println("Individual Axioms Metrics:");
        System.out.println(ontology);
        IndividualAxiomsMetrics metrics = metricsService.individualAxiomsMetrics(ontology);
        return ResponseEntity.ok(metrics);
    }

    @PostMapping("/object-property-metrics")
    public ResponseEntity<ObjectPropertyAxiomsMetrics> objectPropertyMetrics(@RequestBody String ontology) throws OWLOntologyCreationException {
        System.out.println("Object Property Metrics:");
        System.out.println(ontology);
        ObjectPropertyAxiomsMetrics metrics = metricsService.objectPropertyAxiomsMetrics(ontology);
        return ResponseEntity.ok(metrics);
    }
}
