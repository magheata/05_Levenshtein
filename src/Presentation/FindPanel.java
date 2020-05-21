/* Created by andreea on 21/05/2020 */
package Presentation;

import Application.Controller;
import Utils.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class FindPanel extends JPanel{

    private Controller controller;
    private static JPanel findTextPanel;
    private static JPanel replaceTextPanel;
    private JTextField findTextField, replaceTextField;
    private JButton replaceButton, replaceAllButton, closeButton, prevOcurrence, nextOcurrence;
    private JLabel ocurrencesLabel;
    private ArrayList<Integer> indexOcurrences = new ArrayList<>();
    private int currentIndex = 0;

    public FindPanel(Controller controller){
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        this.setVisible(false);
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(initFindTextPanel());
        this.add(initReplaceTextPanel());
    }

    private JPanel initFindTextPanel(){
        findTextPanel = new JPanel();
        findTextPanel.setVisible(false);
        findTextPanel.setBackground(new Color(127, 127, 127));
        JPanel wrapper = new JPanel();
        wrapper.setBackground(new Color(127, 127, 127));
        wrapper.setLayout(new GridBagLayout());
        findTextPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
        findTextField = new JTextField();
        findTextField.setCaretColor(Color.white);
        findTextField.setBackground(new Color(89, 89, 89));
        findTextField.setForeground(Color.white);
        findTextField.setBorder(new EmptyBorder(0, 0, 0, 0));
        findTextField.setSize(new Dimension(300, 20));
        findTextField.setPreferredSize(new Dimension(300, 20));
        findTextPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 2));

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
        prevOcurrence = new JButton(new ImageIcon(Constants.PATH_UP_ARROW_ICON));

        prevOcurrence.addActionListener(e -> {
            if (currentIndex > 1){
                currentIndex--;
            } else {
                currentIndex = indexOcurrences.size() - 1;
            }
            controller.setCursorPosInNotepad(indexOcurrences.get(currentIndex));
            ocurrencesLabel.setText((currentIndex + 1) + "/" + indexOcurrences.size());
        });
        nextOcurrence = new JButton(new ImageIcon(Constants.PATH_DOWN_ARROW_ICON));
        nextOcurrence.addActionListener(e -> {
            if (currentIndex < indexOcurrences.size() - 1){
                currentIndex++;
            } else {
                currentIndex = 0;
            }
            controller.setCursorPosInNotepad(indexOcurrences.get(currentIndex));
            ocurrencesLabel.setText((currentIndex + 1) + "/" + indexOcurrences.size());
        });
        closeButton = new JButton(new ImageIcon(Constants.PATH_CLOSE_ICON));
        ocurrencesLabel = new JLabel();
        setButtonTransparent(prevOcurrence);
        setButtonTransparent(nextOcurrence);

        GridBagConstraints panelConstraints = new GridBagConstraints();
        panelConstraints.fill = GridBagConstraints.HORIZONTAL;
        panelConstraints.gridx = 0;
        panelConstraints.gridy = 0;
        panelConstraints.gridwidth = 2;

        GridBagConstraints closeButtonConstraints = new GridBagConstraints();
        closeButtonConstraints.fill = GridBagConstraints.HORIZONTAL;
        closeButtonConstraints.gridx = 2;
        closeButtonConstraints.gridy = 0;
        closeButtonConstraints.gridwidth = 1;
        closeButtonConstraints.insets = new Insets(0, 181, 0, 0);

        findTextPanel.add(findTextField);
        findTextPanel.add(ocurrencesLabel);
        findTextPanel.add(prevOcurrence);
        findTextPanel.add(nextOcurrence);
        setButtonTransparent(closeButton);

        closeButton.addActionListener(e -> {
            JComponent button = (JComponent) e.getSource();
            button.getParent().getParent().setVisible(false);
            findTextPanel.setVisible(false);
            replaceTextPanel.setVisible(false);
        });
        wrapper.add(findTextPanel, panelConstraints);
        wrapper.add(closeButton, closeButtonConstraints);

        return wrapper;
    }

    private JPanel initReplaceTextPanel(){
        replaceTextPanel = new JPanel();
        replaceTextPanel.setVisible(false);
        replaceTextPanel.setBackground(new Color(127, 127, 127));
        replaceTextPanel.setBorder(new EmptyBorder(0, 5, 0, 5));

        replaceTextField = new JTextField();
        replaceTextField.setBackground(new Color(89, 89, 89));
        replaceTextField.setForeground(Color.white);
        replaceTextField.setCaretColor(Color.white);
        replaceTextField.setBorder(new EmptyBorder(0, 5, 0, 20));
        replaceTextField.setSize(new Dimension(300, 20));
        replaceTextField.setPreferredSize(new Dimension(300, 20));

        replaceTextPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 2));

        replaceAllButton = new JButton("Replace all");

        replaceAllButton.addActionListener(e -> {
            if (!findTextField.getText().isEmpty() && !findTextField.getText().isBlank() && !replaceTextField.getText().isEmpty() && !replaceTextField.getText().isBlank()){
                for (int index : indexOcurrences){
                    controller.replaceWord(index, findTextField.getText().length(), replaceTextField.getText());
                    indexOcurrences.remove(index);
                    if (indexOcurrences.size() > 0){
                        ocurrencesLabel.setText((currentIndex + 1) + "/" + indexOcurrences.size());
                    } else {
                        replaceTextField.setText("");
                        findTextField.setText("");
                        ocurrencesLabel.setText("");
                    }
                }
                controller.checkText();
            }
        });
        replaceButton = new JButton("Replace");
        replaceButton.addActionListener(e -> {
            if (!findTextField.getText().isEmpty() && !findTextField.getText().isBlank() && !replaceTextField.getText().isEmpty() && !replaceTextField.getText().isBlank()){
                controller.replaceWord(indexOcurrences.get(currentIndex), findTextField.getText().length(), replaceTextField.getText());
                indexOcurrences.remove(indexOcurrences.get(currentIndex));
                if (indexOcurrences.size() > 0){
                    ocurrencesLabel.setText((currentIndex + 1) + "/" + indexOcurrences.size());
                } else {
                    replaceTextField.setText("");
                    findTextField.setText("");
                    ocurrencesLabel.setText("");
                }
                controller.checkText();
            }
        });
        replaceTextPanel.add(replaceTextField);
        replaceTextPanel.add(replaceButton);
        replaceTextPanel.add(replaceAllButton);

        return replaceTextPanel;
    }

    private void setButtonTransparent(JButton button){
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
    }


    public static void enableFindPanel(boolean enable){
        if (replaceTextPanel.isVisible()){
            replaceTextPanel.setVisible(false);
        }
        findTextPanel.setVisible(enable);
    }

    public static void enableFindReplacePanel(boolean enable){
        if (!findTextPanel.isVisible()){
            findTextPanel.setVisible(true);
        }
        replaceTextPanel.setVisible(enable);
    }

    private void highlightWord(){
        if (!findTextField.getText().isEmpty() && !findTextField.getText().isBlank()){
            indexOcurrences = controller.findWordInText(findTextField.getText().trim());
            ocurrencesLabel.setText((currentIndex + 1) + "/" + indexOcurrences.size());
            controller.setCursorPosInNotepad(currentIndex);
        } else {
            controller.removeFindWordHighlights();
            ocurrencesLabel.setText("");
            indexOcurrences.clear();
            currentIndex = 0;
        }
    }
}
