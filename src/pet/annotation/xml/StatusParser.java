/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.annotation.xml;

import org.w3c.dom.Element;
import pet.annotation.Status;
import pet.annotation.adapter.StatusAdapter;

/**
 *
 * @author waziz
 */
public class StatusParser {
    
    public static Status parse(final Element xml){
        final String str = xml.getAttribute(ParseHandler.STATUS);
        if (str.isEmpty()){
            return StatusAdapter.NEVER_STARTED;
        } else{
            return StatusAdapter.valueOf(str);
        }
    }
    
}
