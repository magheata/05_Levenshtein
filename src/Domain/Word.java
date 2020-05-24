/**
 * AUTHORS: Rafael Adrián Gil Cañestro
 * Miruna Andreea Gheata
 */
package Domain;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class that represents a Word
 */
public class Word {

    private int distance;
    private String entry;
    private boolean mispelled;
    private int line;
    private int pos;
    private boolean soundexWord;
    private HashMap<Integer, ArrayList<Word>> replaceWords = new HashMap<>();

    /**
     * Constructor used for replacement words
     * @param distance levenshtein distance between the original word and this word (the replacement)
     * @param entry string representing the word
     * @param soundexWord wether or not it is an English type of dicctionary
     */
    public Word(int distance, String entry, boolean soundexWord) {
        this.distance = distance;
        this.entry = entry;
        this.soundexWord = soundexWord;
    }

    /**
     * Constructor used for normal words
     * @param entry
     * @param soundexWord
     */
    public Word(String entry, boolean soundexWord) {
        this.entry = entry;
        this.soundexWord = soundexWord;
    }

    public String getEntry() {
        return entry;
    }

    /**
     * Returns the replacements words that are at a distance equal to the provided one
     * @param distance levenshtein distance wanted
     * @return list of Word elements
     */
    public ArrayList<Word> getReplaceWords(int distance) {
        if (!replaceWords.containsKey(distance)) {
            return null;
        }
        return replaceWords.get(distance);
    }

    public boolean isSoundexWord() {
        return soundexWord;
    }

    public boolean replaceWordsInitialized() {
        return replaceWords.size() > 0;
    }

    public void addDistance(int distance) {
        replaceWords.put(distance, new ArrayList<>());
    }

    public void addReplaceWord(Word replaceWord) {
        replaceWords.get(replaceWord.distance).add(replaceWord);
    }

    public void setMispelled(boolean mispelled) {
        this.mispelled = mispelled;
    }

    public boolean isSameWord(Word word) {
        return (this.line == word.line) && (this.pos == word.pos);
    }

    public void setLine(int line) {
        this.line = line;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getPos() {
        return pos;
    }
}
