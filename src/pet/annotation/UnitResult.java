package pet.annotation;

import java.util.List;
import pet.signal.PETEvent;

public interface UnitResult {

    int getRevision();

    Segment getTranslation();

    List<EffortIndicator> getEfforIndicators();

    List<Assessment> getAssessments();
    
    List<PETEvent> getEvents();

    String getProducerAndRevision();

}
