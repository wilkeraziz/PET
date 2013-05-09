/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pet.frontend.menu;

import javax.swing.JRadioButtonMenuItem;
import pet.annotation.UnitResult;

/**
 *
 * @author waziz
 */
public class PETResultMenuItem extends JRadioButtonMenuItem{

    private final UnitResult result;

    public PETResultMenuItem(final UnitResult result){
        super(result.getTranslation().getProducer() + " (" + result.getRevision() + ")");
        this.result = result;
    }

    public UnitResult getResult(){
        return result;
    }

}
