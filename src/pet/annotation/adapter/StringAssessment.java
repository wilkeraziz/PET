/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.annotation.adapter;

import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import pet.annotation.Assessment;
import pet.annotation.AssessmentChoice;

/**
 *
 * @author waziz
 */
public class StringAssessment implements Assessment {

    private final String id;
    private final List<String> assessment;
    private final String comment;

    public StringAssessment(final String id,
            final String assessment,
            final String comment) {
        this.id = id;
        this.assessment = Collections.singletonList(assessment);
        this.comment = comment;
    }

    public StringAssessment(final AssessmentChoice assessment) {
        this.id = assessment.getId();
        this.assessment = assessment.getAnswer();
        this.comment = assessment.getComment();
    }

    public boolean isBetterThan(final Assessment other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public List<String> toStringList() {
        return assessment;
    }

    @Override
    public String toString() {
        return StringUtils.join(assessment, "|");
    }

    public String getComment() {
        return comment;
    }
}
