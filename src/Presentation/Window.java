/* Created by andreea on 05/05/2020 */
package Presentation;

import Utils.Constants;

import javax.swing.*;

public class Window extends JFrame {

    public Window(){
        initComponents();
    }

    private void initComponents() {
        this.setSize(Constants.DIM_WINDOW);
        this.setPreferredSize(Constants.DIM_WINDOW);
        this.setVisible(true);
        this.pack();
    }
}
