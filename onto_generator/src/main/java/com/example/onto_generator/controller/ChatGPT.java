package com.example.onto_generator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

//import javax.ws.rs.Consumes;
//import javax.ws.rs.POST;
//import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//import java.io.StringWriter;

@Controller
@CrossOrigin(origins = {"http://localhost:4200"})
public class ChatGPT {

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/generate/{apikey}")
    public ResponseEntity<List<String>> generate(@PathVariable String apikey, @RequestBody String prompt) throws Exception {
        return ResponseEntity.ok(Collections.singletonList(chatGPT(apikey, prompt)));
    }

    private String chatGPT(String apikey, String text) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.openai.com/v1/chat/completions");

        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apikey);

        JSONObject json = new JSONObject();
        json.put("model", "gpt-3.5-turbo");

        JSONArray messages = new JSONArray();
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

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/ontology")
    public ResponseEntity<ByteArrayResource> getOntology() throws IOException {
        File ontologyFile = new File("src/main/resources/ontology.owl");
        byte[] bytes = Files.readAllBytes(ontologyFile.toPath());
        ByteArrayResource resource = new ByteArrayResource(bytes);
        return ResponseEntity.ok()
                .contentLength(bytes.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/graph")
    public ResponseEntity<?> getOntologyGraph() {
        File file = new File("src/main/resources/ontology.owl");
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology;

        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();

        try {
            ontology = manager.loadOntologyFromOntologyDocument(file);

            Set<OWLClass> classes;

            classes = ontology.getClassesInSignature();

            for (OWLClass cls : classes) {
                if (!cls.isOWLNamedIndividual()) {
                    Node node = new Node();
                    node.setId(cls.getIRI().getShortForm());
                    nodes.add(node);
                }

                for (OWLObjectPropertyDomainAxiom op : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN)) {
                    if (op.getDomain().equals(cls)) {
                        for(OWLObjectProperty oop : op.getObjectPropertiesInSignature()){
                            Edge edge = new Edge();
                            edge.setId(oop.getIRI().getShortForm());
                            edge.setSource_id(cls.getIRI().getShortForm());

                            for (OWLObjectPropertyRangeAxiom opra : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_RANGE)) {
                                if (opra.getProperty().equals(oop)) {
                                    OWLClassExpression range = opra.getRange();
                                    if (range.isClassExpressionLiteral()) {
                                        edge.setTarget_id(range.asOWLClass().getIRI().getShortForm());
                                    } else {
                                        for (OWLEntity entity : range.getSignature()) {
                                            if (entity.isOWLClass()) {
                                                edge.setTarget_id(entity.asOWLClass().getIRI().getShortForm());
                                            }
                                        }
                                    }
                                }
                            }
                            edges.add(edge);
                        }
                    }
                }
            }

            // Create a JSON object with the nodes and edges
            JsonObject result = new JsonObject();
            JsonArray nodesArray = new JsonArray();
            for(Node n : nodes){
                JsonObject nodeObject = new JsonObject();
                nodeObject.addProperty("id", n.getId());
                nodesArray.add(nodeObject);
            }
            result.add("nodes", nodesArray);

            JsonArray edgesArray = new JsonArray();
            for(Edge e: edges){
                JsonObject edgeObject = new JsonObject();
                edgeObject.addProperty("id", e.getId());
                edgeObject.addProperty("source_id", e.getSource_id());
                edgeObject.addProperty("target_id", e.getTarget_id());
                edgesArray.add(edgeObject);
            }
            result.add("edges", edgesArray);

            // Return the JSON object
            return ResponseEntity.ok(result.toString());

        } catch (OWLOntologyCreationException ex) {
            Logger.getLogger("Error parsing the ontology").log(Level.SEVERE, null, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/validate")
    public ResponseEntity<List<String>> validate() {
        String filePath = "src/main/resources/ontology.owl";
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology;
        try {
            ontology = manager.loadOntologyFromOntologyDocument(IRI.create(filePath));
        } catch (OWLOntologyCreationException e) {
            System.err.println("Failed to load ontology from file: " + filePath);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        // Create a structural reasoner to validate the ontology
        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

        // Check if the ontology is consistent
        if (!reasoner.isConsistent()) {
            System.err.println("Ontology is inconsistent!");
            return ResponseEntity.ok().body(List.of("Ontology is inconsistent!"));
        }

        // Check if there are any unsatisfiable classes
        if (reasoner.isSatisfiable(ontology.getOWLOntologyManager().getOWLDataFactory().getOWLNothing())) {
            System.err.println("There are unsatisfiable classes in the ontology!");
            List<String> errors = new ArrayList<>();
            for (OWLClass cls : reasoner.getUnsatisfiableClasses().getEntitiesMinusBottom()) {
                errors.add(cls.getIRI().toString());
            }
            return ResponseEntity.ok().body(errors);
        }

        // The ontology is valid
        return ResponseEntity.ok().body(List.of("Ontology is valid!"));
    }




}