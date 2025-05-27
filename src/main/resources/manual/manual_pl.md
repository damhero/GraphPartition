# Instrukcja obsługi aplikacji do podziału grafów

## Spis treści
1. [Wprowadzenie](#wprowadzenie)
2. [Wczytywanie grafu](#wczytywanie-grafu)
3. [Podział grafu](#podział-grafu)
4. [Analiza wyników](#analiza-wyników)
5. [Preferencje](#preferencje)
6. [Zapisywanie wyników](#zapisywanie-wyników)

## Wprowadzenie

Aplikacja służy do podziału grafów na podgrafy o podobnym rozmiarze przy zachowaniu odpowiedniego marginesu. Program umożliwia wczytanie grafu z pliku w formatach CSRRG lub TXT, a następnie przeprowadzenie operacji podziału i analizy wyników.

## Wczytywanie grafu

Aby wczytać graf do aplikacji:

1. Wybierz opcję `Plik → Wczytaj` z menu głównego
2. Wybierz odpowiedni format pliku:
    - CSRRG - specjalistyczny format do reprezentacji grafów
    - TXT - prosty format tekstowy
3. W oknie dialogowym znajdź i wybierz plik grafu
4. Po poprawnym wczytaniu grafu, jego wizualizacja pojawi się na ekranie

Format pliku TXT powinien zawierać pary wierzchołków połączonych krawędzią, po jednej parze w każdym wierszu.

## Podział grafu

Po wczytaniu grafu możesz wykonać jego podział:

1. W głównym widoku ustaw parametry:
    - Liczba podgrafów - na ile części ma zostać podzielony graf
    - Margines - maksymalny dopuszczalny współczynnik nierównomierności podziału
2. Kliknij przycisk `Podziel graf`
3. Poczekaj na zakończenie operacji podziału
4. Po zakończeniu podziału, jego wynik zostanie wyświetlony graficznie

## Analiza wyników

Aby przeanalizować wyniki podziału:

1. Wybierz opcję `Narzędzia → Analizuj` z menu głównego
2. W oknie dialogowym zostaną wyświetlone statystyki podziału, takie jak:
    - Liczba wierzchołków w każdym podgrafie
    - Liczba krawędzi przecinających granice podgrafów
    - Współczynnik jakości podziału
3. Wyniki analizy możesz zapisać do pliku

## Preferencje

Aby dostosować ustawienia aplikacji:

1. Wybierz opcję `Edycja → Preferencje` z menu głównego
2. W oknie preferencji możesz zmienić:
    - Język interfejsu
    - Motyw (jasny/ciemny)
    - Rozdzielczość okna aplikacji
3. Zatwierdź zmiany klikając przycisk `Powrót`

## Zapisywanie wyników

Aby zapisać wyniki pracy:

1. Wybierz opcję `Plik → Zapisz` z menu głównego
2. Wybierz format pliku:
    - TXT - zapis tekstowy
    - BIN - format binarny
3. Wskaż lokalizację i nazwę pliku
4. Kliknij przycisk `Zapisz`

---

W razie pytań lub problemów prosimy o kontakt z zespołem wsparcia.