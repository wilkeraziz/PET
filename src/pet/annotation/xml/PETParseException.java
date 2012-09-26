/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pet.annotation.xml;

/**
 *
 * @author waziz
 */
public class PETParseException extends Exception {

    final String type;


    public PETParseException(final String type, final String message) {
        super(message);
        this.type = type;
    }

    public String getType(){
        return type;
    }


}
