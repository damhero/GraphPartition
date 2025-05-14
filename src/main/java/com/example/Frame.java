package com.example;

import javax.swing.*;
import javax.swing.JFrame;

public class Frame extends JFrame {
     Frame() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Aplikacja do podziału grafu");

            MainFrame form = new MainFrame(); // zawiera JPanel
            frame.setContentPane(form.getMainPanel());
            //Main menu
            JMenuBar menuBar = new JMenuBar();

            //Main menu submenus
            JMenu menuFile = new JMenu("Plik");
            JMenu menuUpload = new JMenu("Wczytaj");
            JMenu menuWrite = new JMenu("Zapisz");
            JMenu menuEdit = new JMenu("Edycja");
            JMenu menuTools = new JMenu("Narzędzia");
            JMenu menuHelp = new JMenu("Pomoc");

            //Upload file menu items
            JMenuItem itemUploadText = new JMenuItem("Tekstowy");
            JMenuItem itemUploadBin = new JMenuItem("Binarny");

            //Write file menu items
            JMenuItem itemWriteText = new JMenuItem("Tekstowy");
            JMenuItem itemWriteBin = new JMenuItem("Binarny");

            //Edit menu items
            JMenuItem itemVizSettings = new JMenuItem("Ustawienia wizualizacji");
            JMenuItem itemPreferences = new JMenuItem("Preferencje");

            //Tools menu items
            JMenuItem itemPartition = new JMenuItem("Wykonaj podział grafu");
            JMenuItem itemAnalize = new JMenuItem("Analizuj wynik podziału");

            //Help menu items
            JMenuItem itemManual = new JMenuItem("Instrukcja obsługi");
            JMenuItem itemAbout = new JMenuItem("O programie");

            //Listeners
            itemUploadText.addActionListener(e -> System.out.println("Wczytaj tekstowy"));
            itemUploadBin.addActionListener(e -> System.out.println("Wczytaj binarny"));
            itemWriteText.addActionListener(e -> System.out.println("Zapisz tekstowy"));
            itemWriteBin.addActionListener(e -> System.out.println("Zapisz binarny"));
            itemVizSettings.addActionListener(e -> {
                System.out.println("Ustawienia wizualizacji");
            });
            itemPreferences.addActionListener(e -> System.out.println("preferencje"));
            itemPartition.addActionListener(e -> System.out.println("Podział"));
            itemAnalize.addActionListener(e -> System.out.println("Analizuj wynik"));
            itemManual.addActionListener(e -> System.out.println("Instrukcja"));
            itemAbout.addActionListener(e -> System.out.println("O programie"));

            //Uploads
            menuUpload.add(itemUploadText);
            menuUpload.add(itemUploadBin);

            menuWrite.add(itemWriteText);
            menuWrite.add(itemWriteBin);

            menuFile.add(menuUpload);
            menuFile.add(menuWrite);

            menuEdit.add(itemVizSettings);
            menuEdit.add(itemPreferences);

            menuTools.add(itemPartition);
            menuTools.add(itemAnalize);

            menuHelp.add(itemManual);
            menuHelp.add(itemAbout);

            menuBar.add(menuFile);
            menuBar.add(menuEdit);
            menuBar.add(menuTools);
            menuBar.add(menuHelp);

            frame.setJMenuBar(menuBar);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
