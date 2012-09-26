/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pet.usr.adapter;

import pet.annotation.Status;

/**
 *
 * @author waziz
 */
public enum EditionStatus implements Status{
    UNDEFINED{
        @Override
        public String toString(){
            return "...";
        }
    },
    READY_TO_EDIT{
        @Override
        public String toString(){
            return "ready to edit!";
        }
    },
    EDITING{
        @Override
        public String toString(){
            return "editing...";
        }
    },
    DONE{
        @Override
        public String toString(){
            return "done!";
        }
    },
}
