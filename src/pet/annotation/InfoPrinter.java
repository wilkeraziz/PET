/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pet.annotation;

/**
 *
 * @author waziz
 */
public interface InfoPrinter {

    String getGeneralInfo(final Unit task);


    String getExternalTargetInfo(final Unit task);

    String getExternalSourceInfo(final Unit task);
}
