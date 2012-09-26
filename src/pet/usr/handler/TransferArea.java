/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.usr.handler;

import pet.frontend.components.UnitGUI;

/**
 *
 * @author Wilker
 */
public class TransferArea {
    
    public enum TransferType{
        SOURCE,
        TARGET,
        CONTEXT
    }
    
    private final static ThreadLocal<UnitGUI> object = new ThreadLocal<UnitGUI>();
    private final static ThreadLocal<TransferType> type = new ThreadLocal<TransferType>();
    

    public static void initialize() {
        release();
        TransferArea.object.set(null);
        TransferArea.type.set(null);
    }

    public static void release() {
        object.remove();
        type.remove();
    }
    
    public static void setSource(final UnitGUI source){
        TransferArea.object.set(source);
    }
    
    public static UnitGUI getSource(){
        return object.get();
    }
    
    public static void setType(final TransferType type){
        TransferArea.type.set(type);
    }
    
    public static TransferType getType(){
        return type.get();
    }
}
