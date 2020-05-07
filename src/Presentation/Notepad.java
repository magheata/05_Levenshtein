/* Created by andreea on 07/05/2020 */
package Presentation;

import Utils.Constants;

import javax.swing.*;
import java.awt.*;

public class Notepad extends JPanel {

    public Notepad(){
        initComponents();
    }

    private void initComponents() {
        this.setVisible(true);
        this.setBackground(Color.BLACK);
        this.setSize(Constants.DIM_NOTEPAD);
        this.setPreferredSize(Constants.DIM_NOTEPAD);
    }
}
