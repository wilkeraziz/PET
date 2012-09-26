/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pet.frontend.menu;

import javax.swing.JRadioButtonMenuItem;
import pet.annotation.Segment;

/**
 *
 * @author waziz
 */
public class PETSentenceMenuItem extends JRadioButtonMenuItem{

    private final Segment sentence;

    public PETSentenceMenuItem(final Segment sentence){
        super(sentence.getProducer());
        this.sentence = sentence;
    }

    public Segment getSentence(){
        return sentence;
    }

}
