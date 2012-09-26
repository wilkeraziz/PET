/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.annotation.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import pet.annotation.AbstractEffortIndicator;
import pet.annotation.EffortIndicator;
import pet.annotation.xml.EffortIndicatorParser;
import pet.annotation.xml.ParseHandler;

/**
 *
 * @author waziz
 */
public class WrapEffortIndicator extends AbstractEffortIndicator {

    public static final String TYPE = "wrap";
    private final String id;
    private final List<EffortIndicator> indicators;

    public WrapEffortIndicator(final String id,
            final List<EffortIndicator> indicators) {
        super(false);
        this.id = id;
        this.indicators = Collections.unmodifiableList(new ArrayList<EffortIndicator>(indicators));
    }
    
    public WrapEffortIndicator(final String id,
            final EffortIndicator first, 
            final EffortIndicator second) {
        super(false);
        this.id = id;
        final List<EffortIndicator> list = new ArrayList<EffortIndicator>(2);
        list.add(first);
        list.add(second);
        this.indicators = Collections.unmodifiableList(list);
    }

    public WrapEffortIndicator(final Element xml) {
        super(false);
        this.id = xml.getAttribute(ParseHandler.ID);
        final NodeList xmlList = xml.getElementsByTagName(ParseHandler.ACTION);
        final List<EffortIndicator> list = new ArrayList<EffortIndicator>(xmlList.getLength());
        for (int i = 0; i < xmlList.getLength(); i++){
            final Element xmlIndicator = (Element) xmlList.item(i);
            list.add(EffortIndicatorParser.parse(xmlIndicator, true));
        }
        this.indicators = Collections.unmodifiableList(list);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return indicators.toString();
    }

    public String getType() {
        return TYPE;
    }
    
    public List<EffortIndicator> getIndicators(){
        return indicators;
    }
    
    @Override
    public void writeXML(final Document xml, final Element xmlIndicator) {
        xmlIndicator.setAttribute(ParseHandler.ID, getId());
        xmlIndicator.setAttribute(ParseHandler.TYPE, getType());
        for (final EffortIndicator indicator : indicators){
            final Element child = xml.createElement(indicator.getXMLTag());
            xmlIndicator.appendChild(child);
            indicator.writeXML(xml, child);
        }
    }
}
