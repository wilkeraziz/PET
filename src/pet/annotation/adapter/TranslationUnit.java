package pet.annotation.adapter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import pet.annotation.Segment;
import pet.annotation.Status;

public class TranslationUnit extends AbstractUnit {

    public TranslationUnit(final String id,
            final List<Segment> sources) {
        super(id, sources, StatusAdapter.NEVER_STARTED);
    }

    public TranslationUnit(final String id,
            final List<Segment> sources,
            final List<Segment> references) {
        super(id, sources, references, StatusAdapter.NEVER_STARTED);
    }

    public TranslationUnit(final String id,
            final List<Segment> sources,
            final Status status,
            final Map<String, String> attributes) {
        super(id, sources, status, attributes);
    }

    public TranslationUnit(final String id,
            final List<Segment> sources,
            final List<Segment> references,
            final Status status) {
        super(id, sources, references, status);
    }

    public TranslationUnit(final String id,
            final List<Segment> sources,
            final List<Segment> references,
            final Status status,
            final Map<String, String> attributes) {
        super(id, sources, references, status, attributes);
    }

    @Override
    public Segment getTarget() {
        return StringSentence.getEmptySentence();
    }

    @Override
    public List<Segment> getTargets() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        String result = "ht_id=" + id + " status=" + status;
        result += "\nS: " + sources;
        if (references != null) {
            result += "\nR: " + references;
        } else {
            result += "\nR [no_provided]";
        }
        return result;
    }
}
