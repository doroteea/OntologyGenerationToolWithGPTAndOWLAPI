//package com.example.onto_generator;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.apache.http.util.EntityUtils;
//
//public class OpenAIExample {
//    private static final String API_URL = "https://api.openai.com/v1/engines/davinci-codex/completions";
//    private static final String API_KEY = "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";
//
//    public static void main(String[] args) throws IOException {
//        // Construct the request
//        HttpClient httpClient = HttpClientBuilder.create().build();
//        HttpPost request = new HttpPost(API_URL);
//        request.setHeader("Content-Type", "application/json");
//        request.setHeader("Authorization", "Bearer " + API_KEY);
//        String requestData = "{\"prompt\": \"Create an ontology for a car dealership that includes concepts such as Car, Model, Make, Dealer, and Customer, and roles such as hasModel, hasMake, and hasCustomer\", \"max_tokens\": 1024}";
//        StringEntity requestEntity = new StringEntity(requestData, StandardCharsets.UTF_8);
//        request.setEntity(requestEntity);
//
//        // Send the request and get the response
//        HttpResponse response = httpClient.execute(request);
//        HttpEntity responseEntity = response.getEntity();
//        String responseBody = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
//        System.out.println("Response body:");
//        System.out.println(responseBody);
//    }
//}
