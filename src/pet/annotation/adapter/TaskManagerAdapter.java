package pet.annotation.adapter;


import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pet.annotation.Job;
import pet.annotation.Unit;
import pet.annotation.TaskManager;
import pet.annotation.UnitResult;
import pet.annotation.xml.PETParseException;
import pet.io.XMLJobReader;

public class TaskManagerAdapter implements TaskManager {

    private final Set<Job> jobs;
    private final Map<Unit, List<UnitResult>> resultsByTask;
    private final String user;

    /**
     * Manages a collection of already loaded jobs
     * @param jobs
     * @param user
     */
    public TaskManagerAdapter(final Collection<Job> jobs, final String user) {
        this.user = user;
        this.jobs = new HashSet<Job>(jobs);
        this.resultsByTask = new HashMap<Unit, List<UnitResult>>();
    }

    /**
     * Load all jobs into memory
     * @param user
     */
    public TaskManagerAdapter(final String user) {
        throw new UnsupportedOperationException();
    }

    /**
     * Loads a specific job
     * @param user
     * @param job
     */
    public TaskManagerAdapter(final String user, final File job) throws PETParseException {
        this.user = user;
        this.jobs = new HashSet<Job>(1);
        this.resultsByTask = new HashMap<Unit, List<UnitResult>>();
        final XMLJobReader reader = new XMLJobReader();
        jobs.add(reader.readJob(job));
    }

    @Override
    public List<UnitResult> getResults(final Unit task) {
        final List<UnitResult> results = resultsByTask.get(task);
        if (results == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(results);
    }

    @Override
    public Map<Unit, List<UnitResult>> getResults() {
        return Collections.unmodifiableMap(resultsByTask);
    }

    @Override
    public Set<Job> getJobs() {
        return Collections.unmodifiableSet(jobs);
    }

    @Override
    public String getUser() {
        return user;
    }
}
