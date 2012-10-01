/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.signal;

import java.util.List;
import pet.annotation.EffortIndicator;

/**
 * This is the interface of the interpreters that convert a list of events into 
 * a meaningful effort indicators.
 * @author waziz
 */
public interface EventInterpreter {
    
    /**
     * Converts chronological events into effort indicators
     * @param events a sorted list of events
     * @return a list of effort indicators
     */
    List<EffortIndicator> interpret(final List<PETEvent> events);
    
}
