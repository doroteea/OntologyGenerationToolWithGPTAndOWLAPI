package com.example.onto_generator.controller;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.io.File;

public class Validate {
    public static void main(String[] args) {
        // Load the ontology
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology;
        try {
            ontology = manager.loadOntologyFromOntologyDocument(new File("src/main/resources/ontology.owl"));
        } catch (Exception e) {
            System.out.println("Failed to load the ontology: " + e.getMessage());
            return;
        }

        // Create a Hermit reasoner
        OWLReasonerFactory reasonerFactory = new ReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

        // Validate the ontology
        boolean isConsistent = reasoner.isConsistent();
        if (isConsistent) {
            System.out.println("The ontology is consistent.");
        } else {
            System.out.println("The ontology is inconsistent.");
        }

        // Dispose the reasoner
        reasoner.dispose();
    }
}