/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.annotation.adapter;

import org.joda.time.Period;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import pet.annotation.AbstractEffortIndicator;
import pet.annotation.xml.ParseHandler;
import pet.usr.adapter.Formatter;

/**
 *
 * @author waziz
 */
public class DictionaryEffortIndicator extends AbstractEffortIndicator {

    public static final String TYPE = "dictionary";
    private final String id;
    private final int offset;
    private final String out;
    private final String in;
    private final String dict;
    private final Period t0;

    public DictionaryEffortIndicator(final String id,
            final int offset,
            final String out,
            final String in,
            final Period t0,
            final String dict, 
            final boolean wrapped) {
        super(wrapped);
        this.id = id;
        this.offset = offset;
        this.out = out;
        this.in = in;
        this.t0 = t0;
        this.dict = dict;
    }

    public DictionaryEffortIndicator(final Element xml, 
            final boolean wrapped) {
        super(wrapped);
        this.id = xml.getAttribute(ParseHandler.ID);
        this.offset = Integer.parseInt(xml.getAttribute(ParseHandler.OFFSET));
        this.t0 = Formatter.getMilliFormatter().parsePeriod(xml.getAttribute(ParseHandler.T0));
        final NodeList inXml = xml.getElementsByTagName(ParseHandler.IN);
        if (inXml.getLength() > 0) {
            final Element inElement = ((Element) inXml.item(0));
            this.in = inElement.getTextContent();
            this.dict = inElement.getAttribute(ParseHandler.DICT);
        } else {
            this.in = "";
            this.dict = "";
        }
        final NodeList outXml = xml.getElementsByTagName(ParseHandler.OUT);
        if (outXml.getLength() > 0) {
            this.out = ((Element) outXml.item(0)).getTextContent();
        } else {
            this.out = "";
        }

    }

    public int getOffset() {
        return offset;
    }

    public String getIn() {
        return in;
    }

    public String getOut() {
        return out;
    }

    public String getValue() {
        return in;
    }

    public Period getT0() {
        return t0;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return in;
    }

    public String getType() {
        return TYPE;
    }

    public String getDictionary() {
        return dict;
    }

    @Override
    public void writeXML(final Document xml, final Element xmlIndicator) {
        xmlIndicator.setAttribute(ParseHandler.ID, getId());
        xmlIndicator.setAttribute(ParseHandler.TYPE, getType());
        xmlIndicator.setAttribute(ParseHandler.T0, Formatter.getMilliFormatter().print(t0.normalizedStandard()));
        xmlIndicator.setAttribute(ParseHandler.OFFSET, Integer.toString(getOffset()));
        final Element inXml = xml.createElement(ParseHandler.IN);
        inXml.setTextContent(in);
        inXml.setAttribute(ParseHandler.DICT, dict);
        final Element outXml = xml.createElement(ParseHandler.OUT);
        outXml.setTextContent(out);
        xmlIndicator.appendChild(inXml);
        xmlIndicator.appendChild(outXml);
    }
}
