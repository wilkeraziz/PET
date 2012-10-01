/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.usr.adapter;

import pet.annotation.adapter.StatusAdapter;
import java.awt.Color;
import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import org.joda.time.DateTime;
import org.joda.time.Period;
import pet.annotation.AssessmentChoice;
import pet.annotation.Segment;
import pet.annotation.Status;
import pet.annotation.Unit;
import pet.frontend.util.Clock;
import pet.config.ContextHandler;
import pet.frontend.EditingStatusProvider;
import pet.frontend.FacadeController;
import pet.frontend.components.UnitGUI;
import pet.signal.PETFlowEvent;
import pet.signal.SignalAdapter;

/**
 *
 * @author waziz
 */
public class EditionStatusController implements EditingStatusProvider {

    private final JLabel lblStatus;
    private final JLabel lblTime;
    private final JLabel lblEditionNumber;
    private final JLabel lblProgress;
    private final JLabel lblLeft;
    private final JLabel editingId;
    private final JLabel currentId;
    private final UnitGUI editingSrc;
    private final UnitGUI editingTgt;
    private final UnitGUI currentSentence;
    private final JEditorPane currentExternalSourceInfo;
    private final JEditorPane currentExternalTargetInfo;
    private final JLabel currentGeneralInfo;
    private final EditingUnitSelector taskSelector;
    private EditionStatus status;
    private StatusAdapter jobStatus;
    private Period jobEditingTime;
    private Period jobAssessingTime;
    private int totalTasks;
    private final boolean hideIfNotEditing;
    private final boolean alwaysHideIfNotEditing;
    private final Clock clock;
    private final Set<EditableUnit> done;
    private final List<FacadeController> facadeControllers;

    public EditionStatusController(final JLabel lblStatus,
            final JLabel lblTime,
            final JLabel lblEditionNumber,
            final JLabel lblProgress,
            final JLabel lblLeft,
            final JLabel editingId,
            final UnitGUI editingSrc,
            final UnitGUI editingTgt,
            final JLabel currentGeneralInfo,
            final JLabel currentId,
            final UnitGUI currentSentence,
            final EditingUnitSelector taskSelector,
            final int totalTasks,
            final JEditorPane currentExternalSourceInfo,
            final JEditorPane currentExternalTargetInfo) {

        if (ContextHandler.hideIfNotEditing().isEmpty()
                || ContextHandler.hideIfNotEditing().equalsIgnoreCase("never")) {
            this.hideIfNotEditing = false;
            this.alwaysHideIfNotEditing = false;
        } else {
            this.hideIfNotEditing = true;
            this.alwaysHideIfNotEditing = ContextHandler.hideIfNotEditing().equalsIgnoreCase("always");
        }
        this.lblLeft = lblLeft;
        this.lblStatus = lblStatus;
        this.lblTime = lblTime;
        this.lblEditionNumber = lblEditionNumber;
        this.lblProgress = lblProgress;
        this.editingId = editingId;
        this.editingSrc = editingSrc;
        this.editingTgt = editingTgt;
        this.currentGeneralInfo = currentGeneralInfo;
        this.currentId = currentId;
        this.currentSentence = currentSentence;
        this.taskSelector = taskSelector;
        this.totalTasks = totalTasks;
        this.currentExternalSourceInfo = currentExternalSourceInfo;
        this.currentExternalTargetInfo = currentExternalTargetInfo;
        setStatus(EditionStatus.UNDEFINED);

        this.clock = new Clock(this.lblTime);
        this.clock.start();
        this.done = new HashSet<EditableUnit>();

        this.jobEditingTime = new Period(0L);
        this.jobAssessingTime = new Period(0L);
        for (final pet.annotation.Unit task : taskSelector.getUnits()) {
            if (task.getStatus() == StatusAdapter.FINISHED) {
                done.add((EditableUnit) task);
            }
        }
        updateJobStatus();

        facadeControllers = new ArrayList<FacadeController>();
    }

    private void updateJobStatus() {
        if (done.size() == totalTasks) {
            this.jobStatus = StatusAdapter.FINISHED;
            this.lblProgress.setForeground(Color.blue);
        } else if (done.isEmpty()) {
            this.jobStatus = StatusAdapter.NEVER_STARTED;
            this.lblProgress.setForeground(Color.red);
        } else {
            this.jobStatus = StatusAdapter.GOING_ON;
            this.lblProgress.setForeground(Color.red);
        }
        lblProgress.setText(done.size() + "/" + totalTasks);
    }

    public void update() {
        final EditableUnit task = (EditableUnit) taskSelector.getEditingUnit();
        if (task != null) {
            readToEdit();
            lblEditionNumber.setText("revisions: " + Integer.toString(task.getUnitResults().size()));
        } else {
            undefined();
        }
    }

    public boolean canMakeEditable() {
        final EditableUnit task = (EditableUnit) taskSelector.getEditingUnit();
        return task != null;
    }

    public void undefined() {
        setStatus(EditionStatus.UNDEFINED);
        updateCurrentFacade(false);
    }

    public void readToEdit() {
        if (taskSelector.getEditingUnit() != null) {
            setStatus(EditionStatus.READY_TO_EDIT);
            updateCurrentFacade(false);
        }
    }

    public void acceptMT(final DateTime beforeAssessing, final DateTime afterAssessing, final List<AssessmentChoice> assessments) {
        final EditableUnit task = (EditableUnit) taskSelector.getEditingUnit();
        if (task != null) {
            if (task.getStatus() != StatusAdapter.STARTED) {
                editing();
            }
            ContextHandler.signalManager().fire(SignalAdapter.AUTO_ACCEPT);
            done(task.getOriginalTarget().toString(), assessments);
        }
    }

    public void editing() {
        final EditableUnit unit = (EditableUnit) taskSelector.getEditingUnit();
        if (unit != null) {
            
            updateCurrentFacade(true);
            setStatus(EditionStatus.EDITING);
            
            //unit.activate();
            clock.printing(true);
            clock.resetGettingTime();
            unit.activate();
            //updateCurrentFacade(true);
            //ContextHandler.signalManager().fire(SignalAdapter.EDITING_START);
            ContextHandler.signalManager().fire(new PETFlowEvent(PETFlowEvent.ActionType.EDITING_START));
            
        }
    }

    public void done(final String translation,
            final List<AssessmentChoice> assessments) {
        
        clock.printing(false);
        clock.resetGettingTime();
        final EditableUnit unit = (EditableUnit) taskSelector.getEditingUnit();
        unit.deactivate();
        if (unit.changeSentence(translation, assessments)) {
            done.add(unit);
            jobEditingTime = jobEditingTime.plus(unit.getEditingTime());
            jobAssessingTime = jobAssessingTime.plus(unit.getAssessingTime());
            updateJobStatus();
            setStatus(EditionStatus.DONE);
        }
    }

    public int getDone() {
        return done.size();
    }

    @Override
    public EditionStatus getEditionStatus() {
        return status;
    }

    private void updateCurrentFacade(boolean editing) {
        if (editing) {
            prepareEditing((EditableUnit) taskSelector.getEditingUnit());
        } else {
            // background and font for editable status
            currentSentence.underlying().setBackground(ContextHandler.editableBackGroundColor());
            final JTextPane src = editingSrc.underlying();
            final JTextPane tgt = editingTgt.underlying();
            src.setBackground(ContextHandler.editableBackGroundColor());
            tgt.setBackground(ContextHandler.editableBackGroundColor());
            src.setForeground(Color.BLACK);
            tgt.setForeground(Color.BLACK);
            src.setFont(ContextHandler.editableFont());
            tgt.setFont(ContextHandler.editableFont());
            final EditableUnit task = (EditableUnit) taskSelector.getEditingUnit();
            if (task != null) {
                final String hide = ContextHandler.hideIfNotEditing();
                if (hide.equals("never")) {
                    prepareEditable((EditableUnit) taskSelector.getEditingUnit(), task.getStatus());
                } else if (hide.equals("always")) {
                    clearFacade(task);
                } else if (hide.equals("undone") && task.getStatus().equals(StatusAdapter.FINISHED)) {
                    prepareEditable((EditableUnit) taskSelector.getEditingUnit(), task.getStatus());
                } else {
                    clearFacade(task);
                }
            }
        }
        for (final FacadeController controller : facadeControllers) {
            controller.updateFacade();
        }
    }

    private void clearFacade(final Unit task) {
        currentSentence.clear();
        editingSrc.clear();
        updateGeneralInfoFacade("");
        currentExternalSourceInfo.setText("");
        currentExternalTargetInfo.setText("");
        lblLeft.setText("");
        if (task.getStatus().equals(StatusAdapter.FINISHED)) {
            editingTgt.setSentence(ContextHandler.editableMessageDone());
        } else {
            editingTgt.setSentence(ContextHandler.editableMessageUndone());
        }
    }

    private void prepareEditing(final EditableUnit task) {
        currentSentence.underlying().setBackground(ContextHandler.editingBackGroundColor());
        editingSrc.underlying().setBackground(ContextHandler.editingBackGroundColor());
        editingTgt.underlying().setBackground(ContextHandler.editingBackGroundColor());
        currentSentence.underlying().setFont(ContextHandler.editingFont());
        editingSrc.underlying().setFont(ContextHandler.editingFont());
        editingTgt.underlying().setFont(ContextHandler.editingFont());

        if (task != null) {
            currentSentence.setSentence(getSentenceToDisplayOnTop(task));
            editingSrc.setSentence(task.getSource());
            editingTgt.setSentence(task.getTarget());
            updateGeneralInfoFacade(ContextHandler.infoPrinter().getGeneralInfo(task));
            currentExternalSourceInfo.setText(ContextHandler.infoPrinter().getExternalSourceInfo(task));
            currentExternalTargetInfo.setText(ContextHandler.infoPrinter().getExternalTargetInfo(task));
        }
    }

    private void prepareEditable(final EditableUnit task, final Status status) {
        if (status.equals(StatusAdapter.FINISHED)) {
            currentSentence.underlying().setBackground(ContextHandler.editableBackGroundColor());
            editingSrc.underlying().setBackground(ContextHandler.doneBackGroundColor());
            editingTgt.underlying().setBackground(ContextHandler.doneBackGroundColor());
        } else {
            currentSentence.underlying().setBackground(ContextHandler.editableBackGroundColor());
            editingSrc.underlying().setBackground(ContextHandler.toDoBackGroundColor());
            editingTgt.underlying().setBackground(ContextHandler.toDoBackGroundColor());
        }
        currentSentence.underlying().setFont(ContextHandler.editableFont());
        editingSrc.underlying().setFont(ContextHandler.editableFont());
        editingTgt.underlying().setFont(ContextHandler.editableFont());
        if (task != null) {
            currentSentence.setSentence(getSentenceToDisplayOnTop(task));
            editingSrc.setSentence(task.getSource());
            editingTgt.setSentence(task.getTarget());
            updateGeneralInfoFacade(ContextHandler.infoPrinter().getGeneralInfo(task));
            currentExternalSourceInfo.setText(ContextHandler.infoPrinter().getExternalSourceInfo(task));
            currentExternalTargetInfo.setText(ContextHandler.infoPrinter().getExternalTargetInfo(task));
        }
    }

    private void updateGeneralInfoFacade(final String text) {
        currentGeneralInfo.setText(text);
        //currentGeneralInfo.validate();
    }

    private Segment getSentenceToDisplayOnTop(final EditableUnit task) {
        if (ContextHandler.showReference()) {
            return task.getReference();
        } else {
            if (task.getOriginalUnit() instanceof pet.annotation.adapter.TranslationUnit) {
                return task.getSource();
            } else {
                return task.getOriginalUnit().getTarget();
            }
        }
    }

    private void setStatus(final EditionStatus s) {
        status = s;
        if (s.equals(EditionStatus.READY_TO_EDIT)) {
            lblStatus.setForeground(Color.BLUE);
        } else if (s.equals(EditionStatus.EDITING)) {
            lblStatus.setForeground(Color.RED);
        } else {
            lblStatus.setForeground(Color.BLACK);
        }
        lblStatus.setText(status.toString() + "   ");

    }

    public void discardCurrentMT(final List<AssessmentChoice> assessments) {
        final EditableUnit task = (EditableUnit) taskSelector.getEditingUnit();
        if (task != null) {
            if (task.getStatus() != StatusAdapter.STARTED) {
                editing();
            }
            ContextHandler.signalManager().fire(SignalAdapter.IMPOSSIBLE);
            done(task.getOriginalTarget().toString(), assessments);
        }
    }

    //TODO
    @Deprecated
    public boolean wasCurrentMTEverDiscarded() {
        final EditableUnit task = getEditingTask();
        if (task != null) {
            return false; //task.wasMTEverDiscarded();
        } else {
            return false;
        }
    }

    /*
    @Deprecated
    public void tagCurrentTaskAsImpossible() {
        final EditableUnit task = (EditableUnit) taskSelector.getEditingUnit();
        if (task != null) {
            final DateTime time = new DateTime(System.currentTimeMillis());
            if (task.getStatus() != StatusAdapter.STARTED) {
                ContextHandler.signalManager().fire(SignalAdapter.EDITING_START);
                ContextHandler.signalManager().fire(new PETFlowEvent(PETFlowEvent.ActionType.EDITING_START));
                //task.startEdition(time);
            }
            // task.tagAsImpossible();
            //done(task.getOriginalTarget().toString(), new ArrayList<String>(), "");
        }
    }*/

    public EditableUnit getEditingTask() {
        return (EditableUnit) taskSelector.getEditingUnit();
    }

    public StatusAdapter getJobStatus() {
        return jobStatus;
    }

    public Period getJobEditingTime() {
        return jobEditingTime;
    }

    public Period getJobAssessingTime() {
        return jobAssessingTime;
    }

    public void registerFacadeController(final FacadeController controller) {
        facadeControllers.add(controller);
    }
}
