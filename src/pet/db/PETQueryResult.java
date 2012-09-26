/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pet.db;

import java.util.Map;

/**
 *
 * @author waziz
 */
public interface PETQueryResult {
    String getType();
    String getValue();
    Map<String, String> getAttributes();
}
