/* Created by andreea on 07/05/2020 */
package Presentation;

import Application.Controller;
import Domain.Word;
import Utils.Constants;
import Utils.UnderlineHighlightPainter;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Notepad extends JTextPane {

    private Highlighter.HighlightPainter painterBracketUnmatched = new DefaultHighlighter.DefaultHighlightPainter(Color.red);
    private Highlighter.HighlightPainter painter = new UnderlineHighlightPainter(Color.red);

    private ExecutorService executor;
    private ArrayList<Character> charactersInWord;
    private Controller controller;

    public Notepad(Controller controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        executor = Executors.newSingleThreadExecutor();
        charactersInWord = new ArrayList<>();
        this.setVisible(true);
        this.setEditable(false);
        this.setSize(Constants.DIM_NOTEPAD);
        this.setPreferredSize(Constants.DIM_NOTEPAD);
        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                executor.submit(() -> checkWrittenWordInPanel(e));
            }
        });
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

    public void setNotepadText(StringBuilder content) {
        this.setText(content.toString());
    }

    public void setNotepadEditable(boolean editable) {
        this.setEditable(editable);
    }

    private void getSelectedTextFromPanel(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            JTextPane textPane = (JTextPane) e.getComponent();
            int start = textPane.getSelectionStart();
            String text = textPane.getText();
            if ((0 <= start) && (start <= text.length() - 1)) {
                boolean eof = false;
                if ((text.charAt(start) != ' ') && (text.charAt(start) != '\n')) {
                    int wordStart = start;
                    int wordEnd = start;
                    char currentChar = text.charAt(start);
                    while (!Constants.SYMBOLS.contains(currentChar)) {
                        if (wordStart > 0) {
                            wordStart--;
                            currentChar = text.charAt(wordStart);
                        } else {
                            break;
                        }
                    }
                    if (Constants.SYMBOLS.contains(currentChar)) {
                        wordStart++;
                    }
                    currentChar = text.charAt(start);
                    while (!Constants.SYMBOLS.contains(currentChar)) {
                        if (wordEnd < text.length() - 1) {
                            wordEnd++;
                            currentChar = text.charAt(wordEnd);
                        } else {
                            eof = true;
                            break;
                        }
                    }
                    if (eof) {
                        System.out.println(text.substring(wordStart, wordEnd + 1));
                    } else {
                        System.out.println(text.substring(wordStart, wordEnd));
                    }
                }
            }
        }
    }

    private void checkWrittenWordInPanel(KeyEvent e) {
        JTextPane textPane = (JTextPane) e.getComponent();
        Highlighter highlighter = textPane.getHighlighter();

        int startCursor = textPane.getCaretPosition();
        String text = textPane.getText();
        if ((0 < startCursor) && (startCursor <= text.length())) {
            int auxIdx = startCursor - 1;
            char charAtCursor = text.charAt(auxIdx);
            if (Constants.SYMBOLS.contains(charAtCursor)) {
                auxIdx--;
                charAtCursor = text.charAt(auxIdx);
                while (!Constants.SYMBOLS.contains(charAtCursor)) {
                    charactersInWord.add(charAtCursor);
                    if (auxIdx > 0) {
                        auxIdx--;
                        charAtCursor = text.charAt(auxIdx);
                    } else {
                        break;
                    }
                }
                Collections.reverse(charactersInWord);

                StringBuilder word = new StringBuilder();
                for (Character ch : charactersInWord) {
                    word.append(ch);
                }

                Word writtenWord = new Word(word.toString(), false);
                if (!controller.findWordInDicctionary(writtenWord)) {
                    try {
                        highlighter.addHighlight(auxIdx == 0 ? auxIdx : auxIdx + 1, (auxIdx == 0 ? auxIdx : auxIdx + 1) + word.length(), painter);
                    } catch (BadLocationException badLocationException) {
                        badLocationException.printStackTrace();
                    }

                    System.out.println(word.toString() + " is mispelled. Maybe you meant: ");
                    for (Word entry : writtenWord.getReplaceWords(1)) {
                        System.out.println(entry.getEntry());
                    }
                }
                charactersInWord.clear();
            }
        }
    }

    public String getTextFromNotepad(){
        return this.getText();
    }

}
