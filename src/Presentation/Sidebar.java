/* Created by andreea on 07/05/2020 */
package Presentation;

import Utils.Constants;

import javax.swing.*;
import java.awt.*;

public class Sidebar extends JPanel {

    public Sidebar(){
        initComponents();
    }

    private void initComponents() {
        this.setVisible(true);
        this.setBackground(Color.ORANGE);
        this.setSize(Constants.DIM_SIDEBAR);
        this.setPreferredSize(Constants.DIM_SIDEBAR);
    }
}
