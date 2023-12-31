package crossword;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Puzzles {
    private ArrayList<Puzzle> puzzles;

    @JsonCreator
    public Puzzles(@JsonProperty("puzzles") ArrayList<Puzzle> puzzles) {
        this.puzzles = puzzles;
    }

    public ArrayList<Puzzle> getPuzzles() {
        return puzzles;
    }
}
