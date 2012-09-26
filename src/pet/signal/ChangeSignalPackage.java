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
public class ChangeSignalPackage implements SignalPackage {

    public static enum ChangeType {

        INSERTION,
        DELETION,
        ASSIGNMENT;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    };
    private final int offset;
    private final int length;
    private final String change;
    private final DateTime timestamp;
    private final ChangeType type;

    private ChangeSignalPackage(final ChangeType type, final int offset, final int length, final String change, final DateTime timestamp) {
        this.type = type;
        this.offset = offset;
        this.length = length;
        this.change = change;
        this.timestamp = timestamp;
    }

    public static ChangeSignalPackage getInsertion(final int offset, final int length, final String in, final DateTime timestamp) {
        return new ChangeSignalPackage(ChangeType.INSERTION, offset, length, in, timestamp);
    }

    public static ChangeSignalPackage getDeletion(final int offset, final int length, final String out, final DateTime timestamp) {
        return new ChangeSignalPackage(ChangeType.DELETION, offset, length, out, timestamp);
    }

    public static ChangeSignalPackage getAssignment(final int offset, final int length, final String in, final DateTime timestamp) {
        return new ChangeSignalPackage(ChangeType.ASSIGNMENT, offset, length, in, timestamp);
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }

    public String getChange() {
        return change;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public ChangeType getType(){
        return type;
    }

    @Override
    public String toString() {
        return getChange();
    }
}
