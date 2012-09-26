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
public class FlagEffortIndicator extends AbstractEffortIndicator {

    public static final String TYPE = "flag";
    private final String id;
    private final boolean flag;

    public FlagEffortIndicator(final String id,
            final boolean flag) {
        super(false);
        this.id = id;
        this.flag = flag;
    }
    
    public FlagEffortIndicator(final String id,
            final boolean flag, 
            final boolean wrapped) {
        super(wrapped);
        this.id = id;
        this.flag = flag;
    }

    public FlagEffortIndicator(final Element xml, 
            final boolean wrapped) {
        super(wrapped);
        this.id = xml.getAttribute(ParseHandler.ID);
        this.flag = Boolean.parseBoolean(xml.getTextContent());
    }

    public boolean isOn() {
        return flag;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return Boolean.toString(flag);
    }

    public String getType() {
        return TYPE;
    }
}
