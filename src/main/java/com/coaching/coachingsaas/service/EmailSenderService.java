package com.coaching.coachingsaas.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    private final String apiKey;
    private final String fromEmail;

    public EmailSenderService(
            @Value("${sendgrid.api-key}") String apiKey,
            @Value("${sendgrid.from-email}") String fromEmail
    ) {
        this.apiKey = apiKey;
        this.fromEmail = fromEmail;
    }

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            Email from = new Email(fromEmail);
            Email to = new Email(toEmail);
            String subject = "Your Coaching App OTP";
            Content content = new Content(
                    "text/plain",
                    "Your OTP is: " + otp + "\n\nIt will expire soon."
            );

            Mail mail = new Mail(from, subject, to, content);

            SendGrid sendGrid = new SendGrid(apiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);

            if (response.getStatusCode() >= 400) {
                throw new RuntimeException("SendGrid failed: " + response.getBody());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }
}