/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.annotation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pet.annotation.xml.ParseHandler;

/**
 *
 * @author waziz
 */
public abstract class AbstractEffortIndicator implements EffortIndicator {

    protected boolean wrapped;

    protected AbstractEffortIndicator(final boolean wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String getXMLTag() {
        return (wrapped) ? ParseHandler.ACTION : ParseHandler.INDICATOR;
    }

    @Override
    public void writeXML(final Document xml, final Element xmlIndicator) {
        xmlIndicator.setAttribute(ParseHandler.ID, getId());
        xmlIndicator.setAttribute(ParseHandler.TYPE, getType());
        xmlIndicator.setTextContent(toString());
    }

    public EffortIndicator wrapIt() {
        wrapped = true;
        return this;
    }
}
