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
import pet.annotation.adapter.ResultSentenceAdapter;
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
public class ContextMouseAdapter extends AbstractPopupMouseAdapter {

    protected final JMenu mono2;
    protected final JMenu bilingual2;
    protected final JMenu sources;
    protected final JMenu references;
    protected final JMenu targets;
    protected final JMenu revisions;
    private final boolean displayingReferences;

    protected ContextMouseAdapter(final JPopupMenu popupMenu,
            final JMenu monolingual,
            final JMenu bilingual,
            final JMenu mono2,
            final JMenu bilingual2,
            final JMenu sources,
            final JMenu references,
            final JMenu targets,
            final JMenu revisions) {
        super(popupMenu, monolingual, bilingual);
        this.mono2 = mono2;
        this.bilingual2 = bilingual2;
        this.sources = sources;
        this.references = references;
        this.targets = targets;
        this.revisions = revisions;
        this.displayingReferences = ContextHandler.showReference();
        references.setVisible(this.displayingReferences);
    }

    @Override
    protected TransferType getType() {
        return TransferArea.TransferType.CONTEXT;
    }

    @Override
    protected List<PETDataBase> monoDict() {
        return ContextHandler.s2s();
    }

    @Override
    protected List<PETDataBase> biDict() {
        return ContextHandler.s2t();
    }

    protected List<PETDataBase> monoDict2() {
        return ContextHandler.t2t();
    }

    protected List<PETDataBase> biDict2() {
        return ContextHandler.t2s();
    }

    @Override
    protected void clearSubMenus() {
        super.clearSubMenus();
        mono2.removeAll();
        bilingual2.removeAll();
        sources.removeAll();
        references.removeAll();
        targets.removeAll();
        revisions.removeAll();
    }

    @Override
    protected void addBehaviour(final PETDictionaryMenuItem dict) {
    }

    @Override
    protected void addBehaviour(final PETSentenceMenuItem sentence) {
        sentence.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent ae) {
                UnitHandler.getContextPane().setSentence(sentence.getSentence());
            }
        });
    }

    @Override
    protected void addBehaviour(final PETResultMenuItem result) {
        result.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent ae) {
                UnitHandler.getContextPane().setSentence(new ResultSentenceAdapter(result.getResult()));
            }
        });
    }

    @Override
    protected void extra(final String selection) {

        feedDictionaryMenus(selection, monoDict2(), mono2);

        feedDictionaryMenus(selection, biDict2(), bilingual2);

        //MTs and revisions
        final EditableUnit task = (EditableUnit) UnitHandler.getUnitProvider().getCurrentTask();

        if (task == null || UnitHandler.getEditingStatusProvider().getEditionStatus() != EditionStatus.EDITING) {
            sources.setEnabled(false);
            references.setEnabled(false);
            targets.setEnabled(false);
            revisions.setEnabled(false);
        } else {
            final ButtonGroup group = new ButtonGroup();
            final String producer = UnitHandler.getContextPane().getSentence().getProducer();
            feedSentencesMenu(producer, task.getSources(), sources, group);
            if (displayingReferences) {
                feedSentencesMenu(producer, task.getReferences(), references, group);
            }
            feedSentencesMenu(producer, task.getTargets(), targets, group);
            feedRevisionsMenu(producer, task.getUnitResults(), revisions, group);
        }
    }
}
