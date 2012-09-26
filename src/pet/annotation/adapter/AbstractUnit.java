package pet.annotation.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pet.annotation.Segment;
import pet.annotation.Status;
import pet.annotation.Unit;

public abstract class AbstractUnit implements Unit {

    protected final String id;
    protected final List<Segment> sources;
    protected final List<Segment> references;
    protected final Status status;
    protected final Map<String, String> attributes;

    protected AbstractUnit(final String id,
            final List<Segment> sources,
            final Status status) {
        this.sources = Collections.unmodifiableList(new ArrayList<Segment>(sources));
        this.references = Collections.emptyList();
        this.status = status;
        this.id = id;
        this.attributes = new HashMap<String, String>();
    }

    protected AbstractUnit(final String id,
            final List<Segment> sources,
            final Status status,
            final Map<String, String> attributes) {
        this.sources = Collections.unmodifiableList(new ArrayList<Segment>(sources));
        this.references = Collections.emptyList();
        this.status = status;
        this.id = id;
        this.attributes = new HashMap<String, String>(attributes);
    }

    protected AbstractUnit(final String id,
            final List<Segment> sources,
            final List<Segment> references,
            final Status status) {
        this.sources = Collections.unmodifiableList(new ArrayList<Segment>(sources));
        this.references = Collections.unmodifiableList(new ArrayList<Segment>(references));
        this.status = status;
        this.id = id;
        this.attributes = new HashMap<String, String>();
    }

    protected AbstractUnit(final String id,
            final List<Segment> sources,
            final List<Segment> references,
            final Status status,
            Map<String, String> attributes) {
        this.sources = Collections.unmodifiableList(new ArrayList<Segment>(sources));
        this.references = Collections.unmodifiableList(new ArrayList<Segment>(references));
        this.status = status;
        this.id = id;
        this.attributes = new HashMap<String, String>(attributes);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Segment getSource() {
        return sources.get(0);
    }

    @Override
    public List<Segment> getSources() {
        return sources;
    }

    @Override
    public Segment getReference() {
        return references.get(0);
    }

    @Override
    public List<Segment> getReferences() {
        return references;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(this.attributes);
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!o.getClass().equals(this.getClass())) {
            return false;
        }
        final AbstractUnit other = (AbstractUnit) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
