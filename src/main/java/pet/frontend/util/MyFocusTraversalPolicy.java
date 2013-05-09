/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.frontend.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author waziz
 */
public class MyFocusTraversalPolicy extends FocusTraversalPolicy {

    final List<Component> focusList;
    int focusNumber = 0;

    public MyFocusTraversalPolicy(final List<Component> components) {
        focusList = Collections.unmodifiableList(components);
    }

    public Component getComponentAfter(final Container focusCycleRoot, final Component aComponent) {
        focusNumber = (focusNumber + 1) % focusList.size();
        if (!focusList.get(focusNumber).isEnabled()) {
            getComponentAfter(focusCycleRoot, focusList.get(focusNumber));
        }
        return focusList.get(focusNumber);
    }

    public Component getComponentBefore(final Container focusCycleRoot, final Component aComponent) {
        focusNumber = (focusList.size() + focusNumber - 1) % focusList.size();
        if (!focusList.get(focusNumber).isEnabled()) {
            getComponentBefore(focusCycleRoot, focusList.get(focusNumber));
        }
        return focusList.get(focusNumber);
    }

    public Component getDefaultComponent(final Container focusCycleRoot) {
        return focusList.get(0);
    }

    public Component getLastComponent(Container focusCycleRoot) {
        return focusList.get(focusList.size() - 1);
    }

    public Component getFirstComponent(Container focusCycleRoot) {
        return focusList.get(0);
    }
}
