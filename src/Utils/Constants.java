package Utils;/* Created by andreea on 05/05/2020 */

import Application.Controller;
import Presentation.Window;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.*;

public class Constants {

    public final static int WIDTH_WINDOW = 700;
    public final static int HEIGHT_WINDOW = 750;
    public final static int WIDTH_SIDEBAR = (WIDTH_WINDOW) * 30 / 100;
    public final static int HEIGHT_SIDEBAR = HEIGHT_WINDOW;
    public final static int WIDTH_NOTEPAD = (WIDTH_WINDOW) * 70 / 100;
    public final static int HEIGHT_NOTEPAD = HEIGHT_WINDOW;

    public final static int MAX_DISTANCE = 4;

    public final static Dimension DIM_WINDOW = new Dimension(WIDTH_WINDOW, HEIGHT_WINDOW);
    public final static Dimension DIM_SIDEBAR = new Dimension(WIDTH_SIDEBAR, HEIGHT_SIDEBAR);
    public final static Dimension DIM_NOTEPAD = new Dimension(WIDTH_NOTEPAD, HEIGHT_NOTEPAD);

    public final static String PATH_DICC_ES = "dicc/español.dic";
    public final static String PATH_DICC_EN = "dicc/english.dic";

    public final static String PATH_NEW_FILE_ICON = "src/Presentation/Images/newFile.png";
    public final static String PATH_OPEN_FILE_ICON = "src/Presentation/Images/open.png";
    public final static String PATH_NEW_FROM_EXISTING_FILE_ICON = "src/Presentation/Images/openExisting.png";
    public final static String PATH_UNDO_ICON = "src/Presentation/Images/undo.png";
    public final static String PATH_CUT_ICON = "src/Presentation/Images/cutText.png";
    public final static String PATH_COPY_ICON = "src/Presentation/Images/copyText.png";
    public final static String PATH_PASTE_ICON = "src/Presentation/Images/pasteText.png";
    public final static String PATH_REDO_ICON = "src/Presentation/Images/redo.png";
    public final static String PATH_ADD_FILE_ICON = "src/Presentation/Images/add.png";
    public final static String PATH_EDIT_FILE_ICON = "src/Presentation/Images/edit.png";
    public final static String PATH_SUGGESTIONS_ICON = "src/Presentation/Images/suggestions.png";
    public final static String PATH_LANGUAGE_ICON = "src/Presentation/Images/language.png";
    public final static String PATH_FIND_ICON = "src/Presentation/Images/find.png";
    public final static String PATH_FIND_REPLACE_ICON = "src/Presentation/Images/findAndReplace.png";
    public final static String PATH_SPELLING_ICON = "src/Presentation/Images/spelling.png";
    public final static String PATH_CARET_ICON = "src/Presentation/Images/caret.png";
    public final static String PATH_PANEL_ICON = "src/Presentation/Images/panel.png";
    public final static String PATH_EN_ICON = "src/Presentation/Images/english.png";
    public final static String PATH_ES_ICON = "src/Presentation/Images/español.png";


    public final static String TEXT_FILE_MENU = "File";
    public final static String TEXT_NEW_FILE_ITEM = "New";
    public final static String TEXT_NEW_FROM_EXISTING_ITEM = "New from existing";
    public final static String TEXT_OPEN_FILE_ITEM = "Open...";

    public final static String TEXT_EDIT_MENU = "Edit";
    public final static String TEXT_UNDO_ITEM = "Undo";
    public final static String TEXT_REDO_ITEM = "Redo";
    public final static String TEXT_CUT_ITEM = "Cut";
    public final static String TEXT_COPY_ITEM = "Copy";
    public final static String TEXT_PASTE_ITEM = "Paste";

    public final static String TEXT_PREFERENCES_MENU = "Preferences";
    public final static String TEXT_ENABLE_SUGGESTIONS_ITEM = "Enable suggestions";
    public final static String TEXT_DISABLE_SUGGESTIONS_ITEM = "Disable suggestions";
    public final static String TEXT_LANGUAGE_SUBMENU = "Select language";

    public final static String TEXT_TOOLS_MENU = "Tools";
    public final static String TEXT_SPELLING_ITEM = "Correct spelling";
    public final static String TEXT_ENABLE_EDIT_ITEM = "Edit file";
    public final static String TEXT_FIND_WORD_ITEM = "Find";
    public final static String TEXT_FIND_REPLACE_WORD_ITEM = "Replace...";

    public final static String TEXT_VIEW_MENU = "View";
    public final static String TEXT_SHOW_CARET_ITEM = "Show caret information";
    public final static String TEXT_HIDE_CARET_ITEM = "Hide caret information";
    public final static String TEXT_SHOW_PANEL_ITEM = "Show mispelled words panel";
    public final static String TEXT_HIDE_PANEL_ITEM = "Hide mispelled words panel";

    public final static String KEYSTROKE_FIND = "control F";
    public final static String KEYSTROKE_NEW_FILE = "control N";
    public final static String KEYSTROKE_OPEN_EXISTING_FILE = "control O";
    public final static String KEYSTROKE_SUGGESTIONS = "control S";
    public final static String KEYSTROKE_SPELLING = "control R";
    public final static String KEYSTROKE_MISPELLED_PANEL = "control M";
    public final static String KEYSTROKE_CARET = "control K";
    public final static String KEYSTROKE_UNDO = "control Z";
    public final static String KEYSTROKE_REDO = "control Y";
    public final static String KEYSTROKE_CUT = "control X";
    public final static String KEYSTROKE_COPY = "control C";
    public final static String KEYSTROKE_PASTE = "control V";

    public final static ArrayList<Character> SYMBOLS = new ArrayList<>(Arrays.asList('.', ':', '!', '¡', '?', '¿', ',', ';',
            '&', '(', ')', '=', '#', '+', '-', '_', '*', '@', '{', '<', '>', '}', ' ', '\n'));

    public final static String SYMBOLS_STRING = "[\\.:!¡?¿,;&()=#+\\-_*@{}<>\\ \\\n]";
    public final static ArrayList<String> SOUNDEX_DICTIONARIES = new ArrayList<>(Arrays.asList("english"));
}
