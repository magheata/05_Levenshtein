/* Created by andreea on 05/05/2020 */
package Domain;

import java.util.ArrayList;
import java.util.HashMap;

public class Word {
    private int distance;
    private String entry;
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
        return replaceWords.get(distance);
    }

    public boolean isSoundexWord() {
        return soundexWord;
    }

    public boolean replaceWordsInitialized(){
        return replaceWords.size() > 0;
    }

    public void addDistance(int distance){
        replaceWords.put(distance, new ArrayList<>());
    }

    public void addReplaceWord(Word replaceWord){
        replaceWords.get(replaceWord.distance).add(replaceWord);
    }

}
