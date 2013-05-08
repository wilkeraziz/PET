/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.config;

import java.awt.Color;
import java.awt.Font;
import java.util.List;
import java.util.Map;
import javax.swing.undo.UndoManager;
import pet.annotation.AssessmentDescriptor;
import pet.annotation.InfoPrinter;
import pet.annotation.Segment;
import pet.annotation.adapter.DefaultInfoPrinter;
import pet.db.ExternalInfoParams;
import pet.db.PETDataBase;
import pet.frontend.components.EditableUnitGUI;
import pet.signal.DefaultSignalManager;
import pet.signal.PETFlowListener;
import pet.signal.PETFlowManager;
import pet.signal.SignalManager;

/**
 * @class ContextHandler This context handler centralizes the environment
 * settings basically everything that can be set using the config file
 *
 * @author waziz
 */
public class ContextHandler {

    private final static ThreadLocal<String> contextId = new ThreadLocal<String>();
    private final static ThreadLocal<String> defaultUser = new ThreadLocal<String>();
    private final static ThreadLocal<Color> standardBackGroundColor = new ThreadLocal<Color>();
    private final static ThreadLocal<Color> doneBackGroundColor = new ThreadLocal<Color>();
    private final static ThreadLocal<Color> toDoBackGroundColor = new ThreadLocal<Color>();
    private final static ThreadLocal<Color> editableBackGroundColor = new ThreadLocal<Color>();
    private final static ThreadLocal<Font> idFont = new ThreadLocal<Font>();
    private final static ThreadLocal<Font> standardFont = new ThreadLocal<Font>();
    private final static ThreadLocal<Font> editableFont = new ThreadLocal<Font>();
    private final static ThreadLocal<Color> editingBackGroundColor = new ThreadLocal<Color>();
    private final static ThreadLocal<Font> editingFont = new ThreadLocal<Font>();
    private final static ThreadLocal<Font> generalInfoFont = new ThreadLocal<Font>();
    private final static ThreadLocal<Segment> editableMessageUndone = new ThreadLocal<Segment>();
    private final static ThreadLocal<Segment> editableMessageDone = new ThreadLocal<Segment>();
    private final static ThreadLocal<String> hideIfNotEditing = new ThreadLocal<String>();
    private final static ThreadLocal<Boolean> showSentenceId = new ThreadLocal<Boolean>();
    private final static ThreadLocal<EditableUnitGUI.Tip> showProducer = new ThreadLocal<EditableUnitGUI.Tip>();
    private final static ThreadLocal<Boolean> assessing = new ThreadLocal<Boolean>();
    private final static ThreadLocal<Integer> assessmentsByPage = new ThreadLocal<Integer>();
    private final static ThreadLocal<Boolean> showReference = new ThreadLocal<Boolean>();
    private final static ThreadLocal<Boolean> autoSave = new ThreadLocal<Boolean>();
    private final static ThreadLocal<Integer> autoSaveMemory = new ThreadLocal<Integer>();
    private final static ThreadLocal<String> workspace = new ThreadLocal<String>();
    private final static ThreadLocal<String> source = new ThreadLocal<String>();
    private final static ThreadLocal<String> target = new ThreadLocal<String>();
    private final static ThreadLocal<Boolean> showOutputTimeStampCheckBox = new ThreadLocal<Boolean>();
    private final static ThreadLocal<Boolean> outputTimeStamp = new ThreadLocal<Boolean>();
    private final static ThreadLocal<String> assessmentSeparator = new ThreadLocal<String>();
    private final static ThreadLocal<List<AssessmentDescriptor>> peAssessments = new ThreadLocal<List<AssessmentDescriptor>>();
    private final static ThreadLocal<List<AssessmentDescriptor>> htAssessments = new ThreadLocal<List<AssessmentDescriptor>>();
    private final static ThreadLocal<SignalManager> signalManager = new ThreadLocal<SignalManager>();
    private final static ThreadLocal<PETFlowManager> flowManager = new ThreadLocal<PETFlowManager>();
    private final static ThreadLocal<Boolean> autoAccept = new ThreadLocal<Boolean>();
    private final static ThreadLocal<Boolean> keystrokes = new ThreadLocal<Boolean>();
    private final static ThreadLocal<Boolean> impossible = new ThreadLocal<Boolean>();
    private final static ThreadLocal<Boolean> unchanged = new ThreadLocal<Boolean>();
    private final static ThreadLocal<Boolean> unnecessary = new ThreadLocal<Boolean>();
    private final static ThreadLocal<Boolean> logChanges = new ThreadLocal<Boolean>();
    private final static ThreadLocal<InfoPrinter> infoPrinter = new ThreadLocal<InfoPrinter>();
    private final static ThreadLocal<Map<String, String>> generalInfo = new ThreadLocal<Map<String, String>>();
    private final static ThreadLocal<Map<String, String>> lengthConstraints = new ThreadLocal<Map<String, String>>();
    private final static ThreadLocal<List<PETDataBase>> externalTargetInfo = new ThreadLocal<List<PETDataBase>>();
    private final static ThreadLocal<List<PETDataBase>> externalSourceInfo = new ThreadLocal<List<PETDataBase>>();
    private final static ThreadLocal<ExternalInfoParams> externalInfoParams = new ThreadLocal<ExternalInfoParams>();
    private final static ThreadLocal<Integer> sentencesByPage = new ThreadLocal<Integer>();
    private final static ThreadLocal<Integer> editablePosition = new ThreadLocal<Integer>();
    private final static ThreadLocal<UndoManager> editingUndoManager = new ThreadLocal<UndoManager>();
    private final static ThreadLocal<Boolean> skipAssessmentOnAutoAccept = new ThreadLocal<Boolean>();
    private final static ThreadLocal<Boolean> skipAssessmentOnDiscard = new ThreadLocal<Boolean>();
    private final static ThreadLocal<Boolean> blockEditing = new ThreadLocal<Boolean>();
    private final static ThreadLocal<Boolean> disableCommentOnAssessment = new ThreadLocal<Boolean>();
    private final static ThreadLocal<Boolean> renderHTML = new ThreadLocal<Boolean>();
    private final static ThreadLocal<Boolean> showBottomPane = new ThreadLocal<Boolean>();
    private final static ThreadLocal<Boolean> showTopPane = new ThreadLocal<Boolean>();
    private final static ThreadLocal<Boolean> blindPE = new ThreadLocal<Boolean>();
    private final static ThreadLocal<Boolean> displayST = new ThreadLocal<Boolean>();
    private final static ThreadLocal<Boolean> applyHideIfNotEditingToAll = new ThreadLocal<Boolean>();
    private final static ThreadLocal<List<PETDataBase>> s2s = new ThreadLocal<List<PETDataBase>>();
    private final static ThreadLocal<List<PETDataBase>> s2t = new ThreadLocal<List<PETDataBase>>();
    private final static ThreadLocal<List<PETDataBase>> t2t = new ThreadLocal<List<PETDataBase>>();
    private final static ThreadLocal<List<PETDataBase>> t2s = new ThreadLocal<List<PETDataBase>>();
    private final static ThreadLocal<Boolean> showMTPreview = new ThreadLocal<Boolean>();
    private final static ThreadLocal<Boolean> hideLeftBar = new ThreadLocal<Boolean>();
    private final static ThreadLocal<Boolean> maximiseAssessmentPage = new ThreadLocal<Boolean>();

    public static void initialize(final String id,
            final String workspace,
            final String source,
            final String target,
            final String defaultUser,
            final Color standardBackGroundColor,
            final Color doneBackGroundColor,
            final Color toDoBackGroundColor,
            final Color editableBackGroundColor,
            final Color editingBackGroundColor,
            final Font standardFont,
            final Font editableFont,
            final Font editingFont,
            final Font idFont,
            final Font generalInfoFont,
            final Segment editableMessageUndone,
            final Segment editableMessageDone,
            final String hideIfNotEditing,
            final boolean applyHideIfNotEditingToAll,
            final boolean showSentenceId,
            final EditableUnitGUI.Tip showProducer,
            final boolean assessing,
            final boolean autoSave,
            final int autoSaveMemory,
            final List<AssessmentDescriptor> peAssessments,
            final List<AssessmentDescriptor> htAssessments,
            int assessmentsByPage,
            final Map<String, String> generalInfo,
            final boolean showOutputTimeStampCheckBox,
            final boolean outputTimeStamp,
            final String assessmentSeparator,
            final boolean autoAccept,
            final boolean showReference,
            final boolean keystrokes,
            final boolean impossible,
            final boolean unchanged,
            final boolean unnecessary,
            final boolean logChanges,
            final List<PETDataBase> externalSourceInfo,
            final List<PETDataBase> externalTargetInfo,
            final Map<String, String> lengthConstraints,
            final ExternalInfoParams externalInforParams,
            final int sentencesByPage,
            final int editablePosition,
            final boolean skipAssessmentOnAutoAccept,
            final boolean skipAssessmentOnDiscard,
            final boolean blockEditing,
            final boolean disableCommentOnAssessment,
            final boolean renderHTML,
            final boolean showTopPane,
            final boolean showBottomPane,
            final boolean blindPE,
            final boolean displayST,
            final List<PETDataBase> s2s,
            final List<PETDataBase> s2t,
            final List<PETDataBase> t2t,
            final List<PETDataBase> t2s,
            final boolean showMTPreview,
            final boolean hideLeftBar,
            final boolean maximiseAssessmentPage) {
        //release();

        ContextHandler.contextId.set(id);
        ContextHandler.workspace.set(workspace);
        ContextHandler.source.set(source);
        ContextHandler.target.set(target);
        ContextHandler.defaultUser.set(defaultUser);
        ContextHandler.standardBackGroundColor.set(standardBackGroundColor);
        ContextHandler.doneBackGroundColor.set(doneBackGroundColor);
        ContextHandler.toDoBackGroundColor.set(toDoBackGroundColor);
        ContextHandler.editableBackGroundColor.set(editableBackGroundColor);
        ContextHandler.editingBackGroundColor.set(editingBackGroundColor);
        ContextHandler.standardFont.set(standardFont);
        ContextHandler.editableFont.set(editableFont);
        ContextHandler.editingFont.set(editingFont);
        ContextHandler.idFont.set(idFont);
        ContextHandler.generalInfoFont.set(generalInfoFont);
        ContextHandler.editableMessageUndone.set(editableMessageUndone);
        ContextHandler.editableMessageDone.set(editableMessageDone);
        ContextHandler.hideIfNotEditing.set(hideIfNotEditing);
        ContextHandler.applyHideIfNotEditingToAll.set(applyHideIfNotEditingToAll);
        ContextHandler.showSentenceId.set(showSentenceId);
        ContextHandler.showProducer.set(showProducer);
        ContextHandler.assessing.set(assessing);
        ContextHandler.autoSave.set(autoSave);
        ContextHandler.autoSaveMemory.set(autoSaveMemory);
        ContextHandler.peAssessments.set(peAssessments);
        ContextHandler.htAssessments.set(htAssessments);
        ContextHandler.assessmentsByPage.set(assessmentsByPage);
        ContextHandler.showOutputTimeStampCheckBox.set(showOutputTimeStampCheckBox);
        ContextHandler.outputTimeStamp.set(outputTimeStamp);
        ContextHandler.assessmentSeparator.set(assessmentSeparator);
        ContextHandler.autoAccept.set(autoAccept);
        ContextHandler.showReference.set(showReference);
        ContextHandler.signalManager.set(new DefaultSignalManager());
        ContextHandler.flowManager.set(new PETFlowManager());
        ContextHandler.keystrokes.set(keystrokes);
        ContextHandler.impossible.set(impossible);
        ContextHandler.unchanged.set(unchanged);
        ContextHandler.unnecessary.set(unnecessary);
        ContextHandler.logChanges.set(logChanges);
        ContextHandler.infoPrinter.set(new DefaultInfoPrinter());
        ContextHandler.generalInfo.set(generalInfo);
        ContextHandler.externalSourceInfo.set(externalSourceInfo);
        ContextHandler.externalTargetInfo.set(externalTargetInfo);
        ContextHandler.lengthConstraints.set(lengthConstraints);
        ContextHandler.externalInfoParams.set(externalInforParams);
        ContextHandler.sentencesByPage.set(sentencesByPage);
        ContextHandler.editablePosition.set(editablePosition);
        ContextHandler.editingUndoManager.set(new UndoManager());
        ContextHandler.skipAssessmentOnAutoAccept.set(skipAssessmentOnAutoAccept);
        ContextHandler.skipAssessmentOnDiscard.set(skipAssessmentOnDiscard);
        ContextHandler.blockEditing.set(blockEditing);
        ContextHandler.disableCommentOnAssessment.set(disableCommentOnAssessment);
        ContextHandler.renderHTML.set(renderHTML);
        ContextHandler.showTopPane.set(showTopPane);
        ContextHandler.showBottomPane.set(showBottomPane);
        ContextHandler.blindPE.set(blindPE);
        ContextHandler.displayST.set(displayST);
        ContextHandler.s2s.set(s2s);
        ContextHandler.s2t.set(s2t);
        ContextHandler.t2t.set(t2t);
        ContextHandler.t2s.set(t2s);
        ContextHandler.showMTPreview.set(showMTPreview);
        ContextHandler.hideLeftBar.set(hideLeftBar);
        ContextHandler.maximiseAssessmentPage.set(maximiseAssessmentPage);

        ContextHandler.flowManager().addListener(new PETFlowListener() {

            @Override
            public void editingIsAboutToStart() {
                ContextHandler.editingUndoManager().discardAllEdits();
            }

            @Override
            public void editingHasStarted() {
                ContextHandler.editingUndoManager().discardAllEdits();
            }

            @Override
            public void editingIsAboutToFinish() {
            }

            @Override
            public void editingHasFinished() {
            }
        });
    }

    public static void release() {
        contextId.remove();
        workspace.remove();
        source.remove();
        target.remove();
        defaultUser.remove();
        standardBackGroundColor.remove();
        doneBackGroundColor.remove();
        toDoBackGroundColor.remove();
        editableBackGroundColor.remove();
        editingBackGroundColor.remove();
        standardFont.remove();
        editableFont.remove();
        editingFont.remove();
        idFont.remove();
        generalInfoFont.remove();
        editableMessageUndone.remove();
        editableMessageDone.remove();
        hideIfNotEditing.remove();
        applyHideIfNotEditingToAll.remove();
        showSentenceId.remove();
        showProducer.remove();
        assessing.remove();
        showOutputTimeStampCheckBox.remove();
        outputTimeStamp.remove();
        peAssessments.remove();
        htAssessments.remove();
        autoAccept.remove();
        showReference.remove();
        signalManager.remove();
        flowManager.remove();
        keystrokes.remove();
        impossible.remove();
        unchanged.remove();
        unnecessary.remove();
        logChanges.remove();
        infoPrinter.remove();
        generalInfo.remove();
        externalSourceInfo.remove();
        externalTargetInfo.remove();
        lengthConstraints.remove();
        assessmentsByPage.remove();
        externalInfoParams.remove();
        autoSave.remove();
        autoSaveMemory.remove();
        sentencesByPage.remove();
        editablePosition.remove();
        editingUndoManager.remove();
        skipAssessmentOnAutoAccept.remove();
        skipAssessmentOnDiscard.remove();
        blockEditing.remove();
        disableCommentOnAssessment.remove();
        renderHTML.remove();
        showTopPane.remove();
        showBottomPane.remove();
        blindPE.remove();
        displayST.remove();
        s2s.remove();
        s2t.remove();
        t2t.remove();
        t2s.remove();
        showMTPreview.remove();
        hideLeftBar.remove();
        maximiseAssessmentPage.remove();
    }

    public static String contextId() {
        return contextId.get();
    }

    public static String workspace() {
        return workspace.get();
    }

    public static String source() {
        return source.get();
    }

    public static String target() {
        return target.get();
    }

    public static String defaultUser() {
        return defaultUser.get();
    }

    public static UndoManager editingUndoManager() {
        return editingUndoManager.get();
    }

    public static int sentencesByPage() {
        return sentencesByPage.get();
    }

    public static int editablePosition() {
        return editablePosition.get();
    }

    public static Color standardBackGroundColor() {
        return standardBackGroundColor.get();
    }

    public static Color doneBackGroundColor() {
        return doneBackGroundColor.get();
    }

    public static Color toDoBackGroundColor() {
        return toDoBackGroundColor.get();
    }

    public static Color editableBackGroundColor() {
        return editableBackGroundColor.get();
    }

    public static Color editingBackGroundColor() {
        return editingBackGroundColor.get();
    }

    public static Font standardFont() {
        return standardFont.get();
    }

    public static Font idFont() {
        return idFont.get();
    }

    public static Font editableFont() {
        return editableFont.get();
    }

    public static Font editingFont() {
        return editingFont.get();
    }

    public static Font generalInfoFont() {
        return generalInfoFont.get();
    }

    public static Segment editableMessageUndone() {
        return editableMessageUndone.get();
    }

    public static Segment editableMessageDone() {
        return editableMessageDone.get();
    }

    public static String hideIfNotEditing() {
        return hideIfNotEditing.get();
    }

    public static boolean applyHideIfNotEditingToAll() {
        return applyHideIfNotEditingToAll.get();
    }

    public static boolean showSentenceId() {
        return showSentenceId.get();
    }

    public static EditableUnitGUI.Tip showProducer() {
        return showProducer.get();
    }

    public static boolean autoAccept() {
        return autoAccept.get();
    }

    public static boolean showReference() {
        return showReference.get();
    }

    public static boolean logChanges() {
        return logChanges.get();
    }

    public static boolean assessing() {
        return assessing.get();
    }

    public static boolean autoSave() {
        return autoSave.get();
    }
    
    public static int autoSaveMemory() {
        return autoSaveMemory.get();
    }

    public static List<AssessmentDescriptor> peAssessments() {
        return peAssessments.get();
    }

    public static List<AssessmentDescriptor> htAssessments() {
        return htAssessments.get();
    }

    public static int assessmentsByPage() {
        return assessmentsByPage.get();
    }

    public static boolean showOutputTimeStampCheckBox() {
        return showOutputTimeStampCheckBox.get();
    }

    public static boolean outputTimeStamp() {
        return outputTimeStamp.get();
    }

    public static String assessmentSeparator() {
        return assessmentSeparator.get();
    }

    public static SignalManager signalManager() {
        return signalManager.get();
    }

    public static boolean keystrokes() {
        return keystrokes.get();
    }

    public static boolean impossible() {
        return impossible.get();
    }

    public static boolean unchanged() {
        return unchanged.get();
    }

    public static boolean unnecessary() {
        return unnecessary.get();
    }

    public static InfoPrinter infoPrinter() {
        return infoPrinter.get();
    }

    public static Map<String, String> generalInfo() {
        return generalInfo.get();
    }

    public static List<PETDataBase> externalSourceInfo() {
        return externalSourceInfo.get();
    }

    public static List<PETDataBase> externalTargetInfo() {
        return externalTargetInfo.get();
    }

    public static Map<String, String> lengthConstraints() {
        return lengthConstraints.get();
    }

    public static ExternalInfoParams externalInfoParams() {
        return externalInfoParams.get();
    }

    public static boolean skipAssessmentOnAutoAccept() {
        return skipAssessmentOnAutoAccept.get();
    }

    public static boolean skipAssessmentOnDiscard() {
        return skipAssessmentOnDiscard.get();
    }

    public static boolean blockEditing() {
        return blockEditing.get();
    }

    public static boolean disableCommentOnAssessment() {
        return disableCommentOnAssessment.get();
    }

    public static boolean renderHTML() {
        return renderHTML.get();
    }

    public static boolean showTopPane() {
        return showTopPane.get();
    }

    public static boolean showBottomPane() {
        return showBottomPane.get();
    }

    public static boolean blindPE() {
        return blindPE.get();
    }

    public static boolean displayST() {
        return displayST.get();
    }

    public static List<PETDataBase> s2s() {
        return s2s.get();
    }

    public static List<PETDataBase> s2t() {
        return s2t.get();
    }

    public static List<PETDataBase> t2t() {
        return t2t.get();
    }

    public static List<PETDataBase> t2s() {
        return t2s.get();
    }

    public static boolean showMTPreview() {
        return showMTPreview.get();
    }

    public static boolean hideLeftBar() {
        return hideLeftBar.get();
    }

    public static PETFlowManager flowManager() {
        return flowManager.get();
    }
    
    public static boolean maximiseAssessmentPage(){
        return maximiseAssessmentPage.get();
    }
}
