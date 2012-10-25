/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.frontend.menu;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.text.DefaultEditorKit;
import pet.config.ContextHandler;
import pet.usr.handler.UnitHandler;

/**
 *
 * @author waziz
 */
public class PopupMenuFactory {

    private Map<String, AbstractPopupMouseAdapter> map;

    public PopupMenuFactory() {
        map = new HashMap<String, AbstractPopupMouseAdapter>();
    }

    public AbstractPopupMouseAdapter buildTargetMenu(final String id, final boolean active) {
        AbstractPopupMouseAdapter listener = map.get(id);
        if (listener != null) {
            return listener;
        } else {
            listener = targetMenu(active);
            map.put(id, listener);
            return listener;
        }
    }

    public AbstractPopupMouseAdapter buildContextMenu(final String id) {
        AbstractPopupMouseAdapter listener = map.get(id);
        if (listener != null) {
            return listener;
        } else {
            listener = contextMenu();
            map.put(id, listener);
            return listener;
        }
    }

    public AbstractPopupMouseAdapter buildSourceMenu(final String id, final boolean active) {
        AbstractPopupMouseAdapter listener = map.get(id);
        if (listener != null) {
            return listener;
        } else {
            listener = sourceMenu(active);
            map.put(id, listener);
            return listener;
        }
    }

    private JMenu menuParaphrases(final String name) {
        return new JMenu(name);
    }

    private JMenu menuTranslations(final String name) {
        return new JMenu(name);
    }

    private JMenu menuSources() {
        final JMenu menu = new JMenu("SRCs");
        menu.addMenuListener(new MenuListener() {

            public void menuSelected(MenuEvent me) {
            }

            public void menuDeselected(MenuEvent me) {
                UnitHandler.getContextPane().restore();
            }

            public void menuCanceled(MenuEvent me) {
            }
        });
        return menu;
    }

    private JMenu menuReferences() {
        final JMenu menu = new JMenu("REFs");
        menu.addMenuListener(new MenuListener() {

            public void menuSelected(MenuEvent me) {
            }

            public void menuDeselected(MenuEvent me) {
                UnitHandler.getContextPane().restore();
            }

            public void menuCanceled(MenuEvent me) {
            }
        });
        return menu;
    }

    private JMenu menuMTs() {
        final JMenu menu = new JMenu("MTs");
        menu.addMenuListener(new MenuListener() {

            public void menuSelected(MenuEvent me) {
            }

            public void menuDeselected(MenuEvent me) {
                UnitHandler.getContextPane().restore();
            }

            public void menuCanceled(MenuEvent me) {
            }
        });
        return menu;
    }

    private JMenu menuPEs() {
        final JMenu menu = new JMenu("PEs");
        menu.addMenuListener(new MenuListener() {

            public void menuSelected(MenuEvent me) {
            }

            public void menuDeselected(MenuEvent me) {
                UnitHandler.getContextPane().restore();
            }

            public void menuCanceled(MenuEvent me) {
            }
        });
        return menu;
    }

    private AbstractPopupMouseAdapter contextMenu() {
        final JPopupMenu menu = new JPopupMenu();

        // text operations
        JMenuItem menuItem = new JMenuItem(new DefaultEditorKit.CopyAction());
        menuItem.setText("copy");
        menu.add(menuItem);

        // dictionary lookup
        menu.addSeparator();
        final JMenu s2s = menuParaphrases(ContextHandler.source() + "-" + ContextHandler.source());
        menu.add(s2s);
        final JMenu s2t = menuTranslations(ContextHandler.source() + "-" + ContextHandler.target());
        menu.add(s2t);
        final JMenu t2t = menuParaphrases(ContextHandler.target() + "-" + ContextHandler.target());
        menu.add(t2t);
        final JMenu t2s = menuTranslations(ContextHandler.target() + "-" + ContextHandler.source());
        menu.add(t2s);


        // multiple MTs
        menu.addSeparator();
        final JMenu mts = menuMTs();
        menu.add(mts);
        final JMenu pes = menuPEs();
        menu.add(pes);
        menu.addSeparator();
        final JMenu srcs = menuSources();
        menu.add(srcs);
        final JMenu refs = menuReferences();
        menu.add(refs);

        return new ContextMouseAdapter(menu, s2s, s2t, t2t, t2s, srcs, refs, mts, pes);
    }

    private AbstractPopupMouseAdapter sourceMenu(final boolean active) {
        final JPopupMenu menu = new JPopupMenu();

        // text operations
        JMenuItem menuItem = new JMenuItem(new DefaultEditorKit.CopyAction());
        menuItem.setText("copy");
        menu.add(menuItem);

        // dictionary lookup
        menu.addSeparator();
        final JMenu mono = menuParaphrases(ContextHandler.source() + "-" + ContextHandler.source());
        menu.add(mono);
        final JMenu bilingual = menuTranslations(ContextHandler.source() + "-" + ContextHandler.target());
        menu.add(bilingual);
        if (active) {
            menu.addSeparator();
            final JMenu srcs = menuSources();
            menu.add(srcs);
            return new ActiveSourceMouseAdapter(menu, mono, bilingual, srcs);
        } else {
            return new SourceMouseAdapter(menu, mono, bilingual);
        }
    }

    private AbstractPopupMouseAdapter targetMenu(final boolean active) {
        final JPopupMenu menu = new JPopupMenu();

        // text operations
        JMenuItem menuItem = new JMenuItem(new CopyAction());
        menuItem.setText("copy");
        menu.add(menuItem);
        if (active) {
            menuItem = new JMenuItem(new CutAction());
            menuItem.setText("cut");
            menu.add(menuItem);
            menuItem = new JMenuItem(new PasteAction());
            menuItem.setText("paste");
            menu.add(menuItem);
            menu.addSeparator();
            menuItem = new JMenuItem("trim");
            menuItem.addActionListener(new TrimActionListener());
            menu.add(menuItem);

            menu.addSeparator();

            // editing operations
            menuItem = new JMenuItem("insert/replace");
            menuItem.addActionListener(new InsertActionListener());
            menu.add(menuItem);
            menuItem = new JMenuItem("delete");
            menuItem.addActionListener(new DeleteActionListener());
            menu.add(menuItem);
            final ActionListener shitListener = new ShiftActionListener();
            final JMenu shift = new JMenu("shift");
            menuItem = new JMenuItem("-3");
            menuItem.addActionListener(shitListener);
            shift.add(menuItem);
            menuItem = new JMenuItem("-2");
            menuItem.addActionListener(shitListener);
            shift.add(menuItem);
            menuItem = new JMenuItem("-1");
            menuItem.addActionListener(shitListener);
            shift.add(menuItem);
            menuItem = new JMenuItem("+1");
            menuItem.addActionListener(shitListener);
            shift.add(menuItem);
            menuItem = new JMenuItem("+2");
            menuItem.addActionListener(shitListener);
            shift.add(menuItem);
            menuItem = new JMenuItem("+3");
            menuItem.addActionListener(shitListener);
            shift.add(menuItem);
            shift.addSeparator();
            menuItem = new JMenuItem("BOS");
            menuItem.addActionListener(shitListener);
            shift.add(menuItem);
            menuItem = new JMenuItem("EOS");
            menuItem.addActionListener(shitListener);
            shift.add(menuItem);
            menu.add(shift);
            shift.addSeparator();
            menuItem = new JMenuItem("custom");
            menuItem.addActionListener(shitListener);
            shift.add(menuItem);
            menu.add(shift);
        }


        // dictionary lookup
        menu.addSeparator();

        final JMenu mono = menuParaphrases(ContextHandler.target() + "-" + ContextHandler.target());
        menu.add(mono);
        final JMenu ts = menuTranslations(ContextHandler.target() + "-" + ContextHandler.source());
        menu.add(ts);
        final JMenu st = menuTranslations(ContextHandler.source() + "-" + ContextHandler.target());
        menu.add(st);

        if (active) {
            // multiple MTs
            menu.addSeparator();
            final JMenu mts = menuMTs();
            menu.add(mts);
            final JMenu pes = menuPEs();
            menu.add(pes);
            return new ActiveTargetMouseAdapter(menu, mono, ts, st, mts, pes);
        } else {
            return new TargetMouseAdapter(menu, mono, ts, st);
        }
    }
}
