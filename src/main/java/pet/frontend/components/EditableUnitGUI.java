/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.frontend.components;

import pet.annotation.Segment;
import pet.annotation.adapter.StringSentence;

/**
 * This is a two-state representation of a Sentence, that is, the most recent change and the current Sentence are memorized
 * getSentence and getText return the sentence being displayed
 * 
 * @author waziz
 */
public class EditableUnitGUI extends AbstractUnitGUI {

    private Segment backup;

    public EditableUnitGUI(final Tip tipHandler, final UnitGUIType type, final int id) {
        super(tipHandler, type, id);
        backup = StringSentence.getEmptySentence();
    }

    private void updateState() {
        if (!displaying.toString().equals(super.getText())) {
            backup = displaying;
            displaying = new StringSentence(displaying.getType(), super.getText(), displaying.getProducer());
        }
    }

    // Operations that may change the facade
    @Override
    public void setSentence(final Segment sentence) {
        updateState();
        displaying = sentence;
        updateFacade();
    }

    public void restore() {
        final Segment other = backup;
        this.backup = displaying;
        this.displaying = other;
        updateFacade();
    }

    public void clear() {
        updateState();
        displaying = StringSentence.getEmptySentence();
        updateFacade();
    }

    public void preview(final Segment sentence) {
        updateState();
        backup = displaying;
        displaying = sentence;
        updateFacade();
    }

    // Operations that may change the state
    public Segment getSentence() {
        updateState();
        return displaying;
    }

    @Override
    public String getText() {
        updateState();
        return displaying.toString();
    }
}
