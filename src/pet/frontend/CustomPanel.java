/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.frontend;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import javax.swing.text.*;

/**
 *
 * @author Wilker
 */
public class CustomPanel extends JPanel {

    private final JTextPane sentence;
    private final List<String> data;
    private final Color background;
    private final String taggedText;

    public CustomPanel() {
        super(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(""));
        this.data = new ArrayList<String>();
        this.sentence = new JTextPane();
        background = this.getBackground();
        sentence.setBackground(this.getBackground());
        taggedText = new String();
        add(sentence);
        border();
    }

    private void border() {
        this.sentence.setBorder(BorderFactory.createEtchedBorder());
    }

    public void addSentences(final CustomPanel panel) {
        final List<String> extraData = panel.getData();
        for (int i = 0; i < extraData.size(); i++) {
            data.add(extraData.get(i));
        }
    }

    public void addSentences(final List<String> sentences) {
        for (int i = 0; i < sentences.size(); i++) {
            data.add(sentences.get(i));
        }
    }

    public void setText(final String text) {
        this.sentence.setText("");
        setText(sentence, text);
    }

    public String getText() {
        return taggedText;
    }

    @Override
    public String toString() {
        return sentence.getText();
    }

    public void update() {
        setText("");
        if (this.data.size() > 0) {
            setText(this.sentence, this.data.get(0));
        }
        for (int i = 1; i < this.data.size(); i++) {
            newLine(this.sentence);
            setText(this.sentence, this.data.get(i));
        }
    }

    public List<String> getData() {
        return Collections.unmodifiableList(data);
    }

    protected void addStylesToDocument(final StyledDocument doc) {
        //Initialize some styles.
        final Style def = StyleContext.getDefaultStyleContext().
                getStyle(StyleContext.DEFAULT_STYLE);

        final Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");

        Style s = doc.addStyle("entailment", regular);
        StyleConstants.setBold(s, true);
        StyleConstants.setForeground(s, Color.blue);

        s = doc.addStyle("color_blue", regular);
        StyleConstants.setBold(s, true);
        StyleConstants.setForeground(s, Color.BLUE);

        s = doc.addStyle("color_red", regular);
        StyleConstants.setBold(s, true);
        StyleConstants.setForeground(s, Color.RED);

        s = doc.addStyle("color_green", regular);
        StyleConstants.setBold(s, true);
        StyleConstants.setForeground(s, Color.GREEN);

        s = doc.addStyle("color_cyan", regular);
        StyleConstants.setBold(s, true);
        StyleConstants.setForeground(s, Color.CYAN);

        s = doc.addStyle("color_dark_gray", regular);
        StyleConstants.setBold(s, true);
        StyleConstants.setForeground(s, Color.DARK_GRAY);

        s = doc.addStyle("color_pink", regular);
        StyleConstants.setBold(s, true);
        StyleConstants.setForeground(s, Color.PINK);

        s = doc.addStyle("color_yellow", regular);
        StyleConstants.setBold(s, true);
        StyleConstants.setForeground(s, Color.YELLOW);

        s = doc.addStyle("color_magenta", regular);
        StyleConstants.setBold(s, true);
        StyleConstants.setForeground(s, Color.MAGENTA);

        s = doc.addStyle("color_orange", regular);
        StyleConstants.setBold(s, true);
        StyleConstants.setForeground(s, Color.ORANGE);

        s = doc.addStyle("unknown", regular);
        StyleConstants.setBold(s, true);
        StyleConstants.setUnderline(s, true);

        s = doc.addStyle("system", regular);
        StyleConstants.setBold(s, true);
    }

    private void setText(final JTextPane pane, final String sentence) {
        String[] words = sentence.split(" ");
        String[] styles = new String[words.length];
        for (int i = 0; i < words.length; i++) {
            styles[i] = "regular";
            if (words[i].startsWith("[=u=]")) {
                words[i] = words[i].replace("[=u=]", "");
                styles[i] = "unknown";
            } else if (words[i].startsWith("[=e=]")) {
                words[i] = words[i].replace("[=e=]", "");
                styles[i] = "entailment";
            } else if (words[i].startsWith("[=sys=]")) {
                words[i] = words[i].replace("[=sys=]", "");
                styles[i] = "system";
            } else if (words[i].startsWith("[=g=]")) {
                words[i] = words[i].replace("[=g=]", "");
                styles[i] = "color_green";
            } else if (words[i].startsWith("[=p=]")) {
                words[i] = words[i].replace("[=p=]", "");
                styles[i] = "color_pink";
            } else if (words[i].startsWith("[=y=]")) {
                words[i] = words[i].replace("[=y=]", "");
                styles[i] = "color_yellow";
            } else if (words[i].startsWith("[=m=]")) {
                words[i] = words[i].replace("[=m=]", "");
                styles[i] = "color_magenta";
            } else if (words[i].startsWith("[=o=]")) {
                words[i] = words[i].replace("[=o=]", "");
                styles[i] = "color_orange";
            } else if (words[i].startsWith("[=b=]")) {
                words[i] = words[i].replace("[=b=]", "");
                styles[i] = "color_blue";
            } else if (words[i].startsWith("[=r=]")) {
                words[i] = words[i].replace("[=r=]", "");
                styles[i] = "color_red";
            } else if (words[i].startsWith("[=c=]")) {
                words[i] = words[i].replace("[=c=]", "");
                styles[i] = "color_cyan";
            } else if (words[i].startsWith("[=d=]")) {
                words[i] = words[i].replace("[=d=]", "");
                styles[i] = "color_dark_gray";
            }
        }

        final StyledDocument doc = pane.getStyledDocument();
        addStylesToDocument(doc);

        try {
            for (int i = 0; i < words.length; i++) {
                doc.insertString(doc.getLength(), words[i] + " ",
                        doc.getStyle(styles[i]));
            }
        } catch (final BadLocationException e) {
            System.err.println("Couldn't insert initial text into text pane:" + e);
        }
    }

    private void newLine(final JTextPane pane) {
        final StyledDocument doc = pane.getStyledDocument();
        addStylesToDocument(doc);

        try {
            doc.insertString(doc.getLength(), "\n", doc.getStyle("regular"));
        } catch (final BadLocationException e) {
            System.err.println("Couldn't insert initial text into text pane:" + e);
        }
    }

    public StyledDocument getStyledDocument() {
        return sentence.getStyledDocument();
    }

    public void setStyledDocument(StyledDocument doc) {
        sentence.setStyledDocument(doc);
    }

    @Override
    public String getToolTipText() {
        return "";
    }
}
