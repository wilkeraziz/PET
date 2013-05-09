/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.signal;

/**
 *
 * @author waziz
 */
public interface SingleSignalListener {
    void treat(final Signal s);
    void treat(final Signal s, final SignalPackage o);
}
