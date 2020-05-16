/* Created by andreea on 05/05/2020 */
package Presentation;

import Application.Controller;
import Domain.Word;
import Utils.Constants;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class Window extends JFrame {

    private Controller controller;
    private Sidebar sideBarPanel;
    private Notepad notepadPanel;
    private Menu menuPanel;

    public JFileChooser getFileChooser() {
        return fileChooser;
    }

    private JFileChooser fileChooser;

    public Window(Controller controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());
        this.setSize(Constants.DIM_WINDOW);
        this.setPreferredSize(Constants.DIM_WINDOW);
        this.setMinimumSize(Constants.DIM_WINDOW);

        fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
        fileChooser.setFileFilter(new FileNameExtensionFilter("txt", "txt"));
        notepadPanel = new Notepad(controller);
        controller.setNotepad(notepadPanel);
        sideBarPanel = new Sidebar(controller);
        menuPanel = new Menu(controller);

        JScrollPane notepadScrollPanel = new JScrollPane(notepadPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        this.add(notepadScrollPanel, BorderLayout.WEST);
        this.add(sideBarPanel, BorderLayout.EAST);

        this.setJMenuBar(menuPanel.getMenuBar());
        this.setVisible(true);
        this.pack();
    }

    public void openFileChooser() {
        fileChooser.showOpenDialog(this);
    }

    public void addToModel(Word w){
        sideBarPanel.addToModel(w);
    }
}
