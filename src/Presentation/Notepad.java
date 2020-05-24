/* Created by andreea on 07/05/2020 */
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Notepad extends JTextPane {

    private Highlighter.HighlightPainter painterHighlight = new DefaultHighlighter.DefaultHighlightPainter(new Color(109, 104, 117));
    private Highlighter.HighlightPainter painterUnderline = new UnderlineHighlightPainter(Color.red);

    private ExecutorService executor;
    private ArrayList<Character> charactersInWord;
    private Controller controller;

    private Highlighter highlighter;
    private JList replaceWordsList;
    private JPopupMenu popupMenu;
    private ArrayList<Highlighter.Highlight> highlights = new ArrayList<>();

    private int selectionStart;
    ;

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
        this.setBackground(new Color(53, 53, 53));
        this.setForeground(Color.white);
        this.setSize(Constants.DIM_NOTEPAD);
        this.setPreferredSize(Constants.DIM_NOTEPAD);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                executor.submit(() -> checkWrittenWordInPanel(e));
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                executor.submit(() -> getSelectedTextFromPanel(e));
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
            selectionStart = start;
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
                    Object[] mispelledWord = controller.isMispelledWord(new Word(text.substring(wordStart, wordEnd), controller.isSoundexDictionary()));

                    if ((boolean) mispelledWord[0]) {
                        Word word = (Word) mispelledWord[1];
                        ArrayList<String> replaceStringWords = new ArrayList<>();
                        Iterator it = word.getReplaceWords(2).iterator();
                        while (it.hasNext()) {
                            Word replaceWord = (Word) it.next();
                            replaceStringWords.add(replaceWord.getEntry());
                        }
                    }
                    if (eof) {
                    } else {
                    }
                }
            }
        }
    }

    private void checkWrittenWordInPanel(KeyEvent e) {
        JTextPane textPane = (JTextPane) e.getComponent();
        int startCursor = textPane.getCaretPosition();
        String text = textPane.getText();
        if (e.getKeyCode() == 8) {
            if ((startCursor > 0) && (text.charAt(startCursor - 1) != ' ')) {
                if ((textPane.getSelectionStart() != -1) && (textPane.getSelectionEnd() != -1)){
                    controller.removeMispelledWordsBetweenSelection(textPane.getSelectionStart(),textPane.getSelectionEnd());
                } else {
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
            }
            controller.checkText();
        } else {
            boolean checkWord = false;
            KeyStroke eventKeystroke = KeyStroke.getKeyStrokeForEvent(e);
            for (char specialChar : Constants.SYMBOLS) {
                KeyStroke charKeystroke = KeyStroke.getKeyStroke(specialChar, 0);
                if (eventKeystroke.equals(charKeystroke)) {
                    checkWord = true;
                    break;
                }
            }
            if (!checkWord){
                char charAtStart = text.charAt(startCursor);
                if (Constants.SYMBOLS.contains(charAtStart)){
                    checkWord = true;
                }
            }
            if (checkWord) {
                if ((0 < startCursor) && (startCursor <= text.length())) {
                    int auxIdx = startCursor - 1;
                    char charAtCursor = text.charAt(auxIdx);
                    Word writtenWord;
                    while (!Constants.SYMBOLS.contains(charAtCursor)) {
                        charactersInWord.add(charAtCursor);
                        if (auxIdx > 0) {
                            auxIdx--;
                            charAtCursor = text.charAt(auxIdx);
                        } else {
                            break;
                        }
                    }
                    if (charactersInWord.size() > 0) {
                        Collections.reverse(charactersInWord);
                        StringBuilder word = new StringBuilder();
                        for (Character ch : charactersInWord) {
                            word.append(ch);
                        }
                        writtenWord = new Word(word.toString(), controller.isSoundexDictionary());
                        if (!controller.findWordInDicctionary(writtenWord)) {
                            try {
                                writtenWord.setPos(startCursor - 1);
                                writtenWord.setLine(getCurrentRow(startCursor));
                                if (!controller.isSuggestionUsed()){
                                    controller.addMispelledWord(writtenWord);
                                    if (!containsHighlight(auxIdx, auxIdx + word.length())) {
                                        highlighter.addHighlight(auxIdx == 0 ? auxIdx : (auxIdx + 1), (auxIdx == 0 ? auxIdx : (auxIdx + 1)) + word.length(), painterUnderline);
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

    public void removeHighlights() {
        highlighter.removeAllHighlights();
    }

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

    public void removeUnderlineForWord(int cursorPos, int length) {
        for (Highlighter.Highlight highlight : highlights) {
            if ((highlight.getStartOffset() >= (cursorPos + length)) && (highlight.getEndOffset() >= (cursorPos + length)) && (highlight.getStartOffset() == highlight.getEndOffset())) {
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

    public void resizeNotepad(int width, int height) {
        this.setSize(new Dimension((width * 65 / 100), height));
        this.setPreferredSize(new Dimension((width * 65 / 100), height));
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
            if (h.getPainter() instanceof LayeredHighlighter.LayerPainter){
                highlighter.removeHighlight(h);
            }
        }
    }
}
