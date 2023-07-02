package com.example.onto_generator.service;

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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OntologyService {

    public String validateOntology(String ontology) {
        System.out.println("validate");
        String validationUrl = "http://iot.ee.surrey.ac.uk/SSNValidation/FormInputServlet";
        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(validationUrl);
        post.setEntity(new StringEntity(ontology, ContentType.create("application/rdf+xml")));
        try {
            HttpResponse response = client.execute(post);
            HttpEntity responseEntity = response.getEntity();

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

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(conversionUrl);
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("onto", onto));
        pairs.add(new BasicNameValuePair("to", syntax));
        pairs.add(new BasicNameValuePair("force-accept", "text/plain"));

        try {
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(pairs);
            post.setEntity(formEntity);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            HttpResponse response = client.execute(post);

            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                String convertedOntology = EntityUtils.toString(responseEntity);
                if (Objects.equals(convertedOntology, "\n")) {
                    convertedOntology = "The ontology could not be converted!";
                }
                System.out.println("response: " + convertedOntology);
                return convertedOntology;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "The ontology could not be converted!";
    }
}
