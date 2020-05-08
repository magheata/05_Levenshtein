/* Created by andreea on 07/05/2020 */
package Presentation;

import Utils.Constants;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Notepad extends JTextPane {

    private ExecutorService executor;

    public Notepad(){
        initComponents();
    }

    private void initComponents() {
        executor = Executors.newSingleThreadExecutor();
        this.setVisible(true);
        this.setEditable(false);
        this.setSize(Constants.DIM_NOTEPAD);
        this.setPreferredSize(Constants.DIM_NOTEPAD);
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                executor.submit(() -> getSelectedTextFromPanel(e));
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    public void setNotepadText(StringBuilder content){
        this.setText(content.toString());
    }

    public void setNotepadEditable(boolean editable){
        this.setEditable(editable);
    }

    private void getSelectedTextFromPanel(MouseEvent e){
        if (SwingUtilities.isLeftMouseButton(e)) {
            JTextPane textPane = (JTextPane)  e.getComponent();
            int start = textPane.getSelectionStart();
            String text = textPane.getText();
            if ((0 <= start) && (start <= text.length() - 1)){
                boolean eof = false;
                if ((text.charAt(start) != ' ') && (text.charAt(start) != '\n')){
                    int wordStart = start;
                    int wordEnd = start;
                    char currentChar = text.charAt(start);
                    while (!Constants.SYMBOLS.contains(currentChar)){
                        if (wordStart > 0){
                            wordStart--;
                            currentChar = text.charAt(wordStart);
                        } else {
                            break;
                        }
                    }
                    if (Constants.SYMBOLS.contains(currentChar)){
                        wordStart++;
                    }
                    currentChar = text.charAt(start);
                    while (!Constants.SYMBOLS.contains(currentChar)){
                        if (wordEnd < text.length() - 1){
                            wordEnd++;
                            currentChar = text.charAt(wordEnd);
                        } else {
                            eof = true;
                            break;
                        }
                    }
                    if (eof){
                        System.out.println(text.substring(wordStart, wordEnd  + 1));
                    } else {
                        System.out.println(text.substring(wordStart, wordEnd));
                    }
                }
            }
        }
    }
}
