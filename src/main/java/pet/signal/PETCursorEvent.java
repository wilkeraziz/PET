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
public class PETCursorEvent extends PETAbstractEvent{
    
    public static final String TYPE = "cursor";
    
    private final int dot;
    private final int mark;
    
    public PETCursorEvent(final int mark, final int dot){
        super();
        this.mark = mark;
        this.dot = dot;
    }
    
    public PETCursorEvent(final Element xml){
        super(xml);
        this.mark = Integer.parseInt(xml.getAttribute(ParseHandler.MARK));
        this.dot = Integer.parseInt(xml.getAttribute(ParseHandler.DOT));
    }
    
    @Override
    public String getType(){
        return TYPE;
    }
    
    public int getDot(){
        return dot;
    }
    
    public int getMark(){
        return mark;
    }
        
    @Override
    public void writeXML(final Document xml, final Element xmlEvent, final long t0){
        super.writeXML(xml, xmlEvent, t0);
        xmlEvent.setAttribute(ParseHandler.MARK, Integer.toString(mark));
        xmlEvent.setAttribute(ParseHandler.DOT, Integer.toString(dot));
    }
    
}
