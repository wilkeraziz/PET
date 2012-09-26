/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.frontend.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import org.joda.time.DateTime;
import pet.annotation.Segment;
import pet.annotation.adapter.ResultSentenceAdapter;
import pet.config.ContextHandler;
import pet.frontend.components.AbstractUnitGUI;
import pet.signal.DictionarySignalPackage;
import pet.signal.ReplacementSignalPackage;
import pet.signal.SignalAdapter;
import pet.usr.adapter.EditableUnit;
import pet.usr.adapter.EditionStatus;
import pet.usr.handler.UnitHandler;

/**
 *
 * @author waziz
 */
public class ActiveTargetMouseAdapter extends TargetMouseAdapter {

    protected final JMenu targets;
    protected final JMenu revisions;

    protected ActiveTargetMouseAdapter(final JPopupMenu popupMenu, final JMenu monolingual, final JMenu bilingual, final JMenu inverted, final JMenu targets, final JMenu revisions) {
        super(popupMenu, monolingual, bilingual, inverted);
        this.targets = targets;
        this.revisions = revisions;
    }

    @Override
    protected void clearSubMenus() {
        super.clearSubMenus();
        targets.removeAll();
        revisions.removeAll();
    }

    private boolean overwriteEditing() {
        if (!UnitHandler.getUnitProvider().getCurrentTask().getTarget().toString().equals(UnitHandler.getActiveTarget().getSentence().toString())) {
            final int option = JOptionPane.showConfirmDialog(null, "This operation will discard the current changes to the active unit.\nDo you want to proceed?", "Confirmation", JOptionPane.YES_NO_OPTION);
            return option == JOptionPane.YES_OPTION;
        } else {
            return true;
        }
    }

    @Override
    protected void addBehaviour(final PETDictionaryMenuItem dict) {
        dict.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent ae) {
                if (!UnitHandler.getEditingStatusProvider().getEditionStatus().equals(EditionStatus.EDITING)) {
                    return;
                }
                final AbstractUnitGUI gui = (AbstractUnitGUI) UnitHandler.getActiveTarget();
                final int offset = gui.getSelectionStart();
                final String out  = gui.getSelectedText();
                if (out != null && !out.isEmpty()) { // TODO: memorize operations
                    final String in = dict.getText();
                    gui.replaceSelection(in);
                    ContextHandler.signalManager().fire(SignalAdapter.DICTIONARY_LOOKPUP, DictionarySignalPackage.getReplacement(offset, out, in, new DateTime(System.currentTimeMillis()), dict.getDictionary()));
                }
            }
        });
    }

    @Override
    protected void addBehaviour(final PETSentenceMenuItem sentence) {
        sentence.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent ae) {
                if (overwriteEditing()) {
                    final String out = UnitHandler.getActiveTarget().getSentence().getProducer();
                    final String in = sentence.getSentence().getProducer();
                    ContextHandler.signalManager().fire(SignalAdapter.REPLACEMENT, new ReplacementSignalPackage(out, in, new DateTime(System.currentTimeMillis())));
                    UnitHandler.getActiveTarget().setSentence(sentence.getSentence());
                }
            }
        });
    }

    @Override
    protected void addBehaviour(final PETResultMenuItem result) {
        result.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent ae) {
                if (overwriteEditing()) {
                    final String out = UnitHandler.getActiveTarget().getSentence().getProducer();
                    final Segment rSentence = new ResultSentenceAdapter(result.getResult());
                    final String in = rSentence.getProducer();
                    ContextHandler.signalManager().fire(SignalAdapter.REPLACEMENT, new ReplacementSignalPackage(out, in, new DateTime(System.currentTimeMillis())));
                    UnitHandler.getActiveTarget().setSentence(rSentence);
                }
            }
        });
    }

    @Override
    protected void extra(final String selection) {
        super.extra(selection);

        //MTs and revisions
        final EditableUnit task = (EditableUnit) UnitHandler.getUnitProvider().getCurrentTask();

        if (task == null || UnitHandler.getEditingStatusProvider().getEditionStatus() != EditionStatus.EDITING) {
            targets.setEnabled(false);
            revisions.setEnabled(false);
        } else {
            final String producer = UnitHandler.getActiveTarget().getSentence().getProducer();
            final ButtonGroup group = new ButtonGroup();
            feedSentencesMenu(producer, task.getTargets(), targets, group);
            feedRevisionsMenu(producer, task.getUnitResults(), revisions, group);
        }
    }
}
