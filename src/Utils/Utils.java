/* Created by andreea on 09/05/2020 */
package Utils;

import java.io.File;
import java.util.ArrayList;

public class Utils {
    public ArrayList<File> listFilesForFolder(File folder) {
        ArrayList<File> files = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.getName().equals(".DS_Store")){
                files.add(fileEntry);
            }
        }
        return files;
    }
}
