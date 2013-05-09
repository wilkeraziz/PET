package pet.io;

import java.io.File;
import java.util.List;

import pet.annotation.Job;
import pet.annotation.Unit;

public interface JobReader {

	Job readJob(final File job);
	
	Job readEditedJob(final File job, final List<Unit> editable);

}
