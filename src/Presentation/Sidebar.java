/* Created by andreea on 07/05/2020 */
package Presentation;

import Application.Controller;
import Domain.Language;
import Utils.Constants;

import javax.swing.*;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

public class Sidebar extends JPanel {

    private JComboBox languageSelector;
    private Controller controller;

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
    }
}
