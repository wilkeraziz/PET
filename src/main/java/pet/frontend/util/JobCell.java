/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pet.frontend.util;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author waziz
 */
public class JobCell extends JLabel implements ListCellRenderer{

    public JobCell() {
        setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        // Assumes the stuff in the list has a pretty toString
        setText(value.toString());
        // based on some of the object's property sets the foreground color
        setForeground(Color.red);
        return this;
    }

}





