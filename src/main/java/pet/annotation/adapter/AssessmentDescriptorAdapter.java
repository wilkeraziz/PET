/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.annotation.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pet.annotation.AssessmentDescriptor;

/**
 *
 * @author waziz
 */
public class AssessmentDescriptorAdapter implements AssessmentDescriptor {

    private final String id;
    private final String question;
    private final List<String> answers;
    private final int maxSelection;

    public AssessmentDescriptorAdapter(final String id, final String question, final List<String> answers, final int maxSelection) {
        this.id = id;
        this.question = question;
        this.answers = new ArrayList<String>(answers);
        this.maxSelection = maxSelection;
    }

    public String getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getAnswers() {
        return Collections.unmodifiableList(answers);
    }

    @Override
    public String toString(){
        return getId();
    }

    @Override
    public int maxSelection() {
        return maxSelection;
    }
}
