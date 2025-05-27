package com.example.utils;

import com.example.model.Group;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

public class BINParser {

    public static ArrayList<Group> parse(File binFile) {
        String binPath = binFile.getAbsolutePath();
        String txtPath = binPath.substring(0, binPath.lastIndexOf(".")) + ".txt";
        File txtFile = new File(txtPath);
        return TXTParser.parse(txtFile);
    }
    
    
    public static ArrayList<Group> parseReal(File binFile) {
        ArrayList<Group> partedGraph = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(binFile);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            // Najpierw przeczytaj macierz sąsiedztwa
            int vertexCount = readMatrix(bis);

            // Następnie przeczytaj grupy
            readGroups(bis, partedGraph);

        } catch (Exception e) {
            System.err.println("[!] Błąd podczas parsowania pliku binarnego: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        return partedGraph;
    }

    private static int readMatrix(BufferedInputStream bis) throws IOException {
        int vertexCount = 0;
        boolean matrixEnd = false;

        while (!matrixEnd) {
            try {
                // Czytaj int (4 bajty) - wartość 0 lub 1
                byte[] intBytes = new byte[4];
                int bytesRead = bis.read(intBytes);
                if (bytesRead != 4) break;

                int value = ByteBuffer.wrap(intBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();

                if (value == 1) {
                    vertexCount++;
                }

                // Czytaj następny bajt (spacja lub newline)
                int nextByte = bis.read();
                if (nextByte == -1) break;

                char nextChar = (char) nextByte;

                // Jeśli natrafimy na dwa newline'y pod rząd, kończymy macierz
                if (nextChar == '\n') {
                    // Sprawdź czy następny bajt to też newline
                    bis.mark(1);
                    int peekByte = bis.read();
                    if (peekByte == '\n') {
                        matrixEnd = true;
                    } else {
                        bis.reset();
                    }
                }

            } catch (Exception e) {
                break;
            }
        }

        return vertexCount;
    }

    private static void readGroups(BufferedInputStream bis, ArrayList<Group> partedGraph) throws IOException {
        Group currentGroup = null;

        while (bis.available() > 0) {
            try {
                // Sprawdź czy to początek nowej grupy
                bis.mark(10); // Mark pozycję na wypadek cofnięcia

                // Spróbuj przeczytać "grupa "
                byte[] groupBytes = new byte[6];
                int bytesRead = bis.read(groupBytes);

                if (bytesRead == 6) {
                    String groupStr = new String(groupBytes);
                    if (groupStr.equals("grupa ")) {
                        // Przeczytaj numer grupy (ale w Twoim kodzie C nie widzę zapisu numeru...)
                        // Zakładając że numer grupy to kolejne cyfry do newline
                        StringBuilder groupNumStr = new StringBuilder();
                        int b;
                        while ((b = bis.read()) != -1 && b != '\n') {
                            if (Character.isDigit(b)) {
                                groupNumStr.append((char) b);
                            }
                        }

                        // Zapisz poprzednią grupę jeśli istnieje
                        if (currentGroup != null) {
                            partedGraph.add(currentGroup.deepCopy());
                        }

                        // Utwórz nową grupę
                        int groupNum = groupNumStr.length() > 0 ?
                                Integer.parseInt(groupNumStr.toString()) : partedGraph.size();
                        currentGroup = new Group(groupNum);
                        continue;
                    }
                }

                // Jeśli to nie grupa, cofnij i spróbuj przeczytać jako krawędź
                bis.reset();

                // Próbuj przeczytać krawędź: int-char-int-newline
                byte[] firstIntBytes = new byte[4];
                bytesRead = bis.read(firstIntBytes);
                if (bytesRead != 4) break;

                int firstVertex = ByteBuffer.wrap(firstIntBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();

                // Przeczytaj separator (powinien być '-')
                int separator = bis.read();
                if (separator != '-') {
                    // Jeśli to nie separator, może to newline kończący grupę
                    if (separator == '\n') {
                        // Sprawdź czy następny bajt to też newline (koniec grupy)
                        bis.mark(1);
                        int peekByte = bis.read();
                        if (peekByte == '\n' || peekByte == -1) {
                            // Koniec grupy
                            if (currentGroup != null) {
                                partedGraph.add(currentGroup.deepCopy());
                                currentGroup = null;
                            }
                        } else {
                            bis.reset();
                        }
                    }
                    continue;
                }

                // Przeczytaj drugi wierzchołek
                byte[] secondIntBytes = new byte[4];
                bytesRead = bis.read(secondIntBytes);
                if (bytesRead != 4) break;

                int secondVertex = ByteBuffer.wrap(secondIntBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();

                // Przeczytaj newline
                int newline = bis.read();

                // Dodaj krawędź do aktualnej grupy
                if (currentGroup != null) {
                    currentGroup.addVertex(firstVertex);
                    currentGroup.addVertex(secondVertex);
                    currentGroup.addAdjacencyPair(new ArrayList<>(Arrays.asList(firstVertex, secondVertex)));
                }

            } catch (Exception e) {
                System.err.println("Błąd podczas czytania grup: " + e.getMessage());
                break;
            }
        }

        // Dodaj ostatnią grupę jeśli istnieje
        if (currentGroup != null) {
            partedGraph.add(currentGroup.deepCopy());
        }
    }

    // Metoda pomocnicza do debugowania - wyświetla zawartość pliku jako hex
    public static void debugPrintFile(File binFile) {
        try (FileInputStream fis = new FileInputStream(binFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            int offset = 0;

            while ((bytesRead = fis.read(buffer)) != -1) {
                for (int i = 0; i < bytesRead; i++) {
                    if (offset % 16 == 0) {
                        System.out.printf("%08X: ", offset);
                    }

                    System.out.printf("%02X ", buffer[i] & 0xFF);

                    if ((offset + 1) % 16 == 0) {
                        System.out.print(" | ");
                        for (int j = i - 15; j <= i; j++) {
                            char c = (char) (buffer[j] & 0xFF);
                            System.out.print(Character.isISOControl(c) ? '.' : c);
                        }
                        System.out.println();
                    }
                    offset++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}