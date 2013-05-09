package pet.annotation;

import java.util.List;
import java.util.Map;

import org.joda.time.Period;

public interface EditableTaskFactory {
	
	Unit createTask(final Unit task,
			final Segment target, 
			final Status status,
			final SegmentType type,
			final List<String> targetHistory,
			final List<Map<String,Period>> timeHistory,
    		final List<List<String>> assessmentsHistory,
    		final List<String> commentHistory,
    		final boolean mtEverDiscarded,
    		final boolean disjoint,
    		final boolean equal,
     		final boolean possible,
     		final Period totalEditingTime,
     		final Period totalAssessingTime
			);
	
}
