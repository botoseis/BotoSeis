/*
 *
 */
package botoseis.mainGui.usrproject;

import java.io.*;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.IOException;

import javax.swing.tree.DefaultMutableTreeNode;

import botoseis.mainGui.utils.DefaultNode;

/**
 *
 */
public class ProcessingLine {

   public ProcessingLine(String home, String title) {
        m_workflowsList = new java.util.Vector<botoseis.mainGui.workflows.WorkflowModel>();
        m_selectedFlow = null;
        m_homeDir = home;
        m_title = title;
    }

    public void fillWorkflowsList(DefaultNode root) {
        for (int i = 0; i < m_workflowsList.size(); i++) {
            DefaultMutableTreeNode node = new DefaultNode(m_workflowsList.get(i), DefaultNode.FLOW_TYPE);
            root.add(node);
        }
    }

    public void open(String home) {
        String fsep = System.getProperty("file.separator");
        m_homeDir = home;
        String xmlFile = m_homeDir + fsep + "info.xml";

        DOMParser parser = new DOMParser();

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
                        readElementNode(node);
                        break;
                }
            }
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            javax.swing.JOptionPane.showMessageDialog(null, "Problem");
        }
    }

    private void readElementNode(Node pParent) {
        NodeList nodes = pParent.getChildNodes();
        int len = (nodes != null) ? nodes.getLength() : 0;
        Node node;
        for (int i = 0; i < len; i++) {
            node = nodes.item(i);
            switch (node.getNodeType()) {
                case Node.TEXT_NODE:
                    break;
                case Node.ELEMENT_NODE:
                    if (node.getNodeName().equalsIgnoreCase("title")) {
                        m_title = node.getChildNodes().item(0).getNodeValue();
                    }
                    if (node.getNodeName().equalsIgnoreCase("author")) {
                    }
                    if (node.getNodeName().equalsIgnoreCase("comments")) {
                    }
                    if (node.getNodeName().equalsIgnoreCase("workflow")) {
                        botoseis.mainGui.workflows.WorkflowModel newFlow = new botoseis.mainGui.workflows.WorkflowModel("", "");
                        String s = node.getChildNodes().item(0).getNodeValue();
                        String fsep = System.getProperty("file.separator");
                        newFlow.open(m_homeDir + fsep + s);
                        //m_workflowView.setModel(newFlow);
                        m_workflowsList.add(newFlow);
                    }
                    break;
            }
        }
    }

    public void save() {
        String fsep = System.getProperty("file.separator");
        String xmlFile = m_homeDir + fsep + "info.xml";
        File of = new File(xmlFile);
        FileWriter outF;
        try {
            outF = new FileWriter(of);

            outF.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            outF.write("<BotoSeisLine>\n");
            outF.write("    <title>" + m_title + "</title>\n");
            outF.write("    <author></author>\n");
            outF.write("    <comments></comments>\n");
            for (int i = 0; i < m_workflowsList.size(); i++) {
                outF.write("    <workflow>" + m_workflowsList.get(i).getTitle() + "</workflow>\n");
            }
            for (int i = 0; i < m_workflowsList.size(); i++) {
                m_workflowsList.get(i).save();
            }
            outF.write("</BotoSeisLine>");
            outF.close();
        } catch (IOException ex) {
        }

    }

    public void reloadFlow() {
        String fsep = System.getProperty("file.separator");
        String xmlFile = m_homeDir + fsep + "info.xml";
        File of = new File(xmlFile);
        FileWriter outF;
        try {
            outF = new FileWriter(of);

            outF.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            outF.write("<BotoSeisLine>\n");
            outF.write("    <title>" + m_title + "</title>\n");
            outF.write("    <author></author>\n");
            outF.write("    <comments></comments>\n");
            for (int i = 0; i < m_workflowsList.size(); i++) {
                outF.write("    <workflow>" + m_workflowsList.get(i).getTitle() + "</workflow>\n");
            }

//             for (int i = 0; i < m_workflowsList.size(); i++) {
//                m_workflowsList.get(i).save();
//            }
            outF.write("</BotoSeisLine>");
            outF.close();
        } catch (IOException ex) {
        }

    }

    public boolean addWorkflow(String pTitle) {
        // Test for duplicated title
        boolean titleDuplicated = false;
        for (int i = 0; i < m_workflowsList.size(); i++) {
            if (m_workflowsList.get(i).getTitle().equalsIgnoreCase(pTitle)) {
                return true;
            }

        }

        if (!titleDuplicated) {
            String fsep = System.getProperty("file.separator");
            String apath = m_homeDir + fsep + pTitle;
            java.io.File f = new java.io.File(apath);
            if (f.mkdir()) {
                botoseis.mainGui.workflows.WorkflowModel newFlow = new botoseis.mainGui.workflows.WorkflowModel(apath,
                        pTitle);
                m_workflowsList.add(newFlow);

                String jobp = m_homeDir + fsep + pTitle + fsep + "/jobs";
                f.mkdir();
            } else {
            }

            apath = m_homeDir + fsep + pTitle + "/jobs";
            java.io.File jobF = new java.io.File(apath);
            if (jobF.mkdir()) {
            } else {
            }
            return false;
        }
        return false;
    }

    public String getTitle() {
        return m_title;
    }

    public String getHomedir() {
        return m_homeDir;
    }

    public java.util.Vector<botoseis.mainGui.workflows.WorkflowModel> getWorkflowModel() {
        return m_workflowsList;
    }

    public botoseis.mainGui.workflows.WorkflowModel selectWorkflow(String pTitle) {
        for (int i = 0; i < m_workflowsList.size(); i++) {
            if (m_workflowsList.get(i).getTitle().equalsIgnoreCase(pTitle)) {
                return m_selectedFlow = m_workflowsList.get(i);
            }
        }
        return null;
    }

    public void rename(String newName) {
        this.m_title = newName;
        String fsep = System.getProperty("file.separator");
        File file = new File(m_homeDir);
        File rename = new File(file.getParent() + fsep + newName);
        file.renameTo(rename);
        this.m_homeDir = rename.getPath();
        for (int i = 0; i < m_workflowsList.size(); i++) {
            m_workflowsList.get(i).setHomedir(m_homeDir + fsep + m_workflowsList.get(i).getTitle());
        }
    }

    public void removeFlow(botoseis.mainGui.workflows.WorkflowModel wm) {
        for (int i = 0; i < m_workflowsList.size(); i++) {
            if (wm == m_workflowsList.get(i)) {
                m_workflowsList.remove(i);
            }
        }
        reloadFlow();
    }

    @Override
    public String toString() {
        return m_title;
    }
    // public Workflow.WorkflowView getSelectedWorkflow() {
    //   return m_selectedFlow;
    //}
    // Variable declarations
    private java.util.Vector<botoseis.mainGui.workflows.WorkflowModel> m_workflowsList;
    private botoseis.mainGui.workflows.WorkflowModel m_selectedFlow;
    private String m_homeDir;
    private String m_title;

    public void setTitle(String title) {
        this.m_title  = title;
    }
}
