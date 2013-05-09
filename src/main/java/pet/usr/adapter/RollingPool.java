/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pet.usr.adapter;

import java.util.List;

/**
 *
 * @author waziz
 */
public interface RollingPool<T> {

    boolean moveBackward();

    boolean moveForward();
    
    boolean canMoveBackward();

    boolean canMoveForward();

    void findNextTaskToDo();

    void findPreviousTaskToDo();

    List<T> getPool();
}
