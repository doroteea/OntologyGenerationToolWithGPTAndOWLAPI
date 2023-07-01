package com.example.onto_generator.service;

import com.example.onto_generator.model.Edge;
import com.example.onto_generator.model.Node;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class OntologyService {

    private boolean isObjectPropertyTransitive(OWLObjectProperty op, Reasoner reasoner) {
        OWLDataFactory dataFactory = reasoner.getRootOntology().getOWLOntologyManager().getOWLDataFactory();
        OWLTransitiveObjectPropertyAxiom transitiveAxiom = dataFactory.getOWLTransitiveObjectPropertyAxiom(op);
        OWLObjectPropertyExpression transitiveProperty = transitiveAxiom.getProperty();
        if (op.equals(transitiveProperty)) {
            return true;
        } else {
            OWLSubObjectPropertyOfAxiom subPropertyAxiom = dataFactory.getOWLSubObjectPropertyOfAxiom(op, transitiveProperty);
            return reasoner.isEntailed(subPropertyAxiom);
        }
    }

    public String getGraph(String onto) {
        //File file = new File("src/main/resources/ontology.owl");
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology;
        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();

        try {
            InputStream inStream = new ByteArrayInputStream(onto.getBytes(StandardCharsets.UTF_8));
            ontology = manager.loadOntologyFromOntologyDocument(inStream);

            OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
            OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

            Set<OWLClass> classes = ontology.getClassesInSignature();

            for (OWLClass cls : classes) {
                String className = cls.getIRI().getShortForm();
                Node node = new Node();
                node.setName(className);
                node.setType("concept");
                nodes.add(node);

                // Get all subclasses of cls using the reasoner
                NodeSet<OWLClass> subclasses = reasoner.getSubClasses(cls, true);

// Iterate over the subclasses and add edges to the list
                for (OWLClass subclass : subclasses.getFlattened()) {
                    if (!subclass.isOWLNothing()) { // Ignore the owl:Nothing class
                        Edge edge = new Edge();
                        edge.setName("hasSubclass");
                        edge.setDomain(cls.getIRI().getShortForm());
                        edge.setRange(subclass.getIRI().getShortForm());
                        edge.setType("object_property");
                        edges.add(edge);
                    }
                }

// Get all object property domains using the reasoner
                Set<OWLObjectPropertyDomainAxiom> opDomains = ontology.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN);

// Iterate over the object property domains and add edges to the list
                for (OWLObjectPropertyDomainAxiom opDomain : opDomains) {
                    if (opDomain.getDomain().equals(cls)) {
                        OWLObjectProperty op = (OWLObjectProperty) opDomain.getProperty();
                        Edge edge = new Edge();
                        edge.setName(op.getIRI().getShortForm());
                        edge.setDomain(cls.getIRI().getShortForm());

                        // Check if the object property is transitive
                        boolean isTransitive = isObjectPropertyTransitive(op, (Reasoner) reasoner);
                        edge.setType(isTransitive + " op");

                        // Get the object property range using the reasoner
                        NodeSet<OWLClass> ranges = reasoner.getObjectPropertyRanges(op, true);

                        // Iterate over the ranges and add the first one to the edge
                        for (OWLClass range : ranges.getFlattened()) {
                            if (!range.isOWLThing() && !range.isOWLNothing()) { // Ignore owl:Thing and owl:Nothing classes
                                edge.setRange(range.getIRI().getShortForm());
                                //edge.setType("object_property");
                                break;
                            }
                        }
                        edges.add(edge);
                    }
                }

                for (OWLDataProperty dp : ontology.getDataPropertiesInSignature()) {
                    NodeSet<OWLClass> domains = reasoner.getDataPropertyDomains(dp, true);
                    if (domains.containsEntity(cls)) {
                        String propertyName = dp.getIRI().getShortForm();
                        String range = getDatatypeRange(ontology, dp);
                        Edge edge = new Edge();
                        edge.setName(propertyName);
                        edge.setDomain(className);
                        edge.setRange(range);
                        List<Node> newNodes = new ArrayList<>();
                        nodes.forEach(node1 -> {
                            if (!range.equals(node1.getName())) {
                                newNodes.add(new Node(range, "data_property"));
                            }
                        });
                        nodes.addAll(newNodes);
                        edge.setType("data_property");
                        edges.add(edge);
                    }
                }
            }

            for (OWLNamedIndividual individual : ontology.getIndividualsInSignature()) {
                String individualName = individual.getIRI().getShortForm();
                Node node = new Node();
                node.setName(individualName);
                node.setType("individual");
                nodes.add(node);
            }

            reasoner.dispose();
        } catch (OWLOntologyCreationException ex) {
            Logger.getLogger("Error parsing the ontology").log(Level.SEVERE, null, ex);
        }

        // print the nodes and edges
        System.out.println("Nodes:");
        for (Node node : nodes) {
            System.out.println("Name: " + node.getName() + ", Type: " + node.getType());
        }

        System.out.println("Edges:");
        for (Edge edge : edges) {
            System.out.println("Name: " + edge.getName() + ", Domain: " + edge.getDomain() + ", Range: " + edge.getRange() + ", Type: " + edge.getType());
        }

        //Create a JSON object with the nodes and edges
        JsonObject result = new JsonObject();
        JsonArray nodesArray = new JsonArray();
        for (Node n : nodes) {
            JsonObject nodeObject = new JsonObject();
            nodeObject.addProperty("name", n.getName());
            nodeObject.addProperty("type", n.getType());
            nodesArray.add(nodeObject);
        }
        result.add("nodes", nodesArray);

        JsonArray edgesArray = new JsonArray();
        for (Edge e : edges) {
            JsonObject edgeObject = new JsonObject();
            edgeObject.addProperty("name", e.getName());
            edgeObject.addProperty("type", e.getType());
            edgeObject.addProperty("domain", e.getDomain());
            edgeObject.addProperty("range", e.getRange());
            edgesArray.add(edgeObject);
        }
        result.add("edges", edgesArray);

        // Return the JSON object
        return result.toString();
    }

    private static String getDatatypeRange(OWLOntology ontology, OWLDataProperty property) {
        Set<OWLDataRange> ranges = getDatatypeRanges(ontology, property);
        StringBuilder rangeString = new StringBuilder();
        for (OWLDataRange range : ranges) {
            if (range instanceof OWLDatatype) {
                OWLDatatype datatype = (OWLDatatype) range;
                rangeString.append(datatype.getIRI().getShortForm()).append(", ");
            }
        }
        if (rangeString.length() > 0) {
            rangeString.setLength(rangeString.length() - 2); // Remove the last comma and space
        }
        return rangeString.toString();
    }

    private static Set<OWLDataRange> getDatatypeRanges(OWLOntology ontology, OWLDataProperty property) {
        Set<OWLDataRange> ranges = new HashSet<>();
        Set<OWLDataPropertyRangeAxiom> rangeAxioms = ontology.getDataPropertyRangeAxioms(property);
        for (OWLDataPropertyRangeAxiom rangeAxiom : rangeAxioms) {
            ranges.add(rangeAxiom.getRange());
        }
        return ranges;
    }

    public String convertOntology(String syntax, String onto) {
        try {
            // Load the ontology from the input string
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            InputStream input = new ByteArrayInputStream(onto.getBytes());
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(input);

            // Parse the input format and output format
            //OWLDocumentFormat inputDocFormat = parseDocumentFormat(inputFormat);
            OWLDocumentFormat outputDocFormat = parseDocumentFormat(syntax);

            // Convert the ontology to the desired format
            OutputStream outputStream = new ByteArrayOutputStream();
            manager.saveOntology(ontology, outputDocFormat, outputStream);

            return outputStream.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Could not convert ontology";
        }
    }

    private static OWLDocumentFormat parseDocumentFormat(String format) throws Exception {
        if (format.equalsIgnoreCase("OWL")) {
            return new OWLXMLDocumentFormat();
        } else if (format.equalsIgnoreCase("RDF")) {
            return new RDFXMLDocumentFormat();
        } else if (format.equalsIgnoreCase("Turtle")) {
            return new TurtleDocumentFormat();
        } else if (format.equalsIgnoreCase("Manchester Syntax")) {
            return new ManchesterSyntaxDocumentFormat();
        } else if (format.equalsIgnoreCase("Functional Syntax")) {
            return new FunctionalSyntaxDocumentFormat();
        } else if (format.equalsIgnoreCase("KRSS2")) {
            return new KRSS2DocumentFormat();
        }
        // add more syntaxes as needed
        else {
            throw new Exception("Unsupported ontology format: " + format);
        }
    }

    public String validateOntology(String ontology) {
        System.out.println("validate");
        // Set the URL of the SSNValidation service
        String validationUrl = "http://iot.ee.surrey.ac.uk/SSNValidation/FormInputServlet";

        // Create an HTTP client
        HttpClient httpClient = HttpClients.createDefault();

        // Create an HTTP POST request with the ontology content as the payload
        HttpPost httpPost = new HttpPost(validationUrl);
        httpPost.setEntity(new StringEntity(ontology, ContentType.create("application/rdf+xml")));

        // Execute the request and retrieve the response
        try {
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();

            // Process the response
            if (responseEntity != null) {
                String responseBody = EntityUtils.toString(responseEntity);
                if (Objects.equals(responseBody, "\n")) {
                    responseBody = "The ontology is valid";
                }
                System.out.println("response: " + responseBody);
                return responseBody;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "The ontology is valid!";
    }

    public String convert(String syntax, String onto) {
        System.out.println("convert");
        String conversionUrl = "https://www.ldf.fi/service/owl-converter/";

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(conversionUrl);

        // Create a list to hold form data key-value pairs
        List<NameValuePair> formData = new ArrayList<>();
        formData.add(new BasicNameValuePair("onto", onto));
        formData.add(new BasicNameValuePair("to", syntax));
        formData.add(new BasicNameValuePair("force-accept", "text/plain"));

        // Execute the request and retrieve the response
        try {
            // Create the form entity with the form data
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formData);

            // Set the form entity as the request entity
            httpPost.setEntity(formEntity);

            // Set the ontology content as the payload
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

            // Execute the POST request
            HttpResponse response = httpClient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("Response Code: " + statusCode);

            HttpEntity responseEntity = response.getEntity();

            // Process the response
            if (responseEntity != null) {
                String responseBody = EntityUtils.toString(responseEntity);
                if (Objects.equals(responseBody, "\n")) {
                    responseBody = "The ontology is valid";
                }
                System.out.println("response: " + responseBody);
                return responseBody;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "The ontology is valid!";
    }
}
