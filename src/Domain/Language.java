/* Created by andreea on 08/05/2020 */
package Domain;

import javax.swing.*;

public class Language {
    private String name;
    private ImageIcon icon;

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
