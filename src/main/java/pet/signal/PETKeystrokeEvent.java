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
public class PETKeystrokeEvent extends PETAbstractEvent {

    public static final String TYPE = "keystroke";
    private final char ch;
    private final int offset;

    public PETKeystrokeEvent(final char ch, final int offset) {
        super();
        this.ch = ch;
        this.offset = offset;
    }

    public PETKeystrokeEvent(final Element xml) {
        super(xml);
        this.ch = xml.getTextContent().charAt(0);
        this.offset = Integer.parseInt(xml.getAttribute(ParseHandler.OFFSET));
    }

    public char getChar() {
        return ch;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return Character.toString(ch);
    }

    @Override
    public void writeXML(final Document xml, final Element xmlEvent, final long t0) {
        super.writeXML(xml, xmlEvent, t0);
        xmlEvent.setAttribute(ParseHandler.OFFSET, Integer.toString(offset));
        xmlEvent.setTextContent(toString());
    }
}
