/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.signal;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author waziz
 */
public class Timer {

    private final List<Long> readings;

    public Timer() {
        this.readings = new ArrayList<Long>();
        this.readings.add(time());
    }

    public static long time() {
        return System.currentTimeMillis();
    }

    public long last() {
        return readings.get(readings.size() - 1);
    }

    public long totalElapsed(final long t) {
        return t - readings.get(0);
    }

    public long elapsed(final long t) {
        return t - last();
    }

    public long elapsed(final long t, final int n) {
        return t - readings.get(n);
    }

    public long mark() {
        long t = time();
        this.readings.add(t);
        return t;
    }
}
