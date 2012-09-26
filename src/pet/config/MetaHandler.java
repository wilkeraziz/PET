/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.config;

import java.io.File;
import java.util.Scanner;
import pet.usr.adapter.FileAdapter;
import pet.usr.handler.FileHandler;

/**
 *
 * @author waziz
 */
public class MetaHandler {
    
    private final static ThreadLocal<String> pecDir = new ThreadLocal<String>();
    private final static ThreadLocal<String> pecDefault = new ThreadLocal<String>();
    private final static ThreadLocal<String> pecExample = new ThreadLocal<String>();
    
    /**
     * Initializes the thread objects
     * @param pecDir a directory where PET should look for detailed configurations (i.e. *.pec files)
     * @param pecDefault specifies a default configuration file (i.e. a given .pec file)
     * @param pecExample sets the output file that will hold the example .pec file
     */
    private static void initialize(final String pecDir, 
            final String pecDefault,
            final String pecExample){
        release();
        MetaHandler.pecDir.set(pecDir);
        MetaHandler.pecDefault.set(pecDefault);
        MetaHandler.pecExample.set(pecExample);
    }
    
    /**
     * Releases the thread objects
     */
    public static void release(){
        pecDir.remove();
        pecDefault.remove();
        pecExample.remove();
    }
    
    /**
     * Returns the directory where PET should look for .pec files
     * @return 
     */
    public static String pecDir(){
        return pecDir.get();
    }
    
    /**
     * Returns the default .pec file
     * @return 
     */
    public static String pecDefault(){
        return pecDefault.get();
    }
    
    /**
     * Returns the path to the example .pec file
     * @return 
     */
    public static String pecExample(){
        return pecExample.get();
    }
    
    
    /**
     * This is where everything starts for the handler.
     * A Handler is a design pattern for managing "per-thread global" objects.
     * One should always call its MetaHandler#initialize method
     * as well as its MetaHandler#release
     * 
     * @param pecmeta FileAdapter to the pec.meta file
     */
    public static void initialize(final FileAdapter pecmeta) {
        MetaHandler.release();
        final String petDir = System.getProperty("user.dir");
        String dir = petDir;
        String def = null;
        try {
            final Scanner scanner = new Scanner(pecmeta.getFile());
            while (scanner.hasNext()) {
                String line = scanner.nextLine().trim();
                if (line.startsWith("dir=")) {
                    final String strdir = line.substring(line.indexOf('=') + 1);
                    File newdir = new File(strdir);
                    if (!newdir.isAbsolute()){
                        dir = petDir +  File.separator + strdir;
                    } else{
                        dir = strdir;
                    }
                    continue;
                } 
                if (line.startsWith("default=")) {
                    def = line.substring(line.indexOf('=') + 1);
                    continue;
                }
            }
        } catch (final Exception e) {
            System.out.println(pecmeta.getFile().getAbsolutePath() + " was not found, PET will load the first .pec file available");
        }
        final File defFile = new File(dir);
        if (!defFile.exists() || !defFile.getPath().endsWith(FileHandler.CONFIG_SUFIX)){
            def = null;
        }
        final String example = dir + File.separator + "example.pec";
        MetaHandler.initialize(dir, def, example);
    }
    
}
