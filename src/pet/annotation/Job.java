package pet.annotation;

import java.util.List;

/**
 * This is the interface of a Job in PET
 * @author waziz
 */
public interface Job {

    /**
     * Every job should have a unique id
     * @return 
     */
    String getId();

    /**
     * Jobs are made of units. 
     * They should expose them as an unmodifiable list.
     * @return 
     */
    List<Unit> getUnits();

    /**
     * Returns the results for a given unit
     * @param unit key
     * @return annotation for that unit
     */
    List<UnitResult> getUnitResults(final Unit unit);

    /**
     * Allows one to add an annotation result to a unit
     * @param unit key
     * @param result result
     */
    void addResultToUnit(final Unit unit, final UnitResult result);

    /**
     * Returns the status of the job
     * @return 
     */
    Status getStatus();

    /**
     * Returns how many units have been performed upon
     * @return 
     */
    int getProgress();
}
