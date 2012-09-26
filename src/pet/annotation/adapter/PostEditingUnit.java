package pet.annotation.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import pet.annotation.Segment;
import pet.annotation.Status;

public class PostEditingUnit extends AbstractUnit {

    private final List<Segment> targets;

    public PostEditingUnit(final String id,
            final List<Segment> sources,
            final List<Segment> targets) {
        super(id, sources, StatusAdapter.NEVER_STARTED);
        this.targets = new ArrayList<Segment>(targets);
    }

    public PostEditingUnit(final String id,
            final List<Segment> sources,
            final List<Segment> references,
            final List<Segment> targets) {
        super(id, sources, references, StatusAdapter.NEVER_STARTED);
        this.targets = new ArrayList<Segment>(targets);
    }

    public PostEditingUnit(final String id,
            final List<Segment> sources,
            final List<Segment> targets,
            final Status status,
            final Map<String, String> attributes) {
        super(id, sources, status, attributes);
        this.targets = new ArrayList<Segment>(targets);
    }

    public PostEditingUnit(final String id,
            final List<Segment> sources,
            final List<Segment> references,
            final List<Segment> targets,
            final Status status) {
        super(id, sources, references, status);
        this.targets = new ArrayList<Segment>(targets);
    }

    public PostEditingUnit(final String id,
            final List<Segment> sources,
            final List<Segment> references,
            final List<Segment> targets,
            final Status status,
            final Map<String, String> attributes) {
        super(id, sources, references, status, attributes);
        this.targets = new ArrayList<Segment>(targets);
    }

    @Override
    public Segment getTarget() {
        return targets.get(0);
    }

    @Override
    public List<Segment> getTargets() {
        return Collections.unmodifiableList(targets);
    }

    @Override
    public String toString() {
        String result = "pe_id=" + id + " status=" + status;
        result += "\nS: " + sources;
        if (references != null) {
            result += "\nR: " + references;
        } else {
            result += "\nR [not_provided]";
        }
        result += "\nMT: " + getTarget();
        return result;
    }
}
