/* Created by andreea on 05/05/2020 */

import Application.Controller;
import Presentation.Window;
import Utils.Constants;

public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.setWindow(new Window(controller));
        controller.importDicctionary(Constants.PATH_DICC_ES);
        /*try {
            controller.populateDict(Constants.PATH_DICC_EN);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String word = "revoluton";

        controller.importSoundexDicctionary(Constants.PATH_DICC_EN);
        // search for homophones
        Word wordToFind = new Word(word, true);
        // pruning results with Levenshtein Distance
        controller.findWordInDicctionary(wordToFind);

        System.out.println("Word mispelled: " + word);
        System.out.println("Matching words: ");

        for (Word entry: wordToFind.getReplaceWords(2)) {
            System.out.println(entry.getEntry());
        }*/
    }
}
