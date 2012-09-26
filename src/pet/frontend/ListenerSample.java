/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.frontend;

/**
 *
 * @author waziz
 */
import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.StyledDocument;

public class ListenerSample {

    public static void main(String args[]) {
        JFrame frame = new JFrame("Offset Example");
        Container content = frame.getContentPane();
        JTextPane textArea = new JTextPane();
        final DefaultStyledDocument doc = new DefaultStyledDocument();
        textArea.setDocument(doc);
        

        JScrollPane scrollPane = new JScrollPane(textArea);
        final Document document = textArea.getDocument();

        document.addDocumentListener(new MyListener());

        content.add(scrollPane, BorderLayout.CENTER);
        frame.setSize(250, 150);
        frame.setVisible(true);
    }
}

class MyListener implements DocumentListener {

    public void changedUpdate(DocumentEvent documentEvent) {
        printInfo(documentEvent);
    }

    public void insertUpdate(DocumentEvent documentEvent) {
        printInfo(documentEvent);
    }

    public void removeUpdate(DocumentEvent documentEvent) {
        printInfo(documentEvent);
    }

    public void printInfo(DocumentEvent de) {
        ElementIterator iter = new ElementIterator(de.getDocument());

        for (Element elem = iter.first(); elem != null; elem = iter.next()) {
            
            DocumentEvent.ElementChange change = de.getChange(elem);
            if (change != null) { // null means there was no change in elem
                System.out.println("Element " + elem.getName() + " (depth "
                        + iter.depth() + ") changed its children: "
                        + change.getChildrenRemoved().length
                        + " children removed, "
                        + change.getChildrenAdded().length
                        + " children added.\n");
            }
        }
    }
}

    
