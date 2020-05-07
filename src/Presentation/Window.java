/* Created by andreea on 05/05/2020 */
package Presentation;

import Utils.Constants;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    private Sidebar sideBarPanel;
    private Notepad notepadPanel;
    private Menu menuPanel;

    public Window(){
        initComponents();
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());
        this.setSize(Constants.DIM_WINDOW);
        this.setPreferredSize(Constants.DIM_WINDOW);
        this.setMinimumSize(Constants.DIM_WINDOW);

        notepadPanel = new Notepad();
        sideBarPanel = new Sidebar();
        menuPanel = new Menu();

        this.add(menuPanel, BorderLayout.NORTH);
        this.add(notepadPanel, BorderLayout.WEST);
        this.add(sideBarPanel, BorderLayout.EAST);

        this.setVisible(true);
        this.pack();
    }
}
