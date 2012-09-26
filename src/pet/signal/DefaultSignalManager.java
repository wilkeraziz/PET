/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.signal;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author waziz
 */
public class DefaultSignalManager implements SignalManager {

    private final List<SignalListener> listening;
    private SingleSignalListener singleListener;
    private final List<PETEventListener> evtListeners;

    public DefaultSignalManager() {
        this.listening = new ArrayList<SignalListener>();
        this.singleListener = null;
        this.evtListeners = new ArrayList<PETEventListener>();
    }

    @Override
    public void fire(final PETEvent event) {
        for (final PETEventListener listener : evtListeners) {
            listener.treat(event);
        }
    }

    @Override
    public void addListener(final PETEventListener listener) {
        evtListeners.add(listener);
    }
    
    @Override
    public void removeListener(final PETEventListener listener) {
        evtListeners.remove(listener);
    }

    public void fire(final Signal signal) {
        if (singleListener != null) {
            singleListener.treat(signal);
        }
        for (final SignalListener listener : listening) {
            listener.treat(signal);
        }
    }

    public void fire(final Signal signal, final SignalPackage pack) {
        if (singleListener != null) {
            singleListener.treat(signal, pack);
        }
        for (final SignalListener listener : listening) {
            listener.treat(signal, pack);
        }
    }

    public void addListener(final SignalListener listener) {
        listening.add(listener);
    }

    public void replaceSingleListener(final SingleSignalListener listener) {
        singleListener = listener;
    }
}
