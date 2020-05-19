/* Created by andreea on 07/05/2020 */
package Presentation;

import Application.Controller;
import Domain.Language;
import Domain.Word;
import Utils.Constants;

import javax.swing.*;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import eu.hansolo.custom.*;
import eu.hansolo.tools.ColorDef;


public class Sidebar extends JPanel {

    private JComboBox languageSelector;
    private Controller controller;
    private JScrollPane selectedFilesScrollPane;
    private DefaultListModel listModel;
    private ArrayList<Word> erroresArrayList;
    private JList<ArrayList> lista;
    private int idx;
    private JButton correctTextButton;
    private JButton editTextButton;
    private JPanel buttonsPanel, wrapperPanel;


    public Sidebar(Controller controller) {
        this.controller = controller;
        controller.setSidebar(this);
        initComponents();
    }

    private ArrayList<Language> languages;

    private void initComponents() {

        wrapperPanel = new JPanel();

        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());

        correctTextButton = new JButton("Correct text");
        editTextButton = new JButton("Edit text");

        buttonsPanel.add(correctTextButton);
        buttonsPanel.add(editTextButton);

        wrapperPanel.setLayout(new BorderLayout());

        this.setVisible(true);
        this.setLayout(new BorderLayout());
        this.setSize(Constants.DIM_SIDEBAR);
        this.setPreferredSize(Constants.DIM_SIDEBAR);

        languages = controller.getLanguages();
        languageSelector = new JComboBox<>(languages.toArray(new Language[languages.size()]));
        languageSelector.setRenderer(new LanguageSelectorRenderer());

        Object popup = languageSelector.getUI().getAccessibleChild(languageSelector, 0);
        if (popup instanceof ComboPopup) {
            JList jlist = ((ComboPopup) popup).getList();
            jlist.setFixedCellHeight(50);
        }

        languageSelector.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Language selectedLanguage = (Language) languageSelector.getSelectedItem();
                controller.setSelectedLanguage(selectedLanguage);
                System.out.print(selectedLanguage.getName());
            }
        });
        this.add(languageSelector, BorderLayout.NORTH);

        wrapperPanel.add(buttonsPanel, BorderLayout.CENTER);
        wrapperPanel.add(languageSelector, BorderLayout.SOUTH);
        this.add(wrapperPanel, BorderLayout.NORTH);

        //Creación de la lista de ficheros
        listModel = new DefaultListModel();
        lista = new JList(listModel);
        erroresArrayList = new ArrayList<>();
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.addListSelectionListener(le -> {
            if (!le.getValueIsAdjusting()) {
                idx = lista.getSelectedIndex();
            }
        });

        selectedFilesScrollPane = new JScrollPane(lista, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        selectedFilesScrollPane.setSize(Constants.DIM_SIDEBAR);
        selectedFilesScrollPane.setPreferredSize(Constants.DIM_SIDEBAR);

        this.add(selectedFilesScrollPane);
    }

    //Añade un elemento a la barra lateral
    public void addToModel(Word w) {
        listModel.addElement(w.getEntry());
        erroresArrayList.add(w);
        this.repaint();
    }

    //Quita un elemento de la lista de la barra lateral
    public void removeFromModel(Word word) {
        listModel.remove(listModel.indexOf(word.getEntry()));
        this.repaint();
    }

    public void resizeSideBar(int width, int height) {
        this.setSize(new Dimension((width * 30 / 100), height));
        this.setPreferredSize(new Dimension((width * 30 / 100), height));
    }

    public void resetModel() {
        listModel.clear();
        this.repaint();
    }
}

