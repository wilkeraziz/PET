/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.pej;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pet.annotation.xml.ParseHandler;

/**
 *
 * @author waziz
 */
public class PEJUnit {
    
    public static class Builder{
        private final Set<PEJAttribute> attributes;
        private final List<PEJSegment> segments;
        
        public Builder(){
            attributes = new HashSet<PEJAttribute>();
            segments = new ArrayList<PEJSegment>();
        }
        
        public PEJUnit.Builder addAttribute(final PEJAttribute attr){
            this.attributes.add(attr);
            return this;
        }
        
        public PEJUnit.Builder addAttributes(final Collection<PEJAttribute> attrs){
            this.attributes.addAll(attrs);
            return this;
        }
        
        public PEJUnit.Builder addSegment(final PEJSegment seg){
            this.segments.add(seg);
            return this;
        }
        
        public PEJUnit.Builder addSegments(final List<PEJSegment> segs){
            this.segments.addAll(segs);
            return this;
        }
        
        public PEJUnit build(){
            return new PEJUnit(this);
        }
    }
    
    private final Set<PEJAttribute> attributes;
    private final List<PEJSegment> segments;
    
    private PEJUnit(final PEJUnit.Builder builder){
        this.attributes = Collections.unmodifiableSet(builder.attributes);
        this.segments = Collections.unmodifiableList(builder.segments);
    }
    
    public Element getXml(final Document xml){
        final Element unit = xml.createElement(ParseHandler.UNIT);
        for (final PEJAttribute attr : attributes){
            unit.setAttribute(attr.getKey(), attr.getValue());
        }
        for (final PEJSegment seg : segments){
            unit.appendChild(seg.getXml(xml));
        }
        return unit;
    }
    
    
    
}
