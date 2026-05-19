package com.fitness.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IAService {

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key}")
    private String apiKey;

    // Mantenemos el nombre pedirRutinaAGemini para no romper las llamadas desde otros servicios,
    // pero ahora esta función es universal (sirve para Rutinas JSON y para Dietas en Texto).
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

            // 🔥 HEMOS QUITADO EL CANDADO ESTRICTO DE JSON.
            // Si es una Rutina, el RutinaService ya se encarga de extraer las llaves { }
            // Si es una Dieta, simplemente pasará el texto limpio hacia la pantalla.

            System.out.println("========== IA RESPONDE ==========");
            System.out.println(textoIA);
            System.out.println("=================================");

            return textoIA; // Devolvemos todo directamente

        } catch (Exception e) {
            System.err.println("\n🚨 ================= ERROR EN COMUNICACIÓN IA ================= 🚨");
            System.err.println("Mensaje: " + e.getMessage());
            System.err.println("=================================================================\n");

            throw new RuntimeException("Fallo en Gemini. Detalle exacto: " + e.getMessage());
        }
    }
}