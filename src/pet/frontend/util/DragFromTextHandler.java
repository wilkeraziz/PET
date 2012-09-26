/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.frontend.util;

/**
 * TextTransferHandler.java is used by the TextCutPaste.java example.
 */
import java.awt.datatransfer.*;
import javax.swing.*;
import javax.swing.text.*;
import pet.frontend.components.AbstractUnitGUI;

/**
 * An implementation of TransferHandler that adds support for the
 * import and export of text using drag and drop and cut/copy/paste.
 */
public class DragFromTextHandler extends TransferHandler {
    //Start and end position in the source text.
    //We need this information when performing a MOVE
    //in order to remove the dragged text from the source.

    Position p0 = null, p1 = null;

    /**
     * Perform the actual import.  This method supports both drag and
     * drop and cut/copy/paste.
     */
    @Override
    public boolean importData(final TransferHandler.TransferSupport support) {
        return false;
    }

    /**
     * Bundle up the data for export.
     * TODO: track "from" and "to" using UnitGUI.getType() and UnitGUI.getId()
     */
    @Override
    protected Transferable createTransferable(final JComponent c) {
        AbstractUnitGUI source = (AbstractUnitGUI) c;
        int start = source.getSelectionStart();
        int end = source.getSelectionEnd();
        final Document doc = source.getDocument();
        if (start == end) {
            return null;
        }
        try {
            p0 = doc.createPosition(start);
            p1 = doc.createPosition(end);
        } catch (final BadLocationException e) {
            System.out.println(
                    "Can't create position - unable to remove text from source.");
        }
        return new StringSelection(source.getSelectedText());
    }

    /**
     * These text fields handle both copy and move actions.
     */
    @Override
    public int getSourceActions(final JComponent c) {
        return COPY_OR_MOVE;
    }

    /**
     * Nothing to be done when export is complete, because the source is read-only
     */
    @Override
    protected void exportDone(final JComponent c, final Transferable data, final int action) {
    }

    /**
     * We only support importing strings.
     */
    public boolean canImport(TransferHandler.TransferSupport support) {
        // we only import Strings
        if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return false;
        }
        return true;
    }
}
