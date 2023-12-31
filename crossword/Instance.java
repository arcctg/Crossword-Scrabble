package crossword;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Instance {

    private final Scanner sc = new Scanner(System.in);
    private List<String> tray = new ArrayList<>();
    private String[][] field = new String[15][15];
    private ArrayList<String> fileLines = new ArrayList<>();
    private ArrayList<Puzzle> puzzles;
    private ArrayList<Word> words = new ArrayList<>();
    private int countW = 0;
    private ObjectMapper mapper = new ObjectMapper();
    private RuntimeException ultimateEx;

    public void run(String s) {
        System.out.println("Welcome to Crossword Scrabble");

        fill();

        read(new File(s));
        chRan(true);

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
        while (true){
            ask();

            String com = sc.nextLine().toLowerCase().trim();

            if (!com.matches("[a-d]|((place|a|hint|b) .+|new\\s+puzzle|exit)")) {
                //System.out.println("\nInvalid command. Use 'Place Word row(Hexadecimal) column(Hexadecimal) DIRECTION");
                printAll("Invalid command. Use 'Place Word row(Hexadecimal) column(Hexadecimal) DIRECTION'.");
                continue;
            }

            useCom(com);
        }
//
//        System.out.print("\nNo more puzzles. ");
//        exit(1);
    }

    private void chRan(boolean print) {
        if (puzzles.isEmpty()) {
            System.out.print("No more puzzles. ");
            exit(1);
        }
        Random random = new Random();

        Puzzle puzzle = puzzles.remove(random.nextInt(puzzles.size()));
        words = new ArrayList<>(puzzle.getWords());
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            System.out.println("err thread");;
//        }
        countW = 0;

        tray.clear();
        tray.addAll(List.of(puzzle.getLetter().toUpperCase().split("")));

        draw1(print, puzzle.getWords());
    }

    private void draw1(boolean print, ArrayList<Word> wordss) {
        fill();

        for (Word e : wordss) {
            place(e.toString().split(" "), true);
        }

        if (print) printAll();
    }

    private void draw(ArrayList<String> list) {
        fill();

        list.forEach(e -> place(e.split(" "), true));

        printAll();
    }

    private String[] split(String line) {
        return line.substring(line.indexOf(" ") + 1).replaceAll("((?:\\w+\\s+){4})", "$1###").split("###");
    }


    private void ask() {
        System.out.println("""
                                
                What would you like to do?

                A. Place

                B. Hint
                            
                C. New Puzzle
                              
                D. Exit
                """);
    }

    private void useCom(String comLine) {
        //String[] com = comLine.substring(comLine.indexOf(" ") + 1).split(" ");
        ArrayList<String> com = new ArrayList<>(List.of(comLine.split(" ")));

        switch (com.remove(0)) {
            case "a", "place" -> place(com.toArray(new String[0]), false);
            case "b", "hint" -> hint(com.toArray(new String[0]));
            case "c" -> chRan(true);
            case "new" -> {
                if (com.get(0).equalsIgnoreCase("puzzle")) chRan(true);
                else System.out.println("\nInvalid command");
            }
            case "d", "exit" -> exit(0);

            default -> System.out.println("\nInvalid command");
        }

//        switch (com[0]) {
//            case "a", "place" -> place(com);
//            case "b", "pick" -> pick();
//            case "c", "exit" -> exit();
//            default -> System.out.println("Invalid command");
//        }
    }

    private void hint(String[] com) {
        String err = "";
        String hint = "";

        try {
            int row = Integer.valueOf(com[0], 16);
            int col = Integer.valueOf(com[1], 16);
            String dir = com[2].trim().toLowerCase();

            hint = getHintByPosDir(row + " " + col, dir);

            if (hint == null) throw new RuntimeException("Wrong position or direction.");
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            err = ultimateEx.getMessage() + "Invalid command. Use 'Place Word row(Hexadecimal) column(Hexadecimal) DIRECTION'.";
        } catch (RuntimeException e) {
            err = e.getMessage();
        }

        System.out.println("\n" + hint);
        if (!err.isEmpty()) printAll("\nerr " + err);
        else printAll();
    }

    private String getHintByPosDir(String s, String dir) {
        for (Word e : words) {
            if (e.getPosition().equals(s) && e.getDirection().equalsIgnoreCase(dir)) {
                return e.getHint();
            }
        }
        return null;
    }

    private void place(String[] com, boolean prep) {
        String err = "";
        List<String> list;
        List<String> invalid;
        List<String> errs = new ArrayList<>(List.of("Invalid position or direction."
        , "Invalid letter. Letter X is not in the tray.", "Wrong word, please try again.", "Incorrect word. The word is too long."
        , "The word is shorter than expected.", "Invalid direction.", "Invalid position. Position out of range."));
        Collections.shuffle(errs);
        ultimateEx = new RuntimeException(String.join(" ", errs.toArray(new String[0])));
        RuntimeException ex = new RuntimeException("Invalid position or direction.");
        RuntimeException errHex = new RuntimeException("Invalid command. Use 'Place Word row(Hexadecimal) column(Hexadecimal) DIRECTION'.");

        try {
            if (!String.join(" ", com).matches("\\w+\\s+[\\da-f]+\\s+[\\da-f]+\\s+\\w+") && !prep) throw new NumberFormatException();
            String word = com[0].toUpperCase();

//            if (word.equalsIgnoreCase("end")) {
//                words.clear();
//                throw new RuntimeException("End");
//            }

            list = List.of(word.split(""));

            if (!(invalid = checkIfNot(list)).isEmpty() && !Character.isAlphabetic(invalid.get(0).charAt(0))){
                throw new RuntimeException(String.format("Invalid letter. Letter %s is not in the tray.", invalid.get(0)));
            }

            int row = 0;
            int col = 0;

            if (prep) {
                row = Integer.parseInt(com[1]) - 1;
                col = Integer.parseInt(com[2]) - 1;
            } else {
                row = Integer.valueOf(com[1], 16) - 1;
                col = Integer.valueOf(com[2], 16) - 1;
            }


            String dir = com[3].trim().toLowerCase();
            Word rightWord = null;

            if (!prep) {
                rightWord = getWordByPosDir((row + 1) + " " + (col + 1), dir);

                if (word.length() == 1) {
                    throw new RuntimeException("The word is shorter than expected.");
                }

                if (rightWord == null) {
                    //throw new RuntimeException("rightWord == null");
                    throw ex;
                }

                String rWord = rightWord.getWord();

                if (!word.equalsIgnoreCase(rWord)) {
                    if (word.length() == rWord.length()) throw new RuntimeException("Wrong word, please try again.");
                    else throw new RuntimeException(word.length() > rWord.length() ?
                            "Incorrect word. The word is too long." :
                            "The word is shorter than expected.");
                }
            }


            if (row > 14 || col > 14 || row < 0 || col < 0 || !List.of("dwn", "down", "acr", "across").contains(dir)) {
                //throw new RuntimeException("pos or !List.of(\"dwn\", \"down");
                throw ex;
            }



            //Invalid position
            switch (dir) {
                case "dwn", "down" -> {
                    for (int i = 0; i < list.size(); i++) {
                        field[i + row][col] = prep ? " " : list.get(i);
                    }
                }
                case "acr", "across" -> {
                    for (int i = 0; i < list.size(); i++) {
                        field[row][i + col] = prep ? " " : list.get(i);
                    }
                }
                //Invalid direction
                default -> throw new RuntimeException("Invalid direction.");
            }

            if (!prep) {
                //words.remove(rightWord);
                countW++;
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            err = ultimateEx.getMessage() + " " + errHex.getMessage();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("is shorter")) {
                err = e.getMessage();
            } else {
                err = ultimateEx.getMessage() + e.getMessage();
            }
        }

//    } catch (NumberFormatException | IndexOutOfBoundsException e) {
//        err = errHex.getMessage();
//    } catch (RuntimeException e) {
//        err = e.getMessage();
//    }

        if (!prep) {
            if (countW - 1 == words.size()) {
                printAll();
                System.out.println("\n" + "Congratulations! Would you like to load another puzzle? Yes/No.\n");

                String com1 = sc.nextLine().toLowerCase().trim();

                if (!com1.equals("no") && !com1.equals("yes")) {
                    if (!com1.matches("[a-d]|((place|a|hint|b) .+|new\\s+puzzle|exit)")) {
                        System.out.println("\nInvalid Command.");
                        return;
                    }

                    useCom(com1);
                } else {
                    if (com1.equals("yes")) {
                        chRan(true);
                    } else exit(0);

                }
            } else if (!err.isEmpty()) printAll(err);
            else printAll();
        }
    }

    private List<String> checkIfNot(List<String> list) {
        return list.stream().filter(e -> Collections.frequency(list, e) > Collections.frequency(tray, e)).toList();
    }

    private Word getWordByPosDir(String s, String dir) {
        for (Word e : words) {
            if (e.getPosition().equals(s) && e.getDirection().equalsIgnoreCase(dir)) {
                return e;
            }
        }
        return null;
    }

    private void printAll() {
        print();
        printTray();
    }

    private void printAll(String err) {
        print();
        System.out.println("\n" + err);
        printTray();
    }

    private void exit(int a) {
        System.out.println((a == 0 ? "\n" : "") + "Bye.");
        System.exit(0);
    }

    private void read(File file) {
        if (file.exists() && file.isFile()) {
            try {
                puzzles = mapper.readValue(file, Puzzles.class).getPuzzles();
            } catch (IOException ignored) {}

//            try (BufferedReader reader = new BufferedReader(new FileReader(file))){
//                Puzzles puzzles = mapper.read;
//                for (Puzzle puzzle : puzzles.getPuzzles()) {
//                    System.out.println(puzzle.getLetter());
//                }
//                //reader.lines().forEach(fileLines::add);
//            } catch (IOException ignored) {}
        } else {
            System.out.println("File not found!");
            exit(0);
        }
    }

    private void printTray() {
        System.out.printf("\nTray: %s\n", String.join("", tray));
    }
}
