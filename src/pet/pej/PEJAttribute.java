/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.pej;

import java.util.HashSet;
import java.util.Set;

/**
 * Defines an attribute in a PEJ file.
 * It's simply a pair (key, value).
 * In text representation it should not contain spaces, that is, <key>=<value>
 * @author waziz
 */
public class PEJAttribute {

    private final String key;
    private final String value;

    public PEJAttribute(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    /**
     * Compares only the key
     * @param obj
     * @return 
     */
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof PEJAttribute) {
            final PEJAttribute other = (PEJAttribute) obj;
            return key.equals(other.key);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }

    public static Set<PEJAttribute> parse(final String line) throws FormatException {
        final Set<PEJAttribute> set = new HashSet<PEJAttribute>();
        String[] pairs = line.trim().split("\\s+");
        for (int p = 0; p < pairs.length; p++) {
            String pair = pairs[p];
            String[] parts = pair.split("=");
            if (parts.length == 2) {
                set.add(new PEJAttribute(parts[0], parts[1]));
            } else {
                throw new FormatException("An attribute must be formatted as <key>=<value> (no spaces)");
            }
        }
        return set;
    }
}
