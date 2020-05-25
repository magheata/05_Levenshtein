/**
 * AUTHORS: Rafael Adrián Gil Cañestro
 * Miruna Andreea Gheata
 */
package Presentation;

import Application.Controller;
import Domain.Word;
import Utils.Constants;
import Utils.UnderlineHighlightPainter;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Notepad extends JTextPane {

    private Highlighter highlighter;
    private Highlighter.HighlightPainter painterHighlight = new DefaultHighlighter.DefaultHighlightPainter(Constants.COLOR_HIGHLIGHT);
    private Highlighter.HighlightPainter painterUnderline = new UnderlineHighlightPainter(Color.red);

    private ExecutorService executor;
    private Controller controller;

    private ArrayList<Highlighter.Highlight> highlights = new ArrayList<>();
    private ArrayList<Character> charactersInWord;

    public Notepad(Controller controller) {
        this.controller = controller;
        highlighter = this.getHighlighter();
        initComponents();
    }

    private void initComponents() {
        executor = Executors.newSingleThreadExecutor();
        charactersInWord = new ArrayList<>();
        this.setMargin(new Insets(5, 5, 5, 5));
        this.setEditable(true);
        this.setCaretColor(Color.white);
        this.setBackground(Constants.COLOR_NOTEPAD);
        this.setForeground(Color.white);
        this.setSize(Constants.DIM_NOTEPAD);
        this.setPreferredSize(Constants.DIM_NOTEPAD);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                executor.submit(() -> checkWrittenWordInPanel(e));
            }
        });
    }

    /**
     * Method used to check the last written word in the panel. This method allows an interactive behaviour, so when
     * you type a mispelled word you know right away that it's mispelled
     * @param e
     */
    private void checkWrittenWordInPanel(KeyEvent e) {
        JTextPane textPane = (JTextPane) e.getComponent();
        int startCursor = textPane.getCaretPosition();
        String text = textPane.getText();
        // If the delete key was pressed we check if we have deleted a mispelled word
        if (e.getKeyCode() == 8) {
            if ((startCursor > 0) && (text.charAt(startCursor - 1) != ' ')) {
                Map<Integer, Word> mispelledWordsCursor = new TreeMap<>(controller.getMispelledWordsCursorEnd());
                Object[] keys = mispelledWordsCursor.keySet().toArray();
                int idx = 0;
                while (startCursor > (int) keys[idx]) {
                    idx++;
                }
                Word word = mispelledWordsCursor.get(keys[idx]);
                controller.deleteMispelledWord((int) keys[idx]);
                removeUnderlineForWord((int) keys[idx] - 1, word.getEntry().length());
            }
            // We check the text again to find differences
            controller.checkText();
        }
        else {
            // If we have to check the word
            if (checkWord(text, KeyStroke.getKeyStrokeForEvent(e), startCursor)) {
                if ((0 < startCursor) && (startCursor <= text.length())) {
                    int auxIdx = getStartWord(text, startCursor - 1);
                    Word writtenWord;
                    if (charactersInWord.size() > 0) {
                        writtenWord = new Word(getWrittenWord(), controller.isSoundexDictionary());
                        // If not found in dictionary it's a mispelled word
                        if (!controller.findWordInDicctionary(writtenWord)) {
                            try {
                                writtenWord.setPos(startCursor - 1);
                                writtenWord.setLine(getCurrentRow(startCursor));
                                /* If we have used a suggestion from the dropdown it means that the mispelled word is
                                   corrected so we don't add it */
                                if (!controller.isSuggestionUsed()){
                                    // If no suggestion was used we add the mispelled word and highlight it
                                    controller.addMispelledWord(writtenWord);
                                    if (!containsHighlight(auxIdx, auxIdx + writtenWord.getEntry().length())) {
                                        highlighter.addHighlight(auxIdx == 0 ? auxIdx : (auxIdx + 1),
                                                (auxIdx == 0 ? auxIdx : (auxIdx + 1)) + writtenWord.getEntry().length(),
                                                painterUnderline);
                                        highlights = new ArrayList<>(Arrays.asList(highlighter.getHighlights()));
                                    }
                                }
                            } catch (BadLocationException badLocationException) {
                            }
                        }
                        charactersInWord.clear();
                    }
                }
            }
        }
    }

    /**
     * Method that return the row in the notepad of the word
     * @param caretPos
     * @return
     */
    private int getCurrentRow(int caretPos) {
        int rowNum = (caretPos == 0) ? 1 : 0;
        for (int offset = caretPos; offset > 0; ) {
            try {
                offset = Utilities.getRowStart(this, offset) - 1;
            } catch (BadLocationException e) {
            }
            rowNum++;
        }
        return rowNum;
    }

    /**
     * Underlines a word in the notepad
     * @param word word to underline
     */
    public void underlineMispelledWord(Word word) {
        try {
            String text = this.getText();
            int indexWord = text.toLowerCase().indexOf(word.getEntry().toLowerCase());
            if (!containsHighlight(indexWord, indexWord + word.getEntry().length())) {
                highlighter.addHighlight(indexWord, indexWord + word.getEntry().length(), painterUnderline);
                highlights = new ArrayList<>(Arrays.asList(highlighter.getHighlights()));
            }
        } catch (BadLocationException e) {
        }
    }

    /**
     * Method used to remove all highlights from the Notepad
     */
    public void removeHighlights() {
        highlighter.removeAllHighlights();
    }

    /**
     * Methos used to get the row of a Word.
     * @param word
     * @return
     */
    public int getWordRow(Word word) {
        String text = this.getText();
        ArrayList<Integer> eofIndex = new ArrayList<>();
        int index = text.indexOf("\n");
        int row = 0;
        if (index == -1) {
            return row;
        } else {
            while (index >= 0) {
                eofIndex.add(index);
                index = text.indexOf("\n", index + 1);
            }

            for (int eof : eofIndex) {
                if (text.indexOf(word.getEntry()) > eof) {
                    row++;
                } else {
                    return row;
                }
            }
        }
        return row;
    }

    /**
     * Methos used to remove the underline from a word
     * @param cursorPos
     * @param length
     */
    public void removeUnderlineForWord(int cursorPos, int length) {
        for (Highlighter.Highlight highlight : highlights) {
            if ((highlight.getStartOffset() >= (cursorPos + length)) &&
                    (highlight.getEndOffset() >= (cursorPos + length)) &&
                    (highlight.getStartOffset() == highlight.getEndOffset())) {
                highlighter.removeHighlight(highlight);
                highlights = new ArrayList<>(Arrays.asList(highlighter.getHighlights()));
                break;
            }
        }
    }

    public boolean containsHighlight(int start, int end) {
        if (highlights.size() > 0) {
            for (Highlighter.Highlight h : highlights) {
                if ((h.getStartOffset() == start) && (h.getEndOffset() == end)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public void highlightWordInText(int idx, int length){
        try {
            highlighter.addHighlight(idx, idx + length, painterHighlight);
        } catch (BadLocationException e) {
        }
    }

    public void removeFindWordHighlights(){
        Highlighter.Highlight [] highlights = highlighter.getHighlights();
        for (Highlighter.Highlight h : highlights){
            if (h.getPainter() instanceof DefaultHighlighter.DefaultHighlightPainter){
                highlighter.removeHighlight(h);
            }
        }
    }

    public void setNotepadText(StringBuilder content) {
        this.setText(content.toString());
    }

    public void setNotepadEditable(boolean editable) {
        this.setEditable(editable);
    }

    /**
     * Methos used to know if we have to check the written word in the Notepad
     * @param text text in the Notepad
     * @param eventKeystroke event
     * @param startCursor cursor of the notepad
     * @return
     */
    private boolean checkWord(String text, KeyStroke eventKeystroke, int startCursor){
        // Used to know if we have to check the written word
        for (char specialChar : Constants.SYMBOLS) {
            KeyStroke charKeystroke = KeyStroke.getKeyStroke(specialChar, 0);
            // If the key pressed just now is a special character we check the previous word written
            if (eventKeystroke.equals(charKeystroke)) {
                return true;
            }
        }
            /* If it wasn't a special character pressed but the character at the current cursor is a special character is
            a special character, we check the previous word written */
        char charAtStart = text.charAt(startCursor);
        if (Constants.SYMBOLS.contains(charAtStart)){
            return true;
        }
        return false;
    }

    /**
     * Method used to get the word start
     * @param text
     * @param idx
     * @return
     */
    private int getStartWord(String text, int idx){
        int auxIdx = idx;
        char charAtCursor = text.charAt(auxIdx);
        while (!Constants.SYMBOLS.contains(charAtCursor)) {
            charactersInWord.add(charAtCursor);
            if (auxIdx > 0) {
                auxIdx--;
                charAtCursor = text.charAt(auxIdx);
            } else {
                break;
            }
        }
        return auxIdx;
    }

    /**
     * Methos used to get the written word as a String
     * @return
     */
    private String getWrittenWord(){
        Collections.reverse(charactersInWord);
        StringBuilder word = new StringBuilder();
        for (Character ch : charactersInWord) {
            word.append(ch);
        }
        return word.toString();
    }
}
