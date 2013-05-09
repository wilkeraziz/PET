/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.annotation.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pet.annotation.EffortIndicator;
import pet.annotation.adapter.*;

/**
 *
 * @author waziz
 */
public class EffortIndicatorParser {

    public static Element toXML(final Document xml,
            final EffortIndicator indicator) {
        final Element xmlIndicator = xml.createElement(indicator.getXMLTag());
        indicator.writeXML(xml, xmlIndicator);
        return xmlIndicator;
    }

    public static EffortIndicator parse(final Element xml, final boolean wrapped) {
        final String type = xml.getAttribute(ParseHandler.TYPE);
        if (type.equals(TimeEffortIndicator.TYPE)) {
            return new TimeEffortIndicator(xml, wrapped);
        } else if (type.equals(FlagEffortIndicator.TYPE)) {
            return new FlagEffortIndicator(xml, wrapped);
        } else if (type.equals(CountEffortIndicator.TYPE)) {
            return new CountEffortIndicator(xml, wrapped);
        } else if (type.equals(ChangeEffortIndicator.TYPE)) {
            return new ChangeEffortIndicator(xml, wrapped);
        } else if (type.equals(SystemSelectionEffortIndicator.TYPE)) {
            return new SystemSelectionEffortIndicator(xml, wrapped);
        } else if (type.equals(DictionaryEffortIndicator.TYPE)) {
            return new DictionaryEffortIndicator(xml, wrapped);
        } else if (type.equals(WrapEffortIndicator.TYPE)) {
            return new WrapEffortIndicator(xml);
        } else {
            return new StringEffortIndicator(xml, wrapped);
        }
    }
}
