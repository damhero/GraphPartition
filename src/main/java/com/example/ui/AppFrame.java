package com.example.ui;

import com.example.utils.LanguageManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class AppFrame extends JFrame {
    private final JPanel cards; // główny panel z CardLayout
    private final MainView mainFrame;
    private final PreferencesView prefsForm;

    public AppFrame() {
        setTitle(LanguageManager.get("app.title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);

        // utwórz widoki
        mainFrame = new MainView();
        mainFrame.setAppFrame(this);

        prefsForm = new PreferencesView();
        prefsForm.setAppFrame(this);



        // Panel z CardLayout
        cards = new JPanel(new CardLayout());
        cards.add(mainFrame.getMainPanel(), "MAIN");
        cards.add(prefsForm.getPrefPanel(), "PREFS");

        prefsForm.getBackButton().addActionListener(e -> {switchView("MAIN");});

        setContentPane(cards);
        setJMenuBar(createMenuBar());

        updateLanguage();



        setVisible(true);
        setResolution();

    }
    public void updateLanguage() {
        setTitle(LanguageManager.get("app.title"));
        setJMenuBar(createMenuBar());
        revalidate();
        repaint();


        mainFrame.applyLanguage();
        prefsForm.applyLanguage();
        // Dodaj tu inne widoki, jeśli masz
        // np. helpView.applyLanguage();
    }

    private void setResolution(){
        prefsForm.getResolutionComboBox().addActionListener(e -> {
            String selected = (String) prefsForm.getResolutionComboBox().getSelectedItem();
            if (selected != null) {
                String[] dims = selected.split("x");
                try {
                    int width = Integer.parseInt(dims[0]);
                    int height = Integer.parseInt(dims[1]);
                    setSize(width, height);
                    setLocationRelativeTo(null); // wyśrodkuj po zmianie
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Nieprawidłowa rozdzielczość!", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
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
        JMenu menuFile = new JMenu(LanguageManager.get("menu.file"));

        JMenu menuUpload = new JMenu(LanguageManager.get("menu.upload"));
        JMenuItem itemUploadText = new JMenuItem(LanguageManager.get("menu.upload.text"));
        JMenuItem itemUploadBin = new JMenuItem(LanguageManager.get("menu.upload.bin"));
        itemUploadText.addActionListener(e -> openFile("txt"));
        itemUploadBin.addActionListener(e -> openFile("bin"));
        menuUpload.add(itemUploadText);
        menuUpload.add(itemUploadBin);

        JMenu menuWrite = new JMenu(LanguageManager.get("menu.write"));
        JMenuItem itemWriteText = new JMenuItem(LanguageManager.get("menu.write.text"));
        JMenuItem itemWriteBin = new JMenuItem(LanguageManager.get("menu.write.bin"));
        itemWriteText.addActionListener(e -> System.out.println("Zapisz tekstowy"));
        itemWriteBin.addActionListener(e -> System.out.println("Zapisz binarny"));
        menuWrite.add(itemWriteText);
        menuWrite.add(itemWriteBin);

        menuFile.add(menuUpload);
        menuFile.add(menuWrite);

        return menuFile;
    }

    private JMenu createEditMenu() {
        JMenu menuEdit = new JMenu(LanguageManager.get("menu.edit"));

        JMenuItem itemVizSettings = new JMenuItem(LanguageManager.get("menu.viz.settings"));
        JMenuItem itemPreferences = new JMenuItem(LanguageManager.get("menu.preferences"));

        itemVizSettings.addActionListener(e -> System.out.println("Ustawienia wizualizacji"));
        itemPreferences.addActionListener(e -> switchView("PREFS"));

        menuEdit.add(itemVizSettings);
        menuEdit.add(itemPreferences);

        return menuEdit;
    }

    private JMenu createToolsMenu() {
        JMenu menuTools = new JMenu(LanguageManager.get("menu.tools"));

        JMenuItem itemAnalyze = new JMenuItem(LanguageManager.get("menu.analyze"));

        itemAnalyze.addActionListener(e -> System.out.println("Analiza podziału"));

        menuTools.add(itemAnalyze);

        return menuTools;
    }

    private JMenu createHelpMenu() {
        JMenu menuHelp = new JMenu(LanguageManager.get("menu.help"));

        JMenuItem itemManual = new JMenuItem(LanguageManager.get("menu.manual"));
        JMenuItem itemAbout = new JMenuItem(LanguageManager.get("menu.about"));

        itemManual.addActionListener(e -> JOptionPane.showMessageDialog(this, "Instrukcja jeszcze niedostępna."));
        itemAbout.addActionListener(e -> JOptionPane.showMessageDialog(this, LanguageManager.get("about.text")));

        menuHelp.add(itemManual);
        menuHelp.add(itemAbout);

        return menuHelp;
    }

    private void openFile(String expectedExtension) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Wybierz plik *." + expectedExtension);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "."+expectedExtension, expectedExtension));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String fileName = selectedFile.getName();

            if (fileName.endsWith("." + expectedExtension)) {
                // Wczytaj plik
                JOptionPane.showMessageDialog(this,
                        "Plik wczytany: " + selectedFile.getAbsolutePath(),
                        "Sukces", JOptionPane.INFORMATION_MESSAGE);
                // TODO: tutaj wczytaj zawartość pliku
            }
        }
    }

    private void saveFile(String expectedExtension) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Zapisz plik jako *." + expectedExtension);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "."+expectedExtension, expectedExtension));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String path = selectedFile.getAbsolutePath();

            // Dodaj rozszerzenie, jeśli nie zostało podane
            if (!path.toLowerCase().endsWith("." + expectedExtension)) {
                selectedFile = new File(path + "." + expectedExtension);
            }

            // TODO: tutaj zapisz dane do selectedFile
            JOptionPane.showMessageDialog(this,
                    "Zapisano do pliku: " + selectedFile.getAbsolutePath(),
                    "Zapisano", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    private void switchView(String viewName) {
        CardLayout cl = (CardLayout) cards.getLayout();
        cl.show(cards, viewName);
    }
}