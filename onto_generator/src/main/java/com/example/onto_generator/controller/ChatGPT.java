package com.example.onto_generator.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ChatGPT {

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/generate")
    public ResponseEntity<List<String>> generate(@RequestBody String prompt) throws Exception {
       log.info("Generate with gpt-3 an ontology based on a prompt text");
       return  ResponseEntity.ok(Collections.singletonList(chatGPT(prompt)));
    }

    private String chatGPT(String text) throws Exception {
        String url = "https://api.openai.com/v1/completions";
        String API_KEY = "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";
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Bearer "+API_KEY);

        JSONObject data = new JSONObject();
        data.put("model","text-davinci-003");
        data.put("prompt", text);
        data.put("max_tokens", 4000);
        data.put("temperature", 1.0);

        con.setDoOutput(true);
        con.getOutputStream().write(data.toString().getBytes());

        String output = new BufferedReader(new InputStreamReader(con.getInputStream())).lines()
                .reduce((a, b) -> a + b).get();

        String response = new JSONObject(output).getJSONArray("choices").getJSONObject(0).getString("text");

        System.out.println(response);
        return response;
    }

//    public static void main(String[] args) throws Exception {
//        String prompt =  "Please create an ontology of age-related macular degeneration (AMD) that includes concepts such as drusen, geographic atrophy, choroidal neovascularization, and retinal pigment epithelium (RPE), and roles such as hasDrusen, hasGeographicAtrophy, hasChoroidalNeovascularization, and hasRPE. Additionally, include relationships between these concepts and any other relevant concepts, such as the relationship between drusen and RPE damage.";
//        String p = "Create an ontology that can compile in .racer for a car dealership that includes concepts such as Car, Model, Make, Dealer, and Customer, and roles such as hasModel, hasMake, and hasCustomer";
//        chatGPT(p);
//    }
}
