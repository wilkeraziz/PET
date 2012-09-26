/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.frontend.menu;

import javax.swing.JMenuItem;

/**
 *
 * @author waziz
 */
public class PETDictionaryMenuItem extends JMenuItem {

    private final String dictionary;

    public PETDictionaryMenuItem(final String dictionary, final String entry) {
        super(entry);
        this.dictionary = dictionary;
    }

    public String getDictionary() {
        return dictionary;
    }
}
