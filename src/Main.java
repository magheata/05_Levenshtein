/**
 * AUTHORS: Rafael Adrián Gil Cañestro
 * Miruna Andreea Gheata
 */

import Application.Controller;
import Presentation.Window;
import Utils.Constants;

public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.setWindow(new Window(controller));
        controller.importDicctionary(Constants.PATH_DICC_ES);
    }
}
