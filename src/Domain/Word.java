/* Created by andreea on 05/05/2020 */
package Domain;

import java.util.ArrayList;
import java.util.HashMap;

public class Word {
    private int distance;
    private String entry;
    private boolean mispelled;

    public void setLine(int line) {
        this.line = line;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getLine() {
        return line;
    }

    public int getPos() {
        return pos;
    }

    private int line;
    private int pos;
    private boolean soundexWord;
    private HashMap<Integer, ArrayList<Word>> replaceWords = new HashMap<>();

    public Word(int distance, String entry, boolean soundexWord) {
        this.distance = distance;
        this.entry = entry;
        this.soundexWord = soundexWord;
    }

    public Word(String entry, boolean soundexWord) {
        this.entry = entry;
        this.soundexWord = soundexWord;
    }

    public String getEntry() {
        return entry;
    }

    public ArrayList<Word> getReplaceWords(int distance) {
        if (!replaceWords.containsKey(distance)){
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

    public boolean isMispelled() {
        return mispelled;
    }

    public void setMispelled(boolean mispelled) {
        this.mispelled = mispelled;
    }

    public boolean isSameWord(Word word){
        return (this.line == word.line) && (this.pos == word.pos);
    }

    @Override
    public String toString() {
        return "Word{" +
                "pos=" + pos +
                ", entry='" + entry + '\'' +
                ", soundexWord=" + soundexWord +
                "}\n";
    }
}
