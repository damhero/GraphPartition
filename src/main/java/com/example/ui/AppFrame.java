package com.example.ui;

import com.example.model.Graph;
import com.example.model.PartitionAlg;
import com.example.utils.LanguageManager;
import com.example.utils.ThemeManager;
import com.example.utils.CSRRGParser;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.List;

public class AppFrame extends JFrame {
    private final JPanel cards; // główny panel z CardLayout
    private final MainView mainFrame;
    private final PreferencesView prefsForm;
    private CSRRGParser csrrgParser;

    // Dodajemy zmienne do przechowywania danych grafu niezależnie od parsera
    private ArrayList<Integer> currentAdjacencyList;
    private ArrayList<Integer> currentAdjacencyIndices;
    private boolean isGraphLoaded = false;

    public AppFrame() {
        setTitle(LanguageManager.get("app.title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);

        // 🟢 Ikona aplikacji
        URL iconURL = getClass().getResource("/icons/logo.png");
        if (iconURL != null) {
            setIconImage(new ImageIcon(iconURL).getImage());
        }

        setLocationRelativeTo(null);

        // utwórz widoki
        mainFrame = new MainView();
        mainFrame.setAppFrame(this);

        // Poprawiony ActionListener dla przycisku podziału
        mainFrame.getDivideButton().addActionListener(e -> performGraphPartition());

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
        handleThemeChange();

        setVisible(true);
        setResolution();
    }

    // Nowa metoda do wykonywania podziału grafu
    private void performGraphPartition() {
        // Sprawdź czy graf został wczytany
        if (!isGraphLoaded || currentAdjacencyList == null || currentAdjacencyIndices == null) {
            JOptionPane.showMessageDialog(this,
                    LanguageManager.get("error.no.graph.loaded"), // "Najpierw wczytaj graf!"
                    LanguageManager.get("error.title"), // "Błąd"
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int margin = mainFrame.getSelectedMargin();
            int numParts = mainFrame.getSelectedSubGraphsCount();

            // Oblicz liczbę wierzchołków na podstawie adjacencyIndices
            int nodeCount = currentAdjacencyIndices.size() - 1;

            Graph graph = new Graph(
                    nodeCount,
                    currentAdjacencyList,
                    currentAdjacencyIndices
            );

            PartitionAlg partition = new PartitionAlg(graph, numParts, margin);

            JOptionPane.showMessageDialog(this,
                    LanguageManager.get("partition.success"), // "Podział został wykonany."
                    LanguageManager.get("success.title"), // "Sukces"
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Błąd podczas wykonywania podziału: " + ex.getMessage(),
                    "Błąd", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public void handleThemeChange() {
        prefsForm.onThemeChanged(selected -> {
            String themeKey = selected.equals(LanguageManager.get("theme.option.dark")) ? "dark" : "light";
            ThemeManager.applyTheme(themeKey, this);
        });
    }

    public void updateLanguage() {
        setTitle(LanguageManager.get("app.title"));
        setJMenuBar(createMenuBar());

        mainFrame.applyLanguage();
        prefsForm.applyLanguage();
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
        JMenuItem itemUploadCSRRG = new JMenuItem(LanguageManager.get("menu.upload.csrrg"));
        itemUploadText.addActionListener(e -> openFile("txt"));
        itemUploadBin.addActionListener(e -> openFile("bin"));
        itemUploadCSRRG.addActionListener(e -> openFile("csrrg"));
        menuUpload.add(itemUploadText);
        menuUpload.add(itemUploadBin);
        menuUpload.add(itemUploadCSRRG);

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

        JMenuItem itemPreferences = new JMenuItem(LanguageManager.get("menu.preferences"));

        itemPreferences.addActionListener(e -> switchView("PREFS"));

        menuEdit.add(itemPreferences);

        return menuEdit;
    }

    private JMenu createToolsMenu() {
        JMenu menuTools = new JMenu(LanguageManager.get("menu.tools"));

        JMenuItem itemAnalyze = new JMenuItem(LanguageManager.get("menu.analyze"));

        itemAnalyze.addActionListener(e -> {
            File evalFile = new File("output/partition_eval.txt");
            if (!evalFile.exists()) {
                JOptionPane.showMessageDialog(this, "Brak danych ewaluacji!", "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }

            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(evalFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Ewaluacja podziału", JOptionPane.INFORMATION_MESSAGE);
        });

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
                "." + expectedExtension, expectedExtension));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String fileName = selectedFile.getName();

            if (fileName.endsWith("." + expectedExtension)) {
                if (expectedExtension.equals("csrrg")) {
                    try {
                        this.csrrgParser = new CSRRGParser(selectedFile);

                        // Skopiuj dane do zmiennych klasy
                        this.currentAdjacencyList = csrrgParser.getAdjacencyList4();
                        this.currentAdjacencyIndices = csrrgParser.getAdjacencyIndices5();
                        this.isGraphLoaded = true;

                        System.out.println("AdjacencyList size: " + currentAdjacencyList.size());
                        System.out.println("AdjacencyIndices: " + currentAdjacencyIndices);

                        JOptionPane.showMessageDialog(this,
                                "Plik CSRRG wczytany: " + selectedFile.getAbsolutePath(),
                                "Sukces", JOptionPane.INFORMATION_MESSAGE);

                        mainFrame.getGraphPanel().setGraphData(
                                currentAdjacencyList,
                                currentAdjacencyIndices
                        );
                    } catch (Exception e) {
                        this.isGraphLoaded = false;
                        JOptionPane.showMessageDialog(this,
                                "Błąd podczas wczytywania pliku CSRRG: " + e.getMessage(),
                                "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (expectedExtension.equals("txt")) {
                    try {
                        var groups = com.example.utils.TXTParser.parse(selectedFile);

                        // Oblicz maksymalny indeks wierzchołka
                        int maxVertex = 0;
                        for (var group : groups) {
                            for (var pair : group.getAdjacencyPairs()) {
                                maxVertex = Math.max(maxVertex, Math.max(pair.get(0), pair.get(1)));
                            }
                        }
                        int nodeCount = maxVertex + 1;

                        // Zainicjalizuj listę sąsiedztwa
                        java.util.List<java.util.List<Integer>> neighborMap = new ArrayList<>();
                        for (int i = 0; i < nodeCount; i++) {
                            neighborMap.add(new ArrayList<>());
                        }

                        // Wypełnij sąsiadów
                        for (var group : groups) {
                            for (var pair : group.getAdjacencyPairs()) {
                                int a = pair.get(0);
                                int b = pair.get(1);
                                neighborMap.get(a).add(b);
                                neighborMap.get(b).add(a); // graf nieskierowany
                            }
                        }

                        // Zamień na CSR (adjacencyList + adjacencyIndices)
                        ArrayList<Integer> adjacencyList = new ArrayList<>();
                        ArrayList<Integer> adjacencyIndices = new ArrayList<>();
                        adjacencyIndices.add(0);

                        for (var neighbors : neighborMap) {
                            adjacencyList.addAll(neighbors);
                            adjacencyIndices.add(adjacencyList.size());
                        }

                        // Zapisz dane do zmiennych klasy
                        this.currentAdjacencyList = adjacencyList;
                        this.currentAdjacencyIndices = adjacencyIndices;
                        this.isGraphLoaded = true;

                        // Wyślij do panelu
                        mainFrame.getGraphPanel().setGraphData(adjacencyList, adjacencyIndices);

                        JOptionPane.showMessageDialog(this,
                                "Plik TXT wczytany: " + selectedFile.getAbsolutePath(),
                                "Sukces", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e) {
                        this.isGraphLoaded = false;
                        JOptionPane.showMessageDialog(this,
                                "Błąd podczas wczytywania pliku TXT: " + e.getMessage(),
                                "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Plik wczytany: " + selectedFile.getAbsolutePath(),
                            "Sukces", JOptionPane.INFORMATION_MESSAGE);
                }
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

    // Metody pomocnicze dla dostępu do danych grafu
    public boolean isGraphLoaded() {
        return isGraphLoaded;
    }

    public List<Integer> getCurrentAdjacencyList() {
        return currentAdjacencyList;
    }

    public List<Integer> getCurrentAdjacencyIndices() {
        return currentAdjacencyIndices;
    }
}