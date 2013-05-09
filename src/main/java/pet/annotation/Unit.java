package pet.annotation;

import java.util.List;
import java.util.Map;

/**
 * Units are the minimum pieces of work.
 * Most of the effort indicators are computed once a unit becomes active, 
 * and they are therefore associated to that unit.
 * @author waziz
 */
public interface Unit {

    /**
     * Every unit should be identified uniquely
     * @return 
     */
    String getId();

    /**
     * Units should have a default source sentence
     * @return 
     */
    Segment getSource();

    /**
     * Multiple sources are allowed
     * @return 
     */
    List<Segment> getSources();

    /**
     * Units should have a (possibly null) default reference sentence
     * @return 
     */
    Segment getReference();

    /**
     * Multiple references are allowed
     * @return 
     */
    List<Segment> getReferences();

    /**
     * Units should have a (possibly null) default target sentence
     * @return 
     */
    Segment getTarget();
    
    /**
     * Multiple targets are allowed
     * @return 
     */
    List<Segment> getTargets();

    /**
     * Units also have status
     * @return 
     */
    Status getStatus();

    /**
     * Finally units may carry attributes other than their ids.
     * The id is not returned by this method.
     * @return an unmodifiable map
     */
    Map<String, String> getAttributes();
   
}
