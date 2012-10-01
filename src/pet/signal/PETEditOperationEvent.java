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
public class PETEditOperationEvent extends PETAbstractEvent{
    
    public static final String TYPE = "editop";
    
    public static enum EditOperation{
        DELETE, 
        INSERT,
        REPLACE,
        SHIFT,
        TRIM
    }
    
    private final EditOperation operation;
    
    public PETEditOperationEvent(final EditOperation operation){
        super();
        this.operation = operation;
    }
    
    public PETEditOperationEvent(final Element xml){
        super(xml);
        this.operation = EditOperation.valueOf(xml.getTextContent());
    }
    
    @Override
    public String getType(){
        return TYPE;
    }
    
    public EditOperation getOperation(){
        return operation;
    }
    
    @Override
    public String toString(){
        return operation.toString();
    }
    
    @Override
    public void writeXML(final Document xml, final Element xmlEvent, final long t0){
        super.writeXML(xml, xmlEvent, t0);
        xmlEvent.setTextContent(toString());
    }
    
}
