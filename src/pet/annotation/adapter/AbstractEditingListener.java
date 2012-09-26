/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.annotation.adapter;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.joda.time.DateTime;
import pet.annotation.EditingListener;
import pet.config.ContextHandler;
import pet.signal.ChangeSignalPackage;
import pet.signal.PETFlowListener;
import pet.signal.PETTextChangeEvent;
import pet.signal.SignalAdapter;

/**
 *
 * This class makes sure the necessary logging is given a chance to be performed
 * then gives its implementations the chance to act 
 *
 * @author waziz
 */
public abstract class AbstractEditingListener implements DocumentListener, EditingListener {

    private boolean logging = false;
    private StringBuffer buffer = new StringBuffer();


    public AbstractEditingListener(){
        
        ContextHandler.flowManager().addListener(new PETFlowListener() {

            @Override
            public void editingIsAboutToStart() {
            }

            @Override
            public void editingHasStarted() {
                logging = true;
                resetBuffer();
            }

            @Override
            public void editingIsAboutToFinish() {
                logging = false;
            }

            @Override
            public void editingHasFinished() {

            }
        });
    }

    @Override
    public void on() {
        //logging = true;
    }

    @Override
    public void off() {
        //logging = false;
    }
    
    protected void resetBuffer(){
        buffer = new StringBuffer();
    }

    protected void insertToBuffer(final int offset, final String str){
        buffer.insert(offset, str);
    }

    protected void assignBuffer(final String str){
        resetBuffer();
        buffer.append(str);
    }

    protected String removeFromBuffer(final int offset, final int length){
        final int end = offset + length;
        final String out = buffer.substring(offset, end);
        buffer.delete(offset, end);
        return out;
    }

    @Override
    public boolean isLogging() {
        return logging; // && UnitHandler.getEditingStatusProvider().getEditionStatus().equals(EditionStatus.EDITING);
    }

    @Override
    public final void insertUpdate(final DocumentEvent de) {
        if (isLogging()) {
            logInsert(de);
            insert(de);
        }
    }

    @Override
    public final void removeUpdate(final DocumentEvent de) {

        if (isLogging()) {
            logRemove(de);
            remove(de);
        }
    }

    @Override
    public final void changedUpdate(final DocumentEvent de) {
        if (isLogging()) {
            logChange(de);
            change(de);
        }
    }

    private void logInsert(final DocumentEvent event) {
        final Document doc = (Document) event.getDocument();
        try {
            if (event.getLength() > 0) {
                final String in = doc.getText(event.getOffset(), event.getLength());
                ContextHandler.signalManager().fire(new PETTextChangeEvent(event.getOffset(), in, ""));
                //TODO: remove it
                ContextHandler.signalManager().fire(SignalAdapter.TEXT_INSERTION,
                        ChangeSignalPackage.getInsertion(event.getOffset(), event.getLength(), in, new DateTime(System.currentTimeMillis())));
                insertToBuffer(event.getOffset(), in);
            }
        } catch (final BadLocationException e) {
            System.err.println(e);
        }
    }

    private void logRemove(final DocumentEvent event) {
        if (event.getLength() > 0) {
            final String out = removeFromBuffer(event.getOffset(), event.getLength());
            ContextHandler.signalManager().fire(new PETTextChangeEvent(event.getOffset(), "", out));
            //TODO: remove it
            ContextHandler.signalManager().fire(SignalAdapter.TEXT_DELETION,
                    ChangeSignalPackage.getDeletion(event.getOffset(), event.getLength(), out, new DateTime(System.currentTimeMillis())));
        }
    }

    private void logChange(final DocumentEvent event) {
        final Document doc = (Document) event.getDocument();
        try {
            if (event.getLength() > 0) {
                final String change = doc.getText(event.getOffset(), event.getLength());
                ContextHandler.signalManager().fire(new PETTextChangeEvent(event.getOffset(), change, ""));
                //TODO: remove it
                ContextHandler.signalManager().fire(SignalAdapter.TEXT_ASSIGNMENT,
                        ChangeSignalPackage.getAssignment(event.getOffset(), event.getLength(), change, new DateTime(System.currentTimeMillis())));
                assignBuffer(change);
            }
        } catch (final BadLocationException e) {
            System.err.println(e);
        }
    }
}
