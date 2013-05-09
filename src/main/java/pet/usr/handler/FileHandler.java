package pet.usr.handler;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

public class FileHandler {

	public static final String JOB_SUFIX = ".pej";
	public static final String RESULT_SUFIX = ".per";
        public static final String CONFIG_SUFIX = ".pec";

	public static List<File> getJobFiles(final File directory) {
		return Arrays.asList(directory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getPath().endsWith(JOB_SUFIX);
			}
		}));
	}
	
	public static List<File> getResultFiles(final File directory) {
		return Arrays.asList(directory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getPath().endsWith(RESULT_SUFIX);
			}
		}));
	}

        public static List<File> getConfigFiles(final File directory) {
		return Arrays.asList(directory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getPath().endsWith(CONFIG_SUFIX);
			}
		}));
	}
}
