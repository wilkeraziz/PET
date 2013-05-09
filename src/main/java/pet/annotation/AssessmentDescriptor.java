/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.annotation;

import java.util.List;

/**
 *
 * @author waziz
 */
public interface AssessmentDescriptor {

    String getId();

    String getQuestion();

    List<String> getAnswers();

    int maxSelection();
}
