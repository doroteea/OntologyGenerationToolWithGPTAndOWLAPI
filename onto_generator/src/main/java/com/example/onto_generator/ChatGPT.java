//package com.example.onto_generator;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import org.json.JSONObject;
//
//public class ChatGPT {
//    public static void chatGPT(String text) throws Exception {
//        String url = "https://api.openai.com/v1/completions";
//        String API_KEY = "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";
//        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
//
//        con.setRequestMethod("POST");
//        con.setRequestProperty("Content-Type", "application/json");
//        con.setRequestProperty("Authorization", "Bearer "+API_KEY);
//
//        JSONObject data = new JSONObject();
//        data.put("model", "text-davinci-003");
////        data.put("model","davinci");
//        data.put("prompt", text);
//        data.put("max_tokens", 4000);
//        data.put("temperature", 1.0);
//
//        con.setDoOutput(true);
//        con.getOutputStream().write(data.toString().getBytes());
//
//        String output = new BufferedReader(new InputStreamReader(con.getInputStream())).lines()
//                .reduce((a, b) -> a + b).get();
//
//        System.out.println(new JSONObject(output).getJSONArray("choices").getJSONObject(0).getString("text"));
//    }
//
//    public static void main(String[] args) throws Exception {
//        String prompt =  "Please create an ontology of age-related macular degeneration (AMD) that includes concepts such as drusen, geographic atrophy, choroidal neovascularization, and retinal pigment epithelium (RPE), and roles such as hasDrusen, hasGeographicAtrophy, hasChoroidalNeovascularization, and hasRPE. Additionally, include relationships between these concepts and any other relevant concepts, such as the relationship between drusen and RPE damage.";
//        chatGPT(prompt);
//
//    }
//}