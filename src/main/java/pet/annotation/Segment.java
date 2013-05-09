package pet.annotation;

import java.util.List;
import java.util.Map;

/**
 * Units contain segments, a Segment is a portion of text with an associated type
 * and producer.
 * 
 * @author waziz
 */
public interface Segment {

    /**
     * The type o a Segment is part of its identity.
     * @return 
     */
    SegmentType getType();

    /**
     * Splits the Segment in Words.
     * May imply smart tokenization (as opposed to splitting at spaces) depending on the implementation.
     * @return 
     */
    List<String> asTokens();
    
    /**
     * Within a unit, every Segment of a certain type is produced by a different producer
     * @return 
     */
    String getProducer();
    
    /**
     * Finally a Segment may have attributes other than the producer.
     * The producer is not returned here.
     * @return 
     */
    Map<String, String> getAttributes();

}
