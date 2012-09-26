/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.usr.adapter;

import pet.annotation.Unit;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import pet.config.ContextHandler;
import pet.frontend.components.UnitGUI;
import pet.usr.handler.UnitHandler;

/**
 *
 * @author waziz
 */
public class CurrentPrinter implements CurrentSelector {

    private final JLabel currentLabel;
    private final UnitGUI currentPane;
    final JButton acceptButton;
    final JButton restoreButton;
    private final JLabel currentHistory;
    private final JLabel currentPartialTime;
    private final JLabel currentTotalTime;
    private final boolean acceptingMTs;

    public CurrentPrinter(
            final JLabel currentLabel,
            final UnitGUI currentPane,
            final JButton acceptButton,
            final JButton restoreButton,
            final JLabel currentHistory,
            final JLabel currentPartialTime,
            final JLabel currentTotalTime) {
        this.currentLabel = currentLabel;
        this.currentPane = currentPane;
        this.acceptButton = acceptButton;
        this.restoreButton = restoreButton;
        this.currentHistory = currentHistory;
        this.currentPartialTime = currentPartialTime;
        this.currentTotalTime = currentTotalTime;
        this.acceptingMTs = ContextHandler.autoAccept();
    }

    public void print(final Unit t) {
        final EditableUnit editing = (EditableUnit) t;
        if (editing == null) {
            currentLabel.setText("");
            currentPane.clear();
            currentPane.underlying().setBackground(ContextHandler.editableBackGroundColor());
            currentHistory.setText("");
            currentPartialTime.setText("");
            currentTotalTime.setText("");

        } else {
            currentHistory.setText(Integer.toString(editing.getUnitResults().size()));
            currentTotalTime.setText("total: " + Formatter.getSecondFormatter().print(editing.getTotalTimeSpent().toPeriod().normalizedStandard()));
            final Unit original = editing.getOriginalUnit();
            if (ContextHandler.showSentenceId()) {
                currentLabel.setText(editing.getId());
            } else{
                currentLabel.setText("");
            }
            if (original instanceof pet.annotation.adapter.TranslationUnit) {
                if (ContextHandler.showReference()) {
                    currentPane.setSentence(editing.getReference());
                } else {
                    currentPane.setSentence(editing.getSource());
                }
                acceptButton.setEnabled(acceptingMTs && false);
            } else if (original instanceof pet.annotation.adapter.PostEditingUnit) {
                if (ContextHandler.showReference()) {
                    currentPane.setSentence(editing.getReference());
                } else {
                    currentPane.setSentence(original.getTarget());
                }
                acceptButton.setEnabled(acceptingMTs && true);
            }
        }
    }
}
