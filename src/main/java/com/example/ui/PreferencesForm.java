package com.example.ui;

import javax.swing.*;

public class PreferencesForm {
    private JPanel prefPanel;
    private JLabel resolutionLabel;
    private JComboBox resolutionComboBox;
    private JLabel themeLabel;
    private JComboBox themeComboBox;
    private JComboBox languageComboBox;
    private JLabel languageLabel;
    private JButton backButton;

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
    public JButton getBackButton() {return backButton;}


    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
