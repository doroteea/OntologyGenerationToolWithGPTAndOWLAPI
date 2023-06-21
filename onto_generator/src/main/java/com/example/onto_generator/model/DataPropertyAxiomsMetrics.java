package com.example.onto_generator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class DataPropertyAxiomsMetrics {
    private int SubDataPropertyOfAxiomsMetric;
    private int EquivalentDataPropertyAxiomsMetric;
    private int DisjointDataPropertyAxiomsMetric;
    private int FunctionalDataPropertyAxiomsMetric;
    private int DataPropertyDomainAxiomsMetric;
    private int DataPropertyRangeAxiomsMetric;
}
