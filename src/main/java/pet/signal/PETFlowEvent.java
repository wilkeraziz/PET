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
public class PETFlowEvent extends PETAbstractEvent{
    
    public static final String TYPE = "flow";
    
    public static enum ActionType{
        EDITING_START,
        EDITING_END,
        ASSESSING_START,
        ASSESSING_END,
        
    }
    
    private final ActionType action;
    
    public PETFlowEvent(final ActionType action){
        super();
        this.action = action;
    }
    
    public PETFlowEvent(final Element xml){
        super(xml);
        this.action = ActionType.valueOf(xml.getTextContent());
    }
    
    @Override
    public String getType(){
        return TYPE;
    }
    
    public ActionType getAction(){
        return action;
    }
    
    @Override
    public String toString(){
        return action.toString();
    }
    
    @Override
    public void writeXML(final Document xml, final Element xmlEvent, final long t0){
        super.writeXML(xml, xmlEvent, t0);
        xmlEvent.setTextContent(toString());
    }
    
}
