/* Created by andreea on 08/05/2020 */
package Presentation;

import Domain.Language;
import Utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class LanguageSelectorRenderer extends DefaultListCellRenderer {
    private Map<String, ImageIcon> iconMap = new HashMap<>();
    private Color background = new Color(0, 100, 255, 15);
    private Color defaultBackground = (Color) UIManager.get("List.background");

    public LanguageSelectorRenderer() {
        iconMap.put("Español", new ImageIcon(Constants.PATH_ES_ICON));
        iconMap.put("English", new ImageIcon(Constants.PATH_EN_ICON));
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        Language language = (Language) value;
        this.setText(language.getName());
        this.setIcon(language.getIcon());
        if (!isSelected) {
            this.setBackground(index % 2 == 0 ? background : defaultBackground);
        }
        return this;
    }
}
