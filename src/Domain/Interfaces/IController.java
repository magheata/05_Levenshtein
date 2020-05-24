/* Created by andreea on 18/05/2020 */
package Domain.Interfaces;

import Domain.Dictionary;
import Domain.Language;

import java.util.ArrayList;

public interface IController {
    Dictionary importDicctionary(String path);
    Dictionary importSoundexDicctionary(String path);
    ArrayList<Language> getLanguages();
    void deleteMispelledWord(int idx);
    void replaceMispelledWordFromText(int idx, int lengthDifference);
    void setSelectedLanguage(Language selectedLanguage);
}
