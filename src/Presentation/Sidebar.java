/* Created by andreea on 07/05/2020 */
package Presentation;

import Application.Controller;
import Domain.Word;
import Utils.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;


public class Sidebar extends JPanel {

    private Controller controller;
    private JScrollPane selectedFilesScrollPane;
    private ArrayList<Word> erroresArrayList;
    private static JLabel statusTextLabel;
    private int idx;
    private JPanel panelComboBox;
    private HashMap<Word, JPanel> comboBoxWords = new HashMap<>();

    public Sidebar(Controller controller) {
        this.controller = controller;
        controller.setSidebar(this);
        initComponents();
    }

    private void initComponents() {
        this.setVisible(true);
        this.setLayout(new BorderLayout());
        this.setSize(Constants.DIM_SIDEBAR);
        this.setPreferredSize(Constants.DIM_SIDEBAR);
        //Creación de la lista de ficheros

        panelComboBox = new JPanel();
        panelComboBox.setLayout(new BoxLayout(panelComboBox, BoxLayout.Y_AXIS));

        erroresArrayList = new ArrayList<>();

        selectedFilesScrollPane = new JScrollPane(panelComboBox, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        selectedFilesScrollPane.setSize(Constants.DIM_SIDEBAR);
        selectedFilesScrollPane.setPreferredSize(Constants.DIM_SIDEBAR);

        this.add(selectedFilesScrollPane);
    }

    //Añade un elemento a la barra lateral
    public void addToModel(Word w) {
        ArrayList<String> replacements = new ArrayList<>();
        ArrayList<Word> replaceWord;
        int distUsed = 0;
        for (int i = 1; i <= 4; i++){
            if ((distUsed < 2) && (w.getReplaceWords(i) != null) && (!w.getReplaceWords(i).isEmpty())){
                distUsed++;
                replaceWord =  w.getReplaceWords(i);
                for (Word word : replaceWord){
                    replacements.add(word.getEntry() + " (" + i + ")");
                }
            }
        }
        JPanel panel = new JPanel();

        JLabel mispelledWordLabel = new JLabel(w.getEntry(), JLabel.CENTER);
        mispelledWordLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        Font f = mispelledWordLabel.getFont();
        mispelledWordLabel.setForeground(Color.red);
        mispelledWordLabel.setFont(f.deriveFont(f.getStyle() | Font.BOLD));

        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(mispelledWordLabel);

        JComboBox firstTry = new JComboBox(replacements.toArray());
        firstTry.setBorder(new EmptyBorder(0, 0, 0, 0));
        firstTry.setSelectedItem(firstTry.getItemAt(0));

        JButton replaceButton = new JButton("Set");

        replaceButton.addActionListener(e -> {
            controller.setWordMispelledWord(w, (String) firstTry.getSelectedItem());
            panelComboBox.remove(panel);
            this.repaint();
        });

        JPanel wrapperMispelledWord = new JPanel();
        wrapperMispelledWord.setLayout(new FlowLayout(FlowLayout.CENTER));
        wrapperMispelledWord.add(firstTry);
        wrapperMispelledWord.add(replaceButton);

        panel.add(wrapperMispelledWord);
        panel.add(new JSeparator(SwingConstants.HORIZONTAL));

        panelComboBox.add(panel);
        comboBoxWords.put(w, panel);
        erroresArrayList.add(w);
        this.repaint();
    }

    //Quita un elemento de la lista de la barra lateral
    public void removeFromModel(Word word) {
        panelComboBox.remove(comboBoxWords.get(word));
        comboBoxWords.remove(word);
        this.repaint();
    }

    public void resizeSideBar(int width, int height) {
        this.setSize(new Dimension((width * 30 / 100), height));
        this.setPreferredSize(new Dimension((width * 30 / 100), height));
    }

    public void resetModel() {
        comboBoxWords.clear();
        panelComboBox.removeAll();
        erroresArrayList.clear();
        this.revalidate();
        this.repaint();
    }

}

