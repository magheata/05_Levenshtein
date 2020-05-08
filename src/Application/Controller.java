/* Created by andreea on 05/05/2020 */
package Application;

import Domain.Dictionary;
import Domain.SoundexDictionary;
import Domain.Word;
import Infrastructure.Soundex;
import Infrastructure.SpellChecker;
import Presentation.Notepad;
import Presentation.Window;
import Utils.Constants;
import Utils.MultiMap;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class Controller {

    private Reader reader;
    private SpellChecker spellChecker;

    public void setWindow(Window window) {
        this.window = window;
    }

    private Window window;

    public void setNotepad(Notepad notepad) {
        this.notepad = notepad;
    }

    private Notepad notepad;

    private Dictionary dictionary;

    public MultiMap<String, String> getDict() {
        return dict;
    }

    private MultiMap<String, String> dict = new MultiMap<>();

    public Controller() {
        reader = new Reader();
        spellChecker = new SpellChecker();
    }

    public void importDicctionary(String path) {
        dictionary = new Dictionary(path, reader.readDicc(path));
    }

    public void importSoundexDicctionary(String path) {
        dictionary = new SoundexDictionary(path, reader.readDicc(path));
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

    public void checkText() {
        ArrayList<String> wordsInText = reader.readFile("examples/prueba.txt");
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
}
