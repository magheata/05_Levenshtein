/* Created by andreea on 07/05/2020 */
package Presentation;

import Application.Controller;
import Utils.Constants;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class Menu {

    private JMenuItem newFileMenuItem, newFileFromExistingMenuItem, openExistingMenuItem;
    private JMenu newFileMenu, openFileMenu;

    private Controller controller;

    public JMenuBar getMenuBar() {
        return menuBar;
    }

    private JMenuBar menuBar;

    public Menu(Controller controller){
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        menuBar = new JMenuBar();
        newFileMenu = new JMenu("New");
        newFileMenu.setIcon(new ImageIcon(Constants.PATH_ADD_FILE_ICON));
        menuBar.add(newFileMenu);

        openFileMenu = new JMenu("Open");
        openFileMenu.setIcon(new ImageIcon(Constants.PATH_OPEN_FILE_ICON));

        menuBar.add(openFileMenu);

        newFileMenuItem = new JMenuItem("File");
        newFileMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.enableNotepad(true);
            }
        });
        newFileFromExistingMenuItem = new JMenuItem("File from existing sources...");
        newFileFromExistingMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.openFileChooser(true);
            }
        });
        newFileMenu.add(newFileMenuItem);
        newFileMenu.add(newFileFromExistingMenuItem);

        openExistingMenuItem = new JMenuItem("Open existing file...");
        openExistingMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.openFileChooser(false);
            }
        });
        openFileMenu.add(openExistingMenuItem);
    }
}
