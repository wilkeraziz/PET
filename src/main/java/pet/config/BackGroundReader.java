/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pet.config;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 * BackGroundReader is a SwingWorker, that is, a worker that runs in a different
 * thread from the main one, so that Swing objects continue responsive
 * while some heavy processing is done in background by the SwingWorker.
 * 
 * @author waziz
 */
public class BackGroundReader extends SwingWorker<Void, Void>{

    /**
     * This is used to let some listener know that the worker has
     * finished its job.
     */
    public interface BackGroundReaderListener{
        void configLoaded(final ConfigReader reader);
    }

    private final BackGroundReaderListener listener;
    
    private final ConfigReader reader;

    /**
     * Constructs a BackGroundReader from
     * @param reader a ConfigReader whose "read" method is to be called in background
     * @param response a listener that must be notified when the task is finished
     */
    public BackGroundReader(final ConfigReader reader, final BackGroundReaderListener listener) {
        this.reader = reader;
        this.listener = listener;
    }

    @Override
    /**
     * This SwingWorker is specialized in reading context files.
     */
    protected Void doInBackground(){
        try{
            reader.read();
        }
        catch (final Exception ex){
            JOptionPane.showMessageDialog(null, ex);
            System.exit(1);
        }
        return null;
    }

    @Override
    /**
     * The listener is notified that the reading was done.
     */
    public void done(){
        listener.configLoaded(reader);
    }
    

}
