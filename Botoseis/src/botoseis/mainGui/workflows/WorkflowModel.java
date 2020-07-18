package botoseis.mainGui.workflows;

/*
 * WorkflowModel.java
 *
 * Created on January 5, 2008, 4:24 PM
 *
 * 
 */
import java.util.Enumeration;

import java.io.*;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.util.Vector;
import botoseis.mainGui.workflows.WorkflowProcess;

public class WorkflowModel extends java.util.Observable {

    /** Creates a new instance of FlowchartModel */
    public WorkflowModel(String home, String title) {
        m_homeDir = home;
        m_title = title;
        m_nextProcessID = 0;
        m_procList = new java.util.Vector<WorkflowProcess>();
    }

    public String getHomedir() {
        return m_homeDir;
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
        setChanged();
        notifyObservers();
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
                    if (node.getNodeName().equalsIgnoreCase("process")) {
                        readProcessInfo(node);
                    }
                    break;
            }
        }
    }

    private void readProcessInfo(Node pParent) {
        NodeList nodes = pParent.getChildNodes();
        int len = (nodes != null) ? nodes.getLength() : 0;
        Node node;
        WorkflowProcess wp = null;
        for (int i = 0; i < len; i++) {
            node = nodes.item(i);
            switch (node.getNodeType()) {
                case Node.TEXT_NODE:
                    break;
                case Node.ELEMENT_NODE:
                    if (node.getNodeName().equalsIgnoreCase("title")) {
                        String pTitle = node.getChildNodes().item(0).getNodeValue();
                    }
                    if (node.getNodeName().equalsIgnoreCase("processID")) {
                        String id = node.getChildNodes().item(0).getNodeValue();
                        ProcessModel pm = botoseis.mainGui.admin.ManageProcessesDlg.getProcess(id);
                        botoseis.mainGui.prmview.ParametersPanel pp = new botoseis.mainGui.prmview.ParametersPanel(pm);
                        wp = new WorkflowProcess(pm, pp);
                        m_procList.add(wp);
                    }
                    if (node.getNodeName().equalsIgnoreCase("workflowID")) {
                        wp.setWorkflowID(node.getChildNodes().item(0).getNodeValue());
                        wp.open(m_homeDir);
                    }
                    //-------------------------------
                    if (node.getNodeName().equalsIgnoreCase("processReviewd")) {
                        String rev = node.getChildNodes().item(0).getNodeValue();
                        wp.setReviewed(wp.getBoReviewd(rev));
                    }
                    //-------------------------------//
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
            outF.write("<BotoSeisWorkflow>\n");
            outF.write("    <title>" + m_title + "</title>\n");
            outF.write("    <author></author>\n");
            outF.write("    <comments></comments>\n");
            for (int i = 0; i < m_procList.size(); i++) {
                outF.write("    <process>\n");
                outF.write("       <processID>" + m_procList.get(i).getModelID() + "</processID>\n");
                //-------------------------------
                outF.write("       <processReviewd>" + m_procList.get(i).getStrReviewd() + "</processReviewd>\n");
                //-------------------------------//
                outF.write("       <workflowID>" + m_procList.get(i).getWorkflowID() + "</workflowID>\n");
                outF.write("    </process>\n");
                m_procList.get(i).save(m_homeDir);
            }
            outF.write("</BotoSeisWorkflow>");
            outF.close();
        } catch (IOException ex) {
        }

    }

    public void moveProcessToTop(int index) {
        if ((index > 0) && (index < m_procList.size())) {
            WorkflowProcess p = m_procList.elementAt(index);
            m_procList.remove(index);
            m_procList.add(0, p);
            setChanged();
            notifyObservers();
        }
    }

    public void moveProcessToBottom(int index) {
        if ((index >= 0) && (index < (m_procList.size() - 1))) {
            WorkflowProcess p = m_procList.elementAt(index);
            m_procList.remove(index);
            m_procList.add(p);
            setChanged();
            notifyObservers();
        }
    }

    public WorkflowModel clone(){
        WorkflowModel clone = new WorkflowModel(m_homeDir, m_title);
        Vector<WorkflowProcess> vector = new Vector<WorkflowProcess>();
        for (int i = 0; i < m_procList.size(); i++) {
            WorkflowProcess wp = m_procList.get(i);
            vector.add(wp.clone());
        }
        clone.m_procList = vector;

        return clone;
    }

    public void moveProcessUp(int index) {
        if ((index > 0) && (index < m_procList.size())) {
            WorkflowProcess p = m_procList.elementAt(index);
            m_procList.remove(index);
            m_procList.add(index - 1, p);
            setChanged();
            notifyObservers();
        }
    }

    public void moveProcessDown(int index) {
        if ((index >= 0) && (index < (m_procList.size() - 1))) {
            WorkflowProcess p = m_procList.elementAt(index);
            m_procList.remove(index);
            m_procList.add(index + 1, p);                       
            setChanged();
            notifyObservers();
        }
    }

    public String getTitle() {
        return m_title;
    }

       public void setTitle(String title) {
        this.m_title = title;
    }

    public void addProcess(WorkflowProcess wp) {
        m_nextProcessID++;

        java.util.GregorianCalendar today = new java.util.GregorianCalendar();

        String id = wp.getTitle() + "-";

        id += String.format("%d", today.getTimeInMillis());

        wp.setWorkflowID(id);

        m_procList.add(wp);

        setChanged();
        notifyObservers();
    }

    public void addProcess(WorkflowProcess wp, int position) {
        if (position < 0) {
            addProcess(wp);
        } else {
            m_nextProcessID++;

            java.util.GregorianCalendar today = new java.util.GregorianCalendar();

            String id = wp.getTitle() + "-";

            id += String.format("%d", today.getTimeInMillis());

            wp.setWorkflowID(id);

            m_procList.add(position,wp);

            setChanged();
            notifyObservers();
        }

    }

    public void removeProcess(int index) {
        if ((index >= 0) && (index < m_procList.size())) {
            m_procList.remove(index);
        }

        setChanged();
        notifyObservers();
    }

    public Enumeration processes() {
        return m_procList.elements();
    }

    public java.util.Vector<WorkflowProcess> getProcList() {
        return m_procList;
    }

    public void renameFlow(String newName) {
        this.m_title = newName;
        String fsep = System.getProperty("file.separator");
        File file = new File(m_homeDir);
        File rename = new File(file.getParent() + fsep + newName);
        file.renameTo(rename);
        this.m_homeDir = rename.getPath();

    }
    //-------------------------------

    public void setHomedir(String path) {
        this.m_homeDir = path;
    }

    public java.util.Vector<WorkflowProcess> getProcListExec() {
        Vector<WorkflowProcess> procListExec = new Vector<WorkflowProcess>();
        for (int i = 0; i < m_procList.size(); i++) {
            if (!m_procList.get(i).isReviewed()) {
                procListExec.add(m_procList.get(i));
            }
        }
        return procListExec;
    }

    public void remove() {
        botoseis.mainGui.utils.Utils.deleteFile(new File(m_homeDir));
    }

  

    //-------------------------------//
    @Override
    public String toString() {
        return m_title;
    }
    //
    java.util.Vector<WorkflowProcess> m_procList;
    //
    private int m_nextProcessID = 0;
    private String m_title;
    private String m_homeDir;
    private javax.swing.Timer m_timer;
    private int m_execProcessID;
    private WorkflowProcess m_execProcess;

    public void setProcList(Vector<WorkflowProcess> procList) {
        m_procList = procList;
    }

 
}
