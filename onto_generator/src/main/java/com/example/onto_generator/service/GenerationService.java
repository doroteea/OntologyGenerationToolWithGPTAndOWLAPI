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
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.io.OWLOntologyDocumentTarget;
import org.semanticweb.owlapi.io.StreamDocumentTarget;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.vocab.XSDVocabulary;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class GenerationService {

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

    public String gptRequest(String apikey, String text) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.openai.com/v1/chat/completions");

        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apikey);

        JSONObject json = new JSONObject();
        json.put("model", "gpt-3.5-turbo");

        JSONArray messages = new JSONArray();

        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "Please generate an RDF graph using triplets, creating a web of interconnected concepts, classes, and instances. Ensure that classes are clearly defined and appropriately hierarchical. Use more specific terms in classes and individuals to capture the nuances of the text prompt better. Finally, establish meaningful relationships among individuals using a diverse set of properties. The relationships should capture the effects, causes, and associations derived from the text prompt in detail.\n" +
                "For any concepts identified, construct an RDF triple as follows: [CONCEPT, \"rdf:type\", \"owl:Class\"].If a concept is general, discern a suitable superclass to better illustrate the context.\n" +
                "\n" +
                "If the concepts identified is a subclass of another concept, create a triple as follows: [CONCEPT, \"rdfs:subClassOf\", SUPER_CONCEPT].\n" +
                "\n" +
                "For the an identified instance that refers to a single, specific item within a CONCEPT, create a triple as follows: [INDIVIDUAL, \"rdf:type\", CONCEPT].\n" +
                "When defining relationships(connections) that describe how individuals are related to each other and they should be defined on their concepts, create triples as follows:\n" +
                "[RELATIONSHIP, \"rdf:type\", \"owl:ObjectProperty\"],\n" +
                "[RELATIONSHIP, \"rdfs:domain\", DOMAIN_CONCEPT],\n" +
                "[RELATIONSHIP, \"rdfs:range\", RANGE_CONCEPT].\n" +
                "\n" +
                "In the case of attribute assignments to individuals (pay attentions to numbers, dates and measurement units in general) and they should be defined on their concepts, deliver triples as:\n" +
                "[ATTRIBUTE, \"rdf:type\", \"owl:DatatypeProperty\"],\n" +
                "[ATTRIBUTE, \"rdfs:domain\", DOMAIN_CONCEPT],\n" +
                "[ATTRIBUTE, \"rdfs:range\", xsd:DATA_TYPE].\n" +
                "Data types include: string, integer, date, time, boolean, etc.\n" +
                "\n" +
                "For any supplementary descriptions providing minor information about any elements, format them as a triple: [ELEMENT, \"rdfs:comment\", COMMENT]. An element may be anything described above: concepts, individuals etc.\n" +
                "\n" +
                "Individual assertions come last, given in the format of [INDIVIDUAL, RELATIONSHIP, INDIVIDUAL] in cases of relationships. For attribute assignments, use [INDIVIDUAL, ATTRIBUTE, VALUE].\n" +
                "\n" +
                "The sequence of triples should be ordered like: classes, subclasses, object properties, data properties, and then individual assertions.\n" +
                "\n" +
                "Note: The last triple should not have a comma after. The triples should also make sense in the general context.\n");
        messages.put(systemMessage);

//        // User message with example instructions
//        String filePath = "src/main/resources/context";
//        String instructions = new String(Files.readAllBytes(Paths.get(filePath)));
//
//        JSONObject userMessage = new JSONObject();
//        userMessage.put("role", "user");
//        userMessage.put("content", instructions);
//        messages.put(userMessage);
//        json.put("messages", messages);

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

    public String tripleToOntology(String tripletsString) throws OWLOntologyCreationException, OWLOntologyStorageException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.createOntology();
        OWLDataFactory factory = manager.getOWLDataFactory();

        JSONArray triplets = new JSONArray(tripletsString);
        for (int i = 0; i < triplets.length(); i++) {
            JSONArray triplet = triplets.getJSONArray(i);
            String subject = triplet.getString(0);
            String predicate = triplet.getString(1);
            String object = triplet.getString(2);

            // Create the IRI for the subject
            IRI subjectIRI = IRI.create("#" + subject);
            IRI predicateIRI = IRI.create("#" + predicate);
            // Decide what to do based on the predicate
            switch (predicate) {
                case "rdf:type":
                    switch (object) {
                        case "owl:Class":
                            // The subject is a class
                            OWLClass subjectClass = factory.getOWLClass(subjectIRI);
                            OWLAxiom axiom = factory.getOWLDeclarationAxiom(subjectClass);
                            manager.addAxiom(ontology, axiom);
                            break;
                        case "owl:ObjectProperty":
                            // The subject is an object property
                            OWLObjectProperty subjectObjProperty = factory.getOWLObjectProperty(subjectIRI);
                            axiom = factory.getOWLDeclarationAxiom(subjectObjProperty);
                            manager.addAxiom(ontology, axiom);
                            break;
                        case "owl:DatatypeProperty":
                            // The subject is a datatype property
                            OWLDataProperty subjectDataProperty = factory.getOWLDataProperty(subjectIRI);
                            axiom = factory.getOWLDeclarationAxiom(subjectDataProperty);
                            manager.addAxiom(ontology, axiom);
                            break;
                        default:
                            // The subject is an individual of the object class
                            OWLClass objectClass = factory.getOWLClass(IRI.create("#" + object));
                            OWLNamedIndividual subjectIndividual = factory.getOWLNamedIndividual(subjectIRI);
                            axiom = factory.getOWLClassAssertionAxiom(objectClass, subjectIndividual);
                            manager.addAxiom(ontology, axiom);
                            break;
                    }
                    break;
                case "rdfs:subClassOf":
                    // The object is a superclass of the subject
                    OWLClass superclass = factory.getOWLClass(IRI.create("#" + object));
                    OWLClass subclass = factory.getOWLClass(subjectIRI);
                    OWLAxiom axiom = factory.getOWLSubClassOfAxiom(subclass, superclass);
                    manager.addAxiom(ontology, axiom);
                    break;
                case "rdfs:domain":
                    // The object is the domain of the subject property
                    OWLClass domain = factory.getOWLClass(IRI.create("#" + object));

                    if (ontology.containsObjectPropertyInSignature(subjectIRI)) {
                        OWLObjectProperty property = factory.getOWLObjectProperty(subjectIRI);
                        OWLAxiom axiom1 = factory.getOWLObjectPropertyDomainAxiom(property, domain);
                        manager.addAxiom(ontology, axiom1);
                    } else if (ontology.containsDataPropertyInSignature(subjectIRI)) {
                        OWLDataProperty property = factory.getOWLDataProperty(subjectIRI);
                        OWLAxiom axiom2 = factory.getOWLDataPropertyDomainAxiom(property, domain);
                        manager.addAxiom(ontology, axiom2);
                    }
                    break;
                case "rdfs:range":
                    // The object is the range of the subject property
                    OWLObjectProperty prop = factory.getOWLObjectProperty(subjectIRI);
                    if (object.equals("xsd:integer")) { // Handle specific case for xsd:integer
                        OWLDatatype rangeDataType = factory.getOWLDatatype(IRI.create(XSDVocabulary.INT.getIRI().toString()));
                        axiom = factory.getOWLDataPropertyRangeAxiom(factory.getOWLDataProperty(subjectIRI), rangeDataType);
                    } else {
                        OWLClass range = factory.getOWLClass(IRI.create("#" + object));
                        axiom = factory.getOWLObjectPropertyRangeAxiom(prop, range);
                    }
                    manager.addAxiom(ontology, axiom);
                    break;
                case "rdfs:comment":
                    // The object is a comment about the subject
                    OWLAnnotation comment = factory.getOWLAnnotation(factory.getRDFSComment(), factory.getOWLLiteral(object));

                    OWLEntity subjectEntity;
                    if (ontology.containsClassInSignature(subjectIRI)) {
                        // The subject is a class
                        subjectEntity = factory.getOWLClass(subjectIRI);
                    } else if (ontology.containsObjectPropertyInSignature(subjectIRI)) {
                        // The subject is an object property
                        subjectEntity = factory.getOWLObjectProperty(subjectIRI);
                    } else if (ontology.containsDataPropertyInSignature(subjectIRI)) {
                        // The subject is a data property
                        subjectEntity = factory.getOWLDataProperty(subjectIRI);
                    } else {
                        // The subject is an individual
                        subjectEntity = factory.getOWLNamedIndividual(subjectIRI);
                    }

                    OWLAxiom axiom3 = factory.getOWLAnnotationAssertionAxiom(subjectEntity.getIRI(), comment);
                    manager.addAxiom(ontology, axiom3);
                    break;
                default:
                    // The predicate is a property linking the subject and object
                    OWLNamedIndividual subjectIndividual = factory.getOWLNamedIndividual(subjectIRI);
                    // Handle object property and data property separately
                    if (object.startsWith("xsd:")) { // if object is a literal with datatype
                        String literal = object.substring(4); // strip "xsd:" from object
                        OWLLiteral objectLiteral = factory.getOWLLiteral(literal, factory.getOWLDatatype(IRI.create(XSDVocabulary.INTEGER.getIRI().toString())));
                        OWLAxiom axiom1 = factory.getOWLDataPropertyAssertionAxiom(factory.getOWLDataProperty(predicateIRI), subjectIndividual, objectLiteral);
                        manager.addAxiom(ontology, axiom1);
                    } else { // if object is an individual
                        OWLNamedIndividual objectIndividual = factory.getOWLNamedIndividual(IRI.create("#" + object));
                        OWLAxiom axiom2 = factory.getOWLObjectPropertyAssertionAxiom(factory.getOWLObjectProperty(predicateIRI), subjectIndividual, objectIndividual);
                        manager.addAxiom(ontology, axiom2);
                    }
                    break;

            }

        }
        StringDocumentTarget target = new StringDocumentTarget();
        manager.saveOntology(ontology, new RDFXMLDocumentFormat(), target);

        return target.toString();
    }

    public String chat(String apikey, String text) throws Exception {
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

    public static void common(String onto1, String onto2) throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology1;
        OWLOntology ontology2;

        // Load ontology 1
        InputStream inStream1 = new ByteArrayInputStream(onto1.getBytes(StandardCharsets.UTF_8));
        ontology1 = manager.loadOntologyFromOntologyDocument(inStream1);

        // Load ontology 2
        InputStream inStream2 = new ByteArrayInputStream(onto2.getBytes(StandardCharsets.UTF_8));
        ontology2 = manager.loadOntologyFromOntologyDocument(inStream2);

        // Get common classes
        Set<OWLClass> classes1 = ontology1.getClassesInSignature();
        Set<OWLClass> classes2 = ontology2.getClassesInSignature();
        Set<OWLClass> commonClasses = new HashSet<>(classes1);
        commonClasses.retainAll(classes2);

        // Get common object properties
        Set<OWLObjectProperty> objectProperties1 = ontology1.getObjectPropertiesInSignature();
        Set<OWLObjectProperty> objectProperties2 = ontology2.getObjectPropertiesInSignature();
        Set<OWLObjectProperty> commonObjectProperties = new HashSet<>(objectProperties1);
        commonObjectProperties.retainAll(objectProperties2);

        // Get common data properties
        Set<OWLDataProperty> dataProperties1 = ontology1.getDataPropertiesInSignature();
        Set<OWLDataProperty> dataProperties2 = ontology2.getDataPropertiesInSignature();
        Set<OWLDataProperty> commonDataProperties = new HashSet<>(dataProperties1);
        commonDataProperties.retainAll(dataProperties2);

        // Get common individuals
        Set<OWLNamedIndividual> individuals1 = ontology1.getIndividualsInSignature();
        Set<OWLNamedIndividual> individuals2 = ontology2.getIndividualsInSignature();
        Set<OWLNamedIndividual> commonIndividuals = new HashSet<>(individuals1);
        commonIndividuals.retainAll(individuals2);

        System.out.println("Common Classes:");
        for (OWLClass cls : commonClasses) {
            String className = cls.getIRI().getShortForm();
            System.out.println(className);
        }

        System.out.println("Common Object Properties:");
        for (OWLObjectProperty property : commonObjectProperties) {
            String propertyName = property.getIRI().getShortForm();
            System.out.println(propertyName);
        }

        System.out.println("Common Data Properties:");
        for (OWLDataProperty property : commonDataProperties) {
            String propertyName = property.getIRI().getShortForm();
            System.out.println(propertyName);
        }

        System.out.println("Common Individuals:");
        for (OWLNamedIndividual individual : commonIndividuals) {
            String individualName = individual.getIRI().getShortForm();
            System.out.println(individualName);
        }
    }


    public static void merged(String onto1, String onto2) throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology1;
        OWLOntology ontology2;

        // Load ontology 1
        InputStream inStream1 = new ByteArrayInputStream(onto1.getBytes(StandardCharsets.UTF_8));
        ontology1 = manager.loadOntologyFromOntologyDocument(inStream1);

        // Load ontology 2
        InputStream inStream2 = new ByteArrayInputStream(onto2.getBytes(StandardCharsets.UTF_8));
        ontology2 = manager.loadOntologyFromOntologyDocument(inStream2);

        Set<OWLClass> classes1 = ontology1.getClassesInSignature();
        Set<OWLClass> classes2 = ontology2.getClassesInSignature();

        Set<OWLClass> mergedClasses = new HashSet<>(classes1);
        mergedClasses.addAll(classes2);

        // Get common object properties
        Set<OWLObjectProperty> objectProperties1 = ontology1.getObjectPropertiesInSignature();
        Set<OWLObjectProperty> objectProperties2 = ontology2.getObjectPropertiesInSignature();
        Set<OWLObjectProperty> mergedObjectProperties = new HashSet<>(objectProperties1);
        mergedObjectProperties.addAll(objectProperties2);

        // Get common data properties
        Set<OWLDataProperty> dataProperties1 = ontology1.getDataPropertiesInSignature();
        Set<OWLDataProperty> dataProperties2 = ontology2.getDataPropertiesInSignature();
        Set<OWLDataProperty> mergedDataProperties = new HashSet<>(dataProperties1);
        mergedDataProperties.retainAll(dataProperties2);

        // Get common individuals
        Set<OWLNamedIndividual> individuals1 = ontology1.getIndividualsInSignature();
        Set<OWLNamedIndividual> individuals2 = ontology2.getIndividualsInSignature();
        Set<OWLNamedIndividual> mergedIndividuals = new HashSet<>(individuals1);
        mergedIndividuals.retainAll(individuals2);

        System.out.println("Merged Classes:");
        for (OWLClass cls : mergedClasses) {
            String className = cls.getIRI().getShortForm();
            System.out.println(className);
        }

        System.out.println("Merged Object Properties:");
        for (OWLObjectProperty property : mergedObjectProperties) {
            String propertyName = property.getIRI().getShortForm();
            System.out.println(propertyName);
        }

        System.out.println("Merged Data Properties:");
        for (OWLDataProperty property : mergedDataProperties) {
            String propertyName = property.getIRI().getShortForm();
            System.out.println(propertyName);
        }

        System.out.println("Merged Individuals:");
        for (OWLNamedIndividual individual : mergedIndividuals) {
            String individualName = individual.getIRI().getShortForm();
            System.out.println(individualName);
        }
    }

    public static void main(String[] args) throws OWLOntologyCreationException {
        String onto1 = "<rdf:RDF xmlns=\"http://example.com/ontology#\"\n" +
                "         xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
                "         xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n" +
                "         xmlns:owl=\"http://www.w3.org/2002/07/owl#\">\n" +
                "\n" +
                "  <owl:Ontology rdf:about=\"http://example.com/ontology#AgeRelatedMacularDegeneration1\"/>\n" +
                "\n" +
                "  <owl:Class rdf:about=\"#AgeRelatedMacularDegeneration\">\n" +
                "    <rdfs:subClassOf rdf:resource=\"#EyeDisease\"/>\n" +
                "    <rdfs:comment>Age-related macular degeneration is the leading cause of visual loss and blindness in Americans over the age of 65. It results in blurred vision, distortion, and difficulty with everyday activities, such as reading, driving, and recognizing faces. There are two types, dry and wet, and several risk factors including age, smoking, genetics, race, obesity, and cardiovascular diseases. Lifestyle changes, such as eating a healthy diet, exercising, and avoiding smoking, can reduce the risk of AMD. There are also treatments available, including nutritional supplements, light therapy, laser surgery, and injectable drugs, which depend on the type of AMD.</rdfs:comment>\n" +
                "  </owl:Class>\n" +
                "\n" +
                "  <owl:Class rdf:about=\"#EyeDisease\"/>\n" +
                "\n" +
                "  <owl:Class rdf:about=\"#DryAMD\">\n" +
                "    <rdfs:subClassOf rdf:resource=\"#AgeRelatedMacularDegeneration\"/>\n" +
                "    <rdfs:comment>Dry AMD is caused by thinning and yellow deposits in the macula. It progresses much more slowly than wet AMD and does not usually lead to severe vision loss. Nutritional supplements and light therapy are some of the treatments available for dry AMD.</rdfs:comment>\n" +
                "  </owl:Class>\n" +
                "\n" +
                "  <owl:Class rdf:about=\"#WetAMD\">\n" +
                "    <rdfs:subClassOf rdf:resource=\"#AgeRelatedMacularDegeneration\"/>\n" +
                "    <rdfs:comment>Wet AMD is caused by abnormal growth of blood vessels behind the retina which leak fluid and blood, causing swelling and permanent vision loss if not treated. For wet AMD, injectable drugs and laser surgery are used as treatments.</rdfs:comment>\n" +
                "  </owl:Class>\n" +
                "\n" +
                "  <owl:Class rdf:about=\"#RiskFactor\"/>\n" +
                "  <owl:Class rdf:about=\"#GeneticRiskFactor\">\n" +
                "    <rdfs:subClassOf rdf:resource=\"#RiskFactor\"/>\n" +
                "    <rdfs:comment>AMD has a genetic risk factor associated with it.</rdfs:comment>\n" +
                "  </owl:Class>\n" +
                "  <owl:Class rdf:about=\"#EnvironmentalFactor\"/>\n" +
                "  <owl:Class rdf:about=\"#LifestyleFactor\">\n" +
                "    <rdfs:subClassOf rdf:resource=\"#EnvironmentalFactor\"/>\n" +
                "    <rdfs:comment>Lifestyle factors, such as diet, exercise, and smoking, can reduce or increase the risk of AMD.</rdfs:comment>\n" +
                "  </owl:Class>\n" +
                "\n" +
                "  <owl:ObjectProperty rdf:about=\"#hasRiskFactor\">\n" +
                "    <rdfs:domain rdf:resource=\"#AgeRelatedMacularDegeneration\"/>\n" +
                "    <rdfs:range rdf:resource=\"#RiskFactor\"/>\n" +
                "  </owl:ObjectProperty>\n" +
                "\n" +
                "  <owl:ObjectProperty rdf:about=\"#hasGeneticRiskFactor\">\n" +
                "    <rdfs:domain rdf:resource=\"#AgeRelatedMacularDegeneration\"/>\n" +
                "    <rdfs:range rdf:resource=\"#GeneticRiskFactor\"/>\n" +
                "  </owl:ObjectProperty>\n" +
                "\n" +
                "  <owl:ObjectProperty rdf:about=\"#hasEnvironmentalFactor\">\n" +
                "    <rdfs:domain rdf:resource=\"#AgeRelatedMacularDegeneration\"/>\n" +
                "    <rdfs:range rdf:resource=\"#EnvironmentalFactor\"/>\n" +
                "  </owl:ObjectProperty>\n" +
                "\n" +
                "  <owl:Class rdf:about=\"#NutritionalSupplement\">\n" +
                "    <rdfs:subClassOf rdf:resource=\"#Treatment\"/>\n" +
                "    <rdfs:comment>There are several nutritional supplements available, such as vitamin C and zinc, for the treatment of dry AMD.</rdfs:comment>\n" +
                "  </owl:Class>\n" +
                "\n" +
                "  <owl:Class rdf:about=\"#LightTherapy\">\n" +
                "    <rdfs:subClassOf rdf:resource=\"#Treatment\"/>\n" +
                "    <rdfs:comment>Light therapy is a treatment option for dry AMD.</rdfs:comment>\n" +
                "  </owl:Class>\n" +
                "\n" +
                "  <owl:Class rdf:about=\"#LaserSurgery\">\n" +
                "    <rdfs:subClassOf rdf:resource=\"#Treatment\"/>\n" +
                "    <rdfs:comment>Laser surgery is used as a treatment option for both dry and wet AMD to help stop the leaking of blood and fluid from new vessels.</rdfs:comment>\n" +
                "  </owl:Class>\n" +
                "\n" +
                "  <owl:Class rdf:about=\"#InjectableDrugs\">\n" +
                "    <rdfs:subClassOf rdf:resource=\"#Treatment\"/>\n" +
                "    <rdfs:comment>Injectable drugs are a common treatment option for wet AMD.</rdfs:comment>\n" +
                "  </owl:Class>\n" +
                "\n" +
                "  <owl:Class rdf:about=\"#Treatment\"/>\n" +
                "\n" +
                "  <owl:ObjectProperty rdf:about=\"#hasTreatment\">\n" +
                "    <rdfs:domain rdf:resource=\"#AgeRelatedMacularDegeneration\"/>\n" +
                "    <rdfs:range>\n" +
                "        <owl:Class>\n" +
                "            <owl:unionOf rdf:parseType=\"Collection\">\n" +
                "                <rdf:Description rdf:about=\"#NutritionalSupplement\"/>\n" +
                "                <rdf:Description rdf:about=\"#LightTherapy\"/>\n" +
                "                <rdf:Description rdf:about=\"#LaserSurgery\"/>\n" +
                "                <rdf:Description rdf:about=\"#InjectableDrugs\"/>\n" +
                "            </owl:unionOf>\n" +
                "        </owl:Class>\n" +
                "    </rdfs:range>\n" +
                "    <rdfs:comment>Treatments for AMD include nutritional supplements, light therapy, laser surgery, and injectable drugs.</rdfs:comment>\n" +
                "  </owl:ObjectProperty>\n" +
                "\n" +
                "  <owl:Class rdf:about=\"#RegularEyeExamination\">\n" +
                "    <rdfs:subClassOf rdf:resource=\"#Prevention\"/>\n" +
                "    <rdfs:comment>Regular eye exams are key to detecting AMD early and monitoring its progression.</rdfs:comment>\n" +
                "  </owl:Class>\n" +
                "\n" +
                "  <owl:Class rdf:about=\"#Prevention\"/>\n" +
                "\n" +
                "  <owl:ObjectProperty rdf:about=\"#hasPreventativeMeasure\">\n" +
                "    <rdfs:domain rdf:resource=\"#AgeRelatedMacularDegeneration\"/>\n" +
                "    <rdfs:range>\n" +
                "        <owl:Class>\n" +
                "            <owl:unionOf rdf:parseType=\"Collection\">\n" +
                "                <rdf:Description rdf:about=\"#LifestyleFactor\"/>\n" +
                "                <rdf:Description rdf:about=\"#RegularEyeExamination\"/>\n" +
                "            </owl:unionOf>\n" +
                "        </owl:Class>\n" +
                "    </rdfs:range>\n" +
                "    <rdfs:comment>Preventative measures for AMD include lifestyle changes and regular eye exams.</rdfs:comment>\n" +
                "  </owl:ObjectProperty>\n" +
                "\n" +
                "</rdf:RDF>";

        String onto2 =
                "<rdf:RDF xmlns=\"http://example.com/ontology#\"\n" +
                "         xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
                "         xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n" +
                "         xmlns:owl=\"http://www.w3.org/2002/07/owl#\">\n" +
                "\n" +
                "  <owl:Ontology rdf:about=\"http://example.com/ontology#AgeRelatedMacularDegeneration2\"/>\n" +
                "\n" +
                "  <owl:Class rdf:about=\"#AgeRelatedMacularDegeneration\">\n" +
                "    <rdfs:subClassOf rdf:resource=\"#Disease\"/>\n" +
                "    <rdfs:comment>Age-related macular degeneration (AMD) affects the macula in the eye, and is the leading cause of visual loss and blindness in Americans over the age of 65.</rdfs:comment>\n" +
                "  </owl:Class>\n" +
                "\n" +
                "  <owl:Class rdf:about=\"#Disease\"/>\n" +
                "\n" +
                "  <owl:Class rdf:about=\"#DryAMD\">\n" +
                "    <rdfs:subClassOf rdf:resource=\"#AgeRelatedMacularDegeneration\"/>\n" +
                "    <rdfs:comment>Dry AMD is a more common type of AMD, caused by thinning and yellow deposits in the macula, and progresses much more slowly than wet AMD.</rdfs:comment>\n" +
                "  </owl:Class>\n" +
                "\n" +
                "  <owl:Class rdf:about=\"#WetAMD\">\n" +
                "    <rdfs:subClassOf rdf:resource=\"#AgeRelatedMacularDegeneration\"/>\n" +
                "    <rdfs:comment>Wet AMD is a type of AMD caused by abnormal new blood vessels growing behind the retina, and can lead to permanent vision loss.</rdfs:comment>\n" +
                "  </owl:Class>\n" +
                "\n" +
                "  <owl:Class rdf:about=\"#PhotoreceptorDegeneration\">\n" +
                "    <rdfs:subClassOf rdf:resource=\"#AgeRelatedMacularDegeneration\"/>\n" +
                "    <rdfs:comment>The breakdown of photoreceptor cells in the macula causes blurred vision, distortion, and difficulty with everyday activities such as reading, driving, and recognizing faces.</rdfs:comment>\n" +
                "  </owl:Class>\n" +
                "\n" +
                "  <owl:ObjectProperty rdf:about=\"#hasType\">\n" +
                "    <rdfs:domain rdf:resource=\"#AgeRelatedMacularDegeneration\"/>\n" +
                "    <rdfs:range rdf:resource=\"#Disease\"/>\n" +
                "    <rdfs:comment>AMD can be classified into dry and wet types.</rdfs:comment>\n" +
                "  </owl:ObjectProperty>\n" +
                "\n" +
                "  <owl:ObjectProperty rdf:about=\"#hasRiskFactor\">\n" +
                "    <rdfs:domain rdf:resource=\"#AgeRelatedMacularDegeneration\"/>\n" +
                "    <rdfs:range rdf:resource=\"#RiskFactor\"/>\n" +
                "    <rdfs:comment>The risk factors for AMD include age, smoking, genetics, race, obesity, and cardiovascular diseases.</rdfs:comment>\n" +
                "  </owl:ObjectProperty>\n" +
                "\n" +
                "  <owl:Class rdf:about=\"#RiskFactor\"/>\n" +
                "\n" +
                "  <owl:Class rdf:about=\"#LifestyleModification\">\n" +
                "    <rdfs:subClassOf rdf:resource=\"#RiskFactor\"/>\n" +
                "    <rdfs:comment>Lifestyle changes like eating a healthy diet, exercising regularly, and avoiding smoking can help reduce the risk of AMD.</rdfs:comment>\n" +
                "  </owl:Class>\n" +
                "\n" +
                "  <owl:ObjectProperty rdf:about=\"#advisesToModifyLifestyle\">\n" +
                "    <rdfs:domain rdf:resource=\"#AgeRelatedMacularDegeneration\"/>\n" +
                "    <rdfs:range rdf:resource=\"#LifestyleModification\"/>\n" +
                "  </owl:ObjectProperty>\n" +
                "\n" +
                "  <owl:Class rdf:about=\"#Treatment\"/>\n" +
                "  \n" +
                "  <owl:Class rdf:about=\"#NutritionalSupplement\">\n" +
                "    <rdfs:subClassOf rdf:resource=\"#Treatment\"/>\n" +
                "    <rdfs:comment>Several nutritional supplements are available for dry AMD, such as vitamin C and zinc.</rdfs:comment>\n" +
                "  </owl:Class>\n" +
                "\n" +
                "  <owl:ObjectProperty rdf:about=\"#hasTreatment\">\n" +
                "    <rdfs:domain rdf:resource=\"#AgeRelatedMacularDegeneration\"/>\n" +
                "    <rdfs:range rdf:resource=\"#Treatment\"/>\n" +
                "    <rdfs:comment>Treatments for AMD include light therapy, laser photocoagulation, anti-angiogenic drugs, and injectable drugs.</rdfs:comment>\n" +
                "  </owl:ObjectProperty>\n" +
                "\n" +
                "  <owl:Class rdf:about=\"#WetAMDInjection\"/>\n" +
                "  \n" +
                "  <owl:ObjectProperty rdf:about=\"#hasInjection\">\n" +
                "    <rdfs:domain rdf:resource=\"#WetAMD\"/>\n" +
                "    <rdfs:range rdf:resource=\"#WetAMDInjection\"/>\n" +
                "    <rdfs:comment>Injectable drugs are a main treatment for wet AMD, and are the most effective treatment available.</rdfs:comment>\n" +
                "  </owl:ObjectProperty>\n" +
                "\n" +
                "  <owl:Class rdf:about=\"#Lasersurgery\">\n" +
                "    <rdfs:subClassOf rdf:resource=\"#Treatment\"/>\n" +
                "    <rdfs:comment>Laser surgery is also used to treat AMD.</rdfs:comment>\n" +
                "  </owl:Class>\n" +
                "\n" +
                "  <owl:ObjectProperty rdf:about=\"#hasSurgery\">\n" +
                "    <rdfs:domain rdf:resource=\"#AgeRelatedMacularDegeneration\"/>\n" +
                "    <rdfs:range rdf:resource=\"#Lasersurgery\"/>\n" +
                "  </owl:ObjectProperty>\n" +
                "\n" +
                "  <owl:Class rdf:about=\"#RegularEyeExamination\">\n" +
                "    <rdfs:subClassOf rdf:resource=\"#RiskFactor\"/>\n" +
                "    <rdfs:comment>Regular eye examinations are key to detecting AMD early.</rdfs:comment>\n" +
                "  </owl:Class>\n" +
                "\n" +
                "  <owl:ObjectProperty rdf:about=\"#recommendsEyeExam\">\n" +
                "    <rdfs:domain rdf:resource=\"#AgeRelatedMacularDegeneration\"/>\n" +
                "    <rdfs:range rdf:resource=\"#RegularEyeExamination\"/>\n" +
                "  </owl:ObjectProperty>\n" +
                "\n" +
                "</rdf:RDF>";

        common(onto1, onto2);
        merged(onto1, onto2);
    }
}
