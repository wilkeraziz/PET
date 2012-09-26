/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.frontend.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import pet.config.ContextHandler;
import pet.db.PETDataBase;
import pet.usr.adapter.EditableUnit;
import pet.usr.adapter.EditionStatus;
import pet.usr.handler.TransferArea;
import pet.usr.handler.TransferArea.TransferType;
import pet.usr.handler.UnitHandler;

/**
 *
 * @author waziz
 */
public class ActiveSourceMouseAdapter extends SourceMouseAdapter {

    protected final JMenu sources;

    protected ActiveSourceMouseAdapter(final JPopupMenu popupMenu, final JMenu monolingual, final JMenu bilingual, final JMenu sources) {
        super(popupMenu, monolingual, bilingual);
        this.sources = sources;
    }

    @Override
    protected TransferType getType() {
        return TransferArea.TransferType.SOURCE;
    }

    @Override
    protected List<PETDataBase> monoDict() {
        return ContextHandler.s2s();
    }

    @Override
    protected List<PETDataBase> biDict() {
        return ContextHandler.s2t();
    }

    @Override
    protected void clearSubMenus() {
        super.clearSubMenus();
        sources.removeAll();
    }

    @Override
    protected void addBehaviour(final PETSentenceMenuItem sentence) {
        sentence.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent ae) {
                UnitHandler.getActiveSource().setSentence(sentence.getSentence());
            }
        });
    }

    @Override
    protected void extra(final String selection) {
        super.extra(selection);
        //MTs and revisions
        final EditableUnit task = (EditableUnit) UnitHandler.getUnitProvider().getCurrentTask();

        if (task == null || UnitHandler.getEditingStatusProvider().getEditionStatus() != EditionStatus.EDITING) {
            sources.setEnabled(false);
        } else {
            feedSentencesMenu(UnitHandler.getActiveSource().getSentence().getProducer(), task.getSources(), sources, new ButtonGroup());
        }
    }
}
