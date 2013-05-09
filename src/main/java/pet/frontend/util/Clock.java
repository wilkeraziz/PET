/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pet.frontend.util;

import pet.usr.adapter.Formatter;
import javax.swing.JLabel;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.Period;


/**
 *
 * @author waziz
 */
public class Clock extends Thread{

    private final JLabel clock;
    private long reference;
    private final Integer lock;

    private boolean stop;
    private boolean printing;

    private PeriodFormatter formatter = Formatter.getSecondFormatter();

    public Clock(final JLabel clock){
        this.clock = clock;
        stop = false;
        lock = 0;
        printing = false;
        reference = 0L;
    }

    private void print(){
        long base = 0L;
        synchronized(lock){
            base = reference;
        }
        clock.setText("partial: " + formatter.print(new Period(base,System.currentTimeMillis()).normalizedStandard()));
    }

    @Override
    public void start(){
        updateReference();
        super.start();
    }

    private void updateReference(){
        synchronized(lock){
            reference = System.currentTimeMillis();
        }
    }

    private long getAndUpdateReference(){
        long r = 0L;
        synchronized(lock){
            reference = System.currentTimeMillis();
            r = reference;
        }
        return r;
    }

    public DateTime resetGettingTime(){
        return new DateTime(getAndUpdateReference());
    }

    public Duration resetGettingDelta(){
        final long base = reference;
        return new Duration(base, getAndUpdateReference());
    }

    public void switchOff(){
        stop = true;
    }

    public void printing(final boolean state){
        printing = state;
        if (printing == false){
            clock.setText("");
        }
    }

    @Override
    public void run(){
        while (!stop){
            try{
                if (printing){
                    print();
                }
                sleep(1000L);
            } catch(InterruptedException e){
                System.err.println(e);
            }
        }
    }


}
