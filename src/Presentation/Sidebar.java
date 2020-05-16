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
    private Word errorSeleccionado;
    private SteelCheckBox checkBox;
    private SteelCheckBoxUI checkBoxUI;

    private int idx;
    private JMenuBar menuIncorrectas;

    public Sidebar(Controller controller) {
        this.controller = controller;
        initComponents();
    }

    private ArrayList<Language> languages;

    private void initComponents() {

        JPanel wrapperPanel = new JPanel();

        wrapperPanel.setLayout(new BorderLayout());
        JPanel checkBoxPanel = new JPanel();
        checkBox = new SteelCheckBox();
        checkBox.setText("Enable suggestions");
        checkBox.setColored(true);
        checkBox.setSelectedColor(ColorDef.ORANGE);
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

        checkBoxPanel.add(checkBox);

        wrapperPanel.add(checkBoxPanel, BorderLayout.NORTH);
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
                System.out.println(controller.getWord(idx).getEntry());

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
}

