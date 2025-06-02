package com.example.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class ManualLoader {


    public static String loadManual(String language) {
        String manualPath = "/manual/manual_" + language + ".md";
        try (InputStream is = ManualLoader.class.getResourceAsStream(manualPath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException | NullPointerException e) {
            // W przypadku błędu wczytywania lub braku pliku
            System.err.println("Nie można wczytać instrukcji obsługi: " + e.getMessage());
            return "Instrukcja obsługi nie jest dostępna.";
        }
    }
}
