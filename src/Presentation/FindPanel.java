/* Created by andreea on 21/05/2020 */
package Presentation;

import Application.Controller;
import Utils.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class FindPanel extends JPanel{

    private Controller controller;
    private static JPanel findTextPanel;
    private static JPanel replaceTextPanel;
    private JTextField findTextField, replaceTextField;
    private JButton replaceButton, replaceAllButton, excludeButton, closeButton, prevOcurrence, nextOcurrence;
    private JLabel ocurrencesLabel;
    private ArrayList<Integer> indexOcurrences = new ArrayList<>();

    public FindPanel(Controller controller){
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        this.setVisible(false);
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(initFindTextPanel());
        this.add(initReplaceTextPanel());
    }

    private JPanel initFindTextPanel(){
        findTextPanel = new JPanel();
        findTextPanel.setVisible(false);
        findTextPanel.setBackground(new Color(127, 127, 127));
        JPanel wrapper = new JPanel();
        wrapper.setBackground(new Color(127, 127, 127));
        wrapper.setLayout(new GridBagLayout());
        findTextPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
        findTextField = new JTextField();
        findTextField.setBackground(new Color(89, 89, 89));
        findTextField.setForeground(Color.white);
        findTextField.setBorder(new EmptyBorder(0, 5, 0, 20));
        findTextField.setSize(new Dimension(300, 20));
        findTextField.setPreferredSize(new Dimension(300, 20));
        findTextPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 2));

        prevOcurrence = new JButton(new ImageIcon(Constants.PATH_UP_ARROW_ICON));
        nextOcurrence = new JButton(new ImageIcon(Constants.PATH_DOWN_ARROW_ICON));
        closeButton = new JButton(new ImageIcon(Constants.PATH_CLOSE_ICON));
        ocurrencesLabel = new JLabel("8/8 results");
        setButtonTransparent(prevOcurrence);
        setButtonTransparent(nextOcurrence);

        GridBagConstraints panelConstraints = new GridBagConstraints();
        panelConstraints.fill = GridBagConstraints.HORIZONTAL;
        panelConstraints.gridx = 0;
        panelConstraints.gridy = 0;
        panelConstraints.gridwidth = 2;

        GridBagConstraints closeButtonConstraints = new GridBagConstraints();
        closeButtonConstraints.fill = GridBagConstraints.HORIZONTAL;
        closeButtonConstraints.gridx = 2;
        closeButtonConstraints.gridy = 0;
        closeButtonConstraints.gridwidth = 1;
        closeButtonConstraints.insets = new Insets(0, 181, 0, 0);
        findTextPanel.add(findTextField);
        findTextPanel.add(ocurrencesLabel);
        findTextPanel.add(prevOcurrence);
        findTextPanel.add(nextOcurrence);
        setButtonTransparent(closeButton);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComponent button = (JComponent) e.getSource();
                button.getParent().getParent().setVisible(false);
                findTextPanel.setVisible(false);
                replaceTextPanel.setVisible(false);
            }
        });
        wrapper.add(findTextPanel, panelConstraints);
        wrapper.add(closeButton, closeButtonConstraints);

        return wrapper;
    }

    private JPanel initReplaceTextPanel(){
        replaceTextPanel = new JPanel();
        replaceTextPanel.setVisible(false);
        replaceTextPanel.setBackground(new Color(127, 127, 127));
        replaceTextPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
        replaceTextField = new JTextField();
        replaceTextField.setBackground(new Color(89, 89, 89));
        replaceTextField.setForeground(Color.white);

        replaceTextField.setBorder(new EmptyBorder(0, 5, 0, 20));
        replaceTextField.setSize(new Dimension(300, 20));
        replaceTextField.setPreferredSize(new Dimension(300, 20));
        replaceTextPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 2));
        replaceAllButton = new JButton("Replace all");
        replaceButton = new JButton("Replace");
        excludeButton = new JButton("Exclude");
        replaceTextPanel.add(replaceTextField);
        replaceTextPanel.add(replaceButton);
        replaceTextPanel.add(replaceAllButton);
        replaceTextPanel.add(excludeButton);
        return replaceTextPanel;
    }

    private void setButtonTransparent(JButton button){
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
    }


    public static void enableFindPanel(boolean enable){
        findTextPanel.setVisible(enable);
    }

    public static void enableFindReplacePanel(boolean enable){
        findTextPanel.setVisible(enable);
        replaceTextPanel.setVisible(enable);
    }
}
