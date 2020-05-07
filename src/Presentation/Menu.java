/* Created by andreea on 07/05/2020 */
package Presentation;

import Utils.Constants;

import javax.swing.*;
import java.awt.*;

public class Menu extends JPanel {

    public Menu(){
        initComponents();
    }

    private void initComponents() {
        this.setVisible(true);
        this.setBackground(Color.GREEN);
        this.setSize(Constants.DIM_MENU);
        this.setPreferredSize(Constants.DIM_MENU);
    }
}
