/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.db;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author waziz
 */
public class DefaultQueryResult implements PETQueryResult{

    private final Map<String, String> map;
    private final String type;
    private final String value;

    public DefaultQueryResult(final String type, final String value) {
        this.type = type;
        this.value = value;
        this.map = new HashMap<String, String>();
    }

    public DefaultQueryResult(final String type, final String value, final String attKey, final String attValue) {
        this.type = type;
        this.value = value;
        this.map = new HashMap<String, String>();
        this.map.put(attKey, attValue);
    }

    public DefaultQueryResult(final String type, final String value, final Map<String, String> map) {
        this.type = type;
        this.value = value;
        this.map = new HashMap<String, String>(map);
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
    
    @Override
    public String toString(){
        return value;
    }

    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(map);
    }
}
