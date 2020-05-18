/* Created by andreea on 18/05/2020 */
package Domain.Interfaces;

import Domain.Dictionary;
import Domain.Language;
import Domain.Word;

import java.util.ArrayList;

public interface IController {
    Dictionary importDicctionary(String path);
    Dictionary importSoundexDicctionary(String path);
    boolean findWordInDicctionary(Word wordToFind);
    void checkText();
    void populateDict(String filename);
    void openFileChooser(boolean isEditable);
    void enableNotepad(boolean isEditable);
    ArrayList<Language> getLanguages();
    void addMispelledWord(Word word);
    void deleteMispelledWord(int idx);
    Object[] isMispelledWord(Word word);
    void replaceMispelledWordFromText(int idx, int lengthDifference);
    void resizePanels(int width, int height);
    void setSelectedLanguage(Language selectedLanguage);

    void toggleSuggestions();
}