/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.annotation.adapter;

import java.util.List;
import java.util.Map;
import pet.annotation.Segment;
import pet.annotation.SegmentType;
import pet.annotation.UnitResult;

/**
 *
 * @author waziz
 */
public class ResultSentenceAdapter implements Segment {

    private final UnitResult result;

    public ResultSentenceAdapter(final UnitResult result) {
        this.result = result;
    }

    @Override
    public SegmentType getType() {
        return result.getTranslation().getType();
    }

    @Override
    public List<String> asTokens() {
        return result.getTranslation().asTokens();
    }

    @Override
    public String getProducer() {
        return result.getProducerAndRevision();
    }

    @Override
    public String toString() {
        return result.getTranslation().toString();
    }

    @Override
    public Map<String, String> getAttributes() {
        return result.getTranslation().getAttributes();
    }
}
