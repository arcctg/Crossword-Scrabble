package crossword;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Puzzle {
    private String letter;
    private ArrayList<Word> words;

    @JsonCreator
    public Puzzle(@JsonProperty("letter") String letter, @JsonProperty("words") ArrayList<Word> words) {
        this.letter = letter;
        this.words = words;
    }

    public String getLetter() {
        return letter;
    }

    public ArrayList<Word> getWords() {
        return words;
    }
}
