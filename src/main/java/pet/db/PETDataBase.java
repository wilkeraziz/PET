/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pet.db;

import java.util.List;
import java.util.Set;

/**
 *
 * @author waziz
 */
public interface PETDataBase {
    List<PETQueryResult> getData(final String query);
    void getData(final String query, final Set<PETQueryResult> results);
    String getAlias();
}
