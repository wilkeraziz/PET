/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.signal;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author waziz
 */
public class PETNavigationEvent extends PETAbstractEvent{
    
    public static final String TYPE = "navigation";
    
    public static enum NavigationType{
        HOME,
        END,
        PGUP,
        PGDOWN,
        UP,
        DOWN,
        LEFT,
        RIGHT       
    }
    
    private final NavigationType command;
    
    public PETNavigationEvent(final NavigationType command){
        super();
        this.command = command;
    }
    
    public PETNavigationEvent(final Element xml){
        super(xml);
        this.command = NavigationType.valueOf(xml.getTextContent());
    }
    
    @Override
    public String getType(){
        return TYPE;
    }
    
    public NavigationType getCommand(){
        return command;
    }
    
    @Override
    public String toString(){
        return command.toString();
    }
        
    @Override
    public void writeXML(final Document xml, final Element xmlEvent, final long t0){
        super.writeXML(xml, xmlEvent, t0);
        xmlEvent.setTextContent(toString());
    }
    
}
