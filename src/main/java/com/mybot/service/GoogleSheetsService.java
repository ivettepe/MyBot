package com.mybot.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import lombok.SneakyThrows;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleSheetsService {

    private Sheets sheets;

    private final String sheetName = "Лист1";

    @SneakyThrows
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        GoogleCredentials credentials;
        try (InputStream in = new ClassPathResource("credentials.json").getInputStream()) {
            credentials = ServiceAccountCredentials.fromStream(in)
                .createScoped(List.of("https://www.googleapis.com/auth/spreadsheets"));
        }
        sheets = new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials)
        ).setApplicationName("Bot Google Sheets").build();
    }

    @SneakyThrows
    public void appendRow(String sheetId, List<Object> row) {
        var body = new ValueRange().setValues(List.of(row));
        sheets.spreadsheets().values()
                .append(sheetId, sheetName, body)
                .setValueInputOption("RAW")
                .execute();
    }
}
