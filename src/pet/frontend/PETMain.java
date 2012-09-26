/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PostEMain.java
 *
 * This is where everything starts for the interface.
 * 
 * Created on 16-Nov-2010, 10:55:15
 */
package pet.frontend;

import java.awt.GraphicsEnvironment;
import pet.annotation.xml.PETParseException;
import pet.usr.adapter.EditableUnit;
import pet.usr.adapter.FileAdapter;
import pet.usr.adapter.UserSpaceController;
import pet.annotation.TaskManager;
import pet.annotation.adapter.TaskManagerAdapter;
import pet.usr.handler.SettingsHandler;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import pet.annotation.Job;
import pet.config.BackGroundReader;
import pet.config.ConfigReader;
import pet.config.ConfigWriter;
import pet.config.ContextHandler;
import pet.config.MetaHandler;
import pet.frontend.util.VisualLogger;
import pet.io.XMLJobReader;

/**
 * PETMain is the JFrame that renders PET's starting page (or main page).
 * I intend to change a bit its structure and how it interacts with other objects,
 * but for the time being this is the right place to start following the code in
 * its execution sequence.
 * 
 * 
 * @author waziz
 */
public class PETMain extends javax.swing.JFrame implements UserSpaceController {

    private TaskManager manager;
    private boolean frozen = false;

    /**
     * This is the single constructor of PETMain.
     * It initializes the graphical components and it's also responsible
     * for the loading of configuration files.
     * @param pecmeta FileAdapter to the meta context file
     */
    public PETMain(final FileAdapter pecmeta) {
        // standard java beans stuff
        initComponents();
        
        UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
        
        // just fixing the focus 
        btnUpdate.requestFocusInWindow();
        
        // TODO: implement it or get rid of it
        btnVisualize.setVisible(false);

        // Initializes the visual logger, a text are that shows the progress
        // of file loading/parsing and things like that
        VisualLogger.initialize(txtLog);

        // Load the meta context file and lists the available contexts
        // this initializes the MetaHandler
        updatePECOptions(pecmeta);

        // Load a context file (if selected)
        loadPEC();
    }

    /**
     * Prepares the interface and call the method that loads the meta context file
     * @param pecmeta 
     */
    private void updatePECOptions(final FileAdapter pecmeta) {
        // the meta configuration file is expected to be called "pec.meta"
        VisualLogger.warn("Loading " + pecmeta.getFile().getAbsolutePath());
        MetaHandler.initialize(pecmeta);
        
        final String pecDefault = MetaHandler.pecDefault();
        
        // check for existance of a default .pec and update the interface
        if (pecDefault == null) { 
            VisualLogger.warn("a default .pec was not given, PET will load the first .pec file available");
            btnDefault.setEnabled(false);
        } else {
            VisualLogger.info("PET will default to " + MetaHandler.pecDir() + File.separator + pecDefault);
            btnDefault.setEnabled(true);
        }

        // Lists the available contexts (i.e. *.pec files)
        final File dir = new File(MetaHandler.pecDir());
        final List<File> pecFiles = pet.usr.handler.FileHandler.getConfigFiles(dir);
        if (!pecFiles.isEmpty()) {
            final DefaultComboBoxModel pecModel = new DefaultComboBoxModel();
            final List<FileAdapter> adapters = new ArrayList<FileAdapter>();
            for (final File pec : pecFiles) {
                if (pecDefault != null && pecDefault.equals(pec.getName())) {
                    pecModel.addElement(new FileAdapter(pec));
                } else {
                    adapters.add(new FileAdapter(pec));
                }
            }
            Collections.sort(adapters);
            for (final FileAdapter adapter : adapters) {
                pecModel.addElement(adapter);
            }
            comConfig.setModel(pecModel);

        } else {
            comConfig.removeAll();
            JOptionPane.showMessageDialog(this, "No pec file available in the workspace. Check pec.meta and provide a .pec file.", "Loading parameters", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Returns the selected context if any
     * @return 
     */
    private FileAdapter getSelectedPECFile() {
        final Object obj = comConfig.getSelectedItem();
        if (obj != null) {
            return (FileAdapter) obj;
        } else {
            return null;
        }
    }

    /**
     * Loads the selected context file if any
     */
    private void loadPEC() {
        final FileAdapter config = getSelectedPECFile();
        if (config == null || !config.getFile().exists()) {
            JOptionPane.showMessageDialog(null, "There wasn't a config file. Check '" + MetaHandler.pecExample() + "' for an example.");
            try {
                ConfigWriter.example(new File(MetaHandler.pecExample()));
            } catch (final IOException e) {
                JOptionPane.showMessageDialog(null, "Impossible to create the example config file: " + MetaHandler.pecExample());
            }
            System.exit(0);
        }

        VisualLogger.line();
        VisualLogger.processing("Loading context: " + config);

        // lock everything to which the user could interact affecting the loading procedure
        lockInteraction();
        
        // Constructs a config reader and delegates the loading/parsing of files to a BackGroundReader
        // this is only necessary to keep Swing alive/responsive while heavy parsing is going on
        new BackGroundReader(new ConfigReader(config), new BackGroundReader.BackGroundReaderListener() {

            @Override
            /**
             * This method is only called once things are done, 
             * here the interface is updated (including unlocking the buttons)
             */
            public void configLoaded(final ConfigReader reader) {
                
                // the initialization of the ContextHandler has to be done in the Main thread 
                // otherwise as a Handler, the objects would be global to a different thread
                // threfore inaccessible from where we want to use it
                reader.initializeHandler();
                
                workspace.setText(ContextHandler.workspace());
                chkTimeStamp.setSelected(ContextHandler.outputTimeStamp());
                chkTimeStamp.setEnabled(ContextHandler.showOutputTimeStampCheckBox());

                updateUserContext(ContextHandler.defaultUser());
                VisualLogger.done("Context '" + config + "' loaded.");
                VisualLogger.log("Context: " + getSelectedPECFile() + System.getProperty("line.separator")
                        + "Workspace: " + ContextHandler.workspace() + System.getProperty("line.separator")
                        + "Default user: " + ContextHandler.defaultUser());
                
                // unlock things
                unlockInteraction();
            }
        }).execute();
        //ConfigReader.read(config);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        workspace = new javax.swing.JTextField();
        lblWorkspace = new javax.swing.JLabel();
        btnUpdate = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jobs = new javax.swing.JList();
        lblJobs = new javax.swing.JLabel();
        btnStart = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        results = new javax.swing.JList();
        btnEdit = new javax.swing.JButton();
        btnVisualize = new javax.swing.JButton();
        lblJobs1 = new javax.swing.JLabel();
        chkTimeStamp = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        comConfig = new javax.swing.JComboBox();
        btnDefault = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtLog = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("PET");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        workspace.setEditable(false);

        lblWorkspace.setText("Workspace");

        btnUpdate.setText("User");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        jobs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jobsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jobs);

        lblJobs.setText("New Jobs");

        btnStart.setText("Start");
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });

        results.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resultsMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(results);

        btnEdit.setText("Edit");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnVisualize.setText("Visualize");
        btnVisualize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVisualizeActionPerformed(evt);
            }
        });

        lblJobs1.setText("Results");

        chkTimeStamp.setText("Output timestamp");
        chkTimeStamp.setToolTipText("If you check it output files will have a timestamp as a sufix");

        jLabel1.setText("Configuration");

        comConfig.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comConfigItemStateChanged(evt);
            }
        });
        comConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comConfigActionPerformed(evt);
            }
        });

        btnDefault.setText("Default");
        btnDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDefaultActionPerformed(evt);
            }
        });

        jLabel2.setText("Log");

        jScrollPane3.setViewportView(txtLog);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblWorkspace, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(comConfig, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(workspace, javax.swing.GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                                    .addComponent(btnDefault, 0, 0, Short.MAX_VALUE)))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 814, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(chkTimeStamp)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 198, Short.MAX_VALUE)
                                        .addComponent(btnStart))
                                    .addComponent(lblJobs)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblJobs1)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(btnVisualize)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnEdit)))))
                        .addGap(13, 13, 13)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnEdit, btnStart, btnVisualize});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(comConfig, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(lblWorkspace)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(workspace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnUpdate)))
                    .addComponent(btnDefault))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblJobs)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblJobs1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnEdit)
                    .addComponent(btnVisualize)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnStart)
                        .addComponent(chkTimeStamp)))
                .addGap(7, 7, 7)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnEdit, btnStart, btnVisualize});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Updates the interface for a new user
     * @param evt 
     */
    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed

        final String user = JOptionPane.showInputDialog(this, "Annotator:", "User", JOptionPane.OK_OPTION);
        if (user == null || user.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Annotator's id required", "Information missing", JOptionPane.ERROR_MESSAGE);
        } else {
            updateUserContext(user);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    /**
     * Locks a few interactable components
     */
    private void lockInteraction() {
        frozen = true;
        btnDefault.setEnabled(false);
        btnEdit.setEnabled(false);
        btnStart.setEnabled(false);
        btnUpdate.setEnabled(false);
        btnVisualize.setEnabled(false);
        comConfig.setEnabled(false);
    }

    /**
     * Unlocks a few interactable components
     */
    private void unlockInteraction() {
        frozen = false;
        btnDefault.setEnabled(true);
        btnEdit.setEnabled(true);
        btnStart.setEnabled(true);
        btnUpdate.setEnabled(true);
        btnVisualize.setEnabled(true);
        comConfig.setEnabled(true);
    }

    /**
     * This initializes SettingsHandler.
     * TODO: look into the actual need for a SettingsHandler
     * @param user the user id
     */
    private void updateUserContext(final String user) {
        jobs.setModel(new DefaultListModel());
        results.setModel(new DefaultListModel());
        if (user != null && !user.trim().isEmpty()) {
            final File userSpaceDir = new File(ContextHandler.workspace() + File.separator + user);
            if (!userSpaceDir.exists()) {
                userSpaceDir.mkdir();
                JOptionPane.showMessageDialog(this, "A user space has been created", "Setting working enviroment", JOptionPane.INFORMATION_MESSAGE);
            }
            SettingsHandler.release();
            SettingsHandler.initialize(ContextHandler.workspace(), user); //, new EditableTask.EditableTaskFactory());
            updateUserSpace();
        }
    }

    @Override
    /**
     * Lists the jobs available to the current user
     */
    public void updateUserSpace() {
        final String user = SettingsHandler.getUser();
        final File workspaceDir = new File(SettingsHandler.getWorkspace());
        final File userSpaceDir = new File(workspaceDir.getPath() + File.separator + user);
        this.setTitle("PET - " + user);
        final List<File> jobFiles = pet.usr.handler.FileHandler.getJobFiles(userSpaceDir);
        final List<File> resultFiles = pet.usr.handler.FileHandler.getResultFiles(userSpaceDir);
        if (!jobFiles.isEmpty()) {
            final DefaultListModel jobListModel = new DefaultListModel();
            final List<FileAdapter> adapters = new ArrayList<FileAdapter>();
            for (final File job : jobFiles) {
                adapters.add(new FileAdapter(job));
            }
            Collections.sort(adapters);
            for (final FileAdapter adapter : adapters) {
                jobListModel.addElement(adapter);
            }
            jobs.setModel(jobListModel);

        } else {
            jobs.removeAll();
            JOptionPane.showMessageDialog(this, "No jobs available in the workspace", "Loading jobs", JOptionPane.INFORMATION_MESSAGE);
        }
        if (!resultFiles.isEmpty()) {
            final DefaultListModel resultListModel = new DefaultListModel();
            final List<FileAdapter> adapters = new ArrayList<FileAdapter>();
            for (final File result : resultFiles) {
                adapters.add(new FileAdapter(result));
            }
            Collections.sort(adapters);
            for (final FileAdapter adapter : adapters) {
                resultListModel.addElement(adapter);
            }
            results.setModel(resultListModel);

        } else {
            results.removeAll();
        }

    }

    /**
     * Starts the selected job.
     * This method calls the creation of the annotation page
     */
    private void startJob() {
        if (!frozen && jobs.getSelectedIndex() >= 0) {
            final FileAdapter pej = (FileAdapter) jobs.getSelectedValue();
            final String per = pej.getFile().getAbsolutePath().replaceAll("pej$", "per");
            int option = JOptionPane.YES_OPTION;
            if (new File(per).exists()) {
                option = JOptionPane.showConfirmDialog(this, "This file has been started, proceeding may overwrite the partial results. Do you want to proceed?");
            }
            if (option == JOptionPane.YES_OPTION) {
                try {
                    manager = new TaskManagerAdapter(SettingsHandler.getUser(), ((FileAdapter) jobs.getSelectedValue()).getFile());
                    if (!manager.getJobs().isEmpty()) {
                        createAnnotationPage(manager.getJobs().iterator().next(), null).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "The job couldn't be loaded.", "Loading", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (final PETParseException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), ex.getType(), JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Continue a .per
     */
    private void continueJob() {
        if (!frozen && results.getSelectedIndex() >= 0) {
            try {
                manager = new TaskManagerAdapter(SettingsHandler.getUser(), ((FileAdapter) results.getSelectedValue()).getFile());
                final List<pet.annotation.Unit> tasks = new ArrayList<pet.annotation.Unit>();
                //PlainTextJobReader reader = new PlainTextJobReader(ContextHandler.assessmentSeparator());
                final XMLJobReader reader = new XMLJobReader();
                final pet.annotation.Job job = reader.readEditedJob(((FileAdapter) results.getSelectedValue()).getFile(), tasks);
                if (job == null) {
                    JOptionPane.showMessageDialog(this, "Problems reading the job", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!manager.getJobs().isEmpty()) {
                    createAnnotationPage(job, getEditableTasks(tasks)).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "The job couldn't be loaded.", "Loading", JOptionPane.ERROR_MESSAGE);
                }
            } catch (final PETParseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), ex.getType(), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        SettingsHandler.release();
    }//GEN-LAST:event_formWindowClosing

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
        startJob();
    }//GEN-LAST:event_btnStartActionPerformed

    /**
     * Actually creeates the annotation page (BorderLayoutAnnotationPage)
     * @param job 
     * @param tasks
     * @return 
     */
    private JFrame createAnnotationPage(final Job job, final List<EditableUnit> tasks) {
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final JFrame page = new BorderLayoutAnnotationPage(job, tasks, this, chkTimeStamp.isSelected());
        page.setMaximizedBounds(ge.getMaximumWindowBounds());
        page.setExtendedState(page.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        return page;
    }

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        continueJob();
    }//GEN-LAST:event_btnEditActionPerformed

    private List<EditableUnit> getEditableTasks(final List<pet.annotation.Unit> tasks) {
        final List<EditableUnit> editable = new ArrayList<EditableUnit>(tasks.size());
        for (final pet.annotation.Unit task : tasks) {
            editable.add((EditableUnit) task);
        }
        return editable;
    }

    private void btnVisualizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVisualizeActionPerformed

        if (results.getSelectedIndex() >= 0) {
            try {
                manager = new TaskManagerAdapter(SettingsHandler.getUser(), ((FileAdapter) results.getSelectedValue()).getFile());
                final List<pet.annotation.Unit> tasks = new ArrayList<pet.annotation.Unit>();
                //PlainTextJobReader reader = new PlainTextJobReader(ContextHandler.assessmentSeparator());
                final XMLJobReader reader = new XMLJobReader();
                final pet.annotation.Job job = reader.readEditedJob(((FileAdapter) results.getSelectedValue()).getFile(), tasks);
                if (!manager.getJobs().isEmpty()) {
                    createAnnotationPage(job, getEditableTasks(tasks)).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "The job couldn't be loaded.", "Loading", JOptionPane.ERROR_MESSAGE);
                }
            } catch (final PETParseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), ex.getType(), JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnVisualizeActionPerformed

    private void jobsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jobsMouseClicked
        // TODO add your handling code here:
        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
            startJob();
        }
    }//GEN-LAST:event_jobsMouseClicked

    private void resultsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resultsMouseClicked
        // TODO add your handling code here:
        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
            continueJob();
        }
    }//GEN-LAST:event_resultsMouseClicked

private void btnDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDefaultActionPerformed
// TODO add your handling code here:
    if (MetaHandler.pecDefault() != null) {
        comConfig.setSelectedIndex(0);
    }
}//GEN-LAST:event_btnDefaultActionPerformed

private void comConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comConfigActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_comConfigActionPerformed

private void comConfigItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comConfigItemStateChanged
// TODO add your handling code here:
    if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
        if (!((FileAdapter) evt.getItem()).toString().equals(ContextHandler.contextId())) {
            loadPEC();
        }

    }
}//GEN-LAST:event_comConfigItemStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        String path = System.getProperty("user.dir") + File.separator + "pec.meta";
        if (args.length == 1){
            if (new File(args[0]).isAbsolute()){
                path = args[0];
            } else{
                path = System.getProperty("user.dir") + File.separator + args[0];
            }
        }
        final FileAdapter pecmeta = new FileAdapter(new File(path));
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new PETMain(pecmeta).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDefault;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnStart;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnVisualize;
    private javax.swing.JCheckBox chkTimeStamp;
    private javax.swing.JComboBox comConfig;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList jobs;
    private javax.swing.JLabel lblJobs;
    private javax.swing.JLabel lblJobs1;
    private javax.swing.JLabel lblWorkspace;
    private javax.swing.JList results;
    private javax.swing.JTextPane txtLog;
    private javax.swing.JTextField workspace;
    // End of variables declaration//GEN-END:variables
}
