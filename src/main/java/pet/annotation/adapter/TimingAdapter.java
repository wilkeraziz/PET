/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.annotation.adapter;

import org.joda.time.Period;
import pet.annotation.Timing;

/**
 *
 * @author waziz
 */
public class TimingAdapter implements Timing{
    
    final private Period assessing;
    final private Period editing;

    public TimingAdapter(final Period assessing, final Period editing){
        this.assessing = assessing;
        this.editing = editing;
    }
    
    public Period getAssessing() {
        return assessing;
    }

    public Period getEditing() {
        return editing;
    }
    
    
    
}
