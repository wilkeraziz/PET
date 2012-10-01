/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.usr.adapter;

import pet.annotation.SegmentType;
import pet.annotation.Segment;
import pet.annotation.Status;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.joda.time.Period;
import pet.annotation.Assessment;
import pet.annotation.AssessmentChoice;
import pet.annotation.EffortIndicator;
import pet.annotation.UnitResult;
import pet.annotation.adapter.*;
import pet.annotation.adapter.TranslationUnit;
import pet.config.ContextHandler;
import pet.signal.*;
import pet.signal.ChangeSignalPackage.ChangeType;
import pet.usr.handler.SettingsHandler;

/**
 *
 * @author waziz
 */
public class EditableUnit implements pet.annotation.Unit {

    private final pet.annotation.Unit unit;
    private IncompleteUnitResult editing;
    private final SegmentType type;
    private final List<UnitResult> results;
    private final SignalHandler builder;

    private EditableUnit(final EditableUnit unit, final Segment target) {
        this.unit = unit.getOriginalUnit();
        this.type = unit.type;
        this.editing = new IncompleteUnitResult(target);
        this.results = new ArrayList<UnitResult>(unit.results);
        this.builder = new SignalHandler(unit.getStatus());
    }

    public EditableUnit(final pet.annotation.Unit unit) {
        this.unit = unit;
        this.type = (unit instanceof pet.annotation.adapter.TranslationUnit) ? SegmentTypeAdapter.HT : SegmentTypeAdapter.POST_EDITED_MT;
        this.editing = new IncompleteUnitResult(unit.getTarget());
        this.results = new ArrayList<UnitResult>();
        this.builder = new SignalHandler(unit.getStatus());
    }

    public EditableUnit(final pet.annotation.Unit unit, final List<UnitResult> results) {
        this.unit = unit;
        this.type = (unit instanceof pet.annotation.adapter.TranslationUnit) ? SegmentTypeAdapter.HT : SegmentTypeAdapter.POST_EDITED_MT;
        this.results = new ArrayList<UnitResult>(results);
        this.builder = new SignalHandler(unit.getStatus());
        int maxr = -1;
        int r = -1;
        for (int i = 0; i < results.size(); i++) {
            final UnitResult result = results.get(i);
            if (maxr < result.getRevision()) {
                maxr = result.getRevision();
                r = i;
            }
        }
        if (r >= 0) {
            this.editing = new IncompleteUnitResult(maxr, results.get(r).getTranslation());
        } else {
            this.editing = new IncompleteUnitResult(unit.getTarget());
        }
    }

    public EditableUnit getSnapshot(final String target) {
        return new EditableUnit(this,
                new StringSentence(type,
                target,
                getUpdatedProducer()));
    }

    private String getUpdatedProducer() {
        if (unit instanceof TranslationUnit) {
            return SettingsHandler.getUser();
        } else {
            return SettingsHandler.getUser() + "." + unit.getTarget().getProducer();
        }
    }

    @Override
    public String getId() {
        return unit.getId();
    }

    @Override
    public Segment getSource() {
        return unit.getSource();
    }

    @Override
    public List<Segment> getSources() {
        return unit.getSources();
    }

    @Override
    public Segment getReference() {
        return unit.getReference();
    }

    @Override
    public List<Segment> getReferences() {
        return unit.getReferences();
    }

    @Override
    public Segment getTarget() {
        return editing;
    }

    public IncompleteUnitResult getEditing() {
        return editing;
    }

    @Override
    public List<Segment> getTargets() {
        return unit.getTargets();
    }

    public void activate() {
        builder.publishListeners();
    }

    public void deactivate() {
        builder.unpublishListeners();
    }

    public pet.annotation.Unit getOriginalUnit() {
        return unit;
    }

    public List<UnitResult> getUnitResults() {
        return Collections.unmodifiableList(results);
    }

    @Override
    public Status getStatus() {
        return builder.getStatus();
    }

    public Segment getOriginalTarget() {
        return unit.getTarget();
    }

    public Period getTotalTimeSpent() {
        return builder.getTotalTimeSpent();
    }

    public Period getEditingTime() {
        return builder.getTotalEditingTime();
    }

    public Period getAssessingTime() {
        return builder.getTotalAssessingTime();
    }

    @Override
    public Map<String, String> getAttributes() {
        return unit.getAttributes();
    }

    public boolean changeSentence(final String edited,
            final List<AssessmentChoice> assessments) {
        if (getStatus() == StatusAdapter.STARTED) {
            ContextHandler.signalManager().fire(SignalAdapter.DONE);

            editing = editing.consolidate(new StringSentence(type, edited, getUpdatedProducer()));
            results.add(builder.buildResult(editing, assessments));
            return true;
        }
        return false;
    }

    class SignalHandler implements SingleSignalListener, PETEventListener {

        private DateTime editingStartTime;
        private DateTime editingEndTime;
        private DateTime assessingStartTime;
        private DateTime assessingEndTime;
        
        private boolean logging;
        private boolean autoaccept;
        private boolean impossible;
        private boolean unnecessary;
        private boolean unchanged;
        private Status status;
        private final List<ChangeEffortIndicator> changes;
        private final List<EffortIndicator> actions;
        private final boolean compactLog = true;
        private final List<PETEvent> events;
        
        private final List<EventInterpreter> interpreters; // TODO: make it customizable

        private SignalHandler(final Status status) {
            impossible = false;
            unnecessary = false;
            unchanged = false;
            autoaccept = false;
            this.status = status;
            this.changes = new ArrayList<ChangeEffortIndicator>();
            this.actions = new ArrayList<EffortIndicator>();
            this.logging = false;

            this.events = new ArrayList<PETEvent>();
            this.interpreters =  new ArrayList<EventInterpreter>();
            if (ContextHandler.keystrokes()){
                this.interpreters.add(new KeystrokeInterpreter());
            }
        }

        @Override
        public void treat(final PETEvent event) {
            events.add(event);
        }

        public void publishListeners() {
            ContextHandler.signalManager().replaceSingleListener(builder);
            ContextHandler.signalManager().addListener(builder);
        }

        public void unpublishListeners() {
            ContextHandler.signalManager().removeListener(builder);
        }

        public Period getTotalEditingTime() {
            final Period totalTimeSpent = new Period(0L);
            for (final UnitResult result : results) {
                for (final EffortIndicator indicator : result.getEfforIndicators()) {
                    if (indicator instanceof TimeEffortIndicator && indicator.getId().equals("editing")) {
                        totalTimeSpent.plus(((TimeEffortIndicator) indicator).getDuration());
                    }
                }
            }
            return totalTimeSpent;
        }

        public Period getTotalAssessingTime() {
            final Period totalTimeSpent = new Period(0L);
            for (final UnitResult result : results) {
                for (final EffortIndicator indicator : result.getEfforIndicators()) {
                    if (indicator instanceof TimeEffortIndicator && indicator.getId().equals("assessing")) {
                        totalTimeSpent.plus(((TimeEffortIndicator) indicator).getDuration());
                    }
                }
            }
            return totalTimeSpent;
        }

        public Period getTotalTimeSpent() {
            Period totalTimeSpent = new Period(0L);
            for (final UnitResult result : results) {
                for (final EffortIndicator indicator : result.getEfforIndicators()) {
                    if (indicator instanceof TimeEffortIndicator) {
                        totalTimeSpent = totalTimeSpent.plus(((TimeEffortIndicator) indicator).getDuration());
                    }
                }
            }
            return totalTimeSpent;
        }

        private void startLogging() {
            logging = true;
        }

        private void stopLogging() {
            logging = false;
        }

        public UnitResult buildResult(final IncompleteUnitResult incomplete,
                final List<AssessmentChoice> assessments) {
            stopLogging();
            final List<EffortIndicator> indicators = new ArrayList<EffortIndicator>(2);
            indicators.add(new TimeEffortIndicator("editing", new Period(editingStartTime, editingEndTime)));
            final List<Assessment> assessmentList = new ArrayList<Assessment>(assessments.size());
            if (ContextHandler.assessing()) {
                indicators.add(new TimeEffortIndicator("assessing", new Period(assessingStartTime, assessingEndTime)));
                for (final AssessmentChoice chosen : assessments) {
                    assessmentList.add(new StringAssessment(chosen));
                }
            }
            
            if (ContextHandler.autoAccept()) {
                indicators.add(new FlagEffortIndicator("autoaccept", autoaccept));
            }
            if (ContextHandler.impossible()) {
                indicators.add(new FlagEffortIndicator("impossible", impossible));
            }
            if (ContextHandler.unnecessary()) {
                indicators.add(new FlagEffortIndicator("unnecessary", unnecessary));
            }
            if (ContextHandler.unchanged()) {
                if (!unchanged) {
                    unchanged = incomplete.getTranslation().equals(unit.getTarget());
                }
                indicators.add(new FlagEffortIndicator("unchanged", unchanged));
            }
            if (ContextHandler.logChanges()) {
                if (!changes.isEmpty()) {
                    if (!compactLog) {
                        indicators.addAll(changes);
                    } else {
                        final List<ChangeEffortIndicator> compact = new ArrayList<ChangeEffortIndicator>();
                        int length = changes.get(0).getLength();
                        int offset = changes.get(0).getOffset();
                        String change = changes.get(0).getChange();
                        ChangeType last = changes.get(0).getOperation();
                        Period t0 = changes.get(0).getT0();
                        Period t1 = changes.get(0).getT0();

                        for (int ch = 1; ch < changes.size(); ch++) {
                            if (last == ChangeType.INSERTION && last == changes.get(ch).getOperation() && offset == (changes.get(ch).getOffset() - length)) {
                                length += changes.get(ch).getLength();
                                change += changes.get(ch).getChange();
                                t1 = changes.get(ch).getT0();
                            } else if (last == ChangeType.DELETION && last == changes.get(ch).getOperation() && (offset == (changes.get(ch).getOffset() + changes.get(ch).getLength()) || offset == changes.get(ch).getOffset())) {
                                if (offset == (changes.get(ch).getOffset() + changes.get(ch).getLength())) {
                                    change = changes.get(ch).getChange() + change;
                                } else {
                                    change = change + changes.get(ch).getChange();
                                }
                                offset = changes.get(ch).getOffset();
                                length += changes.get(ch).getLength();

                                t1 = changes.get(ch).getT0();
                            } else {
                                compact.add(new ChangeEffortIndicator(last, offset, length, change, t0, t1.minus(t0)));
                                last = changes.get(ch).getOperation();
                                length = changes.get(ch).getLength();
                                offset = changes.get(ch).getOffset();
                                change = changes.get(ch).getChange();
                                t0 = changes.get(ch).getT0();
                                t1 = changes.get(ch).getT0();
                            }
                        }
                        compact.add(new ChangeEffortIndicator(last, offset, length, change, t0, t1.minus(t0)));

                        if (compact.size() > 1) {
                            final List<EffortIndicator> wrapped = new ArrayList<EffortIndicator>(compact.size());
                            int i = 0;
                            int j = 1;
                            while (j < compact.size()) {
                                final ChangeEffortIndicator first = compact.get(i);
                                final ChangeEffortIndicator second = compact.get(j);
                                if (first.getOffset() == second.getOffset()
                                        //&& !first.getChange().trim().equals(second.getChange().trim())
                                        && (first.getOperation() == ChangeType.INSERTION && second.getOperation() == ChangeType.DELETION
                                        || second.getOperation() == ChangeType.INSERTION && first.getOperation() == ChangeType.DELETION)) {
                                    wrapped.add(new WrapEffortIndicator("substitution", first.wrapIt(), second.wrapIt()));
                                    i += 2;
                                    j += 2;
                                    continue;
                                }
                                if (first.getOffset() != second.getOffset()
                                        && first.getChange().trim().equals(second.getChange().trim())
                                        && (first.getOperation() == ChangeType.INSERTION && second.getOperation() == ChangeType.DELETION
                                        || second.getOperation() == ChangeType.INSERTION && first.getOperation() == ChangeType.DELETION)) {
                                    wrapped.add(new WrapEffortIndicator("shift", first.wrapIt(), second.wrapIt()));
                                    i += 2;
                                    j += 2;
                                    continue;
                                }
                                wrapped.add(first);
                                i++;
                                j++;
                            }
                            if (i < compact.size() && j >= compact.size()) {
                                wrapped.add(compact.get(i));
                                i++;
                            }
                            indicators.addAll(wrapped);
                        } else {
                            indicators.addAll(compact);
                        }


                    }
                    changes.clear();
                }
                if (!actions.isEmpty()) {
                    indicators.addAll(actions);
                    actions.clear();
                }
            }

            
            for (final EventInterpreter interpreter : this.interpreters){
                indicators.addAll(interpreter.interpret(events));
            }
            
            final UnitResult result = new UnitResultAdapter(incomplete,
                    indicators,
                    assessmentList,
                    events);

            events.clear();
            
            impossible = false;
            unchanged = false;
            unnecessary = false;


            return result;
        }
        
       

        @Override
        public void treat(final Signal signal) {
            if (!logging) {
                if (signal == SignalAdapter.EDITING_START) {
                    status = StatusAdapter.STARTED;
                    editingStartTime = new DateTime(System.currentTimeMillis());
                    startLogging();
                    return;
                }
                return;
            }
            if (signal == SignalAdapter.DONE) {
                status = StatusAdapter.FINISHED;
                return;
            }

            if (signal == SignalAdapter.EDITING_END) {
                editingEndTime = new DateTime(System.currentTimeMillis());
                return;
            }
            if (signal == SignalAdapter.ASSESSING_START) {
                assessingStartTime = new DateTime(System.currentTimeMillis());
                return;
            }
            if (signal == SignalAdapter.ASSESSING_END) {
                assessingEndTime = new DateTime(System.currentTimeMillis());
                return;
            }
            
            if (signal == SignalAdapter.UNNECESSARY) {
                unnecessary = true;
                return;
            }
            if (signal == SignalAdapter.IMPOSSIBLE) {
                impossible = true;
                return;
            }
            if (signal == SignalAdapter.ACCEPT) {
                unchanged = true;
                return;
            }
            if (signal == SignalAdapter.AUTO_ACCEPT) {
                autoaccept = true;
                unchanged = true;
                return;
            }

        }

        @Override
        public void treat(final Signal signal, final SignalPackage pack) {
            if (!logging) {
                if (signal == SignalAdapter.EDITING_START) {
                    status = StatusAdapter.STARTED;
                    editingStartTime = new DateTime(System.currentTimeMillis());
                    startLogging();
                    return;
                }
                return;
            }
            if (signal == SignalAdapter.TEXT_INSERTION) {
                final ChangeSignalPackage change = (ChangeSignalPackage) pack;
                changes.add(ChangeEffortIndicator.getInsertion(change.getOffset(),
                        change.getLength(),
                        change.getChange(),
                        new Period(editingStartTime, change.getTimestamp()),
                        Period.ZERO));
                return;
            }
            if (signal == SignalAdapter.TEXT_DELETION) {
                final ChangeSignalPackage change = (ChangeSignalPackage) pack;
                changes.add(ChangeEffortIndicator.getDeletion(change.getOffset(),
                        change.getLength(),
                        change.getChange(),
                        new Period(editingStartTime, change.getTimestamp()),
                        Period.ZERO));
                return;
            }
            if (signal == SignalAdapter.TEXT_ASSIGNMENT) {
                final ChangeSignalPackage change = (ChangeSignalPackage) pack;
                changes.add(ChangeEffortIndicator.getAssignment(change.getOffset(),
                        change.getLength(),
                        change.getChange(),
                        new Period(editingStartTime, change.getTimestamp()),
                        Period.ZERO));
                return;
            }
            if (signal == SignalAdapter.REPLACEMENT) {
                final ReplacementSignalPackage change = (ReplacementSignalPackage) pack;
                actions.add(new SystemSelectionEffortIndicator("target", change.toString(), new Period(editingStartTime, change.getTimestamp()), false));
                return;
            }
            if (signal == SignalAdapter.DICTIONARY_LOOKPUP) {
                final DictionarySignalPackage change = (DictionarySignalPackage) pack;
                actions.add(new DictionaryEffortIndicator(change.getOperation().toString(), change.getOffset(), change.getOut(), change.getIn(), new Period(editingStartTime, change.getTimestamp()), change.getDictionary(), false));
                return;
            }
        }

        public Status getStatus() {
            return status;
        }
    }
}
