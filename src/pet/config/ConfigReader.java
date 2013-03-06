/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pet.config;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.swing.JOptionPane;
import pet.annotation.AssessmentDescriptor;
import pet.annotation.adapter.AssessmentDescriptorAdapter;
import pet.annotation.adapter.StringSentence;
import pet.annotation.xml.PETParseException;
import pet.db.ExternalInforParamsAdapter;
import pet.db.PETDataBase;
import pet.db.ParaphraseDB;
import pet.frontend.components.EditableUnitGUI;
import pet.frontend.util.VisualLogger;
import pet.usr.adapter.FileAdapter;

/**
 *
 * @author waziz
 */
public class ConfigReader {

    private final FileAdapter config;
    private List<PETDataBase> externalSourceInformation = new ArrayList<PETDataBase>();
    private List<PETDataBase> externalTargetInformation = new ArrayList<PETDataBase>();
    private List<PETDataBase> s2s = new ArrayList<PETDataBase>();
    private List<PETDataBase> s2t = new ArrayList<PETDataBase>();
    private List<PETDataBase> t2s = new ArrayList<PETDataBase>();
    private List<PETDataBase> t2t = new ArrayList<PETDataBase>();
    private Map<String, String> lengthConstraints = new HashMap<String, String>();
    private Map<String, String> generalInfos = new HashMap<String, String>();
    private List<AssessmentDescriptor> postEditingAssessments = new ArrayList<AssessmentDescriptor>();
    private List<AssessmentDescriptor> translationAssessments = new ArrayList<AssessmentDescriptor>();
    private String user = "";
    private String workspace = null;
    private boolean autoAccept = false;
    private boolean showReference = false;
    private boolean showSentenceId = false;
    private boolean assessing = true;
    private boolean impossible = false;
    private boolean unchanged = false;
    private boolean unnecessary = false;
    private boolean keystrokes = false;
    private boolean logChanges = false;
    private boolean autoSave = false;
    private int autoSaveMemory = -1;
    private boolean skipAssessmentOnAutoAccept = false;
    private boolean skipAssessmentOnDiscard = false;
    private String hideIfNotEditing = "never";
    private String editableMessageUndone = "Click to start...";
    private String editableMessageDone = "Click to redo...";
    private String assessmentSeparator = "[|]";
    private Font generalInfoFont = new Font("Ubuntu", Font.PLAIN, 12);
    private Font standardFont = new Font("Ubuntu", Font.PLAIN, 12);
    private Font editableFont = new Font("Ubuntu", Font.ITALIC, 14);
    private Font editingFont = new Font("Ubuntu", Font.BOLD, 14);
    private Font idFont = new Font("Times", Font.PLAIN, 8);
    private int sentencesByPage = 11;
    private int editablePosition = 5;
    private boolean showOutputTimeStampCheckBox = true;
    private boolean outputTimeStamp = false;
    private final ExternalInforParamsAdapter.Builder externalParamsBuilder = new ExternalInforParamsAdapter.Builder();
    private boolean blockEditing = false;
    private boolean disableCommentOnAssessment = false;
    private boolean renderHTML = false;
    private boolean showTopPane = true;
    private boolean showBottomPane = true;
    private boolean blindPE = false;
    private boolean displayST = true;
    private int assessmentsByPage = 1;
    private boolean applyHideIfNotEditingToAll = false;
    private boolean showMTPreview = false;
    private EditableUnitGUI.Tip showProducer = EditableUnitGUI.Tip.HIDE;
    private final String pecDir;
    private String source = "f";
    private String target = "e";
    private boolean hideLeftBar = false;

    public ConfigReader(final FileAdapter config) {
        this.config = config;
        this.pecDir = MetaHandler.pecDir();
    }

    public void read() throws InvalidConfigException {
        try {
            final Scanner scanner = new Scanner(config.getFile());

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.startsWith("#")) {
                    continue;
                }
                if (line.startsWith("user=")) {
                    user = line.substring(line.indexOf('=') + 1);
                    continue;
                }
                if (line.startsWith("source=")) {
                    source = line.substring(line.indexOf('=') + 1);
                    continue;
                }
                if (line.startsWith("target=")) {
                    target = line.substring(line.indexOf('=') + 1);
                    continue;
                }
                if (line.startsWith("workspace=")) {
                    String w = line.substring(line.indexOf('=') + 1);
                    if (!new File(w).isAbsolute()){
                        w = pecDir + File.separator + w;
                    }
                    final File workspaceDir = new File(w);
                    if (workspaceDir.isDirectory()) {
                        workspace = w;
                    } else {
                        throw new InvalidConfigException("Workspace directory not found: " + w);
                    }
                    continue;
                }
                if (line.startsWith("postEditingAssessment=")) {
                    String list = line.substring(line.indexOf('=') + 1);
                    final List<String> params = Arrays.asList(list.split(assessmentSeparator));
                    if (params.size() < 4) {
                        VisualLogger.error("Parse Error: ignoring malformed 'postEditingAssessment' for it requires an id, a question, the maximum number of answers and a list of available answers");
                    } else {
                        final String id = params.get(0);
                        final String question = params.get(1);
                        final String strMaxAnswers = params.get(2);
                        int maxAnswers = -1;
                        if (!strMaxAnswers.equals("*")) {
                            maxAnswers = Integer.parseInt(strMaxAnswers);
                        }
                        final List<String> answers = new ArrayList<String>();

                        for (int a = 3; a < params.size(); a++) {
                            answers.add(params.get(a));
                        }
                        postEditingAssessments.add(new AssessmentDescriptorAdapter(id, question, answers, maxAnswers));
                    }
                    continue;
                }
                if (line.startsWith("translationAssessment=")) {
                    String list = line.substring(line.indexOf('=') + 1);
                    final List<String> params = Arrays.asList(list.split(assessmentSeparator));
                    if (params.size() < 4) {
                        VisualLogger.error("Parse Error: ignoring malformed 'translationAssessment' for it requires an id, a question, the maximum number of answers and a list of available answers");
                    } else {
                        final String id = params.get(0);
                        final String question = params.get(1);
                        final String strMaxAnswers = params.get(2);
                        int maxAnswers = -1;
                        if (!strMaxAnswers.equals("*")) {
                            maxAnswers = Integer.parseInt(strMaxAnswers);
                        }
                        final List<String> answers = new ArrayList<String>();
                        for (int a = 3; a < params.size(); a++) {
                            answers.add(params.get(a));
                        }
                        translationAssessments.add(new AssessmentDescriptorAdapter(id, question, answers, maxAnswers));
                    }
                    continue;
                }
                if (line.startsWith("sentencesByPage=")) {
                    try {
                        int n = Integer.parseInt(line.substring(line.indexOf('=') + 1));
                        sentencesByPage = n;
                        editablePosition = sentencesByPage / 2;
                    } catch (final Exception e) {
                    }
                }
                if (line.startsWith("assessmentsByPage=")) {
                    String nAssesments = line.substring(line.indexOf('=') + 1);
                    try {
                        assessmentsByPage = Integer.parseInt(nAssesments);
                    } catch (final Exception e) {
                    }
                    continue;
                }
                if (line.equals("showSentenceId")) {
                    showSentenceId = true;
                    continue;
                }
                if (line.equals("showProducers")) {
                    showProducer = EditableUnitGUI.Tip.SHOW;
                    continue;
                }
                if (line.equals("disableAssessment")) {
                    assessing = false;
                    continue;
                }
                if (line.equals("enableAutoAccept")) {
                    autoAccept = true;
                    continue;
                }
                if (line.equals("enableKeystrokes")) {
                    keystrokes = true;
                    continue;
                }
                if (line.equals("enableDiscard") || line.equals("enableImpossible")) {
                    impossible = true;
                    continue;
                }
                if (line.equals("autoSave") || line.startsWith("autoSave=")) {
                    autoSave = true;
                    if (line.startsWith("autoSave=")){
                        autoSaveMemory = Integer.parseInt(line.substring(line.indexOf('=') + 1));
                    }
                    continue;
                }
                if (line.equals("enableUnchanged")) {
                    unchanged = true;
                    continue;
                }
                if (line.equals("enableUnnecessary")) {
                    unnecessary = true;
                    continue;
                }
                if (line.equals("trackChanges")) {
                    logChanges = true;
                    continue;
                }
                if (line.startsWith("generalInfoFont=")) {
                    String detail = line.substring(line.indexOf('=') + 1);
                    String params[] = detail.split(",");
                    int len = 12;
                    if (params.length == 2) {
                        len = Integer.parseInt(params[1]);
                    }
                    generalInfoFont = new Font(params[0], Font.PLAIN, len);
                }
                if (line.startsWith("generalInfo=")) {
                    String info = line.substring(line.indexOf('=') + 1);
                    String color = "";
                    int comma = info.indexOf(",");
                    if (comma >= 0) {
                        color = info.substring(comma + 1);
                        info = info.substring(0, comma);
                    }
                    generalInfos.put(info, color);
                    continue;
                }
                if (line.startsWith("lengthConstraints=")) {
                    String info = line.substring(line.indexOf('=') + 1);
                    String[] fields = info.split(",");
                    if (fields.length == 3) {
                        lengthConstraints.put("ideal", fields[0]);
                        lengthConstraints.put("preferable", fields[1]);
                        lengthConstraints.put("max", fields[2]);
                    }
                    continue;
                }
                if (line.startsWith("idFont=")) {
                    String detail = line.substring(line.indexOf('=') + 1);
                    String params[] = detail.split(",");
                    int len = 8;
                    if (params.length == 2) {
                        len = Integer.parseInt(params[1]);
                    }
                    idFont = new Font(params[0], Font.PLAIN, len);
                }
                if (line.startsWith("standardFont=")) {
                    String detail = line.substring(line.indexOf('=') + 1);
                    String params[] = detail.split(",");
                    int len = 12;
                    if (params.length == 2) {
                        len = Integer.parseInt(params[1]);
                    }
                    standardFont = new Font(params[0], Font.PLAIN, len);
                }
                if (line.startsWith("editableFont=")) {
                    String detail = line.substring(line.indexOf('=') + 1);
                    String params[] = detail.split(",");
                    int len = 14;
                    if (params.length == 2) {
                        len = Integer.parseInt(params[1]);
                    }
                    editableFont = new Font(params[0], Font.ITALIC, len);
                }
                if (line.startsWith("editingFont=")) {
                    String detail = line.substring(line.indexOf('=') + 1);
                    String params[] = detail.split(",");
                    int len = 14;
                    if (params.length == 2) {
                        len = Integer.parseInt(params[1]);

                    }
                    editingFont = new Font(params[0], Font.BOLD, len);
                }
                if (line.startsWith("externalSourceInfo=")) {
                    String db = line.substring(line.indexOf('=') + 1);
                    VisualLogger.processing("Loading file: " + db);
                    try {
                        externalSourceInformation.add(ParaphraseDB.parse(workspace + File.separator + db));
                        VisualLogger.done();
                    } catch (final PETParseException ex) {
                        VisualLogger.error(ex.getMessage());
                    } catch (final FileNotFoundException ex){
                        VisualLogger.error("External info file (source) disregarded (file not found): " + db);
                    }
                }
                if (line.startsWith("externalTargetInfo=")) {
                    String db = line.substring(line.indexOf('=') + 1);
                    VisualLogger.processing("Loading file: " + db);
                    try {
                        externalTargetInformation.add(ParaphraseDB.parse(workspace + File.separator + db));
                        VisualLogger.done();
                    } catch (final PETParseException ex) {
                        VisualLogger.error(ex.getMessage());
                    } catch (final FileNotFoundException ex){
                        VisualLogger.error("External info file (target) disregarded (file not found): " + db);
                    }
                }
                if (line.startsWith("hideIfNotEditing=")) {
                    String opt = line.substring(line.indexOf('=') + 1);
                    if (!opt.isEmpty()) {
                        hideIfNotEditing = opt;
                    }
                }
                if (line.startsWith("editableMessageUndone=")) {
                    editableMessageUndone = line.substring(line.indexOf('=') + 1);
                    if (editableMessageUndone == null) {
                        editableMessageUndone = "";
                    }
                }
                if (line.startsWith("editableMessageDone=")) {
                    editableMessageDone = line.substring(line.indexOf('=') + 1);
                    if (editableMessageDone == null) {
                        editableMessageDone = "";
                    }
                }

                if (line.equals("hideOutputTimeStampCheckBox")) {
                    showOutputTimeStampCheckBox = false;
                }

                if (line.equals("outputTimeStamp")) {
                    outputTimeStamp = true;
                }

                if (line.equals("showReference")) {
                    showReference = true;
                }
                if (line.startsWith("externalSourceInfoMaxOrder=")) {
                    int order = Integer.parseInt(line.substring(line.indexOf('=') + 1));
                    externalParamsBuilder.sourceMaxOrder(order);
                }
                if (line.startsWith("externalSourceInfoMinOrder=")) {
                    int order = Integer.parseInt(line.substring(line.indexOf('=') + 1));
                    externalParamsBuilder.sourceMinOrder(order);
                }
                if (line.startsWith("externalSourceInfoMinLength=")) {
                    int len = Integer.parseInt(line.substring(line.indexOf('=') + 1));
                    externalParamsBuilder.sourceMinLength(len);
                }
                if (line.startsWith("externalTargetInfoMaxOrder=")) {
                    int order = Integer.parseInt(line.substring(line.indexOf('=') + 1));
                    externalParamsBuilder.targetMaxOrder(order);
                }
                if (line.startsWith("externalTargetInfoMinOrder=")) {
                    int order = Integer.parseInt(line.substring(line.indexOf('=') + 1));
                    externalParamsBuilder.targetMinOrder(order);
                }
                if (line.startsWith("externalTargetInfoMinLength=")) {
                    int len = Integer.parseInt(line.substring(line.indexOf('=') + 1));
                    externalParamsBuilder.targetMinLength(len);
                }
                if (line.equals("externalSourceInfoNoLonger")) {
                    externalParamsBuilder.sourceNoLonger(true);
                }
                if (line.equals("externalTargetInfoNoLonger")) {
                    externalParamsBuilder.targetNoLonger(true);
                }
                if (line.equals("skipAssessmentOnAutoAccept")) {
                    skipAssessmentOnAutoAccept = true;
                    continue;
                }
                if (line.equals("skipAssessmentOnDiscard")) {
                    skipAssessmentOnDiscard = true;
                    continue;
                }
                if (line.equals("blockEditing")) {
                    blockEditing = true;
                    continue;
                }
                if (line.equals("disableCommentOnAssessment")) {
                    disableCommentOnAssessment = true;
                    continue;
                }
                if (line.equals("renderHTML")) {
                    renderHTML = true;
                }
                if (line.equals("hideLeftBar")) {
                    hideLeftBar = true;
                    continue;
                }
                if (line.equals("hideTopPane")) {
                    showTopPane = false;
                    continue;
                }
                if (line.equals("hideBottomPane")) {
                    showBottomPane = false;
                    continue;
                }
                if (line.equals("blindPE")) {
                    blindPE = true;
                    continue;
                }
                if (line.equals("displayTS")) {
                    displayST = false;
                    continue;
                }
                if (line.equals("applyHideIfNotEditingToAll")) {
                    applyHideIfNotEditingToAll = true;
                    continue;
                }
                if (line.startsWith("s2s=")) {
                    String db = line.substring(line.indexOf('=') + 1);
                    VisualLogger.processing("Loading file: " + db);
                    try {
                        s2s.add(ParaphraseDB.parse(workspace + File.separator + db));
                        VisualLogger.done();
                    } catch (final PETParseException ex) {
                        VisualLogger.error(ex.getMessage());
                    } catch (final FileNotFoundException ex){
                        VisualLogger.error("Source-source dictionary disregarded (file not found): " + db);
                    }
                }
                if (line.startsWith("s2t=")) {
                    String db = line.substring(line.indexOf('=') + 1);
                    VisualLogger.processing("Loading file: " + db);
                    try {
                        s2t.add(ParaphraseDB.parse(workspace + File.separator + db));
                        VisualLogger.done();
                    } catch (final PETParseException ex) {
                        VisualLogger.error(ex.getMessage());
                    } catch (final FileNotFoundException ex){
                        VisualLogger.error("Source-target dictionary disregarded (file not found): " + db);
                    }
                }
                if (line.startsWith("t2t=")) {
                    String db = line.substring(line.indexOf('=') + 1);
                    VisualLogger.processing("Loading file: " + db);
                    try {
                        t2t.add(ParaphraseDB.parse(workspace + File.separator + db));
                        VisualLogger.done();
                    } catch (final PETParseException ex) {
                        VisualLogger.error(ex.getMessage());
                    } catch (final FileNotFoundException ex){
                        VisualLogger.error("Target-target dictionary disregarded (file not found): " + db);
                    }
                }
                if (line.startsWith("t2s=")) {
                    String db = line.substring(line.indexOf('=') + 1);
                    VisualLogger.processing("Loading file: " + db);
                    try {
                        t2s.add(ParaphraseDB.parse(workspace + File.separator + db));
                        VisualLogger.done();
                    } catch (final PETParseException ex) {
                        VisualLogger.error(ex.getMessage());
                    } catch (final FileNotFoundException ex){
                        VisualLogger.error("Target-source dictionary disregarded (file not found): " + db);
                    }
                }
                if (line.equals("showMTPreview")) {
                    showMTPreview = true;
                    continue;
                }
            }
            if (workspace == null) {
                throw new InvalidConfigException("A workspace is required: set 'workspace=<path>' in the config file.");
            }

        } catch (final FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Configuration file not found: " + config.getFile(), "Error", JOptionPane.ERROR);
        }
    }

    public void initializeHandler() {
        ContextHandler.initialize(config.toString(),
                workspace,
                source,
                target,
                user,
                new Color(209, 205, 201),
                new Color(156, 230, 148),
                new Color(243, 96, 98),
                new Color(255, 255, 255),
                new Color(253, 252, 202),
                standardFont,
                editableFont,
                editingFont,
                idFont,
                generalInfoFont,
                StringSentence.getCaption(editableMessageUndone),
                StringSentence.getCaption(editableMessageDone),
                hideIfNotEditing,
                applyHideIfNotEditingToAll,
                showSentenceId,
                showProducer,
                assessing,
                autoSave,
                autoSaveMemory,
                postEditingAssessments,
                translationAssessments,
                assessmentsByPage,
                generalInfos,
                showOutputTimeStampCheckBox,
                outputTimeStamp,
                assessmentSeparator,
                autoAccept,
                showReference,
                keystrokes,
                impossible,
                unchanged,
                unnecessary,
                logChanges,
                externalSourceInformation,
                externalTargetInformation,
                lengthConstraints,
                externalParamsBuilder.build(),
                sentencesByPage,
                editablePosition,
                skipAssessmentOnAutoAccept,
                skipAssessmentOnDiscard,
                blockEditing,
                disableCommentOnAssessment,
                renderHTML,
                showTopPane,
                showBottomPane,
                blindPE,
                displayST,
                s2s,
                s2t,
                t2t,
                t2s,
                showMTPreview,
                hideLeftBar);
    }
}
