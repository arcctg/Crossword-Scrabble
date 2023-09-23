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
        System.out.println("Welcome to Crossword Scrabble");

        fill();

        print();

        read(new File(s));

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
            default -> System.out.println("Invalid command");
        }
    }

    private void place(String[] com) {
        String err = "";

        try {
            List<String> list = new ArrayList<>(List.of(com[1].toUpperCase().split("")));
            RuntimeException ex = new RuntimeException("Invalid position. Position out of range.");
            List<String> invalid;

            if (tray.size() == 0) {
                throw new RuntimeException("Your tray is empty.");
            } else if ((invalid = checkIfNot(list)).size() != 0){
                throw new RuntimeException(String.format("Invalid letter. Letter %s is not in the tray.\n", invalid.get(0)));
            }


            int row = Integer.valueOf(com[2], 16) - 1;
            int col = Integer.valueOf(com[3], 16) - 1;

            if (row > 14 || col > 14 || row < 0 || col < 0) {
                throw ex;
            }

            switch (com[4]) {
                case "dwn", "down" -> {
                    for (int i = 0; i < list.size(); i++) {
                        if (field[i + row][col].equals("#")) {
                            throw ex;
                        }

                        field[i + row][col] = list.get(i);
                    }
                }
                case "acr", "across" -> {
                    for (int i = 0; i < list.size(); i++) {
                        if (field[row][i + col].equals("#")) {
                            throw ex;
                        }

                        field[row][i + col] = list.get(i);
                    }
                }
                default -> throw new RuntimeException("Invalid direction.");
            }
        } catch (NumberFormatException e) {
            err = "Invalid command. Use 'Place Word row(Hexadecimal) column(Hexadecimal) DIRECTION'.";
        } catch (IndexOutOfBoundsException ignored) {

        } catch (RuntimeException e) {
            err = e.getMessage();
        }

        if (err.length() != 0) printAll("\n" + err);
        else printAll();
    }

    private List<String> checkIfNot(List<String> list) {
        //return list.stream().filter(e -> !tray.contains(e)).toList().size() != 0;
        return list.stream().filter(e -> Collections.frequency(list, e) > Collections.frequency(tray, e)).toList();
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

    private void printAll(String err) {
        print();
        System.out.println(err);
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
