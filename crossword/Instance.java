package crossword;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Instance {

    private final Scanner sc = new Scanner(System.in);
    private List<String> tray = new ArrayList<>();
    private String[][] field = new String[15][15];
    private ArrayList<String> fileLines = new ArrayList<>();

    public void run(String s) {
        System.out.println("Welcome to Crossword Scrabble");

        fill();

        read(new File(s));
        chRan();

        process();
    }

    private void print() {
        System.out.println("\n  1 2 3 4 5 6 7 8 9 A B C D E F");

        for (int i = 0; i < 15; i++) {
            System.out.printf("%s %s\n", Integer.toHexString(i + 1).toUpperCase(), String.join(" ", field[i]));
        }
    }

    private void fill() {
        for (String[] strings : field) {
            Arrays.fill(strings, "#");
        }
    }

    private void process() {
        boolean go = true;

        while (go){
            ask();

            switch (sc.nextLine().toLowerCase().trim()) {
                case "a", "shuffle" -> go = chRan();
                case "b", "exit" -> exit(0);

                default -> System.out.println("Invalid command");
            }
        }

        System.out.print("\nNo more puzzles. ");
        exit(1);
    }

    private boolean chRan() {
        if (fileLines.isEmpty()) return false;
        Random random = new Random();

        String line = fileLines.remove(random.nextInt(fileLines.size()));

        tray.clear();
        tray.addAll(List.of(line.substring(0, line.indexOf(" ")).toUpperCase().split("")));

        ArrayList<String> list = new ArrayList<>(List.of(split(line)));

        draw(list);
        return true;
    }

    private void draw(ArrayList<String> list) {
        fill();

        list.forEach(e -> place(e.split(" ")));

        printAll();
    }

    private String[] split(String line) {
        return line.substring(line.indexOf(" ") + 1).replaceAll("((?:\\w+\\s+){4})", "$1###").split("###");
    }


    private void ask() {
        System.out.println("""
                
                What would you like to do?

                A. Shuffle

                B. Exit
                """);
    }

    private void place(String[] com) {
        List<String> list;
        RuntimeException ex = new RuntimeException("Invalid position. Position out of range.");

        try {

            String word = com[0].toUpperCase();
            int row = Integer.parseInt(com[1]) - 1;
            int col = Integer.parseInt(com[2]) - 1;
            String dir = com[3].trim().toLowerCase();

            list = List.of(word.split(""));


            if (row > 14 || col > 14 || row < 0 || col < 0) {
                throw ex;
            }

            if (!List.of("dwn", "down", "acr", "across").contains(dir)) {
                throw new RuntimeException("Invalid direction.");
            }

            //Invalid position
            switch (dir) {
                case "dwn", "down" -> {
                    for (int i = 0; i < list.size(); i++) {
                        field[i + row][col] = list.get(i);
                    }
                }
                case "acr", "across" -> {
                    for (int i = 0; i < list.size(); i++) {
                        field[row][i + col] = list.get(i);
                    }
                }
                //Invalid direction
                default -> throw new RuntimeException("Invalid direction.");
            }

        } catch (RuntimeException ignored) {
        }
    }

    private void printAll() {
        print();
        printTray();
    }

    private void exit(int a) {
        System.out.println((a == 0 ? "\n" : "") + "Bye.");
        System.exit(0);
    }

    private void read(File file) {
        if (file.exists() && file.isFile()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))){
                reader.lines().forEach(fileLines::add);
            } catch (IOException ignored) {}
        } else {
            System.out.println("File not found!");
            exit(1);
        }
    }

    private void printTray() {
        System.out.printf("\nTray: %s\n", String.join("", tray));
    }
}
