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
public class TargetMouseAdapter extends AbstractPopupMouseAdapter {

    protected final JMenu inverted;

    protected TargetMouseAdapter(final JPopupMenu popupMenu, final JMenu monolingual, final JMenu bilingual, final JMenu inverted) {
        super(popupMenu, monolingual, bilingual);
        this.inverted = inverted;
    }

    @Override
    protected TransferType getType() {
        return TransferArea.TransferType.TARGET;
    }

    @Override
    protected List<PETDataBase> monoDict() {
        return ContextHandler.t2t();
    }

    @Override
    protected List<PETDataBase> biDict() {
        return ContextHandler.t2s();
    }

    protected List<PETDataBase> inverted() {
        return ContextHandler.s2t();
    }

    @Override
    protected void clearSubMenus() {
        super.clearSubMenus();
        inverted.removeAll();
    }

    @Override
    protected void addBehaviour(final PETDictionaryMenuItem dict) {
    }

    @Override
    protected void addBehaviour(final PETSentenceMenuItem item) {
    }

    @Override
    protected void addBehaviour(final PETResultMenuItem result) {
    }

    @Override
    protected void extra(final String selection) {
        //inverted translations
        feedDictionaryMenus(selection, inverted(), inverted);
    }
}
