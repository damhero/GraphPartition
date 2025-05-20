package com.example.ui;

import com.example.utils.LanguageManager;
import java.util.function.Consumer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

public class PreferencesView {
    private JPanel prefPanel;
    private JLabel resolutionLabel;
    private JComboBox<String> resolutionComboBox;
    private JLabel themeLabel;
    private JComboBox<String> themeComboBox;
    private JComboBox<String> languageComboBox;
    private JLabel languageLabel;
    private JButton backButton;
    private AppFrame appFrame;

    public PreferencesView() {
        resolutionComboBox.setSelectedIndex(1);
        setupListeners();
        applyLanguage();
    }

    public void setAppFrame(AppFrame frame) {
        this.appFrame = frame;
    }
    private void setupListeners() {
        languageComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) languageComboBox.getSelectedItem();
                if ("Polski".equals(selected)) {
                    LanguageManager.setLanguage(Locale.forLanguageTag("pl"));
                } else if ("English".equals(selected)) {
                    LanguageManager.setLanguage(Locale.ENGLISH);
                }
                if (appFrame != null) {
                    appFrame.updateLanguage();
                }
            }
        });
    }
    public void onThemeChanged(Consumer<String> handler) {
        themeComboBox.addActionListener(e -> {
            String selected = (String) themeComboBox.getSelectedItem();
            if (selected != null) {
                handler.accept(selected);
            }
        });
    }

    public void applyLanguage() {
        languageLabel.setText(LanguageManager.get("label.language"));
        resolutionLabel.setText(LanguageManager.get("label.resolution"));
        backButton.setText(LanguageManager.get("button.back"));
        themeLabel.setText(LanguageManager.get("label.theme"));

        // 🔸 Zachowaj wybrane indeksy
        int selectedLangIdx = languageComboBox.getSelectedIndex();
        int selectedResIdx = resolutionComboBox.getSelectedIndex();
        int selectedThemeIdx = themeComboBox.getSelectedIndex();

// 🔹 Wypełnij na nowo ComboBoxy
        languageComboBox.removeAllItems();
        languageComboBox.addItem(LanguageManager.get("language.option.polish"));
        languageComboBox.addItem(LanguageManager.get("language.option.english"));

        resolutionComboBox.removeAllItems();
        resolutionComboBox.addItem(LanguageManager.get("resolution.option.1"));
        resolutionComboBox.addItem(LanguageManager.get("resolution.option.2"));
        resolutionComboBox.addItem(LanguageManager.get("resolution.option.3"));

        themeComboBox.removeAllItems();
        themeComboBox.addItem(LanguageManager.get("theme.option.light"));
        themeComboBox.addItem(LanguageManager.get("theme.option.dark"));

// 🔸 Przywróć wcześniejsze wybory
        languageComboBox.setSelectedIndex(Math.max(0, selectedLangIdx));
        resolutionComboBox.setSelectedIndex(Math.max(0, selectedResIdx));
        themeComboBox.setSelectedIndex(Math.max(0, selectedThemeIdx));
    }

    // Gettery
    public JPanel getPrefPanel() {
        return prefPanel;
    }

    public JComboBox<String> getResolutionComboBox() {
        return resolutionComboBox;
    }

    public JComboBox<String> getThemeComboBox() {
        return themeComboBox;
    }

    public JComboBox<String> getLanguageComboBox() {
        return languageComboBox;
    }

    public JButton getBackButton() {
        return backButton;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}