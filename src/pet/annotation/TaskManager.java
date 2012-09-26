package pet.annotation;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TaskManager {

	List<UnitResult> getResults(final Unit task);

	Map<Unit, List<UnitResult>> getResults();

	Set<Job> getJobs();
	
	String getUser();
	
}
