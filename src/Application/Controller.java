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
import Utils.*;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.Utilities;
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
    private static FindPanel findPanel;
    private static Window window;
    private static Notepad notepad;
    private static Sidebar sidebar;
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

    public static boolean isSuggestionsEnabled() {
        return suggestionsEnabled;
    }

    private static boolean suggestionsEnabled = false;

    public static boolean isSuggestionUsed() {
        return suggestionUsed;
    }

    public static void setSuggestionUsed(boolean suggestionUsed) {
        Controller.suggestionUsed = suggestionUsed;
    }

    private static boolean suggestionUsed = false;
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
        executor.submit(() ->{
            window.resetModel();
            mispelledWords.clear();
            String[] wordsInText = notepad.getText().split(Constants.SYMBOLS_STRING);
            Word word;
            int index;
            int oldIndex = -1;
            for (String wordToFind : wordsInText) {
                if ((!wordToFind.equals("")) && (!wordToFind.equals(" "))) {
                    word = new Word(wordToFind, dictionary.getType().equals(Constants.PATH_DICC_EN) ? true : false);
                    if (!findWordInDicctionary(word)) {
                        int row = notepad.getWordRow(word);
                        index = notepad.getText().indexOf(word.getEntry(), oldIndex + 1);
                        word.setLine(row);
                        word.setPos(index + wordToFind.length());
                        oldIndex = index;
                        addMispelledWord(word);
                        notepad.underlineMispelledWord(word);
                    }
                }
            }
            updateMispelledWordsCount();
        });
    }

    public static void correctSpellingFromText(){
        executor.submit(() ->{
            StringBuilder correctedText = new StringBuilder();
            correctedText.append(notepad.getText());
            int difference = 0;
            ArrayList<Word> auxMispelledWords = new ArrayList<>(mispelledWords);
            for (Word mispelledWord : auxMispelledWords){
                int wordStart = ((mispelledWord.getPos() + difference) - mispelledWord.getEntry().length());
                boolean firstWord = false;
                if (wordStart < 0) {
                    firstWord = true;
                }
                if (!firstWord){
                    while (Constants.SYMBOLS.contains(correctedText.charAt(wordStart))){
                        wordStart++;
                    }
                }

                String replaceWord = getFirstReplaceWord(mispelledWord);
                if (replaceWord != null){
                    correctedText.delete(firstWord ? (wordStart + 1) : wordStart,
                            firstWord ? (wordStart + 1) + mispelledWord.getEntry().length() : wordStart + mispelledWord.getEntry().length());
                    for (int i = 0; i < replaceWord.length(); i++){
                        if (firstWord){
                            correctedText.insert(wordStart + i + 1, replaceWord.toCharArray()[i]);
                        } else {
                            correctedText.insert(wordStart + i, replaceWord.toCharArray()[i]);
                        }
                    }
                    mispelledWords.remove(mispelledWord);
                    difference = difference + (replaceWord.length() - mispelledWord.getEntry().length());
                    if (firstWord){
                        difference++;
                    }
                    removeFromModel(mispelledWord);
                }
            }
            notepad.setText(correctedText.toString());
            mispelledWordsCursorEnd.clear();
            window.updateStatusText("No errors in text", new ImageIcon(Constants.PATH_CORRECT_ICON));
        });
    }

    private static String getFirstReplaceWord(Word word){
        int distance = 1;
        while ((word.getReplaceWords(distance) == null || word.getReplaceWords(distance).isEmpty()) && distance <= Constants.MAX_DISTANCE){
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
        mispelledWords.clear();
        SwingUtilities.invokeLater(() -> {
            notepad.removeHighlights();
            window.setSelectedLanguageLabel(selectedLanguage.getName().substring(0, 1).toUpperCase() + selectedLanguage.getName().substring(1), selectedLanguage.getIcon());
        });
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
                updateMispelledCursorEnds(word.getPos(), word.getEntry().length());
            }
        } else {
            mispelledWords.add(word);
            addToModel(word);
        }
        window.updateStatusText(mispelledWords.size() + " mispelled words in text", new ImageIcon(Constants.PATH_INCORRECT_ICON));
        if (mispelledWordsCursorEnd.get(word.getPos()) != null) {
            mispelledWordsCursorEnd.remove(word.getPos());
            mispelledWordsCursorEnd.put(word.getPos(), word);
        } else {
            mispelledWordsCursorEnd.put(word.getPos(), word);
        }
    }

    @Override
    public void deleteMispelledWord(int idx) {
        if (mispelledWordsCursorEnd.get(idx) == null){
            idx--;
        }
        int wordLength = mispelledWordsCursorEnd.get(idx).getEntry().length();
        notepad.removeUnderlineForWord(idx - wordLength, wordLength);
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
        updateMispelledWordsCount();
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
        updateMispelledCursorEnds(idx, lengthDifference);
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

    public static void resetNotepad(boolean isEditable) {
        notepad.setText("");
        notepad.setNotepadEditable(isEditable);
        window.updateStatusText("", null);
        mispelledWordsCursorEnd.clear();
        mispelledWords.clear();
        window.resetModel();
    }

    public static void enableNotepad(boolean isEditable) {
        notepad.setNotepadEditable(isEditable);
    }

    public static void enableFindPanel(boolean enabled){
        findPanel.setVisible(enabled);
        findPanel.enableFindPanel(enabled);
    }

    public static void enableSidebarPanel(boolean enabled){
        sidebar.setVisible(enabled);
        if (enabled){
            notepad.setSize(Constants.DIM_NOTEPAD);
            notepad.setPreferredSize(Constants.DIM_NOTEPAD);
        } else {
            notepad.setSize(Constants.DIM_WINDOW);
            notepad.setPreferredSize(Constants.DIM_WINDOW);
        }
    }

    public static void enableFindReplacePanel(boolean enabled){
        findPanel.setVisible(enabled);
        findPanel.enableFindReplacePanel(enabled);
    }

    public void setFindPanel(FindPanel findPanel) {
        this.findPanel = findPanel;
    }

    public ArrayList<Integer> findWordInText(String wordToFind){
        removeFindWordHighlights();
        String text = notepad.getText();
        ArrayList<Integer> indexes = new ArrayList<>();
        int index = text.indexOf(wordToFind);
        while (index != -1){
            int finalIndex = index;
            SwingUtilities.invokeLater(() -> notepad.highlightWordInText(finalIndex, wordToFind.length()));
            indexes.add(index);
            index = text.indexOf(wordToFind, index + 1);
        }
        return indexes;
    }

    public void removeFindWordHighlights(){
        SwingUtilities.invokeLater(() -> notepad.removeFindWordHighlights());
    }

    public void setCursorPosInNotepad(int currentIndex) {
        SwingUtilities.invokeLater(() -> window.setCaretPosition(currentIndex));
    }

    public void deleteWordsFromPanel(ArrayList<Integer> indexes, int length){
        Collections.reverse(indexes);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++){
            sb.append(" ");
        }
        for (int index : indexes){
            try {
                notepad.getDocument().remove(index, length);
                notepad.getDocument().insertString(index, sb.toString(), null);
                String text = notepad.getText();
                System.out.println(text);
            } catch (BadLocationException e) {
            }
        }
    }

    public void trimText(int start, int end){
        try {

            int nextWordIndex = end;
            while ((nextWordIndex < notepad.getText().length()) && (Constants.SYMBOLS.contains(notepad.getText().charAt(nextWordIndex)))){
                notepad.getDocument().remove(nextWordIndex - 1, 1);
                nextWordIndex++;
            }
            String text = notepad.getText(start, end - start);
            text = text.trim();
            notepad.getDocument().remove(start, end - start);
            notepad.getDocument().insertString(start, text, null);
            notepad.setCaretPosition(end);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void replaceWords(String old, String newWord){
        notepad.setText(notepad.getText().replace(old, newWord));
    }
    public int replaceWord(int index, int length, String text) {
        int difference = -9999;
        try {
            difference = text.length() - length;
            if (Math.abs(difference) != 0){
                text = text.concat(" ");
                if (difference < 0){
                    difference--;
                } else {
                    difference++;
                }
            }
            updateMispelledCursorEnds(index, difference);
            notepad.getDocument().insertString(index, text, null);
            System.out.println(notepad.getText());
        } catch (BadLocationException e) {
        }
        return difference;
    }

    public static void updateMispelledCursorEnds(int idx, int lengthDifference) {
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

    public static void removeMispelledWordsBetweenSelection(int selectionStart, int selectionEnd) {
        for (int idx : mispelledWordsCursorEnd.keySet()){
            if ((selectionStart <= idx)  && (idx <= selectionEnd)){
                removeFromModel(mispelledWordsCursorEnd.get(idx));
                mispelledWords.remove(mispelledWordsCursorEnd.get(idx));
                mispelledWordsCursorEnd.remove(idx);
            }
        }
    }

    public void setWordMispelledWord(Word mispelledWord, String correctedWord) {
        String newWord = correctedWord.split(" ")[0];
        int index = (int) getKeysByValue(mispelledWordsCursorEnd, mispelledWord).toArray()[0];
        try {
            int wordStart;
            if ((index - (mispelledWord.getEntry().length()) > 0)){
                wordStart = index - mispelledWord.getEntry().length();
            } else {
                wordStart = index - (mispelledWord.getEntry().length() - 1);
            }
            while (Constants.SYMBOLS.contains(notepad.getText().charAt(wordStart))){
                wordStart++;
            }
            notepad.getDocument().remove(wordStart, mispelledWord.getEntry().length());
            notepad.getDocument().insertString(wordStart, newWord, null);
            updateMispelledCursorEnds(index, newWord.length() - mispelledWord.getEntry().length());
            mispelledWordsCursorEnd.remove(index);
            mispelledWords.remove(mispelledWord);
            updateMispelledWordsCount();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private static void updateMispelledWordsCount(){
        if (mispelledWords.size() == 0){
            window.updateStatusText("No errors in text", new ImageIcon(Constants.PATH_CORRECT_ICON));

        } else {
            window.updateStatusText(mispelledWords.size() + " mispelled words in text", new ImageIcon(Constants.PATH_INCORRECT_ICON));
        }
    }

    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        return map.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), value))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}
