/**
 * AUTHORS: Rafael Adrián Gil Cañestro
 * Miruna Andreea Gheata
 */
package Domain;

import javax.swing.*;

/**
 * Class that represents a Language
 */
public class Language {
    private String name;
    private ImageIcon icon;

    /**
     * Constructor of the class.
     * @param name
     * @param icon
     */
    public Language(String name, ImageIcon icon) {
        this.name = name;
        this.icon = icon;
    }


    public String getName() {
        return name;
    }

    public ImageIcon getIcon() {
        return icon;
    }

}
