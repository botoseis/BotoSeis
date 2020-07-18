package botoseis.mainGui.admin;

/*
 * EditProcessListDlg.java
 *
 * Created on 20 de Dezembro de 2007, 10:15
 */
import botoseis.mainGui.admin.EditProcessDlg;
import botoseis.mainGui.prmview.ProcessParameter;
import botoseis.mainGui.temp.MainWindow;
import botoseis.mainGui.workflows.ParametersGroup;
import botoseis.mainGui.workflows.ProcessModel;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.tree.TreePath;
import javax.swing.JOptionPane;

import java.io.*;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import botoseis.mainGui.utils.DefaultNode;

public class ManageProcessesDlg extends javax.swing.JDialog {
    MainWindow main;
    public ManageProcessesDlg(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        main = (MainWindow) parent;
        savedListModel = loadProcessesList();

        processesList.setModel(savedListModel);
    }

    public static ProcessModel getProcess(String id) {
        ProcessModel ret = null;

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) savedListModel.getRoot();

        Enumeration en = root.breadthFirstEnumeration();

        DefaultMutableTreeNode node;
        ProcessModel p;

        String [] ids1 = id.split("-");
        while (en.hasMoreElements()) {
            node = (DefaultMutableTreeNode) en.nextElement();
            if (node.getUserObject() instanceof ProcessModel) {
                p = (ProcessModel) node.getUserObject();
                String [] ids2 = p.getID().split("-");
                if (ids1[0].equalsIgnoreCase(ids2[0])) {
                    ret = p;
                    break;
                }
            }
        }
        

        return ret;
    }

    private void saveProcessesList() {
        Object botov = System.getenv("BOTOSEIS_ROOT");
        String xmlFile = botov.toString() + "/proclist.xml";
        File of = new File(xmlFile);
        FileWriter outF;
        try {
            outF = new FileWriter(of);

            outF.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            outF.write("<BotoSeis>\n");

            DefaultTreeModel tm = (DefaultTreeModel) processesList.getModel();

            DefaultMutableTreeNode root = (DefaultMutableTreeNode) tm.getRoot();

            Enumeration ec = root.children();
            DefaultMutableTreeNode node;
            int indent = 1;
            while (ec.hasMoreElements()) {
                node = (DefaultMutableTreeNode) ec.nextElement();
                if (node.getUserObject() instanceof String) {
                    writeGroupInfo(outF, node, indent);
                } else {
                    ProcessModel proc = (ProcessModel) node.getUserObject();
                    writeProcessInfo(outF, proc, indent);
                }
            }
            outF.write("</BotoSeis>");
            outF.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void writeGroupInfo(FileWriter pOutF, DefaultMutableTreeNode pNode, int pIndent) {
        String tab = "    "; // 4 spaces tabs.
        String ind = "";
        for (int i = 0; i < pIndent; i++) {
            ind += tab;
        }
        try {
            pOutF.write(ind + "<Group>\n");
            pOutF.write(ind + tab + "<Title>" + pNode.toString() + "</Title>\n");
            Enumeration ec = pNode.children();
            while (ec.hasMoreElements()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) ec.nextElement();
                if (node.getUserObject() instanceof ProcessModel) {
                    ProcessModel proc = (ProcessModel) node.getUserObject();
                    writeProcessInfo(pOutF, proc, pIndent + 1);
                } else {
                    writeGroupInfo(pOutF, node, pIndent + 1);
                }
            }
            pOutF.write(ind + "</Group>\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private void writeProcessInfo(FileWriter pOutF, ProcessModel pProc, int pIndent) {
        String tab = "    "; // 4 spaces tabs.
        String ind = "";
        for (int i = 0; i < pIndent; i++) {
            ind += tab;
        }
        try {
            pOutF.write(ind + "<Process>\n");
            pOutF.write(ind + tab + "<Title>" + pProc.getTitle() + "</Title>\n");
            pOutF.write(ind + tab + "<ID>" + pProc.getID() + "</ID>\n");
            pOutF.write(ind + tab + "<Brief>" + pProc.getBrief() + "</Brief>\n");
            writeProcessAuthorInfo(pOutF, pProc, pIndent + 1);
            pOutF.write(ind + tab + "<Executable>" + pProc.getExecutablePath() + "</Executable>\n");
            pOutF.write(ind + tab + "<CallConvention>" + pProc.getCallingConvention() + "</CallConvention>\n");
            pOutF.write(ind + tab + "<HasInput>" + pProc.hasInput() + "</HasInput>\n");
            pOutF.write(ind + tab + "<HasOutput>" + pProc.hasOutput() + "</HasOutput>\n");
            writeProcessParameters(pOutF, pProc, pIndent + 1);
            pOutF.write(ind + "</Process>\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void writeProcessAuthorInfo(FileWriter pOutF, ProcessModel pProc, int pIndent) {
        String tab = "    "; // 4 spaces tabs.
        String ind = "";
        for (int i = 0; i < pIndent; i++) {
            ind += tab;
        }
        try {
            pOutF.write(ind + "<Author>\n");
            pOutF.write(ind + tab + "<Name>" + pProc.getAuthorName() + "</Name>\n");
            pOutF.write(ind + tab + "<email>" + pProc.getAuthorEmail() + "</email>\n");
            pOutF.write(ind + "</Author>\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void writeProcessParameters(FileWriter pOutF, ProcessModel pProc, int pIndent) {
        String tab = "    "; // 4 spaces tabs.
        String ind = "";
        for (int i = 0; i < pIndent; i++) {
            ind += tab;
        }
        try {
            Vector<ParametersGroup> groups = pProc.getParameters();
            for (int i = 0; i < groups.size(); i++) {
                pOutF.write(ind + "<ParametersGroup>\n");
                pOutF.write(ind + tab + "<Name>" + groups.get(i).getGroupName() + "</Name>\n");
                pOutF.write(ind + tab + "<Description>" + groups.get(i).getDescription() + "</Description>\n");
                Vector<ProcessParameter> params = groups.get(i).getParameters();
                for (int j = 0; j < params.size(); j++) {
                    pOutF.write(ind + tab + "<Parameter>\n");
                    ProcessParameter p = params.get(j);
                    pOutF.write(ind + tab + tab + "<Name>" + p.name + "</Name>\n");
                    pOutF.write(ind + tab + tab + "<Type>" + p.type + "</Type>\n");
                    if (p.type.equalsIgnoreCase("Options")) {
                        pOutF.write(ind + tab + tab + "<OptionsValues>" + p.optionValues + "</OptionsValues>\n");
                        pOutF.write(ind + tab + tab + "<OptionsSelectionType>" + p.optionsListSelectionType + "</OptionsSelectionType>\n");
                    }
                    pOutF.write(ind + tab + tab + "<KeyValuePair>" + p.keyvaluePair + "</KeyValuePair>\n");
                    pOutF.write(ind + tab + tab + "<Brief>" + p.brief + "</Brief>\n");
                    pOutF.write(ind + tab + tab + "<DefaultValue>" + p.defaultValue + "</DefaultValue>\n");
                    pOutF.write(ind + tab + "</Parameter>\n");
                }
                pOutF.write(ind + "</ParametersGroup>\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public static DefaultTreeModel loadProcessesList() {
        Object botov = System.getenv("BOTOSEIS_ROOT");
        String xmlFile = botov.toString() + "/proclist.xml";

        DOMParser parser = new DOMParser();
        DefaultTreeModel treeModel = null;

        DefaultNode root = new DefaultNode("All Processes",DefaultNode.DEFUALT_TYPE);
        treeModel = new DefaultTreeModel(root);

        try {
            parser.parse(xmlFile);

            Document document = parser.getDocument();
            NodeList nodes = document.getChildNodes();
            int len = (nodes != null) ? nodes.getLength() : 0;
            Node node;
            for (int i = 0; i < len; i++) {
                node = nodes.item(i);
                switch (node.getNodeType()) {
                    case Node.COMMENT_NODE:
                        break;
                    case Node.ELEMENT_NODE:
                        readElementNode(node, treeModel, root);
                        break;
                }
            }
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        savedListModel = treeModel;

        return treeModel;
    }

    private static void readElementNode(Node pParent, DefaultTreeModel tm, DefaultNode lparent) {
        NodeList nodes = pParent.getChildNodes();
        int len = (nodes != null) ? nodes.getLength() : 0;
        Node node;
        for (int i = 0; i < len; i++) {
            node = nodes.item(i);
            switch (node.getNodeType()) {
                case Node.TEXT_NODE:
                    break;
                case Node.ELEMENT_NODE:
                    if (node.getNodeName().equalsIgnoreCase("Group")) {
                        readGroupInfo(node, tm, lparent);
                    } else if (node.getNodeName().equalsIgnoreCase("Process")) {
                        readProcessInfo(node, tm, lparent);
                    }
                    break;
            }
        }
    }

    private static void readGroupInfo(Node group, DefaultTreeModel tm, DefaultNode lparent) {
        NodeList nodes = group.getChildNodes();
        int len = (nodes != null) ? nodes.getLength() : 0;
        Node node;
        DefaultNode gleaf = null;
        for (int i = 0; i < len; i++) {
            node = nodes.item(i);
            if (node.getNodeName().equalsIgnoreCase("Title")) {
                gleaf = new DefaultNode(node.getChildNodes().item(0).getNodeValue(),DefaultNode.GROUP_TYPE);
                tm.insertNodeInto(gleaf, lparent, lparent.getChildCount());
            }
        }

        for (int i = 0; i < len; i++) {
            node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (node.getNodeName().equalsIgnoreCase("Group")) {
                    readGroupInfo(node, tm, gleaf);
                } else if (node.getNodeName().equalsIgnoreCase("Process")) {
                    readProcessInfo(node, tm, gleaf);
                }
            }
        }
    }

    private static void readProcessInfo(Node procNode, DefaultTreeModel tm, DefaultNode gparent) {
        NodeList nodes = procNode.getChildNodes();
        int len = (nodes != null) ? nodes.getLength() : 0;
        Node node;
        DefaultNode gleaf = null;
        ProcessModel proc = new ProcessModel();
        for (int i = 0; i < len; i++) {
            node = nodes.item(i);
            if (node.getNodeName().equalsIgnoreCase("Title")) {
                if (node.getChildNodes().getLength() > 0) {
                    proc.setTitle(node.getChildNodes().item(0).getNodeValue());
                }
            } else if (node.getNodeName().equalsIgnoreCase("ID")) {
                if (node.getChildNodes().getLength() > 0) {
                    proc.setID(node.getChildNodes().item(0).getNodeValue());
                }
            } else if (node.getNodeName().equalsIgnoreCase("Brief")) {
                if (node.getChildNodes().getLength() > 0) {
                    proc.setBrief(node.getChildNodes().item(0).getNodeValue());
                }
            } else if (node.getNodeName().equalsIgnoreCase("Author")) {
                if (node.getChildNodes().getLength() > 0) {
                    readProcessAuthorInfo(proc, node);
                }
            } else if (node.getNodeName().equalsIgnoreCase("Executable")) {
                if (node.getChildNodes().getLength() > 0) {
                    proc.setExecutable(node.getChildNodes().item(0).getNodeValue());
                }
            } else if (node.getNodeName().equalsIgnoreCase("CallConvention")) {
                if (node.getChildNodes().getLength() > 0) {
                    proc.setCallingConvention(node.getChildNodes().item(0).getNodeValue());
                }
            } else if (node.getNodeName().equalsIgnoreCase("HasInput")) {
                if (node.getChildNodes().getLength() > 0) {
                    proc.setHasInput(Boolean.parseBoolean(node.getChildNodes().item(0).getNodeValue()));
                }
            } else if (node.getNodeName().equalsIgnoreCase("HasOutput")) {
                if (node.getChildNodes().getLength() > 0) {
                    proc.setHasOutput(Boolean.parseBoolean(node.getChildNodes().item(0).getNodeValue()));
                }
            } else if (node.getNodeName().equalsIgnoreCase("ParametersGroup")) {
                if (node.getChildNodes().getLength() > 0) {
                    readProcessParameterGroup(proc, node);
                }
            }
        }

        gleaf = new DefaultNode(proc,DefaultNode.PROCESS_TYPE);
        tm.insertNodeInto(gleaf, gparent, gparent.getChildCount());
    }

    private static void readProcessAuthorInfo(ProcessModel proc, Node pNode) {
        NodeList nodes = pNode.getChildNodes();
        int len = (nodes != null) ? nodes.getLength() : 0;
        Node node;

        for (int i = 0; i < len; i++) {
            node = nodes.item(i);
            if (node.getNodeName().equalsIgnoreCase("Name")) {
                if (node.getChildNodes().getLength() > 0) {
                    proc.setAuthorName(node.getChildNodes().item(0).getNodeValue());
                }
            } else if (node.getNodeName().equalsIgnoreCase("email")) {
                if (node.getChildNodes().getLength() > 0) {
                    proc.setAuthorEMail(node.getChildNodes().item(0).getNodeValue());
                }
            }
        }
    }

    private static void readProcessParameterGroup(ProcessModel proc, Node pNode) {
        NodeList nodes = pNode.getChildNodes();
        int len = (nodes != null) ? nodes.getLength() : 0;
        Node node;
        ParametersGroup pg = new ParametersGroup();
        proc.addParametersGroup(pg);
        for (int i = 0; i < len; i++) {
            node = nodes.item(i);
            if (node.getNodeName().equalsIgnoreCase("Name")) {
                if (node.getChildNodes().getLength() > 0) {
                    pg.setGroupName(node.getChildNodes().item(0).getNodeValue());
                }
            } else if (node.getNodeName().equalsIgnoreCase("Description")) {
                if (node.getChildNodes().getLength() > 0) {
                    pg.setDescription(node.getChildNodes().item(0).getNodeValue());
                }
            } else if (node.getNodeName().equalsIgnoreCase("Parameter")) {
                readProcessParameterInfo(node, pg);
            }
        }
    }

    private static void readProcessParameterInfo(Node pNode, ParametersGroup pGroup) {
        NodeList nodes = pNode.getChildNodes();
        int len = (nodes != null) ? nodes.getLength() : 0;
        Node node;
        ProcessParameter prm = new ProcessParameter();
        pGroup.addParameter(prm);
        for (int i = 0; i < len; i++) {
            node = nodes.item(i);
            if (node.getNodeName().equalsIgnoreCase("Name")) {
                if (node.getChildNodes().getLength() > 0) {
                    prm.name = node.getChildNodes().item(0).getNodeValue();
                }
            } else if (node.getNodeName().equalsIgnoreCase("Type")) {
                if (node.getChildNodes().getLength() > 0) {
                    prm.type = node.getChildNodes().item(0).getNodeValue();
                }
            } else if (node.getNodeName().equalsIgnoreCase("OptionsValues")) {
                if (node.getChildNodes().getLength() > 0) {
                    prm.optionValues = node.getChildNodes().item(0).getNodeValue();
                }
            } else if (node.getNodeName().equalsIgnoreCase("Brief")) {
                if (node.getChildNodes().getLength() > 0) {
                    prm.brief = node.getChildNodes().item(0).getNodeValue();
                }
            } else if (node.getNodeName().equalsIgnoreCase("DefaultValue")) {
                if (node.getChildNodes().getLength() > 0) {
                    prm.defaultValue = node.getChildNodes().item(0).getNodeValue();
                }
            } else if (node.getNodeName().equalsIgnoreCase("OptionsSelectionType")) {
                if (node.getChildNodes().getLength() > 0) {
                    prm.optionsListSelectionType = node.getChildNodes().item(0).getNodeValue();
                }
            } else if (node.getNodeName().equalsIgnoreCase("KeyValuePair")) {
                if (node.getChildNodes().getLength() > 0) {
                    prm.keyvaluePair = node.getChildNodes().item(0).getNodeValue();
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

        jScrollPane1 = new javax.swing.JScrollPane();
        processesList = new javax.swing.JTree();
        btnAddGroup = new javax.swing.JButton();
        btnAddProcess = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        m_title = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        m_brief = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        m_authorName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        m_authorEMail = new javax.swing.JTextField();
        btnOk = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        m_exePath = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        m_parametersTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        processesList.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                processesListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(processesList);

        btnAddGroup.setText("Add group");
        btnAddGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddGroupActionPerformed(evt);
            }
        });

        btnAddProcess.setText("Add process");
        btnAddProcess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddProcessActionPerformed(evt);
            }
        });

        btnRemove.setText("Remove");
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        btnEdit.setText("Edit");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Process general properties"));

        jLabel1.setText("Title:");

        m_title.setEditable(false);

        jLabel2.setText("Description:");

        m_brief.setEditable(false);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Author"));

        jLabel3.setText("Name:");

        m_authorName.setEditable(false);

        jLabel4.setText("email:");

        m_authorEMail.setEditable(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(m_authorEMail)
                    .addComponent(m_authorName, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(m_authorName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(m_authorEMail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_brief, javax.swing.GroupLayout.PREFERRED_SIZE, 495, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_title, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 741, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(m_title, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(m_brief, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        btnOk.setText("Ok");
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        jLabel7.setText("Process executable:");

        m_exePath.setEditable(false);

        m_parametersTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Group", "Name", "Type", "Values", "Brief", "Default"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        m_parametersTable.setEnabled(false);
        m_parametersTable.setFocusable(false);
        jScrollPane2.setViewportView(m_parametersTable);

        jPanel3.setBackground(new java.awt.Color(0, 102, 102));

        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Process parameters");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addContainerGap(672, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
        );

        jLabel9.setText("Avaiable processes");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnRemove, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnEdit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnAddGroup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnAddProcess, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(300, 300, 300))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_exePath, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 777, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnAddGroup)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddProcess)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEdit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemove)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(m_exePath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOk)
                    .addComponent(btnCancel))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        processesList.setModel(savedListModel);
        setVisible(false);
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        saveProcessesList();
        main.initProcessesList();
        setVisible(false);
    }//GEN-LAST:event_btnOkActionPerformed

    private void btnAddProcessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddProcessActionPerformed
        EditProcessDlg dlg = new EditProcessDlg(null, true);
        dlg.setVisible(true);
        if (dlg.getResponseOk()) {
            DefaultMutableTreeNode parent = null;
            TreePath tp = processesList.getSelectionPath();
            if (tp == null) {
                parent = (DefaultMutableTreeNode) processesList.getModel().getRoot();
            } else {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp.getLastPathComponent();
                if (node.getUserObject() instanceof ProcessModel) {
                    parent = (DefaultMutableTreeNode) node.getParent();
                } else {
                    parent = node;
                }
            }
            // Check for duplicated process titles
            Enumeration en = parent.children();
            boolean flag = false;
            while (en.hasMoreElements()) {
                if (dlg.getProcessTitle().equalsIgnoreCase(en.nextElement().toString())) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                ProcessModel proc = new ProcessModel();
                proc.setTitle(dlg.getProcessTitle());
                proc.setBrief(dlg.getProcessBrief());
                proc.setAuthorEMail(dlg.getAuthorEMail());
                proc.setAuthorName(dlg.getAuthorName());
                proc.setExecutable(dlg.getExePath());
                proc.setCallingConvention(dlg.getCallConvention());
                proc.setHasInput(dlg.getHasInput());
                proc.setHasOutput(dlg.getHasOutput());

                String pID = dlg.getProcessTitle();

                java.util.GregorianCalendar today = new java.util.GregorianCalendar();

                pID += "-" + String.format("%d", today.getTimeInMillis());
                proc.setID(pID);

                java.util.Vector<ParametersGroup> grps = dlg.getParameters();
                for (int i = 0; i < grps.size(); i++) {
                    proc.addParametersGroup(grps.get(i));
                }
                DefaultMutableTreeNode leaf = new DefaultMutableTreeNode(proc);
                DefaultTreeModel tm = (DefaultTreeModel) processesList.getModel();
                tm.insertNodeInto(leaf, parent, parent.getChildCount());
            } else {
                JOptionPane.showMessageDialog(null, "An item with the same title is already defined.\n" +
                        "Please try again choosing a different title.");
            }
        }

    }//GEN-LAST:event_btnAddProcessActionPerformed

    private void processesListValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_processesListValueChanged
        DefaultMutableTreeNode sel;
        if (processesList.getSelectionPath() != null) {
            sel = (DefaultMutableTreeNode) processesList.getSelectionPath().getLastPathComponent();
            if (!sel.isRoot()) {
                if (sel.getUserObject() instanceof ProcessModel) {
                    // Process
                    ProcessModel proc = (ProcessModel) sel.getUserObject();
                    m_title.setText(proc.getTitle());
                    m_brief.setText(proc.getBrief());
                    m_authorName.setText(proc.getAuthorName());
                    m_authorEMail.setText(proc.getAuthorEmail());
                    m_exePath.setText(proc.getExecutablePath());

                    int lc = m_parametersTable.getRowCount();
                    int cc = m_parametersTable.getColumnCount();
                    for (int i = 0; i < lc; i++) {
                        for (int j = 0; j < cc; j++) {
                            m_parametersTable.setValueAt("", i, j);
                        }
                    }

                    Vector<ParametersGroup> pgList = proc.getParameters();

                    ParametersGroup group;
                    Vector<ProcessParameter> prmList;
                    int count = 0;
                    for (int i = 0; i < pgList.size(); i++) {
                        group = pgList.get(i);
                        String grpName = group.getGroupName();

                        prmList = group.getParameters();
                        ProcessParameter prm;
                        for (int j = 0; j < prmList.size(); j++) {
                            prm = prmList.get(j);
                            m_parametersTable.setValueAt(grpName, count, 0);
                            m_parametersTable.setValueAt(prm.name, count, 1);
                            m_parametersTable.setValueAt(prm.type, count, 2);
                            m_parametersTable.setValueAt(prm.optionValues, count,
                                    3);
                            m_parametersTable.setValueAt(prm.brief, count, 4);
                            m_parametersTable.setValueAt(prm.defaultValue, count,
                                    5);
                            count++;
                        }
                    }
                }
            }
        } else {
            m_title.setText("");
            m_brief.setText("");
            m_authorName.setText("");
            m_authorEMail.setText("");
            m_exePath.setText("");
            int MaxRows = 100;
            for (int j = 0; j < MaxRows; j++) {
                m_parametersTable.setValueAt("", j, 0);
                m_parametersTable.setValueAt("", j, 1);
                m_parametersTable.setValueAt("", j, 2);
                m_parametersTable.setValueAt("", j, 4);
                m_parametersTable.setValueAt("", j, 5);
            }
        }
    }//GEN-LAST:event_processesListValueChanged

    private void btnAddGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddGroupActionPerformed
        String title = JOptionPane.showInputDialog("Title");

        if (title != null) {
            DefaultMutableTreeNode parent = null;
            DefaultMutableTreeNode sel;
            TreePath tp = processesList.getSelectionPath();

            if (tp == null) {
                parent = (DefaultMutableTreeNode) processesList.getModel().getRoot();
            } else {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp.getLastPathComponent();

                if (node.getUserObject() instanceof ProcessModel) {
                    parent = (DefaultMutableTreeNode) node.getParent();
                } else {
                    parent = node;
                }
            }

            // Check repeated title
            Enumeration en = parent.children();
            boolean flag = false;
            while (en.hasMoreElements()) {
                if (title.equalsIgnoreCase(en.nextElement().toString())) {
                    flag = true;
                    break;
                }

            }

            if (!flag) {
                DefaultMutableTreeNode gleaf = new DefaultMutableTreeNode(title);

                DefaultTreeModel tm = (DefaultTreeModel) processesList.getModel();
                tm.insertNodeInto(gleaf, parent, parent.getChildCount());
            } else {
                JOptionPane.showMessageDialog(null, "An item with the same title is already defined.\n" +
                        "Please try again choosing a different title.");
            }
        }
}//GEN-LAST:event_btnAddGroupActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        DefaultMutableTreeNode sel;
        sel = (DefaultMutableTreeNode) processesList.getSelectionPath().getLastPathComponent();
        if (!sel.isRoot()) {
            if (sel.getUserObject() instanceof ProcessModel) {
                ProcessModel p = (ProcessModel) sel.getUserObject();

                EditProcessDlg dlg = new EditProcessDlg(null, true);

                dlg.setProcessTitle(p.getTitle());
                dlg.setProcessBrief(p.getBrief());
                dlg.setAuthorName(p.getAuthorName());
                dlg.setAuthorEMail(p.getAuthorEmail());
                dlg.setExePath(p.getExecutablePath());
                dlg.setCallConvention(p.getCallingConvention());
                dlg.setHasInput(p.hasInput());
                dlg.setHasOutput(p.hasOutput());

                dlg.setParameters(p.getParameters());

                dlg.setVisible(true);

                if (dlg.getResponseOk()) {
                    p.setTitle(dlg.getProcessTitle());
                    p.setBrief(dlg.getProcessBrief());
                    p.setAuthorName(dlg.getAuthorName());
                    p.setAuthorEMail(dlg.getAuthorEMail());
                    p.setExecutable(dlg.getExePath());
                    p.setCallingConvention(dlg.getCallConvention());
                    p.setHasInput(dlg.getHasInput());
                    p.setHasOutput(dlg.getHasOutput());
                    p.setParameters(dlg.getParameters());
                }
            } else if (sel.getUserObject() instanceof String) {
                String title = JOptionPane.showInputDialog("Title");
                if (title != null) {
                    sel.setUserObject(title);
                }
            }
            TreePath p = new TreePath(savedListModel.getPathToRoot(sel));
            processesList.setSelectionPath(p);
            processesList.updateUI();
            processesListValueChanged(null);
        }
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        DefaultMutableTreeNode sel;
        sel = (DefaultMutableTreeNode) processesList.getSelectionPath().getLastPathComponent();
        if (!sel.isRoot()) {
            savedListModel.removeNodeFromParent(sel);
            processesList.updateUI();
        }
    }//GEN-LAST:event_btnRemoveActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new ManageProcessesDlg(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }
    static DefaultTreeModel savedListModel;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddGroup;
    private javax.swing.JButton btnAddProcess;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnOk;
    private javax.swing.JButton btnRemove;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField m_authorEMail;
    private javax.swing.JTextField m_authorName;
    private javax.swing.JTextField m_brief;
    private javax.swing.JTextField m_exePath;
    private javax.swing.JTable m_parametersTable;
    private javax.swing.JTextField m_title;
    private javax.swing.JTree processesList;
    // End of variables declaration//GEN-END:variables
}
