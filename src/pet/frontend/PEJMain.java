/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PEJMain.java
 *
 * Created on 02-Jul-2012, 11:35:22
 */
package pet.frontend;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import pet.pej.FormatException;
import pet.pej.PEJAttribute;
import pet.pej.PEJBuilder;
import pet.pej.PEJSegment.SegmentType;

/**
 *
 * @author waziz
 */
public class PEJMain extends javax.swing.JFrame {

    private File lastLocation = new File(System.getProperty("user.dir"));

    private static class TextFromOneProducer {

        private final SegmentType type;
        private final File file;
        private final String producer;

        public TextFromOneProducer(final SegmentType type, final File file, final String producer) {
            this.type = type;
            this.file = file;
            this.producer = producer;
        }

        public File getFile() {
            return file;
        }

        public String getProducer() {
            return producer;
        }

        public SegmentType getType() {
            return type;
        }

        @Override
        public String toString() {
            return type + ": " + file + " (" + producer + ")";
        }
    }

    private static class TextFromMultipleProducers {

        private final SegmentType type;
        private final File file;
        private final File meta;

        public TextFromMultipleProducers(final SegmentType type, final File file, final File meta) {
            this.type = type;
            this.file = file;
            this.meta = meta;
        }

        public File getFile() {
            return file;
        }

        public File getMeta() {
            return meta;
        }

        public SegmentType getType() {
            return type;
        }

        @Override
        public String toString() {
            return type + ": " + file + " (" + meta + ")";
        }
    }
    final List<File> sources;
    final List<File> targets;
    final List<File> references;
    final List<File> metas;
    final DefaultListModel model;
    final DefaultListModel attrs;

    /** Creates new form PEJMain */
    public PEJMain() {
        initComponents();

        lblInfo.setText("");
        sources = new ArrayList<File>();
        targets = new ArrayList<File>();
        references = new ArrayList<File>();
        metas = new ArrayList<File>();
        model = new DefaultListModel();
        attrs = new DefaultListModel();
        lstFiles.setModel(model);
        lstJob.setModel(attrs);
    }

    private void dialogs(final SegmentType type, final String alias) {

        final JFileChooser mainChooser = new JFileChooser();
        mainChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        mainChooser.setMultiSelectionEnabled(false);
        mainChooser.setCurrentDirectory(lastLocation);
        int mainStatus = mainChooser.showDialog(this, "Select the " + alias + " file");
        if (mainStatus == JFileChooser.APPROVE_OPTION) {
            final File selected = mainChooser.getSelectedFile();
            if (!lastLocation.equals(selected)) {
                lastLocation = selected;
            }
            final int option = JOptionPane.showConfirmDialog(this, "Do you have a meta file (with attributes) for this?");
            if (option == JOptionPane.YES_OPTION) {
                final JFileChooser metaChooser = new JFileChooser();
                metaChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                metaChooser.setMultiSelectionEnabled(false);
                metaChooser.setCurrentDirectory(lastLocation);
                int metaStatus = metaChooser.showDialog(this, "Select the meta file");
                if (metaStatus == JFileChooser.APPROVE_OPTION) {
                    final File meta = metaChooser.getSelectedFile();
                    if (!lastLocation.equals(meta)) {
                        lastLocation = meta;
                    }
                    model.addElement(new TextFromMultipleProducers(type, selected, meta));
                }
            } else {
                final String producer = JOptionPane.showInputDialog(this, "Producer (required):");
                if (producer != null) {
                    model.addElement(new TextFromOneProducer(type, selected, producer));
                }
            }
        }
    }

    private String getId() {
        for (int i = 0; i < attrs.size(); i++) {
            final PEJAttribute attr = (PEJAttribute) attrs.get(i);
            if (attr.getKey().equals("id")) {
                return attr.getValue();
            }
        }
        String id = null;
        final String suggestion = new File(txtOutput.getText()).getName().replaceAll("\\.pej$", "");
        do {
            id = JOptionPane.showInputDialog(this, "Job's id:", suggestion);
        } while (id == null || id.isEmpty());
        return id;
    }

    private List<PEJAttribute> getJobsAttributes() {
        final List<PEJAttribute> attributes = new ArrayList<PEJAttribute>(model.size());
        for (int i = 0; i < attrs.size(); i++) {
            attributes.add((PEJAttribute) attrs.get(i));
        }
        return attributes;
    }

    private int save(final String output) {
        try {
            final PEJBuilder builder = new PEJBuilder(output, getId());
            builder.addJobAttributes(getJobsAttributes());
            if (!txtUnitsMeta.getText().isEmpty()) {
                builder.loadUnitsAttributesFromFile(txtUnitsMeta.getText());
            }
            final int missing = builder.isMissingIds();
            if (missing == builder.getNUnits()) {
                builder.idUnitsSequentially();
            } else if (missing > 0) {
                int option = JOptionPane.showConfirmDialog(this, "At least " + missing + " units miss their ids. Do you want me to redefine all units' ids with sequential integers?", "Units must have ids", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    builder.idUnitsSequentially();
                } else {
                    JOptionPane.showMessageDialog(this, "Then you must fix " + txtUnitsMeta.getText());
                }
            }
            for (int i = 0; i < model.getSize(); i++) {
                final Object obj = model.get(i);
                if (obj instanceof TextFromOneProducer) {
                    final TextFromOneProducer input = (TextFromOneProducer) obj;
                    builder.addSegmentsFromOneProducer(input.getType(), input.getFile(), input.getProducer());
                } else {
                    final TextFromMultipleProducers input = (TextFromMultipleProducers) obj;
                    builder.addSegmentsWithAttributes(input.getType(), input.getFile(), input.getMeta());
                }
            }
            builder.build();
            return builder.getNUnits();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(PEJMain.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Impossible to create the xml object: " + ex);
        } catch (TransformerException ex) {
            Logger.getLogger(PEJMain.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Impossible to create the xml object: " + ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PEJMain.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "File not found: " + ex);
        } catch (FormatException ex) {
            Logger.getLogger(PEJMain.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(PEJMain.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, ex);
        }
        return 0;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        lstFiles = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        btnAddS = new javax.swing.JButton();
        btnAddMT = new javax.swing.JButton();
        btnAddR = new javax.swing.JButton();
        btnPEJ = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtOutput = new javax.swing.JTextField();
        txtUnitsMeta = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstJob = new javax.swing.JList();
        btnUnits = new javax.swing.JButton();
        btnJobAsttr = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        btnDo = new javax.swing.JButton();
        lblInfo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("PEJ");

        lstFiles.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstFiles.setToolTipText("Click twice to remove an entry");
        lstFiles.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstFilesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(lstFiles);

        jLabel1.setForeground(new java.awt.Color(250, 21, 21));
        jLabel1.setText("Files");

        btnAddS.setText("+ S");
        btnAddS.setToolTipText("Add a file or sources");
        btnAddS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddSActionPerformed(evt);
            }
        });

        btnAddMT.setText("+ MT");
        btnAddMT.setToolTipText("Add a file of draft translations");
        btnAddMT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddMTActionPerformed(evt);
            }
        });

        btnAddR.setText("+ R");
        btnAddR.setToolTipText("Add a file or references");
        btnAddR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddRActionPerformed(evt);
            }
        });

        btnPEJ.setText("PEJ");
        btnPEJ.setToolTipText("Set the output file");
        btnPEJ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPEJActionPerformed(evt);
            }
        });

        jLabel2.setForeground(new java.awt.Color(250, 21, 21));
        jLabel2.setText("Output");

        txtOutput.setEditable(false);

        txtUnitsMeta.setEditable(false);
        txtUnitsMeta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUnitsMetaActionPerformed(evt);
            }
        });

        jLabel3.setText("Job's attributes");

        lstJob.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstJob.setToolTipText("Click twice to remove an entry");
        lstJob.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstJobMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(lstJob);

        btnUnits.setText("units");
        btnUnits.setToolTipText("Add a meta file of attributes for units");
        btnUnits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUnitsActionPerformed(evt);
            }
        });

        btnJobAsttr.setText("+attr");
        btnJobAsttr.setToolTipText("Add one or more attributes to the Job");
        btnJobAsttr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJobAsttrActionPerformed(evt);
            }
        });

        jLabel4.setText("Units' attributes");

        btnDo.setText("Do");
        btnDo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDoActionPerformed(evt);
            }
        });

        lblInfo.setText("jLabel5");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
                            .addComponent(txtOutput, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
                            .addComponent(txtUnitsMeta, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnDo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnAddS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnAddR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnAddMT))
                            .addComponent(btnPEJ, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnJobAsttr, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnUnits)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(btnJobAsttr)))
                .addGap(2, 2, 2)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtUnitsMeta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnUnits))
                        .addGap(5, 5, 5)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, 0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(115, 115, 115)
                        .addComponent(btnAddS)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddR)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddMT)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtOutput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPEJ))
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnDo)
                    .addComponent(lblInfo))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddSActionPerformed
        // TODO add your handling code here:
        dialogs(SegmentType.S, "source");
    }//GEN-LAST:event_btnAddSActionPerformed

    private void btnAddRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddRActionPerformed
        // TODO add your handling code here:
        dialogs(SegmentType.R, "reference");
    }//GEN-LAST:event_btnAddRActionPerformed

    private void btnAddMTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddMTActionPerformed
        // TODO add your handling code here:
        dialogs(SegmentType.T, "pre-translated");
    }//GEN-LAST:event_btnAddMTActionPerformed

    private void btnPEJActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPEJActionPerformed
        // TODO add your handling code here:
        final JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        chooser.setCurrentDirectory(lastLocation);
        chooser.addChoosableFileFilter(new FileFilter() {

            @Override
            public boolean accept(File file) {
                return file.getAbsolutePath().endsWith(".pej");
            }

            @Override
            public String getDescription() {
                return "PET Jobs (.pej)";
            }
        });
        int status = chooser.showSaveDialog(this);
        if (status == JFileChooser.APPROVE_OPTION) {
            final File selected = chooser.getSelectedFile();
            if (!lastLocation.equals(selected)) {
                lastLocation = selected;
            }
            if (selected.getAbsolutePath().endsWith(".pej")) {
                txtOutput.setText(selected.getAbsolutePath());
            } else {
                txtOutput.setText(selected.getAbsolutePath() + ".pej");
            }
        }
    }//GEN-LAST:event_btnPEJActionPerformed

    private void txtUnitsMetaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUnitsMetaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUnitsMetaActionPerformed

    private void btnUnitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUnitsActionPerformed
        // TODO add your handling code here:
        final JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        chooser.setCurrentDirectory(lastLocation);
        int status = chooser.showDialog(this, "Select a file containing the units' attributes");
        if (status == JFileChooser.APPROVE_OPTION) {
            final File selected = chooser.getSelectedFile();
            if (!lastLocation.equals(selected)) {
                lastLocation = selected;
            }
            txtUnitsMeta.setText(selected.getAbsolutePath());
        }

    }//GEN-LAST:event_btnUnitsActionPerformed

    private void btnJobAsttrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJobAsttrActionPerformed
        // TODO add your handling code here:
        final String input = JOptionPane.showInputDialog(this, "Space separated list of pairs: <key>=<value>");
        if (input != null) {
            String[] pairs = input.split("\\s+");
            for (int i = 0; i < pairs.length; i++) {
                String[] pair = pairs[i].split("=");
                if (pair.length == 2) {
                    attrs.addElement(new PEJAttribute(pair[0], pair[1]));
                }
            }
        }
    }//GEN-LAST:event_btnJobAsttrActionPerformed

    private void lstJobMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstJobMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            final int selected = lstJob.getSelectedIndex();
            if (selected >= 0) {
                attrs.remove(selected);
            }
        }
    }//GEN-LAST:event_lstJobMouseClicked

    private void lstFilesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstFilesMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            final int selected = lstFiles.getSelectedIndex();
            if (selected >= 0) {
                model.remove(selected);
            }
        }
    }//GEN-LAST:event_lstFilesMouseClicked

    private void btnDoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDoActionPerformed
        // TODO add your handling code here:
        if (!txtOutput.getText().isEmpty() && model.getSize() != 0) {
            final String output = txtOutput.getText();
            int units = save(output);
            if (units >= 0) {
                model.clear();
                attrs.clear();
                txtUnitsMeta.setText("");
                txtOutput.setText("");
                lblInfo.setForeground(Color.blue);
                lblInfo.setText(units + " units saved to " + output);
            } else {
                lblInfo.setForeground(Color.red);
                lblInfo.setText("Revise the settings!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "'Files' and 'Output' are mandatory!");
        }
    }//GEN-LAST:event_btnDoActionPerformed

    private static List<PEJAttribute> getAttributes(final String args[]) {
        final List<PEJAttribute> attrs = new ArrayList<PEJAttribute>();
        for (int i = 0; i < args.length; i += 2) {
            attrs.add(new PEJAttribute(args[i], args[i + 1]));
        }
        return attrs;
    }

    private static List<List<String>> groupTuples(final int n, final String args[]) {
        final List<List<String>> groups = new ArrayList<List<String>>();
        for (int i = 0; i < args.length; i += n) {
            final List<String> group = new ArrayList<String>(n);
            for (int j = 0; j < n; j++) {
                group.add(args[i + j]);
            }
            groups.add(group);
        }
        return groups;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(final String args[]) {
        System.nanoTime();
        if (args.length != 0) {

            final Options options = new Options();

            options.addOption(OptionBuilder.withArgName("identifier").hasArg().isRequired().withDescription("Job identifier - determines the name of the PER that will be produced out of this PEJ.").create("id"));
            options.addOption(OptionBuilder.withArgName("file").hasArg().isRequired().withDescription("Path to the resulting PEJ").create("pej"));

            options.addOption(OptionBuilder.withLongOpt("help").withDescription("Print these instructions").create("h"));
            options.addOption(OptionBuilder.withDescription("Sequential ids (overwrites ids coming from -units)").create("seq"));

            options.addOption(OptionBuilder.withArgName("segments producer").hasArgs(2).withDescription("Adds source segments from a single producer [multiple allowed]").create("s"));
            options.addOption(OptionBuilder.withArgName("segments attributes").hasArgs(2).withDescription("Adds source segments specifying a file with attributes (which should include the producer) [multiple allowed]").create("S"));

            options.addOption(OptionBuilder.withArgName("segments producer").hasArgs(2).withDescription("Adds reference segments from a single producer [multiple allowed]").create("r"));
            options.addOption(OptionBuilder.withArgName("segments attributes").hasArgs(2).withDescription("Adds reference segments specifying a file with attributes (which should include the producer) [multiple allowed]").create("R"));

            options.addOption(OptionBuilder.withArgName("segments producer").hasArgs(2).withDescription("Adds target segments from a single producer [multiple allowed]").create("t"));
            options.addOption(OptionBuilder.withArgName("segments attributes").hasArgs(2).withDescription("Adds target segments specifying a file with attributes (which should include the producer) [multiple allowed]").create("T"));

            options.addOption(OptionBuilder.withArgName("file").hasArg().withDescription("File containing attributes of the units [multiple allowed]").create("units"));


            options.addOption(OptionBuilder.withArgName("property=value").hasArgs(2).withValueSeparator().withDescription("Add a property to a job [multiple allowed]").create("D"));
            options.addOption(OptionBuilder.withArgName("tsv").hasArg().isRequired().withDescription("Tab separated file: segid sysid source MT").create("tsv"));

            HelpFormatter formatter = new HelpFormatter();


            final CommandLineParser parser = new GnuParser();
            try {
                final CommandLine line = parser.parse(options, args);
                if (line.hasOption("h")) {
                    formatter.printHelp("pej", options);
                    System.exit(0);
                }

                try {
                    final PEJBuilder builder = new PEJBuilder(line.getOptionValue("pej"), line.getOptionValue("id"));

                    if (line.hasOption("D")) {
                        builder.addJobAttributes(getAttributes(line.getOptionValues("D")));
                    }

                    if (line.hasOption("units")) {
                        for (final String unit : line.getOptionValues("units")) {
                            builder.loadUnitsAttributesFromFile(unit);
                        }
                    }

                    if (line.hasOption("seq")) {
                        builder.idUnitsSequentially();
                    }

                    if (line.hasOption("s")) {
                        final List<List<String>> groups = groupTuples(2, line.getOptionValues("s"));
                        for (final List<String> group : groups) {
                            builder.addSegmentsFromOneProducer(SegmentType.S, new File(group.get(0)), group.get(1));
                        }
                    }
                    if (line.hasOption("S")) {
                        final List<List<String>> groups = groupTuples(2, line.getOptionValues("S"));
                        for (final List<String> group : groups) {
                            builder.addSegmentsWithAttributes(SegmentType.S, new File(group.get(0)), new File(group.get(1)));
                        }
                    }
                    if (line.hasOption("r")) {
                        final List<List<String>> groups = groupTuples(2, line.getOptionValues("r"));
                        for (final List<String> group : groups) {
                            builder.addSegmentsFromOneProducer(SegmentType.R, new File(group.get(0)), group.get(1));
                        }
                    }
                    if (line.hasOption("R")) {
                        final List<List<String>> groups = groupTuples(2, line.getOptionValues("R"));
                        for (final List<String> group : groups) {
                            builder.addSegmentsWithAttributes(SegmentType.R, new File(group.get(0)), new File(group.get(1)));
                        }
                    }
                    if (line.hasOption("t")) {
                        final List<List<String>> groups = groupTuples(2, line.getOptionValues("t"));
                        for (final List<String> group : groups) {
                            builder.addSegmentsFromOneProducer(SegmentType.T, new File(group.get(0)), group.get(1));
                        }
                    }
                    if (line.hasOption("T")) {
                        final List<List<String>> groups = groupTuples(2, line.getOptionValues("T"));
                        for (final List<String> group : groups) {
                            builder.addSegmentsWithAttributes(SegmentType.T, new File(group.get(0)), new File(group.get(1)));
                        }
                    }
                    if (line.hasOption("tsv")) {
                        builder.loadFromTabSeparatedFile(line.getOptionValue("tsv"));
                    }
                    builder.build();
                    System.out.println(builder.getNUnits() + " units were written to " + builder.getPathToPEJ());
                } catch (ParserConfigurationException ex) {
                    Logger.getLogger(PEJMain.class.getName()).log(Level.SEVERE, null, ex);
                } catch (TransformerException ex) {
                    Logger.getLogger(PEJMain.class.getName()).log(Level.SEVERE, null, ex);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(PEJMain.class.getName()).log(Level.SEVERE, null, ex);
                } catch (FormatException ex) {
                    Logger.getLogger(PEJMain.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(PEJMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (final ParseException ex) {
                formatter.printHelp("pej", options);
                System.out.println(ex);
            }
        } else {
            java.awt.EventQueue.invokeLater(new Runnable() {

                public void run() {
                    new PEJMain().setVisible(true);
                }
            });
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddMT;
    private javax.swing.JButton btnAddR;
    private javax.swing.JButton btnAddS;
    private javax.swing.JButton btnDo;
    private javax.swing.JButton btnJobAsttr;
    private javax.swing.JButton btnPEJ;
    private javax.swing.JButton btnUnits;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JList lstFiles;
    private javax.swing.JList lstJob;
    private javax.swing.JTextField txtOutput;
    private javax.swing.JTextField txtUnitsMeta;
    // End of variables declaration//GEN-END:variables
}
