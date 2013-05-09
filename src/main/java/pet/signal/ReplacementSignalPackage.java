/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.signal;

import org.joda.time.DateTime;

/**
 *
 * @author waziz
 */
public class ReplacementSignalPackage implements SignalPackage {

    private final String out;
    private final String in;
    private final DateTime timestamp;


    public ReplacementSignalPackage(final String out, final String in, final DateTime timestamp) {
        this.out = out;
        this.in = in;
        this.timestamp = timestamp;
    }

    public String getOut(){
        return out;
    }

    public String getIn(){
        return in;
    }
    
    public DateTime getTimestamp() {
        return timestamp;
    }


    @Override
    public String toString() {
        return getIn();
    }
}
