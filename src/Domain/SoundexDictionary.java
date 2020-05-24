/* Created by andreea on 07/05/2020 */
package Domain;

import Infrastructure.Soundex;
import Utils.MultiMap;

import java.util.ArrayList;
import java.util.Collection;

public class SoundexDictionary extends Dictionary {

    private MultiMap<String, String> dict;

    public SoundexDictionary(String type, ArrayList<String> entries) {
        super(type, entries);
        populateDict();
    }

    private void populateDict() {
        dict = new MultiMap<String, String>();
        for (Word word : entries) {
            dict.put(Soundex.soundex(word.getEntry()), word.getEntry());
        }
    }

    public MultiMap<String, String> getDict() {
        return dict;
    }
}
