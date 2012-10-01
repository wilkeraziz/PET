/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.annotation.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pet.signal.PETCommandEvent;
import pet.signal.PETEditOperationEvent;
import pet.signal.PETEvent;
import pet.signal.PETFlowEvent;
import pet.signal.PETKeystrokeEvent;
import pet.signal.PETNavigationEvent;
import pet.signal.PETTextChangeEvent;

/**
 *
 * @author waziz
 */
public class EventParser {

    public static Element toXML(final Document xml,
            final PETEvent event, final long t0) {
        final Element xmlEvent = xml.createElement(event.getType());
        event.writeXML(xml, xmlEvent, t0);
        return xmlEvent;
    }

    public static PETEvent parse(final Element xml) {
        final String type = xml.getTagName();
        if (type.equals(PETCommandEvent.TYPE)) {
            return new PETCommandEvent(xml);
        } else if (type.equals(PETNavigationEvent.TYPE)) {
            return new PETNavigationEvent(xml);
        } else if (type.equals(PETTextChangeEvent.TYPE)) {
            return new PETTextChangeEvent(xml);
        } else if (type.equals(PETKeystrokeEvent.TYPE)) {
            return new PETKeystrokeEvent(xml);
        } else if (type.equals(PETEditOperationEvent.TYPE)){
            return new PETEditOperationEvent(xml);
        } else if (type.equals(PETFlowEvent.TYPE)){
            return new PETFlowEvent(xml);
        } else {
            System.err.println("Skipping unknown event: " + type);
        }
        return null;
    }
}
