package com.example.onto_generator.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class ObjectPropertyAxiomsMetricsDTO {
    private int subObjectPropertyAxioms;
    private int equivalentObjectPropertyAxioms;
    private int inverseObjectPropertyAxiomsMetric;
    private int disjointObjectPropertyAxiomsMetric;
    private int functionalObjectPropertyAxiomsMetric;
    private int inverseFunctionalObjectPropertiesAxiomsMetric;
    private int transitiveObjectPropertyAxiomsMetric;
    private int symmetricObjectPropertyAxiomsMetric;
    private int asymmetricObjectPropertyAxiomsMetric;
    private int reflexiveObjectPropertyAxiomsMetric;
    private int irreflexiveObjectPropertyAxiomsMetric;
    private int objectPropertyDomainAxiomsMetric;
    private int objectPropertyRangeAxiomsMetric;
    private int subPropertyChainOfAxiomsMetric;
}
