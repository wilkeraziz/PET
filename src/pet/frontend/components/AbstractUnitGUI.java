/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.frontend.components;

import javax.swing.JTextPane;
import pet.annotation.Segment;
import pet.annotation.adapter.StringSentence;
import pet.config.ContextHandler;
import pet.signal.PETTextChangeEvent.Reason;
import pet.usr.handler.UnitHandler;

/**
 *
 * @author waziz
 */
public abstract class AbstractUnitGUI extends JTextPane implements UnitGUI {

    private static interface Handler {
        String tip(final String tip);
    }

    public enum Tip {

        SHOW(new Handler() {

            public String tip(String tip) {
                return tip;
            }
        }),
        HIDE(new Handler() {

            public String tip(String tip) {
                return null;
            }
        });
        
        private final Handler handler;

        Tip(final Handler handler) {
            this.handler = handler;
        }

        public String get(final String tip) {
            return handler.tip(tip);
        }
    }

    protected Segment displaying;
    protected final Tip tipHandler;
    protected final UnitGUIType type;
    protected final int id;

    public AbstractUnitGUI(final Tip tipHandler, final UnitGUIType type, final int id) {
        super();
        this.displaying = StringSentence.getEmptySentence();
        this.tipHandler = tipHandler;
        this.type = type;
        this.id = id;
    }

    protected void updateFacade() {
        super.setText(this.displaying.toString());
        
        setCaretPosition(0);
        super.setToolTipText(this.tipHandler.get(this.displaying.getProducer()));
    }

    /**
     * Underlying JTextPane
     * @return
     */
    public JTextPane underlying() {
        return (JTextPane) this;
    }

    @Override
    public UnitGUIType getType(){
        return type;
    }

    @Override
    public int getId(){
        return id;
    }

    
}
