/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AnnotationPage3.java
 *
 * Created on 13-Jan-2012, 11:37:22
 */
package pet.frontend;

import pet.frontend.components.UnitGUI;
import pet.frontend.components.AbstractUnitGUI;
import pet.frontend.components.EditableUnitGUI;
import pet.frontend.components.NonEditableUnitGUI;
import pet.frontend.menu.PopupMenuFactory;
import java.awt.event.MouseWheelEvent;
import pet.frontend.util.MyUndoableEditListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.joda.time.DateTime;
import pet.annotation.AssessmentChoice;
import pet.annotation.AssessmentDescriptor;
import pet.annotation.Job;
import pet.annotation.Unit;
import pet.annotation.adapter.StatusAdapter;
import pet.config.ContextHandler;
import pet.constraints.LengthConstraintEditingListener;
import pet.constraints.UnconstrainedEditingListener;
import pet.frontend.menu.AbstractPopupMouseAdapter;
import pet.frontend.menu.DeleteActionListener;
import pet.frontend.menu.InsertActionListener;
import pet.frontend.menu.ShiftActionListener;
import pet.frontend.menu.TrimActionListener;
import pet.frontend.util.DragFromTextHandler;
import pet.frontend.util.DragFromAndDropToTextHandler;
import pet.frontend.util.WaitingUntilPostEditingStarts;
import pet.io.XMLJobWriter;
import pet.signal.PETCommandEvent;
import pet.signal.PETEditOperationEvent;
import pet.signal.PETFlowEvent;
import pet.signal.PETKeystrokeEvent;
import pet.signal.PETNavigationEvent;
import pet.signal.SignalAdapter;
import pet.usr.adapter.AssessmentListener;
import pet.usr.adapter.AssessmentSelector;
import pet.usr.adapter.CurrentPrinter;
import pet.usr.adapter.EditableUnit;
import pet.usr.adapter.EditionStatus;
import pet.usr.adapter.EditionStatusController;
import pet.usr.adapter.TaskPool;
import pet.usr.adapter.TaskPrinter;
import pet.usr.adapter.UserSpaceController;
import pet.usr.handler.FileHandler;
import pet.usr.handler.SettingsHandler;
import pet.usr.handler.UnitHandler;

/**
 *
 * @author waziz
 */
public class BorderLayoutAnnotationPage extends javax.swing.JFrame implements BitextPage, AssessmentListener {

    private final UserSpaceController userSpaceController;
    private final Job job;
    private final List<EditableUnit> tasks;
    private final boolean timeStamped;
    private final Map<Integer, MultiKey> alignments;
    private final TaskPool pool;
    private final TaskPrinter printer;
    private final EditionStatusController statusController;
    private final int sentencesByPage;
    private final int editablePosition;
    // editing stuff
    private final JLabel editingId;
    private final UnitGUI editingSrc;
    private final UnitGUI editingTgt;
    private List<AssessmentChoice> assessments;
    // info stuff
    private final JLabel lblProgress = new JLabel();
    private final JLabel lblStatus = new JLabel();
    private final JLabel lblPartialTime = new JLabel();
    private final JLabel lblTotalTime = new JLabel();
    private final JLabel lblEditionNumber = new JLabel();
    private final JLabel lblLeft = new JLabel();
    private final JLabel lblGeneralInfo = new JLabel();
    private final JLabel currentId = new JLabel();
    //private final JTextPane currentSentence = new JTextPane();
    private final UnitGUI txtSInfo;
    private final UnitGUI txtTInfo;
    //private final JTextPane txtContext = new JTextPane();
    private final UnitGUI txtContext;
    //private final JTextPane txtMTPreview = new JTextPane();
    private final JToggleButton btnBind = new JToggleButton();
    private final JButton btnRevert = new JButton();
    private final JButton btnAcceptMT = new JButton();
    private final JButton btnCopyContext = new JButton();
    private final JButton btnPreviousLast = new JButton();
    private final JButton btnPrevious = new JButton();
    private final JButton btnNext = new JButton();
    private final JButton btnNextUndone = new JButton();
    private final JButton btnSave = new JButton();
    private final JButton btnClose = new JButton();
    private final JButton btnImpossible = new JButton();
    private final JLabel lblSaving = new JLabel("...");
    private final static Color GREEN = new Color(0, 153, 0);
    private int lastSave = 0;
    private final boolean autoSave;
    private final List<WaitingUntilPostEditingStarts> waitingPEStart;
    private final PopupMenuFactory menuFactory;
    private final List<MultiKey> scrollers;

    /**
     * This class is quite big and underplanned 
     * so expect to find all kind of bad programming practices here.
     * I'll try to slowly improve it.
     * 
     * @param job
     * @param editableTasks
     * @param userSpaceController
     * @param timeStamped 
     */
    public BorderLayoutAnnotationPage(final Job job,
            final List<EditableUnit> editableTasks,
            final UserSpaceController userSpaceController,
            final boolean timeStamped) {
        initComponents();
        this.waitingPEStart = new ArrayList<WaitingUntilPostEditingStarts>();

        this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);

        this.job = job;
        this.userSpaceController = userSpaceController;
        this.timeStamped = timeStamped;
        this.autoSave = ContextHandler.autoSave();
        this.sentencesByPage = ContextHandler.sentencesByPage();
        this.editablePosition = ContextHandler.editablePosition();
        this.setTitle(job.getId() + " by " + SettingsHandler.getUser());
        this.menuFactory = new PopupMenuFactory();
        this.txtContext = new NonEditableUnitGUI(ContextHandler.showProducer(), UnitGUI.UnitGUIType.CONTEXT, 0);
        this.scrollers = new ArrayList<MultiKey>();

        txtSInfo = new EditableUnitGUI(AbstractUnitGUI.Tip.HIDE, UnitGUI.UnitGUIType.SOURCE, 0);
        txtTInfo = new EditableUnitGUI(AbstractUnitGUI.Tip.HIDE, UnitGUI.UnitGUIType.TARGET, 0);

        if (editableTasks == null) {
            this.tasks = getEditableTasks(job.getUnits());
        } else {
            this.tasks = new ArrayList<EditableUnit>(editableTasks);
        }
        this.pool = new TaskPool(this.tasks, sentencesByPage, editablePosition);

        // customizes visual stuff
        alignments = new HashMap<Integer, MultiKey>(sentencesByPage);


        initPanels();
        final MultiKey editable = alignments.get(editablePosition);
        editingId = (JLabel) editable.getKey(0);
        editingSrc = (UnitGUI) editable.getKey(1);
        editingTgt = (UnitGUI) editable.getKey(2);


        this.statusController = new EditionStatusController(lblStatus,
                lblPartialTime,
                lblEditionNumber,
                lblProgress,
                lblLeft,
                editingId,
                editingSrc,
                editingTgt,
                lblGeneralInfo,
                currentId,
                txtContext,
                pool,
                tasks.size(),
                txtSInfo.underlying(),
                txtTInfo.underlying());
        lastSave = statusController.getDone();

        printer = new TaskPrinter(pool,
                alignments,
                new CurrentPrinter(
                currentId,
                txtContext,
                btnAcceptMT,
                btnCopyContext,
                lblEditionNumber,
                lblPartialTime,
                lblTotalTime),
                editablePosition);
        printer.print();

        if (!ContextHandler.lengthConstraints().isEmpty()) {
            final LengthConstraintEditingListener listener = new LengthConstraintEditingListener(lblLeft, editingSrc, editingTgt);
            editingTgt.underlying().getDocument().addDocumentListener(listener);
            statusController.registerFacadeController(listener);
            UnitHandler.initialize(pool, statusController, listener, editingSrc, editingTgt, txtContext);
            listener.on();
        } else {
            final UnconstrainedEditingListener listener = new UnconstrainedEditingListener();
            UnitHandler.initialize(pool, statusController, listener, editingSrc, editingTgt, txtContext);
            editingTgt.underlying().getDocument().addDocumentListener(listener);
            if (ContextHandler.logChanges()) {
                listener.on();
            }
        }

        if (ContextHandler.blockEditing()) {
            editingTgt.underlying().setEditable(false);
        }

        statusController.update();
        updateProgress();


        ((JPanel) this.getContentPane()).getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                Event.CTRL_MASK), new AbstractAction() {

            public void actionPerformed(ActionEvent ae) {
                saveProgress(false);
            }
        });

        findBestStartPosition();
        btnNext.requestFocusInWindow();

    }

    private void findBestStartPosition() {
        final EditableUnit task = statusController.getEditingTask();
        if (task != null && task.getStatus().equals(StatusAdapter.FINISHED)) {
            btnNextUndone.doClick();
        }
    }

    private void updateProgress() {
        final int done = statusController.getDone();
        lblProgress.setText(done + "/" + tasks.size());
        lblSaving.setForeground(GREEN);
        lblSaving.setText(lastSave + " saved");
    }

    private List<EditableUnit> getEditableTasks(final List<Unit> tasks) {
        final List<EditableUnit> editableTasks = new ArrayList<EditableUnit>(tasks.size());
        for (final Unit t : tasks) {
            editableTasks.add(new EditableUnit(t));
        }
        return Collections.unmodifiableList(editableTasks);
    }

    private void initPanels() {



        final EditableUnitGUI.Tip showProducer = ContextHandler.showProducer();
        currentId.setFont(ContextHandler.idFont());

        txtContext.underlying().setEditable(false);
        if (ContextHandler.renderHTML()) {
            txtContext.underlying().setContentType("text/html");
            //txtMTPreview.setContentType("text/html");
        }
        lblSaving.setFont(new java.awt.Font("Ubuntu", 0, 10));
        lblSaving.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProgress.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProgress.setVisible(true); // TODO: ask the context handler
        lblProgress.setToolTipText(this.job.getId());
        txtSInfo.underlying().setContentType("text/html");
        txtTInfo.underlying().setContentType("text/html");
        lblGeneralInfo.setFont(ContextHandler.generalInfoFont());

        final Dimension topBox = new Dimension(340, 90);
        final Dimension topLeftMargin = new Dimension(30, 90);
        final Dimension topRightMargin = new Dimension(45, 90);
        final Dimension topCenterMargin = new Dimension(265, 90);
        final Dimension topFirstRow = new Dimension(265, 15);
        final Dimension topSecondRow = new Dimension(265, 75);

        final Dimension centerBox = new Dimension(340, 100);
        final Dimension bitextBox = new Dimension(265, 100);
        final Dimension bitextIdBox = new Dimension(30, 100);

        final Dimension bottomBox = new Dimension(340, 80);
        final Dimension bottomLeftMargin = new Dimension(30, 80);
        final Dimension bottomRightMargin = new Dimension(45, 80);
        final Dimension bottomCenterMargin = new Dimension(265, 80);

        // THE PAGE USES BORDER LAYOUT
        // THIS IS THE PAGE'S START
        final JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setPreferredSize(topBox);
        this.add(topPanel, BorderLayout.PAGE_START);

        // topPanel should look like this
        //  | |           |   |
        //  | |-----------|   |
        //  | |           |   |
        final JPanel topLeftPanel = new JPanel(new BorderLayout());
        final JPanel topCenterPanel = new JPanel(new BorderLayout());
        final JPanel topRightPanel = new JPanel(new BorderLayout());

        topLeftPanel.setPreferredSize(topLeftMargin);
        topCenterPanel.setPreferredSize(topCenterMargin);
        topRightPanel.setPreferredSize(topRightMargin);

        // the central one is split into two, so:
        final JPanel topFirstRowPanel = new JPanel(new GridLayout(1, 0, 1, 1));

        final int topSecondRowColumns = (ContextHandler.showMTPreview()) ? 2 : 1;
        final JPanel topSecondRowPanel = new JPanel(new GridLayout(0, topSecondRowColumns, 0, 0));
        topFirstRowPanel.setPreferredSize(topFirstRow);
        topSecondRowPanel.setPreferredSize(topSecondRow);

        topCenterPanel.add(topFirstRowPanel, BorderLayout.PAGE_START);
        topCenterPanel.add(topSecondRowPanel, BorderLayout.PAGE_END);

        if (!ContextHandler.hideLeftBar()) {
            topPanel.add(topLeftPanel, BorderLayout.LINE_START);
        }
        topPanel.add(topCenterPanel, BorderLayout.CENTER);
        topPanel.add(topRightPanel, BorderLayout.LINE_END);

        // now we add the JLabels and JTextPanes in the panels
        topLeftPanel.add(currentId);

        final JScrollPane contextScroll = new JScrollPane(txtContext.underlying(), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        //final JScrollPane previewScroll = new JScrollPane(txtMTPreview, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        if (ContextHandler.showTopPane()) {
            topSecondRowPanel.add(contextScroll);
            txtContext.underlying().addMouseListener(menuFactory.buildContextMenu("context"));
        }
        // if (ContextHandler.showMTPreview()){
        //      topSecondRowPanel.add(previewScroll);
        //      txtMTPreview.addMouseListener(menuFactory.buildTargetMenu("context", false));
        //  }


        final JPanel topFirstRowPanelFirstHalf = new JPanel(new GridLayout(1, 0, 1, 1));
        final JPanel topFirstRowPanelSecondHalf = new JPanel(new GridLayout(1, 0, 1, 1));
        topFirstRowPanel.add(topFirstRowPanelFirstHalf);
        topFirstRowPanel.add(topFirstRowPanelSecondHalf);

        topFirstRowPanelFirstHalf.add(lblGeneralInfo);
        topFirstRowPanelSecondHalf.add(lblStatus);
        topFirstRowPanelSecondHalf.add(lblLeft);
        topFirstRowPanelSecondHalf.add(lblPartialTime);
        topFirstRowPanelSecondHalf.add(lblEditionNumber);
        topFirstRowPanelSecondHalf.add(lblTotalTime);
        topRightPanel.add(lblProgress, BorderLayout.CENTER);
        topRightPanel.add(lblSaving, BorderLayout.SOUTH);


        // THIS IS THE PAGE'S CENTER
        final JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setPreferredSize(centerBox);
        this.add(centerPanel, BorderLayout.CENTER);
        // it looks like this
        // | |     |
        // | |     |
        // | |     |
        final JPanel idPanel = new JPanel(new GridLayout(0, 1, 2, 1));
        final int nColumns = (ContextHandler.blindPE()) ? 1 : 2;
        final boolean displayST = ContextHandler.displayST();
        final JPanel bitextPanel = new JPanel(new GridLayout(0, nColumns, 2, 1));
        bitextPanel.setPreferredSize(bitextBox);
        idPanel.setPreferredSize(bitextIdBox);
        if (!ContextHandler.hideLeftBar()) {
            centerPanel.add(idPanel, BorderLayout.LINE_START);
        }
        centerPanel.add(bitextPanel, BorderLayout.CENTER);

        // THIS IS THE PAGE'S BOTTOM
        final JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setPreferredSize(bottomBox);

        if (ContextHandler.showBottomPane()) {
            this.add(bottomPanel, BorderLayout.PAGE_END);
        }
        // bottomPanel should look like this
        //  | |           |   |
        //  | |           |   |
        final JPanel bottomLeftPanel = new JPanel(new BorderLayout());
        final JPanel bottomCenterPanel = new JPanel(new GridLayout(0, 2, 2, 1));
        final JPanel bottomRightPanel = new JPanel(new GridLayout(0, 1, 2, 1));
        bottomLeftPanel.setPreferredSize(bottomLeftMargin);
        bottomCenterPanel.setPreferredSize(bottomCenterMargin);
        bottomRightPanel.setPreferredSize(bottomRightMargin);
        if (!ContextHandler.hideLeftBar()) {
            bottomPanel.add(bottomLeftPanel, BorderLayout.LINE_START);
        }
        bottomPanel.add(bottomCenterPanel, BorderLayout.CENTER);
        bottomPanel.add(bottomRightPanel, BorderLayout.LINE_END);

        // now we add stuff to the bottom panels
        //       bottomRightPanel.add(lblSaving);
        //externalInfoPanel.setLayout(new GridLayout(0, 2, 2, 1));
        final JScrollPane leftScroll = new JScrollPane(txtSInfo.underlying(), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        final JScrollPane rigthScroll = new JScrollPane(txtTInfo.underlying(), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        bottomCenterPanel.add(leftScroll);
        bottomCenterPanel.add(rigthScroll);



        final Font font = ContextHandler.standardFont();
        final Color color = ContextHandler.standardBackGroundColor();
        final boolean showId = ContextHandler.showSentenceId();
        final Font idFont = ContextHandler.idFont();

        final MouseWheelListener mouseWheelListener = new java.awt.event.MouseWheelListener() {

            public void mouseWheelMoved(MouseWheelEvent mwe) {
                tookbarMouseWheelMoved(mwe);
            }
        };
        idPanel.addMouseWheelListener(mouseWheelListener);
        topLeftPanel.addMouseWheelListener(mouseWheelListener);
        topRightPanel.addMouseWheelListener(mouseWheelListener);
        bottomLeftPanel.addMouseWheelListener(mouseWheelListener);
        bottomRightPanel.addMouseWheelListener(mouseWheelListener);

        AbstractPopupMouseAdapter srcMenu = menuFactory.buildSourceMenu("source", false);
        AbstractPopupMouseAdapter activeSrcMenu = menuFactory.buildSourceMenu("source-active", true);
        AbstractPopupMouseAdapter tgtMenu = menuFactory.buildTargetMenu("target", false);
        AbstractPopupMouseAdapter activeTgtMenu = menuFactory.buildTargetMenu("target-active", true);


        final DragFromTextHandler dragHandler = new DragFromTextHandler();
        final DragFromAndDropToTextHandler dragAndDropHandler = new DragFromAndDropToTextHandler();

        txtContext.underlying().setTransferHandler(dragHandler);
        txtContext.underlying().setDragEnabled(true);

        txtSInfo.underlying().setTransferHandler(dragHandler);
        txtSInfo.underlying().setDragEnabled(true);
        txtTInfo.underlying().setTransferHandler(dragHandler);
        txtTInfo.underlying().setDragEnabled(true);

        for (int i = 0; i < sentencesByPage; i++) {

            //final AbstractUnitGUI src = (i == editablePosition) ? new EditableUnitGUI(showProducer, UnitGUI.UnitGUIType.SOURCE, 0) : new NonEditableUnitGUI(showProducer, UnitGUI.UnitGUIType.SOURCE, i-editablePosition);
            final AbstractUnitGUI src = new NonEditableUnitGUI(showProducer, UnitGUI.UnitGUIType.SOURCE, i - editablePosition);
            final AbstractUnitGUI tgt = (i == editablePosition) ? new EditableUnitGUI(showProducer, UnitGUI.UnitGUIType.TARGET, 0) : new NonEditableUnitGUI(showProducer, UnitGUI.UnitGUIType.TARGET, i - editablePosition);

            if (ContextHandler.renderHTML()) {
                src.setContentType("text/html");
                tgt.setContentType("text/html");
            }

            src.setFont(font);
            src.setBackground(color);
            src.setEditable(false);
            src.setAutoscrolls(true);

            final JScrollPane srcScroll = new JScrollPane(src, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

            tgt.setFont(font);
            tgt.setBackground(color);
            //tgt.setBorder(BorderFactory.createLineBorder(Color.black));
            tgt.setEditable(false);
            final JScrollPane tgtScroll = new JScrollPane(tgt, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

            scrollers.add(new MultiKey(srcScroll, tgtScroll, srcScroll.getVerticalScrollBar().getModel()));

            final JLabel id = new JLabel();
            id.setFont(idFont);
            id.setText("s " + Integer.toString(i + 1));
            //id.setBorder(BorderFactory.createLineBorder(Color.black));
            id.setVisible(showId);
            src.setTransferHandler(dragHandler);
            src.setDragEnabled(true);

            if (i == editablePosition) {
                src.addMouseListener(activeSrcMenu);
                tgt.addMouseListener(activeTgtMenu);

                tgt.setTransferHandler(dragAndDropHandler);
                tgt.setDragEnabled(true);


                tgt.setEditable(true);
                tgt.setFont(ContextHandler.editableFont());
                src.setFont(ContextHandler.editableFont());
                id.setFont(idFont);
                src.setBackground(ContextHandler.standardBackGroundColor());
                tgt.setBackground(ContextHandler.standardBackGroundColor());

                //TitledBorder titled = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black,2), "title");
                //titled.setTitleJustification(TitledBorder.CENTER);
                //src.setBorder(titled);
                src.setBorder(BorderFactory.createLineBorder(Color.black, 2));
                tgt.setBorder(BorderFactory.createLineBorder(Color.black, 2));
                id.setBorder(BorderFactory.createLineBorder(Color.black, 2));
                tgt.addFocusListener(new java.awt.event.FocusAdapter() {

                    @Override
                    public void focusGained(java.awt.event.FocusEvent evt) {
                        tgtSentenceFocusGained(evt);
                    }
                });
                tgt.addKeyListener(new java.awt.event.KeyAdapter() {

                    @Override
                    public void keyPressed(java.awt.event.KeyEvent evt) {
                        tgtSentenceKeyPressed(evt);
                    }

                    @Override
                    public void keyReleased(java.awt.event.KeyEvent evt) {
                        tgtSentenceKeyReleased(evt);
                    }

                    @Override
                    public void keyTyped(java.awt.event.KeyEvent evt) {
                        tgtSentenceKeyTyped(evt);
                    }
                });

                final MyUndoableEditListener undoableListener = new MyUndoableEditListener(ContextHandler.editingUndoManager());
                tgt.getDocument().addUndoableEditListener(undoableListener);
                final InputMap inMap = tgt.getInputMap();

                inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
                        Event.CTRL_MASK), undoableListener.getUndo());
                inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y,
                        Event.CTRL_MASK), undoableListener.getRedo());
                inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R,
                        Event.CTRL_MASK), new AbstractAction() {

                    private final ActionListener listener = new InsertActionListener();

                    public void actionPerformed(ActionEvent ae) {
                        listener.actionPerformed(ae);
                    }
                });
                inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_I,
                        Event.CTRL_MASK), new AbstractAction() {

                    private final ActionListener listener = new InsertActionListener();

                    public void actionPerformed(ActionEvent ae) {
                        listener.actionPerformed(ae);
                    }
                });
                inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,
                        Event.CTRL_MASK), new AbstractAction() {

                    private final ActionListener listener = new DeleteActionListener();

                    public void actionPerformed(ActionEvent ae) {
                        listener.actionPerformed(ae);
                    }
                });
                inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                        Event.CTRL_MASK), new AbstractAction() {

                    private final ActionListener listener = new ShiftActionListener();

                    public void actionPerformed(ActionEvent ae) {
                        listener.actionPerformed(ae);
                    }
                });
                inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_T,
                        Event.CTRL_MASK), new AbstractAction() {

                    private final ActionListener listener = new TrimActionListener();

                    public void actionPerformed(ActionEvent ae) {
                        listener.actionPerformed(ae);
                    }
                });

                inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                        Event.ALT_MASK), new AbstractAction() {

                    public void actionPerformed(ActionEvent ae) {
                        btnNext.requestFocusInWindow();
                        btnNext.doClick();
                        editingTgt.underlying().requestFocus();
                    }
                });
                inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
                        Event.ALT_MASK), new AbstractAction() {

                    public void actionPerformed(ActionEvent ae) {
                        btnNext.requestFocusInWindow();
                        btnNext.doClick();
                    }
                });
                inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_P,
                        Event.ALT_MASK), new AbstractAction() {

                    public void actionPerformed(ActionEvent ae) {
                        btnPrevious.requestFocusInWindow();
                        btnPrevious.doClick();
                        editingTgt.underlying().requestFocus();
                    }
                });
                inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,
                        Event.ALT_MASK), new AbstractAction() {

                    public void actionPerformed(ActionEvent ae) {
                        btnPrevious.requestFocusInWindow();
                        btnPrevious.doClick();
                    }
                });
                inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C,
                        Event.ALT_MASK), new AbstractAction() {

                    public void actionPerformed(ActionEvent ae) {
                        btnCopyContext.doClick();
                    }
                });
                inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                        Event.ALT_MASK), new AbstractAction() {

                    public void actionPerformed(ActionEvent ae) {
                        if (btnAcceptMT.isEnabled()) {
                            btnNext.requestFocusInWindow();
                            btnAcceptMT.doClick();
                        }
                    }
                });
                inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R,
                        Event.ALT_MASK), new AbstractAction() {

                    public void actionPerformed(ActionEvent ae) {
                        btnRevert.doClick();
                    }
                });
                inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,
                        Event.ALT_MASK), new AbstractAction() {

                    public void actionPerformed(ActionEvent ae) {
                        btnNext.requestFocusInWindow();
                        btnImpossible.doClick();
                    }
                });
                inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_B,
                        Event.ALT_MASK), new AbstractAction() {

                    public void actionPerformed(ActionEvent ae) {
                        btnBind.doClick();
                        editingTgt.underlying().requestFocus();
                    }
                });

            } else {
                src.addMouseListener(srcMenu);
                tgt.addMouseListener(tgtMenu);
                tgt.setTransferHandler(dragHandler);
                tgt.setDragEnabled(true);
            }



            if (displayST) {
                if (nColumns == 2) {
                    bitextPanel.add(srcScroll);
                }
                bitextPanel.add(tgtScroll);
            } else {
                bitextPanel.add(tgtScroll);
                if (nColumns == 2) {
                    bitextPanel.add(srcScroll);
                }
            }
            idPanel.add(id);
            alignments.put(i, new MultiKey(id, src, tgt));

        }
        initTools();

    }

    private void initTools() {

        btnImpossible.setEnabled(ContextHandler.impossible());

        final Dimension toolsMargin = new Dimension(45, 125);
        final JPanel toolsPanel = new JPanel(new GridLayout(0, 1, 2, 5));
        toolsPanel.setPreferredSize(toolsMargin);
        this.add(toolsPanel, BorderLayout.LINE_END);

        btnBind.setIcon(new javax.swing.ImageIcon(getClass().getResource("/rendering/icons/bind2.png"))); // NOI18N
        btnBind.setToolTipText("Bind source-target scrolling (B)");

        btnNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/rendering/icons/down.png"))); // NOI18N
        btnNext.setToolTipText("Next task (DOWN)");
        btnPrevious.setIcon(new javax.swing.ImageIcon(getClass().getResource("/rendering/icons/up.png"))); // NOI18N
        btnPrevious.setToolTipText("Previous task (UP)");
        btnNextUndone.setIcon(new javax.swing.ImageIcon(getClass().getResource("/rendering/icons/down last.png"))); // NOI18N
        btnNextUndone.setToolTipText("Find next undone task (END)");
        btnPreviousLast.setIcon(new javax.swing.ImageIcon(getClass().getResource("/rendering/icons/up last.png"))); // NOI18N
        btnPreviousLast.setToolTipText("Find previous undone task (HOME)");
        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/rendering/icons/save.gif"))); // NOI18N
        btnSave.setToolTipText("Save (F10)");
        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/rendering/icons/quit.png"))); // NOI18N
        btnClose.setToolTipText("Close (ALT+F4)");


        btnCopyContext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/rendering/icons/copy.gif")));
        btnCopyContext.setToolTipText("Copy text from the top box to the editing box (C)");
        btnAcceptMT.setIcon(new javax.swing.ImageIcon(getClass().getResource("/rendering/icons/accept2.png")));
        btnAcceptMT.setToolTipText("Accept the MT (A)");
        btnRevert.setIcon(new javax.swing.ImageIcon(getClass().getResource("/rendering/icons/copy.png")));
        btnRevert.setToolTipText("Revert to last revision (R)");


        btnImpossible.setIcon(new javax.swing.ImageIcon(getClass().getResource("/rendering/icons/bin.png")));
        btnImpossible.setToolTipText("Discard unit (D)");

        toolsPanel.add(btnCopyContext);
        toolsPanel.add(btnRevert);
        if (ContextHandler.autoAccept()) {
            toolsPanel.add(btnAcceptMT);
        }
        if (ContextHandler.impossible()) {
            toolsPanel.add(btnImpossible);
        }
        toolsPanel.add(btnBind);
        toolsPanel.add(btnPreviousLast);
        toolsPanel.add(btnPrevious);
        toolsPanel.add(btnNext);
        toolsPanel.add(btnNextUndone);
        toolsPanel.add(btnSave);
        toolsPanel.add(btnClose);

        final MouseWheelListener mouseWheelListener = new java.awt.event.MouseWheelListener() {

            public void mouseWheelMoved(MouseWheelEvent mwe) {
                tookbarMouseWheelMoved(mwe);
            }
        };

        final KeyAdapter keyListener = new java.awt.event.KeyAdapter() {

            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                toolbarKeyPressed(evt);
            }
        };

        btnCopyContext.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCopyContextActionPerformed(evt);
            }
        });
        btnCopyContext.addKeyListener(keyListener);

        btnAcceptMT.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAcceptMTActionPerformed(evt);
            }
        });
        btnAcceptMT.addKeyListener(keyListener);

        btnRevert.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRevertActionPerformed(evt);
            }
        });
        btnRevert.addKeyListener(keyListener);

        btnImpossible.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImpossibleActionPerformed(evt);
            }
        });
        btnImpossible.addKeyListener(keyListener);

        btnPrevious.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousActionPerformed(evt);
            }
        });
        btnPrevious.addKeyListener(keyListener);

        btnNext.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });
        btnNext.addKeyListener(keyListener);

        btnNextUndone.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextUndoneActionPerformed(evt);
            }
        });
        btnNextUndone.addKeyListener(keyListener);

        btnPreviousLast.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousLastActionPerformed(evt);
            }
        });
        btnPreviousLast.addKeyListener(keyListener);

        btnSave.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        btnSave.addKeyListener(keyListener);

        btnClose.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        btnClose.addKeyListener(keyListener);

        btnBind.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBindActionPerformed(evt);
            }
        });

        toolsPanel.addMouseWheelListener(mouseWheelListener);
        toolsPanel.addKeyListener(keyListener);

    }

    private boolean previous(boolean continuous) {
        if (continuous) {
            if (pool.moveBackward()) {
                printer.print();
                statusController.update();
                return true;
            }
        } else {
            pool.findPreviousTaskToDo();
            printer.print();
            statusController.update();
            return true;
        }
        return false;
    }

    private boolean next(final boolean continuous) {
        boolean status = false;
        if (continuous) {
            if (pool.moveForward()) {
                printer.print();
                statusController.update();
                status = true;
            }
        } else {
            pool.findNextTaskToDo();
            printer.print();
            statusController.update();
            status = true;
        }
        btnNext.requestFocusInWindow();
        return status;
    }

    private void getAssessment(final EditableUnit snapshot) {
        ContextHandler.signalManager().fire(new PETFlowEvent(PETFlowEvent.ActionType.EDITING_END));

        if (ContextHandler.assessing()) {
            ContextHandler.signalManager().fire(new PETFlowEvent(PETFlowEvent.ActionType.ASSESSING_START));
            assessments = new ArrayList<AssessmentChoice>();
            final int assessmentsByPage = ContextHandler.assessmentsByPage();
            final List<AssessmentDescriptor> descriptors = AssessmentSelector.getAssessmentDescriptors(snapshot);
            for (int i = 0; i < descriptors.size(); i += assessmentsByPage) {
                final List<AssessmentDescriptor> selected = new ArrayList<AssessmentDescriptor>();

                for (int j = 0; j < assessmentsByPage; j++) {
                    if (i + j < descriptors.size()) {
                        selected.add(descriptors.get(i + j));
                    } else {
                        break;
                    }
                }
                final GridAssessmentPage dialog = new GridAssessmentPage(new javax.swing.JFrame(),
                        true,
                        this,
                        AssessmentSelector.getSummary(snapshot),
                        selected);
                dialog.setVisible(true);
            }
            ContextHandler.signalManager().fire(new PETFlowEvent(PETFlowEvent.ActionType.ASSESSING_END));
        } else {
            skipAssessment();
        }

    }

    private void skipAssessment() {
        ContextHandler.signalManager().fire(new PETFlowEvent(PETFlowEvent.ActionType.EDITING_END));
        assessments = Collections.emptyList();
    }

    @Override
    public void assess(final List<AssessmentChoice> chosen) {
        assessments.addAll(chosen);
    }

    private boolean move(final boolean forward, final boolean continuous) {
        final EditableUnit editing = (EditableUnit) pool.getPool().get(editablePosition);
        if (editing != null) { // there is a task
            if (statusController.getEditionStatus() == EditionStatus.EDITING) { //it's being edited
                ContextHandler.flowManager().editingIsAboutToFinish();
                if (canSave()) { // it can be saved
                    final EditableUnit snapshot = getSnapshot(editing);
                    getAssessment(snapshot);
                    statusController.done(snapshot.getTarget().toString(),
                            assessments);
                    ContextHandler.flowManager().editingHasFinished();
                    boolean opResult = false;
                    if (forward) {
                        opResult = next(continuous);
                    } else {
                        opResult = previous(continuous);
                    }//                            statusController.readToEdit();
                    if (autoSave) {
                        saveProgress(true);
                    } else if (lblSaving.getForeground().equals(GREEN)) {
                        lblSaving.setForeground(Color.RED);
                    }
                    return opResult;
                }
            }
        }
        if (forward) {
            return next(continuous);
        } else {
            return previous(continuous);
        }
    }

    private boolean canSave() {
        final String newSentence = editingTgt.getText().trim();
        if (newSentence.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You left an empty translation", "Warning", JOptionPane.ERROR_MESSAGE);
            return false;
        } else {
            return true;
        }
    }

    private EditableUnit getSnapshot(final EditableUnit editing) {
        return editing.getSnapshot(editingTgt.getText().trim());
    }

    private EditableUnit getSnapshot(final EditableUnit editing, final String target) {
        return editing.getSnapshot(target);
    }

    private void writeXMLTaskResults(final boolean auto) {
        String fileName = "";
        final int done = statusController.getDone();
        if (!auto || done > 0) {
            String suffix = "";
            if (auto) {
                suffix = "." + Integer.toString(done);
            }
            if (timeStamped) {
                fileName = SettingsHandler.getWorkspace()
                        + File.separator + SettingsHandler.getUser()
                        + File.separator + job.getId()
                        + "." + new DateTime(System.currentTimeMillis()).toString("ddMMyy_hhmmss")
                        + FileHandler.RESULT_SUFIX + suffix;
            } else {
                fileName = SettingsHandler.getWorkspace()
                        + File.separator + SettingsHandler.getUser()
                        + File.separator + job.getId()
                        + FileHandler.RESULT_SUFIX + suffix;
            }
            final File file = new File(fileName);
            final XMLJobWriter writer = new XMLJobWriter();
            writer.save(job, statusController.getJobStatus(), statusController.getDone(), tasks, file);
        }
    }

    private void saveProgress(boolean auto) {
        lblSaving.setText("saving...");
        writeXMLTaskResults(
                auto);
        lastSave = statusController.getDone();
        lblSaving.setForeground(GREEN);
        lblSaving.setText("<html>" + lastSave + " saved<br>" + new DateTime(System.currentTimeMillis()).toString("hh:mm:ss") + "</html>");
    }

    private void tookbarMouseWheelMoved(final MouseWheelEvent evt) {
        if (!statusController.getEditionStatus().equals(EditionStatus.EDITING)) {
            int notches = evt.getWheelRotation();
            if (notches < 0) {
                btnPrevious.doClick();
            } else { //down
                btnNext.doClick();
            }
        }
    }

    private void toolbarKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_I) {
            editingTgt.underlying().requestFocusInWindow();
        }
        if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
            btnNext.doClick();
        }
        if (evt.getKeyCode() == KeyEvent.VK_UP) {
            btnPrevious.doClick();
        }
        if (evt.getKeyCode() == KeyEvent.VK_HOME) {
            btnPreviousLast.doClick();
        }
        if (evt.getKeyCode() == KeyEvent.VK_END) {
            btnNextUndone.doClick();
        }
        if (evt.getKeyCode() == KeyEvent.VK_F10) {
            btnSave.doClick();
        }
        if (evt.getKeyCode() == KeyEvent.VK_C) {
            if (btnCopyContext.isEnabled()) {
                btnCopyContext.doClick();
            }
        }
        if (evt.getKeyCode() == KeyEvent.VK_A) {
            if (btnAcceptMT.isEnabled()) {
                btnAcceptMT.doClick();
            }
        }
        if (evt.getKeyCode() == KeyEvent.VK_R) {
            if (btnRevert.isEnabled()) {
                btnRevert.doClick();
            }
        }
        if (evt.getKeyCode() == KeyEvent.VK_D) {
            if (btnImpossible.isEnabled()) {
                btnImpossible.doClick();
            }
        }
        if (evt.getKeyCode() == KeyEvent.VK_B) {
            if (btnBind.isEnabled()) {
                btnBind.doClick();
            }
        }
    }

    private void btnCopyContextActionPerformed(java.awt.event.ActionEvent evt) {
        final EditableUnit editing = statusController.getEditingTask();
        if (editing != null) {
            if (statusController.getEditionStatus().equals(EditionStatus.EDITING)) {
                editingTgt.setSentence(txtContext.getSentence());
            } else {
                waitingPEStart.add(new WaitingUntilPostEditingStarts() {

                    public void started() {
                        editingTgt.setSentence(txtContext.getSentence());
                    }
                });
                editingTgt.underlying().requestFocus();
            }
        } else {
            btnNext.requestFocus();
        }
    }

    private void btnRevertActionPerformed(java.awt.event.ActionEvent evt) {
        final EditableUnit editing = statusController.getEditingTask();
        if (editing != null && statusController.getEditionStatus().equals(EditionStatus.EDITING) && editing.getUnitResults().size() > 0) {
            editingTgt.setSentence(editing.getTarget());
        }
    }

    private void btnAcceptMTActionPerformed(java.awt.event.ActionEvent evt) {

        final DateTime beforeAssessing = new DateTime(System.currentTimeMillis());
        final EditableUnit editing = statusController.getEditingTask();
        if (editing != null) {
            if (ContextHandler.skipAssessmentOnAutoAccept()) {
                skipAssessment();
            } else {
                getAssessment(editing.getSnapshot(editing.getOriginalTarget().toString()));
            }
            final DateTime afterAssessing = new DateTime(System.currentTimeMillis());
            statusController.acceptMT(beforeAssessing, afterAssessing, assessments);
            btnNext.doClick();

        }
    }

    private void btnImpossibleActionPerformed(java.awt.event.ActionEvent evt) {
        final EditableUnit editing = statusController.getEditingTask();
        if (editing != null) {
            if (ContextHandler.skipAssessmentOnDiscard()) {
                skipAssessment();
            } else {
                getAssessment(editing.getSnapshot(editing.getTarget().toString()));
            }
            statusController.discardCurrentMT(assessments);
            btnNext.doClick();
        }

    }

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {
        move(true, true);
        btnNext.requestFocus();
    }

    private void btnPreviousActionPerformed(java.awt.event.ActionEvent evt) {
        move(false, true);
        btnPrevious.requestFocus();
    }

    private void btnNextUndoneActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        move(true, false);
        btnNextUndone.requestFocus();
    }

    private void btnPreviousLastActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        move(false, false);
        btnPreviousLast.requestFocus();
    }

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        saveProgress(false);
        btnNext.requestFocus();
    }

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        closeForm();
        btnNext.requestFocus();
    }

    private void btnBindActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        if (btnBind.isSelected()) {
            for (final MultiKey key : scrollers) {
                final JScrollPane src = (JScrollPane) key.getKey(0);
                final JScrollPane tgt = (JScrollPane) key.getKey(1);
                src.getVerticalScrollBar().setModel(tgt.getVerticalScrollBar().getModel());
            }
        } else {
            for (final MultiKey key : scrollers) {
                final JScrollPane src = (JScrollPane) key.getKey(0);
                src.getVerticalScrollBar().setModel((BoundedRangeModel) key.getKey(2));
            }
        }

    }

    /**
     * Here we treat some of the relevant keystrokes.
     * Mostly the normal visible characters, but also delete and backspace (without ctrl) and TAB.
     * @param evt 
     */
    private void tgtSentenceKeyTyped(java.awt.event.KeyEvent evt) {
        if (!Character.isISOControl(evt.getKeyChar())) { //if it's a normal char
            ContextHandler.signalManager().fire(new PETKeystrokeEvent(evt.getKeyChar()));
        } else { // if it's a command
            if (!evt.isControlDown()) { // except for those prefixed by 'ctrl', as they cannot be reliably captured in KeyTyped handlers
                switch (evt.getKeyChar()) {
                    case KeyEvent.VK_DELETE:
                        ContextHandler.signalManager().fire(new PETCommandEvent(PETCommandEvent.CommandType.DELETE));
                        break;
                    case KeyEvent.VK_BACK_SPACE:
                        ContextHandler.signalManager().fire(new PETCommandEvent(PETCommandEvent.CommandType.BACKSPACE));
                        break;
                    case KeyEvent.VK_TAB:
                        ContextHandler.signalManager().fire(new PETKeystrokeEvent(evt.getKeyChar()));
                        break;
                }
            }

        }
    }

    /**
     * Here we treat some other keystrokes, be careful not to count things twice!
     * Here we treat the ctrl+ events as well as navigation without ctrl.
     * Navigation with ctrl for while is not distinguished, but it could be in the future.
     * @param evt 
     */
    private void tgtSentenceKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.isControlDown()) { // for instance the commands prefixed by 'ctrl'
            switch (evt.getKeyCode()) {
                case KeyEvent.VK_DOWN:
                    ContextHandler.signalManager().fire(new PETNavigationEvent(PETNavigationEvent.NavigationType.DOWN));
                    break;
                case KeyEvent.VK_UP:
                    ContextHandler.signalManager().fire(new PETNavigationEvent(PETNavigationEvent.NavigationType.UP));
                    break;
                case KeyEvent.VK_LEFT:
                    ContextHandler.signalManager().fire(new PETNavigationEvent(PETNavigationEvent.NavigationType.LEFT));
                    break;
                case KeyEvent.VK_RIGHT:
                    ContextHandler.signalManager().fire(new PETNavigationEvent(PETNavigationEvent.NavigationType.RIGHT));
                    break;
                case KeyEvent.VK_HOME:
                    ContextHandler.signalManager().fire(new PETNavigationEvent(PETNavigationEvent.NavigationType.HOME));
                    break;
                case KeyEvent.VK_END:
                    ContextHandler.signalManager().fire(new PETNavigationEvent(PETNavigationEvent.NavigationType.END));
                    break;
                case KeyEvent.VK_PAGE_DOWN:
                    ContextHandler.signalManager().fire(new PETNavigationEvent(PETNavigationEvent.NavigationType.PGDOWN));
                    break;
                case KeyEvent.VK_PAGE_UP:
                    ContextHandler.signalManager().fire(new PETNavigationEvent(PETNavigationEvent.NavigationType.PGUP));
                    break;
                case KeyEvent.VK_C:
                    ContextHandler.signalManager().fire(new PETCommandEvent(PETCommandEvent.CommandType.COPY));
                    break;
                case KeyEvent.VK_X:
                    ContextHandler.signalManager().fire(new PETCommandEvent(PETCommandEvent.CommandType.CUT));
                    break;
                case KeyEvent.VK_V:
                    ContextHandler.signalManager().fire(new PETCommandEvent(PETCommandEvent.CommandType.PASTE));
                    break;
                case KeyEvent.VK_Z:
                    ContextHandler.signalManager().fire(new PETCommandEvent(PETCommandEvent.CommandType.UNDO));
                    break;
                case KeyEvent.VK_Y:
                    ContextHandler.signalManager().fire(new PETCommandEvent(PETCommandEvent.CommandType.REDO));
                    break;
                case KeyEvent.VK_R:
                    ContextHandler.signalManager().fire(new PETEditOperationEvent(PETEditOperationEvent.EditOperation.REPLACE)); // TODO: this should be fired by the operations themselves rather than the keystroke logger
                    break;
                case KeyEvent.VK_I:
                    ContextHandler.signalManager().fire(new PETEditOperationEvent(PETEditOperationEvent.EditOperation.INSERT));
                    break;
                case KeyEvent.VK_D:
                    ContextHandler.signalManager().fire(new PETCommandEvent(PETCommandEvent.CommandType.DELETE));
                    break;
                case KeyEvent.VK_T:
                    ContextHandler.signalManager().fire(new PETEditOperationEvent(PETEditOperationEvent.EditOperation.TRIM));
                    break;
                case KeyEvent.VK_S:
                    ContextHandler.signalManager().fire(new PETEditOperationEvent(PETEditOperationEvent.EditOperation.SHIFT));
                    break;
            }
        } else {
            switch (evt.getKeyCode()) {
                case KeyEvent.VK_DOWN:
                    ContextHandler.signalManager().fire(new PETNavigationEvent(PETNavigationEvent.NavigationType.DOWN));
                    break;
                case KeyEvent.VK_UP:
                    ContextHandler.signalManager().fire(new PETNavigationEvent(PETNavigationEvent.NavigationType.UP));
                    break;
                case KeyEvent.VK_LEFT:
                    ContextHandler.signalManager().fire(new PETNavigationEvent(PETNavigationEvent.NavigationType.LEFT));
                    break;
                case KeyEvent.VK_RIGHT:
                    ContextHandler.signalManager().fire(new PETNavigationEvent(PETNavigationEvent.NavigationType.RIGHT));
                    break;
                case KeyEvent.VK_HOME:
                    ContextHandler.signalManager().fire(new PETNavigationEvent(PETNavigationEvent.NavigationType.HOME));
                    break;
                case KeyEvent.VK_END:
                    ContextHandler.signalManager().fire(new PETNavigationEvent(PETNavigationEvent.NavigationType.END));
                    break;
                case KeyEvent.VK_PAGE_DOWN:
                    ContextHandler.signalManager().fire(new PETNavigationEvent(PETNavigationEvent.NavigationType.PGDOWN));
                    break;
                case KeyEvent.VK_PAGE_UP:
                    ContextHandler.signalManager().fire(new PETNavigationEvent(PETNavigationEvent.NavigationType.PGUP));
                    break;
            }
        }

        ContextHandler.signalManager().fire(SignalAdapter.KEYSTROKE);

        try {
            int keycode = evt.getKeyCode();
            char keychar = evt.getKeyChar();

            if (keycode == KeyEvent.VK_UP || keycode == KeyEvent.VK_DOWN || keycode == KeyEvent.VK_LEFT || keycode == KeyEvent.VK_RIGHT) {
                ContextHandler.signalManager().fire(SignalAdapter.KEYSTYPED_ARROW);
            } else if (keycode == KeyEvent.VK_HOME || keycode == KeyEvent.VK_END || keycode == KeyEvent.VK_PAGE_DOWN || keycode == KeyEvent.VK_PAGE_UP) {
                ContextHandler.signalManager().fire(SignalAdapter.KEYSTYPED_JUMP);
            } else if (keycode == KeyEvent.VK_COPY) {
                ContextHandler.signalManager().fire(SignalAdapter.KEYSTYPED_COPY);
            } else if (keycode == KeyEvent.VK_PASTE) {
                ContextHandler.signalManager().fire(SignalAdapter.KEYSTYPED_PASTE);
            } else if (keycode == KeyEvent.VK_CUT) {
                ContextHandler.signalManager().fire(SignalAdapter.KEYSTYPED_CUT);
            } else if (keycode == KeyEvent.VK_DELETE) {
                ContextHandler.signalManager().fire(SignalAdapter.KEYSTYPED_DELETE);
            } else if (keycode == KeyEvent.VK_BACK_SPACE) {
                ContextHandler.signalManager().fire(SignalAdapter.KEYSTYPED_BACKSPACE);
            } else if (Character.isWhitespace(keychar)) {
                ContextHandler.signalManager().fire(SignalAdapter.KEYSTYPED_WHITE);
            } else if (Character.isISOControl(keychar)) {
                ContextHandler.signalManager().fire(SignalAdapter.KEYSTYPED_ISO);
            } else if (Character.isDigit(keychar)) {
                ContextHandler.signalManager().fire(SignalAdapter.KEYSTYPED_DIGIT);
            } else if (Character.isLetter(keychar)) {
                ContextHandler.signalManager().fire(SignalAdapter.KEYSTYPED_LETTER);
            } else if (Character.isDefined(keychar)) {
                ContextHandler.signalManager().fire(SignalAdapter.KEYSTYPED_SYMBOL);
            }
        } catch (final Exception e) {
            System.err.println("Excepetion caught: " + e);
        }
    }

    private void tgtSentenceKeyReleased(java.awt.event.KeyEvent evt) {
    }

    private void tgtSentenceFocusGained(java.awt.event.FocusEvent evt) {
        // changes to 'editing' status if the task is ready for edition
        if (statusController.getEditionStatus() == EditionStatus.READY_TO_EDIT) {
            ContextHandler.flowManager().editingIsAboutToStart();
            statusController.editing();
            ContextHandler.flowManager().editingHasStarted();
            for (final WaitingUntilPostEditingStarts waiter : waitingPEStart) {
                waiter.started();
            }
            waitingPEStart.clear();
        }
    }

    private void closeForm() {
        if (UnitHandler.getEditingStatusProvider().getEditionStatus() == EditionStatus.EDITING) {
            final int saveUnit = JOptionPane.showConfirmDialog(this, "Do you want to keep the current unit?", "Closing", JOptionPane.YES_NO_OPTION);
            if (saveUnit == JOptionPane.YES_OPTION) {
                move(true, true);
            }

        }
        if (!autoSave) {
            int option = JOptionPane.showConfirmDialog(this, "Would you like to save your progress?", "Closing", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (option == JOptionPane.YES_OPTION) {
                saveProgress(false);
                userSpaceController.updateUserSpace();
                dispose();
            } else if (option == JOptionPane.NO_OPTION) {
                userSpaceController.updateUserSpace();
                dispose();
            }
        } else {
            saveProgress(false);
            userSpaceController.updateUserSpace();
            dispose();
        }

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    //@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        //setDefaultLookAndFeelDecorated(true);
        setMinimumSize(new Dimension(600, 300));
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        //setUndecorated(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        closeForm();
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                // new AnnotationPage3().setVisible(true);
            }
        });


    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
