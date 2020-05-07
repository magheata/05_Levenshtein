/* Created by andreea on 07/05/2020 */
package Domain;

import java.util.ArrayList;

public class Dictionary {
    private String type;
    private ArrayList<Word> entries;

    public Dictionary(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public ArrayList<Word> getEntries() {
        return entries;
    }

    public void setEntries(ArrayList<String> entries) {
        this.entries = new ArrayList<>();
        Word word;
        for(String entry : entries){
            word = new Word(entry);
            this.entries.add(word);
        }
    }
}
