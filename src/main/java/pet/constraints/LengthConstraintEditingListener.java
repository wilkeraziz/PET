/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.constraints;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter;
import pet.annotation.Status;
import pet.annotation.Unit;
import pet.annotation.adapter.AbstractEditingListener;
import pet.annotation.adapter.StatusAdapter;
import pet.config.ContextHandler;
import pet.frontend.FacadeController;
import pet.frontend.components.UnitGUI;
import pet.usr.adapter.EditionStatus;
import pet.usr.handler.UnitHandler;

/**
 *
 * @author waziz
 */
public class LengthConstraintEditingListener extends AbstractEditingListener implements FacadeController {

    private final JLabel left;
    private final String ideal;
    private final String preferable;
    private final String max;
    private int idealLen;
    private int preferableLen;
    private int maxLen;
    private final UnitGUI source;
    private final UnitGUI target;
    private final String hide;
    private final static Color GREEN = new Color(0, 153, 0);
    private final static Highlighter.HighlightPainter redHighlight = new DefaultHighlightPainter(Color.RED);
    private final static Font defaultFont = new Font("", Font.BOLD, 12);
    private final static Font bigFont = new Font("", Font.BOLD, 15);
    private Unit currentTask;
    private int lll = 0;

    public LengthConstraintEditingListener(final JLabel left, final UnitGUI source, final UnitGUI target) {
        this.left = left;
        this.ideal = ContextHandler.lengthConstraints().get("ideal");
        this.preferable = ContextHandler.lengthConstraints().get("preferable");
        this.max = ContextHandler.lengthConstraints().get("max");
        this.source = source;
        this.target = target;
        hide = ContextHandler.hideIfNotEditing();
        currentTask = null;
    }

    public void insert(final DocumentEvent event) {
        updateFacade();
    }

    public void remove(final DocumentEvent event) {
        updateFacade();
    }

    public void change(final DocumentEvent event) {
        updateFacade();
    }

    @Override
    public void updateFacade() {
        final EditionStatus editingStatus = UnitHandler.getEditingStatusProvider().getEditionStatus();
        final Unit task = UnitHandler.getUnitProvider().getCurrentTask();
        if (task != null) {
            final Status taskStatus = task.getStatus();
            if (editingStatus.equals(EditionStatus.EDITING)) {
                paint(editingStatus, taskStatus, task);
            } else if (hide.equals("never")) {
                paint(editingStatus, taskStatus, task);
            } else if (hide.equals("always")) {
                clear();
            } else if (hide.equals("undone") && taskStatus.equals(StatusAdapter.FINISHED)) {
                paint(editingStatus, taskStatus, task);
            } else {
                clear();
            }
        } else {
            clear();
        }

    }

    private int getLength(final EditionStatus editingStatus, final Unit task) {
        int len = 0;
        String text = null;
        if (editingStatus.equals(EditionStatus.EDITING)) {
            text = target.getText();
        } else {
            text = task.getTarget().toString();
        }
        text = text.trim();
        if (!text.isEmpty()) {
            char previous = text.charAt(0);
            len = 1;
            for (int i = 1; i < text.length(); i++) {
                char current = text.charAt(i);
                if (!(previous == ' ' && current == ' ')) {
                    len++;
                }
                previous = current;
            }
        }
        lll = len;
        return len;
    }

    private void paint(final EditionStatus editingStatus, final Status taskStatus, final Unit task) {
        updateConstraints();
        int len = getLength(editingStatus, task);
        if (len > maxLen) {
            setBlack(editingStatus);
        } else if (len > idealLen) {
            setRed(editingStatus);
        } else {
            if (len > preferableLen) {
                setBlue(editingStatus);
            } else {
                setGreen(editingStatus);
            }
        }
        left.setText(Integer.toString(idealLen - len) + " left | " + Integer.toString(len) + " total");
    }

    private void clear() {
        left.setText("");
    }

    private void adjustFont(final EditionStatus status) {
        if (!status.equals(EditionStatus.EDITING)) {
            target.underlying().setFont(ContextHandler.editableFont());
            target.underlying().setBackground(ContextHandler.editableBackGroundColor());
        }
    }

    private void setNormal() {
        if (left.isOpaque()) {
            left.setOpaque(false);
        }
        if (!left.getFont().equals(defaultFont)) {
            left.setFont(defaultFont);
        }
        if (!target.underlying().getFont().equals(defaultFont)) {
            target.underlying().setFont(defaultFont);
        }
        target.underlying().getHighlighter().removeAllHighlights();
    }

    private void setGreen(final EditionStatus status) {
        setNormal();
        if (!left.getForeground().equals(GREEN)) {
            left.setForeground(GREEN);
        }
        changeTextColor(GREEN);
        
        adjustFont(status);
    }

    private boolean changeTextColor(final Color color){
        if (target.underlying().getDocument().getLength() != 0 && !target.underlying().getForeground().equals(color)){
            target.underlying().setForeground(color);
            return true;
        }
        return false;
    }

    private void setBlue(final EditionStatus status) {
        setNormal();
        if (!left.getForeground().equals(Color.BLUE)) {
            left.setForeground(Color.BLUE);
        }
        changeTextColor(Color.BLUE);
        adjustFont(status);
    }

    private void setRed(final EditionStatus status) {
        setNormal();
        if (!left.getForeground().equals(Color.RED)) {
            left.setForeground(Color.RED);
        }
        changeTextColor(Color.RED);
        adjustFont(status);
    }

    private void setBlack(final EditionStatus status) {
        if (!left.isOpaque()) {
            left.setOpaque(true);
            left.setBackground(Color.RED);
            left.setForeground(Color.BLACK);
            left.setFont(bigFont);
        }
        try {
            target.underlying().getHighlighter().addHighlight(0, target.getText().length(), redHighlight);
        } catch (final BadLocationException ex) {
        }

        if (changeTextColor(Color.BLACK)) {
            target.underlying().setFont(bigFont);
        }
        adjustFont(status);
    }

    private void updateConstraints() {
        final Unit editing = UnitHandler.getUnitProvider().getCurrentTask();
        if (editing != null) {
            if (editing != currentTask) {
                currentTask = editing;
                idealLen = Integer.parseInt(currentTask.getAttributes().get(ideal));
                preferableLen = Integer.parseInt(currentTask.getAttributes().get(preferable));
                maxLen = Integer.parseInt(currentTask.getAttributes().get(max));
            }
        } else {
            idealLen = 0;
            preferableLen = 0;
            maxLen = 0;
        }
    }
}
