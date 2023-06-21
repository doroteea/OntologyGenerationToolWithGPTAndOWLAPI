package com.example.onto_generator.service.metrics;

import com.example.onto_generator.model.DataPropertyAxiomsMetrics;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;

public class DataPropertyAxiomsMetricService {
    private boolean withImports;
    private OWLOntology ontology;
    private DataPropertyAxiomsMetrics metrics;

    public DataPropertyAxiomsMetricService(OWLOntology ontology, boolean withImports) {
        this.ontology = ontology;
        this.withImports = withImports;
        this.metrics = new DataPropertyAxiomsMetrics();

        calculateMetrics();
    }

    public void calculateMetrics() {
        countSubDataPropertyOfAxiomsMetric();
        countEquivalentDataPropertyAxiomsMetric();
        countDisjointDataPropertyAxiomsMetric();
        countFunctionalDataPropertyAxiomsMetric();
        countDataPropertyDomainAxiomsMetric();
        countDataPropertyRangeAxiomsMetric();
    }

    private void countSubDataPropertyOfAxiomsMetric() {
        int subDataPropertyOfAxiomsCount = ontology.getAxiomCount(AxiomType.SUB_DATA_PROPERTY,
                OntologyUtility.ImportClosures(withImports));
        metrics.setSubDataPropertyOfAxiomsMetric(subDataPropertyOfAxiomsCount);
    }

    private void countEquivalentDataPropertyAxiomsMetric() {
        int equivalentDataPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.EQUIVALENT_DATA_PROPERTIES,
                OntologyUtility.ImportClosures(withImports));
        metrics.setEquivalentDataPropertyAxiomsMetric(equivalentDataPropertyAxiomsCount);
    }

    private void countDisjointDataPropertyAxiomsMetric() {
        int disjointDataPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.DISJOINT_DATA_PROPERTIES,
                OntologyUtility.ImportClosures(withImports));
        metrics.setDisjointDataPropertyAxiomsMetric(disjointDataPropertyAxiomsCount);
    }

    private void countFunctionalDataPropertyAxiomsMetric() {
        int functionalDataPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.FUNCTIONAL_DATA_PROPERTY,
                OntologyUtility.ImportClosures(withImports));
        metrics.setFunctionalDataPropertyAxiomsMetric(functionalDataPropertyAxiomsCount);
    }

    private void countDataPropertyDomainAxiomsMetric() {
        int dataPropertyDomainAxiomsMetric = ontology.getAxiomCount(AxiomType.DATA_PROPERTY_DOMAIN,
                OntologyUtility.ImportClosures(withImports));
        metrics.setDataPropertyDomainAxiomsMetric(dataPropertyDomainAxiomsMetric);
    }

    private void countDataPropertyRangeAxiomsMetric() {
        int dataPropertyRangeAxiomsMetric = ontology.getAxiomCount(AxiomType.DATA_PROPERTY_RANGE,
                OntologyUtility.ImportClosures(withImports));
        metrics.setDataPropertyRangeAxiomsMetric(dataPropertyRangeAxiomsMetric);
    }

    public DataPropertyAxiomsMetrics getMetrics() {
        return metrics;
    }

    public void printMetrics() {
        System.out.println("Data Property Metrics:");
        System.out.println("SubDataPropertyOfAxiomsMetric: " + metrics.getSubDataPropertyOfAxiomsMetric());
        System.out.println("EquivalentDataPropertyAxiomsMetric: " + metrics.getEquivalentDataPropertyAxiomsMetric());
        System.out.println("DisjointDataPropertyAxiomsMetric: " + metrics.getDisjointDataPropertyAxiomsMetric());
        System.out.println("FunctionalDataPropertyAxiomsMetric: " + metrics.getFunctionalDataPropertyAxiomsMetric());
        System.out.println("DataPropertyDomainAxiomsMetric: " + metrics.getDataPropertyDomainAxiomsMetric());
        System.out.println("DataPropertyRangeAxiomsMetric: " + metrics.getDataPropertyRangeAxiomsMetric());
    }

}
