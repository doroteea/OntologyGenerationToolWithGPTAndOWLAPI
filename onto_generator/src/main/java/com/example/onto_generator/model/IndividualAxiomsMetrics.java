package com.example.onto_generator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class IndividualAxiomsMetrics {
    private int classAssertionAxiomsMetric;
    private int objectPropertyAssertionAxiomsMetric;
    private int dataPropertyAssertionAxiomsMetric;
    private int negativeObjectPropertyAssertionAxiomsMetric;
    private int negativeDataPropertyAssertionAxiomsMetric;
    private int sameIndividualsAxiomsMetric;
    private int differentIndividualsAxiomsMetric;
}
