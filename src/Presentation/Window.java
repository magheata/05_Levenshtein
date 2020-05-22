/* Created by andreea on 05/05/2020 */
package Presentation;

import Application.Controller;
import Domain.Language;
import Domain.Word;
import Utils.Constants;
import Utils.DocumentSizeFilter;
import Utils.MenuBuilder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;

public class Window extends JFrame {

    private Controller controller;
    private Sidebar sideBarPanel;
    private Notepad notepadPanel;
    private FindPanel findPanel;
    private JFileChooser fileChooser;
    private JScrollPane scrollPane;
    private static HashMap<Object, Action> actions;
    //undo helpers
    static final int MAX_CHARACTERS = 300;
    private static JLabel languageSelectedLabel, statusTextLabel;

    public Window(Controller controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());
        this.setSize(Constants.DIM_WINDOW);
        this.setPreferredSize(Constants.DIM_WINDOW);
        this.setMinimumSize(Constants.DIM_WINDOW);
        this.setResizable(false);
        //Create the status area.
        JPanel statusPane = new JPanel(new GridLayout(1, 1));
        CaretListenerLabel caretListenerLabel = new CaretListenerLabel("Caret Status");
        statusPane.add(caretListenerLabel);

        initNotepadPanel(caretListenerLabel);

        JPanel wrapperNotepad = new JPanel();
        wrapperNotepad.setLayout(new BorderLayout());
        wrapperNotepad.setVisible(true);
        wrapperNotepad.setPreferredSize(new Dimension(Constants.WIDTH_WINDOW, 30));
        wrapperNotepad.setSize(new Dimension(Constants.WIDTH_WINDOW, 60));

        statusTextLabel = new JLabel();
        statusTextLabel.setBorder(new EmptyBorder(10, 10, 10, 20));
        wrapperNotepad.add(statusTextLabel, BorderLayout.EAST);

        languageSelectedLabel = new JLabel("Español");
        languageSelectedLabel.setIcon(new ImageIcon(Constants.PATH_ES_ICON));
        languageSelectedLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

        wrapperNotepad.add(languageSelectedLabel, BorderLayout.WEST);

        fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
        fileChooser.setFileFilter(new FileNameExtensionFilter("txt", "txt"));

        sideBarPanel = new Sidebar(controller);

        findPanel = new FindPanel(controller);
        controller.setFindPanel(findPanel);

        scrollPane = new JScrollPane(notepadPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        this.add(findPanel, BorderLayout.PAGE_START);
        JPanel outerWrapper = new JPanel();
        outerWrapper.setLayout(new BorderLayout());
        outerWrapper.add(sideBarPanel, BorderLayout.EAST);
        outerWrapper.add(scrollPane, BorderLayout.WEST);
        this.add(outerWrapper, BorderLayout.CENTER);
        this.add(statusPane, BorderLayout.PAGE_END);
        outerWrapper.add(wrapperNotepad, BorderLayout.NORTH);
        this.setJMenuBar(createMenuBar());
        this.setVisible(true);
        this.pack();
    }

    private void initNotepadPanel(CaretListenerLabel caretListenerLabel){
        notepadPanel = new Notepad(controller);
        notepadPanel.setEditable(false);
        controller.setNotepad(notepadPanel);
        notepadPanel.addCaretListener(caretListenerLabel);
        // Add undo/redo actions
        notepadPanel.getActionMap().put("UNDO_ACTION", new MenuBuilder.UndoAction());
        notepadPanel.getActionMap().put("REDO_ACTION", new MenuBuilder.RedoAction());
        createActionTable(notepadPanel);
        StyledDocument styledDoc = notepadPanel.getStyledDocument();
        AbstractDocument doc = null;
        if (styledDoc instanceof AbstractDocument) {
            doc = (AbstractDocument) styledDoc;
            doc.setDocumentFilter(new DocumentSizeFilter(MAX_CHARACTERS));
        } else {
            System.err.println("Text pane's document isn't an AbstractDocument!");
            System.exit(-1);
        }
        doc.addUndoableEditListener(new MenuBuilder.MyUndoableEditListener());
    }

    //region Model Manipulation
    public void addToModel(Word w){
        sideBarPanel.addToModel(w);
    }

    public void resetModel(){
        sideBarPanel.resetModel();
    }

    public void removeFromModel(Word word){
        sideBarPanel.removeFromModel(word);
    }
    //endregion

    private JMenuBar createMenuBar(){
        JMenuBar menuBar = new JMenuBar();
        for (String menu : MenuBuilder.MENU_ITEMS_ORDER){
            JMenu newMenu = new JMenu(menu);
            for (String item : MenuBuilder.MAP_MENU_ITEMS.get(menu)){
                if (MenuBuilder.IS_SUBMENU.contains(item)){
                    JMenu subMenuItem = new JMenu(item);
                    for (String subItem : MenuBuilder.MAP_MENU_ITEMS.get(item)){
                        for (Language language : controller.getLanguages()){
                            if (language.getName().equals(subItem)){
                                JMenuItem langItem = new JMenuItem(language.getName().substring(0, 1).toUpperCase() + language.getName().substring(1));
                                langItem.setIcon(language.getIcon());
                                langItem.addActionListener(e -> controller.setSelectedLanguage(language));
                                subMenuItem.add(langItem);
                            }
                        }
                    }
                    newMenu.add(subMenuItem);
                } else {
                    JMenuItem menuItem;
                    String keystroke = MenuBuilder.KEYSTROKES.get(item);
                    if ((keystroke!= null) && (keystroke != "")){
                        menuItem = new JMenuItem(item, MenuBuilder.KEYEVENTS.get(keystroke));
                        menuItem.setAccelerator(KeyStroke.getKeyStroke(keystroke));
                    } else {
                        menuItem = new JMenuItem(item);
                    }
                    menuItem.setIcon(new ImageIcon(MenuBuilder.MENU_ICONS.get(item)));
                    MenuBuilder.MENU_ITEMS.put(item, menuItem);
                    menuItem.addActionListener(MenuBuilder.MENU_ACTIONLISTENERS.get(item));

                    newMenu.add(menuItem);
                }
                if (MenuBuilder.ADD_SEPARATION_AFTER.contains(item)){
                    newMenu.addSeparator();
                }
            }
            if (!MenuBuilder.IS_SUBMENU.contains(menu)){
                menuBar.add(newMenu);
            }
        }
        return menuBar;
    }

    public void setCaretPosition(int currentIndex) {
        notepadPanel.setCaretPosition(currentIndex);
        scrollPane.getVerticalScrollBar().setValue(currentIndex);
    }

    //region Caret
    //This listens for and reports caret movements.
    protected class CaretListenerLabel extends JLabel
            implements CaretListener {
        public CaretListenerLabel(String label) {
            super(label);
        }

        //Might not be invoked from the event dispatch thread.
        public void caretUpdate(CaretEvent e) {
            displaySelectionInfo(e.getDot(), e.getMark());
        }

        //This method can be invoked from any thread.  It
        //invokes the setText and modelToView methods, which
        //must run on the event dispatch thread. We use
        //invokeLater to schedule the code for execution
        //on the event dispatch thread.
        protected void displaySelectionInfo(final int dot,
                                            final int mark) {
            SwingUtilities.invokeLater(() -> {
                if (dot == mark) {  // no selection
                    try {
                        Rectangle caretCoords = notepadPanel.modelToView(dot);
                        //Convert it to view coordinates.
                        setText("caret: text position: " + dot
                                + "\n");
                    } catch (BadLocationException ble) {
                        setText("caret: text position: " + dot + "\n");
                    }
                } else if (dot < mark) {
                    setText("selection from: " + dot
                            + " to " + mark + "\n");
                } else {
                    setText("selection from: " + mark
                            + " to " + dot + "\n");
                }
            });
        }
    }
    //endregion

    //region UndoManager
    //This one listens for edits that can be undone.

    //The following two methods allow us to find an
    //action provided by the editor kit by its name.
    private void createActionTable(JTextComponent textComponent) {
         actions = new HashMap<>();
        Action[] actionsArray = textComponent.getActions();
        for (int i = 0; i < actionsArray.length; i++) {
            Action a = actionsArray[i];
            actions.put(a.getValue(Action.NAME), a);
        }
    }

    public static Action getActionByName(String name) {
        return actions.get(name);
    }

    //endregion

    //Add a couple of emacs key bindings for navigation.
    public JFileChooser getFileChooser() {
        return fileChooser;
    }

    public static void setSelectedLanguageLabel(String language, ImageIcon icon){
        languageSelectedLabel.setText(language);
        languageSelectedLabel.setIcon(icon);
    }

    public static void updateStatusText(String text, ImageIcon icon){
        statusTextLabel.setText(text);
        statusTextLabel.setIcon(icon);
    }
}
