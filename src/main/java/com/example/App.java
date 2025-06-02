package com.example;

import com.example.ui.AppFrame;
import java.net.URISyntaxException;

import com.example.utils.ThemeManager;

public class App {
    public static void main(String[] args) throws URISyntaxException {
        ThemeManager.setupDefault();
        new AppFrame();
    }
}