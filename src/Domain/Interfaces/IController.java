/* Created by andreea on 18/05/2020 */
package Domain.Interfaces;

import Domain.Dictionary;
import Domain.Language;
import Domain.Word;

import java.util.ArrayList;
import java.util.concurrent.Future;

public interface IController {
    Dictionary importDicctionary(String path);

    Dictionary importSoundexDicctionary(String path);

    boolean findWordInDicctionary(Word wordToFind);

    Future<ArrayList<Integer>> findWordInText(String wordToFind);

    ArrayList<Language> getLanguages();

    void addMispelledWord(Word word);

    void addReplaceWord(Word wordToFind, String replaceWord, int distance);

    void checkText();

    void correctSpellingFromText();

    void correctMispelledWord(Word mispelledWord, String correctedWord);

    void deleteMispelledWord(int idx);

    void replaceMispelledWordFromText(int idx, int lengthDifference);

    int replaceWord(int index, int lengthPreviousWord, String replacement);

    void replaceWords(String old, String newWord);

    void setSelectedLanguage(Language selectedLanguage);

    void updateMispelledCursorEnds(int idx, int lengthDifference);
}
