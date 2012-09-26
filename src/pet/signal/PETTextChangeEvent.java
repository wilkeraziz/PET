/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.signal;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import pet.annotation.xml.ParseHandler;

/**
 *
 * @author waziz
 */
public class PETTextChangeEvent extends PETAbstractEvent {
    
    public static final String TYPE = "change";
    
    public static enum Reason{
        STANDARD{
            @Override
            public String toString(){
                return "";
            }
        },
        INITIATE{
            @Override
            public String toString(){
                return "[I]";
            }
        },
    }

    private final int offset;
    private final String in;
    private final String out;

    public PETTextChangeEvent(final int offset, final String in, final String out) {
        this.offset = offset;
        this.in = in;
        this.out = out;
    }
    
    public PETTextChangeEvent(final Element xml){
        super(xml);
        this.offset = Integer.parseInt(xml.getAttribute(ParseHandler.OFFSET));
        final NodeList xmlIn = xml.getElementsByTagName(ParseHandler.IN);
        if (xmlIn.getLength() == 1){
            this.in = ((Element) xmlIn.item(0)).getTextContent();
        } else{
            this.in = "";
        }
        final NodeList xmlOut = xml.getElementsByTagName(ParseHandler.OUT);
        if (xmlOut.getLength() == 1){
            this.out = ((Element) xmlOut.item(0)).getTextContent();
        } else{
            this.out = "";
        }
    }

    @Override
    public String getType(){
        return TYPE;
    }
    
    public int getOffset() {
        return offset;
    }

    public String getIn() {
        return in;
    }

    public String getOut() {
        return out;
    }

    @Override
    public String toString() {
        return "(" + in + ", " + out + ")@" + Integer.toString(offset);
    }
        
    @Override
    public void writeXML(final Document xml, final Element xmlEvent, final long t0){
        super.writeXML(xml, xmlEvent, t0);
        xmlEvent.setAttribute(ParseHandler.OFFSET, Integer.toString(offset));
        if (!in.isEmpty()){
            final Element inXml = xml.createElement(ParseHandler.IN);
            inXml.setTextContent(in);
            xmlEvent.appendChild(inXml);
        }
        if (!out.isEmpty()){
            final Element outXml = xml.createElement(ParseHandler.OUT);
            outXml.setTextContent(out);
            xmlEvent.appendChild(outXml);
        }
    }
}
