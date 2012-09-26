/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.annotation.adapter;

import org.w3c.dom.Element;
import pet.annotation.AbstractEffortIndicator;
import pet.annotation.xml.ParseHandler;

/**
 *
 * @author waziz
 */
public class StringEffortIndicator extends AbstractEffortIndicator {

    public static final String TYPE = "string";
    private final String id;
    private final String value;

    public StringEffortIndicator(final String id,
            final String value, final boolean wrapped) {
        super(wrapped);
        this.id = id;
        this.value = value;
    }

    public StringEffortIndicator(final Element xml, final boolean wrapped) {
        super(wrapped);
        this.id = xml.getAttribute(ParseHandler.ID);
        this.value = xml.getTextContent();
    }

    public String getValue() {
        return value;
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
}
