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
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Notepad extends JTextPane {

    private Highlighter.HighlightPainter painterBracketUnmatched = new DefaultHighlighter.DefaultHighlightPainter(Color.red);
    private Highlighter.HighlightPainter painter = new UnderlineHighlightPainter(Color.red);

    private ExecutorService executor;
    private ArrayList<Character> charactersInWord;
    private Controller controller;

    private Highlighter highlighter;
;
    public Notepad(Controller controller) {
        this.controller = controller;
        highlighter = this.getHighlighter();
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
        int startCursor = textPane.getCaretPosition();
        String text = textPane.getText();

        System.out.println(e.getKeyCode());
        if (e.getKeyCode() == 8){
            if (text.charAt(startCursor - 1) != ' '){
                Map<Integer, Word> mispelledWordsCursor = new TreeMap<>(controller.getMispelledWordsCursorEnd());
                Object[] keys = mispelledWordsCursor.keySet().toArray();
                int idx = 0;
                while (startCursor > (int) keys[idx]){
                    idx++;
                }
                Word word = mispelledWordsCursor.get(keys[idx]);
                System.out.println(word);
                controller.deleteMispelledWord((int) keys[idx]);
                removeHighlightForWord((int) keys[idx] - 1, word.getEntry().length());
            }
        } else {
            if ((0 < startCursor) && (startCursor <= text.length())) {
                int auxIdx = startCursor - 1;
                char charAtCursor = text.charAt(auxIdx);
                Word writtenWord;
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
                    if (charactersInWord.size() > 0){
                        Collections.reverse(charactersInWord);
                        StringBuilder word = new StringBuilder();
                        for (Character ch : charactersInWord) {
                            word.append(ch);
                        }
                        writtenWord = new Word(word.toString(), controller.isSoundexDictionary());
                        if (!controller.findWordInDicctionary(writtenWord)) {
                            try {
                                writtenWord.setPos(startCursor);
                                writtenWord.setLine(getCurrentRow(startCursor));
                                controller.addMispelledWord(writtenWord);
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
        }
    }

    private int getCurrentRow(int caretPos){
        int rowNum = (caretPos == 0) ? 1 : 0;
        for (int offset = caretPos; offset > 0;) {
            try {
                offset = Utilities.getRowStart(this, offset) - 1;
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
            rowNum++;
        }
        return rowNum;
    }

    public void underlineMispelledWord(Word word) {
        try {
            String text = this.getText();
            int indexWord = text.toLowerCase().indexOf(word.getEntry().toLowerCase());
            highlighter.addHighlight(indexWord, indexWord + word.getEntry().length(), painter);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void removeHighlights(){
        highlighter.removeAllHighlights();
    }

    public int getWordRow(Word word){
        String text = this.getText();
        ArrayList<Integer> eofIndex = new ArrayList<>();
        int index = text.indexOf("\n");
        int row = 0;
        if (index == -1){
            return row;
        } else {
            while (index >= 0) {
                eofIndex.add(index);
                System.out.println(index);
                index = text.indexOf("\n", index + 1);
            }

            for (int eof : eofIndex){
                if (text.indexOf(word.getEntry()) > eof){
                    row++;
                } else {
                    return row;
                }
            }
        }
        return row;
    }

    private void removeHighlightForWord(int cursorPos, int length){
        Highlighter.Highlight[] highlights = highlighter.getHighlights();
        for (Highlighter.Highlight highlight : highlights){
            if ((highlight.getStartOffset() == (cursorPos - length)) && (highlight.getEndOffset() == cursorPos)){
                highlighter.removeHighlight(highlight);
            }
        }
        Highlighter.Highlight highlight = highlights[0];
    }
}
