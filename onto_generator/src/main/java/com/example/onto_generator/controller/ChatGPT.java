package com.example.onto_generator.controller;
//how can i open in protege an owl file from java code?
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.List;

@Controller
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class ChatGPT {

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/generate")
    public ResponseEntity<List<String>> generate(@RequestBody String prompt) throws Exception {
        log.info("Generate with gpt-3.5 an ontology based on a prompt text");
        return ResponseEntity.ok(Collections.singletonList(chatGPT(prompt)));
    }

    private String chatGPT(String text) throws Exception {
        String url = "https://api.openai.com/v1/completions";
        String API_KEY = "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";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.openai.com/v1/chat/completions");

        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + API_KEY);

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
        JSONObject jsonResponse = new JSONObject(EntityUtils.toString(responseEntity));
        String ontology = jsonResponse.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
        System.out.println(ontology);
        return ontology;
    }
}
