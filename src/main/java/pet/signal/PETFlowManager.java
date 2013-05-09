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
public class PETFlowManager {
    
    private final List<PETFlowListener> listeners;
    
    public PETFlowManager(){
        listeners = new ArrayList<PETFlowListener>();
    }
    
    public void addListener(final PETFlowListener listener){
        this.listeners.add(listener);
    }
    
    public void editingIsAboutToStart(){
        for (final PETFlowListener listener : listeners){
            listener.editingIsAboutToStart();
        }
    }
    
    public void editingHasStarted(){
        for (final PETFlowListener listener : listeners){
            listener.editingHasStarted();
        }
    }
    
    public void editingIsAboutToFinish(){
        for (final PETFlowListener listener : listeners){
            listener.editingIsAboutToFinish();
        }
    }
    
    public void editingHasFinished(){
        for (final PETFlowListener listener : listeners){
            listener.editingHasFinished();
        }
    }
    
}
