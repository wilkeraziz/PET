/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.frontend.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.text.BadLocationException;
import pet.frontend.components.AbstractUnitGUI;
import pet.usr.adapter.EditionStatus;
import pet.usr.handler.UnitHandler;

/**
 *
 * @author waziz
 */
public class DragFromAndDropToTextHandler extends DragFromTextHandler {


    /**
     * Perform the actual import.  This method supports both drag and
     * drop and cut/copy/paste.
     */
    @Override
    public boolean importData(final TransferHandler.TransferSupport support) {
        //If we can't handle the import, bail now.
        if (!canImport(support) || UnitHandler.getEditingStatusProvider().getEditionStatus() != EditionStatus.EDITING) {
            return false;
        }

        //Fetch the data -- bail if this fails
        String data;
        try {
            data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
        } catch (final UnsupportedFlavorException e) {
            return false;
        } catch (final java.io.IOException e) {
            return false;
        }

        final AbstractUnitGUI tc = (AbstractUnitGUI) support.getComponent();
        tc.replaceSelection(data);
        return true;
    }

    /**
     * When the export is complete, remove the old text if the action
     * was a move.
     */
    @Override
    protected void exportDone(final JComponent c, final Transferable data, final int action) {
        if (action == MOVE && (p0 != null) && (p1 != null)
                && (p0.getOffset() != p1.getOffset())) {
            try {
                final AbstractUnitGUI tc = (AbstractUnitGUI) c;
                tc.getDocument().remove(p0.getOffset(),
                        p1.getOffset() - p0.getOffset());
            } catch (final BadLocationException e) {
                System.out.println("Can't remove text from source.");
            }
        }
    }
}
