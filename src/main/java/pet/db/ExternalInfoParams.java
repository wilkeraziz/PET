/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pet.db;

/**
 *
 * @author waziz
 */
public interface ExternalInfoParams {

    int sourceMinOrder();
    int sourceMaxOrder();
    int targetMinOrder();
    int targetMaxOrder();
    int sourceMinLength();
    int targetMinLengt();
    boolean sourceNoLonger();
    boolean targetNoLonger();

}
