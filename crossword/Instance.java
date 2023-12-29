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

        //clear();
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
        //Collections.shuffle(tray);

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

    private void useCom(String[] com) {
        switch (com[0]) {
            case "a", "place" -> place(com);
            case "b", "pick" -> pick();
            case "c", "exit" -> exit(1);
            default -> System.out.println("Invalid command");
        }
    }

    private void place(String[] com) {
        String err = "";
        List<String> list;
        String errWord = "Incorrect word";
        RuntimeException ex = new RuntimeException("Invalid position. Position out of range.");
        RuntimeException errHex = new RuntimeException("Invalid command. Use 'Place Word row(Hexadecimal) column(Hexadecimal) DIRECTION'.");
        List<String> invalid;

        try {
            //Your tray is empty.
//            if (tray.isEmpty()) {
//                throw new RuntimeException("Your tray is empty.");
//            }

            //Invalid command
//            if (com.length < 5 || !String.join(" ", com).toLowerCase().matches("(place|a) .+ .*\\s.*\\s.*")) { //(place|a) .+ [a-f0-9]\s[a-f0-9]\s[a-z]+  (place|a) .+ [a-z0-9A-Z]\s[a-z0-9A-Z]
//                throw errHex;
//            }

            String word = com[0].toUpperCase();
            int row = Integer.parseInt(com[1]) - 1;
            int col = Integer.parseInt(com[2]) - 1;
            String dir = com[3].trim().toLowerCase();

            list = List.of(word.split(""));

            //Invalid position

//            int row = Integer.valueOf(com[2], 16) - 1; Hexadecimal to decimal
//            int col = Integer.valueOf(com[3], 16) - 1;


            if (row > 14 || col > 14 || row < 0 || col < 0) {
                throw ex;
            }

            if (!List.of("dwn", "down", "acr", "across").contains(dir)) {
                throw new RuntimeException("Invalid direction.");
            }

            //Invalid letter
//            HashSet<String> set = new HashSet<>(List.of(word.split("")));
//            boolean b = word.length() > 9 && (word.contains("CROSSWORD") || word.contains("SCRABBLE"));
//            boolean c = (set.containsAll(List.of("CROSSWORD".split(""))) || set.containsAll(List.of("SCRABBLE".split("")))) && word.length() > 9;
//
//            if (!(invalid = checkIfNot(list)).isEmpty()){
//                if (!Character.isAlphabetic(invalid.get(0).charAt(0))) {
//                    throw new RuntimeException(String.format("Invalid letter. Letter %s is not in the tray.", invalid.get(0)));
//                } else {
//                    if (c) {
//                        throw new RuntimeException(errWord + ". The word is too long.");
//                    } else if (!word.equals("CROSSWORD") && !word.equals("SCRABBLE")) {
//                        throw new RuntimeException(errWord + ", please try again." + "Incorrect word. The word is too long.");
//                    }
//                }
//            }
//
//            //Incorrect word
//            if (c) {
//                throw new RuntimeException(errWord + ". The word is too long.");
//            } else if (!word.equals("CROSSWORD") && !word.equals("SCRABBLE")) {
//                throw new RuntimeException(errWord + ", please try again." + "Incorrect word. The word is too long.");
//            }
            //if (word.length() > 9) throw new RuntimeException(errWord + ", please try again.");

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

        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            err = errHex.getMessage();
        } catch (RuntimeException e) {
            err = e.getMessage();
        }
    }

    private List<String> checkIfNot(List<String> list) {
        //return list.stream().filter(e -> !tray.contains(e)).toList().size() != 0;
        return list.stream().filter(e -> Collections.frequency(list, e) > Collections.frequency(tray, e)).toList();
    }

    private void shuffle() {
        if (bag.isEmpty()) {
            System.out.println("\nThe bag is empty.");
            printBag();
            printTray();
        } else {
            Collections.shuffle(bag);
            if (!tray.isEmpty()) printTray();
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

        if (!tray.containsAll(list)) {
            tray.addAll(list);

            list.forEach(e -> bag.remove(e));
        }



//        list.forEach(e -> {
//            if (!bag.remove(e)) bag.remove("*");
//        });

        //tray.addAll(list.stream().filter(e -> bag.remove(e)).toList());
        //bag.remove("A");

        printAll();
    }

    private void printAll() {
        print();
        printTray();
        //printBag();
    }

    private void printAll(String err) {
        print();
        System.out.println(err);
        printTray();
        //printBag();
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
