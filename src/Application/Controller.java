/* Created by andreea on 05/05/2020 */
package Application;

import Domain.Dictionary;
import Domain.Language;
import Domain.SoundexDictionary;
import Domain.Word;
import Infrastructure.Soundex;
import Infrastructure.SpellChecker;
import Presentation.Notepad;
import Presentation.Window;
import Utils.Constants;
import Utils.MultiMap;
import Utils.Utils;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Controller {

    private Reader reader;
    private Utils utils;
    private SpellChecker spellChecker;
    private Language selectedLanguage;
    private Window window;
    private Notepad notepad;
    private Dictionary dictionary;
    private MultiMap<String, String> dict = new MultiMap<String, String>();
    private HashMap<String, Dictionary> languageDictionary = new HashMap<>();
    private HashMap<String, String> dictionaryPath = new HashMap<>();
    private HashMap<String, Language> availableLanguages = new HashMap<>();
    private HashMap<Integer, Word> mispelledWordsCursorEnd = new HashMap<>();

    private ArrayList<Word> mispelledWords = new ArrayList<>();

    private boolean isSoundexDictionary;

    public Controller() {
        initApplication();
        reader = new Reader();
        spellChecker = new SpellChecker();
    }

    private void initApplication() {
        utils = new Utils();
        loadAvailableLanguages();
    }

    private void loadAvailableLanguages(){
        ArrayList<File> dictionaries = utils.listFilesForFolder(new File("dicc/"));
        for (File dictionary : dictionaries){
            String language = dictionary.getName().split("\\.")[0];
            Language newLanguage = new Language(language, new ImageIcon("src/Presentation/Images/" + language + ".png"));
            languageDictionary.put(newLanguage.getName(), null);
            dictionaryPath.put(newLanguage.getName(), dictionary.getAbsolutePath());
            availableLanguages.put(language, newLanguage);
        }
    }

    public Dictionary importDicctionary(String path) {
        dictionary = new Dictionary(path, reader.readDicc(path));
        return dictionary;
    }

    public Dictionary importSoundexDicctionary(String path) {
        return new SoundexDictionary(path, reader.readDicc(path));
    }

    public boolean findWordInDicctionary(Word wordToFind) {
         if (wordToFind.isSoundexWord()) {
            Collection<String> collection = dict.get(Soundex.soundex(wordToFind.getEntry()));
            for (String homophone : collection) {
                int distance = spellChecker.levenshtein(wordToFind.getEntry(), homophone);
                if (distance == 0) {
                    return true;
                } else {
                    if (!wordToFind.replaceWordsInitialized()) {
                        for (int i = 1; i <= 2; i++) {
                            wordToFind.addDistance(i);
                        }
                    }
                    if ((distance < 3)) {
                        wordToFind.addReplaceWord(new Word(distance, homophone, true));
                    }
                }
            }
        } else {
            for (Word word : dictionary.getEntries()) {
                int distance = spellChecker.levenshtein(wordToFind.getEntry(), word.getEntry());
                if (distance == 0) {
                    return true;
                } else {
                    if (!wordToFind.replaceWordsInitialized()) {
                        for (int i = 1; i <= 2; i++) {
                            wordToFind.addDistance(i);
                        }
                    }
                    if ((distance < 3)) {
                        wordToFind.addReplaceWord(new Word(distance, word.getEntry(), false));
                    }
                }
            }
        }

        return false;
    }

    public StringBuilder getFileContent(String path) {
        return reader.getFileContent(path);
    }

    public void checkText(String path) {
        ArrayList<String> wordsInText = reader.readFile(path);
        Word word;
        for (String wordToFind : wordsInText) {
            word = new Word(wordToFind, dictionary.getType().equals(Constants.PATH_DICC_EN) ? true : false);
            if (findWordInDicctionary(word)) {
                System.out.println("Word \"" + wordToFind + "\" exists");
            } else {
                System.out.println("Word \"" + wordToFind + "\" could not be found. Maybe you meant: ");
                for (Word replaceWord : word.getReplaceWords(1)) {
                    System.out.println(replaceWord.getEntry());
                }
            }
        }
    }

    public void checkText() {
        String[] wordsInText = notepad.getText().split(Constants.SYMBOLS_STRING);
        Word word;
        for (String wordToFind : wordsInText) {
            word = new Word(wordToFind, dictionary.getType().equals(Constants.PATH_DICC_EN) ? true : false);
            if (findWordInDicctionary(word)) {
                System.out.println("Word \"" + wordToFind + "\" exists");
            } else {
                notepad.underlineMispelledWord(word);
                int row = notepad.getWordRow(word);
                word.setLine(row);
                word.setPos(notepad.getText().indexOf(word.getEntry()));
                mispelledWords.add(word);
                System.out.println("Word \"" + wordToFind + "\" could not be found. Maybe you meant: ");
                for (Word replaceWord : word.getReplaceWords(1)) {
                    System.out.println(replaceWord.getEntry());
                }
            }
        }
        System.out.println(mispelledWords);
    }

    public void populateDict(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String soundex = Soundex.soundex(line);
                dict.put(soundex, line);
            }
        }
    }

    public JFileChooser getFileChooser() {
        return window.getFileChooser();
    }

    public void openFileChooser(boolean isEditable) {
        switch (window.getFileChooser().showOpenDialog(window)) {
            case JFileChooser.APPROVE_OPTION:
                notepad.setNotepadText(reader.getFileContent(window.getFileChooser().getSelectedFile().getAbsolutePath()));
                notepad.setNotepadEditable(isEditable);
                break;
        }
    }

    public void enableNotepad(boolean isEditable) {
        notepad.setNotepadEditable(isEditable);
    }

    public ArrayList<Language> getLanguages() {
        ArrayList<Language> langs = new ArrayList<>();;
        for (String language : dictionaryPath.keySet()){
            langs.add(availableLanguages.get(language));
        }
        return langs;
    }

    public void setSelectedLanguage(Language selectedLanguage){
        notepad.removeHighlights();
        mispelledWords.clear();
        this.selectedLanguage = selectedLanguage;
        if (languageDictionary.get(selectedLanguage.getName()) == null){
            if (Constants.SOUNDEX_DICTIONARIES.contains(selectedLanguage.getName())){
                languageDictionary.put(selectedLanguage.getName(), importSoundexDicctionary(dictionaryPath.get(selectedLanguage.getName())));
                isSoundexDictionary = true;
            } else {
                languageDictionary.put(selectedLanguage.getName(), importDicctionary(dictionaryPath.get(selectedLanguage.getName())));
                isSoundexDictionary = false;
            }
        }
        dictionary = languageDictionary.get(selectedLanguage.getName());
        if (Constants.SOUNDEX_DICTIONARIES.contains(selectedLanguage.getName())) {
            isSoundexDictionary = true;
        } else {
            isSoundexDictionary = false;
        }
        checkText();
    }

    public void setNotepad(Notepad notepad) {
        this.notepad = notepad;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public MultiMap<String, String> getDict() {
        return dict;
    }

    public void addMispelledWord(Word word, int row, int col){
        word.setLine(row);
        word.setPos(col);
        word.setMispelled(true);
        mispelledWords.add(word);
        if(mispelledWordsCursorEnd.get(col) != null){
            mispelledWordsCursorEnd.remove(col);
            mispelledWordsCursorEnd.put(col, word);
        }
        System.out.println(mispelledWords.toString());
    }

    public boolean isSoundexDictionary(){
        return isSoundexDictionary;
    }
}
