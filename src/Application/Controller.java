/* Created by andreea on 05/05/2020 */
package Application;

import Domain.Dictionary;
import Domain.Interfaces.IController;
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
import javax.swing.text.Highlighter;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Controller implements IController {

    private static Reader reader;
    private Utils utils;
    private static SpellChecker spellChecker;
    private Language selectedLanguage;
    private static Window window;
    private static Notepad notepad;
    private Sidebar sidebar;
    private static Dictionary dictionary;
    private static MultiMap<String, String> dict = new MultiMap<String, String>();
    private HashMap<String, Dictionary> languageDictionary = new HashMap<>();
    private HashMap<String, String> dictionaryPath = new HashMap<>();
    private HashMap<String, Language> availableLanguages = new HashMap<>();
    private static HashMap<Integer, Word> mispelledWordsCursorEnd = new HashMap<>();
    private static ArrayList<Word> mispelledWords = new ArrayList<>();
    private boolean dictPopulated = false;
    private static boolean isSoundexDictionary;
    private static ExecutorService executor = Executors.newSingleThreadExecutor();
    private Highlighter.Highlight[] highlights;
    private static int distance = 1;
    private static boolean suggestionsEnabled = false;

    public Controller() {
        initApplication();
        reader = new Reader();
        spellChecker = new SpellChecker();
        selectedLanguage = availableLanguages.get("español");
    }

    @Override
    public Dictionary importDicctionary(String path) {
        dictionary = new Dictionary(path, reader.readDicc(path));
        return dictionary;
    }

    @Override
    public Dictionary importSoundexDicctionary(String path) {
        return new SoundexDictionary(path, reader.readDicc(path));
    }

    public static boolean findWordInDicctionary(Word wordToFind) {
        if (!wordToFind.getEntry().equals("") && !wordToFind.getEntry().equals(" ")) {
            if (wordToFind.isSoundexWord()) {
                Collection<String> collection = dict.get(Soundex.soundex(wordToFind.getEntry()));
                for (String homophone : collection) {
                    int distance = spellChecker.levenshtein(wordToFind.getEntry(), homophone);
                    if (distance == 0) {
                        return true;
                    } else {
                        if (!wordToFind.replaceWordsInitialized()) {
                            for (int i = 1; i <= Constants.MAX_DISTANCE; i++) {
                                wordToFind.addDistance(i);
                            }
                        }
                        if ((distance <= Constants.MAX_DISTANCE)) {
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
                            for (int i = 1; i <= Constants.MAX_DISTANCE; i++) {
                                wordToFind.addDistance(i);
                            }
                        }
                        if ((distance <= Constants.MAX_DISTANCE)) {
                            wordToFind.addReplaceWord(new Word(distance, word.getEntry(), false));
                        }
                    }
                }
            }
        }
        return false;
    }

    public static void checkText() {
        window.resetModel();
        mispelledWords.clear();
        String[] wordsInText = notepad.getText().split(Constants.SYMBOLS_STRING);
        Word word;
        for (String wordToFind : wordsInText) {
            if ((!wordToFind.equals("")) && (!wordToFind.equals(" "))) {
                word = new Word(wordToFind, dictionary.getType().equals(Constants.PATH_DICC_EN) ? true : false);
                if (!findWordInDicctionary(word)) {
                    int row = notepad.getWordRow(word);
                    word.setLine(row);
                    word.setPos(notepad.getText().indexOf(word.getEntry()) + wordToFind.length());
                    addMispelledWord(word);
                    notepad.underlineMispelledWord(word);
                }
            }
        }
    }

    public static void correctSpellingFromText(){
        StringBuilder correctedText = new StringBuilder();
        correctedText.append(notepad.getText());
        int difference = 0;
        ArrayList<Word> auxMispelledWords = new ArrayList<>(mispelledWords);
        for (Word mispelledWord : auxMispelledWords){
            int  wordStart = (mispelledWord.getPos() + difference) - mispelledWord.getEntry().length();
            if (wordStart < 0){
                wordStart++;
            }
            String replaceWord = getFirstReplaceWord(mispelledWord);
            if (replaceWord != null){
                correctedText.replace(wordStart, wordStart + mispelledWord.getEntry().length(), replaceWord);
                mispelledWords.remove(mispelledWord);
                difference = difference + replaceWord.length() - mispelledWord.getEntry().length();
                removeFromModel(mispelledWord);
            }
        }
        notepad.setText(correctedText.toString());
    }

    private static String getFirstReplaceWord(Word word){
        int distance = 1;
        while (word.getReplaceWords(distance) == null || word.getReplaceWords(distance).isEmpty() || distance <= Constants.MAX_DISTANCE){
            distance++;
        }
        if (distance > Constants.MAX_DISTANCE){
            return null;
        }
        return word.getReplaceWords(distance).get(0).getEntry();
    }

    @Override
    public void populateDict(String filename){
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String soundex = Soundex.soundex(line);
                dict.put(soundex, line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openFileChooser(boolean isEditable) {
        switch (window.getFileChooser().showOpenDialog(window)) {
            case JFileChooser.APPROVE_OPTION:
                notepad.setNotepadText(reader.getFileContent(window.getFileChooser().getSelectedFile().getAbsolutePath()));
                notepad.setNotepadEditable(isEditable);
                executor.submit(() -> checkText());
                break;
        }
    }

    @Override
    public ArrayList<Language> getLanguages() {
        ArrayList<Language> langs = new ArrayList<>();

        for (String language : dictionaryPath.keySet()) {
            langs.add(availableLanguages.get(language));
        }
        return langs;
    }

    @Override
    public void setSelectedLanguage(Language selectedLanguage) {
        notepad.removeHighlights();
        mispelledWords.clear();
        this.selectedLanguage = selectedLanguage;
        if (languageDictionary.get(selectedLanguage.getName()) == null) {
            if (Constants.SOUNDEX_DICTIONARIES.contains(selectedLanguage.getName())) {
                languageDictionary.put(selectedLanguage.getName(), importSoundexDicctionary(dictionaryPath.get(selectedLanguage.getName())));
            } else {
                languageDictionary.put(selectedLanguage.getName(), importDicctionary(dictionaryPath.get(selectedLanguage.getName())));
            }
        }
        dictionary = languageDictionary.get(selectedLanguage.getName());
        if (Constants.SOUNDEX_DICTIONARIES.contains(selectedLanguage.getName())) {
            isSoundexDictionary = true;
            if (!dictPopulated) {
                populateDict(Constants.PATH_DICC_EN);
            }
        } else {
            isSoundexDictionary = false;
        }
        executor.submit(() -> checkText());
    }

    public static void toggleSuggestions() {
        suggestionsEnabled = !suggestionsEnabled;
    }

    private static ArrayList<Word> getReplaceWords(String input) {
        Iterator it = mispelledWords.iterator();
        while (it.hasNext()) {
            Word mispelledWord = (Word) it.next();
            if (mispelledWord.getEntry().equals(input)) {
                return mispelledWord.getReplaceWords(distance);
            }
        }
        return null;
    }

    public static void addMispelledWord(Word word) {
        word.setMispelled(true);
        boolean duplicate = false;
        ArrayList<Word> auxMispelledWord = (ArrayList<Word>) mispelledWords.clone();
        if (auxMispelledWord.size() > 0) {
            for (Word mispelledWord : auxMispelledWord) {
                if (word.getEntry().equals(mispelledWord.getEntry())) {
                    if (word.isSameWord(mispelledWord)) {
                        duplicate = true;
                    }
                }
            }
            if (!duplicate) {
                mispelledWords.add(word);
                addToModel(word);
            }
        } else {
            mispelledWords.add(word);
            addToModel(word);
        }

        if (mispelledWordsCursorEnd.get(word.getPos()) != null) {
            mispelledWordsCursorEnd.remove(word.getPos());
            mispelledWordsCursorEnd.put(word.getPos(), word);
        } else {
            mispelledWordsCursorEnd.put(word.getPos(), word);
        }
    }

    @Override
    public void deleteMispelledWord(int idx) {
        int wordLength = mispelledWordsCursorEnd.get(idx).getEntry().length();
        notepad.removeHighlightForWord(idx - wordLength, wordLength);
        Word mispelledWord = mispelledWordsCursorEnd.get(idx);
        for (Word word : mispelledWords) {
            if (word.getEntry().equals(mispelledWord.getEntry())) {
                if (word.isSameWord(mispelledWord)) {
                    window.removeFromModel(word);
                    break;
                }
            }
        }
        mispelledWords.remove(mispelledWordsCursorEnd.get(idx));
        mispelledWordsCursorEnd.remove(idx);
    }

    @Override
    public Object[] isMispelledWord(Word word) {
        Iterator it = mispelledWords.iterator();
        while (it.hasNext()) {
            Word mispelledWord = (Word) it.next();
            if (mispelledWord.getEntry().equals(word.getEntry())) {
                return new Object[]{true, mispelledWord};
            }
        }
        return new Object[]{false, null};
    }

    @Override
    public void replaceMispelledWordFromText(int idx, int lengthDifference) {
        deleteMispelledWord(idx);
        HashMap<Integer, Word> mispelledWordsCursorEndAux = new HashMap<>(mispelledWordsCursorEnd);
        Set<Integer> cursorEnds = mispelledWordsCursorEnd.keySet();
        if (Math.abs(lengthDifference) != 0) {
            int newCursorEnd;
            for (int cursorEnd : cursorEnds) {
                if (cursorEnd > idx){
                    Word word = mispelledWordsCursorEndAux.get(cursorEnd);
                    newCursorEnd = cursorEnd + lengthDifference;
                    word.setPos(newCursorEnd);
                    mispelledWordsCursorEndAux.put(newCursorEnd, word);
                    mispelledWordsCursorEndAux.remove(cursorEnd);
                }
            }
            mispelledWordsCursorEnd = mispelledWordsCursorEndAux;
        }
    }

    public static void addToModel(Word w) {
        window.addToModel(w);
    }

    public static void removeFromModel(Word w) {
        window.removeFromModel(w);
    }

    @Override
    public void resizePanels(int width, int height) {
        SwingUtilities.invokeLater(() -> {
            notepad.resizeNotepad(width, height);
            sidebar.resizeSideBar(width, height);
        });
    }

    public void increaseDistance() {
        distance++;
    }

    public void resetDistance() {
        distance = 1;
    }

    private static ArrayList<Word> getWords(String input) {
        if (suggestionsEnabled){
            //the suggestion provider can control text search related stuff, e.g case insensitive match, the search  limit etc.
            if (input.isEmpty()) {
                return null;
            }
            ArrayList<Word> words = (ArrayList<Word>)
                    dictionary.getEntries().stream()
                            .filter(s -> s.getEntry().startsWith(input))
                            .limit(20)
                            .collect(Collectors.toList());

            for (Word word : words) {
                if (word.getEntry().equals(input)) {
                    return new ArrayList<>(words.subList(1, words.size()));
                }
            }
            return words;
        }
        return null;
    }

    private void initApplication() {
        utils = new Utils();
        loadAvailableLanguages();
    }

    private void loadAvailableLanguages() {
        ArrayList<File> dictionaries = utils.listFilesForFolder(new File("dicc/"));
        for (File dictionary : dictionaries) {
            String language = dictionary.getName().split("\\.")[0];
            Language newLanguage = new Language(language, new ImageIcon("src/Presentation/Images/" + language + ".png"));
            languageDictionary.put(newLanguage.getName(), null);
            dictionaryPath.put(newLanguage.getName(), dictionary.getAbsolutePath());
            availableLanguages.put(language, newLanguage);
        }
    }

    public void setSidebar(Sidebar sidebar) {
        this.sidebar = sidebar;
    }

    public HashMap<Integer, Word> getMispelledWordsCursorEnd() {
        return mispelledWordsCursorEnd;
    }


    public void setNotepad(Notepad notepad) {
        this.notepad = notepad;
        SuggestionDropDownDecorator.decorate(notepad, new TextComponentWordSuggestionClient(Controller::getWords));
        SuggestionDropDownDecorator.decorate(notepad, new TextComponentWordReplace(Controller::getReplaceWords, this), this);
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public boolean isSoundexDictionary() {
        return isSoundexDictionary;
    }

    public static void enableNotepad(boolean isEditable) {
        notepad.setText("");
        notepad.setNotepadEditable(isEditable);
    }
}
