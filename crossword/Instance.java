package crossword;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Instance {

    private final Scanner sc = new Scanner(System.in);
    private List<String> bag = new ArrayList<>();
    private List<String> tray = new ArrayList<>();
    private String[][] field = new String[15][15];
    public void run(String s) {
        System.out.println("Welcome to Crossword Scrabble\n");

        fill();

        print();

        read(new File(s));

        process();
    }

    private void print() {
        System.out.println("  1 2 3 4 5 6 7 8 9 A B C D E F");

        for (int i = 0; i < 15; i++) {
            System.out.printf("%s %s\n", Integer.toHexString(i + 1).toUpperCase(), String.join(" ", field[i]));
        }
    }

    private void fill() {
        for (String[] strings : field) {
            Arrays.fill(strings, "#");
        }

        clear();
    }

    private void clear() {
        for (int i = 2; i < 11; i++) {
            field[3][i] = " ";
        }

        for (int i = 1; i < 9; i++) {
            field[i][9] = " ";
        }
    }

    private void process() {
        while (true){
            ask();

            String com = sc.nextLine().toLowerCase().trim();

            if (!com.matches("[a-c]|((place|a) [a-z_0-9\\s]+|pick|exit)")) {
                System.out.println("\nInvalid Command.");
                continue;
            }

            useCom(com.split(" "));
        }
    }

    private void ask() {
        System.out.println("""
                
                What would you like to do?

                A. Place

                B. Pick

                C. Exit
                """);
    }

    private void useCom(String[] com) {
        switch (com[0]) {
            case "a", "place" -> place(com);
            case "b", "pick" -> pick();
            case "c", "exit" -> exit();
        }
    }

    private void place(String[] com) {
        List<String> list = new ArrayList<>(List.of(com[1].toUpperCase().split("")));



        if (tray.size() == 0) {
            System.out.println("Your tray is empty.");
            return;
        } else if (checkIfNot(list)){
            System.out.println("");
            return;
        }


        int row = Integer.parseInt(com[2]) - 1;
        int col = Integer.valueOf(com[3], 16) - 1;

        switch (com[4]) {
            case "dwn" -> {
                for (int i = row; i < list.size(); i++) {
                    field[i][col] = list.remove(0);
                }
            }
            case "acr" -> {
                for (int i = col; i < list.size(); i++) {
                    field[row][i] = list.remove(0);
                }
            }
            default -> System.out.println("Invalid");
        }
    }

    private boolean checkIfNot(List<String> list) {
        return list.stream().filter(e -> !tray.contains(e)).toList().size() != 0;
    }

    private void shuffle() {
        if (bag.size() == 0) {
            System.out.println("\nThe bag is empty.");
            printBag();
            printTray();
        } else {
            Collections.shuffle(bag);
            if (tray.size() != 0) printTray();
            printBag();
        }
    }

    private void pick() {
//        if (bag.size() == 0) {
//            System.out.println("\nNo more tiles to pick.");
//        }
//
//        for (int i = 0; i < 7; i++) {
//            if (bag.size() == 0) {
//                break;
//            }
//
//            tray.add(bag.remove(new Random().nextInt(bag.size())));
//        }

        List<String> list = List.of("DSCBLWORRBAOSE".split(""));

        tray.addAll(list);

        bag.removeAll(list);

        printAll();
    }

    private void printAll() {
        print();
        printTray();
        printBag();
    }

    private void exit() {
        System.out.println("\nBye.");
        System.exit(0);
    }

    private void read(File file) {
        if (file.exists() && file.isFile()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))){
                HashMap<Character, Integer> map = new HashMap<>();

                reader.lines().forEach(e -> map.put(e.charAt(0), Integer.parseInt(e.substring(2))));

                fillBag(map);
            } catch (IOException ignored) {}
        } else {
            System.out.println("File not found!");
            exit();
        }
    }

    private void fillBag(HashMap<Character, Integer> map) {
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<Character, Integer> entry : map.entrySet()) {
            builder.append(entry.getKey().toString().repeat(entry.getValue()));
        }

        bag.addAll(Arrays.stream(builder.toString().split("")).toList());

        printBag();
    }

    private void printBag() {
        System.out.printf("\nBag: %s\n", String.join("", bag));
    }

    private void printTray() {
        System.out.printf("\nTray: %s\n", String.join("", tray));
    }
}
