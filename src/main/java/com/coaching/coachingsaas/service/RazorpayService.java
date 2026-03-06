package com.coaching.coachingsaas.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class RazorpayService {

    @Value("${razorpay.key_id}")
    private String keyId;

    @Value("${razorpay.key_secret}")
    private String keySecret;

    @Value("${razorpay.pro_plan_id}")
    private String proPlanId;

    public String getKeyId() {
        return keyId;
    }

    // ✅ Create a PRO subscription on Razorpay
    public String createProMonthlySubscription(Long coachingId) {
        try {
            String json = """
            {
              "plan_id": "%s",
              "total_count": 120,
              "quantity": 1,
              "customer_notify": 1,
              "notes": {
                "coachingId": "%s"
              }
            }
            """.formatted(proPlanId, coachingId);

            String basicAuth = Base64.getEncoder().encodeToString(
                    (keyId + ":" + keySecret).getBytes(StandardCharsets.UTF_8)
            );

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.razorpay.com/v1/subscriptions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Basic " + basicAuth)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() < 200 || res.statusCode() >= 300) {
                throw new RuntimeException("Razorpay error: " + res.body());
            }

            // Very small JSON parse (no extra libs): find "id":"sub_..."
            String body = res.body();
            String marker = "\"id\":\"";
            int i = body.indexOf(marker);
            if (i < 0) throw new RuntimeException("Cannot parse subscription id: " + body);
            int start = i + marker.length();
            int end = body.indexOf("\"", start);
            return body.substring(start, end);

        } catch (Exception e) {
            throw new RuntimeException("Create subscription failed: " + e.getMessage(), e);
        }
    }

    // ✅ Verify checkout signature for subscription authorisation
    // signature = HMAC_SHA256(subscription_id + "|" + payment_id, keySecret)
    public boolean verifySubscriptionSignature(String subscriptionId, String paymentId, String razorpaySignature) {
        String payload = subscriptionId + "|" + paymentId;
        String expected = hmacSha256Hex(payload, keySecret);
        return expected.equals(razorpaySignature);
    }

    private String hmacSha256Hex(String message, String secret) {
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(keySpec);
            byte[] hash = sha256Hmac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("HMAC error", e);
        }
    }
}