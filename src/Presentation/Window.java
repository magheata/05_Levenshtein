/**
 * AUTHORS: Rafael Adrián Gil Cañestro
 * Miruna Andreea Gheata
 */
package Presentation;

import Application.Controller;
import Domain.Language;
import Domain.Word;
import Utils.Constants;
import Utils.DocumentSizeFilter;
import Utils.MenuBuilder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
    static final int MAX_CHARACTERS = Integer.MAX_VALUE;
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
        initNotepadPanel();

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

        JPanel outerWrapper = new JPanel();
        outerWrapper.setLayout(new BorderLayout());
        outerWrapper.add(sideBarPanel, BorderLayout.EAST);
        outerWrapper.add(scrollPane, BorderLayout.WEST);
        outerWrapper.add(wrapperNotepad, BorderLayout.NORTH);

        this.add(findPanel, BorderLayout.PAGE_START);
        this.add(outerWrapper, BorderLayout.CENTER);
        this.add(statusPane, BorderLayout.PAGE_END);
        this.setJMenuBar(createMenuBar());
        this.setVisible(true);
        this.pack();
    }

    private void initNotepadPanel(){
        notepadPanel = new Notepad(controller);
        notepadPanel.setEditable(false);
        controller.setNotepad(notepadPanel);
        // We add the undo and redo listeners
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
        // Listener for when the Notepad is written on and modified
        doc.addUndoableEditListener(new MenuBuilder.MyUndoableEditListener());
    }

    //region Model Manipulation
    public void addToSidebar(Word w){
        sideBarPanel.addToSidebar(w);
    }

    public void resetSidebar(){
        sideBarPanel.resetModel();
    }

    public void removeFromSidebar(Word word){
        sideBarPanel.removeFromSidebar(word);
    }
    //endregion

    /**
     * Methos used to create the Menu
     * @return
     */
    private JMenuBar createMenuBar(){
        JMenuBar menuBar = new JMenuBar();
        for (String menu : MenuBuilder.MENU_ITEMS_ORDER){
            JMenu newMenu = new JMenu(menu);
            for (String item : MenuBuilder.MAP_MENU_ITEMS.get(menu)){
                // If it's a submenu it's the Languages menu so we add the languages
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
        scrollPane.getVerticalScrollBar().setValue(currentIndex);
    }

    //region UndoManager
    private void createActionTable(JTextComponent textComponent) {
         actions = new HashMap<>();
        Action[] actionsArray = textComponent.getActions();
        for (int i = 0; i < actionsArray.length; i++) {
            Action a = actionsArray[i];
            actions.put(a.getValue(Action.NAME), a);
        }
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
