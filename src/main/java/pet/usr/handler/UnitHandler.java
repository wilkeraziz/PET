package pet.usr.handler;

import java.util.HashMap;
import java.util.Map;
import pet.annotation.EditingListener;
import pet.frontend.EditingStatusProvider;
import pet.frontend.components.UnitGUI;
import pet.frontend.UnitProvider;

public class UnitHandler {

    private final static ThreadLocal<UnitProvider> unitProvider = new ThreadLocal<UnitProvider>();
    private final static ThreadLocal<EditingStatusProvider> editingStatusProvider = new ThreadLocal<EditingStatusProvider>();
    private final static ThreadLocal<EditingListener> editingListener = new ThreadLocal<EditingListener>();
    private final static ThreadLocal<UnitGUI> activeSource = new ThreadLocal<UnitGUI>();
    private final static ThreadLocal<UnitGUI> activeTarget = new ThreadLocal<UnitGUI>();
    private final static ThreadLocal<UnitGUI> contextPane = new ThreadLocal<UnitGUI>();
    private final static ThreadLocal<Map<String, Integer>> producerToMask = new ThreadLocal<Map<String, Integer>>();

    public static void initialize(final UnitProvider unitProvider,
            final EditingStatusProvider editingStatusProvider,
            final EditingListener editingListener,
            final UnitGUI activeSource,
            final UnitGUI activeTarget,
            final UnitGUI contextPane) {
        release();
        UnitHandler.unitProvider.set(unitProvider);
        UnitHandler.editingStatusProvider.set(editingStatusProvider);
        UnitHandler.editingListener.set(editingListener);
        UnitHandler.activeSource.set(activeSource);
        UnitHandler.activeTarget.set(activeTarget);
        UnitHandler.contextPane.set(contextPane);
        UnitHandler.producerToMask.set(new HashMap<String, Integer>());
    }

    public static void release() {
        unitProvider.remove();
        editingStatusProvider.remove();
        editingListener.remove();
        activeSource.remove();
        activeTarget.remove();
        contextPane.remove();
        producerToMask.remove();
    }

    public static UnitProvider getUnitProvider() {
        return unitProvider.get();
    }

    public static EditingStatusProvider getEditingStatusProvider() {
        return editingStatusProvider.get();
    }

    public static EditingListener getEditingListener() {
        return editingListener.get();
    }

    public static UnitGUI getActiveSource() {
        return activeSource.get();
    }

    public static UnitGUI getActiveTarget() {
        return activeTarget.get();
    }

    public static UnitGUI getContextPane() {
        return contextPane.get();
    }

    public static String producerToMask(final String producer){
        String[] parts = producer.split(" ");
        String revision = "";
        if (parts.length == 2){
            revision = parts[1];
        }
        parts = parts[0].split("\\.");
        if (parts.length > 0){
            
        }


        final Map<String, Integer> map = producerToMask.get();
        Integer mask = map.get(parts[0]);
        if (mask != null){
            return mask.toString();
        } else{
            mask = map.size() + 1;
            map.put(producer, mask);
            return mask.toString();
        }
    }
}
