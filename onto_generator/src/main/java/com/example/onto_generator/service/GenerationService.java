package com.example.onto_generator.service;

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
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.XSDVocabulary;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GenerationService {

    public String gptRequest(String apikey, String text) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost("https://api.openai.com/v1/chat/completions");
        post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        post.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apikey);
        JSONObject json = new JSONObject();
        json.put("model", "gpt-3.5-turbo");


        int firstPromptIndex = text.indexOf("prompt:");
        int secondPromptIndex = text.indexOf("prompt:", firstPromptIndex + 1);
        String system_message = text.substring(0, secondPromptIndex);
        String user_message = text.substring(secondPromptIndex);
        system_message = system_message.trim();
        user_message = user_message.trim();

        JSONArray messages = new JSONArray();
        JSONObject sysMsg = new JSONObject();
        sysMsg.put("role", "system");
        sysMsg.put("content", system_message);
        messages.put(sysMsg);
        JSONObject usrMsg = new JSONObject();
        usrMsg.put("role", "user");
        usrMsg.put("content", user_message);
        messages.put(usrMsg);
        json.put("messages", messages);

        String requestBody = json.toString();
        StringEntity entity = new StringEntity(requestBody);
        post.setEntity(entity);
        HttpResponse response = client.execute(post);
        HttpEntity responseEntity = response.getEntity();

        String jsonResponseString = EntityUtils.toString(responseEntity);
        System.out.println(jsonResponseString);
        JSONObject jsonResponse = new JSONObject(jsonResponseString);

        String ontology = jsonResponse.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
        if (ontology.contains("],]")) {
            ontology = ontology.replace("],]", "]]");
        }
        return ontology;
    }

    public String tripleToOntology(String tripletsString) throws OWLOntologyCreationException, OWLOntologyStorageException {
        OWLOntologyManager owlOntologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = owlOntologyManager.createOntology();
        OWLDataFactory dataFactory = owlOntologyManager.getOWLDataFactory();

//        if(!(tripletsString.contains("[[") && tripletsString.contains("]]"))){
//            tripletsString = "["+tripletsString+"]";
//        }

        JSONArray triplets = new JSONArray(tripletsString);
        for (int i = 0; i < triplets.length(); i++) {
            JSONArray triplet = triplets.getJSONArray(i);
            if (triplet.length() != 3) {
                continue;
            }
            String triple_subject = triplet.getString(0);
            String triple_predicate = triplet.getString(1);
            String triple_object = triplet.get(2).toString();

            IRI subjectIRI = IRI.create("#" + triple_subject);
            IRI predicateIRI = IRI.create("#" + triple_predicate);
            IRI objectIRI = IRI.create("#" + triple_object);
            switch (triple_predicate) {
                case "rdf:type":
                        if(Objects.equals(triple_object, "owl:Class")){
                            OWLClass subjectClass = dataFactory.getOWLClass(subjectIRI);
                            OWLAxiom class_axiom = dataFactory.getOWLDeclarationAxiom(subjectClass);
                            owlOntologyManager.addAxiom(ontology, class_axiom);
                        } else if (Objects.equals(triple_object, "owl:ObjectProperty")){
                            OWLObjectProperty subjectObjProperty = dataFactory.getOWLObjectProperty(subjectIRI);
                            OWLAxiom object_property_axiom = dataFactory.getOWLDeclarationAxiom(subjectObjProperty);
                            owlOntologyManager.addAxiom(ontology, object_property_axiom);
                        } else if (Objects.equals(triple_object, "owl:DatatypeProperty")){
                            OWLDataProperty subjectDataProperty = dataFactory.getOWLDataProperty(subjectIRI);
                            OWLAxiom data_property_axiom = dataFactory.getOWLDeclarationAxiom(subjectDataProperty);
                            owlOntologyManager.addAxiom(ontology, data_property_axiom);
                        } else {
                            OWLClass objectClass = dataFactory.getOWLClass(objectIRI);
                            OWLNamedIndividual subjectIndividual = dataFactory.getOWLNamedIndividual(subjectIRI);
                            OWLAxiom individual_axiom = dataFactory.getOWLClassAssertionAxiom(objectClass, subjectIndividual);
                            owlOntologyManager.addAxiom(ontology, individual_axiom);
                        }
                    break;
                case "rdfs:subClassOf":
                    OWLClass superClass = dataFactory.getOWLClass(objectIRI);
                    OWLClass subClass = dataFactory.getOWLClass(subjectIRI);
                    OWLAxiom axiom = dataFactory.getOWLSubClassOfAxiom(subClass, superClass);
                    owlOntologyManager.addAxiom(ontology, axiom);
                    break;
                case "rdfs:domain":
                    OWLClass domain = dataFactory.getOWLClass(objectIRI);
                    if (ontology.containsObjectPropertyInSignature(subjectIRI)) {
                        OWLObjectProperty property = dataFactory.getOWLObjectProperty(subjectIRI);
                        OWLAxiom axiom1 = dataFactory.getOWLObjectPropertyDomainAxiom(property, domain);
                        owlOntologyManager.addAxiom(ontology, axiom1);
                    } else if (ontology.containsDataPropertyInSignature(subjectIRI)) {
                        OWLDataProperty property = dataFactory.getOWLDataProperty(subjectIRI);
                        OWLAxiom axiom2 = dataFactory.getOWLDataPropertyDomainAxiom(property, domain);
                        owlOntologyManager.addAxiom(ontology, axiom2);
                    }
                    break;
                case "rdfs:range":
                    switch (triple_object) {
                        case "xsd:integer" -> {
                            OWLDatatype rangeDataType = dataFactory.getOWLDatatype(IRI.create(XSDVocabulary.INT.getIRI().toString()));
                            axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataFactory.getOWLDataProperty(subjectIRI), rangeDataType);
                        }
                        case "xsd:date" -> {
                            OWLDatatype rangeDataType = dataFactory.getOWLDatatype(IRI.create(XSDVocabulary.DATE_TIME.getIRI().toString()));
                            axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataFactory.getOWLDataProperty(subjectIRI), rangeDataType);
                        }
                        case "xsd:float" -> {
                            OWLDatatype rangeDataType = dataFactory.getOWLDatatype(IRI.create(XSDVocabulary.FLOAT.getIRI().toString()));
                            axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataFactory.getOWLDataProperty(subjectIRI), rangeDataType);
                        }
                        case "xsd:string" -> {
                            OWLDatatype rangeDataType = dataFactory.getOWLDatatype(IRI.create(XSDVocabulary.STRING.getIRI().toString()));
                            axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataFactory.getOWLDataProperty(subjectIRI), rangeDataType);
                        }
                        case "xsd:boolean" -> {
                            OWLDatatype rangeDataType = dataFactory.getOWLDatatype(IRI.create(XSDVocabulary.BOOLEAN.getIRI().toString()));
                            axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataFactory.getOWLDataProperty(subjectIRI), rangeDataType);
                        }
                        default -> {
                            OWLObjectProperty objectProperty = dataFactory.getOWLObjectProperty(subjectIRI);
                            OWLClass range = dataFactory.getOWLClass(objectIRI);
                            axiom = dataFactory.getOWLObjectPropertyRangeAxiom(objectProperty, range);
                        }
                    }
                    owlOntologyManager.addAxiom(ontology, axiom);
                    break;
                case "rdfs:comment":
                    OWLAnnotation comment = dataFactory.getOWLAnnotation(dataFactory.getRDFSComment(), dataFactory.getOWLLiteral(triple_object));
                    OWLEntity subjectEntity;
                    if (ontology.containsClassInSignature(subjectIRI)) {
                        subjectEntity = dataFactory.getOWLClass(subjectIRI);
                    } else if (ontology.containsObjectPropertyInSignature(subjectIRI)) {
                        subjectEntity = dataFactory.getOWLObjectProperty(subjectIRI);
                    } else if (ontology.containsDataPropertyInSignature(subjectIRI)) {
                        subjectEntity = dataFactory.getOWLDataProperty(subjectIRI);
                    } else {
                        subjectEntity = dataFactory.getOWLNamedIndividual(subjectIRI);
                    }
                    OWLAxiom axiom3 = dataFactory.getOWLAnnotationAssertionAxiom(subjectEntity.getIRI(), comment);
                    owlOntologyManager.addAxiom(ontology, axiom3);
                    break;
                default:
                    OWLNamedIndividual subjectIndividual = dataFactory.getOWLNamedIndividual(subjectIRI);
                    if (triple_object.startsWith("xsd:")) {
                        String literal = triple_object.substring(4);
                        OWLLiteral objectLiteral;
                        String integer_regex = "-?\\d+";
                        String float_regex = "-?\\d+(\\.\\d+)?";
                        String date_regex = "\\d{4}-\\d{2}-\\d{2}";
                        if (literal.matches(integer_regex)) {
                            objectLiteral = dataFactory.getOWLLiteral(literal, OWL2Datatype.XSD_INT);
                        } else if (literal.matches(float_regex)) {
                            objectLiteral = dataFactory.getOWLLiteral(literal, OWL2Datatype.XSD_FLOAT);
                        } else if (literal.matches(date_regex)) {
                            objectLiteral = dataFactory.getOWLLiteral(literal, OWL2Datatype.XSD_DATE_TIME);
                        } else if (literal.matches("true|false")) {
                            objectLiteral = dataFactory.getOWLLiteral(literal, OWL2Datatype.XSD_BOOLEAN);
                        } else {
                            objectLiteral = dataFactory.getOWLLiteral(literal, OWL2Datatype.XSD_STRING);
                        }

                        OWLDataProperty dataProperty = dataFactory.getOWLDataProperty(predicateIRI);
                        OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();
                        String objectLiteralXML = renderer.render(objectLiteral);

                        OWLAxiom axiom1 = dataFactory.getOWLDataPropertyAssertionAxiom(dataProperty, subjectIndividual, objectLiteralXML);
                        owlOntologyManager.addAxiom(ontology, axiom1);
                    }
                    else
                    {
                        OWLNamedIndividual individual = dataFactory.getOWLNamedIndividual(objectIRI);
                        OWLAxiom axiom2 = dataFactory.getOWLObjectPropertyAssertionAxiom(dataFactory.getOWLObjectProperty(predicateIRI), subjectIndividual, individual);
                        owlOntologyManager.addAxiom(ontology, axiom2);
                    }
                    break;
            }

        }
        StringDocumentTarget target = new StringDocumentTarget();
        owlOntologyManager.saveOntology(ontology, new RDFXMLDocumentFormat(), target);

        return target.toString();
    }
}
