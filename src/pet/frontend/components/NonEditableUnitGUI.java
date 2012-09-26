/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.frontend.components;

import pet.annotation.Segment;
import pet.annotation.adapter.StringSentence;

/**
 * In this implementation one Sentence is stored, regardless of which one is being displayed
 * Whenever the setence is requested by getSentence() that fixed sentence will be returned as opposed to the one being displayed
 * The displayed sentence is returned by getDisplaying()
 * To fix a sentence use setSentence()
 * To change the displayed sentence use preview(Sentence)
 * To display back the fixed sentence use restore()
 * Here getText() returns the text of the fixed Sentence
 *
 * @author waziz
 */
public class NonEditableUnitGUI extends AbstractUnitGUI {

    private Segment sentence;

    public NonEditableUnitGUI(final Tip tipHandler, final UnitGUIType type, final int id) {
        super(tipHandler, type, id);
        sentence = displaying;
    }

    // Operations that may change the facade
    public void setSentence(final Segment sentence) {
        this.sentence = sentence;
        this.displaying = this.sentence;
        updateFacade();
    }    

    public void clear() {
        this.sentence = StringSentence.getEmptySentence();
        this.displaying = this.sentence;
        updateFacade();
    }

    public void preview(final Segment sentence) {
        this.displaying = sentence;
        updateFacade();
    }

    public void restore() {
        this.displaying = this.sentence;
        updateFacade();
    }

    public Segment getSentence() {
        return this.sentence;
    }

    public Segment getDisplaying(){
        return this.displaying;
    }

    @Override
    public String getText() {
        return this.sentence.toString();
    }

    
}
