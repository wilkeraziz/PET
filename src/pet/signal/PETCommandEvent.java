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
public class PETCommandEvent extends PETAbstractEvent{
    
    public static final String TYPE = "command";
    
    public static enum CommandType{
        COPY,
        PASTE,
        CUT,
        DELETE,
        BACKSPACE,
        INSERT,
        REPLACE,
        SHIFT,
        TRIM,
        UNDO,
        REDO
    }
    
    private final CommandType command;
    
    public PETCommandEvent(final CommandType command){
        super();
        this.command = command;
    }
    
    public PETCommandEvent(final Element xml){
        super(xml);
        this.command = CommandType.valueOf(xml.getTextContent());
    }
    
    @Override
    public String getType(){
        return TYPE;
    }
    
    public CommandType getCommand(){
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
