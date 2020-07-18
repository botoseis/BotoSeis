package botoseis.mainGui.admin;

/*
 * NewProcessDlg.java
 * 
 */
import botoseis.mainGui.prmview.ProcessParameter;
import botoseis.mainGui.workflows.ParametersGroup;
import java.util.Enumeration;
import javax.swing.JFileChooser;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class EditProcessDlg extends javax.swing.JDialog {

    public EditProcessDlg(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Groups");
        m_treeModel = new DefaultTreeModel(root);

        prmList.setModel(m_treeModel);
        prmList.setRootVisible(false);

        prmList.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                prmListValueChanged(evt);
            }
        });

        btnNewParameter.setEnabled(false);
        btnEdit.setEnabled(false);
        btnRemove.setEnabled(false);
        btnUp.setEnabled(false);
        btnDown.setEnabled(false);
    }

    public boolean getResponseOk() {
        return responseOk;
    }

    public void setProcessTitle(String s) {
        processTitle.setText(s);
    }

    public String getProcessTitle() {
        return processTitle.getText();
    }

    public void setProcessBrief(String s) {
        processBrief.setText(s);
    }

    public String getProcessBrief() {
        return processBrief.getText();
    }

    public void setAuthorName(String s) {
        authorName.setText(s);
    }

    public String getAuthorName() {
        return authorName.getText();
    }

    public void setAuthorEMail(String s) {
        authorEMail.setText(s);
    }

    public String getAuthorEMail() {
        return authorEMail.getText();
    }

    public void setExePath(String s) {
        exePath.setText(s);
    }

    public String getExePath() {
        return exePath.getText();
    }

    public void setCallConvention(String s) {
        if ("SU".equalsIgnoreCase(s)) {
            callConvention.setSelectedIndex(0);
        } else {
            callConvention.setSelectedIndex(1);
        }
    }

    public String getCallConvention() {
        return callConvention.getSelectedItem().toString();
    }

    public void setHasInput(boolean f) {
        hasInput.setSelected(f);
    }

    public boolean getHasInput() {
        return hasInput.isSelected();
    }

    public void setHasOutput(boolean f) {
        hasOutput.setSelected(f);
    }

    public boolean getHasOutput() {
        return hasOutput.isSelected();
    }

    public void setParameters(java.util.Vector<ParametersGroup> pg) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Groups");
        m_treeModel = new DefaultTreeModel(root);

        prmList.setModel(m_treeModel);
        prmList.setRootVisible(true);

        for (int i = 0; i < pg.size(); i++) {
            ParametersGroup g = pg.get(i);

            Group ng = new Group(g.getGroupName(), g.getDescription());
            DefaultMutableTreeNode nodeG = new DefaultMutableTreeNode(ng);
            m_treeModel.insertNodeInto(nodeG, root, i);

            java.util.Vector<ProcessParameter> plist = g.getParameters();
            for (int j = 0; j < plist.size(); j++) {
                ProcessParameter pp = plist.get(j);

                ProcessParameter np = new ProcessParameter();
                np.brief = pp.brief;
                np.defaultValue = pp.defaultValue;
                np.name = pp.name;
                np.optionValues = pp.optionValues;
                np.type = pp.type;
                np.keyvaluePair = pp.keyvaluePair;
                np.optionsListSelectionType = pp.optionsListSelectionType;
                
                DefaultMutableTreeNode nodeP = new DefaultMutableTreeNode(np);
                m_treeModel.insertNodeInto(nodeP, nodeG, j);
            }
        }
        prmList.updateUI();
    }

    public java.util.Vector<ParametersGroup> getParameters() {
        java.util.Vector<ParametersGroup> res = new java.util.Vector<ParametersGroup>();

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) m_treeModel.getRoot();

        Enumeration rc = root.children();
        while (rc.hasMoreElements()) {
            DefaultMutableTreeNode nodeG = (DefaultMutableTreeNode) rc.nextElement();
            Group tg = (Group) nodeG.getUserObject();
            ParametersGroup g = new ParametersGroup(tg.name, tg.desc);
            res.add(g);

            Enumeration rc2 = nodeG.children();
            while (rc2.hasMoreElements()) {
                DefaultMutableTreeNode nodeP = (DefaultMutableTreeNode) rc2.nextElement();
                ProcessParameter tp = (ProcessParameter) nodeP.getUserObject();
                ProcessParameter p = new ProcessParameter();
                p.brief = tp.brief;
                p.defaultValue = tp.defaultValue;
                p.name = tp.name;
                p.optionValues = tp.optionValues;
                p.type = tp.type;
                p.keyvaluePair = tp.keyvaluePair;
                p.optionsListSelectionType = tp.optionsListSelectionType;

                g.addParameter(p);
            }
        }
        return res;
    }

    private void prmListValueChanged(javax.swing.event.TreeSelectionEvent evt) {
        DefaultMutableTreeNode sel;
        if (prmList.getSelectionPath() != null) {
            sel = (DefaultMutableTreeNode) prmList.getSelectionPath().getLastPathComponent();
            if (!sel.isRoot()) {
                if (sel.getUserObject() instanceof Group) {
                    labelItemType.setVisible(false);
                    itemType.setVisible(false);
                    labelItemValues.setVisible(false);
                    itemValues.setVisible(false);
                    labelDefaultValue.setVisible(false);
                    itemDefaultValue.setVisible(false);

                    Group g = (Group) sel.getUserObject();
                    String name = g.name;
                    String desc = g.desc;

                    itemName.setText(name);
                    itemDesc.setText(desc);

                } else if (sel.getUserObject() instanceof ProcessParameter) {
                    labelItemType.setVisible(true);
                    itemType.setVisible(true);
                    labelItemValues.setVisible(true);
                    itemValues.setVisible(true);
                    labelDefaultValue.setVisible(true);
                    itemDefaultValue.setVisible(true);
                    btnNewParameter.setEnabled(true);

                    ProcessParameter p = (ProcessParameter) sel.getUserObject();

                    itemName.setText(p.name);
                    itemDesc.setText(p.brief);
                    itemType.setText(p.type);
                    itemValues.setText(p.optionValues);
                    itemDefaultValue.setText(p.defaultValue);
                }
                int i = m_treeModel.getIndexOfChild(sel.getParent(),
                        sel);
                int n = m_treeModel.getChildCount(sel.getParent());

                if (i == 0) {
                    btnUp.setEnabled(false);
                } else {
                    btnUp.setEnabled(true);
                }
                if (i == (n - 1)) {
                    btnDown.setEnabled(false);
                } else {
                    btnDown.setEnabled(true);
                }
                btnNewParameter.setEnabled(true);
                btnEdit.setEnabled(true);
                btnRemove.setEnabled(true);
            } else {
                itemName.setText("");
                itemDesc.setText("");
                btnNewParameter.setEnabled(false);
                btnEdit.setEnabled(false);
                btnRemove.setEnabled(false);
                btnUp.setEnabled(false);
                btnDown.setEnabled(false);
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

        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        prmList = new javax.swing.JTree();
        btnNewGroup = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        btnUp = new javax.swing.JButton();
        btnDown = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        labelItemType = new javax.swing.JLabel();
        labelDefaultValue = new javax.swing.JLabel();
        itemName = new javax.swing.JTextField();
        itemDesc = new javax.swing.JTextField();
        itemDefaultValue = new javax.swing.JTextField();
        labelItemValues = new javax.swing.JLabel();
        itemValues = new javax.swing.JTextField();
        itemType = new javax.swing.JTextField();
        btnNewParameter = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        labelTitle = new javax.swing.JLabel();
        processTitle = new javax.swing.JTextField();
        labelBrief = new javax.swing.JLabel();
        labelExec = new javax.swing.JLabel();
        exePath = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        labelAuthorName = new javax.swing.JLabel();
        processBrief = new javax.swing.JTextField();
        authorName = new javax.swing.JTextField();
        labelAuthorEMail = new javax.swing.JLabel();
        authorEMail = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        callConvention = new javax.swing.JComboBox();
        hasInput = new javax.swing.JCheckBox();
        hasOutput = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        btnCancel = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add process");
        setModal(true);
        setResizable(false);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Parameters"));

        jScrollPane1.setViewportView(prmList);

        btnNewGroup.setText("New group");
        btnNewGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewGroupActionPerformed(evt);
            }
        });

        btnRemove.setText("Remove");
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        btnUp.setText("Up");
        btnUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpActionPerformed(evt);
            }
        });

        btnDown.setText("Down");
        btnDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Details"));

        jLabel1.setText("Name:");

        jLabel2.setText("Description:");

        labelItemType.setText("Type:");

        labelDefaultValue.setText("Default value:");

        itemName.setEditable(false);

        itemDesc.setEditable(false);

        itemDefaultValue.setEditable(false);

        labelItemValues.setText("Options Values:");

        itemValues.setEditable(false);

        itemType.setEditable(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelDefaultValue, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelItemType, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelItemValues, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(itemName, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(itemDesc, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                    .addComponent(itemType, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(itemValues, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                    .addComponent(itemDefaultValue, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(itemName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(itemDesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelItemType)
                    .addComponent(itemType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelItemValues)
                    .addComponent(itemValues, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelDefaultValue)
                    .addComponent(itemDefaultValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(93, Short.MAX_VALUE))
        );

        btnNewParameter.setText("New parameter");
        btnNewParameter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewParameterActionPerformed(evt);
            }
        });

        btnEdit.setText("Edit");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnNewGroup, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                    .addComponent(btnNewParameter, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                    .addComponent(btnEdit, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                    .addComponent(btnRemove, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                    .addComponent(btnUp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                    .addComponent(btnDown, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(btnNewGroup)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnNewParameter)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEdit)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnRemove)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnUp)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDown))))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("General properties"));

        labelTitle.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelTitle.setText("Title:");

        processTitle.setMaximumSize(new java.awt.Dimension(200, 18));
        processTitle.setMinimumSize(new java.awt.Dimension(200, 18));
        processTitle.setPreferredSize(new java.awt.Dimension(200, 18));

        labelBrief.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelBrief.setText("Brief:");

        labelExec.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelExec.setText("Executable:");

        exePath.setMaximumSize(new java.awt.Dimension(100, 18));
        exePath.setMinimumSize(new java.awt.Dimension(100, 18));
        exePath.setPreferredSize(new java.awt.Dimension(100, 18));

        btnSearch.setText("Search...");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        labelAuthorName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelAuthorName.setText("Author name:");

        processBrief.setMaximumSize(new java.awt.Dimension(20, 23));
        processBrief.setMinimumSize(new java.awt.Dimension(6, 23));
        processBrief.setPreferredSize(new java.awt.Dimension(6, 23));

        authorName.setMaximumSize(new java.awt.Dimension(100, 23));
        authorName.setMinimumSize(new java.awt.Dimension(6, 23));
        authorName.setPreferredSize(new java.awt.Dimension(6, 23));

        labelAuthorEMail.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelAuthorEMail.setText("email:");

        authorEMail.setPreferredSize(new java.awt.Dimension(150, 18));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Input/Output"));

        jLabel4.setText("Calling convention");

        callConvention.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "SU", "BotoSeis" }));

        hasInput.setText("Has input");

        hasOutput.setText("Has output");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(callConvention, 0, 101, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(hasInput)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hasOutput)
                .addGap(349, 349, 349))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabel4))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(callConvention, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(hasInput)
                        .addComponent(hasOutput)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(107, 107, 107)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(labelTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(processTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(labelExec, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(exePath, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSearch))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(labelBrief, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(labelAuthorName))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(authorName, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(labelAuthorEMail)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(authorEMail, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(processBrief, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelTitle)
                    .addComponent(processTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelBrief)
                    .addComponent(processBrief, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(labelAuthorName))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelAuthorEMail)
                            .addComponent(authorName, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(authorEMail, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelExec)
                    .addComponent(exePath, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnOk.setText("Ok");
        btnOk.setMinimumSize(new java.awt.Dimension(65, 23));
        btnOk.setPreferredSize(new java.awt.Dimension(65, 23));
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 781, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void btnNewGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewGroupActionPerformed
        EditGroupDlg dlg = new EditGroupDlg(null, true);

        dlg.setVisible(true);

        String name = dlg.getGroupName();
        String desc = dlg.getGroupDesc();

        if (dlg.getResponseOk() && !name.isEmpty()) {
            Group g = new Group(name, desc);
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(g);
            m_treeModel.insertNodeInto(node,
                    (DefaultMutableTreeNode) m_treeModel.getRoot(),
                    m_treeModel.getChildCount(m_treeModel.getRoot()));
            m_treeModel.reload();
            TreePath p = new TreePath(m_treeModel.getPathToRoot(node));
            prmList.setSelectionPath(p);
            prmList.setRootVisible(true);
        }
    }//GEN-LAST:event_btnNewGroupActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        DefaultMutableTreeNode sel;
        if (prmList.getSelectionPath() != null) {
            sel = (DefaultMutableTreeNode) prmList.getSelectionPath().getLastPathComponent();
            if (!sel.isRoot()) {
                if (sel.getUserObject() instanceof Group) {

                    Group g = (Group) sel.getUserObject();

                    String name = g.name;
                    String desc = g.desc;

                    EditGroupDlg dlg = new EditGroupDlg(null, true);

                    dlg.setGroupName(name);
                    dlg.setGroupDesc(desc);

                    dlg.setVisible(true);

                    name = dlg.getGroupName();
                    desc = dlg.getGroupDesc();

                    if (dlg.getResponseOk() && !name.isEmpty()) {
                        g.name = name;
                        g.desc = desc;

                        itemName.setText(name);
                        itemDesc.setText(desc);
                    }

                } else if (sel.getUserObject() instanceof ProcessParameter) {
                    ProcessParameter p = (ProcessParameter) sel.getUserObject();

                    EditParametersDlg dlg = new EditParametersDlg(null, true);

                    dlg.setParameterName(p.name);
                    dlg.setDescription(p.brief);
                    dlg.setType(p.type);
                    dlg.setOptionsValues(p.optionValues);
                    dlg.setDefaultValue(p.defaultValue);
                    dlg.setKeyValuePairOption(p.keyvaluePair);
                    dlg.setOptionsSelectionType(p.optionsListSelectionType);

                    dlg.setVisible(true);

                    String name = dlg.getParameterName();

                    if (dlg.getResponseOk() && !name.isEmpty()) {
                        p.name = name;
                        p.brief = dlg.getDescription();
                        p.type = dlg.getType_();
                        p.optionValues = dlg.getOptionsValues();
                        p.defaultValue = dlg.getDefaultValue();
                        p.keyvaluePair = dlg.isKeyValuPairSelected();
                        p.optionsListSelectionType = dlg.getOptionsSelectionType();

                        itemName.setText(p.name);
                        itemDesc.setText(p.brief);
                        itemType.setText(p.type);
                        itemValues.setText(p.optionValues);
                        itemDefaultValue.setText(p.defaultValue);
                    }
                }
            }
        }
        prmList.updateUI();
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        DefaultMutableTreeNode sel;
        if (prmList.getSelectionPath() != null) {
            sel = (DefaultMutableTreeNode) prmList.getSelectionPath().getLastPathComponent();
            if (!sel.isRoot()) {
                m_treeModel.removeNodeFromParent(sel);
                itemName.setText("");
                itemDesc.setText("");
                labelItemType.setVisible(false);
                itemType.setVisible(false);
                labelItemValues.setVisible(false);
                itemValues.setVisible(false);
                labelDefaultValue.setVisible(false);
                itemDefaultValue.setVisible(false);
            }
        }
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void btnUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpActionPerformed
        DefaultMutableTreeNode sel;
        if (prmList.getSelectionPath() != null) {
            sel = (DefaultMutableTreeNode) prmList.getSelectionPath().getLastPathComponent();
            if (!sel.isRoot()) {
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) sel.getParent();
                int i = m_treeModel.getIndexOfChild(parent, sel);
                if (i > 0) {
                    m_treeModel.removeNodeFromParent(sel);
                    m_treeModel.insertNodeInto(sel, parent, i - 1);
                    TreePath p = new TreePath(m_treeModel.getPathToRoot(sel));
                    prmList.setSelectionPath(p);
                }
            }
        }
    }//GEN-LAST:event_btnUpActionPerformed

    private void btnDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownActionPerformed
        DefaultMutableTreeNode sel;
        if (prmList.getSelectionPath() != null) {
            sel = (DefaultMutableTreeNode) prmList.getSelectionPath().getLastPathComponent();
            if (!sel.isRoot()) {
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) sel.getParent();
                int i = m_treeModel.getIndexOfChild(parent, sel);
                int n = m_treeModel.getChildCount(parent);

                if (i < (n - 1)) {
                    m_treeModel.removeNodeFromParent(sel);
                    m_treeModel.insertNodeInto(sel, parent, i + 1);
                    TreePath p = new TreePath(m_treeModel.getPathToRoot(sel));
                    prmList.setSelectionPath(p);
                }
            }
        }
    }//GEN-LAST:event_btnDownActionPerformed

    private void btnNewParameterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewParameterActionPerformed
        DefaultMutableTreeNode sel;
        if (prmList.getSelectionPath() != null) {
            sel = (DefaultMutableTreeNode) prmList.getSelectionPath().getLastPathComponent();
            if (!sel.isRoot()) {
                DefaultMutableTreeNode parent = null;
                if (sel.getUserObject() instanceof Group) {
                    parent = sel;
                } else if (sel.getUserObject() instanceof ProcessParameter) {
                    parent = (DefaultMutableTreeNode) sel.getParent();
                }

                EditParametersDlg dlg = new EditParametersDlg(null, true);
                dlg.setVisible(true);

                String name = dlg.getParameterName();

                if (dlg.getResponseOk() && !name.isEmpty()) {
                    ProcessParameter p = new ProcessParameter();

                    p.name = name;
                    p.brief = dlg.getDescription();
                    p.type = dlg.getType_();
                    p.optionValues = dlg.getOptionsValues();
                    p.keyvaluePair = dlg.isKeyValuPairSelected();
                    p.optionsListSelectionType = dlg.getOptionsSelectionType();
                    p.defaultValue = dlg.getDefaultValue();

                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(p);

                    m_treeModel.insertNodeInto(node, parent,
                            parent.getChildCount());

                }
            }
        }
}//GEN-LAST:event_btnNewParameterActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        JFileChooser jfc = new JFileChooser();

        int ret = jfc.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            exePath.setText(jfc.getSelectedFile().toString());
        }
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        responseOk = true;
        setVisible(false);
    }//GEN-LAST:event_btnOkActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        responseOk = false;
        setVisible(false);
    }//GEN-LAST:event_btnCancelActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new EditProcessDlg(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }

    class Group {

        public Group(String n, String d) {
            name = n;
            desc = d;
        }

        @Override
        public String toString() {
            return name;
        }
        public String name;
        public String desc;
    }
    private boolean responseOk = false;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField authorEMail;
    private javax.swing.JTextField authorName;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDown;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnNewGroup;
    private javax.swing.JButton btnNewParameter;
    private javax.swing.JButton btnOk;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUp;
    private javax.swing.JComboBox callConvention;
    private javax.swing.JTextField exePath;
    private javax.swing.JCheckBox hasInput;
    private javax.swing.JCheckBox hasOutput;
    private javax.swing.JTextField itemDefaultValue;
    private javax.swing.JTextField itemDesc;
    private javax.swing.JTextField itemName;
    private javax.swing.JTextField itemType;
    private javax.swing.JTextField itemValues;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel labelAuthorEMail;
    private javax.swing.JLabel labelAuthorName;
    private javax.swing.JLabel labelBrief;
    private javax.swing.JLabel labelDefaultValue;
    private javax.swing.JLabel labelExec;
    private javax.swing.JLabel labelItemType;
    private javax.swing.JLabel labelItemValues;
    private javax.swing.JLabel labelTitle;
    private javax.swing.JTree prmList;
    private javax.swing.JTextField processBrief;
    private javax.swing.JTextField processTitle;
    // End of variables declaration//GEN-END:variables
    DefaultTreeModel m_treeModel = null;
}
