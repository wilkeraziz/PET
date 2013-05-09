/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.frontend.util;

/**
 *
 * @author waziz
 */
public class DisplayObject<T> {

    public interface Handler<T> {

        T display(final T object);
    }

    public Handler<T> show = new Handler<T>() {

        public T display(T object) {
            return object;
        }
    };

    public Handler<T> hide = new Handler<T>() {
        public T display(T object) {
            return null;
        }
    };

    
}
