/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.frontend.menu;

import java.util.List;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import pet.config.ContextHandler;
import pet.db.PETDataBase;
import pet.usr.handler.TransferArea;
import pet.usr.handler.TransferArea.TransferType;

/**
 *
 * @author waziz
 */
public class SourceMouseAdapter extends AbstractPopupMouseAdapter {

    protected SourceMouseAdapter(final JPopupMenu popupMenu, final JMenu monolingual, final JMenu bilingual) {
        super(popupMenu, monolingual, bilingual);
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
    protected void addBehaviour(final PETDictionaryMenuItem dict) {
    }

    @Override
    protected void addBehaviour(final PETSentenceMenuItem sentence) {
    }

    @Override
    protected void addBehaviour(final PETResultMenuItem result) {
    }

    @Override
    protected void extra(final String selection) {
    }
}
