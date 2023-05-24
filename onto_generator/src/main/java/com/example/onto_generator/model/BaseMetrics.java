package com.example.onto_generator.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Set;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class BaseMetrics {
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
