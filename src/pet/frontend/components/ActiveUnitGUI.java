/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.frontend.components;

/**
 * This is a two-state representation of a Sentence, that is, the most recent change and the current Sentence are memorized
 * getSentence and getText return the sentence being displayed
 * 
 * @author waziz
 */
public class ActiveUnitGUI extends EditableUnitGUI {

    public ActiveUnitGUI(final Tip tipHandler, final UnitGUIType type, final int id) {
        super(tipHandler, type, id);
    }

    @Override
    protected void updateFacade() {
        super.setText(this.displaying.toString());
        setCaretPosition(0);
        super.setToolTipText(this.tipHandler.get(this.displaying.getProducer()));
    }
    
}
