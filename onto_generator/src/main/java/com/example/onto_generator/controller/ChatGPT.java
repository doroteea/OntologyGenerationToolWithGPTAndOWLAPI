package com.example.onto_generator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
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
import java.util.Collections;
import java.util.List;

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

//    @PostMapping("/generate/graph")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.TEXT_PLAIN)
//    public Response generateGraph(String jsonOntology) throws OWLOntologyCreationException, IOException {
//        // Parse JSON ontology to OWL ontology
//        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
//        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(jsonOntology);
//
//        // Render ontology in RDF/XML format
//        StringWriter writer = new StringWriter();
//        XMLWriterPreferences.getInstance().setUseNamespaceEntities(false);
//        OWLOntologyXMLNamespaceManager nsManager = new OWLOntologyXMLNamespaceManager(ontology, new SimpleShortFormProvider());
//        RDFXMLDocumentFormat format = new RDFXMLDocumentFormat();
//        format.setNamespaceManager(nsManager);
//        manager.saveOntology(ontology, format, writer);
//
//        // Convert RDF/XML to Turtle
//        Model model = ModelFactory.createDefaultModel();
//        model.read(new ByteArrayInputStream(writer.toString().getBytes()), null, "RDF/XML");
//        StringWriter turtleWriter = new StringWriter();
//        model.write(turtleWriter, "TURTLE");
//
//        return Response.ok(turtleWriter.toString()).build();
//    }

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

    @GetMapping("/ontologyTEST")
    public String getOntologyData() throws OWLOntologyCreationException {
        // Load the OWL file
        File owlFile = new File("src/main/resources/ontology.owl");
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = ontologyManager.loadOntologyFromOntologyDocument(owlFile);

        // Create a reasoner to compute the subclass hierarchy
        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
        reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);

        // Convert the ontology to a JSON object
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.put("name", ontology.getOntologyID().getOntologyIRI().get().toString());
        ArrayNode childrenNode = rootNode.putArray("children");
        reasoner.getTopClassNode().getEntities().forEach(cls -> addChildNode(cls.asOWLClass(), childrenNode, reasonerFactory, ontology));
        // Return the ontology data as a JSON string
        return rootNode.toString();
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

    private void addChildNode(OWLClass cls, ArrayNode parentNode, OWLReasonerFactory reasonerFactory, OWLOntology ontology) {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode childNode = objectMapper.createObjectNode();
            childNode.put("name", cls.getIRI().toString());
            ArrayNode grandchildrenNode = childNode.putArray("children");
            OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
            reasoner.getSubClasses(cls, false).entities().forEach(subCls -> addChildNode(subCls.asOWLClass(), grandchildrenNode, reasonerFactory, ontology));
            parentNode.add(childNode);
        }




}