/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.annotation;

import org.joda.time.Period;

/**
 *
 * @author waziz
 */
public interface Timing {

    Period getAssessing();

    Period getEditing();
}
