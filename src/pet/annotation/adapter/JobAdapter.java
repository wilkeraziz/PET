package pet.annotation.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import pet.annotation.Job;
import pet.annotation.Status;
import pet.annotation.Unit;
import pet.annotation.UnitResult;

public class JobAdapter implements Job {

    private final String id;
    private final Status status;
    private final List<Unit> tasks;
    private final Map<Unit,List<UnitResult>> resultsByTask;

    public JobAdapter(final String id,
            final Status status,
            final List<Unit> tasks) {
        assert !tasks.isEmpty() : "A job requires at least one task";
        this.status = status;
        this.tasks = new ArrayList<Unit>(tasks);
        this.resultsByTask = new HashMap<Unit, List<UnitResult>>(tasks.size());
        this.id = id;
    }
    
    public JobAdapter(final String id,
            final Status status,
            final List<Unit> tasks,
            final Map<Unit,List<UnitResult>> resultsByTask) {
        assert !tasks.isEmpty() : "A job requires at least one task";
        this.status = status;
        this.tasks = new ArrayList<Unit>(tasks);
        this.resultsByTask = new HashMap<Unit, List<UnitResult>>(resultsByTask);
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public List<Unit> getUnits() {
        return Collections.unmodifiableList(tasks);
    }
    
    @Override
    public List<UnitResult> getUnitResults(final Unit task){
        return resultsByTask.get(task);
    }
    
    @Override
    public int getProgress(){
        int n = 0;
        for (final Unit task : tasks){
            final List<UnitResult> results = resultsByTask.get(task);
            if (results != null && results.size() > 0){
                n++;
            }
        }
        return n;
    }
    
    @Override
    public void addResultToUnit(final Unit task, final UnitResult result){
        List<UnitResult> results = getUnitResults(task);
        if (results == null){
            results = new ArrayList<UnitResult>();
            this.resultsByTask.put(task, results);
        }
        results.add(result);
    }

    @Override
    public String toString() {
        String result = "jobid=" + id + " status=" + status;
        for (final Unit t : tasks) {
            result += "\n" + t.toString();
        }
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof JobAdapter)) {
            return false;
        }
        final JobAdapter other = (JobAdapter) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
