/* Created by andreea on 05/05/2020 */

import Application.Controller;
import Infrastructure.Soundex;
import Utils.Constants;
import Utils.MultiMap;

import java.io.IOException;
import java.util.Collection;

public class Main {

    public static void main(String[] args) {
        //Window w = new Window();
        Controller controller = new Controller();
        controller.importDicctionary(Constants.PATH_DICC_ES);
        controller.checkText();
        try {
            controller.populateDict(Constants.PATH_DICC_EN);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String word = "revoluton";

        // search for homophones
        Collection<String> collection = dict.get(Soundex.soundex(word));
        /*MultiMap<Integer, String> sortedWords = new MultiMap();

        // pruning results with Levenshtein Distance
        for (String str: collection) {
            int distance = controller.findWordInDicctionary(word);

            if (distance < 3) {
                sortedWords.put(distance, str);
            }
        }

        System.out.println("Word mispelled: " + word);
        System.out.println("Matching words: ");

        for (Map.Entry<Integer, Collection<String>> entry: sortedWords.entrySet()) {
            int value = entry.getKey();

            for (String str: entry.getValue()) {
                System.out.println(str + " - " + value);
            }
        }*/
    }

    public static MultiMap<String, String> dict = new MultiMap<>();

}
