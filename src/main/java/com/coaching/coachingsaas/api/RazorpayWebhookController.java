package com.coaching.coachingsaas.api;

import com.coaching.coachingsaas.domain.Coaching;
import com.coaching.coachingsaas.domain.PlanType;
import com.coaching.coachingsaas.repo.CoachingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/billing/webhook")
public class RazorpayWebhookController {

    private final CoachingRepository coachingRepository;

    @Value("${razorpay.webhook_secret}")
    private String webhookSecret;

    public RazorpayWebhookController(CoachingRepository coachingRepository) {
        this.coachingRepository = coachingRepository;
    }

    @PostMapping("/razorpay")
    public String handle(HttpServletRequest request, @RequestHeader("X-Razorpay-Signature") String signature) {
        try {
            String body = readBody(request);

            // ✅ Verify webhook signature: HMAC-SHA256(raw_body, webhookSecret)
            String expected = hmacSha256Hex(body, webhookSecret);
            if (!expected.equals(signature)) {
                throw new RuntimeException("Invalid webhook signature");
            }

            // Very small parsing (no libs): detect event + subscription id + period dates
            // For production later we can parse JSON properly, but this works now.
            String event = extractJsonString(body, "\"event\":\"", "\"");
            String subId = extractJsonString(body, "\"subscription_id\":\"", "\""); // sometimes in payload
            if (subId == null) subId = extractJsonString(body, "\"id\":\"sub_", "\""); // fallback

            if (subId == null) return "NO_SUB_ID";

            Coaching c = coachingRepository.findByRazorpaySubscriptionId(subId).orElse(null);
            if (c == null) return "NO_COACHING";

            // Update status based on event
            // Common subscription events: subscription.activated, subscription.cancelled, subscription.completed, subscription.paused
            if (event != null && event.contains("subscription.activated")) {
                c.setPlanType(PlanType.PRO);
                c.setSubscriptionStatus("ACTIVE");

                // try reading current period end from payload (unix timestamps)
                Long startAt = extractJsonLong(body, "\"current_start\":", ",");
                Long endAt = extractJsonLong(body, "\"current_end\":", ",");

                if (startAt != null) c.setCurrentPeriodStart(toLocalDateTime(startAt));
                if (endAt != null) c.setCurrentPeriodEnd(toLocalDateTime(endAt));

                coachingRepository.save(c);
                return "OK_ACTIVE";
            }

            if (event != null && (event.contains("subscription.cancelled") || event.contains("subscription.completed"))) {
                c.setSubscriptionStatus("CANCELLED");
                c.setPlanType(PlanType.FREE);
                coachingRepository.save(c);
                return "OK_CANCELLED";
            }

            if (event != null && event.contains("subscription.paused")) {
                c.setSubscriptionStatus("PAUSED");
                c.setPlanType(PlanType.FREE);
                coachingRepository.save(c);
                return "OK_PAUSED";
            }

            // default
            c.setSubscriptionStatus(event == null ? "UNKNOWN" : event);
            coachingRepository.save(c);

            return "OK";

        } catch (Exception e) {
            throw new RuntimeException("Webhook error: " + e.getMessage(), e);
        }
    }

    private String readBody(HttpServletRequest request) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = request.getReader()) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    private String hmacSha256Hex(String message, String secret) throws Exception {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256Hmac.init(keySpec);
        byte[] hash = sha256Hmac.doFinal(message.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private LocalDateTime toLocalDateTime(long unixSeconds) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(unixSeconds), ZoneId.systemDefault());
    }

    private String extractJsonString(String body, String prefix, String until) {
        int i = body.indexOf(prefix);
        if (i < 0) return null;
        int start = i + prefix.length();
        int end = body.indexOf(until, start);
        if (end < 0) return null;
        return body.substring(start, end);
    }

    private Long extractJsonLong(String body, String prefix, String until) {
        int i = body.indexOf(prefix);
        if (i < 0) return null;
        int start = i + prefix.length();
        int end = body.indexOf(until, start);
        if (end < 0) end = body.indexOf("}", start);
        if (end < 0) return null;
        String num = body.substring(start, end).trim().replaceAll("[^0-9]", "");
        if (num.isEmpty()) return null;
        return Long.parseLong(num);
    }
}