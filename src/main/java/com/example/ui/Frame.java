package com.example.ui;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {
    private final JPanel cards; // główny panel z CardLayout
    private final MainFrame mainFrame;
    private final PreferencesForm prefsForm;

    public Frame() {
        setTitle("Aplikacja do podziału grafu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // utwórz widoki
        mainFrame = new MainFrame();
        prefsForm = new PreferencesForm();

        // Panel z CardLayout
        cards = new JPanel(new CardLayout());
        cards.add(mainFrame.getMainPanel(), "MAIN");
        cards.add(prefsForm.getPrefPanel(), "PREFS");

        setContentPane(cards);
        setJMenuBar(createMenuBar());

        setVisible(true);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createEditMenu());
        menuBar.add(createToolsMenu());
        menuBar.add(createHelpMenu());
        return menuBar;
    }

    private JMenu createFileMenu() {
        JMenu menuFile = new JMenu("Plik");

        JMenu menuUpload = new JMenu("Wczytaj");
        JMenuItem itemUploadText = new JMenuItem("Tekstowy");
        JMenuItem itemUploadBin = new JMenuItem("Binarny");
        itemUploadText.addActionListener(e -> System.out.println("Wczytaj tekstowy"));
        itemUploadBin.addActionListener(e -> System.out.println("Wczytaj binarny"));
        itemUploadCSRRG.addACtionListener(e - > System.out.println("Wczytaj CSRRG"));
        menuUpload.add(itemUploadText);
        menuUpload.add(itemUploadBin);
        menuUpload.add(itemUploadCSRRG)

        JMenu menuWrite = new JMenu("Zapisz");
        JMenuItem itemWriteText = new JMenuItem("Tekstowy");
        JMenuItem itemWriteBin = new JMenuItem("Binarny");
        itemWriteText.addActionListener(e -> System.out.println("Zapisz tekstowy"));
        itemWriteBin.addActionListener(e -> System.out.println("Zapisz binarny"));
        menuWrite.add(itemWriteText);
        menuWrite.add(itemWriteBin);

        menuFile.add(menuUpload);
        menuFile.add(menuWrite);

        return menuFile;
    }

    private JMenu createEditMenu() {
        JMenu menuEdit = new JMenu("Edycja");

        JMenuItem itemVizSettings = new JMenuItem("Ustawienia wizualizacji");
        JMenuItem itemPreferences = new JMenuItem("Preferencje");

        itemVizSettings.addActionListener(e -> System.out.println("Ustawienia wizualizacji"));
        itemPreferences.addActionListener(e -> switchView("PREFS"));

        menuEdit.add(itemVizSettings);
        menuEdit.add(itemPreferences);

        return menuEdit;
    }

    private JMenu createToolsMenu() {
        JMenu menuTools = new JMenu("Narzędzia");

        JMenuItem itemPartition = new JMenuItem("Wykonaj podział grafu");
        JMenuItem itemAnalyze = new JMenuItem("Analizuj wynik podziału");

        itemPartition.addActionListener(e -> System.out.println("Wykonaj podział grafu"));
        itemAnalyze.addActionListener(e -> System.out.println("Analiza podziału"));

        menuTools.add(itemPartition);
        menuTools.add(itemAnalyze);

        return menuTools;
    }

    private JMenu createHelpMenu() {
        JMenu menuHelp = new JMenu("Pomoc");

        JMenuItem itemManual = new JMenuItem("Instrukcja obsługi");
        JMenuItem itemAbout = new JMenuItem("O programie");
        JMenuItem itemBack = new JMenuItem("Wróć do głównego");

        itemManual.addActionListener(e -> JOptionPane.showMessageDialog(this, "Instrukcja jeszcze niedostępna."));
        itemAbout.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Niniejsza aplikacja została zaprojektowana na potrzeby zajęć w ramach przedmiotu Języki i metodyka programowania 2\n" +
                        "realizowanego podczas drugiego semestru studiów 1 stopnia Informatyka Stosowna na wydziale Elektrycznym Politechniki Warszawskiej\n" +
                        "Maj 2025\n" +
                        "Autorzy: Damian Brudkowski i Wojciech Ziembowicz", "O programie", JOptionPane.INFORMATION_MESSAGE));
        itemBack.addActionListener(e -> switchView("MAIN"));

        menuHelp.add(itemManual);
        menuHelp.add(itemAbout);
        menuHelp.add(itemBack);

        return menuHelp;
    }

    private void switchView(String viewName) {
        CardLayout cl = (CardLayout) cards.getLayout();
        cl.show(cards, viewName);
    }
}
