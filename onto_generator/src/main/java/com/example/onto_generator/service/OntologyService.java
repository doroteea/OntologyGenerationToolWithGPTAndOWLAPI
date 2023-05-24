package com.example.onto_generator.service;

import com.example.onto_generator.model.BaseMetrics;
import com.example.onto_generator.model.Edge;
import com.example.onto_generator.model.Node;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class OntologyService {

    public String generateOntology(String apikey, String prompt) throws Exception {
        System.out.println("here i am :"+prompt);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.openai.com/v1/completions");
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apikey);

        JSONObject json = new JSONObject();
        json.put("model", "text-davinci-003");

        String filePath = "src/main/resources/context";
        String instructions = new String(Files.readAllBytes(Paths.get(filePath)));

        JSONArray messages = new JSONArray();

        // System message with example instructions
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "As an ontology generator, your task is to analyze abstract texts from scientific papers and generate structured ontologies in the OWL format. You should extract relevant concepts, relationships, and domain-specific knowledge from the abstracts to create comprehensive and accurate ontological representations. Your generated ontologies should capture the key information present in the scientific papers and facilitate knowledge organization and retrieval in scientific domains.");
        messages.put(systemMessage);

        // User message with example instructions
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", instructions);
        messages.put(userMessage);

        // User message with the abstract prompt
        JSONObject promptMessage = new JSONObject();
        promptMessage.put("role", "user");
        promptMessage.put("content", prompt);
        messages.put(promptMessage);

        json.put("messages", messages);

        String requestBody = json.toString();

        // Modify the request payload
        JSONObject modifiedRequestJson = new JSONObject();
        modifiedRequestJson.put("model", "text-davinci-003");
        modifiedRequestJson.put("prompt", requestBody);

        String modifiedRequestBody = modifiedRequestJson.toString();

        StringEntity entity = new StringEntity(modifiedRequestBody);
        httpPost.setEntity(entity);

        // Remaining code remains the same
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();

        String jsonResponseString = EntityUtils.toString(responseEntity);
        System.out.println(jsonResponseString);
        JSONObject jsonResponse = new JSONObject(jsonResponseString);

        JSONArray choices = jsonResponse.getJSONArray("choices");
        if (choices.length() > 0) {
            JSONObject choice = choices.getJSONObject(0);
            String ontology = choice.getString("text");
            System.out.println(ontology);

            FileOutputStream outputStream = new FileOutputStream("ontology.owl");
            outputStream.write(ontology.getBytes());
            outputStream.close();

            return ontology;
        } else {
            throw new Exception("No ontology generated.");
        }
    }


    public String chatGPT(String apikey, String text) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.openai.com/v1/chat/completions");

        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apikey);

        JSONObject json = new JSONObject();
        json.put("model", "gpt-3.5-turbo");

        JSONArray messages = new JSONArray();

        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "As an ontology generator, your task is to analyze abstract texts from scientific papers and generate structured ontologies in the OWL format. You should extract relevant concepts, relationships, and domain-specific knowledge from the abstracts to create comprehensive and accurate ontological representations. Your generated ontologies should capture the key information present in the scientific papers and facilitate knowledge organization and retrieval in scientific domains.");
        messages.put(systemMessage);

        // User message with example instructions
        String filePath = "src/main/resources/context";
        String instructions = new String(Files.readAllBytes(Paths.get(filePath)));

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", instructions);
        messages.put(userMessage);
        json.put("messages", messages);

        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", text);
        messages.put(message);

        json.put("messages", messages);

        String requestBody = json.toString();

        StringEntity entity = new StringEntity(requestBody);
        httpPost.setEntity(entity);

        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();

        String jsonResponseString = EntityUtils.toString(responseEntity);
        System.out.println(jsonResponseString);
        JSONObject jsonResponse = new JSONObject(jsonResponseString);
        String ontology = jsonResponse.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");


        System.out.println(ontology);

        FileOutputStream outputStream = new FileOutputStream("ontology.owl");
        outputStream.write(ontology.getBytes());
        outputStream.close();

        return ontology;
    }

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

    public BaseMetrics baseMetrics(String onto) throws OWLOntologyCreationException {
        // Load the ontology from the input string
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        InputStream input = new ByteArrayInputStream(onto.getBytes());
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(input);

        BaseMetricService metricService = new BaseMetricService(ontology, true);

        metricService.printMetrics();

        return metricService.getMetrics();
    }
}
