/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pet.usr.adapter;

import pet.annotation.Unit;
import java.util.List;

/**
 *
 * @author waziz
 */
public interface EditingUnitSelector {
    Unit getEditingUnit();

    List<Unit> getUnits();
}
