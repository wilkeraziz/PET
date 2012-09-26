/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.annotation.adapter;

import java.util.Scanner;
import pet.usr.adapter.Formatter;
import org.joda.time.Period;
import org.w3c.dom.Element;
import pet.annotation.AbstractEffortIndicator;
import pet.annotation.xml.ParseHandler;

/**
 *
 * @author waziz
 */
public class TimeEffortIndicator extends AbstractEffortIndicator {

    public static final String TYPE = "time";
    private final String id;
    private final Period duration;

    public TimeEffortIndicator(final String id,
            final Period duration) {
        super(false);
        this.id = id;
        this.duration = duration;
    }
    
    public TimeEffortIndicator(final String id,
            final Period duration,
            final boolean wrapped) {
        super(wrapped);
        this.id = id;
        this.duration = duration;
    }

    public TimeEffortIndicator(final Element xml, final boolean wrapped) {
        super(wrapped);
        this.id = xml.getAttribute(ParseHandler.ID);
        this.duration = Formatter.getMilliFormatter().parsePeriod(xml.getTextContent());
    }

    public Period getDuration() {
        return duration;
    }

    public String getType() {
        return TYPE;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return Formatter.getMilliFormatter().print(duration.normalizedStandard());
    }

}
