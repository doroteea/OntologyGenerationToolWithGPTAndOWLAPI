package com.example.onto_generator.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class DataPropertyAxiomsMetricsDTO {
    private int SubDataPropertyOfAxiomsMetric;
    private int EquivalentDataPropertyAxiomsMetric;
    private int DisjointDataPropertyAxiomsMetric;
    private int FunctionalDataPropertyAxiomsMetric;
    private int DataPropertyDomainAxiomsMetric;
    private int DataPropertyRangeAxiomsMetric;
}
