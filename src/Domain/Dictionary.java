/* Created by andreea on 07/05/2020 */
package Domain;

import Utils.Constants;

import java.util.ArrayList;

public class Dictionary {
    protected String type;
    protected ArrayList<Word> entries;

    public Dictionary(String type, ArrayList<String> entries) {
        this.type = type;
        this.entries = new ArrayList<>();
        Word word;
        for (String entry : entries) {
            word = new Word(entry, type.equals(Constants.PATH_DICC_EN) ? true : false);
            this.entries.add(word);
        }
    }

    public String getType() {
        return type;
    }

    public ArrayList<Word> getEntries() {
        return entries;
    }
}
