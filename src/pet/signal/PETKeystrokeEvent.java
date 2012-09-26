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
public class PETKeystrokeEvent extends PETAbstractEvent{
    
    public static final String TYPE = "keystroke";
    private final char ch;
    
    public PETKeystrokeEvent(final char ch){
        super();
        this.ch = ch;
    }
    
    public PETKeystrokeEvent(final Element xml){
        super(xml);
        this.ch = xml.getTextContent().charAt(0);
    }
    
    public char getChar(){
        return ch;
    }
    
    @Override
    public String getType(){
        return TYPE;
    }
    
    @Override
    public String toString(){
        return Character.toString(ch);
    }
        
    @Override
    public void writeXML(final Document xml, final Element xmlEvent, final long t0){
        super.writeXML(xml, xmlEvent, t0);
        xmlEvent.setTextContent(toString());
    }
    
}
