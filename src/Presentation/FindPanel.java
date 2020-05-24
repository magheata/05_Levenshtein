/**
 * AUTHORS: Rafael Adrián Gil Cañestro
 * Miruna Andreea Gheata
 */
package Presentation;

import Application.Controller;
import Utils.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.Future;

public class FindPanel extends JPanel{

    private static JPanel findTextPanel, replaceTextPanel;

    private Controller controller;

    private JTextField findTextField, replaceTextField;
    private JButton replaceButton, replaceAllButton, closeButton, prevOcurrence, nextOcurrence;
    private JLabel ocurrencesLabel;

    private ArrayList<Integer> indexOcurrences = new ArrayList<>();

    private int currentIndex = 0;

    public FindPanel(Controller controller){
        this.controller = controller;
        initComponents();
    }

    /**
     * Initialization of the element
     */
    private void initComponents() {
        this.setVisible(false);
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(initFindTextPanel());
        this.add(initReplaceTextPanel());
    }

    /**
     * Creates the JPanel used to find the word
     * @return
     */
    private JPanel initFindTextPanel(){
        findTextPanel = new JPanel();
        //region FINDTEXTPANEL SETTINGS
        findTextPanel.setVisible(false);
        findTextPanel.setBackground(Constants.COLOR_LIGHT_GREY);
        findTextPanel.setLayout(new GridBagLayout());
        findTextPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
        findTextPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 2));
        //endregion

        JPanel wrapper = new JPanel();
        wrapper.setBackground(Constants.COLOR_LIGHT_GREY);
        wrapper.setLayout(new GridBagLayout());

        findTextField = new JTextField();
        //region FINDTEXTFIELD SETTINGS
        findTextField.setCaretColor(Color.white);
        findTextField.setBackground(Constants.COLOR_DARK_GREY);
        findTextField.setForeground(Color.white);
        findTextField.setBorder(new EmptyBorder(0, 0, 0, 100));
        findTextField.setSize(Constants.DIM_FIND_TEXT);
        findTextField.setPreferredSize(Constants.DIM_FIND_TEXT);
        findTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                highlightWord();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                highlightWord();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                highlightWord();
            }
        });
        //endregion

        prevOcurrence = new JButton(new ImageIcon(Constants.PATH_UP_ARROW_ICON));
        prevOcurrence.addActionListener(e -> navigateOccurrencesButtonsAction(true));

        nextOcurrence = new JButton(new ImageIcon(Constants.PATH_DOWN_ARROW_ICON));
        nextOcurrence.addActionListener(e -> navigateOccurrencesButtonsAction(false));

        closeButton = new JButton(new ImageIcon(Constants.PATH_CLOSE_ICON));
        ocurrencesLabel = new JLabel();
        ocurrencesLabel.setText("      ");

        setButtonTransparent(prevOcurrence);
        setButtonTransparent(nextOcurrence);
        setButtonTransparent(closeButton);

        //region GRIDBAGCONSTRAINTS
        GridBagConstraints findTextConstraints = new GridBagConstraints();
        findTextConstraints.fill = GridBagConstraints.HORIZONTAL;
        findTextConstraints.gridx = 0;
        findTextConstraints.gridy = 0;
        findTextConstraints.gridwidth = 1;
        findTextConstraints.anchor = GridBagConstraints.LINE_START;

        GridBagConstraints ocurrencesConstraints = new GridBagConstraints();
        ocurrencesConstraints.fill = GridBagConstraints.HORIZONTAL;
        ocurrencesConstraints.gridx = 1;
        ocurrencesConstraints.gridy = 0;
        ocurrencesConstraints.gridwidth = 1;
        ocurrencesConstraints.insets = new Insets(0, 300, 0, 0);

        GridBagConstraints panelConstraints = new GridBagConstraints();
        panelConstraints.fill = GridBagConstraints.HORIZONTAL;
        panelConstraints.gridx = 0;
        panelConstraints.gridy = 0;
        panelConstraints.gridwidth = 2;
        panelConstraints.anchor = GridBagConstraints.LINE_START;

        GridBagConstraints closeButtonConstraints = new GridBagConstraints();
        closeButtonConstraints.fill = GridBagConstraints.HORIZONTAL;
        closeButtonConstraints.gridx = 2;
        closeButtonConstraints.gridy = 0;
        closeButtonConstraints.gridwidth = 1;
        closeButtonConstraints.insets = new Insets(0, 300, 0, 0);
//endregion

        findTextPanel.add(findTextField, findTextConstraints);
        findTextPanel.add(ocurrencesLabel, ocurrencesConstraints);
        findTextPanel.add(prevOcurrence);
        findTextPanel.add(nextOcurrence);

        closeButton.addActionListener(e -> {
            JComponent button = (JComponent) e.getSource();
            button.getParent().getParent().setVisible(false);
            findTextPanel.setVisible(false);
            replaceTextPanel.setVisible(false);
            controller.removeFindWordHighlights();
        });

        wrapper.add(findTextPanel, panelConstraints);
        wrapper.add(closeButton, closeButtonConstraints);

        return wrapper;
    }

    /**
     * Creates the JPanel used to replace a word
     * @return
     */
    private JPanel initReplaceTextPanel(){
        replaceTextPanel = new JPanel();
        //region REPLACETEXTPANEL SETTINGS
        replaceTextPanel.setVisible(false);
        replaceTextPanel.setBackground(Constants.COLOR_LIGHT_GREY);
        replaceTextPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
        replaceTextPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 2));
        //endregion

        replaceTextField = new JTextField();
        //region REPLACETEXTFIELD SETTINGS
        replaceTextField.setBackground(Constants.COLOR_DARK_GREY);
        replaceTextField.setForeground(Color.white);
        replaceTextField.setCaretColor(Color.white);
        replaceTextField.setBorder(new EmptyBorder(0, 5, 0, 20));
        replaceTextField.setSize(new Dimension(300, 20));
        replaceTextField.setPreferredSize(new Dimension(300, 20));
        //endregion

        replaceAllButton = new JButton("Replace all");
        replaceAllButton.addActionListener(e -> replaceAllButtonAction());

        replaceButton = new JButton("Replace");
        replaceButton.addActionListener(e -> replaceButtonAction());

        replaceTextPanel.add(replaceTextField);
        replaceTextPanel.add(replaceButton);
        replaceTextPanel.add(replaceAllButton);
        return replaceTextPanel;
    }

    /**
     * Method used to change the button design to transparent
     * @param button button element to change
     */
    private void setButtonTransparent(JButton button){
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
    }

    /**
     * Method that makes the find panel visible
     * @param enable
     */
    public static void enableFindPanel(boolean enable){
        // if the replace panel is visible we hide it
        if (replaceTextPanel.isVisible()){
            replaceTextPanel.setVisible(false);
        }
        findTextPanel.setVisible(enable);
    }

    /**
     * Methos that makes the find & replace panel visible
     * @param enable
     */
    public static void enableFindReplacePanel(boolean enable){
        // if the find panel is not visible we make it visible
        if (!findTextPanel.isVisible()){
            findTextPanel.setVisible(true);
        }
        replaceTextPanel.setVisible(enable);
    }

    /**
     * Methos used to highlight the word to find
     */
    private void highlightWord(){
        // if there is a word types in the find text field we look for it
        if (!findTextField.getText().isEmpty() && !findTextField.getText().isBlank()){
            // Find all occurrences of the string inside the text
            Future<ArrayList<Integer>> future = controller.findWordInText(findTextField.getText().trim());
            while(!future.isDone()) {}
            try {
                indexOcurrences = future.get();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            ocurrencesLabel.setText((currentIndex + 1) + "/" + indexOcurrences.size());
            controller.setCursorPosInNotepad(currentIndex);
        // Otherwise we delete the highlights
        } else {
            controller.removeFindWordHighlights();
            ocurrencesLabel.setText("      ");
            indexOcurrences.clear();
            currentIndex = 0;
        }
    }

    /**
     * Navigates through the list of occurrences of the text found
     * @param isPrev wether or not we want to go to the previous word or the next word
     */
    private void navigateOccurrencesButtonsAction(boolean isPrev){
        if (isPrev){
            if (currentIndex > 0){
                currentIndex--;
            } else {
                currentIndex = indexOcurrences.size() - 1;
            }
        } else {
            if (currentIndex < indexOcurrences.size() - 1){
                currentIndex++;
            } else {
                currentIndex = 0;
            }
        }
        if (indexOcurrences.size() > 0){
            controller.setCursorPosInNotepad(indexOcurrences.get(currentIndex));
        }
        ocurrencesLabel.setText((currentIndex + 1) + "/" + indexOcurrences.size());
    }

    /**
     * Method called when the replace button is actioned
     */
    private void replaceButtonAction(){
        if (!findTextField.getText().isEmpty() && !findTextField.getText().isBlank() && !replaceTextField.getText().isEmpty() && !replaceTextField.getText().isBlank()){
            controller.replaceWord(indexOcurrences.get(currentIndex), findTextField.getText().length(), replaceTextField.getText());
            indexOcurrences.remove(indexOcurrences.get(currentIndex));
            if (indexOcurrences.size() > 0){
                ocurrencesLabel.setText((currentIndex + 1) + "/" + indexOcurrences.size());
            } else {
                replaceTextField.setText("");
                findTextField.setText("");
                ocurrencesLabel.setText("      ");
            }
            controller.checkText();
        }
    }

    /**
     * Method called when the replaceAll button
     */
    private void replaceAllButtonAction(){
        // If we have values in the find and replace text fields we proceed
        if (!findTextField.getText().isEmpty() && !findTextField.getText().isBlank() && !replaceTextField.getText().isEmpty() && !replaceTextField.getText().isBlank()){
            // We replace the occurrences of the old string with the new string
            controller.replaceWords(findTextField.getText(), replaceTextField.getText());
            // We update the indexes of the mispelled words
            for (int idx : indexOcurrences){
                controller.updateMispelledCursorEnds(idx, replaceTextField.getText().length() - findTextField.getText().length());
            }
            replaceTextField.setText("");
            findTextField.setText("");
            ocurrencesLabel.setText("      ");
            indexOcurrences.clear();
            controller.checkText();
        }
    }
}
