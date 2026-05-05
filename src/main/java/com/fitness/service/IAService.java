package com.fitness.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value; // Importante para leer properties
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IAService {

    // 🟢 Leemos los valores del application.properties
    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key}")
    private String apiKey;

    public String pedirRutinaAGemini(String promptText) {
        RestTemplate restTemplate = new RestTemplate();

        // 🔗 Construcción de URL dinámica
        String urlConClave = apiUrl + "?key=" + apiKey;

        // Construcción del cuerpo de la petición
        JSONObject body = new JSONObject();
        JSONArray contents = new JSONArray();
        JSONObject parts = new JSONObject();
        parts.put("text", promptText);
        contents.put(new JSONObject().put("parts", new JSONArray().put(parts)));
        body.put("contents", contents);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(urlConClave, entity, String.class);
            JSONObject jsonResponse = new JSONObject(response.getBody());

            // Extraemos el texto crudo de la respuesta de Google
            String textoIA = jsonResponse.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

            // 🔥 LIMPIEZA QUIRÚRGICA DEL JSON:
            // Buscamos solo lo que esté entre el primer '{' y el último '}'
            int primerCorchete = textoIA.indexOf("{");
            int ultimoCorchete = textoIA.lastIndexOf("}");

            String jsonLimpio;
            if (primerCorchete != -1 && ultimoCorchete != -1 && ultimoCorchete > primerCorchete) {
                jsonLimpio = textoIA.substring(primerCorchete, ultimoCorchete + 1);
            } else {
                throw new RuntimeException("La IA no ha devuelto un formato JSON válido (faltan llaves)");
            }

            System.out.println("========== IA RESPONDE (Procesado) ==========");
            System.out.println(jsonLimpio);
            System.out.println("=============================================");

            return jsonLimpio;

        } catch (Exception e) {
            System.err.println("\n🚨 ================= ERROR EN COMUNICACIÓN IA ================= 🚨");
            System.err.println("Mensaje: " + e.getMessage());
            System.err.println("=================================================================\n");

            throw new RuntimeException("Fallo en Gemini. Detalle exacto: " + e.getMessage());
        }
    }
}