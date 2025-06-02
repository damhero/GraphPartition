package com.example.ui;

import com.example.model.Graph;
import com.example.model.Group;
import com.example.model.PartitionAlg;
import com.example.utils.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class AppFrame extends JFrame {
    private final JPanel cards; // główny panel z CardLayout
    private final MainView mainFrame;
    private final PreferencesView prefsForm;
    private CSRRGParser csrrgParser;
    private int currentVertexCount;
    private String currentGraphFormat = ""; // Możliwe wartości: "txt", "bin"

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
        updateButtonStates();
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

            Graph graph = new Graph(currentVertexCount, currentAdjacencyList, currentAdjacencyIndices);

            PartitionAlg partition = new PartitionAlg(graph, numParts, margin);

            // Pobierz wyniki podziału z obiektu partition
            // Zakładam, że PartitionAlg ma metodę getPartitions() lub podobną,
            // która zwraca informacje o podziale grafu
            Map<Integer, Integer> partitionGroups = partition.getPartitionGroups();

            // Określenie liczby grup w podziale
            int groupCount = 0;
            for (int groupId : partitionGroups.values()) {
                groupCount = Math.max(groupCount, groupId + 1); // +1 bo numeracja od 0
            }


            // Zastosuj podział do panelu grafu używając metody applyPartition
            mainFrame.getGraphPanel().applyPartition(partitionGroups, groupCount);


            // Odśwież panel grafu
            mainFrame.getGraphPanel().repaint();

            JOptionPane.showMessageDialog(this,
                    LanguageManager.get("partition.success"), // "Podział został wykonany."
                    LanguageManager.get("success.title"), // "Sukces"
                    JOptionPane.INFORMATION_MESSAGE);

            // Zamiast pokazywać nowy widok, pozostań na obecnym i pokaż dialog z wynikami
            showPartitionResult();

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
        itemWriteText.addActionListener(e -> saveFile("txt"));
        itemWriteBin.addActionListener(e -> saveFile("bin"));
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

        itemAnalyze.addActionListener(e -> showPartitionResult());

        menuTools.add(itemAnalyze);

        return menuTools;
    }

    private void showPartitionResult(){
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
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), LanguageManager.get("analyze.title"), JOptionPane.INFORMATION_MESSAGE);
    }

    private JMenu createHelpMenu() {
        JMenu menuHelp = new JMenu(LanguageManager.get("menu.help"));

        JMenuItem itemManual = new JMenuItem(LanguageManager.get("menu.manual"));
        JMenuItem itemAbout = new JMenuItem(LanguageManager.get("menu.about"));

        itemManual.addActionListener(e -> showManual());
        itemAbout.addActionListener(e -> JOptionPane.showMessageDialog(this, LanguageManager.get("about.text")));

        menuHelp.add(itemManual);
        menuHelp.add(itemAbout);

        return menuHelp;
    }

    private void showManual() {
        JDialog manualDialog = new JDialog(this, LanguageManager.get("menu.manual"), true);
        manualDialog.setSize(800, 600);
        manualDialog.setLocationRelativeTo(this);

        // Pobierz aktualny język z LanguageManager
        String currentLanguage = LanguageManager.getCurrentLocale().getLanguage();

        // Wczytaj instrukcję obsługi z pliku
        String markdownContent = ManualLoader.loadManual(currentLanguage);

        // Konwertuj Markdown na HTML
        String htmlContent = convertMarkdownToHtml(markdownContent);

        // Utwórz komponent do wyświetlania treści
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setText(htmlContent);
        textPane.setEditable(false);
        textPane.setCaretPosition(0);

        // Dodaj pasek przewijania
        JScrollPane scrollPane = new JScrollPane(textPane);
        manualDialog.add(scrollPane);

        manualDialog.setVisible(true);
    }

    /**
     * Konwertuje tekst Markdown na HTML
     * @param markdown tekst w formacie Markdown
     * @return sformatowany HTML
     */
    private String convertMarkdownToHtml(String markdown) {
        // Podstawowy szablon HTML z CSS dla stylowania
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><style>");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.6; }");
        html.append("h1 { color: #2c3e50; border-bottom: 1px solid #eee; padding-bottom: 10px; }");
        html.append("h2 { color: #3498db; margin-top: 20px; }");
        html.append("h3 { color: #2980b9; }");
        html.append("pre { background: #f8f8f8; border: 1px solid #ddd; padding: 10px; border-radius: 5px; overflow-x: auto; }");
        html.append("code { background: #f8f8f8; padding: 2px 4px; }");
        html.append("ul, ol { padding-left: 25px; }");
        html.append("a { color: #3498db; text-decoration: none; }");
        html.append("a:hover { text-decoration: underline; }");
        html.append("</style></head><body>");

        // Zamień nagłówki
        String content = markdown;
        content = content.replaceAll("(?m)^# (.*?)$", "<h1>$1</h1>");
        content = content.replaceAll("(?m)^## (.*?)$", "<h2>$1</h2>");
        content = content.replaceAll("(?m)^### (.*?)$", "<h3>$1</h3>");

        // Zamień listy
        content = content.replaceAll("(?m)^\\* (.*?)$", "<li>$1</li>");
        content = content.replaceAll("(?m)^\\d+\\. (.*?)$", "<li>$1</li>");
        content = content.replaceAll("(?s)<li>.*?</li>", "<ul>$0</ul>");
        content = content.replaceAll("<ul>(<li>.*?</li>)</ul><ul>", "<ul>$1");
        content = content.replaceAll("</li></ul><ul><li>", "</li><li>");

        // Zamień pogrubienia i kursywy
        content = content.replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>");
        content = content.replaceAll("\\*(.*?)\\*", "<em>$1</em>");

        // Zamień odnośniki
        content = content.replaceAll("\\[(.*?)\\]\\((.*?)\\)", "<a href=\"$2\">$1</a>");

        // Zamień bloki kodu
        content = content.replaceAll("(?s)```(.*?)```", "<pre><code>$1</code></pre>");

        // Zamień linie kodu w tekście
        content = content.replaceAll("`(.*?)`", "<code>$1</code>");

        // Zamień poziome linie
        content = content.replaceAll("(?m)^---$", "<hr>");

        // Akapity
        content = content.replaceAll("(?m)^([^<].*?)$", "<p>$1</p>");
        content = content.replaceAll("<p>\\s*</p>", "");

        // Popraw zagnieżdżone tagi
        content = content.replaceAll("<p>(<h[1-3]>.*?</h[1-3]>)</p>", "$1");
        content = content.replaceAll("<p>(<ul>.*?</ul>)</p>", "$1");
        content = content.replaceAll("<p>(<pre>.*?</pre>)</p>", "$1");
        content = content.replaceAll("<p>(<hr>)</p>", "$1");

        html.append(content);
        html.append("</body></html>");

        return html.toString();
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
                mainFrame.getGraphPanel().resetPartition();
                if (expectedExtension.equals("csrrg")) {
                    try {
                        this.csrrgParser = new CSRRGParser(selectedFile);

                        // Skopiuj dane do zmiennych klasy
                        this.currentVertexCount = csrrgParser.getVerticesList2().size();
                        this.currentAdjacencyList = csrrgParser.getAdjacencyList4();
                        this.currentAdjacencyIndices = csrrgParser.getAdjacencyIndices5();
                        this.isGraphLoaded = true;

                        JOptionPane.showMessageDialog(this,
                                "Plik CSRRG wczytany: " + selectedFile.getAbsolutePath(),
                                "Sukces", JOptionPane.INFORMATION_MESSAGE);

                        mainFrame.getGraphPanel().setGraphData(
                                currentVertexCount,
                                currentAdjacencyList,
                                currentAdjacencyIndices
                        );
                        this.currentGraphFormat="csrrg";
                        updateButtonStates();
                    } catch (Exception e) {
                        this.isGraphLoaded = false;
                        JOptionPane.showMessageDialog(this,
                                "Błąd podczas wczytywania pliku CSRRG: " + e.getMessage(),
                                "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (expectedExtension.equals("txt")) {
                    try {
                        ArrayList<Group> groups = TXTParser.parse(selectedFile);

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
                        mainFrame.getGraphPanel().setGraphData(nodeCount, adjacencyList, adjacencyIndices);


                        // Przygotuj dane partycji
                        Map<Integer, Integer> partitionGroups = new HashMap<>();
                        for(Group g : groups){
                            Map<Integer, Integer> tempGroupPartition = g.getPartitionGroups();
                            partitionGroups.putAll(tempGroupPartition);
                            System.out.println(tempGroupPartition.toString());
                        }


                        mainFrame.getGraphPanel().setGraphData(nodeCount, adjacencyList, adjacencyIndices);
                        mainFrame.getGraphPanel().applyPartition(partitionGroups, groups.size());

                        // Dodatkowe zapewnienie odświeżenia
                        mainFrame.getGraphPanel().invalidate();
                        mainFrame.getGraphPanel().revalidate();
                        mainFrame.getGraphPanel().repaint();

                        SwingUtilities.invokeLater(() -> {
                            mainFrame.getGraphPanel().repaint();
                        });


                        JOptionPane.showMessageDialog(this,
                                "Plik TXT wczytany: " + selectedFile.getAbsolutePath(),
                                "Sukces", JOptionPane.INFORMATION_MESSAGE);

                        this.currentGraphFormat="txt";
                        updateButtonStates();
                    } catch (Exception e) {
                        this.isGraphLoaded = false;
                        JOptionPane.showMessageDialog(this,
                                "Błąd podczas wczytywania pliku TXT: " + e.getMessage(),
                                "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (expectedExtension.equals("bin")) {
                    try {
                        var groups = com.example.utils.BINParser.parse(selectedFile);

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
                        mainFrame.getGraphPanel().setGraphData(nodeCount, adjacencyList, adjacencyIndices);

                        // Przygotuj dane partycji
                        Map<Integer, Integer> partitionGroups = new HashMap<>();
                        for(Group g : groups){
                            Map<Integer, Integer> tempGroupPartition = g.getPartitionGroups();
                            partitionGroups.putAll(tempGroupPartition);
                            System.out.println(tempGroupPartition.toString());
                        }


                        mainFrame.getGraphPanel().setGraphData(nodeCount, adjacencyList, adjacencyIndices);
                        mainFrame.getGraphPanel().applyPartition(partitionGroups, groups.size());

                        // Dodatkowe zapewnienie odświeżenia
                        mainFrame.getGraphPanel().invalidate();
                        mainFrame.getGraphPanel().revalidate();
                        mainFrame.getGraphPanel().repaint();

                        SwingUtilities.invokeLater(() -> {
                            mainFrame.getGraphPanel().repaint();
                        });


                        JOptionPane.showMessageDialog(this,
                                "Plik BIN wczytany: " + selectedFile.getAbsolutePath(),
                                "Sukces", JOptionPane.INFORMATION_MESSAGE);
                        mainFrame.getGraphPanel().repaint();
                        this.currentGraphFormat="bin";
                        updateButtonStates();
                    } catch (Exception e) {
                        this.isGraphLoaded = false;
                        JOptionPane.showMessageDialog(this,
                                "Błąd podczas wczytywania pliku BIN: " + e.getMessage(),
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

//    private void saveFile(String expectedExtension) {
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setDialogTitle("Zapisz plik jako *." + expectedExtension);
//        fileChooser.setAcceptAllFileFilterUsed(false);
//        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
//                "."+expectedExtension, expectedExtension));
//
//        int result = fileChooser.showSaveDialog(this);
//        if (result == JFileChooser.APPROVE_OPTION) {
//            File selectedFile = fileChooser.getSelectedFile();
//            String path = selectedFile.getAbsolutePath();
//
//            // Dodaj rozszerzenie, jeśli nie zostało podane
//            if (!path.toLowerCase().endsWith("." + expectedExtension)) {
//                selectedFile = new File(path + "." + expectedExtension);
//            }
//
//            // TODO: tutaj zapisz dane do selectedFile
//            JOptionPane.showMessageDialog(this,
//                    "Zapisano do pliku: " + selectedFile.getAbsolutePath(),
//                    "Zapisano", JOptionPane.INFORMATION_MESSAGE);
//        }
//    }
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

        // Sprawdź, czy graf jest załadowany
        if (!isGraphLoaded) {
            JOptionPane.showMessageDialog(this,
                    "Brak wczytanego grafu do zapisania!",
                    "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Pobierz dane grafu
            int vertexCount = currentVertexCount;
            List<Integer> adjacencyList = currentAdjacencyList;
            List<Integer> adjacencyIndices = currentAdjacencyIndices;

            // Pobierz informacje o podziale grafu (jeśli istnieje)
            Map<Integer, Integer> partitionData = mainFrame.getGraphPanel().getPartitionData();
            boolean hasPartition = !partitionData.isEmpty();

            // Utwórz macierz sąsiedztwa dla formatu TXT
            if (expectedExtension.equals("txt")) {
                FileSaver.saveTxtFormat(selectedFile, vertexCount, adjacencyList, adjacencyIndices, partitionData, hasPartition);
            } else if (expectedExtension.equals("bin")) {
                FileSaver.saveBinFormat(selectedFile, vertexCount, adjacencyList, adjacencyIndices, partitionData, hasPartition);
            }

            JOptionPane.showMessageDialog(this,
                    "Zapisano do pliku: " + selectedFile.getAbsolutePath(),
                    "Zapisano", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Błąd podczas zapisywania pliku: " + e.getMessage(),
                    "Błąd", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}


    private void switchView(String viewName) {
        CardLayout cl = (CardLayout) cards.getLayout();
        cl.show(cards, viewName);
    }
    private void updateButtonStates() {
        // Wyłącz przycisk, jeśli format to TXT lub BIN
        if ("txt".equals(currentGraphFormat) || "bin".equals(currentGraphFormat)) {
            mainFrame.getDivideButton().setEnabled(false);

            // Opcjonalnie: dodaj podpowiedź wyjaśniającą dlaczego przycisk jest nieaktywny
            mainFrame.getDivideButton().setToolTipText("Podział grafu nie jest dostępny dla formatu " + currentGraphFormat.toUpperCase());
        } else {
            mainFrame.getDivideButton().setEnabled(true);
            mainFrame.getDivideButton().setToolTipText(null); // Usuń podpowiedź jeśli była wcześniej ustawiona
        }
    }

    // Metody pomocnicze dla dostępu do danych grafu
    public void setIsGraphLoaded(boolean bool) {
        isGraphLoaded = bool;
    }

    public boolean isGraphLoaded() {
        return isGraphLoaded;
    }

    public int getCurrentVertexCount() {
        return currentVertexCount;
    }

    public List<Integer> getCurrentAdjacencyList() {
        return currentAdjacencyList;
    }

    public List<Integer> getCurrentAdjacencyIndices() {
        return currentAdjacencyIndices;
    }
}