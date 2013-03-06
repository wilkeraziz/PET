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
public class PETCommandEvent extends PETAbstractEvent{
    
    public static final String TYPE = "command";
    
    public static enum CommandType{
        COPY,
        PASTE,
        CUT,
        DELETE,
        BACKSPACE,
        UNDO,
        REDO
    }
    
    private final CommandType command;
    private final int offset;
    
    public PETCommandEvent(final CommandType command, final int offset){
        super();
        this.command = command;
        this.offset = offset;
    }
    
    public PETCommandEvent(final Element xml){
        super(xml);
        this.command = CommandType.valueOf(xml.getTextContent());
        this.offset = Integer.parseInt(xml.getAttribute(ParseHandler.OFFSET));
    }
    
    @Override
    public String getType(){
        return TYPE;
    }
    
    public CommandType getCommand(){
        return command;
    }
    
    public int getOffset() {
        return offset;
    }
    
    @Override
    public String toString(){
        return command.toString();
    }
    
    @Override
    public void writeXML(final Document xml, final Element xmlEvent, final long t0){
        super.writeXML(xml, xmlEvent, t0);
        xmlEvent.setAttribute(ParseHandler.OFFSET, Integer.toString(offset));
        xmlEvent.setTextContent(toString());
    }
    
}
