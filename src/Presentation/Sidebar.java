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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Sidebar extends JPanel {

    private JComboBox languageSelector;
    private Controller controller;
    private JScrollPane selectedFilesScrollPane;
    private DefaultListModel listModel;
    private ArrayList<Word> erroresArrayList;
    private JList<ArrayList> lista;
    private Word errorSeleccionado;
    int idx;

    public Sidebar(Controller controller) {
        this.controller = controller;
        initComponents();
    }

    private ArrayList<Language> languages;

    private void initComponents() {
        this.setVisible(true);
        this.setLayout(new BorderLayout());
        this.setSize(Constants.DIM_SIDEBAR);
        this.setPreferredSize(Constants.DIM_SIDEBAR);

        languages = controller.getLanguages();
        languageSelector = new JComboBox<>(languages.toArray(new Language[languages.size()]));
        languageSelector.setRenderer(new LanguageSelectorRenderer());

        Object popup = languageSelector.getUI().getAccessibleChild(languageSelector, 0);
        if (popup instanceof ComboPopup) {
            JList jlist = ((ComboPopup)popup).getList();
            jlist.setFixedCellHeight(50);
        }

        languageSelector.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                Language selectedLanguage = (Language) languageSelector.getSelectedItem();
                controller.setSelectedLanguage(selectedLanguage);
                System.out.print(selectedLanguage.getName());
            }
        });
        this.add(languageSelector, BorderLayout.NORTH);


        //Creación de la lista de ficheros
        listModel = new DefaultListModel();
        lista = new JList(listModel);
        erroresArrayList = new ArrayList<Word>();
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent le) {
                if(!le.getValueIsAdjusting()) {
                    idx = lista.getSelectedIndex();
                        System.out.println(controller.getWord(idx).getEntry());

                }
            }
        });

        selectedFilesScrollPane = new JScrollPane(lista, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        selectedFilesScrollPane.setSize(Constants.DIM_SIDEBAR);
        selectedFilesScrollPane.setPreferredSize(Constants.DIM_SIDEBAR);

        this.add(selectedFilesScrollPane);


    }

    public void addToModel(Word w) {
            System.out.println(w.toString());
            listModel.addElement(w);
            erroresArrayList.add(w);
            System.out.println(w.getEntry());
            this.repaint();
    }

    public void removeOfModel(int x){
        listModel.remove(x);
    }

    }

