package com.example.onto_generator.service.metrics;

import com.example.onto_generator.model.BaseMetrics;
import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.metrics.AxiomCount;
import org.semanticweb.owlapi.metrics.DLExpressivity;
import org.semanticweb.owlapi.metrics.LogicalAxiomCount;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLClassExpressionCollector;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BaseMetricService {
    private boolean includeImports;
    private OWLOntology ontology;
    private BaseMetrics metrics;

    public BaseMetricService(OWLOntology ontology, boolean includeImports) {
        this.ontology = ontology;
        this.includeImports = includeImports;
        this.metrics = new BaseMetrics();

        calculateMetrics();
    }

    private void calculateMetrics() {
        calculateAxiomsMetric();
        calculateLogicalAxiomsMetric();
        calculateClassesCount();
        calculateObjectPropertiesCount();
        calculateDataPropertiesCount();
        calculateIndividualsCount();
        calculateDLExpressivity();
        calculateAnonymousClassesCount();
        calculateImports();
    }

    private void calculateAxiomsMetric() {
        AxiomCount axiomsCounter = new AxiomCount(ontology);
        axiomsCounter.setImportsClosureUsed(includeImports);
        metrics.setAxioms(axiomsCounter.getValue());
    }

    private void calculateClassesCount() {
        Set<OWLClass> classes = ontology.getClassesInSignature(OntologyUtility.ImportClosures(includeImports));
        metrics.setClassCount(classes.size());
    }

    private void calculateAnonymousClassesCount() {
        int anonymousClassesCount = 0;
        OWLClassExpressionCollector expressionCollector = new OWLClassExpressionCollector();
        Set<OWLClassExpression> expressions = (Set<OWLClassExpression>) expressionCollector.visit(ontology);
        for (OWLClassExpression expression : expressions) {
            if (expression.isAnonymous()) {
                anonymousClassesCount++;
            }
        }
        metrics.setAnonymousClassCount(anonymousClassesCount);
    }

    private void calculateObjectPropertiesCount() {
        Set<OWLObjectProperty> objectProperties = ontology.getObjectPropertiesInSignature(OntologyUtility.ImportClosures(includeImports));
        metrics.setObjectPropertyCount(objectProperties.size());
    }

    private void calculateDataPropertiesCount() {
        Set<OWLDataProperty> dataProperties = ontology.getDataPropertiesInSignature(OntologyUtility.ImportClosures(includeImports));
        metrics.setDataPropertyCount(dataProperties.size());
    }

    private void calculateLogicalAxiomsMetric() {
        LogicalAxiomCount logicalAxiomsCounter = new LogicalAxiomCount(ontology);
        logicalAxiomsCounter.setImportsClosureUsed(includeImports);
        metrics.setLogicalAxiomsCount(logicalAxiomsCounter.getValue());
    }

    private void calculateIndividualsCount() {
        Set<OWLNamedIndividual> individuals = ontology.getIndividualsInSignature(OntologyUtility.ImportClosures(includeImports));
        metrics.setIndividualsCount(individuals.size());
    }

    private void calculateDLExpressivity() {
        DLExpressivity dlExpressivity = new DLExpressivity(ontology);
        metrics.setDLExpresivity(dlExpressivity.getValue());
    }

    private void calculateImports() {
        Set<OWLImportsDeclaration> declaredImports = ontology.getImportsDeclarations();
        Set<String> declaredImportStrings = new HashSet<>();
        for (OWLImportsDeclaration owlImportsDeclaration : declaredImports) {
            declaredImportStrings.add(owlImportsDeclaration.getIRI().toString());
        }
        metrics.setDeclaredImports(declaredImportStrings);

        Set<String> directImports = new HashSet<>();
        Set<IRI> directImportIRIs = ontology.getDirectImportsDocuments();
        for (IRI iri : directImportIRIs) {
            directImports.add(iri.toString());
        }
        metrics.setDirectImports(directImports);

        Set<String> allActualImports = new HashSet<>();
        Set<OWLOntology> allActualImportOntologies = ontology.getImports();
        for (OWLOntology importOntology : allActualImportOntologies) {
            allActualImports.add(importOntology.getOntologyID().getOntologyIRI().toString());
        }
        metrics.setActualImports(allActualImports);
    }

    public BaseMetrics getBaseMetrics() {
        return metrics;
    }

    public void printBaseMetrics() {
        System.out.println("Base Metrics:");
        System.out.println("Axioms: " + metrics.getAxioms());
        System.out.println("Logical Axioms Count: " + metrics.getLogicalAxiomsCount());
        System.out.println("Class Count: " + metrics.getClassCount());
        System.out.println("Object Property Count: " + metrics.getObjectPropertyCount());
        System.out.println("Data Property Count: " + metrics.getDataPropertyCount());
        System.out.println("Individuals Count: " + metrics.getIndividualsCount());
        System.out.println("DL Expressivity: " + metrics.getDLExpresivity());
        System.out.println("Anonymous Class Count: " + metrics.getAnonymousClassCount());

        System.out.println("Declared Imports:");
        for (String declaredImport : metrics.getDeclaredImports()) {
            System.out.println("- " + declaredImport);
        }

        System.out.println("Direct Imports:");
        for (String directImport : metrics.getDirectImports()) {
            System.out.println("- " + directImport);
        }

        System.out.println("All Actual Imports:");
        for (String actualImport : metrics.getActualImports()) {
            System.out.println("- " + actualImport);
        }
    }
}
