/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.annotation;

import javax.swing.event.DocumentEvent;

/**
 *
 * @author waziz
 */
public interface EditingListener {

    void on();

    void off();

    boolean isLogging();

    void insert(final DocumentEvent event);

    void remove(final DocumentEvent event);

    void change(final DocumentEvent event);
}
