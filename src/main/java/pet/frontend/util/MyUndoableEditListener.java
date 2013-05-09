/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.frontend.util;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

/**
 *
 * @author waziz
 */
public class MyUndoableEditListener implements UndoableEditListener {

    protected final UndoManager undoer;
    protected final UndoAction undo = new UndoAction();
    protected final RedoAction redo = new RedoAction();

    protected class UndoAction extends AbstractAction {
        
        UndoAction() {
            super("Undo");
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent ae) {
            try {
                undoer.undo();
            } catch (final CannotUndoException ex) {
            }
            updateUndoState();
            redo.updateRedoState();
        }

        protected void updateUndoState() {
            if (undoer.canUndo()) {
                setEnabled(true);
                putValue(Action.NAME, undoer.getUndoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Undo");
            }
        }
    }

    protected class RedoAction extends AbstractAction {

        RedoAction() {
            super("Redo");
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent ae) {
            try {
                undoer.redo();
            } catch (final CannotUndoException ex) {
            }
            updateRedoState();
            undo.updateUndoState();
        }

        protected void updateRedoState() {
            if (undoer.canRedo()) {
                setEnabled(true);
                putValue(Action.NAME, undoer.getRedoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Redo");
            }
        }
    }

    public MyUndoableEditListener(final UndoManager undoer) {
        this.undoer = undoer;
    }

    public Action getUndo() {
        return undo;
    }

    public Action getRedo() {
        return redo;
    }

    public void undoableEditHappened(final UndoableEditEvent e) {
        undoer.addEdit(e.getEdit());
        undo.updateUndoState();
        redo.updateRedoState();
    }
}
