package com.example.onto_generator.controller;

import com.example.onto_generator.service.OntologyService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@CrossOrigin(origins = {"http://localhost:4200"})
public class OntologyController {

    @Autowired
    private OntologyService ontologyService;

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/graph")
    public ResponseEntity<?> getOntologyGraph(@RequestBody String onto) {
        System.out.println("loaded: " + onto);
        return ResponseEntity.ok(ontologyService.getGraph(onto));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/validate")
    public ResponseEntity<List<String>> validate(@RequestBody String onto) {
        return ResponseEntity.ok(Collections.singletonList(validateOntology(onto)));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/convert/{syntax}")
    public ResponseEntity<List<String>> convert(@PathVariable String syntax, @RequestBody String onto) {
        return ResponseEntity.ok(Collections.singletonList(ontologyService.convert(syntax, onto)));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/validator")
    public ResponseEntity<List<String>> validator(@RequestBody String onto) throws OWLOntologyCreationException {
        return ResponseEntity.ok(Collections.singletonList(ontologyService.validateOntology(onto)));
    }

    public String validateOntology(String onto) {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology;
        try {
            StringDocumentSource documentSource = new StringDocumentSource(onto);
            OWLOntologyLoaderConfiguration configuration = new OWLOntologyLoaderConfiguration()
                    .setLoadAnnotationAxioms(true);

            ontology = manager.loadOntologyFromOntologyDocument(documentSource, configuration);

        } catch (OWLOntologyCreationException e) {
            return "Could not parse ontology";
        }

        // Create a structural reasoner to validate the ontology
        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

        // Check if the ontology is consistent
        if (!reasoner.isConsistent()) {
            System.err.println("Ontology is inconsistent!");
            return "Ontology is inconsistent!";
        }

        // Check if there are any unsatisfiable classes
        if (reasoner.isSatisfiable(ontology.getOWLOntologyManager().getOWLDataFactory().getOWLNothing())) {
            System.err.println("There are unsatisfiable classes in the ontology!");
            List<String> errors = new ArrayList<>();
            for (OWLClass cls : reasoner.getUnsatisfiableClasses().getEntitiesMinusBottom()) {
                errors.add(cls.getIRI().toString());
            }
            return errors.toString();
        }

        // The ontology is valid
        return "Ontology is valid!";
    }

}