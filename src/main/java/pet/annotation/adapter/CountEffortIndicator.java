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
public class CountEffortIndicator extends AbstractEffortIndicator{

    
    public static final String TYPE = "count";
    private final String id;
    private final int count;

    public CountEffortIndicator(final String id,
            final int count) {
        super(false);
        this.id = id;
        this.count  = count;
    }
    
    public CountEffortIndicator(final String id,
            final int count, 
            final boolean wrapped) {
        super(wrapped);
        this.id = id;
        this.count  = count;
    }

    public CountEffortIndicator(final Element xml, 
            final boolean wrapped) {
        super(wrapped);
        this.id = xml.getAttribute(ParseHandler.ID);
        this.count = Integer.parseInt(xml.getTextContent());
    }

    public int getCount() {
        return count;
    }

    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public String toString(){
        return Integer.toString(count);
    }
    
    public String getType(){
        return TYPE;
    }
    
    
    
}