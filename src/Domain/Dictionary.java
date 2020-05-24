/**
 * AUTHORS: Rafael Adrián Gil Cañestro
 * Miruna Andreea Gheata
 */
package Domain;

import Utils.Constants;

import java.util.ArrayList;

/**
 * Class that represents a Dictionary
 */
public class Dictionary {
    protected String type;
    protected ArrayList<Word> entries;

    /**
     * Constructor of the class. The is the path of the dictionary and the entries are the words extracted from the
     * .dicc file.
     * @param type
     * @param entries
     */
    public Dictionary(String type, ArrayList<String> entries) {
        this.type = type;
        this.entries = new ArrayList<>();
        Word word;
        // For every String representing a word we create a Word object and add it to the entries
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
