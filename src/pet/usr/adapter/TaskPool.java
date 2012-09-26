/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.usr.adapter;

import pet.annotation.Unit;
import pet.annotation.adapter.StatusAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pet.frontend.UnitProvider;

/**
 *
 * @author waziz
 */
public class TaskPool implements RollingPool<Unit>, EditingUnitSelector, UnitProvider {

    private final List<Unit> tasks;
    private final int poolSize;
    private final int startingPosition;
    private final int numberOfTasks;
    private int pointer;

    public TaskPool(final List<? extends Unit> tasks, final int poolSize, final int startingPosition) {
        this.tasks = new ArrayList<Unit>(tasks);
        this.poolSize = poolSize;
        this.startingPosition = startingPosition;
        this.numberOfTasks = this.tasks.size();
        pointer = 0;
    }

    @Override
    public boolean canMoveBackward() {
        if (pointer < 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean canMoveForward() {
        if (pointer >= numberOfTasks) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean moveForward() {
        if (canMoveForward()) {
            pointer++;
            return true;
        } else {
            return false;
        }
    }

    public void findNextTaskToDo() {
        while (moveForward()) {
            final Unit editing = getEditingUnit();
            if (editing != null) {
                if (editing.getStatus() != StatusAdapter.FINISHED) {
                    break;
                }
            } else {
                break;
            }
        }
    }

    public void findPreviousTaskToDo() {
        while (moveBackward()) {
            final Unit editing = getEditingUnit();
            if (editing != null) {
                if (editing.getStatus() != StatusAdapter.FINISHED) {
                    break;
                }
            } else {
                break;
            }
        }
    }

    @Override
    public boolean moveBackward() {
        if (canMoveBackward()) {
            pointer--;
            return true;
        } else {
            return false;
        }
    }

    private Unit unitAt(int index) {
        if (index >= 0 && index < numberOfTasks) {
            return tasks.get(index);
        } else {
            return null;
        }
    }

    @Override
    public List<Unit> getPool() {
        final List<Unit> tasksInPool = new ArrayList<Unit>(poolSize);
        for (int i = startingPosition; i >= 0; i--) {
            tasksInPool.add(unitAt(pointer - i));
        }
        for (int i = 1; i < poolSize - startingPosition; i++) {
            tasksInPool.add(unitAt(pointer + i));
        }
        return Collections.unmodifiableList(tasksInPool);
    }

    @Override
    public String toString() {
        return numberOfTasks + " displayed using " + poolSize + " units, currently at " + pointer;
    }

    @Override
    public Unit getEditingUnit() {
        if (pointer >= 0 && pointer < numberOfTasks) {
            return tasks.get(pointer);
        } else {
            return null;
        }

    }

    @Override
    public Unit getCurrentTask() {
        return getEditingUnit();
    }

    @Override
    public List<Unit> getUnits() {
        return Collections.unmodifiableList(tasks);
    }
}
