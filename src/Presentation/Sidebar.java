/* Created by andreea on 07/05/2020 */
package Presentation;

import Application.Controller;
import Domain.Word;
import Utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;



public class Sidebar extends JPanel {

    private Controller controller;
    private JScrollPane selectedFilesScrollPane;
    private DefaultListModel listModel;
    private ArrayList<Word> erroresArrayList;
    private JList<ArrayList> lista;
    private int idx;

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

