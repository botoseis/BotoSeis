package botoseis.mainGui.main;

import java.awt.event.ActionEvent;
import java.util.GregorianCalendar;
import botoseis.mainGui.workflows.ProcessModel;
import botoseis.mainGui.admin.ManageProcessesDlg;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import botoseis.mainGui.usrproject.UserProject;
import botoseis.mainGui.workflows.WorkflowProcess;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/*
 * MainWindow.java
 *
 * Created on 12 de Dezembro de 2007, 09:13
 */
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import botoseis.mainGui.usrproject.ProcessingLine;
import botoseis.mainGui.utils.AboutDlg;
import botoseis.mainGui.utils.DefaultNode;
import botoseis.mainGui.utils.RendererTree;
import botoseis.mainGui.utils.Utils;
import botoseis.mainGui.workflows.WorkflowModel;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainWindow extends javax.swing.JFrame {

    public MainWindow() {
        initComponents();

        labelProjectExplorer.setName("Project explorer - ");

        fileNew.setAction(newProjectAction);
        fileNew.setText("New Project...");
        fileNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/botoseis/pics/New16.gif")));

        btnNewProject.setAction(newProjectAction);
        btnNewProject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/botoseis/pics/New24.gif")));
        btnNewProject.setToolTipText("New project");

        fileOpen.setAction(openProjectAction);
        fileOpen.setText("Open Project...");
        fileOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/botoseis/pics/Open16.gif")));

        btnOpenProject.setAction(openProjectAction);
        btnOpenProject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/botoseis/pics/Open24.gif")));
        btnOpenProject.setToolTipText("Open project");

        fileSave.setAction(saveProjectAction);
        fileSave.setText("Save Project...");
        fileSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/botoseis/pics/Save16.gif")));

        btnSaveProject.setAction(saveProjectAction);
        btnSaveProject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/botoseis/pics/Save24.gif")));
        btnSaveProject.setToolTipText("Save project");

        jSplitPane2.setDividerLocation(400);
        jSplitPane1.setDividerLocation(300);

        m_selectedLineNode = null;
        m_selectedWorkflowNode = null;
        m_activeProject = null;

        m_workflowView = new botoseis.mainGui.workflows.WorkflowView(parametersPanel);

        panelWorkflow.add(m_workflowView);

        m_jobsPanel = new botoseis.mainGui.workflows.ProcessingJobsPanel();

        //m_consoleArea = new javax.swing.JTextArea();

        //m_processOutputTab.addTab("CONSOLE", m_consoleArea);
        m_processOutputTab.addTab("Jobs history", m_jobsPanel);

        Object botov = System.getenv("BOTOSEIS_ROOT");
        Object suv = System.getenv("CWPROOT");

        if (botov == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "BOTOSEIS_ROOT environment variable must be set!\n" + "Set the corresponding variable and run BotoSeis again.");
            System.exit(0);
        }

        if (suv == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "CWPROOT environment variable must be set!\n" + "Set the corresponding variable and run BotoSeis again.");
            System.exit(0);
        }

        botoseis_root = botov.toString();


        rootNode = new DefaultNode("", DefaultNode.PROJECT_TYPE);
        DefaultTreeModel tm = new DefaultTreeModel(rootNode);


        areaExplorer.setModel(tm);
        areaExplorer.setRootVisible(false);
        areaExplorer.setCellRenderer(new RendererTree());
        DefaultTreeSelectionModel sm = new DefaultTreeSelectionModel();
        sm.setSelectionMode(DefaultTreeSelectionModel.SINGLE_TREE_SELECTION);
        areaExplorer.setSelectionModel(sm);

        sm = new DefaultTreeSelectionModel();
        processesList.setSelectionModel(sm);


        initProcessesList();
        processesList.setCellRenderer(new RendererTree());
        DefaultNode root = (DefaultNode) processesList.getModel().getRoot();

        java.util.Enumeration en = root.breadthFirstEnumeration();

        java.util.Vector<Object> items = new java.util.Vector<Object>();
        DefaultMutableTreeNode node;
        while (en.hasMoreElements()) {
            node = (DefaultMutableTreeNode) en.nextElement();
            if (!(node.getUserObject() instanceof String)) {
                items.add(node.getUserObject());
            }
        }

        javax.swing.DefaultComboBoxModel cm = new javax.swing.DefaultComboBoxModel(items);
        comboProcessesList.setModel(cm);

        botoseis.mainGui.autocomplete.Configurator.enableAutoCompletion(comboProcessesList);

        fileNewLine.setEnabled(false);
        fileNewWorkflow.setEnabled(false);
        openRecentProject();

        jSplitPane1.setOneTouchExpandable(true);
        jSplitPane2.setOneTouchExpandable(true);
        jSplitPane3.setOneTouchExpandable(true);
        jSplitPane4.setOneTouchExpandable(true);

        JPanel jp = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton clear = new JButton(new javax.swing.ImageIcon(getClass().getResource("/botoseis/pics/trash.png")));
        clear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                 m_consoleArea.setText("");
            }
        });
        clear.setPreferredSize(new Dimension(16,16));
        clear.setToolTipText("Clear console");
        jp.add(new JLabel("Console"));
        jp.add(clear);
        m_processOutputTab.setTabComponentAt(0, jp);

    }

    public void openRecentProject() {
        String prj = "";
        try {
            File file = new File(botoseis_root + "/.last");

            if (file.exists()) {
                BufferedReader buff = new BufferedReader(new FileReader(file));
                prj = buff.readLine();
                buff.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (prj != null && !prj.trim().equals("")) {
            String split[] = prj.split("##@@");
            for (int i = 0; i < split.length; i++) {
                File file = new File(split[i]);
                if (file.exists()) {
                    new OpenProjectAction(split[i]).exec();
                }
            }

        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane3 = new javax.swing.JSplitPane();
        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        parametersPanel = new javax.swing.JPanel();
        panelW = new javax.swing.JPanel();
        jToolBar3 = new javax.swing.JToolBar();
        btnMoveTop = new javax.swing.JButton();
        btnMoveUP = new javax.swing.JButton();
        btnMoveDown = new javax.swing.JButton();
        btnMoveBottom = new javax.swing.JButton();
        jSeparator14 = new javax.swing.JToolBar.Separator();
        btnRemoveProcessFromWorkflow = new javax.swing.JButton();
        scrollPaneA = new javax.swing.JScrollPane();
        panelWorkflow = new javax.swing.JPanel();
        jToolBar4 = new javax.swing.JToolBar();
        btnRun = new javax.swing.JButton();
        jPanel16 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        m_processOutputTab = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        m_consoleArea = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jSplitPane4 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        areaExplorer = new javax.swing.JTree();
        jPanel4 = new javax.swing.JPanel();
        labelProjectExplorer = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        btnAddProcessToWorkflow = new javax.swing.JButton();
        comboProcessesList = new javax.swing.JComboBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        processesList = new javax.swing.JTree();
        jPanel6 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnNewProject = new javax.swing.JButton();
        btnOpenProject = new javax.swing.JButton();
        btnSaveProject = new javax.swing.JButton();
        mainMenuBar = new javax.swing.JMenuBar();
        FileMenu = new javax.swing.JMenu();
        fileNew = new javax.swing.JMenuItem();
        fileNewLine = new javax.swing.JMenuItem();
        fileNewWorkflow = new javax.swing.JMenuItem();
        jSeparator15 = new javax.swing.JSeparator();
        fileOpen = new javax.swing.JMenuItem();
        fileClose = new javax.swing.JMenuItem();
        fileDelete = new javax.swing.JMenuItem();
        jSeparator16 = new javax.swing.JSeparator();
        fileSave = new javax.swing.JMenuItem();
        jSeparator17 = new javax.swing.JSeparator();
        fileExit = new javax.swing.JMenuItem();
        optionsMenu2 = new javax.swing.JMenu();
        menuEditProcList = new javax.swing.JMenuItem();
        menuRecoveryProject = new javax.swing.JMenuItem();
        HelpMenu2 = new javax.swing.JMenu();
        menuDocumentation = new javax.swing.JMenuItem();
        jSeparator20 = new javax.swing.JSeparator();
        menuAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BotoSeis (BETA)");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jSplitPane3.setDividerLocation(300);
        jSplitPane3.setMaximumSize(new java.awt.Dimension(10, 10));

        jSplitPane1.setDividerLocation(500);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jSplitPane2.setDividerLocation(400);

        parametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Parameters"));
        parametersPanel.setLayout(new java.awt.GridBagLayout());
        jScrollPane4.setViewportView(parametersPanel);

        jSplitPane2.setRightComponent(jScrollPane4);

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);

        btnMoveTop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/botoseis/pics/Top16.gif"))); // NOI18N
        btnMoveTop.setToolTipText("Move to top of workflow");
        btnMoveTop.setFocusable(false);
        btnMoveTop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMoveTop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMoveTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveTopActionPerformed(evt);
            }
        });
        jToolBar3.add(btnMoveTop);

        btnMoveUP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/botoseis/pics/Up16.gif"))); // NOI18N
        btnMoveUP.setToolTipText("Move one position up");
        btnMoveUP.setFocusable(false);
        btnMoveUP.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMoveUP.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMoveUP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveUPActionPerformed(evt);
            }
        });
        jToolBar3.add(btnMoveUP);

        btnMoveDown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/botoseis/pics/Down16.gif"))); // NOI18N
        btnMoveDown.setToolTipText("Move one position down");
        btnMoveDown.setFocusable(false);
        btnMoveDown.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMoveDown.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMoveDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveDownActionPerformed(evt);
            }
        });
        jToolBar3.add(btnMoveDown);

        btnMoveBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/botoseis/pics/Bottom16.gif"))); // NOI18N
        btnMoveBottom.setToolTipText("Move to bottom of workflow");
        btnMoveBottom.setFocusable(false);
        btnMoveBottom.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMoveBottom.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMoveBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveBottomActionPerformed(evt);
            }
        });
        jToolBar3.add(btnMoveBottom);
        jToolBar3.add(jSeparator14);

        btnRemoveProcessFromWorkflow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/botoseis/pics/Remove16.gif"))); // NOI18N
        btnRemoveProcessFromWorkflow.setToolTipText("Remove the selected process");
        btnRemoveProcessFromWorkflow.setFocusable(false);
        btnRemoveProcessFromWorkflow.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRemoveProcessFromWorkflow.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemoveProcessFromWorkflow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveProcessFromWorkflowActionPerformed(evt);
            }
        });
        jToolBar3.add(btnRemoveProcessFromWorkflow);

        scrollPaneA.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPaneA.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        panelWorkflow.setMaximumSize(new java.awt.Dimension(1000, 500));
        panelWorkflow.setMinimumSize(new java.awt.Dimension(1000, 500));
        panelWorkflow.setPreferredSize(new java.awt.Dimension(1000, 500));
        panelWorkflow.setLayout(new javax.swing.BoxLayout(panelWorkflow, javax.swing.BoxLayout.LINE_AXIS));
        scrollPaneA.setViewportView(panelWorkflow);

        jToolBar4.setFloatable(false);
        jToolBar4.setRollover(true);

        btnRun.setIcon(new javax.swing.ImageIcon(getClass().getResource("/botoseis/pics/Play16.gif"))); // NOI18N
        btnRun.setToolTipText("Run workflow");
        btnRun.setFocusable(false);
        btnRun.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRun.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRunActionPerformed(evt);
            }
        });
        jToolBar4.add(btnRun);

        javax.swing.GroupLayout panelWLayout = new javax.swing.GroupLayout(panelW);
        panelW.setLayout(panelWLayout);
        panelWLayout.setHorizontalGroup(
            panelWLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelWLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelWLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollPaneA, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelWLayout.createSequentialGroup()
                        .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 192, Short.MAX_VALUE)
                        .addComponent(jToolBar4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelWLayout.setVerticalGroup(
            panelWLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelWLayout.createSequentialGroup()
                .addComponent(scrollPaneA, javax.swing.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelWLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToolBar4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jSplitPane2.setLeftComponent(panelW);

        jSplitPane1.setTopComponent(jSplitPane2);

        jPanel16.setBackground(new java.awt.Color(255, 255, 255));
        jPanel16.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jPanel16.setPreferredSize(new java.awt.Dimension(746, 300));

        jPanel17.setBackground(new java.awt.Color(102, 102, 102));

        jLabel9.setFont(new java.awt.Font("Dialog", 1, 11));
        jLabel9.setForeground(new java.awt.Color(204, 204, 204));
        jLabel9.setText("Output");
        jLabel9.setAlignmentX(0.5F);

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(457, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        m_processOutputTab.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        m_consoleArea.setColumns(20);
        m_consoleArea.setEditable(false);
        m_consoleArea.setRows(5);
        jScrollPane1.setViewportView(m_consoleArea);

        m_processOutputTab.addTab("CONSOLE", jScrollPane1);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_processOutputTab, javax.swing.GroupLayout.DEFAULT_SIZE, 730, Short.MAX_VALUE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_processOutputTab, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE))
        );

        jSplitPane1.setBottomComponent(jPanel16);

        jSplitPane3.setRightComponent(jSplitPane1);

        jPanel2.setLayout(new java.awt.GridLayout(1, 0));

        jSplitPane4.setDividerLocation(250);
        jSplitPane4.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        areaExplorer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                areaExplorerMouseClicked(evt);
            }
        });
        areaExplorer.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                areaExplorerValueChanged(evt);
            }
        });
        jScrollPane5.setViewportView(areaExplorer);

        jPanel4.setBackground(new java.awt.Color(102, 102, 102));

        labelProjectExplorer.setFont(new java.awt.Font("Dialog", 1, 11));
        labelProjectExplorer.setForeground(new java.awt.Color(204, 204, 204));
        labelProjectExplorer.setText("Project explorer - ");
        labelProjectExplorer.setAlignmentX(0.5F);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelProjectExplorer, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelProjectExplorer, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                        .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap()))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                    .addGap(18, 18, 18)))
        );

        jSplitPane4.setLeftComponent(jPanel1);

        jPanel7.setBackground(new java.awt.Color(102, 102, 102));

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 11));
        jLabel3.setForeground(new java.awt.Color(204, 204, 204));
        jLabel3.setText("Processes list");
        jLabel3.setAlignmentX(0.5F);

        btnAddProcessToWorkflow.setText(">>");
        btnAddProcessToWorkflow.setToolTipText("Add the selected process to workflow");
        btnAddProcessToWorkflow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddProcessToWorkflowActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 129, Short.MAX_VALUE)
                .addComponent(btnAddProcessToWorkflow)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnAddProcessToWorkflow)
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE))
        );

        comboProcessesList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Williams", "Maria", "Olinda", "Joao", "Carlos", "Marcos", "Madalena", "Wanderley", "Wagner", "Julio" }));

        processesList.setDragEnabled(true);
        processesList.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                processesListValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(processesList);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                        .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(comboProcessesList, javax.swing.GroupLayout.Alignment.LEADING, 0, 276, Short.MAX_VALUE))
                    .addContainerGap()))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 548, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(comboProcessesList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jSplitPane4.setRightComponent(jPanel3);

        jPanel2.add(jSplitPane4);

        jSplitPane3.setLeftComponent(jPanel2);

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1048, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );

        jToolBar1.setRollover(true);

        btnNewProject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/botoseis/pics/New24.gif"))); // NOI18N
        btnNewProject.setToolTipText("New project");
        btnNewProject.setFocusable(false);
        btnNewProject.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNewProject.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnNewProject);

        btnOpenProject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/botoseis/pics/Open24.gif"))); // NOI18N
        btnOpenProject.setToolTipText("Open project");
        btnOpenProject.setFocusable(false);
        btnOpenProject.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOpenProject.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnOpenProject);

        btnSaveProject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/botoseis/pics/Save24.gif"))); // NOI18N
        btnSaveProject.setToolTipText("Save project");
        btnSaveProject.setFocusable(false);
        btnSaveProject.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSaveProject.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnSaveProject);

        FileMenu.setText("File");

        fileNew.setText("New Project...");
        FileMenu.add(fileNew);

        fileNewLine.setText("New line...");
        fileNewLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileNewLineActionPerformed(evt);
            }
        });
        FileMenu.add(fileNewLine);

        fileNewWorkflow.setText("New workflow...");
        fileNewWorkflow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileNewWorkflowActionPerformed(evt);
            }
        });
        FileMenu.add(fileNewWorkflow);
        FileMenu.add(jSeparator15);

        fileOpen.setText("Open Project...");
        FileMenu.add(fileOpen);

        fileClose.setText("Close Project");
        fileClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileCloseActionPerformed(evt);
            }
        });
        FileMenu.add(fileClose);

        fileDelete.setText("Delete Project");
        fileDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileDeleteActionPerformed(evt);
            }
        });
        FileMenu.add(fileDelete);
        FileMenu.add(jSeparator16);

        fileSave.setText("Save");
        FileMenu.add(fileSave);
        FileMenu.add(jSeparator17);

        fileExit.setText("Exit");
        fileExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileExit1ActionPerformed(evt);
            }
        });
        FileMenu.add(fileExit);

        mainMenuBar.add(FileMenu);

        optionsMenu2.setText("Options");

        menuEditProcList.setText("Edit processes list...");
        menuEditProcList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        optionsMenu2.add(menuEditProcList);

        menuRecoveryProject.setText("Recovery project");
        menuRecoveryProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRecoveryProjectActionPerformed(evt);
            }
        });
        optionsMenu2.add(menuRecoveryProject);

        mainMenuBar.add(optionsMenu2);

        HelpMenu2.setText("Help");

        menuDocumentation.setText("Documentation");
        HelpMenu2.add(menuDocumentation);
        HelpMenu2.add(jSeparator20);

        menuAbout.setText("About");
        menuAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAboutActionPerformed(evt);
            }
        });
        HelpMenu2.add(menuAbout);

        mainMenuBar.add(HelpMenu2);

        setJMenuBar(mainMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSplitPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1052, Short.MAX_VALUE)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 804, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing

        try {
            BufferedWriter buff = new BufferedWriter(new FileWriter(botoseis_root + "/.last"));
            String saida = "";
            for (int i = 0; i < rootNode.getChildCount(); i++) {

                if (!((UserProject) ((DefaultNode) rootNode.getChildAt(i)).getUserObject()).getTitle().equals("default")) {
                    saida += ((UserProject) ((DefaultNode) rootNode.getChildAt(i)).getUserObject()).getprojHomeDir() + "##@@";
                }
            }
            buff.write(saida);
            buff.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }//GEN-LAST:event_formWindowClosing

            private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
                ManageProcessesDlg dlg = new ManageProcessesDlg(this, true);
                dlg.setVisible(true);
            //processesList.setModel();
    }//GEN-LAST:event_jMenuItem12ActionPerformed

            private void fileNewLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileNewLineActionPerformed

                String lineName = JOptionPane.showInputDialog(areaExplorer, "Enter a name for the line");

                if (lineName != null) {
                    addNewLine(lineName);
                }

}//GEN-LAST:event_fileNewLineActionPerformed

            private void fileNewWorkflowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileNewWorkflowActionPerformed
                String name = JOptionPane.showInputDialog(areaExplorer, "Enter workflow name.");
                if (name != null) {
                    addWorkFlow(name);
                }

}//GEN-LAST:event_fileNewWorkflowActionPerformed

            private void menuAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAboutActionPerformed
                AboutDlg dlg = new botoseis.mainGui.utils.AboutDlg(this, true);

                dlg.setVisible(true);
}//GEN-LAST:event_menuAboutActionPerformed

            private void fileExit1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileExit1ActionPerformed
                System.exit(0);
            }//GEN-LAST:event_fileExit1ActionPerformed

            private void btnRemoveProcessFromWorkflowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveProcessFromWorkflowActionPerformed
                if (m_currentWorkflow != null) {//GEN-LAST:event_btnRemoveProcessFromWorkflowActionPerformed
            int i = m_workflowView.getSelectedProcessIndex();
            m_currentWorkflow.removeProcess(i);
        }

    }

private void btnMoveTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveTopActionPerformed
    if (m_currentWorkflow != null) {
        int i = m_workflowView.getSelectedProcessIndex();
        m_currentWorkflow.moveProcessToTop(i);
    }
}//GEN-LAST:event_btnMoveTopActionPerformed

private void btnMoveUPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveUPActionPerformed
    if (m_currentWorkflow != null) {
        int i = m_workflowView.getSelectedProcessIndex();
        m_currentWorkflow.moveProcessUp(i);
    }
}//GEN-LAST:event_btnMoveUPActionPerformed

private void btnMoveDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveDownActionPerformed
    if (m_currentWorkflow != null) {
        int i = m_workflowView.getSelectedProcessIndex();
        m_currentWorkflow.moveProcessDown(i);
    }
}//GEN-LAST:event_btnMoveDownActionPerformed

private void btnMoveBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMoveBottomActionPerformed
    if (m_currentWorkflow != null) {
        int i = m_workflowView.getSelectedProcessIndex();
        m_currentWorkflow.moveProcessToBottom(i);
    }
}//GEN-LAST:event_btnMoveBottomActionPerformed

private void btnRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRunActionPerformed
    if (m_currentWorkflow != null) {
        String jobHome = m_currentWorkflow.getHomedir() + "/jobs";

        java.util.GregorianCalendar today = new java.util.GregorianCalendar();

        jobHome += String.format("/%d-%d-%d--%d-%d-%d", today.get(GregorianCalendar.MONTH),
                today.get(GregorianCalendar.DATE), today.get(GregorianCalendar.YEAR),
                today.get(GregorianCalendar.HOUR_OF_DAY), today.get(GregorianCalendar.MINUTE),
                today.get(GregorianCalendar.SECOND));

        java.io.File jobF = new java.io.File(jobHome);

        if (jobF.mkdir()) {
            botoseis.mainGui.workflows.WorkflowJob njob = new botoseis.mainGui.workflows.WorkflowJob(jobHome, m_currentWorkflow, m_consoleArea);
            njob.start();


            m_jobsPanel.addJob(njob);
        } else {
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Coudn't create project folder: " + jobHome);
        }
    }
}//GEN-LAST:event_btnRunActionPerformed

private void processesListValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_processesListValueChanged
    if (evt.isAddedPath()) {
        DefaultNode node =
                (DefaultNode) processesList.getSelectionPath().getLastPathComponent();
        if (node.getUserObject() instanceof ProcessModel) {
            comboProcessesList.setSelectedItem(node.getUserObject());
        }
    }
}//GEN-LAST:event_processesListValueChanged

private void btnAddProcessToWorkflowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddProcessToWorkflowActionPerformed
    if (m_currentWorkflow != null) {
        ProcessModel model = (ProcessModel) comboProcessesList.getSelectedItem();

        botoseis.mainGui.prmview.ParametersPanel pp = new botoseis.mainGui.prmview.ParametersPanel(model);
        WorkflowProcess wp = new WorkflowProcess(model, pp);

        m_currentWorkflow.addProcess(wp, m_workflowView.getInsertMarkposition());
    }

}//GEN-LAST:event_btnAddProcessToWorkflowActionPerformed

private void areaExplorerValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_areaExplorerValueChanged

    TreePath selPath = evt.getPath();

    if (selPath != null) {
        DefaultNode n = (DefaultNode) selPath.getLastPathComponent();
        m_activeProject = DefaultNode.getProject(n);
        m_selectedLineNode = null;
        m_selectedWorkflowNode = null;
        m_currentWorkflow = null;
        String str = labelProjectExplorer.getName() + m_activeProject.getTitle();
        labelProjectExplorer.setText(str);


        if (n.getUserObject() instanceof botoseis.mainGui.workflows.WorkflowModel) {
            DefaultNode parent = (DefaultNode) n.getParent();
            if (parent.getUserObject() instanceof botoseis.mainGui.usrproject.ProcessingLine) {
                m_activeProject.selectLine(parent.getUserObject().toString());
            } else {
                m_activeProject.selectLine("GlobalLine");
            }
            m_currentWorkflow = m_activeProject.selectWorkflow(n.getUserObject().toString());
            if (m_currentWorkflow != null) {
                m_workflowView.setModel(m_currentWorkflow);

                loadJobsHistory(m_currentWorkflow);
                m_selectedWorkflowNode = n;
                m_selectedLineNode = (DefaultNode) n.getParent();
            }
        } else if (n.getUserObject() instanceof botoseis.mainGui.usrproject.ProcessingLine) {
            m_activeProject.selectLine(n.getUserObject().toString());
            m_selectedLineNode = n;
        } else {
            m_activeProject.selectLine("GlobalLine");
        }
        if (m_currentWorkflow == null) {
            m_workflowView.clear();
            parametersPanel.removeAll();
            parametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Parameters"));
            parametersPanel.updateUI();
        }
    }

}//GEN-LAST:event_areaExplorerValueChanged

private void fileCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileCloseActionPerformed

    m_currentWorkflow = null;
    m_workflowView.clear();
    parametersPanel.removeAll();
    parametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Parameters"));
    parametersPanel.updateUI();

    rootNode.remove(getProject(m_activeProject));
    areaExplorer.updateUI();

    if (m_activeProject != null) {
        m_activeProject.save();
        m_activeProject.close();
        m_activeProject = null;
    }
    fileNewLine.setEnabled(false);
    fileNewWorkflow.setEnabled(false);

}//GEN-LAST:event_fileCloseActionPerformed

private void areaExplorerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_areaExplorerMouseClicked


    areaExplorer.setSelectionPath(areaExplorer.getClosestPathForLocation(evt.getX(), evt.getY()));
    DefaultNode node = (DefaultNode) areaExplorer.getLastSelectedPathComponent();
    if (node != null) {
        if (evt.getButton() == MouseEvent.BUTTON3) {
            JMenuItem newFlow = new JMenuItem("New Workflow");
            newFlow.setToolTipText("New Workflow");
            newFlow.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    fileNewWorkflowActionPerformed(evt);
                }
            });

            JMenuItem newLine = new JMenuItem("New Line");
            newLine.setToolTipText("New Line");
            newLine.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    fileNewLineActionPerformed(e);
                }
            });

            JPopupMenu jpm = new JPopupMenu();

            JMenuItem copy = new JMenuItem("Copy");
            copy.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    NODECOPY = (DefaultNode) areaExplorer.getLastSelectedPathComponent();
                }
            });


            JMenuItem paste = new JMenuItem("Paste");
            paste.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (NODECOPY != null) {

                        if (NODECOPY.getType() == DefaultNode.FLOW_TYPE) {
                            addWorkFlow(NODECOPY);
                        }
                    }
                }
            });


            if (node.getType() == DefaultNode.LINE_TYPE) {
                JMenuItem renameLine = new JMenuItem("Rename");
                renameLine.setToolTipText("Rename the selected line");
                renameLine.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        boolean valid = false;
                        String newName = null;
                        while (!valid) {
                            newName = JOptionPane.showInputDialog(areaExplorer, "New Name");
                            if (newName == null) {
                                break;
                            }
                            if (newName.trim().equals("")) {
                                JOptionPane.showMessageDialog(areaExplorer, "Invalid name");
                            } else {
                                valid = true;
                            }
                        }
                        if (valid) {
                            DefaultNode selNode = (DefaultNode) areaExplorer.getLastSelectedPathComponent();
                            ProcessingLine line = (ProcessingLine) selNode.getUserObject();
                            line.rename(newName);
                            m_activeProject.reloadLines();
                            areaExplorer.updateUI();
                        }
                    }
                });

                JMenuItem removeLine = new JMenuItem("Remove");
                removeLine.setToolTipText("Remove the selected line");
                removeLine.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        DefaultNode selNode = (DefaultNode) areaExplorer.getLastSelectedPathComponent();
                        ProcessingLine line = (ProcessingLine) selNode.getUserObject();
                        if (JOptionPane.showConfirmDialog(areaExplorer, "Are you sure you want to permanently remove the line # " + line.getTitle() + " # ?\nDirectory: " + line.getHomedir()) == JOptionPane.YES_OPTION) {

                            ((DefaultMutableTreeNode) selNode.getParent()).remove(selNode);
                            m_activeProject.removeLine(selNode.toString());
                            areaExplorer.updateUI();
                            m_activeProject.reloadLines();
                        }
                    }
                });

                jpm.add(paste);
                jpm.addSeparator();
                jpm.add(renameLine);
                jpm.add(removeLine);
                jpm.addSeparator();
                jpm.add(newLine);
                jpm.addSeparator();
                jpm.add(newFlow);
            } else {
                if (node.getType() == DefaultNode.FLOW_TYPE) {
                    JMenuItem renemaFlow = new JMenuItem("Rename");
                    renemaFlow.setToolTipText("Rename the selected workflow");
                    renemaFlow.addActionListener(new java.awt.event.ActionListener() {

                        public void actionPerformed(java.awt.event.ActionEvent evt) {

                            boolean valid = false;
                            String newName = null;
                            while (!valid) {
                                newName = JOptionPane.showInputDialog(areaExplorer, "New Name");
                                if (newName == null) {
                                    break;
                                }
                                if (newName.trim().equals("")) {
                                    JOptionPane.showMessageDialog(areaExplorer, "Invalid name");
                                } else {
                                    valid = true;
                                }
                            }
                            if (valid) {
                                DefaultNode selNode = (DefaultNode) areaExplorer.getLastSelectedPathComponent();
                                WorkflowModel wm = (WorkflowModel) selNode.getUserObject();
                                wm.renameFlow(newName);
                                File file = new File(wm.getHomedir());
                                m_activeProject.reloadFlows(file.getParent());
                                areaExplorer.updateUI();
                            }
                        }
                    });

                    JMenuItem removeFlow = new JMenuItem("Remove");
                    removeFlow.setToolTipText("Remove the selected workflow");
                    removeFlow.addActionListener(new java.awt.event.ActionListener() {

                        public void actionPerformed(java.awt.event.ActionEvent evt) {

                            DefaultNode selNode = (DefaultNode) areaExplorer.getLastSelectedPathComponent();
                            WorkflowModel wm = (WorkflowModel) selNode.getUserObject();
                            if (JOptionPane.showConfirmDialog(areaExplorer, "Are you sure you want to permanently remove the workflow # " + wm.getTitle() + " # ?\nDirectory: " + wm.getHomedir()) == JOptionPane.YES_OPTION) {
                                DefaultNode parent = (DefaultNode) selNode.getParent();
                                if (parent.getUserObject() instanceof ProcessingLine) {
                                    ProcessingLine line = (ProcessingLine) (parent).getUserObject();
                                    line.removeFlow(wm);
                                } else {
                                    m_activeProject.selectLine("GlobalLine").removeFlow(wm);
                                }

                                wm.remove();
                                parent.remove(selNode);
                                m_workflowView.repaint();
                                areaExplorer.updateUI();
                            }
                        }
                    });



                    jpm.add(copy);
                    jpm.add(paste);
                    jpm.addSeparator();
                    jpm.add(renemaFlow);
                    jpm.add(removeFlow);
                    jpm.addSeparator();
                    jpm.add(newFlow);



                } else {

                    JMenuItem save = new JMenuItem("Save");
                    save.setToolTipText("Save project");

                    save.addActionListener(new SaveProjectAction());

                    JMenuItem close = new JMenuItem("Close");
                    close.setToolTipText("Close project");
                    close.addActionListener(new java.awt.event.ActionListener() {

                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            fileCloseActionPerformed(evt);
                        }
                    });

                    JMenuItem delete = new JMenuItem("Delete");
                    delete.setToolTipText("Close project");
                    delete.addActionListener(new java.awt.event.ActionListener() {

                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            fileDeleteActionPerformed(evt);
                        }
                    });


                    jpm.add(close);
                    jpm.addSeparator();
                    jpm.add(newLine);
                    jpm.add(newFlow);
                    jpm.addSeparator();
                    jpm.add(delete);
                }

            }


            jpm.show(areaExplorer, evt.getX(), evt.getY());
        }
    }
}//GEN-LAST:event_areaExplorerMouseClicked

private void menuRecoveryProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRecoveryProjectActionPerformed
//    JFileChooser jfc = new JFileChooser();
//    jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//    jfc.showOpenDialog(null);
//    File file = jfc.getSelectedFile();
//    if (file != null) {
//
//    }
}//GEN-LAST:event_menuRecoveryProjectActionPerformed

private void fileDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileDeleteActionPerformed

//        JOptionPane.showMessageDialog(null,m_activeProject.getprojHomeDir());
        if (JOptionPane.showConfirmDialog(null, m_activeProject.getprojHomeDir()+"\nAre you sure?") == JOptionPane.YES_OPTION) {

            if (m_activeProject != null) {
                Utils.deleteFile(new File(m_activeProject.getprojHomeDir()));
                rootNode.remove(getProject(m_activeProject));
                ((DefaultTreeModel) areaExplorer.getModel()).setRoot(rootNode);
                m_activeProject = null;
                JOptionPane.showMessageDialog(null, "Project successfully deleted");
            }
        }
    
}//GEN-LAST:event_fileDeleteActionPerformed

    private void loadJobsHistory(botoseis.mainGui.workflows.WorkflowModel m) {
        String home = m.getHomedir() + "/jobs";
        java.io.File jobsdir = new java.io.File(home);

        String[] jlist = jobsdir.list();

        for (int i = 0; i < jlist.length; i++) {
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }

    public void initProcessesList() {
        DefaultTreeModel tm = ManageProcessesDlg.loadProcessesList();
        processesList.setModel(tm);

    }

  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu FileMenu;
    private javax.swing.JMenu HelpMenu2;
    private javax.swing.JTree areaExplorer;
    private javax.swing.JButton btnAddProcessToWorkflow;
    private javax.swing.JButton btnMoveBottom;
    private javax.swing.JButton btnMoveDown;
    private javax.swing.JButton btnMoveTop;
    private javax.swing.JButton btnMoveUP;
    private javax.swing.JButton btnNewProject;
    private javax.swing.JButton btnOpenProject;
    private javax.swing.JButton btnRemoveProcessFromWorkflow;
    private javax.swing.JButton btnRun;
    private javax.swing.JButton btnSaveProject;
    private javax.swing.JComboBox comboProcessesList;
    private javax.swing.JMenuItem fileClose;
    private javax.swing.JMenuItem fileDelete;
    private javax.swing.JMenuItem fileExit;
    private javax.swing.JMenuItem fileNew;
    private javax.swing.JMenuItem fileNewLine;
    private javax.swing.JMenuItem fileNewWorkflow;
    private javax.swing.JMenuItem fileOpen;
    private javax.swing.JMenuItem fileSave;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JToolBar.Separator jSeparator14;
    private javax.swing.JSeparator jSeparator15;
    private javax.swing.JSeparator jSeparator16;
    private javax.swing.JSeparator jSeparator17;
    private javax.swing.JSeparator jSeparator20;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JSplitPane jSplitPane4;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JToolBar jToolBar4;
    private javax.swing.JLabel labelProjectExplorer;
    private javax.swing.JTextArea m_consoleArea;
    private javax.swing.JTabbedPane m_processOutputTab;
    private javax.swing.JMenuBar mainMenuBar;
    private javax.swing.JMenuItem menuAbout;
    private javax.swing.JMenuItem menuDocumentation;
    private javax.swing.JMenuItem menuEditProcList;
    private javax.swing.JMenuItem menuRecoveryProject;
    private javax.swing.JMenu optionsMenu2;
    private javax.swing.JPanel panelW;
    private javax.swing.JPanel panelWorkflow;
    private javax.swing.JPanel parametersPanel;
    private javax.swing.JTree processesList;
    private javax.swing.JScrollPane scrollPaneA;
    // End of variables declaration//GEN-END:variables
    private DefaultMutableTreeNode m_selectedLineNode;
    private DefaultMutableTreeNode m_selectedWorkflowNode;
    private botoseis.mainGui.workflows.WorkflowModel m_currentWorkflow;
    private UserProject m_activeProject;
    private botoseis.mainGui.workflows.WorkflowView m_workflowView;
    private botoseis.mainGui.workflows.ProcessingJobsPanel m_jobsPanel;
    private NewProjectAction newProjectAction = new NewProjectAction();
    private OpenProjectAction openProjectAction = new OpenProjectAction();
    private SaveProjectAction saveProjectAction = new SaveProjectAction();
    private String botoseis_root;
    private String pathFont;
    public static DefaultNode NODECOPY;
    private DefaultNode rootNode;

    private void addWorkFlow(String name) {
        try {

            m_activeProject.addWorkflow(name);
            botoseis.mainGui.workflows.WorkflowModel sw = m_activeProject.selectWorkflow(name);

            m_workflowView.setModel(sw);

            if (m_selectedLineNode != null && (m_selectedLineNode.getUserObject() instanceof botoseis.mainGui.usrproject.ProcessingLine)) {
                // Insert new workflow in the selected line
                DefaultNode node = new DefaultNode(sw, DefaultNode.FLOW_TYPE);
                DefaultTreeModel tm = (DefaultTreeModel) areaExplorer.getModel();
                tm.insertNodeInto(node, m_selectedLineNode,
                        m_selectedLineNode.getChildCount());

            } else {
                DefaultNode node = new DefaultNode(sw, DefaultNode.FLOW_TYPE);
                DefaultTreeModel tm = (DefaultTreeModel) areaExplorer.getModel();
                tm.insertNodeInto(node, (DefaultNode) tm.getRoot(),
                        ((DefaultNode) tm.getRoot()).getChildCount());

            }
            saveProjectAction.actionPerformed(null);
        } catch (NullPointerException e) {
            javax.swing.JOptionPane.showMessageDialog(this, e.toString());
        }
    }

    private void addWorkFlow(DefaultNode dnode) {
        try {

            WorkflowModel sm = (WorkflowModel) dnode.getUserObject();
            WorkflowModel clone = sm.clone();

            while (m_activeProject.addWorkflow(clone.getTitle())) {
                String name = JOptionPane.showInputDialog("Name invalid!. Type the new name:").trim();
                clone.setTitle(name);
            }
            botoseis.mainGui.workflows.WorkflowModel sw = m_activeProject.selectWorkflow(clone.getTitle());
            sw.setProcList(clone.getProcList());

            m_workflowView.setModel(clone);

            if (m_selectedLineNode != null && (m_selectedLineNode.getUserObject() instanceof botoseis.mainGui.usrproject.ProcessingLine)) {
                // Insert new workflow in the selected line
                DefaultNode node = new DefaultNode(clone, DefaultNode.FLOW_TYPE);
                DefaultTreeModel tm = (DefaultTreeModel) areaExplorer.getModel();
                tm.insertNodeInto(node, m_selectedLineNode,
                        m_selectedLineNode.getChildCount());

            } else {
                DefaultNode node = new DefaultNode(clone, DefaultNode.FLOW_TYPE);
                DefaultTreeModel tm = (DefaultTreeModel) areaExplorer.getModel();
                tm.insertNodeInto(node, (DefaultNode) tm.getRoot(),
                        ((DefaultNode) tm.getRoot()).getChildCount());

            }
            saveProjectAction.actionPerformed(null);
        } catch (NullPointerException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, e.toString());

        }
    }

    private void addNewLine(String lineName) {
        m_activeProject.addLine(lineName);
        botoseis.mainGui.usrproject.ProcessingLine line = m_activeProject.selectLine(lineName);

        if (!line.toString().equalsIgnoreCase("GlobalLine")) {

            DefaultNode node = new DefaultNode(line, DefaultNode.LINE_TYPE);

            DefaultTreeModel tm = (DefaultTreeModel) areaExplorer.getModel();
//            DefaultNode root = (DefaultNode) tm.getRoot();
            DefaultNode root = getProject(m_activeProject);

            tm.insertNodeInto(node, root, root.getChildCount());

            tm.reload();

            Object els[] = new Object[2];
            els[0] = root;
            els[1] = node;

            TreePath path = new TreePath(els);
            areaExplorer.setSelectionPath(path);

        }
        saveProjectAction.actionPerformed(null);
    }

    private DefaultNode getProject(UserProject m_activeProject) {
        for (int i = 0; i < rootNode.getChildCount(); i++) {
            if (((UserProject) (((DefaultNode) rootNode.getChildAt(i)).getUserObject())).getTitle().equals(m_activeProject.getTitle())) {
                return (DefaultNode) rootNode.getChildAt(i);
            }
        }
        return null;
    }

    //
    //javax.swing.JTextArea m_consoleArea;
    class SaveProjectAction extends javax.swing.AbstractAction {

        public SaveProjectAction() {
        }

        public void actionPerformed(ActionEvent e) {
            if (m_activeProject != null) {
                m_activeProject.save();
            }
        }
    }

    class OpenProjectAction extends javax.swing.AbstractAction {

        String path;

        public OpenProjectAction() {
        }

        public OpenProjectAction(String path) {
            this.path = path;
        }

        public void exec() {

            String prjHome = path;

            m_activeProject = new UserProject(prjHome, "noname");
            m_activeProject.open(prjHome);
            String str = labelProjectExplorer.getName() + m_activeProject.getTitle();
            labelProjectExplorer.setText(str);

            DefaultNode root = new DefaultNode(m_activeProject, DefaultNode.PROJECT_TYPE);
            m_activeProject.fillLinesList(root);
            rootNode.add(root);
            ((DefaultTreeModel) areaExplorer.getModel()).setRoot(rootNode);

            fileNewLine.setEnabled(true);
            fileNewWorkflow.setEnabled(true);
        }

        public void actionPerformed(ActionEvent e) {
            javax.swing.JFileChooser jfc = new javax.swing.JFileChooser(new File(Utils.getCurrentPath()));
            jfc.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

            int ret = jfc.showOpenDialog(null);

            if (ret == javax.swing.JFileChooser.APPROVE_OPTION) {
                String prjHome = jfc.getSelectedFile().toString();
                Utils.setCurrentPath(prjHome);

                m_activeProject = new UserProject(prjHome, "noname");
                m_activeProject.open(prjHome);
                String str = labelProjectExplorer.getName() + m_activeProject.getTitle();
                labelProjectExplorer.setText(str);

                DefaultNode root = new DefaultNode(m_activeProject, DefaultNode.PROJECT_TYPE);


                m_activeProject.fillLinesList(root);
                rootNode.add(root);
                fileNewLine.setEnabled(true);
                fileNewWorkflow.setEnabled(true);
                ((DefaultTreeModel) areaExplorer.getModel()).setRoot(rootNode);
            }
        }
    }

    class NewProjectAction extends javax.swing.AbstractAction {

        public NewProjectAction() {
        }

        public void actionPerformed(ActionEvent e) {
            NewProjectDlg dlg = new NewProjectDlg(null, true);
            dlg.setVisible(true);
            if (!dlg.prjName.getText().isEmpty()) {
//                if (m_activeProject != null) {
//                    m_activeProject.save();
//                    m_activeProject.close();
//                }
                // Create project home folder
                java.io.File f = new java.io.File(dlg.prjFolder.getText());
                if (f.exists()) {
                    String s = dlg.prjFolder.getText();
                    javax.swing.JOptionPane.showMessageDialog(null,
                            "Selected folder already exists: " + s);
                } else if (f.mkdir()) {
                    m_activeProject = new UserProject(dlg.prjFolder.getText(),
                            dlg.prjName.getText());
                    String str = labelProjectExplorer.getName();
                    str += dlg.prjName.getText();
                    labelProjectExplorer.setText(str);

                    fileNewLine.setEnabled(true);
                    m_activeProject.save();
                    DefaultNode root = new DefaultNode(m_activeProject, DefaultNode.PROJECT_TYPE);
                    rootNode.add(root);

//                    DefaultTreeModel tm = new DefaultTreeModel(root);

//                    areaExplorer.setModel(tm);
//                    areaExplorer.setRootVisible(true);
                    ((DefaultTreeModel) areaExplorer.getModel()).setRoot(rootNode);
                    fileNewLine.setEnabled(true);
                    fileNewWorkflow.setEnabled(true);
                } else {
                    String s = dlg.prjFolder.getText();
                    javax.swing.JOptionPane.showMessageDialog(null,
                            "Coudn't create project folder: " + s);
                }
            }
        }
    }
}
