/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.usr.adapter;

import java.util.List;
import java.util.Map;
import javax.swing.JLabel;
import org.apache.commons.collections.keyvalue.MultiKey;
import pet.annotation.Unit;
import pet.annotation.adapter.StatusAdapter;
import java.util.Iterator;
import pet.config.ContextHandler;
import pet.frontend.components.UnitGUI;

/**
 *
 * @author waziz
 */
public class TaskPrinter {

    private final TaskPool pool;
    private final Map<Integer, MultiKey> alignments;
    private final CurrentSelector currentPrinter;
    private final int currentPointer;
    private final String hide;

    public TaskPrinter(final TaskPool pool,
            final Map<Integer, MultiKey> alignments,
            final CurrentSelector currentPrinter,
            final int currentPointer) {
        this.pool = pool;
        this.alignments = alignments;
        this.currentPrinter = currentPrinter;
        this.currentPointer = currentPointer;
        if (ContextHandler.applyHideIfNotEditingToAll()) {
            this.hide = ContextHandler.hideIfNotEditing();
        } else {
            this.hide = null;
        }

    }

    public void print() {
        final List<Unit> tasks = pool.getPool();
        if (tasks.size() == alignments.size()) {

            final Iterator<Unit> taskIterator = tasks.iterator();

            for (int position = 0; position < alignments.size(); position++) {
                //for (final Map.Entry<Integer,MultiKey> entry : alignments.entrySet()){
                final MultiKey alignment = alignments.get(position);
                //final Integer position = entry.getKey();
                final Unit task = taskIterator.next();

                //final MultiKey alignment = entry.getValue();
                final JLabel id = (JLabel) alignment.getKey(0);
                final UnitGUI src = (UnitGUI) alignment.getKey(1);
                final UnitGUI tgt = (UnitGUI) alignment.getKey(2);

                basicFacade(task, id, src, tgt, position - currentPointer);

            }
            currentPrinter.print(tasks.get(currentPointer));
        } else {
            throw new IllegalStateException("The task pool doesn't fit to the printable area: " + tasks.size() + " instead of " + alignments.size());
        }
    }

    private void basicFacade(final Unit task, final JLabel id, final UnitGUI src, final UnitGUI tgt, final int orientation) {

        if (task == null) {
            id.setText("");
            src.clear();
            tgt.clear();
            if (orientation != 0) {
                src.underlying().setBackground(ContextHandler.standardBackGroundColor());
                tgt.underlying().setBackground(ContextHandler.standardBackGroundColor());
            }
        } else {
            if (hide == null || hide.equals("never") || (hide.equals("undone") && task.getStatus() == StatusAdapter.FINISHED)) {
                id.setText(task.getId());
                src.setSentence(task.getSource());
                tgt.setSentence(task.getTarget());
            } else {
                id.setText("");
                src.clear();
                tgt.clear();
            }
            if (task.getStatus() != StatusAdapter.FINISHED) {
                src.underlying().setBackground(ContextHandler.toDoBackGroundColor());
                tgt.underlying().setBackground(ContextHandler.toDoBackGroundColor());
            } else {
                src.underlying().setBackground(ContextHandler.doneBackGroundColor());
                tgt.underlying().setBackground(ContextHandler.doneBackGroundColor());
            }

        }
    }
}
