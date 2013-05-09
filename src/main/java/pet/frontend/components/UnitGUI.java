/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.frontend.components;

import javax.swing.JTextPane;
import pet.annotation.Segment;

/**
 *
 * @author waziz
 */
public interface UnitGUI {

    public enum UnitGUIType{
        SOURCE,
        CONTEXT,
        TARGET
    };

    UnitGUIType getType();

    int getId();

    String getText();

    void setSentence(final Segment sentence);

    Segment getSentence();

    void preview(final Segment sentence);

    void restore();

    void clear();

    JTextPane underlying();
    
}
