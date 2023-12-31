package crossword;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Word {
    private String word;
    private String position;
    private String direction;
    private String hint;

    @JsonCreator
    public Word(@JsonProperty("word") String word, @JsonProperty("position") String position,
                @JsonProperty("direction") String direction, @JsonProperty("hint") String hint) {
        this.word = word;
        this.position = position;
        this.direction = direction;
        this.hint = hint;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s", word, position, direction, hint);
    }

    public String getWord() {
        return word;
    }

    public String getPosition() {
        return position;
    }

    public String getDirection() {
        return direction;
    }

    public String getHint() {
        return hint;
    }
}
