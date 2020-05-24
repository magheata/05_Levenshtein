/**
 * AUTHORS: Rafael Adrián Gil Cañestro
 * Miruna Andreea Gheata
 */
package Application;

import Domain.Dictionary;
import Domain.Interfaces.IController;
import Domain.Language;
import Domain.SoundexDictionary;
import Domain.Word;
import Infrastructure.*;
import Presentation.FindPanel;
import Presentation.Notepad;
import Presentation.Sidebar;
import Presentation.Window;
import Utils.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static Utils.Utils.getKeysByValue;

/**
 * Controller of the project. Handles the communication between the Model and the View, as well as the rest of the necessary
 * classes used for the implementation of the program.
 */
public class Controller implements IController {

    private Utils utils;
    private static SoundexDictionary soundex;

    private static Reader reader;
    private static SpellChecker spellChecker;
    private static FindPanel findPanel;
    private static Window window;
    private static Notepad notepad;
    private static Sidebar sidebar;
    private static Dictionary dictionary;

    private static ArrayList<Word> mispelledWords = new ArrayList<>();
    private static boolean isSoundexDictionary;
    private static ExecutorService executor = Executors.newSingleThreadExecutor();
    private static int distance = 1;
    private static boolean suggestionsEnabled = false;
    private static boolean suggestionUsed = false;

    private boolean dictPopulated = false;

    private static HashMap<Integer, Word> mispelledWordsCursorEnd = new HashMap<>();

    private HashMap<String, Dictionary> languageDictionary = new HashMap<>();
    private HashMap<String, String> dictionaryPath = new HashMap<>();
    private HashMap<String, Language> availableLanguages = new HashMap<>();

    public Controller() {
        initApplication();
    }

    /**
     * Saves the mispelled word
     * @param word mispelled word to save
     */
    public static void addMispelledWord(Word word) {
        word.setMispelled(true);
        // Used to know if we have already saved this mispelled word
        boolean duplicate = false;
        ArrayList<Word> auxMispelledWord = (ArrayList<Word>) mispelledWords.clone();

        // if we already have mispeleld words save we check to see if we have saved it in a previous iteration
        if (auxMispelledWord.size() > 0) {
            for (Word mispelledWord : auxMispelledWord) {
                // If the word is the same we check if the position and line is the same as well
                if (word.getEntry().equals(mispelledWord.getEntry())) {
                    if (word.isSameWord(mispelledWord)) {
                        // If so, it is a duplicate
                        duplicate = true;
                    }
                }
            }
            // If it's not a duplicate we save the mispelled word
            if (!duplicate) {
                mispelledWords.add(word);
                // We add it to the Sidebar
                addToSidebar(word);
                // We update the cursor ends of the words that come after this one
                updateMispelledCursorEnds(word.getPos(), word.getEntry().length());
            }
            // if it's empty we simply add the word
        } else {
            mispelledWords.add(word);
            addToSidebar(word);
        }
        // Update the number of mispelled words and display it
        window.updateStatusText(mispelledWords.size() + " mispelled words in text", new ImageIcon(Constants.PATH_INCORRECT_ICON));
        if (mispelledWordsCursorEnd.get(word.getPos()) != null) {
            mispelledWordsCursorEnd.remove(word.getPos());
            mispelledWordsCursorEnd.put(word.getPos(), word);
        } else {
            mispelledWordsCursorEnd.put(word.getPos(), word);
        }
    }

    /**
     * Adds a replacement word with a determined distance to a Word element.
     * @param wordToFind word to add to
     * @param replaceWord replacement word
     * @param distance Levenshtein distance
     */
    private static void addReplaceWord(Word wordToFind, String replaceWord, int distance) {
        if (!wordToFind.replaceWordsInitialized()) {
            for (int i = 1; i <= Constants.MAX_DISTANCE; i++) {
                wordToFind.addDistance(i);
            }
        }
        // If the distance is within the permitted range we add the mispelled word
        if ((distance <= Constants.MAX_DISTANCE)) {
            wordToFind.addReplaceWord(new Word(distance, replaceWord, isSoundexDictionary));
        }
    }

    public static void addToSidebar(Word w) {
        window.addToSidebar(w);
    }

    /**
     * Analyzes the text in the Notepad and marks the mispelled words.
     */
    public static void checkText() {
        executor.submit(() -> {
            // Reset the necessary variables
            window.resetSidebar();
            mispelledWords.clear();
            // Get the words in the text by splitting the text using the symbols stored in the Constants.SYMBOLS_STRING
            String[] wordsInText = notepad.getText().split(Constants.SYMBOLS_STRING);
            Word word;

            int index; // used to know current index
            int oldIndex = -1; // used to know previous word index
            for (String wordToFind : wordsInText) {
                // Check if word is blank
                if ((!wordToFind.equals("")) && (!wordToFind.equals(" "))) {
                    // Create the word
                    word = new Word(wordToFind, dictionary.getType().equals(Constants.PATH_DICC_EN) ? true : false);
                    // If we do not find the word, mark it as mispelled
                    if (!findWordInDicctionary(word)) {
                        int row = notepad.getWordRow(word);
                        // get index of the current mispelled word. We look for it after the oldIndex because it might
                        // be possible to have the same word mispelled more than once
                        index = notepad.getText().indexOf(word.getEntry(), oldIndex + 1);
                        word.setLine(row);
                        word.setPos(index + wordToFind.length());
                        oldIndex = index;
                        // Save the mispelled word
                        addMispelledWord(word);
                        notepad.underlineMispelledWord(word);
                    }
                }
            }
            // Update the display of the number of mispelled words
            updateMispelledWordsCount();
        });
    }

    /**
     * Checks text and replaces all mispelled words with the first replacement it finds.
     */
    public static void correctSpellingFromText() {
        executor.submit(() -> {
            StringBuilder correctedText = new StringBuilder(notepad.getText());
            int difference = 0;
            ArrayList<Word> auxMispelledWords = new ArrayList<>(mispelledWords);
            for (Word mispelledWord : auxMispelledWords) {
                int wordStart = ((mispelledWord.getPos() + difference) - mispelledWord.getEntry().length());
                boolean firstWord = false;
                if (wordStart < 0) {
                    firstWord = true;
                }
                // We get the index of the next word
                if (!firstWord) {
                    while (Constants.SYMBOLS.contains(correctedText.charAt(wordStart))) {
                        wordStart++;
                    }
                }
                // Get the first replacement word we find
                String replaceWord = getFirstReplaceWord(mispelledWord);
                if (replaceWord != null) {
                    // we delete the previous word from the text
                    correctedText.delete(firstWord ? (wordStart + 1) : wordStart,
                            firstWord ? (wordStart + 1) + mispelledWord.getEntry().length() : wordStart + mispelledWord.getEntry().length());
                    mispelledWords.remove(mispelledWord);
                    // we add the replacement
                    if (firstWord) {
                        correctedText.insert(wordStart + 1, replaceWord);
                    } else {
                        correctedText.insert(wordStart, replaceWord);
                    }
                    difference = difference + (replaceWord.length() - mispelledWord.getEntry().length());
                    if (firstWord) {
                        difference++;
                    }
                    // we remove the word from the sidebar
                    removeFromSidebar(mispelledWord);
                }
            }

            // Set the corrected text to the notepad and clear the mispelled words variables
            notepad.setText(correctedText.toString());
            mispelledWordsCursorEnd.clear();
            window.updateStatusText("No errors in text", new ImageIcon(Constants.PATH_CORRECT_ICON));
        });
    }

    /**
     * Used when a mispelled word from the Sidebar is set to a suggested replacement.
     * @param mispelledWord word to replace
     * @param correctedWord string to replace with
     */
    public void correctMispelledWord(Word mispelledWord, String correctedWord) {
        String newWord = correctedWord.split(" ")[0];
        int index = (int) getKeysByValue(mispelledWordsCursorEnd, mispelledWord).toArray()[0];
        try {
            // Get the word start for the word
            int wordStart;
            if ((index - (mispelledWord.getEntry().length()) >= 0)) {
                wordStart = index - mispelledWord.getEntry().length();
            } else {
                wordStart = index - (mispelledWord.getEntry().length() - 1);
            }
            while (Constants.SYMBOLS.contains(notepad.getText().charAt(wordStart))) {
                wordStart++;
            }
            // Remove old word from the text and replace it with the new one
            notepad.getDocument().remove(wordStart, mispelledWord.getEntry().length());
            notepad.getDocument().insertString(wordStart, newWord, null);
            mispelledWords.remove(mispelledWordsCursorEnd.remove(index));

            // Update the indexes for the mispelled words
            updateMispelledCursorEnds(index, newWord.length() - mispelledWord.getEntry().length());
            // Update display of mispelled words count
            updateMispelledWordsCount();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }


    /**
     * Deletes the mispelled word found at the index and updates the rest of the indexes accordingly
     * @param idx
     */
    @Override
    public void deleteMispelledWord(int idx) {
        // We check if the index is the right one, if not we decrease it
        if (mispelledWordsCursorEnd.get(idx) == null) {
            idx--;
        }
        int wordLength = mispelledWordsCursorEnd.get(idx).getEntry().length();
        // We remove the underline from the word
        notepad.removeUnderlineForWord(idx - wordLength, wordLength);

        // We get the word and we remove the correct one from the sidebar
        window.removeFromSidebar(mispelledWordsCursorEnd.get(idx));
        mispelledWords.remove(mispelledWordsCursorEnd.remove(idx));
        updateMispelledWordsCount();
    }

    /**
     * Checks if the word is in the current dictionary
     * @param wordToFind
     * @return True if words is found, False otherwise
     */
    public static boolean findWordInDicctionary(Word wordToFind) {
        // If the word is not blank
        if (!wordToFind.getEntry().equals("") && !wordToFind.getEntry().equals(" ")) {
            // If it's a soundex dicionary (English) we have to get the words that are pronounced the same. This way
            // we limit the search
            int distance = -1;
            if (wordToFind.isSoundexWord()) {
                Collection<String> collection = soundex.getDict().get(Soundex.soundex(wordToFind.getEntry()));
                for (String homophone : collection) {
                    distance = spellChecker.levenshtein(wordToFind.getEntry(), homophone);
                    if (distance == 0) {
                        return true;
                    } else {
                        addReplaceWord(wordToFind, homophone, distance);
                    }
                }
                // Otherwise we look for the word in the normal dictionary
            } else {
                for (Word word : dictionary.getEntries()) {
                    distance = spellChecker.levenshtein(wordToFind.getEntry(), word.getEntry());
                    if (distance == 0) {
                        return true;
                    } else {
                        addReplaceWord(wordToFind, word.getEntry(), distance);
                    }
                }
            }
        }
        return false;
    }

    /**
     * Method that searches for all the ocurrences of the given String inside the text.
     * @param wordToFind string to find
     * @return indexes of all the ocurrences of the wordToFind
     */
    public Future<ArrayList<Integer>> findWordInText(String wordToFind) {
        return executor.submit(() -> {
            // remove the previous highlighted words
            removeFindWordHighlights();
            String text = notepad.getText();
            ArrayList<Integer> indexes = new ArrayList<>();
            int index = text.indexOf(wordToFind);
            while (index != -1) {
                int finalIndex = index;
                SwingUtilities.invokeLater(() -> notepad.highlightWordInText(finalIndex, wordToFind.length()));
                indexes.add(index);
                // we keep looking for the next ocurrences after this index
                index = text.indexOf(wordToFind, index + 1);
            }
            return indexes;
        });
    }

    /**
     * Return the first replace word it finds for the given Word element. It looks through all the replacement words
     * with all the distances until it finds a valid replacement word. If the word has no replacement words within the
     * maximum distance, it return null
     * @param word word to replace
     * @return String with the replacement word
     */
    private static String getFirstReplaceWord(Word word) {
        // We start at distance 1
        int distance = 1;
        // We check if we have words for this distance; if not, we increment the distance and try again
        while ((word.getReplaceWords(distance) == null || word.getReplaceWords(distance).isEmpty()) && distance <= Constants.MAX_DISTANCE) {
            distance++;
        }
        // If the distance is bigger than the maximum it means no words were found, we return null
        if (distance > Constants.MAX_DISTANCE) {
            return null;
        }
        // Return the first word with the given distance
        return word.getReplaceWords(distance).get(0).getEntry();
    }

    /**
     * Returns all Languages available for this program
     * @return list of Language elements
     */
    @Override
    public ArrayList<Language> getLanguages() {
        ArrayList<Language> langs = new ArrayList<>();
        for (String language : dictionaryPath.keySet()) {
            langs.add(availableLanguages.get(language));
        }
        return langs;
    }

    /**
     * Method used for the dropdown replacement suggestion component. With the given string input it determines which
     * mispelled word it is and gets its replacement words.
     * @param input
     * @return
     */
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

    /**
     * Method used for the dropdown suggestion component. It returns up to 20 words that start with the given input.
     * @param input
     * @return
     */
    private static ArrayList<Word> getWords(String input) {
        if (suggestionsEnabled) {
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
                // We take out the same word from the list and return the rest
                if (word.getEntry().equals(input)) {
                    return new ArrayList<>(words.subList(1, words.size()));
                }
            }
            return words;
        }
        return null;
    }

    /**
     * Creates a new Dictionary object from the path given
     * @param path path to the .dicc file
     * @return new Dictionary object
     */
    @Override
    public Dictionary importDicctionary(String path) {
        dictionary = new Dictionary(path, reader.readDicc(path));
        return dictionary;
    }

    /**
     * Creates Soundex element
     * @param path
     * @return
     */
    @Override
    public Dictionary importSoundexDicctionary(String path) {
        soundex = new SoundexDictionary(path, reader.readDicc(path));
        return soundex;
    }

    /**
     * Initialization of Controller
     */
    private void initApplication() {
        utils = new Utils();
        reader = new Reader();
        spellChecker = new SpellChecker();
        loadAvailableLanguages();
    }

    /**
     * Loads all available languages for the program. The available languages are determined by the number of "dicc" files
     * found in the dicc/ folder.
     */
    private void loadAvailableLanguages() {
        // Get all files from the folder
        ArrayList<File> dictionaries = utils.listFilesForFolder(new File("dicc/"));
        for (File dictionary : dictionaries) {
            // for each file
            String language = dictionary.getName().split("\\.")[0];
            // Create the new language with the name of the file
            Language newLanguage = new Language(language, new ImageIcon("src/Presentation/Images/" + language + ".png"));
            languageDictionary.put(newLanguage.getName(), null);
            // Save the dictionary path for this language
            dictionaryPath.put(newLanguage.getName(), dictionary.getAbsolutePath());
            availableLanguages.put(language, newLanguage);
        }
    }

    /**
     * Creates an instance of JFileChooser and displays it. When the user selects a file it copies the content of the file
     * in the Notepad.
     * @param isEditable boolean that determines if the notepad can be edited or not. It will be False when we open a file.
     *                   It will be True if we create a file from an existing file.
     */
    public static void openFileChooser(boolean isEditable) {
        switch (window.getFileChooser().showOpenDialog(window)) {
            case JFileChooser.APPROVE_OPTION:
                // Sets the text
                notepad.setNotepadText(reader.getFileContent(window.getFileChooser().getSelectedFile().getAbsolutePath()));
                // Makes it editable
                notepad.setNotepadEditable(isEditable);
                // Check the text for errors
                executor.submit(() -> checkText());
                break;
        }
    }

    /**
     * Removes highlights for found word in text
     */
    public void removeFindWordHighlights() {
        SwingUtilities.invokeLater(() -> notepad.removeFindWordHighlights());
    }

    /**
     * Removes word from sidebar
     * @param w
     */
    public static void removeFromSidebar(Word w) {
        SwingUtilities.invokeLater(() -> {
            window.removeFromSidebar(w);
        });
    }

    @Override
    public void replaceMispelledWordFromText(int idx, int lengthDifference) {
        deleteMispelledWord(idx);
        updateMispelledCursorEnds(idx, lengthDifference);
    }

    /**
     * Method used when Find&Replace is used.
     * @param index index of word start
     * @param lengthPreviousWord length of the previous word
     * @param replacement word to replace with
     * @return difference of letters bewteen last word and replaced word
     */
    public int replaceWord(int index, int lengthPreviousWord, String replacement) {
        int difference = replacement.length() - lengthPreviousWord;
        try {
            updateMispelledCursorEnds(index, difference);
            notepad.getDocument().remove(index, lengthPreviousWord);
            notepad.getDocument().insertString(index, replacement, null);
        } catch (BadLocationException e) {
        }
        return difference;
    }

    /**
     * Replaces all ocurrences of the oldWord with the newWord
     * @param old string to replace
     * @param newWord string to replace with
     */
    public void replaceWords(String old, String newWord) {
        notepad.setText(notepad.getText().replace(old, newWord));
    }

    /**
     * Resets the necessary variables used for the control of the Notepad
     * @param isEditable
     */
    public static void resetNotepad(boolean isEditable) {
        notepad.setText("");
        notepad.setNotepadEditable(isEditable);
        window.updateStatusText("", null);
        mispelledWordsCursorEnd.clear();
        mispelledWords.clear();
        window.resetSidebar();
    }

    /**
     * Used to move cursor to the desired occurrence of the word to find
     * @param currentIndex I
     */
    public void setCursorPosInNotepad(int currentIndex) {
        SwingUtilities.invokeLater(() -> window.setCaretPosition(currentIndex));
    }

    /**
     * Sets the selected Language by the user
     * @param selectedLanguage
     */
    @Override
    public void setSelectedLanguage(Language selectedLanguage) {
        mispelledWords.clear();
        SwingUtilities.invokeLater(() -> {
            notepad.removeHighlights();
            window.setSelectedLanguageLabel(selectedLanguage.getName().substring(0, 1).toUpperCase() + selectedLanguage.getName().substring(1), selectedLanguage.getIcon());
        });
        // We change the Dictonary
        if (languageDictionary.get(selectedLanguage.getName()) == null) {
            if (Constants.SOUNDEX_DICTIONARIES.contains(selectedLanguage.getName())) {
                languageDictionary.put(selectedLanguage.getName(), importSoundexDicctionary(dictionaryPath.get(selectedLanguage.getName())));
                isSoundexDictionary = true;

            } else {
                languageDictionary.put(selectedLanguage.getName(), importDicctionary(dictionaryPath.get(selectedLanguage.getName())));
                isSoundexDictionary = false;

            }
        }
        dictionary = languageDictionary.get(selectedLanguage.getName());
        // Check the text for mispelled words
        executor.submit(() -> checkText());
    }

    /**
     * Used to activate/deactivate the suggestion option
     */
    public static void toggleSuggestions() {
        suggestionsEnabled = !suggestionsEnabled;
    }

    /**
     * Updates the indexes of the mispelled words after one mispelled word is removed. The mispelled words to update are
     * only the ones that come after the removed word.
     * @param idx index of the removed word
     * @param lengthDifference offset to add to the indexes
     */
    public static void updateMispelledCursorEnds(int idx, int lengthDifference) {
        // If there is a difference bewteen the old and new word we update the indexes
        if (Math.abs(lengthDifference) != 0) {
            HashMap<Integer, Word> mispelledWordsCursorEndAux = new HashMap<>(mispelledWordsCursorEnd);
            int newCursorEnd;
            // We check every mispelled word
            for (int cursorEnd : mispelledWordsCursorEnd.keySet()) {
                // If its index is greater than the one of the deleted word we update it
                if (cursorEnd > idx) {
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

    /**
     * Updates the display of the number of mispelled words
     */
    private static void updateMispelledWordsCount() {
        if (mispelledWords.size() == 0) {
            window.updateStatusText("No errors in text", new ImageIcon(Constants.PATH_CORRECT_ICON));

        } else {
            window.updateStatusText(mispelledWords.size() + " mispelled words in text", new ImageIcon(Constants.PATH_INCORRECT_ICON));
        }
    }

    //region SETTERS & GETTERS
    public static void enableNotepad(boolean isEditable) {
        notepad.setNotepadEditable(isEditable);
    }

    public static void enableFindPanel(boolean enabled) {
        findPanel.setVisible(enabled);
        findPanel.enableFindPanel(enabled);
    }

    public static void enableFindReplacePanel(boolean enabled) {
        findPanel.setVisible(enabled);
        findPanel.enableFindReplacePanel(enabled);
    }

    public static void enableSidebarPanel(boolean enabled) {
        sidebar.setVisible(enabled);
        if (enabled) {
            notepad.setSize(Constants.DIM_NOTEPAD);
            notepad.setPreferredSize(Constants.DIM_NOTEPAD);
        } else {
            notepad.setSize(Constants.DIM_WINDOW);
            notepad.setPreferredSize(Constants.DIM_WINDOW);
        }
    }

    public HashMap<Integer, Word> getMispelledWordsCursorEnd() {
        return mispelledWordsCursorEnd;
    }

    public void increaseDistance() {
        distance++;
    }

    public boolean isSoundexDictionary() {
        return isSoundexDictionary;
    }

    public static boolean isSuggestionUsed() {
        return suggestionUsed;
    }

    public void resetDistance() {
        distance = 1;
    }

    public void setFindPanel(FindPanel findPanel) {
        this.findPanel = findPanel;
    }

    /**
     * Creates Notepad element and adds the listeners for the dropdown suggestion and replacements elements
     * @param notepad
     */
    public void setNotepad(Notepad notepad) {
        this.notepad = notepad;
        SuggestionDropDownDecorator.decorate(notepad, new TextComponentWordSuggestionClient(Controller::getWords));
        SuggestionDropDownDecorator.decorate(notepad, new TextComponentWordReplace(Controller::getReplaceWords, this), this);
    }

    public void setSidebar(Sidebar sidebar) {
        this.sidebar = sidebar;
    }

    public static void setSuggestionUsed(boolean suggestionUsed) {
        Controller.suggestionUsed = suggestionUsed;
    }

    public void setWindow(Window window) {
        this.window = window;
    }
    //endregion
}
