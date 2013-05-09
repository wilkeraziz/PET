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
import pet.signal.ChangeSignalPackage.ChangeType;
import pet.usr.adapter.Formatter;

/**
 *
 * @author waziz
 */
public class ChangeEffortIndicator extends AbstractEffortIndicator {

    public static final String TYPE = "change";
    private final ChangeType operation;
    private final int offset;
    private final int length;
    private final String change;
    private final Period t0;
    private final Period elapsed;

    public ChangeEffortIndicator(final ChangeType operation,
            final int offset,
            final int length,
            final String change,
            final Period t0,
            final Period elapsed) {
        super(false);
        this.operation = operation;
        this.change = change;
        this.offset = offset;
        this.length = length;
        this.t0 = t0;
        this.elapsed = elapsed;
    }
    
    public ChangeEffortIndicator(final ChangeType operation,
            final int offset,
            final int length,
            final String change,
            final Period t0,
            final Period elapsed, 
            final boolean wrapped) {
        super(wrapped);
        this.operation = operation;
        this.change = change;
        this.offset = offset;
        this.length = length;
        this.t0 = t0;
        this.elapsed = elapsed;
    }

    public static ChangeEffortIndicator getInsertion(
            final int offset,
            final int length,
            final String in,
            final Period t0,
            final Period elapsed) {
        return new ChangeEffortIndicator(ChangeType.INSERTION, offset, length, in, t0, elapsed, false);
    }

    public static ChangeEffortIndicator getDeletion(
            final int offset,
            final int length,
            final String out,
            final Period t0,
            final Period elapsed) {
        return new ChangeEffortIndicator(ChangeType.DELETION, offset, length, out, t0, elapsed, false);
    }

    public static ChangeEffortIndicator getAssignment(
            final int offset,
            final int length,
            final String in,
            final Period t0,
            final Period elapsed) {
        return new ChangeEffortIndicator(ChangeType.ASSIGNMENT, offset, length, in, t0, elapsed, false);
    }

    public ChangeEffortIndicator getOther(
            final int offset,
            final int length,
            final String change,
            final Period t0,
            final Period elapsed) {
        return new ChangeEffortIndicator(operation, offset, length, change, t0, elapsed, wrapped);
    }

    public ChangeEffortIndicator(final Element xml, 
            final boolean wrapped) {
        super(wrapped);
        this.operation = ChangeType.valueOf(xml.getAttribute(ParseHandler.ID).toUpperCase());
        this.offset = Integer.parseInt(xml.getAttribute(ParseHandler.OFFSET));
        this.length = Integer.parseInt(xml.getAttribute(ParseHandler.LENGTH));
        this.t0 = Formatter.getMilliFormatter().parsePeriod(xml.getAttribute(ParseHandler.T0));
        this.elapsed = Formatter.getMilliFormatter().parsePeriod(xml.getAttribute(ParseHandler.ELAPSED));
        this.change = xml.getTextContent();
    }

    public String getChange() {
        return change;
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }

    public Period getElapsed() {
        return elapsed;
    }

    public Period getT0() {
        return t0;
    }

    @Override
    public String getId() {
        return operation.toString();
    }

    public ChangeType getOperation() {
        return operation;
    }

    @Override
    public String toString() {
        return change;
    }

    public String getType() {
        return TYPE;
    }

    @Override
    public void writeXML(final Document xml, final Element xmlIndicator) {
        xmlIndicator.setAttribute(ParseHandler.ID, getId());
        xmlIndicator.setAttribute(ParseHandler.TYPE, getType());
        xmlIndicator.setAttribute(ParseHandler.OFFSET, Integer.toString(getOffset()));
        xmlIndicator.setAttribute(ParseHandler.LENGTH, Integer.toString(getLength()));
        xmlIndicator.setAttribute(ParseHandler.T0, Formatter.getMilliFormatter().print(t0.normalizedStandard()));
        xmlIndicator.setAttribute(ParseHandler.ELAPSED, Formatter.getMilliFormatter().print(elapsed.normalizedStandard()));
        xmlIndicator.setTextContent(toString());
    }
}
