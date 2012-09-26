/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.frontend.menu;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import pet.annotation.Segment;
import pet.annotation.UnitResult;
import pet.db.PETDataBase;
import pet.db.PETQueryResult;
import pet.frontend.components.AbstractUnitGUI;
import pet.usr.handler.TransferArea;
import pet.usr.handler.UnitHandler;

/**
 *
 * @author waziz
 */
public abstract class AbstractPopupMouseAdapter extends MouseAdapter {

    protected final JPopupMenu popup;
    protected final JMenu monolingual;
    protected final JMenu bilingual;

    protected AbstractPopupMouseAdapter(final JPopupMenu popupMenu, final JMenu monolingual, final JMenu bilingual) {
        popup = popupMenu;
        this.monolingual = monolingual;
        this.bilingual = bilingual;
    }

    @Override
    public final void mousePressed(final MouseEvent e) {
        maybeShowPopup(e);
    }

    @Override
    public final void mouseReleased(final MouseEvent e) {
        maybeShowPopup(e);
    }

    protected abstract TransferArea.TransferType getType();

    protected abstract List<PETDataBase> monoDict();

    protected abstract List<PETDataBase> biDict();

    protected void clearSubMenus() {
        monolingual.removeAll();
        bilingual.removeAll();
    }

    protected abstract void extra(final String selection);

    protected void feedDictionaryMenus(final String selection, final List<PETDataBase> dbs, final JMenu menu) {

        if (dbs.size() == 1) {
            if (feedDictionaryMenu(selection, dbs.get(0), menu) != 0) {
                menu.setEnabled(true);
            } else {
                menu.setEnabled(false);
            }
        } else {
            int m = 0;
            for (final PETDataBase db : dbs) {
                final JMenu dict = new JMenu(db.getAlias());
                if (feedDictionaryMenu(selection, db, dict) != 0) {
                    dict.setEnabled(true);
                    m++;
                } else {
                    dict.setEnabled(false);
                }
                menu.add(dict);
            }
            if (m == 0) {
                menu.setEnabled(false);
            } else {
                menu.setEnabled(true);
            }
        }
    }

    /**
     * Most units are ready-only, therefore their dictionaries do not require interaction
     * @param selection
     * @param db
     * @param menu
     * @return
     */
    protected int feedDictionaryMenu(final String selection, final PETDataBase db, final JMenu menu) {
        int n = 0;
        for (final PETQueryResult result : db.getData(selection)) { //TODO allow for relaxed queries
            n++;
            final PETDictionaryMenuItem item = new PETDictionaryMenuItem(db.getAlias(), result.toString());
            addBehaviour(item);
            menu.add(item);
        }
        return n;
    }

    protected abstract void addBehaviour(final PETDictionaryMenuItem dict);

    protected abstract void addBehaviour(final PETSentenceMenuItem sentence);

    protected abstract void addBehaviour(final PETResultMenuItem result);

    protected void feedSentencesMenu(final String currentProducer, final List<Segment> toDisplay, final JMenu menu, final ButtonGroup group) {
        if (toDisplay.size() > 0) {
            menu.setEnabled(true);
            
            for (final Segment sentence : toDisplay) {
                final String producer = sentence.getProducer();

                final PETSentenceMenuItem producerItem = new PETSentenceMenuItem(sentence);
                group.add(producerItem);
                if (producer.equals(currentProducer)) {
                    group.setSelected(producerItem.getModel(), true);
                }

                producerItem.addMouseMotionListener(new MouseMotionListener() {

                    public void mouseDragged(MouseEvent e) {
                    }

                    public void mouseMoved(MouseEvent e) {
                        UnitHandler.getContextPane().preview(sentence);
                    }
                });

                addBehaviour(producerItem);

                menu.add(producerItem);

            }
        } else {
            menu.setEnabled(false);
        }
    }

    protected void feedRevisionsMenu(final String currentProducer, final List<UnitResult> results, final JMenu menu, final ButtonGroup group) {
        if (results.size() > 0) {
            menu.setEnabled(true);
            for (int i = results.size() - 1; i >= 0; i--) {
                final UnitResult result = results.get(i);
                final PETResultMenuItem producerItem = new PETResultMenuItem(result);
                group.add(producerItem);
                if (result.getProducerAndRevision().equals(currentProducer)) {
                    group.setSelected(producerItem.getModel(), true);
                }

                producerItem.addMouseMotionListener(new MouseMotionListener() {

                    public void mouseDragged(MouseEvent e) {
                    }

                    public void mouseMoved(MouseEvent e) {
                        UnitHandler.getContextPane().preview(result.getTranslation());
                    }
                });

                addBehaviour(producerItem);

                menu.add(producerItem);

            }
        } else {
            menu.setEnabled(false);
        }
    }

    private void maybeShowPopup(final MouseEvent e) {
        if (e.isPopupTrigger()) {
            clearSubMenus();

            if (e.getComponent() instanceof AbstractUnitGUI) {

                final AbstractUnitGUI where = (AbstractUnitGUI) e.getComponent();
                TransferArea.setSource(where);
                TransferArea.setType(getType());

                final String selection = where.getSelectedText();

                // paraphrases
                feedDictionaryMenus(selection, monoDict(), monolingual);

                //translations
                feedDictionaryMenus(selection, biDict(), bilingual);

                extra(selection);

            } else {
                monolingual.setEnabled(false);
                bilingual.setEnabled(false);
            }

            popup.show(e.getComponent(),
                    e.getX(), e.getY());

        }
    }
}
