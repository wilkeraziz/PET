/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.signal;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pet.annotation.xml.ParseHandler;

/**
 *
 * @author waziz
 */
public abstract class PETAbstractEvent implements PETEvent{
    
    private final long when;
    
    public PETAbstractEvent(){
        when = Timer.time();
    }
    
    public PETAbstractEvent(final Element xml){
        this.when = Long.parseLong(xml.getAttribute(ParseHandler.T));
    }
    
    @Override
    public long when(){
        return when;
    }
    
    @Override
    public String toString(){
        return Long.toString(when);
    }
    
    @Override
    public void writeXML(final Document xml, final Element xmlEvent, final long t0){
        xmlEvent.setAttribute(ParseHandler.T, Long.toString(when - t0));
    }
    
    
    
}
