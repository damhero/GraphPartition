package com.example.utils;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class ThemeManager {

    private static String currentTheme = "light"; // domyślny motyw

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

    public static String getCurrentTheme() {
        return currentTheme;
    }

    public static void setupDefault() {
        FlatLightLaf.setup();
        currentTheme = "light";
    }
}