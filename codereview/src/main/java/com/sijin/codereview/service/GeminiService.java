package com.sijin.codereview.service;

import com.sijin.codereview.model.CodeIssue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.Map;

@Service
public class GeminiService {

    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String URL =
            "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=";

    public GeminiService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(5000);
        factory.setReadTimeout(20000);

        this.restTemplate = new RestTemplate(factory);
    }

    public void enrichIssue(CodeIssue issue) {

        try {
            String prompt = buildPrompt(issue);

            Map<String, Object> body = Map.of(
                    "contents", new Object[]{
                            Map.of(
                                    "parts", new Object[]{
                                            Map.of("text", prompt)
                                    }
                            )
                    }
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            System.out.println("➡️ Gemini call: " + issue.getType());

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    URL + apiKey,
                    entity,
                    Map.class
            );

            System.out.println("⬅️ Gemini response received");

            String result = extractText(response.getBody());

            // 🔥 CLEAN SPLIT
            String explanation = extractSection(result, "Explanation:");
            String suggestion = extractSection(result, "Suggestion:");

            issue.setAiExplanation(explanation);
            issue.setAiSuggestion(suggestion);

        } catch (Exception e) {
            System.out.println("❌ Gemini failed: " + issue.getFile());
            issue.setAiExplanation("AI failed");
            issue.setAiSuggestion("Suggestion unavailable");
        }
    }

    // 🔥 STRUCTURED PROMPT
    private String buildPrompt(CodeIssue issue) {
        return """
                You are a senior code reviewer.

                Give output STRICTLY in this format:

                Explanation:
                <short explanation>

                Suggestion:
                <clear fix>

                Issue: %s
                Severity: %s
                """.formatted(
                issue.getType(),
                issue.getSeverity()
        );
    }

    // 🔥 SAFE RESPONSE PARSER
    private String extractText(Map body) {
        try {
            var candidates = (java.util.List) body.get("candidates");
            if (candidates == null || candidates.isEmpty()) return "No AI response";

            var first = (Map) candidates.get(0);
            var content = (Map) first.get("content");
            var parts = (java.util.List) content.get("parts");

            if (parts == null || parts.isEmpty()) return "Empty AI response";

            var textPart = (Map) parts.get(0);
            return (String) textPart.get("text");

        } catch (Exception e) {
            return "Failed to parse AI response";
        }
    }

    // 🔥 FINAL FIX — CLEAN SECTION EXTRACTION
    private String extractSection(String text, String section) {
        try {
            int start = text.indexOf(section);
            if (start == -1) return text;

            int nextSectionIndex = text.indexOf("Suggestion:", start + section.length());
            int issueIndex = text.indexOf("Issue:", start + section.length());

            int end = text.length();

            // Explanation ends at Suggestion
            if (section.equals("Explanation:") && nextSectionIndex != -1) {
                end = nextSectionIndex;
            }

            // Remove trailing garbage (Issue / Severity)
            if (issueIndex != -1 && issueIndex < end) {
                end = issueIndex;
            }

            return text.substring(start + section.length(), end).trim();

        } catch (Exception e) {
            return text;
        }
    }
}