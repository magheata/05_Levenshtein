/* Created by andreea on 06/05/2020 */
package Application;

import java.io.*;
import java.util.ArrayList;

public class Reader {

    public ArrayList<String> readFile(String path){
        File file = new File(path);
        ArrayList<String> dicc = new ArrayList<>();
        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            String str;
            while ((str = in.readLine()) != null) {
                String[] words = str.split(" ");
                for (String word : words){
                    dicc.add(normalizeWord(word));
                }
            }
            in.close();
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            unsupportedEncodingException.printStackTrace();
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return dicc;
    }


    public ArrayList<String> readDicc(String path){
        File file = new File(path);
        ArrayList<String> dicc = new ArrayList<>();
        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            String str;
            while ((str = in.readLine()) != null) {
                dicc.add(str);
            }
            in.close();
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            unsupportedEncodingException.printStackTrace();
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return dicc;
    }

    private String normalizeWord(String word){
        word = word.replaceAll("[.:!¡?¿,;&()=#+'\\-_*@{<>}]", "");
        return word.toLowerCase();
    }
}
