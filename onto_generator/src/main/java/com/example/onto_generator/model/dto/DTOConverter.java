package com.example.onto_generator.model.dto;

import com.example.onto_generator.model.*;

import org.springframework.stereotype.Component;

@Component
public class DTOConverter {

    public BaseMetricsDTO convertToBaseMetricsDTO(BaseMetrics baseMetrics) {
        return BaseMetricsDTO.builder()
            .Axioms(baseMetrics.getAxioms())
            .LogicalAxiomsCount(baseMetrics.getLogicalAxiomsCount())
            .ClassCount(baseMetrics.getClassCount())
            .AnonymousClassCount(baseMetrics.getAnonymousClassCount())
            .ObjectPropertyCount(baseMetrics.getObjectPropertyCount())
            .DataPropertyCount(baseMetrics.getDataPropertyCount())
            .IndividualsCount(baseMetrics.getIndividualsCount())
            .DLExpresivity(baseMetrics.getDLExpresivity())
            .directImports(baseMetrics.getDirectImports())
            .declaredImports(baseMetrics.getDeclaredImports())
            .actualImports(baseMetrics.getActualImports())
            .build();
    }

    public ClassAxiomsMetricsDTO convertToClassAxiomsMetricsDTO(ClassAxiomsMetrics classAxiomsMetrics) {
        return new ClassAxiomsMetricsDTO(
            classAxiomsMetrics.getSubClassOfAxiomsMetric(),
            classAxiomsMetrics.getEquivalentClassesAxiomsMetric(),
            classAxiomsMetrics.getDisjointClassesAxiomsMetric(),
            classAxiomsMetrics.getGCIMetric(),
            classAxiomsMetrics.getHiddenGCIMetric()
        );
    }

    public DataPropertyAxiomsMetricsDTO convertToDataPropertyAxiomsMetricsDTO(DataPropertyAxiomsMetrics dataPropertyAxiomsMetrics) {
        return new DataPropertyAxiomsMetricsDTO(
            dataPropertyAxiomsMetrics.getSubDataPropertyOfAxiomsMetric(),
            dataPropertyAxiomsMetrics.getEquivalentDataPropertyAxiomsMetric(),
            dataPropertyAxiomsMetrics.getDisjointDataPropertyAxiomsMetric(),
            dataPropertyAxiomsMetrics.getFunctionalDataPropertyAxiomsMetric(),
            dataPropertyAxiomsMetrics.getDataPropertyDomainAxiomsMetric(),
            dataPropertyAxiomsMetrics.getDataPropertyRangeAxiomsMetric()
        );
    }

    public IndividualAxiomsMetricsDTO convertToIndividualAxiomsMetricsDTO(IndividualAxiomsMetrics individualAxiomsMetrics) {
        return new IndividualAxiomsMetricsDTO(
            individualAxiomsMetrics.getClassAssertionAxiomsMetric(),
            individualAxiomsMetrics.getObjectPropertyAssertionAxiomsMetric(),
            individualAxiomsMetrics.getDataPropertyAssertionAxiomsMetric(),
            individualAxiomsMetrics.getNegativeObjectPropertyAssertionAxiomsMetric(),
            individualAxiomsMetrics.getNegativeDataPropertyAssertionAxiomsMetric(),
            individualAxiomsMetrics.getSameIndividualsAxiomsMetric(),
            individualAxiomsMetrics.getDifferentIndividualsAxiomsMetric()
        );
    }

    public ObjectPropertyAxiomsMetricsDTO convertToObjectPropertyAxiomsMetricsDTO(ObjectPropertyAxiomsMetrics objectPropertyAxiomsMetrics) {
        return new ObjectPropertyAxiomsMetricsDTO(
            objectPropertyAxiomsMetrics.getSubObjectPropertyAxioms(),
            objectPropertyAxiomsMetrics.getEquivalentObjectPropertyAxioms(),
            objectPropertyAxiomsMetrics.getInverseObjectPropertyAxiomsMetric(),
            objectPropertyAxiomsMetrics.getDisjointObjectPropertyAxiomsMetric(),
            objectPropertyAxiomsMetrics.getFunctionalObjectPropertyAxiomsMetric(),
            objectPropertyAxiomsMetrics.getInverseFunctionalObjectPropertiesAxiomsMetric(),
            objectPropertyAxiomsMetrics.getTransitiveObjectPropertyAxiomsMetric(),
            objectPropertyAxiomsMetrics.getSymmetricObjectPropertyAxiomsMetric(),
            objectPropertyAxiomsMetrics.getAsymmetricObjectPropertyAxiomsMetric(),
            objectPropertyAxiomsMetrics.getReflexiveObjectPropertyAxiomsMetric(),
            objectPropertyAxiomsMetrics.getIrreflexiveObjectPropertyAxiomsMetric(),
            objectPropertyAxiomsMetrics.getObjectPropertyDomainAxiomsMetric(),
            objectPropertyAxiomsMetrics.getObjectPropertyRangeAxiomsMetric(),
            objectPropertyAxiomsMetrics.getSubPropertyChainOfAxiomsMetric()
        );
    }
}
