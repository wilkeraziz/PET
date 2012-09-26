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
public interface PETEvent {
    
    long when();
    
    String getType();
    
    void writeXML(final Document xml, final Element xmlEvent, final long t0);

}
