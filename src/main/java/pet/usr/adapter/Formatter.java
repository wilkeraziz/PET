/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pet.usr.adapter;

import java.util.Scanner;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 *
 * @author waziz
 */
public class Formatter {

    private static PeriodFormatter secondFormatter = new PeriodFormatterBuilder()
                .appendHours()
                .appendSuffix("h")
//                .appendSeparator(" ")
                .appendMinutes()
                .appendSuffix("m")
//                .appendSeparator(" ")
                .appendSeconds()
                .appendSuffix("s")
                .toFormatter();

    private static PeriodFormatter milliFormatter = new PeriodFormatterBuilder()
                .appendHours()
                .appendSuffix("h")
                .appendMinutes()
                .appendSuffix("m")
                .appendSeconds()
                .appendSuffix("s")
                .appendPrefix(",")
                .appendMillis()
                .toFormatter();

    public static PeriodFormatter getSecondFormatter(){
        return secondFormatter;
    }

    public static PeriodFormatter getMilliFormatter(){
        return milliFormatter;
    }

}

