package pet.annotation.adapter;

import java.util.List;
import java.util.Map;
import pet.annotation.Segment;
import pet.annotation.SegmentType;

public class IncompleteUnitResult implements Segment {

    private final int baseRevision;
    private Segment translation;
    private final String producer;

    public IncompleteUnitResult(final Segment translation) {
        this.baseRevision = 0;
        this.translation = translation;
        this.producer = getProducer(translation.getProducer(), baseRevision);
    }

    private static String getProducer(final String producer, final int revision) {
        if (revision != 0) {
            return producer + " (" + revision + ")";
        } else {
            return producer;
        }
    }

    public IncompleteUnitResult(final int baseRevision,
            final Segment translation) {
        this.baseRevision = baseRevision;
        this.translation = translation;
        this.producer = translation.getProducer() + " (" + baseRevision + ")";
    }

    public Segment getTranslation() {
        return translation;
    }

    public int getRevisionToBe() {
        return baseRevision + 1;
    }

    public int getBaseRevision() {
        return baseRevision;
    }

    public IncompleteUnitResult consolidate(final Segment done) {
        return new IncompleteUnitResult(getRevisionToBe(), done);
    }

    @Override
    public SegmentType getType() {
        return translation.getType();
    }

    @Override
    public List<String> asTokens() {
        return translation.asTokens();
    }

    @Override
    public String getProducer() {
        return producer;
    }

    @Override
    public String toString() {
        return translation.toString();
    }

    @Override
    public Map<String, String> getAttributes() {
        return translation.getAttributes();
    }
}
