/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.frontend.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextPane;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author waziz
 */
public class VisualLogger {

    private final static String separator = System.getProperty("line.separator");
    private final static List<StyledDocument> logDoc = new ArrayList<StyledDocument>();
    private final static Color GREEN = new Color(0, 153, 0);

    public static void initialize(final JTextPane area) {
        release();
        final DefaultCaret caret = (DefaultCaret)area.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        VisualLogger.logDoc.add(area.getStyledDocument());
    }

    public static void release() {
        logDoc.clear();
    }


    public static void line() {
        log("\n------------------------------------------------------------------------------------------------------------------------\n", Color.DARK_GRAY, false, false);
    }

    public static void warn(final String message) {
        log(message, Color.RED, false, true);
    }

    public static void error(final String message) {
        log(message, Color.RED, true, false);
    }

    public static void info(final String message) {
        log(message, Color.BLUE, false, false);
    }

    public static void processing(final String message) {
        log(message, GREEN, true, true);
    }

    public static void done() {
        log("\t[done]", GREEN, true, false);
    }

    public static void done(final String message) {
        log(message, Color.BLUE, true, false);
    }

    public static void log(final String message){
        log(message, Color.BLACK, false, false);
    }

    public static void log(final String message, final Color color, final boolean bold, final boolean italic) {
        final StyledDocument doc = doc();

        //  Define a keyword attribute
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        StyleConstants.setForeground(keyWord, color);
        StyleConstants.setBold(keyWord, bold);
        StyleConstants.setItalic(keyWord, italic);
        //  Add some text
        try {
            doc.insertString(doc.getLength(), separator + message, keyWord);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static StyledDocument doc() {
        return logDoc.get(0);
    }
}
