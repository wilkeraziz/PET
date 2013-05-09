/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.annotation.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pet.annotation.AssessmentChoice;

/**
 *
 * @author waziz
 */
public class AssessmentChoiceAdapter implements AssessmentChoice {

    private final String id;
    private final List<String> answer;
    private final String comment;

    public AssessmentChoiceAdapter(final String id, final String answer, final String comment) {
        this.id = id;
        this.answer = Collections.singletonList(answer);
        this.comment = comment;
    }

    public AssessmentChoiceAdapter(final String id, final List<String> answer, final String comment) {
        this.id = id;
        this.answer = new ArrayList<String>(answer);
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public List<String> getAnswer() {
        return answer;
    }

    public String getComment() {
        return comment;
    }

    
}
