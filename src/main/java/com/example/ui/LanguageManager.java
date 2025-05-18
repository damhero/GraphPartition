package com.example.ui;

import java.util.*;

public class LanguageManager {
    private static Locale currentLocale = Locale.forLanguageTag("pl");
    private static ResourceBundle bundle = ResourceBundle.getBundle("Messages", currentLocale);

    public static void setLanguage(Locale locale) {
        currentLocale = locale;
        bundle = ResourceBundle.getBundle("Messages", currentLocale);
    }

    public static String get(String key) {
        return bundle.getString(key);
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }
}