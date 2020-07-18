/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package botoseis.mainGui.workflows;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import botoseis.mainGui.workflows.ProcessModel;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 *
 * @author gabriel
 */
public class WorkflowProcess {

    public WorkflowProcess(ProcessModel pModel, botoseis.mainGui.prmview.ParametersSource prmSource) {
        m_prmSource = prmSource;
        m_execStatus = WorkflowJob.STARTING;
        m_model = pModel;
        lengthProcessed = 0L;

        last = false;
    }

    public void start(String homeDir) {

        lengthProcessed = 0;
        m_timeStop = 0;

        try {
            java.util.Vector<String> cmd = m_prmSource.getParametersInline();
            cmd.add(0, m_model.getExecutablePath());
            pb = new ProcessBuilder(cmd);
            pb.directory(new java.io.File(homeDir));
            m_proc = pb.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        status = WorkflowJob.RUNNING;
        flowLog.append("\nProcess started: " + this.getTitle() + " \n");
        Thread log = new Thread() {

            @Override
            public void run() {

                try {
                    int b = 0;
                    b = m_proc.getErrorStream().read();
                    do {
                        flowLog.append(String.valueOf((char) b));

                        if (flowLog.checkError()) {
                            status = WorkflowJob.ERROR;
                        }
                        if (status.equals(WorkflowJob.STOPPED)) {
                            break;
                        }

                        b = m_proc.getErrorStream().read();
                    } while (b >= 0);

//                    if (last) {
//                        m_timeStop = System.currentTimeMillis();
//                    }
                    if (!status.equals(WorkflowJob.STOPPED)) {
                        status = WorkflowJob.COMPLETED;
                    }
                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }
        };
        log.start();

        Thread output = new Thread() {

            @Override
            public void run() {

                try {
                    if (input != null) {
                        BufferedOutputStream buffout = new BufferedOutputStream(getOutStream());
                        BufferedInputStream buffInp = new BufferedInputStream(input);
                        int b = 0;
                        byte[] len = new byte[1024];
                        do {
                            if (status.equals(WorkflowJob.STOPPED)) {
                                break;
                            }
                            b = buffInp.read(len);
                            if (b >= 0) {
                                buffout.write(len, 0, b);
                                lengthProcessed += len.length;
                            }
                        } while (b >= 0);

                        buffInp.close();
                        buffout.close();
                    }

                    if (last) {
                        BufferedInputStream buffInp = new BufferedInputStream(getInputStream());
                        int b = 0;
                        while ((b = buffInp.read()) >= 0) {
                            m_console.append(String.valueOf((char) b));
                        }
                        m_console.append("---------------------------- END ---------------------------------");
                        m_console.append("\n\n");
                         m_timeStop = System.currentTimeMillis();
                        status = WorkflowJob.COMPLETED;
                        getInputStream().close();
                        getErrorStream().close();
                        getOutStream().close();
                        input.close();
                    }

                } catch (Exception e) {
//                    e.printStackTrace();
                    try{
                        getInputStream().close();
                        getErrorStream().close();
                        getOutStream().close();
                        input.close();
                        m_timeStop = System.currentTimeMillis();
                        m_console.append("---------------------------- END ---------------------------------");
                        m_console.append("\n\n");
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    status = WorkflowJob.STOPPED;
                }
            }
        };
        output.start();



    }

    public WorkflowProcess clone() {
        WorkflowProcess wp = new WorkflowProcess(m_model, m_prmSource.clone(m_model));
        wp.setReviewed(reviewed);
        wp.m_flowID = m_flowID;
        return wp;
    }

    public botoseis.mainGui.prmview.ParametersSource getParametersSource() {
        return m_prmSource;
    }

    public ProcessModel getModel() {
        return m_model;
    }

    public String getWorkflowID() {
        return m_flowID;
    }

    public void setWorkflowID(String s) {
        m_flowID = s;
    }

    public String getModelID() {
        return m_model.getID();
    }

    public String getLastCmdLine() {
        return m_lastCmdLine;
    }

    public String getTitle() {
        return m_model.getTitle();
    }

    public void setTimeStop(Long value) {
        m_timeStop = value;
    }

    public void stop() {
        try {
            m_proc.getErrorStream().close();
            m_proc.getInputStream().close();
            m_proc.getOutputStream().close();
            status = WorkflowJob.STOPPED;
            m_proc.destroy();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public boolean isReviewed() {
        return reviewed;
    }

    public void setReviewed(boolean reviewed) {
        this.reviewed = reviewed;
    }
    
   

    public String getStrReviewd() {
        if (this.isReviewed()) {
            return "true";
        } else {
            return "false";
        }
    }

    public boolean getBoReviewd(String str) {
        if (str.equalsIgnoreCase("true")) {
            return true;
        } else {
            return false;
        }
    }

    public OutputStream getOutStream() {
        return m_proc.getOutputStream();
    }

    public Long getLentghProcessed() {
        return lengthProcessed;
    }

    public InputStream getInputStream() {
        return m_proc.getInputStream();
    }

    public void setInputStream(InputStream input) {
        this.input = input;
    }

    public InputStream getErrorStream() {
        return m_proc.getErrorStream();
    }

    public void setFlowLog(WorkflowLogDlg flowLog) {
        this.flowLog = flowLog;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public long getTimeStop() {
        return m_timeStop;
    }

    public String getStatus() {
        return status;
    }

    public void open(String homeDir) {
        String flowID = m_flowID.replace("/", "-");
        String fsep = System.getProperty("file.separator");
        String nlFile = homeDir + fsep + flowID + ".nl";
        m_prmSource.loadFromFile(nlFile);
    }

    public void save(String homeDir) {
        String flowID = m_flowID.replace("/", "-");
        String fsep = System.getProperty("file.separator");
        String nlFile = homeDir + fsep + flowID + ".nl";
        m_prmSource.saveToFile(nlFile);
    }
    private ProcessBuilder pb;
    private Process m_proc = null;
    private String m_execStatus;
    private String m_flowID;
    private ProcessModel m_model;
    private botoseis.mainGui.prmview.ParametersSource m_prmSource;
    private String m_lastCmdLine;
    //-------------------------------
    private boolean reviewed;
    private WorkflowLogDlg flowLog;
    private InputStream input;
    private long lengthProcessed;
    private String status;
    private boolean last;
    private long m_timeStop;
    private JTextArea m_console;

    void setConsole(JTextArea m_console) {
        this.m_console = m_console;
    }
    //-------------------------------//
}
