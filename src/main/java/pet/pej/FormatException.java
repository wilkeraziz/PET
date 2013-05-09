/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.pej;

/**
 * Simply wraps a standar exception giving it a specific type.
 * @author waziz
 */
public class FormatException extends Exception {

    public FormatException(final String message) {
        super(message);
    }
}