/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.frontend.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
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
public class ShiftActionListener implements ActionListener {

    final static SimpleAttributeSet keyWord = new SimpleAttributeSet();

    public void actionPerformed(ActionEvent ae) {
        if (!UnitHandler.getEditingStatusProvider().getEditionStatus().equals(EditionStatus.EDITING)) {
            return;
        }

        final AbstractUnitGUI gui = (AbstractUnitGUI) UnitHandler.getActiveTarget();
        final String selected = gui.getSelectedText();
        if (selected != null && !selected.isEmpty()) { // TODO: memorize operations
            final int start = gui.getSelectionStart();
            final int end = gui.getSelectionEnd();
            final Document doc = gui.getDocument();
            int destination = -1;
            if (ae.getSource() instanceof JMenuItem) {
                final JMenuItem source = (JMenuItem) (ae.getSource());
                if (source.getText().equals("BOS")) {
                    try {
                        destination = 0;
                        gui.replaceSelection("");
                        doc.insertString(destination, selected.trim() + " ", keyWord);
                    } catch (BadLocationException ex) {
                        Logger.getLogger(ShiftActionListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (source.getText().equals("EOS")) {
                    try {
                        gui.replaceSelection("");
                        destination = doc.getLength();
                        doc.insertString(destination, " " + selected.trim(), keyWord);
                    } catch (BadLocationException ex) {
                        Logger.getLogger(ShiftActionListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (source.getText().equals("custom")) {
                    final String strDist = JOptionPane.showInputDialog("Distance: ");
                    try {
                        final int dist = Integer.parseInt(strDist.replaceAll("^[+]", ""));
                        destination = move(doc, gui, start, end, selected, dist);
                    } catch (final Exception e) {
                    }
                } else {
                    try {
                        final String opt = source.getText().replaceAll("^[+]", "");
                        final int dist = Integer.parseInt(opt);
                        destination = move(doc, gui, start, end, selected, dist);
                    } catch (final Exception e) {
                    }
                }
            } else {
                final String strDist = JOptionPane.showInputDialog("Distance: ");
                try {
                    final int dist = Integer.parseInt(strDist.replaceAll("^[+]", ""));
                    destination = move(doc, gui, start, end, selected, dist);
                } catch (final Exception e) {
                }
            }
            if (destination >= 0) {
                ContextHandler.signalManager().fire(new PETEditOperationEvent(new PETEditOperationEvent.Shift(selected, start, destination)));
            }

        }

    }

    private int move(final Document doc, final AbstractUnitGUI gui, final int start, final int end, final String text, final int dist) {
        gui.replaceSelection("");
        int destination = -1;
        if (dist > 0) {
            try {
                final String tail = doc.getText(start, doc.getLength() - start);
                int i = 0;
                for (; i < tail.length(); i++) { //consume blank
                    if (tail.charAt(i) != ' ') {
                        break;
                    }
                }
                int skiped = 0;

                while (skiped < dist && i < tail.length()) {
                    for (; i < tail.length(); i++) { // consume non-blank
                        if (tail.charAt(i) == ' ') {
                            skiped++;
                            break;
                        }
                    }
                    for (; i < tail.length(); i++) { //consume blank
                        if (tail.charAt(i) != ' ') {
                            break;
                        }
                    }
                }
                destination = start + i;
                if (start + i < doc.getLength()) {
                    doc.insertString(destination, text.trim() + " ", keyWord);
                } else {
                    doc.insertString(destination, " " + text.trim(), keyWord);
                }
            } catch (BadLocationException ex) {
                Logger.getLogger(ShiftActionListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (dist < 0) {
            try {
                final int d = -dist;
                final String head = doc.getText(0, start);
                int i = start - 1;
                for (; i >= 0; i--) { //consume blank
                    if (head.charAt(i) != ' ') {
                        break;
                    }
                }
                int skiped = 0;

                while (skiped < d && i >= 0) {
                    for (; i >= 0; i--) { // consume non-blank
                        if (head.charAt(i) == ' ') {
                            skiped++;
                            break;
                        }
                    }
                    for (; i >= 0; i--) { //consume blank
                        if (head.charAt(i) != ' ') {
                            break;
                        }
                    }
                }
                if (i > 0) {
                    destination = i + 1;
                    doc.insertString(destination, " " + text.trim(), keyWord);
                } else {
                    destination = 0;
                    doc.insertString(destination, text.trim() + " ", keyWord);
                }
            } catch (BadLocationException ex) {
                Logger.getLogger(ShiftActionListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return destination;
    }
}
