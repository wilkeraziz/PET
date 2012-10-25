/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.frontend.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import pet.config.ContextHandler;
import pet.frontend.components.AbstractUnitGUI;
import pet.signal.PETEditOperationEvent;
import pet.usr.adapter.EditionStatus;
import pet.usr.handler.UnitHandler;

/**
 *
 * @author waziz
 */
public class TrimActionListener implements ActionListener {

    final static SimpleAttributeSet keyWord = new SimpleAttributeSet();

    public void actionPerformed(ActionEvent ae) {
        if (!UnitHandler.getEditingStatusProvider().getEditionStatus().equals(EditionStatus.EDITING)) {
            return;
        }
        final AbstractUnitGUI gui = (AbstractUnitGUI) UnitHandler.getActiveTarget();
        final Document doc = gui.getDocument();
        try {
            final String text = doc.getText(0, doc.getLength());
            final int oldlen = text.length();
            doc.remove(0, doc.getLength());
            doc.insertString(0, text.trim().replaceAll("\\s+", " "), keyWord);
            ContextHandler.signalManager().fire(new PETEditOperationEvent(new PETEditOperationEvent.Trim(oldlen - doc.getLength())));
        } catch (BadLocationException ex) {
            Logger.getLogger(TrimActionListener.class.getName()).log(Level.SEVERE, null, ex);
        }


    }
}
