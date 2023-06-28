package com.example.onto_generator.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class BaseMetricsDTO {
    private int Axioms;
    private int LogicalAxiomsCount;
    private int ClassCount;
    private int AnonymousClassCount;
    private int ObjectPropertyCount;
    private int DataPropertyCount;
    private int IndividualsCount;
    private String DLExpresivity;
    private Set<String> directImports;
    private Set<String> declaredImports;
    private Set<String> actualImports;
}
