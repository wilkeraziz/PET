/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.pej;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pet.annotation.xml.ParseHandler;

/**
 * An unmodifiable segment.
 * Use PEJSegment.Builder to construct objects.
 * @author waziz
 */
public class PEJSegment {
    
    public static enum SegmentType{
        S,
        R,
        T {
            @Override
            public String toString(){
                return "MT";
            }
        }
    }
    
    public static class Builder{
        private final SegmentType type;
        private String text;
        private final Set<PEJAttribute> attributes;
        
        public Builder(final SegmentType type){
            this.type = type;
            this.attributes = new HashSet<PEJAttribute>();
        }
        
        public PEJSegment.Builder text(final String text){
            this.text = text;
            return this;
        }
        
        public PEJSegment.Builder addAttribute(final PEJAttribute attr){
            this.attributes.add(attr);
            return this;
        }
        
        public PEJSegment.Builder addAttributes(final Collection<PEJAttribute> attrs){
            this.attributes.addAll(attrs);
            return this;
        }
        
        public PEJSegment build(){
            return new PEJSegment(this);
        }
        
    }
    
    private final SegmentType type;
    private final String text;
    private final Set<PEJAttribute> attributes;
    
    private PEJSegment(final PEJSegment.Builder builder){
        this.type = builder.type;
        this.text = builder.text;
        this.attributes = Collections.unmodifiableSet(builder.attributes);
    }
    
    public SegmentType getType(){
        return type;
    }
    
    public String getText(){
        return text;
    }
    
    public Set<PEJAttribute> getAttributes(){
        return attributes;
    }
    
    public Element getXml(final Document xml){
        final Element segment = xml.createElement(type.toString());
        for (final PEJAttribute attr : attributes){
            segment.setAttribute(attr.getKey(), attr.getValue());
        }
        segment.setTextContent(text);
        return segment;
    }
    
}
