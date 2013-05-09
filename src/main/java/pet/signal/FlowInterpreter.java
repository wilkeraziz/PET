/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.signal;

import java.util.ArrayList;
import java.util.List;
import org.joda.time.Period;
import pet.annotation.EffortIndicator;
import pet.annotation.adapter.TimeEffortIndicator;

/**
 * This interpreter is responsible for the key-typing events.
 * It generates a handful of indicators, all count indicators.
 * 
 * @author waziz
 */
public class FlowInterpreter implements EventInterpreter {

    private final boolean assessing;

    public FlowInterpreter(final boolean assessing) {
        this.assessing = assessing;
    }

    /**
     * Counts keys by group. 
     * @param events
     * @return 
     */
    @Override
    public List<EffortIndicator> interpret(final List<PETEvent> events) {

        final List<Period> editingPartials = new ArrayList<Period>();
        final List<Period> assessingPartials = new ArrayList<Period>();

        long start = 0;
        for (final PETEvent e : events) {
            if (e instanceof PETFlowEvent) {
                final PETFlowEvent evt = (PETFlowEvent) e;
                if (evt.getAction() == PETFlowEvent.ActionType.EDITING_START) {
                    start = evt.when();
                } else if (evt.getAction() == PETFlowEvent.ActionType.EDITING_END) {
                    editingPartials.add(new Period(evt.when() - start));
                    start = evt.when();
                } else if (evt.getAction() == PETFlowEvent.ActionType.ASSESSING_START) {
                    start = evt.when();
                } else if (evt.getAction() == PETFlowEvent.ActionType.ASSESSING_END) {
                    assessingPartials.add(new Period(evt.when() - start));
                    start = evt.when();
                }
            }
        }
        final List<EffortIndicator> indicators = new ArrayList<EffortIndicator>();
        Period editingSum = Period.ZERO;
        for (final Period p : editingPartials) {
            editingSum = editingSum.plus(p);
        }
        indicators.add(new TimeEffortIndicator("editing", editingSum));

        if (assessing) {
            Period assessingSum = Period.ZERO;
            for (final Period p : assessingPartials) {
                assessingSum = assessingSum.plus(p);
            }
            indicators.add(new TimeEffortIndicator("assessing", assessingSum));
        }
        return indicators;
    }
}
