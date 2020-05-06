/* Created by andreea on 05/05/2020 */
package Application;

import Infrastructure.SpellChecker;

import java.util.ArrayList;
import java.util.HashMap;

public class Controller {

    private Reader reader;
    private SpellChecker spellChecker;
    private ArrayList<String> dictionary;
    private ArrayList<String> replaceWords;

    private HashMap<String, HashMap<Integer, ArrayList<String>>> replaceWordsFor = new HashMap<>();

    public Controller(){
        reader = new Reader();
        spellChecker = new SpellChecker();
    }

    public void importDicctionary(String path){
        dictionary = reader.readFile(path);
    }
    
    public boolean findWordInDicctionary(String wordToFind){
        replaceWords = new ArrayList<>();
        for (String word: dictionary) {
            int distance = spellChecker.levenshtein(wordToFind, word);
            if (distance == 0){
                return true;
            } else {
                if (!replaceWordsFor.containsKey(wordToFind)){
                    HashMap<Integer, ArrayList<String>> replaceWords = new HashMap<>();
                    for (int i = 1; i <= 2; i++){
                        replaceWords.put(i, new ArrayList<>());
                    }
                    replaceWordsFor.put(wordToFind, replaceWords);
                }
                if ((distance < 3)){
                    /*if (word.length() == wordToFind.length()){
                        addReplaceWord(distance, word, wordToFind);
                    }*/
                    addReplaceWord(distance, word, wordToFind);
                }
            }
        }
        return false;
    }

    private void addReplaceWord(int distance, String replaceWord, String wordToFind){
        replaceWordsFor.get(wordToFind).get(distance).add(replaceWord);
    }

    public ArrayList<String> getReplaceWords() {
        return replaceWords;
    }

    public void checkText(){
        ArrayList<String> wordsInText = reader.readFile("examples/prueba.txt");
        for (String wordToFind: wordsInText.get(0).split(" ")) {
            if (findWordInDicctionary(wordToFind.toLowerCase())){
                System.out.println("Word \""+ wordToFind+ "\" exists");
            } else {
                System.out.println("Word \"" + wordToFind + "\" could not be found. Maybe you meant: ");
                for (String replaceWord: replaceWordsFor.get(wordToFind).get(1)) {
                    System.out.println(replaceWord);
                }
                /*System.out.println("-----------------------------------------------------------------");
                for (String replaceWord: replaceWordsFor.get(wordToFind).get(2)) {
                    System.out.println(replaceWord);
                }*/
            }
        }
    }
}
