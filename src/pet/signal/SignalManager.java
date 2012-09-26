/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.signal;

/**
 *
 * @author waziz
 */
public interface SignalManager {
    
    void fire(final PETEvent event);
    void addListener(final PETEventListener listener);
    void removeListener(final PETEventListener listener);
    
    void fire(final Signal e);
    void fire(final Signal e, final SignalPackage o);
    void addListener(final SignalListener listener);
    void replaceSingleListener(final SingleSignalListener listener);
    
}
