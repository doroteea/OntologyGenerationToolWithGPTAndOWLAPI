package com.example.onto_generator.valid_test;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDF;

import java.io.File;
import java.io.InputStream;

public class SPARQLQueryExample {

    public static void main(String[] args) {
        // Load the OWL ontology file
        String owlFilePath = "C:\\Users\\Doroteea\\IdeaProjects\\licenta\\onto_generator\\src\\main\\resources\\ontology.owl";
        Model model = ModelFactory.createDefaultModel();
        InputStream inputStream = FileManager.get().open(owlFilePath);
        if (inputStream == null) {
            System.out.println("File not found: " + owlFilePath);
            return;
        }
        System.out.println(inputStream);
        model.read(inputStream, null);

        String queryString = "PREFIX : <http://example.com/ontology#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "\n" +
                "SELECT ?diseaseType ?comment\n" +
                "WHERE {\n" +
                "  ?diseaseType rdfs:subClassOf :AgeRelatedMacularDegeneration ;\n" +
                "               rdfs:comment ?comment .\n" +
                "}";

        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                RDFNode diseaseType = solution.get("diseaseType");
                RDFNode comment = solution.get("comment");
                System.out.println("Disease Type: " + diseaseType);
                System.out.println("Comment: " + comment);
                System.out.println();
            }
        }

    }
}
