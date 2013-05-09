package pet.annotation.adapter;

import pet.annotation.SegmentType;
import pet.annotation.xml.ParseHandler;

public enum SegmentTypeAdapter implements SegmentType {
	SOURCE{
            @Override
            public String toString(){
                return ParseHandler.S;
            }
        }, 
        REFERENCE{
            @Override
            public String toString(){
                return ParseHandler.R;
            }
            
        }, 
        MT{
            @Override
            public String toString(){
                return ParseHandler.MT;
            }
            
        }, 
        HT{
            @Override
            public String toString(){
                return ParseHandler.HT;
            }
            
        }, 
        POST_EDITED_MT {
            @Override
            public String toString(){
                return ParseHandler.PE;
            }
            
        },
        CAPTION;
}
