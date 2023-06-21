package com.example.onto_generator.service;

import com.example.onto_generator.model.*;
import com.example.onto_generator.service.metrics.*;
import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MetricsService {

    public BaseMetrics baseMetrics(String onto) throws OWLOntologyCreationException {
        // Load the ontology from the input string
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        InputStream input = new ByteArrayInputStream(onto.getBytes());
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(input);

        BaseMetricService metricService = new BaseMetricService(ontology, true);

        metricService.printBaseMetrics();

        return metricService.getBaseMetrics();
    }

    public ClassAxiomsMetrics classAxiomsMetrics(String onto) throws OWLOntologyCreationException {
        // Load the ontology from the input string
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        InputStream input = new ByteArrayInputStream(onto.getBytes());
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(input);

        ClassAxiomsMetricService service = new ClassAxiomsMetricService(ontology, true);

        service.printClassAxiomsMetrics();
        return service.getClassAxiomsMetrics();

    }

    public DataPropertyAxiomsMetrics dataPropertyAxiomsMetrics(String ontology) throws OWLOntologyCreationException {
        // Load the ontology from the input string
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        InputStream input = new ByteArrayInputStream(ontology.getBytes());
        OWLOntology owlOntology = manager.loadOntologyFromOntologyDocument(input);

        DataPropertyAxiomsMetricService service = new DataPropertyAxiomsMetricService(owlOntology, true);

        service.printMetrics();
        return service.getMetrics();
    }

    public IndividualAxiomsMetrics individualAxiomsMetrics(String ontology) throws OWLOntologyCreationException {
        // Load the ontology from the input string
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        InputStream input = new ByteArrayInputStream(ontology.getBytes());
        OWLOntology owlOntology = manager.loadOntologyFromOntologyDocument(input);

        IndividualAxiomsMetricService service = new IndividualAxiomsMetricService(owlOntology, true);

        service.printMetrics();
        return service.getMetrics();
    }

    public ObjectPropertyAxiomsMetrics objectPropertyAxiomsMetrics(String ontology) throws OWLOntologyCreationException {
        // Load the ontology from the input string
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        InputStream input = new ByteArrayInputStream(ontology.getBytes());
        OWLOntology owlOntology = manager.loadOntologyFromOntologyDocument(input);

        ObjectPropertyAxiomsMetricService service = new ObjectPropertyAxiomsMetricService(owlOntology, true);

        service.printMetrics();
        return service.getMetrics();
    }



}
