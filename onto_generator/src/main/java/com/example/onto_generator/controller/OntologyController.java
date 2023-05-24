package com.example.onto_generator.controller;

import com.example.onto_generator.model.BaseMetrics;
import com.example.onto_generator.service.OntologyService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@CrossOrigin(origins = {"http://localhost:4200"})
public class OntologyController {

    @Autowired
    private OntologyService service;

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/generate/{apikey}")
    public ResponseEntity<List<String>> generate(@PathVariable String apikey, @RequestBody String prompt) throws Exception {
        return ResponseEntity.ok(Collections.singletonList(service.chatGPT(apikey, prompt)));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/graph")
    public ResponseEntity<?> getOntologyGraph(@RequestBody String onto) {
        System.out.println("loaded: " + onto);
        return ResponseEntity.ok(service.getGraph(onto));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/validate")
    public ResponseEntity<List<String>> validate(@RequestBody String onto) {
        return ResponseEntity.ok(Collections.singletonList(validateOntology(onto)));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/convert/{syntax}")
    public ResponseEntity<List<String>> convert(@PathVariable String syntax, @RequestBody String onto) {
        return ResponseEntity.ok(Collections.singletonList(service.convertOntology(syntax, onto)));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/metrics")
    public ResponseEntity<BaseMetrics> metrics(@RequestBody String onto) throws OWLOntologyCreationException {
        System.out.println("here is the controller");
        System.out.println(onto);
        return ResponseEntity.ok(service.baseMetrics(onto));
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

    public void generateGraph(String onto) {
//        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
//        OWLOntology ontology;
//
//        List<Node> nodes = new ArrayList<>();
//        List<Edge> edges = new ArrayList<>();
//
//        try {
//            StringDocumentSource documentSource = new StringDocumentSource(onto);
//            OWLOntologyLoaderConfiguration configuration = new OWLOntologyLoaderConfiguration()
//                    .setLoadAnnotationAxioms(true);
//
//            ontology = manager.loadOntologyFromOntologyDocument(documentSource, configuration);
//
//            Set<OWLClass> classes;
//            classes = ontology.getClassesInSignature();
//
//            for (OWLClass cls : classes) {
//                Node node = new Node();
//                node.setId(cls.getIRI().getShortForm());
//                nodes.add(node);
//
//                for (OWLSubClassOfAxiom subclass : ontology.getSubClassAxiomsForSuperClass(cls)) {
//                    OWLClassExpression subclassExpression = subclass.getSubClass();
//                    if (subclassExpression.isOWLClass()) {
//                        Edge edge = new Edge();
//                        edge.setId("hasSubclass");
//                        edge.setSource_id(cls.getIRI().getShortForm());
//                        edge.setTarget_id(subclassExpression.asOWLClass().getIRI().getShortForm());
//                        edges.add(edge);
//                    }
//                }
//
//                for (OWLObjectPropertyDomainAxiom op : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN)) {
//                    if (op.getDomain().equals(cls)) {
//                        for (OWLObjectProperty oop : op.getObjectPropertiesInSignature()) {
//                            Edge edge = new Edge();
//                            edge.setId(oop.getIRI().getShortForm());
//                            edge.setSource_id(cls.getIRI().getShortForm());
//
//                            for (OWLObjectPropertyRangeAxiom opra : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_RANGE)) {
//                                if (opra.getProperty().equals(oop)) {
//                                    OWLClassExpression range = opra.getRange();
//                                    if (range.isClassExpressionLiteral()) {
//                                        edge.setTarget_id(range.asOWLClass().getIRI().getShortForm());
//                                    } else {
//                                        for (OWLEntity entity : range.getSignature()) {
//                                            if (entity.isOWLClass()) {
//                                                edge.setTarget_id(entity.asOWLClass().getIRI().getShortForm());
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                            edges.add(edge);
//                        }
//                    }
//                }
//                System.out.println(" \tData Property Domain");
//                for (OWLDataPropertyDomainAxiom dp : ontology.getAxioms(AxiomType.DATA_PROPERTY_DOMAIN)) {
//                    if (dp.getDomain().equals(cls)) {
//                        for(OWLDataProperty odp : dp.getDataPropertiesInSignature()){
//                            System.out.println("\t\t +: " + odp.getIRI().getShortForm());
//                        }
//                        //System.out.println("\t\t +:" + dp.getProperty());
//                    }
//                }
//            }
//
//            System.out.println("Nodes:");
//            for(Node c: nodes){
//                System.out.println(c.toString());
//            }
//            System.out.println("Edges:");
//            for(Edge e: edges){
//                System.out.println(e.toString());
//            }
//
//            // Create a JSON object with the nodes and edges
//            JsonObject result = new JsonObject();
//            JsonArray nodesArray = new JsonArray();
//            for (Node n : nodes) {
//                JsonObject nodeObject = new JsonObject();
//                nodeObject.addProperty("id", n.getId());
//                nodesArray.add(nodeObject);
//            }
//            result.add("nodes", nodesArray);
//
//            JsonArray edgesArray = new JsonArray();
//            for (Edge e : edges) {
//                JsonObject edgeObject = new JsonObject();
//                edgeObject.addProperty("id", e.getId());
//                edgeObject.addProperty("source_id", e.getSource_id());
//                edgeObject.addProperty("target_id", e.getTarget_id());
//                edgesArray.add(edgeObject);
//            }
//            result.add("edges", edgesArray);
//
//            // Return the JSON object
//            return result.toString();
//
//        } catch (OWLOntologyCreationException ex) {
//            Logger.getLogger("Error parsing the ontology").log(Level.SEVERE, null, ex);
//            //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//            return "Could not parse ontology";
//        }
    }

    public String validateOntology(String onto) {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology;
        try {
            StringDocumentSource documentSource = new StringDocumentSource(onto);
            OWLOntologyLoaderConfiguration configuration = new OWLOntologyLoaderConfiguration()
                    .setLoadAnnotationAxioms(true);

            ontology = manager.loadOntologyFromOntologyDocument(documentSource, configuration);

        } catch (OWLOntologyCreationException e) {
            return "Could not parse ontology";
        }

        // Create a structural reasoner to validate the ontology
        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

        // Check if the ontology is consistent
        if (!reasoner.isConsistent()) {
            System.err.println("Ontology is inconsistent!");
            return "Ontology is inconsistent!";
        }

        // Check if there are any unsatisfiable classes
        if (reasoner.isSatisfiable(ontology.getOWLOntologyManager().getOWLDataFactory().getOWLNothing())) {
            System.err.println("There are unsatisfiable classes in the ontology!");
            List<String> errors = new ArrayList<>();
            for (OWLClass cls : reasoner.getUnsatisfiableClasses().getEntitiesMinusBottom()) {
                errors.add(cls.getIRI().toString());
            }
            return errors.toString();
        }

        // The ontology is valid
        return "Ontology is valid!";
    }

//    @CrossOrigin(origins = "http://localhost:4200")
//    @GetMapping("/ontology")
//    public ResponseEntity<ByteArrayResource> getOntology() throws IOException {
//        File ontologyFile = new File("src/main/resources/ontology.owl");
//        byte[] bytes = Files.readAllBytes(ontologyFile.toPath());
//        ByteArrayResource resource = new ByteArrayResource(bytes);
//        return ResponseEntity.ok()
//                .contentLength(bytes.length)
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(resource);
//    }

}