/* Created by andreea on 05/05/2020 */
package Application;

import Domain.Dictionary;
import Domain.Language;
import Domain.SoundexDictionary;
import Domain.Word;
import Infrastructure.Soundex;
import Infrastructure.SpellChecker;
import Presentation.*;
import Utils.Constants;
import Utils.MultiMap;
import Utils.Utils;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Controller {

    private Reader reader;
    private Utils utils;
    private SpellChecker spellChecker;
    private Language selectedLanguage;
    private Window window;
    private Notepad notepad;
    private static Dictionary dictionary;

    private MultiMap<String, String> dict = new MultiMap<String, String>();
    private HashMap<String, Dictionary> languageDictionary = new HashMap<>();
    private HashMap<String, String> dictionaryPath = new HashMap<>();
    private HashMap<String, Language> availableLanguages = new HashMap<>();
    private HashMap<Integer, Word> mispelledWordsCursorEnd = new HashMap<>();

    private static ArrayList<Word> mispelledWords = new ArrayList<>();

    private boolean dictPopulated = false;

    private static boolean isSoundexDictionary;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public Controller() {
        initApplication();
        reader = new Reader();
        spellChecker = new SpellChecker();
        selectedLanguage = availableLanguages.get("español");
    }

    private static ArrayList<Word> getWords(String input) {
        //the suggestion provider can control text search related stuff, e.g case insensitive match, the search  limit etc.
        if (input.isEmpty()) {
            return null;
        }
        ArrayList<Word> words = (ArrayList<Word>)
                dictionary.getEntries().stream()
                .filter(s -> s.getEntry().startsWith(input))
                .limit(20)
                .collect(Collectors.toList());

        for (Word word: words){
            if (word.getEntry().equals(input)){
                return new ArrayList<>(words.subList(1, words.size()));
            }
        }
        return words;
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
        if (!wordToFind.getEntry().equals("") && !wordToFind.getEntry().equals(" ")){
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
        }
        return false;
    }

    public StringBuilder getFileContent(String path) {
        return reader.getFileContent(path);
    }

    public void checkText() {
        String[] wordsInText = notepad.getText().split(Constants.SYMBOLS_STRING);
        Word word;
        for (String wordToFind : wordsInText) {
            if ((!wordToFind.equals("")) && (!wordToFind.equals(" "))){
                word = new Word(wordToFind, dictionary.getType().equals(Constants.PATH_DICC_EN) ? true : false);
                if (!findWordInDicctionary(word)) {
                    notepad.underlineMispelledWord(word);
                    int row = notepad.getWordRow(word);
                    word.setLine(row);
                    word.setPos(notepad.getText().indexOf(word.getEntry()));
                    mispelledWords.add(word);
                }
            }
        }
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

    public void openFileChooser(boolean isEditable) {
        switch (window.getFileChooser().showOpenDialog(window)) {
            case JFileChooser.APPROVE_OPTION:
                notepad.setNotepadText(reader.getFileContent(window.getFileChooser().getSelectedFile().getAbsolutePath()));
                notepad.setNotepadEditable(isEditable);
                executor.submit(() -> checkText());
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
            } else {
                languageDictionary.put(selectedLanguage.getName(), importDicctionary(dictionaryPath.get(selectedLanguage.getName())));
            }
        }
        dictionary = languageDictionary.get(selectedLanguage.getName());
        if (Constants.SOUNDEX_DICTIONARIES.contains(selectedLanguage.getName())) {
            isSoundexDictionary = true;
            if (!dictPopulated){
                try {
                    populateDict(Constants.PATH_DICC_EN);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            isSoundexDictionary = false;
        }
        executor.submit(() -> checkText());
    }

    private static ArrayList<Word> getReplaceWords(String input) {
        Iterator it = mispelledWords.iterator();
        while (it.hasNext()){
            Word mispelledWord = (Word) it.next();
            if (mispelledWord.getEntry().equals(input)){
                return mispelledWord.getReplaceWords(1);
            }
        }
        return null;
    }

    public void setNotepad(Notepad notepad) {
        this.notepad = notepad;
        SuggestionDropDownDecorator.decorate(notepad, new TextComponentWordSuggestionClient(Controller::getWords));
        SuggestionDropDownDecorator.decorate(notepad, new TextComponentWordReplace(Controller::getReplaceWords), this);
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public void addMispelledWord(Word word){
        word.setMispelled(true);
        boolean duplicate = false;
        ArrayList<Word> auxMispelledWord = (ArrayList<Word>) mispelledWords.clone();
        if (auxMispelledWord.size() > 0){
            for (Word mispelledWord : auxMispelledWord){
                if (word.getEntry().equals(mispelledWord.getEntry())){
                    if (word.isSameWord(mispelledWord)){
                        duplicate = true;
                    }
                }
            }
            if (!duplicate){
                mispelledWords.add(word);
            }
        } else {
            mispelledWords.add(word);
        }

        if(mispelledWordsCursorEnd.get(word.getPos()) != null){
            mispelledWordsCursorEnd.remove(word.getPos());
            mispelledWordsCursorEnd.put(word.getPos(), word);
        } else {
            mispelledWordsCursorEnd.put(word.getPos(), word);
        }
        System.out.println(mispelledWords.toString());
    }

    public boolean isSoundexDictionary(){
        return isSoundexDictionary;
    }

    public void deleteMispelledWord(int idx) {
        mispelledWords.remove(mispelledWordsCursorEnd.get(idx));
        mispelledWordsCursorEnd.remove(idx);
    }

    public HashMap<Integer, Word> getMispelledWordsCursorEnd() {
        return mispelledWordsCursorEnd;
    }

    public Object[] isMispelledWord(Word word){
        Iterator it = mispelledWords.iterator();
        while(it.hasNext()){
            Word mispelledWord = (Word) it.next();
            if (mispelledWord.getEntry().equals(word.getEntry())){
                return new Object[]{true, mispelledWord};
            }
        }
        return new Object[]{false, null};
    }
}
