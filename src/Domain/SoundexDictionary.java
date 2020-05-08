/* Created by andreea on 07/05/2020 */
package Domain;

import Infrastructure.Soundex;
import Utils.MultiMap;

import java.util.ArrayList;
import java.util.Collection;

public class SoundexDictionary extends Dictionary {

    public MultiMap<String, String> dict = new MultiMap<>();

    public SoundexDictionary(String type, ArrayList<String> entries) {
        super(type, entries);
        populateDict();
    }

    public void populateDict() {
        for (Word word : entries) {
            dict.put(Soundex.soundex(word.getEntry()), word.getEntry());
        }
    }

    public Collection<String> getHomophones(String word) {
        return dict.get(word);
    }
}
