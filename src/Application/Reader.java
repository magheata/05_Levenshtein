/**
 * AUTHORS: Rafael Adrián Gil Cañestro
 * Miruna Andreea Gheata
 */
package Application;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class used in order to read files.
 */
public class Reader {

    /**
     * Reads a dictionary file
     * @param path path to the dicc file
     * @return list containing all the words from the file
     */
    public ArrayList<String> readDicc(String path) {
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

    /**
     * Reads the content of the file and returns it
     * @param path path to the file
     * @return string containing text of the file
     */
    public StringBuilder getFileContent(String path) {
        StringBuilder sb = new StringBuilder();
        try {
            Scanner scanner = new Scanner(new File(path));
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
                sb.append("\n");
            }
            scanner.close();
        } catch (IOException ex) {
        }
        return sb;
    }
}
