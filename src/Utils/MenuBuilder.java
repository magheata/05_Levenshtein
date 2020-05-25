/**
 * AUTHORS: Rafael Adrián Gil Cañestro
 * Miruna Andreea Gheata
 */
package Utils;

import Application.Controller;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.*;

public class MenuBuilder {

    protected static UndoManager undo = new UndoManager();
    protected static UndoAction undoAction = new UndoAction();
    protected static RedoAction redoAction = new RedoAction();
    public static Controller controller;

    public MenuBuilder(Controller controller){
        this.controller = controller;
    }
    public static final String [] MENU_ITEMS_ORDER = new String[] {
            Constants.TEXT_FILE_MENU,
            Constants.TEXT_EDIT_MENU,
            Constants.TEXT_VIEW_MENU,
            Constants.TEXT_TOOLS_MENU,
            Constants.TEXT_PREFERENCES_MENU,
            Constants.TEXT_LANGUAGE_SUBMENU };

    public static final ArrayList<String> IS_SUBMENU = new ArrayList<>(Arrays.asList(Constants.TEXT_LANGUAGE_SUBMENU));

    public static final ArrayList<String> ADD_SEPARATION_AFTER = new ArrayList<>(Arrays.asList(
            Constants.TEXT_NEW_FROM_EXISTING_ITEM,
            Constants.TEXT_SPELLING_ITEM
    ));

    public static final Map<String, ArrayList<String>> MAP_MENU_ITEMS = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(Constants.TEXT_FILE_MENU, new ArrayList<>() {
                {
                    add(Constants.TEXT_NEW_FILE_ITEM);
                    add(Constants.TEXT_NEW_FROM_EXISTING_ITEM);
                    add(Constants.TEXT_OPEN_FILE_ITEM);
                }
            }
            ),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_EDIT_MENU, new ArrayList<>() {
                {
                    add(Constants.TEXT_UNDO_ITEM);
                    add(Constants.TEXT_REDO_ITEM);
                }
            }
            ),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_VIEW_MENU, new ArrayList<>() {
                {
                    add(Constants.TEXT_HIDE_PANEL_ITEM);
                }
            }
            ),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_TOOLS_MENU, new ArrayList<>() {
                {
                    add(Constants.TEXT_ENABLE_EDIT_ITEM);
                    add(Constants.TEXT_SPELLING_ITEM);
                    add(Constants.TEXT_FIND_WORD_ITEM);
                    add(Constants.TEXT_FIND_REPLACE_WORD_ITEM);
                }
            }
            ),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_PREFERENCES_MENU, new ArrayList<>() {
                {
                    add(Constants.TEXT_ENABLE_SUGGESTIONS_ITEM);
                    add(Constants.TEXT_LANGUAGE_SUBMENU);
                }
            }
            ),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_LANGUAGE_SUBMENU, new ArrayList<>() {
                {
                    add("español");
                    add("english");
                }
            }
            )
    );

    public static final Map<String, String> KEYSTROKES = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(Constants.TEXT_UNDO_ITEM, Constants.KEYSTROKE_UNDO),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_REDO_ITEM, Constants.KEYSTROKE_REDO),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_NEW_FILE_ITEM, Constants.KEYSTROKE_NEW_FILE),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_OPEN_FILE_ITEM, Constants.KEYSTROKE_OPEN_EXISTING_FILE),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_ENABLE_SUGGESTIONS_ITEM, Constants.KEYSTROKE_SUGGESTIONS),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_DISABLE_SUGGESTIONS_ITEM, Constants.KEYSTROKE_SUGGESTIONS),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_SPELLING_ITEM, Constants.KEYSTROKE_SPELLING),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_FIND_WORD_ITEM, Constants.KEYSTROKE_FIND),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_HIDE_PANEL_ITEM, Constants.KEYSTROKE_MISPELLED_PANEL),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_SHOW_PANEL_ITEM, Constants.KEYSTROKE_MISPELLED_PANEL),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_ENABLE_EDIT_ITEM, Constants.KEYSTROKE_EDIT),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_DISABLE_EDIT_ITEM, Constants.KEYSTROKE_EDIT)
    );

    public static final Map<String, String> MENU_ICONS = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(Constants.TEXT_UNDO_ITEM, Constants.PATH_UNDO_ICON),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_REDO_ITEM, Constants.PATH_REDO_ICON),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_NEW_FILE_ITEM, Constants.PATH_NEW_FILE_ICON),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_NEW_FROM_EXISTING_ITEM, Constants.PATH_NEW_FROM_EXISTING_FILE_ICON),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_OPEN_FILE_ITEM, Constants.PATH_OPEN_FILE_ICON),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_ENABLE_SUGGESTIONS_ITEM, Constants.PATH_SUGGESTIONS_ICON),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_DISABLE_SUGGESTIONS_ITEM, Constants.PATH_SUGGESTIONS_ICON),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_ENABLE_EDIT_ITEM, Constants.PATH_EDIT_FILE_ICON),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_LANGUAGE_SUBMENU, Constants.PATH_LANGUAGE_ICON),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_SPELLING_ITEM, Constants.PATH_SPELLING_ICON),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_FIND_WORD_ITEM, Constants.PATH_FIND_ICON),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_FIND_REPLACE_WORD_ITEM, Constants.PATH_FIND_REPLACE_ICON),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_HIDE_PANEL_ITEM, Constants.PATH_PANEL_ICON),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_SHOW_PANEL_ITEM, Constants.PATH_PANEL_ICON)
    );

    public static final Map<String, JMenuItem> MENU_ITEMS = new HashMap<>();


    public static final Map<String, ActionListener> MENU_ACTIONLISTENERS = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(Constants.TEXT_UNDO_ITEM, undoAction),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_REDO_ITEM, redoAction),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_NEW_FILE_ITEM, e -> controller.resetNotepad(true)),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_NEW_FROM_EXISTING_ITEM, e -> controller.openFileChooser(true)),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_OPEN_FILE_ITEM, e -> controller.openFileChooser(false)),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_ENABLE_SUGGESTIONS_ITEM, e -> {
                if (MENU_ITEMS.get(Constants.TEXT_ENABLE_SUGGESTIONS_ITEM).getText().equals(Constants.TEXT_ENABLE_SUGGESTIONS_ITEM)){
                    MENU_ITEMS.get(Constants.TEXT_ENABLE_SUGGESTIONS_ITEM).setText(Constants.TEXT_DISABLE_SUGGESTIONS_ITEM);
                } else {
                    MENU_ITEMS.get(Constants.TEXT_ENABLE_SUGGESTIONS_ITEM).setText(Constants.TEXT_ENABLE_SUGGESTIONS_ITEM);
                }
                controller.toggleSuggestions();
            }),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_DISABLE_SUGGESTIONS_ITEM, e -> {
                if (MENU_ITEMS.get(Constants.TEXT_DISABLE_SUGGESTIONS_ITEM).getText().equals(Constants.TEXT_ENABLE_SUGGESTIONS_ITEM)){
                    MENU_ITEMS.get(Constants.TEXT_DISABLE_SUGGESTIONS_ITEM).setText(Constants.TEXT_DISABLE_SUGGESTIONS_ITEM);
                } else {
                    MENU_ITEMS.get(Constants.TEXT_DISABLE_SUGGESTIONS_ITEM).setText(Constants.TEXT_ENABLE_SUGGESTIONS_ITEM);
                }
                controller.toggleSuggestions();
            }),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_SPELLING_ITEM, e -> controller.correctSpellingFromText()),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_FIND_WORD_ITEM, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    controller.enableFindPanel(true);
                }
            }),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_FIND_REPLACE_WORD_ITEM, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    controller.enableFindReplacePanel(true);
                }
            }),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_HIDE_PANEL_ITEM, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (MENU_ITEMS.get(Constants.TEXT_HIDE_PANEL_ITEM).getText().equals(Constants.TEXT_HIDE_PANEL_ITEM)){
                        MENU_ITEMS.get(Constants.TEXT_HIDE_PANEL_ITEM).setText(Constants.TEXT_SHOW_PANEL_ITEM);
                        controller.enableSidebarPanel(false);
                    } else {
                        MENU_ITEMS.get(Constants.TEXT_HIDE_PANEL_ITEM).setText(Constants.TEXT_HIDE_PANEL_ITEM);
                        controller.enableSidebarPanel(true);
                    }
                }
            }),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_SHOW_PANEL_ITEM, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (MENU_ITEMS.get(Constants.TEXT_SHOW_PANEL_ITEM).getText().equals(Constants.TEXT_SHOW_PANEL_ITEM)){
                        MENU_ITEMS.get(Constants.TEXT_SHOW_PANEL_ITEM).setText(Constants.TEXT_HIDE_PANEL_ITEM);
                        controller.enableSidebarPanel(true);
                    } else {
                        MENU_ITEMS.get(Constants.TEXT_SHOW_PANEL_ITEM).setText(Constants.TEXT_SHOW_PANEL_ITEM);
                        controller.enableSidebarPanel(false);
                    }
                }
            }),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_ENABLE_EDIT_ITEM, e -> {
                if (MENU_ITEMS.get(Constants.TEXT_ENABLE_EDIT_ITEM).getText().equals(Constants.TEXT_ENABLE_EDIT_ITEM)){
                    MENU_ITEMS.get(Constants.TEXT_ENABLE_EDIT_ITEM).setText(Constants.TEXT_DISABLE_EDIT_ITEM);
                    controller.enableNotepad(true);
                } else {
                    MENU_ITEMS.get(Constants.TEXT_ENABLE_EDIT_ITEM).setText(Constants.TEXT_ENABLE_EDIT_ITEM);
                    controller.enableNotepad(false);
                    controller.checkText();
                }
            }),
            new AbstractMap.SimpleEntry<>(Constants.TEXT_DISABLE_EDIT_ITEM, e -> {
                if (MENU_ITEMS.get(Constants.TEXT_DISABLE_EDIT_ITEM).getText().equals(Constants.TEXT_ENABLE_EDIT_ITEM)){
                    MENU_ITEMS.get(Constants.TEXT_DISABLE_EDIT_ITEM).setText(Constants.TEXT_DISABLE_EDIT_ITEM);
                    controller.enableNotepad(true);
                } else {
                    MENU_ITEMS.get(Constants.TEXT_DISABLE_EDIT_ITEM).setText(Constants.TEXT_ENABLE_EDIT_ITEM);
                    controller.enableNotepad(false);
                    controller.checkText();
                }
            })
    );

    public static final Map<String, Integer> KEYEVENTS = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(Constants.KEYSTROKE_UNDO, KeyEvent.VK_Z),
            new AbstractMap.SimpleEntry<>(Constants.KEYSTROKE_REDO, KeyEvent.VK_Y),
            new AbstractMap.SimpleEntry<>(Constants.KEYSTROKE_NEW_FILE, KeyEvent.VK_N),
            new AbstractMap.SimpleEntry<>(Constants.KEYSTROKE_OPEN_EXISTING_FILE, KeyEvent.VK_O),
            new AbstractMap.SimpleEntry<>(Constants.KEYSTROKE_SUGGESTIONS, KeyEvent.VK_S),
            new AbstractMap.SimpleEntry<>(Constants.KEYSTROKE_SPELLING, KeyEvent.VK_R),
            new AbstractMap.SimpleEntry<>(Constants.KEYSTROKE_FIND, KeyEvent.VK_F),
            new AbstractMap.SimpleEntry<>(Constants.KEYSTROKE_MISPELLED_PANEL, KeyEvent.VK_M),
            new AbstractMap.SimpleEntry<>(Constants.KEYSTROKE_CARET, KeyEvent.VK_K),
            new AbstractMap.SimpleEntry<>(Constants.KEYSTROKE_EDIT, KeyEvent.VK_E)
    );

    public static class UndoAction extends AbstractAction {
        public UndoAction() {
            super("Undo");
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                undo.undo();
                controller.checkText();
            } catch (CannotUndoException ex) {
            }
            updateUndoState();
            redoAction.updateRedoState();
        }

        protected void updateUndoState() {
            if (undo.canUndo()) {
                setEnabled(true);
                putValue(Action.NAME, undo.getUndoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Undo");
            }
        }
    }

    public static class RedoAction extends AbstractAction {
        public RedoAction() {
            super("Redo");
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                undo.redo();
                controller.checkText();
            } catch (CannotRedoException ex) {

            }
            updateRedoState();
            undoAction.updateUndoState();
        }

        protected void updateRedoState() {
            if (undo.canRedo()) {
                setEnabled(true);
                putValue(Action.NAME, undo.getRedoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Redo");
            }
        }
    }

    public static class MyUndoableEditListener
            implements UndoableEditListener {
        public void undoableEditHappened(UndoableEditEvent e) {
            //Remember the edit and update the menus.
            undo.addEdit(e.getEdit());
            undoAction.updateUndoState();
            redoAction.updateRedoState();
        }
    }
}
