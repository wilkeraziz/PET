/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.usr.adapter;

import java.util.List;
import pet.annotation.AssessmentChoice;

/**
 *
 * @author waziz
 */
public interface AssessmentListener {

    void assess(final List<AssessmentChoice> chosen);
}
