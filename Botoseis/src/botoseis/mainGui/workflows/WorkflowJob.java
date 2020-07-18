/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package botoseis.mainGui.workflows;

import java.util.Date;
import botoseis.mainGui.workflows.WorkflowModel;


/**
 *
 * @author gabriel
 */
public class WorkflowJob {

    public WorkflowJob(String homeDir, WorkflowModel wm, javax.swing.JTextArea console) {
        m_homeDir = homeDir;
        m_workflow = wm.clone();
        m_console = console;
        m_procList = m_workflow.getProcListExec();
        status = STARTING;
        flowLog = new WorkflowLogDlg();

    }

    public void start() {
        m_logFile = new java.io.File(m_homeDir + "/log.txt");
        String cmd = "";
         for (int i = 0; i < m_procList.size(); i++) {
            WorkflowProcess wp = m_procList.get(i);
            cmd += wp.getModel().getExecutablePath()+" ";
            java.util.Vector<String> pl = wp.getParametersSource().getParametersInline();
            for (int j = 0; j < pl.size(); j++) {
                cmd += pl.get(j)+" ";
            }
            if(i != m_procList.size()-1)
                cmd+= " | ";      
        }
        Date current = new Date(System.currentTimeMillis());
        flowLog.append(formatCenter("BotoSeis v1.0 (c) 2008\n"));
        flowLog.append(formatCenter("Developed at Federal University of Para\n"));
        flowLog.append(formatCenter("Geophysics Department\n\n"));
        flowLog.append(formatLeft("Executing workflow: " + m_workflow.getTitle(), 0));
        flowLog.append("Start time: "+ current.toLocaleString());
        flowLog.append(formatLeft("Command executed: " + cmd, 1));

        m_console.append("Executing workflow: " + m_workflow.getTitle()+"\n");
        m_console.append("Workflow out:\n");

        m_timeStart = System.currentTimeMillis();
        new Thread(new Runnable() {

            public void run() {
                for (int i = 0; i < m_procList.size(); i++){
                    WorkflowProcess wp = m_procList.get(i);
                    wp.setFlowLog(flowLog);
                    
                    if (i != 0) {
                        WorkflowProcess w = m_procList.get( i -1);
                        wp.setInputStream(w.getInputStream());
                    }
                    if (i == m_procList.size() - 1) {
                        wp.setLast(true);
                        wp.setConsole(m_console);
                    }
                    wp.start(m_homeDir);
                }
            }
        }).start();
    }


    public void setStatus(String status){
        this.status = status;
    }

    public WorkflowLogDlg getFlowLog(){
        return  flowLog;
    }


      String formatCenter(String s) {
        int pad = COLS / 2 - s.length() / 2;
        String s2 = "";
        for (int i = 0; i < pad; i++) {
            s2 += " ";
        }
        s2 += s;
        return s2;
    }

    private String formatLeft(String s, int indent) {
        int pad = indent * INDENT_SIZE;
        String s2 = "";
        for (int i = 0; i < pad; i++) {
            s2 += " ";
        }
        s2 += s;
        return s2;
    }

    private String formatRight(String s) {
        int pad = COLS - s.length();
        String s2 = "";
        for (int i = 0; i < pad; i++) {
            s2 += " ";
        }
        s2 += s;
        return s2;
    }


    public Date getTimeStart() {
        return new Date(m_timeStart);
    }

    public Date getTimeStop() {
        if (m_procList.lastElement().getTimeStop() == 0) {
            return null;
        } else {
            return new Date(m_procList.lastElement().getTimeStop());
        }
    }

    public String getDuration() {
        float value;
        if (m_procList.lastElement().getTimeStop() != 0) {
            long time = m_procList.lastElement().getTimeStop() - m_timeStart;
            if(time < 60000){
                value = time / 1000;
            return String.format("%.2f sec",value );
            }else{
                value = time / 1000;
                value = value/60;
                return String.format("%.2f min", value);
            }
        }
        return "";
    }

    public String getProcessed(){

        if(m_procList == null){
            return "unkonwn";
        }
        double value = 0;
        for (int i = 0; i < m_procList.size(); i++) {
            WorkflowProcess wp = m_procList.get(i);
            value += wp.getLentghProcessed();
        }
        value = value / (m_procList.size()-1);
        return String.format("%.2f", (value/1048576));
    }

    public void stop(){
        m_procList.lastElement().setTimeStop(System.currentTimeMillis());
        status = WorkflowJob.STOPPED;
        for (int i = 0; i < m_procList.size(); i++) {
            WorkflowProcess wp = m_procList.get(i);
            wp.stop();
        }
    }


    public String getTitle() {
        return m_workflow.getTitle();
    }

    public String getHomeDir(){
        return m_homeDir;
    }

    public String getStatus(){
         if(flowLog.checkError()){
            return WorkflowJob.ERROR;
        }
        return m_procList.lastElement().getStatus();
    }

    String m_homeDir;
    WorkflowModel m_workflow;
    private int m_execProcessID;
    private javax.swing.Timer m_timer;
    private Long m_timeStart;
    private java.io.File m_logFile;
    java.util.Vector<botoseis.mainGui.workflows.WorkflowProcess> m_procList = new java.util.Vector<botoseis.mainGui.workflows.WorkflowProcess>();
    javax.swing.JTextArea m_console;
    // Constants
    private int COLS = 80;
    private int INDENT_SIZE = 4;
    private String status;
    public static String STARTING = "Starting";
    public static String RUNNING = "Running...";
    public static String COMPLETED = "Completed";
    public static String STOPPED = "Stopped";
    public static String ERROR = "Error";
    private WorkflowLogDlg flowLog;

}


