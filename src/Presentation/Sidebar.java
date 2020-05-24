/**
 * AUTHORS: Rafael Adrián Gil Cañestro
 * Miruna Andreea Gheata
 */
package Presentation;

import Application.Controller;
import Domain.Word;
import Utils.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class that represents the Sidebar of the program. This Sidebar contains the information of the mispelled words,
 * as well as a way of replacing said words
 */
public class Sidebar extends JPanel {

    private Controller controller;
    private JScrollPane selectedFilesScrollPane;
    private ArrayList<Word> erroresArrayList;
    private JPanel panelComboBox;
    private HashMap<Word, JPanel> comboBoxWords = new HashMap<>();

    public Sidebar(Controller controller) {
        this.controller = controller;
        controller.setSidebar(this);
        initComponents();
    }

    /**
     * Initializes the JPanel for the Sidebar
     */
    private void initComponents() {
        this.setVisible(true);
        this.setLayout(new BorderLayout());
        this.setSize(Constants.DIM_SIDEBAR);
        this.setPreferredSize(Constants.DIM_SIDEBAR);

        panelComboBox = new JPanel();
        panelComboBox.setLayout(new BoxLayout(panelComboBox, BoxLayout.Y_AXIS));

        erroresArrayList = new ArrayList<>();

        selectedFilesScrollPane = new JScrollPane(panelComboBox, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        selectedFilesScrollPane.setSize(Constants.DIM_SIDEBAR);
        selectedFilesScrollPane.setPreferredSize(Constants.DIM_SIDEBAR);

        this.add(selectedFilesScrollPane);
    }

    /**
     * Method used to add a mispelled word to the sidebar. Each word has a panel with a JLabel representing the
     * mispelled word, a JComboBox with the replacement words, and a JButton that sets the correct word.
     * @param mispelledWord
     */
    public void addToSidebar(Word mispelledWord) {

        JPanel panel = new JPanel();

        JLabel mispelledWordLabel = new JLabel(mispelledWord.getEntry(), JLabel.CENTER);
        mispelledWordLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        Font f = mispelledWordLabel.getFont();
        mispelledWordLabel.setForeground(Color.red);
        mispelledWordLabel.setFont(f.deriveFont(f.getStyle() | Font.BOLD));

        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(mispelledWordLabel);

        // We create a ComboBox with the replacements for this word
        JComboBox replacementsComboBox = new JComboBox(getReplacements(mispelledWord).toArray());
        replacementsComboBox.setBorder(new EmptyBorder(0, 0, 0, 0));
        replacementsComboBox.setSelectedItem(replacementsComboBox.getItemAt(0));

        JButton replaceButton = new JButton("Set");

        replaceButton.addActionListener(e -> {
            controller.correctMispelledWord(mispelledWord, (String) replacementsComboBox.getSelectedItem());
            // We remove the panel for this word from the sidebar
            panelComboBox.remove(panel);
            this.repaint();
        });

        JPanel wrapperMispelledWord = new JPanel();
        wrapperMispelledWord.setLayout(new FlowLayout(FlowLayout.CENTER));
        wrapperMispelledWord.add(replacementsComboBox);
        wrapperMispelledWord.add(replaceButton);

        panel.add(wrapperMispelledWord);
        panel.add(new JSeparator(SwingConstants.HORIZONTAL));

        panelComboBox.add(panel);
        comboBoxWords.put(mispelledWord, panel);
        erroresArrayList.add(mispelledWord);
        this.repaint();
    }

    /**
     * Removes a mispelled word from the Sidebar
     * @param word
     */
    public void removeFromSidebar(Word word) {
        panelComboBox.remove(comboBoxWords.get(word));
        comboBoxWords.remove(word);
        this.repaint();
    }

    public void resetModel() {
        comboBoxWords.clear();
        panelComboBox.removeAll();
        erroresArrayList.clear();
        this.revalidate();
        this.repaint();
    }

    /**
     * Method used to get the list of replacement words for the mispeleld word.
     * @param mispelledWord word to replace
     * @return list of replacements words
     */
    private ArrayList<String> getReplacements(Word mispelledWord){
        ArrayList<String> replacements = new ArrayList<>();
        ArrayList<Word> replaceWord;
        int distUsed = 0;
        // We will show replace words with a maximum distance of 2 points from the word. If no words are given for the
        // distance, we increase it and check if there are any words for this new distance
        for (int i = 1; i <= Constants.MAX_DISTANCE; i++){
            if ((distUsed < 2) && (mispelledWord.getReplaceWords(i) != null) && (!mispelledWord.getReplaceWords(i).isEmpty())){
                distUsed++;
                replaceWord =  mispelledWord.getReplaceWords(i);
                for (Word word : replaceWord){
                    replacements.add(word.getEntry() + " (" + i + ")");
                }
            }
        }
        return replacements;
    }
}

