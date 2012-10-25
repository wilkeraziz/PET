/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.frontend.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import pet.config.ContextHandler;
import pet.frontend.components.AbstractUnitGUI;
import pet.signal.PETEditOperationEvent;
import pet.usr.adapter.EditionStatus;
import pet.usr.handler.UnitHandler;

/**
 *
 * @author waziz
 */
public class InsertActionListener implements ActionListener {

    public void actionPerformed(ActionEvent ae) {
        if (!UnitHandler.getEditingStatusProvider().getEditionStatus().equals(EditionStatus.EDITING)) {
            return;
        }
        final AbstractUnitGUI gui = (AbstractUnitGUI) UnitHandler.getActiveTarget();
        final String selected = gui.getSelectedText();
        final int offset = gui.getSelectionStart();
        final String message = (selected != null && !selected.isEmpty()) ? "Replacing '" + selected + "'" : "Insertion: " ;
        final String insertion = JOptionPane.showInputDialog(message, selected);
        if (insertion != null && !insertion.isEmpty()) { // TODO: memorize operations
            gui.replaceSelection(insertion);
            if (selected != null && !selected.isEmpty()){
                ContextHandler.signalManager().fire(new PETEditOperationEvent(new PETEditOperationEvent.Substitution(offset, selected, insertion)));
            } else{
                ContextHandler.signalManager().fire(new PETEditOperationEvent(new PETEditOperationEvent.Insertion(offset, insertion)));
            }
        }

    }
}
