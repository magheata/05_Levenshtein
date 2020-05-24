/* Created by andreea on 15/05/2020 */
package Domain.Interfaces;

import Domain.Word;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public interface ISuggestionClient<C extends JComponent> {
    Point getPopupLocation(C invoker);
    void setSelectedText(C invoker, String selectedValue);
    ArrayList<Word> get(C invoker);
}
