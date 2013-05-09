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
public class DictionarySignalPackage implements SignalPackage {

    public static enum DictionaryOperation {

        LOOKUP,
        REPLACEMENT;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }
    
    private final int offset;
    private final String out;
    private final String in;
    private final String dict;
    private final DateTime timestamp;
    private final DictionaryOperation operation;

    public DictionarySignalPackage(final DictionaryOperation operation, final int offset, final String out, final String in, final DateTime timestamp, final String dict) {
        this.offset = offset;
        this.out = out;
        this.in = in;
        this.timestamp = timestamp;
        this.operation = operation;
        this.dict = dict;
    }

    public static DictionarySignalPackage getReplacement(final int offset, final String out, final String in, final DateTime timestamp, final String dict) {
        return new DictionarySignalPackage(DictionaryOperation.REPLACEMENT, offset, out, in, timestamp, dict);
    }

    public int getOffset(){
        return offset;
    }
    
    public String getOut() {
        return out;
    }

    public String getIn() {
        return in;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public DictionaryOperation getOperation() {
        return operation;
    }

    @Override
    public String toString() {
        return getOut() + " -- " + getIn();
    }
    
    public String getDictionary(){
        return dict;
    }
}
