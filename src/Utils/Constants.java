package Utils;/* Created by andreea on 05/05/2020 */

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Constants {

    public final static int WIDTH_WINDOW = 700;
    public final static int HEIGHT_WINDOW = 750;
    public final static int WIDTH_SIDEBAR = 200;
    public final static int HEIGHT_SIDEBAR = 750;
    public final static int WIDTH_NOTEPAD = 500;
    public final static int HEIGHT_NOTEPAD = 750;

    public final static Dimension DIM_WINDOW = new Dimension(WIDTH_WINDOW, HEIGHT_WINDOW);
    public final static Dimension DIM_SIDEBAR = new Dimension(WIDTH_SIDEBAR, HEIGHT_SIDEBAR);
    public final static Dimension DIM_NOTEPAD = new Dimension(WIDTH_NOTEPAD, HEIGHT_NOTEPAD);

    public final static String PATH_DICC_ES = "dicc/español.dic";
    public final static String PATH_DICC_EN = "dicc/english.dic";

    public final static String PATH_ADD_FILE_ICON = "src/Presentation/Images/add.png";
    public final static String PATH_OPEN_FILE_ICON = "src/Presentation/Images/folder.png";
    public final static String PATH_EN_ICON = "src/Presentation/Images/english.png";
    public final static String PATH_ES_ICON = "src/Presentation/Images/español.png";

    public final static ArrayList<Character> SYMBOLS = new ArrayList<>(Arrays.asList('.', ':', '!', '¡', '?', '¿', ',', ';',
            '&', '(', ')', '=', '#', '+', '-', '_', '*', '@', '{', '<', '>', '}', ' ', '\n'));

    public final static String SYMBOLS_STRING = "[\\.:!¡?¿,;&()=#+\\-_*@{}<>\\ \\\n]";
    public final static ArrayList<String> SOUNDEX_DICTIONARIES = new ArrayList<>(Arrays.asList("english"));
}
