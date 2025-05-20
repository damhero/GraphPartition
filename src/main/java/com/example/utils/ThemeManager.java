package com.example.utils;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class ThemeManager {

    private static String currentTheme = "light"; // domyślny motyw

    /**
     * Ustawia motyw na podstawie nazwy i odświeża wygląd
     * @param themeName "light" lub "dark"
     * @param frame główne okno aplikacji
     */
    public static void applyTheme(String themeName, JFrame frame) {
        try {
            if ("dark".equalsIgnoreCase(themeName)) {
                FlatDarkLaf.setup();
                currentTheme = "dark";
            } else {
                FlatLightLaf.setup();
                currentTheme = "light";
            }
            SwingUtilities.updateComponentTreeUI(frame);
            frame.repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Zwraca nazwę aktualnego motywu
     */
    public static String getCurrentTheme() {
        return currentTheme;
    }

    /**
     * Ustawia domyślny motyw na starcie (np. z ustawień użytkownika)
     */
    public static void setupDefault() {
        FlatLightLaf.setup(); // albo FlatDarkLaf.setup()
        currentTheme = "light";
    }
}