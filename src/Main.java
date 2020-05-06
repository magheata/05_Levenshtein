/* Created by andreea on 05/05/2020 */

import Application.Controller;
import Utils.Constants;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        //Window w = new Window();
        Controller controller = new Controller();
        controller.importDicctionary(Constants.PATH_DICC_ES);
        ArrayList<String> replaceWords = controller.getReplaceWords();
        controller.checkText();
    }
}
