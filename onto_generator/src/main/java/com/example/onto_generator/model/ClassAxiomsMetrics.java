package com.example.onto_generator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class ClassAxiomsMetrics {
    private int SubClassOfAxiomsMetric;
    private int EquivalentClassesAxiomsMetric;
    private int DisjointClassesAxiomsMetric;
    private int GCIMetric;
    private int HiddenGCIMetric;
}
