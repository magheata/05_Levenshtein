/**
 * AUTHORS: Rafael Adrián Gil Cañestro
 * Miruna Andreea Gheata
 */package Infrastructure.SuggestionClients;

import Application.Controller;
import Domain.Interfaces.ISuggestionClient;
import Domain.Word;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.function.Function;

public class WordSuggestionsClient implements ISuggestionClient<JTextComponent> {
    private Function<String, ArrayList<Word>> suggestionProvider;
    private Controller controller;

    public WordSuggestionsClient(Function<String, ArrayList<Word>> suggestionProvider, Controller controller) {
        this.suggestionProvider = suggestionProvider;
        this.controller = controller;
    }

    @Override
    public Point getPopupLocation(JTextComponent invoker) {
        int caretPosition = invoker.getCaretPosition();
        try {
            Rectangle2D rectangle2D = invoker.modelToView(caretPosition);
            return new Point((int) rectangle2D.getX(), (int) (rectangle2D.getY() + rectangle2D.getHeight()));
        } catch (BadLocationException e) {
        }
        return null;
    }

    @Override
    public void setSelectedText(JTextComponent tp, String selectedValue) {
        int cp = tp.getCaretPosition();
        try {
            if (cp == 0 || tp.getText(cp - 1, 1).trim().isEmpty()) {
                tp.getDocument().insertString(cp, selectedValue, null);
            } else {
                int previousWordIndex = Utilities.getPreviousWord(tp, cp);
                String text = tp.getText(previousWordIndex, cp - previousWordIndex);
                if (selectedValue.startsWith(text)) {
                    tp.getDocument().insertString(cp, selectedValue.substring(text.length()), null);
                } else {
                    tp.getDocument().insertString(cp, selectedValue, null);
                }
                controller.setSuggestionUsed(true);
            }
        } catch (BadLocationException e) {
        }
    }

    @Override
    public ArrayList<Word> get(JTextComponent tp) {
        int cp = tp.getCaretPosition();
        try {
            if (cp != 0) {
                String text = tp.getText(cp - 1, 1);
                if (text.trim().isEmpty()) {
                    return null;
                }
            }
            int previousWordIndex = Utilities.getPreviousWord(tp, cp);
            String text = tp.getText(previousWordIndex, cp - previousWordIndex);
            return suggestionProvider.apply(text.trim());
        } catch (BadLocationException e) {
        }
        return null;
    }
}
