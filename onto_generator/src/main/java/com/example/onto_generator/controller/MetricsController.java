package com.example.onto_generator.controller;

import com.example.onto_generator.model.*;
import com.example.onto_generator.model.dto.*;
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

    @Autowired
    private DTOConverter dtoConverter;

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/metrics")
    public ResponseEntity<BaseMetricsDTO> metrics(@RequestBody String ontology) throws OWLOntologyCreationException {
        BaseMetrics metrics = metricsService.baseMetrics(ontology);
        BaseMetricsDTO metricsDTO = dtoConverter.convertToBaseMetricsDTO(metrics);
        return ResponseEntity.ok(metricsDTO);
    }

    @PostMapping("/data-property-metrics")
    public ResponseEntity<DataPropertyAxiomsMetricsDTO> dataPropertyMetrics(@RequestBody String ontology) throws OWLOntologyCreationException {
        DataPropertyAxiomsMetrics metrics = metricsService.dataPropertyAxiomsMetrics(ontology);
        DataPropertyAxiomsMetricsDTO metricsDTO = dtoConverter.convertToDataPropertyAxiomsMetricsDTO(metrics);
        return ResponseEntity.ok(metricsDTO);
    }

    @PostMapping("/class-axioms-metrics")
    public ResponseEntity<ClassAxiomsMetricsDTO> classAxiomsMetrics(@RequestBody String ontology) throws OWLOntologyCreationException {
        ClassAxiomsMetrics metrics = metricsService.classAxiomsMetrics(ontology);
        ClassAxiomsMetricsDTO metricsDTO = dtoConverter.convertToClassAxiomsMetricsDTO(metrics);
        return ResponseEntity.ok(metricsDTO);
    }

    @PostMapping("/individual-axioms-metrics")
    public ResponseEntity<IndividualAxiomsMetricsDTO> individualAxiomsMetrics(@RequestBody String ontology) throws OWLOntologyCreationException {
        IndividualAxiomsMetrics metrics = metricsService.individualAxiomsMetrics(ontology);
        IndividualAxiomsMetricsDTO metricsDTO = dtoConverter.convertToIndividualAxiomsMetricsDTO(metrics);
        return ResponseEntity.ok(metricsDTO);
    }

    @PostMapping("/object-property-metrics")
    public ResponseEntity<ObjectPropertyAxiomsMetricsDTO> objectPropertyMetrics(@RequestBody String ontology) throws OWLOntologyCreationException {
        ObjectPropertyAxiomsMetrics metrics = metricsService.objectPropertyAxiomsMetrics(ontology);
        ObjectPropertyAxiomsMetricsDTO metricsDTO = dtoConverter.convertToObjectPropertyAxiomsMetricsDTO(metrics);
        return ResponseEntity.ok(metricsDTO);
    }
}
