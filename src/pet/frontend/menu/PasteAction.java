/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.frontend.menu;

import java.awt.event.ActionEvent;
import javax.swing.text.DefaultEditorKit;
import pet.config.ContextHandler;
import pet.signal.PETCommandEvent;


public class PasteAction extends DefaultEditorKit.CutAction {

    public PasteAction() {
        super();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ContextHandler.signalManager().fire(new PETCommandEvent(PETCommandEvent.CommandType.PASTE));
        super.actionPerformed(e);
    }
}