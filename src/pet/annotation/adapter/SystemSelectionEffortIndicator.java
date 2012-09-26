/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.annotation.adapter;

import org.joda.time.Period;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pet.annotation.AbstractEffortIndicator;
import pet.annotation.xml.ParseHandler;
import pet.usr.adapter.Formatter;

/**
 *
 * @author waziz
 */
public class SystemSelectionEffortIndicator extends AbstractEffortIndicator {

    public static final String TYPE = "sysselection";
    private final String id;
    private final String value;
    private final Period t0;

    public SystemSelectionEffortIndicator(final String id,
            final String value,
            final Period t0, final boolean wrapped) {
        super(wrapped);
        this.id = id;
        this.value = value;
        this.t0 = t0;
    }

    public SystemSelectionEffortIndicator(final Element xml, final boolean wrapped) {
        super(wrapped);
        this.id = xml.getAttribute(ParseHandler.ID);
        this.value = xml.getTextContent();
        this.t0 = Formatter.getMilliFormatter().parsePeriod(xml.getAttribute(ParseHandler.T0));
    }

    public String getValue() {
        return value;
    }

    public Period getT0(){
        return t0;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return value;
    }

    public String getType() {
        return TYPE;
    }

    @Override
    public void writeXML(final Document xml, final Element xmlIndicator) {
        xmlIndicator.setAttribute(ParseHandler.ID, getId());
        xmlIndicator.setAttribute(ParseHandler.TYPE, getType());
        xmlIndicator.setAttribute(ParseHandler.T0, Formatter.getMilliFormatter().print(t0.normalizedStandard()));
        xmlIndicator.setTextContent(toString());
    }
}
