/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.signal;

import java.util.ArrayList;
import java.util.List;
import pet.annotation.EffortIndicator;
import pet.annotation.adapter.CountEffortIndicator;

/**
 * This interpreter is responsible for the key-typing events.
 * It generates a handful of indicators, all count indicators.
 * 
 * @author waziz
 */
public class KeystrokeInterpreter implements EventInterpreter {
    
    /**
     * Counts keys by group. 
     * @param events
     * @return 
     */
    @Override
    public List<EffortIndicator> interpret(final List<PETEvent> events) {
        // PETKeystrokeEvent
        int digit = 0;
        int letter = 0;
        int white = 0;
        int symbol = 0;
        // PETNavigationEvent
        int navigation = 0;
        // PETCommandEvent
        int erase = 0;
        int copy = 0;
        int paste = 0;
        int cut = 0;
        int doing = 0;
        // PETEditOperationEvent
        int insert = 0;
        int replace = 0;
        int shift = 0;
        int trim = 0;

        for (final PETEvent e : events) {
            if (e instanceof PETKeystrokeEvent) {
                final PETKeystrokeEvent evt = (PETKeystrokeEvent) e;
                char ch = evt.getChar();
                if (Character.isDigit(ch)) {
                    digit++;
                } else if (Character.isLetter(ch)) {
                    letter++;
                } else if (Character.isWhitespace(ch)) {
                    white++;
                } else {
                    symbol++;
                }
            } else if (e instanceof PETNavigationEvent) {
                navigation++;
            } else if (e instanceof PETCommandEvent) {
                final PETCommandEvent evt = (PETCommandEvent) e;
                if (evt.getCommand() == PETCommandEvent.CommandType.BACKSPACE || evt.getCommand() == PETCommandEvent.CommandType.DELETE) {
                    erase++;
                } else if (evt.getCommand() == PETCommandEvent.CommandType.COPY) {
                    copy++;
                } else if (evt.getCommand() == PETCommandEvent.CommandType.PASTE) {
                    paste++;
                } else if (evt.getCommand() == PETCommandEvent.CommandType.CUT) {
                    cut++;
                } else if (evt.getCommand() == PETCommandEvent.CommandType.UNDO || evt.getCommand() == PETCommandEvent.CommandType.REDO) {
                    doing++;
                } 
                

            }
        }
        final List<EffortIndicator> indicators = new ArrayList<EffortIndicator>();
        indicators.add(new CountEffortIndicator("letter-keys", letter));
        indicators.add(new CountEffortIndicator("digit-keys", digit));
        indicators.add(new CountEffortIndicator("white-keys", white));
        indicators.add(new CountEffortIndicator("symbol-keys", symbol));
        indicators.add(new CountEffortIndicator("navigation-keys", navigation));
        indicators.add(new CountEffortIndicator("erase-keys", erase));
        indicators.add(new CountEffortIndicator("copy-keys", copy));
        indicators.add(new CountEffortIndicator("cut-keys", cut));
        indicators.add(new CountEffortIndicator("paste-keys", paste));
        indicators.add(new CountEffortIndicator("do-keys", doing));
        return indicators;
    }
}
