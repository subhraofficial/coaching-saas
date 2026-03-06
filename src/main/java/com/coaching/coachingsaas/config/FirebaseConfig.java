package com.coaching.coachingsaas.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;

//@Configuration
public class FirebaseConfig {

    @Value("${app.firebase.credentials}")
    private Resource firebaseCredentials;

    @PostConstruct
    public void init() throws Exception {
        if (!FirebaseApp.getApps().isEmpty()) return;

        try (InputStream is = firebaseCredentials.getInputStream()) {
            GoogleCredentials creds = GoogleCredentials.fromStream(is);
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(creds)
                    .build();
            FirebaseApp.initializeApp(options);
        }
    }
}