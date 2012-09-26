package pet.annotation;

import java.util.List;

public interface Assessment {
	
	boolean isBetterThan(final Assessment other);
        
        String getId();

        String getComment();

        List<String> toStringList();
	
	
}
