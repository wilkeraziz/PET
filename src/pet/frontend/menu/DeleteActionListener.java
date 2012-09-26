/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.frontend.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import pet.frontend.components.AbstractUnitGUI;
import pet.usr.adapter.EditionStatus;
import pet.usr.handler.UnitHandler;

/**
 *
 * @author waziz
 */
public class DeleteActionListener implements ActionListener {

    public void actionPerformed(ActionEvent ae) {
        if (!UnitHandler.getEditingStatusProvider().getEditionStatus().equals(EditionStatus.EDITING)) {
            return;
        }
        final AbstractUnitGUI gui = (AbstractUnitGUI) UnitHandler.getActiveTarget();
        final String selected = gui.getSelectedText();
        if (selected != null && !selected.isEmpty()) {
            gui.replaceSelection("");
        }


    }
}
