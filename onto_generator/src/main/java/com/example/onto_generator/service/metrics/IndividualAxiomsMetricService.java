package com.example.onto_generator.service.metrics;

import com.example.onto_generator.model.IndividualAxiomsMetrics;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;

public class IndividualAxiomsMetricService {
    private boolean withImports;
    private OWLOntology ontology;
    private IndividualAxiomsMetrics metrics;

    public IndividualAxiomsMetricService(OWLOntology ontology, boolean withImports) {
        this.ontology = ontology;
        this.withImports = withImports;
        this.metrics = new IndividualAxiomsMetrics();
        calculateMetrics();
    }

    public void calculateMetrics() {
        countClassAssertionAxiomsMetric();
        countObjectPropertyAssertionAxiomsMetric();
        countDataPropertyAssertionAxiomsMetric();
        countNegativeObjectPropertyAssertionAxiomsMetric();
        countNegativeDataPropertyAssertionAxiomsMetric();
        countSameIndividualsAxiomsMetric();
        countDifferentIndividualsAxiomsMetric();
    }

    private void countClassAssertionAxiomsMetric() {
        int classAssertionAxiomsCount = ontology.getAxiomCount(AxiomType.CLASS_ASSERTION,
                OntologyUtility.ImportClosures(withImports));
        metrics.setClassAssertionAxiomsMetric(classAssertionAxiomsCount);
    }

    private void countObjectPropertyAssertionAxiomsMetric() {
        int objectPropertyAssertionAxiomsCount = ontology
                .getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION, OntologyUtility.ImportClosures(withImports)).size();
        metrics.setObjectPropertyAssertionAxiomsMetric(objectPropertyAssertionAxiomsCount);
    }

    private void countDataPropertyAssertionAxiomsMetric() {
        int dataPropertyAssertionAxiomsCount = ontology.getAxiomCount(AxiomType.DATA_PROPERTY_ASSERTION,
                OntologyUtility.ImportClosures(withImports));
        metrics.setDataPropertyAssertionAxiomsMetric(dataPropertyAssertionAxiomsCount);
    }

    private void countNegativeObjectPropertyAssertionAxiomsMetric() {
        int negativeObjectPropertyAssertionAxiomsCount = ontology
                .getAxiomCount(AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION, OntologyUtility.ImportClosures(withImports));
        metrics.setNegativeObjectPropertyAssertionAxiomsMetric(negativeObjectPropertyAssertionAxiomsCount);
    }

    private void countNegativeDataPropertyAssertionAxiomsMetric() {
        int negativeDataPropertyAssertionAxiomsCount = ontology
                .getAxiomCount(AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION, OntologyUtility.ImportClosures(withImports));
        metrics.setNegativeDataPropertyAssertionAxiomsMetric(negativeDataPropertyAssertionAxiomsCount);
    }

    private void countSameIndividualsAxiomsMetric() {
        int sameIndividualAxiomsCount = ontology.getAxiomCount(AxiomType.SAME_INDIVIDUAL,
                OntologyUtility.ImportClosures(withImports));
        metrics.setSameIndividualsAxiomsMetric(sameIndividualAxiomsCount);
    }

    private void countDifferentIndividualsAxiomsMetric() {
        int differentIndividualAxiomsCount = ontology.getAxiomCount(AxiomType.DIFFERENT_INDIVIDUALS,
                OntologyUtility.ImportClosures(withImports));
        metrics.setDifferentIndividualsAxiomsMetric(differentIndividualAxiomsCount);
    }

    public IndividualAxiomsMetrics getMetrics() {
        return metrics;
    }

    public void printMetrics() {
        System.out.println("Individual Metrics:");
        System.out.println("ClassAssertionAxiomsMetric: " + metrics.getClassAssertionAxiomsMetric());
        System.out.println("ObjectPropertyAssertionAxiomsMetric: " + metrics.getObjectPropertyAssertionAxiomsMetric());
        System.out.println("DataPropertyAssertionAxiomsMetric: " + metrics.getDataPropertyAssertionAxiomsMetric());
        System.out.println("NegativeObjectPropertyAssertionAxiomsMetric: " + metrics.getNegativeObjectPropertyAssertionAxiomsMetric());
        System.out.println("NegativeDataPropertyAssertionAxiomsMetric: " + metrics.getNegativeDataPropertyAssertionAxiomsMetric());
        System.out.println("SameIndividualsAxiomsMetric: " + metrics.getSameIndividualsAxiomsMetric());
        System.out.println("DifferentIndividualsAxiomsMetric: " + metrics.getDifferentIndividualsAxiomsMetric());
    }
}
