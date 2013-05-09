/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.annotation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author waziz
 */
public interface EffortIndicator {

    
    String getId();
    String getType();
    String getXMLTag();
    void writeXML(final Document xml, final Element xmlIndicator);
    
}
