package com.example.onto_generator.service.metrics;

import com.example.onto_generator.model.ClassAxiomsMetrics;
import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.metrics.GCICount;
import org.semanticweb.owlapi.metrics.HiddenGCICount;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClassAxiomsMetricService {
    private boolean withImports;
    private OWLOntology ontology;
    private ClassAxiomsMetrics metrics;

    public ClassAxiomsMetricService(OWLOntology ontology, boolean withImports) {
        this.ontology = ontology;
        this.withImports = withImports;
        this.metrics = new ClassAxiomsMetrics();

        calculateMetrics();
    }

    private void calculateMetrics() {
        metrics.setSubClassOfAxiomsMetric(countSubClassOfAxiomsMetric());
        metrics.setEquivalentClassesAxiomsMetric(countEquivalentClassesAxiomsMetric());
        metrics.setDisjointClassesAxiomsMetric(countDisjointClassesAxiomsMetric());
        metrics.setGCIMetric(countGCIMetric());
        metrics.setHiddenGCIMetric(countHiddenGCIMetric());
    }

    private int countSubClassOfAxiomsMetric() {
        return ontology.getAxiomCount(AxiomType.SUBCLASS_OF,
                OntologyUtility.ImportClosures(withImports));
    }

    private int countEquivalentClassesAxiomsMetric() {
        return ontology.getAxiomCount(AxiomType.EQUIVALENT_CLASSES,
                OntologyUtility.ImportClosures(withImports));
    }

    private int countDisjointClassesAxiomsMetric() {
        return ontology.getAxiomCount(AxiomType.DISJOINT_CLASSES,
                OntologyUtility.ImportClosures(withImports));
    }

    private int countGCIMetric() {
        GCICount gc = new GCICount(ontology);
        gc.setImportsClosureUsed(withImports);
        return gc.getValue();
    }

    private int countHiddenGCIMetric() {
        HiddenGCICount hgc = new HiddenGCICount(ontology);
        hgc.setImportsClosureUsed(withImports);
        return hgc.getValue();
    }

    public ClassAxiomsMetrics getClassAxiomsMetrics() {
        return metrics;
    }

    public void printClassAxiomsMetrics() {
        System.out.println("Class Axioms Metrics:");
        System.out.println("SubClassOf Axioms: " + metrics.getSubClassOfAxiomsMetric());
        System.out.println("EquivalentClasses Axioms: " + metrics.getEquivalentClassesAxiomsMetric());
        System.out.println("DisjointClasses Axioms: " + metrics.getDisjointClassesAxiomsMetric());
        System.out.println("GCI Count: " + metrics.getGCIMetric());
        System.out.println("Hidden GCI Count: " + metrics.getHiddenGCIMetric());
    }

}
