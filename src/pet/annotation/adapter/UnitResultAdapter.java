package pet.annotation.adapter;

import java.util.ArrayList;
import java.util.List;


import pet.annotation.Assessment;
import pet.annotation.EffortIndicator;
import pet.annotation.Segment;
import pet.annotation.UnitResult;
import pet.signal.PETEvent;

public class UnitResultAdapter implements UnitResult {

    private final int revision;
    private final List<Assessment> assessments;
    private final List<PETEvent> events;
    private final List<EffortIndicator> indicators;
    private final Segment translation;
    private final String producer;

    public UnitResultAdapter(final int revision,
            final Segment translation,
            final List<EffortIndicator> indicators,
            final List<Assessment> assessments,
            final List<PETEvent> events) {
        this.revision = revision;
        this.translation = translation;
        this.indicators = new ArrayList<EffortIndicator>(indicators);
        this.assessments = new ArrayList<Assessment>(assessments);
        this.producer = translation.getProducer() + " (" + revision + ")";
        this.events = new ArrayList<PETEvent>(events);
    }

    public UnitResultAdapter(final IncompleteUnitResult incomplete,
            final List<EffortIndicator> indicators,
            final List<Assessment> assessments,
            final List<PETEvent> events) {
        this.revision = incomplete.getBaseRevision();
        this.translation = incomplete.getTranslation();
        this.indicators = new ArrayList<EffortIndicator>(indicators);
        this.assessments = new ArrayList<Assessment>(assessments);
        this.producer = translation.getProducer() + " (" + revision + ")";
        this.events = new ArrayList<PETEvent>(events);
    }

    @Override
    public Segment getTranslation() {
        return translation;
    }

    @Override
    public int getRevision() {
        return revision;
    }

    @Override
    public List<Assessment> getAssessments() {
        return assessments;
    }

    @Override
    public List<PETEvent> getEvents() {
        return events;
    }

    @Override
    public List<EffortIndicator> getEfforIndicators() {
        return indicators;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof UnitResultAdapter)) {
            return false;
        }
        final UnitResultAdapter other = (UnitResultAdapter) o;
        return revision == other.revision;
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(revision).hashCode();
    }

    @Override
    public String getProducerAndRevision() {
        return producer;
    }
}
