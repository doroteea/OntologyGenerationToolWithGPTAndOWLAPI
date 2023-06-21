package com.example.onto_generator.service.metrics;

import com.example.onto_generator.model.ObjectPropertyAxiomsMetrics;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;

public class ObjectPropertyAxiomsMetricService {
    private boolean withImports;
    private OWLOntology ontology;
    private ObjectPropertyAxiomsMetrics metrics;

    public ObjectPropertyAxiomsMetricService(OWLOntology owlOntology, boolean withImports) {
        
        this.ontology = owlOntology;
        this.withImports = withImports;
        this.metrics = new ObjectPropertyAxiomsMetrics();
        calculateMetrics();
    }

    public void calculateMetrics() {
        countSubObjectPropertyAxioms();
        countEquivalentObjectPropertyAxioms();
        countInverseObjectPropertyAxiomsMetric();
        countDisjointObjectPropertyAxiomsMetric();
        countFunctionalObjectPropertyAxiomsMetric();
        countInverseFunctionalObjectPropertiesAxiomsMetric();
        countTransitiveObjectPropertyAxiomsMetric();
        countSymmetricObjectPropertyAxiomsMetric();
        countAsymmetricObjectPropertyAxiomsMetric();
        countReflexiveObjectPropertyAxiomsMetric();
        countIrreflexiveObjectPropertyAxiomsMetric();
        countObjectPropertyDomainAxiomsMetric();
        countObjectPropertyRangeAxiomsMetric();
        countSubPropertyChainOfAxiomsMetric();

    }
    private void countSubObjectPropertyAxioms() {
        int subObjectPropertyOfAxiomsCount = ontology.getAxiomCount(AxiomType.SUB_OBJECT_PROPERTY,
                OntologyUtility.ImportClosures(withImports));
        metrics.setSubObjectPropertyAxioms(subObjectPropertyOfAxiomsCount);
    }

    private void countEquivalentObjectPropertyAxioms() {
        int equivalentObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.EQUIVALENT_OBJECT_PROPERTIES,
                OntologyUtility.ImportClosures(withImports));
        metrics.setEquivalentObjectPropertyAxioms(equivalentObjectPropertyAxiomsCount);
    }

    private void countInverseObjectPropertyAxiomsMetric() {
        int inverseObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.INVERSE_OBJECT_PROPERTIES,
                OntologyUtility.ImportClosures(withImports));
        metrics.setInverseObjectPropertyAxiomsMetric(inverseObjectPropertyAxiomsCount);
    }

    private void countDisjointObjectPropertyAxiomsMetric() {
        int disjointObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.DISJOINT_OBJECT_PROPERTIES,
                OntologyUtility.ImportClosures(withImports));
        metrics.setDisjointObjectPropertyAxiomsMetric(disjointObjectPropertyAxiomsCount);
    }

    private void countFunctionalObjectPropertyAxiomsMetric() {
        int functionalObjectPropertiyAxiomsCount = ontology.getAxiomCount(AxiomType.FUNCTIONAL_OBJECT_PROPERTY,
                OntologyUtility.ImportClosures(withImports));
        metrics.setFunctionalObjectPropertyAxiomsMetric(functionalObjectPropertiyAxiomsCount);
    }

    private void countInverseFunctionalObjectPropertiesAxiomsMetric() {
        int inverseFunctionalObjectPropertyAxiomsCount = ontology
                .getAxiomCount(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY, OntologyUtility.ImportClosures(withImports));
        metrics.setInverseFunctionalObjectPropertiesAxiomsMetric(inverseFunctionalObjectPropertyAxiomsCount);
    }

    private void countTransitiveObjectPropertyAxiomsMetric() {
        int transitiveObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.TRANSITIVE_OBJECT_PROPERTY,
                OntologyUtility.ImportClosures(withImports));
        metrics.setTransitiveObjectPropertyAxiomsMetric(transitiveObjectPropertyAxiomsCount);
    }

    private void countSymmetricObjectPropertyAxiomsMetric() {
        int symmetricObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.SYMMETRIC_OBJECT_PROPERTY,
                OntologyUtility.ImportClosures(withImports));
        metrics.setSymmetricObjectPropertyAxiomsMetric(symmetricObjectPropertyAxiomsCount);
    }

    private void countAsymmetricObjectPropertyAxiomsMetric() {
        int asymmetricObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.ASYMMETRIC_OBJECT_PROPERTY,
                OntologyUtility.ImportClosures(withImports));
        metrics.setAsymmetricObjectPropertyAxiomsMetric(asymmetricObjectPropertyAxiomsCount);
    }

    private void countReflexiveObjectPropertyAxiomsMetric() {
        int reflexiveObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.REFLEXIVE_OBJECT_PROPERTY,
                OntologyUtility.ImportClosures(withImports));
        metrics.setReflexiveObjectPropertyAxiomsMetric(reflexiveObjectPropertyAxiomsCount);
    }

    private void countIrreflexiveObjectPropertyAxiomsMetric() {
        int irreflexiveObjectPropertyAxiomsCount = ontology.getAxiomCount(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY,
                OntologyUtility.ImportClosures(withImports));
        metrics.setIrreflexiveObjectPropertyAxiomsMetric(irreflexiveObjectPropertyAxiomsCount);
    }

    private void countObjectPropertyDomainAxiomsMetric() {
        int objectPropertyDomainAxiomsCount = ontology.getAxiomCount(AxiomType.OBJECT_PROPERTY_DOMAIN,
                OntologyUtility.ImportClosures(withImports));
        metrics.setObjectPropertyDomainAxiomsMetric(objectPropertyDomainAxiomsCount);
    }

    private void countObjectPropertyRangeAxiomsMetric() {
        int objectPropertyRangeAxiomsCount = ontology.getAxiomCount(AxiomType.OBJECT_PROPERTY_RANGE,
                OntologyUtility.ImportClosures(withImports));
        metrics.setObjectPropertyRangeAxiomsMetric(objectPropertyRangeAxiomsCount);
    }

    private void countSubPropertyChainOfAxiomsMetric() {
        int subPropertyChainOfAxiomsCount = ontology.getAxiomCount(AxiomType.SUB_PROPERTY_CHAIN_OF,
                OntologyUtility.ImportClosures(withImports));
        metrics.setSubPropertyChainOfAxiomsMetric(subPropertyChainOfAxiomsCount);
    }

    public ObjectPropertyAxiomsMetrics getMetrics() {
        return metrics;
    }

    public void printMetrics() {
        System.out.println("Object Property Metrics:");
        System.out.println("SubObjectPropertyAxioms: " + metrics.getSubObjectPropertyAxioms());
        System.out.println("EquivalentObjectPropertyAxioms: " + metrics.getEquivalentObjectPropertyAxioms());
        System.out.println("InverseObjectPropertyAxioms: " + metrics.getInverseObjectPropertyAxiomsMetric());
        System.out.println("DisjointObjectPropertyAxioms: " + metrics.getDisjointObjectPropertyAxiomsMetric());
        System.out.println("FunctionalObjectPropertyAxioms: " + metrics.getFunctionalObjectPropertyAxiomsMetric());
        System.out.println("InverseFunctionalObjectPropertyAxioms: " + metrics.getInverseFunctionalObjectPropertiesAxiomsMetric());
        System.out.println("TransitiveObjectPropertyAxioms: " + metrics.getTransitiveObjectPropertyAxiomsMetric());
        System.out.println("SymmetricObjectPropertyAxioms: " + metrics.getSymmetricObjectPropertyAxiomsMetric());
        System.out.println("AsymmetricObjectPropertyAxioms: " + metrics.getAsymmetricObjectPropertyAxiomsMetric());
        System.out.println("ReflexiveObjectPropertyAxioms: " + metrics.getReflexiveObjectPropertyAxiomsMetric());
        System.out.println("IrreflexiveObjectPropertyAxioms: " + metrics.getIrreflexiveObjectPropertyAxiomsMetric());
        System.out.println("ObjectPropertyDomainAxioms: " + metrics.getObjectPropertyDomainAxiomsMetric());
        System.out.println("ObjectPropertyRangeAxioms: " + metrics.getObjectPropertyRangeAxiomsMetric());
        System.out.println("SubPropertyChainOfAxioms: " + metrics.getSubPropertyChainOfAxiomsMetric());
    }


}
