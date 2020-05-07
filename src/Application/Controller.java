/* Created by andreea on 05/05/2020 */
package Application;

import Domain.Dictionary;
import Domain.Word;
import Infrastructure.Soundex;
import Infrastructure.SpellChecker;
import Utils.MultiMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Controller {

    private Reader reader;
    private SpellChecker spellChecker;

    private Dictionary dictionary;

    private MultiMap<String, String> dict = new MultiMap<>();

    public Controller(){
        reader = new Reader();
        spellChecker = new SpellChecker();
    }

    public void importDicctionary(String path){
        dictionary = new Dictionary(path);
        dictionary.setEntries(reader.readDicc(path));
    }
    
    public boolean findWordInDicctionary(Word wordToFind){
        for (Word word: dictionary.getEntries()) {
            int distance = spellChecker.levenshtein(wordToFind.getEntry(), word.getEntry());
            if (distance == 0){
                return true;
            } else {
                if (!wordToFind.replaceWordsInitialized()){
                    for (int i = 1; i <= 2; i++){
                        wordToFind.addDistance(i);
                    }
                }
                if ((distance < 3)){
                    wordToFind.addReplaceWord(new Word(distance, word.getEntry()));
                }
            }
        }
        return false;
    }

    public void checkText(){
        ArrayList<String> wordsInText = reader.readFile("examples/prueba.txt");
        Word word;
        for (String wordToFind: wordsInText) {
            word = new Word(wordToFind);
            if (findWordInDicctionary(word)){
                System.out.println("Word \""+ wordToFind+ "\" exists");
            } else {
                System.out.println("Word \"" + wordToFind + "\" could not be found. Maybe you meant: ");
                for (Word replaceWord: word.getReplaceWords(1)) {
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
}
