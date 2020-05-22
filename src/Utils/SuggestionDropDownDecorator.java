/* Created by andreea on 15/05/2020 */
package Utils;

import Application.Controller;
import Domain.Interfaces.ISuggestionClient;
import Domain.Word;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;

public class SuggestionDropDownDecorator <C extends JComponent> {
    private final C invoker;
    private ISuggestionClient<C> client;

    private JPopupMenu popupMenu;
    private JList<String> listComp;
    private DefaultListModel<String> listModel;
    private boolean disableTextEvent;
    private boolean replace;
    private Controller controller;


    public SuggestionDropDownDecorator(C invoker, ISuggestionClient<C> client, Controller controller) {
        this.invoker = invoker;
        this.controller = controller;
        this.client = client;
    }


    public SuggestionDropDownDecorator(C invoker, ISuggestionClient<C> client) {
        this.invoker = invoker;
        this.client = client;
    }

    public static <C extends JComponent> void decorate(C component, ISuggestionClient<C> suggestionClient) {
        SuggestionDropDownDecorator<C> d = new SuggestionDropDownDecorator<>(component, suggestionClient);
        d.initSuggestionCompListener();
        d.initInvokerKeyListeners();
        d.initPopup();
    }

    public static <C extends JComponent> void decorate(C component, ISuggestionClient<C> suggestionClient, Controller controller) {
        SuggestionDropDownDecorator<C> d = new SuggestionDropDownDecorator<>(component, suggestionClient, controller);
        d.initInvokerMouseListener();
        d.initInvokerKeyListeners();
        d.initPopup();
    }

    private void initPopup() {
        popupMenu = new JPopupMenu();
        listModel = new DefaultListModel<>();
        listComp = new JList<>(listModel);
        listComp.setBorder(BorderFactory.createEmptyBorder(0, 2, 5, 2));
        listComp.setFocusable(false);
        popupMenu.setFocusable(false);
        popupMenu.add(listComp);
        listComp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                selectFromList(e);
            }
        });
    }

    private void initSuggestionCompListener() {
        if (invoker instanceof JTextComponent) {
            JTextComponent tc = (JTextComponent) invoker;
            tc.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    update(e);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    update(e);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    update(e);
                }

                private void update(DocumentEvent e) {
                    if (disableTextEvent) {
                        return;
                    }
                    SwingUtilities.invokeLater(() -> {
                        ArrayList<Word> suggestions = client.get(invoker);
                        ArrayList<String> suggestionsList = new ArrayList<>();
                        if (suggestions != null && !suggestions.isEmpty()) {
                            if (!suggestionsList.contains(invoker)){
                                Iterator it = suggestions.iterator();
                                while (it.hasNext()){
                                    Word suggestion = (Word) it.next();
                                    suggestionsList.add(suggestion.getEntry());
                                }
                                showPopup(suggestionsList);
                            }
                        } else {
                            popupMenu.setVisible(false);
                        }
                    });
                }
            });
        }//todo init invoker components other than text components
    }

    private void showPopup(ArrayList<String> suggestions) {
        listModel.clear();
        suggestions.forEach(listModel::addElement);
        Point p = client.getPopupLocation(invoker);
        if (p == null) {
            return;
        }
        popupMenu.pack();
        listComp.setSelectedIndex(0);
        popupMenu.show(invoker, (int) p.getX(), (int) p.getY());
    }

    private void initInvokerKeyListeners() {
        //not using key inputMap cause that would override the original handling
        invoker.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    selectFromList(e);
                } else if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    selectFromList(e);
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    moveUp(e);
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    moveDown(e);
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    popupMenu.setVisible(false);
                }
            }
        });
    }

    private void initInvokerMouseListener(){
        invoker.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int distance = 1;
                ArrayList<Word> replacements = client.get(invoker);
                while (((replacements == null) || (replacements.isEmpty()))  && (distance != Constants.MAX_DISTANCE)){
                    distance++;
                    controller.increaseDistance();
                    replacements = client.get(invoker);
                }
                if (replacements != null && !replacements.isEmpty()) {
                    ArrayList<String> replacementsList = new ArrayList<>();
                    Iterator it = replacements.iterator();
                    while (it.hasNext()){
                        Word suggestion = (Word) it.next();
                        replacementsList.add(suggestion.getEntry());
                    }
                    showPopup(replacementsList);
                } else {
                    popupMenu.setVisible(false);
                }
                controller.resetDistance();
            }
        });
    }

    private void selectFromList(KeyEvent e) {
        if (popupMenu.isVisible()) {
            int selectedIndex = listComp.getSelectedIndex();
            if (selectedIndex != -1) {
                popupMenu.setVisible(false);
                String selectedValue = listComp.getSelectedValue();
                disableTextEvent = true;
                client.setSelectedText(invoker, selectedValue);
                disableTextEvent = false;
                e.consume();
            }
        }
    }

    private void selectFromList(MouseEvent e) {
        if (popupMenu.isVisible()) {
            int selectedIndex = listComp.getSelectedIndex();
            if (selectedIndex != -1) {
                popupMenu.setVisible(false);
                String selectedValue = listComp.getSelectedValue();
                disableTextEvent = true;
                client.setSelectedText(invoker, selectedValue);
                disableTextEvent = false;
                e.consume();
            }
        }
    }

    private void moveDown(KeyEvent keyEvent) {
        if (popupMenu.isVisible() && listModel.getSize() > 0) {
            int selectedIndex = listComp.getSelectedIndex();
            if (selectedIndex < listModel.getSize()) {
                listComp.setSelectedIndex(selectedIndex + 1);
                keyEvent.consume();
            }
        }
    }

    private void moveUp(KeyEvent keyEvent) {
        if (popupMenu.isVisible() && listModel.getSize() > 0) {
            int selectedIndex = listComp.getSelectedIndex();
            if (selectedIndex > 0) {
                listComp.setSelectedIndex(selectedIndex - 1);
                keyEvent.consume();
            }
        }
    }
}
