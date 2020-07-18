package botoseis.mainGui.workflows;

import java.util.Vector;
/*
 * ProcessModel.java
 *
 * Created on December 26, 2007, 5:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

public class ProcessModel {

    /** Creates a new instance of ProcessModel */
    public ProcessModel() {
        m_parameters = new Vector<ParametersGroup>();
    }

    public void setTitle(String pTitle) {
        m_title = pTitle;
    }

    public String getTitle() {
        return m_title;
    }

    public void setBrief(String pBrief) {
        m_brief = pBrief;
    }

    public String getBrief() {
        return m_brief;
    }

    public void setAuthorName(String pName) {
        m_authorName = pName;
    }

    public String getAuthorName() {
        return m_authorName;
    }

    public void setAuthorEMail(String pEMail) {
        m_authorEMail = pEMail;
    }

    public String getAuthorEmail() {
        return m_authorEMail;
    }

    public void setExecutable(String pPath) {
        m_exePath = pPath;
    }

    public String getExecutablePath() {
        return m_exePath;
    }

    public void addParametersGroup(ParametersGroup pGroup) {
        m_parameters.add(pGroup);
    }

    public void setParameters(Vector<ParametersGroup> p) {
        m_parameters.clear();
        for (int i = 0; i < p.size(); i++) {
            m_parameters.add(p.get(i));
        }
    }

    public Vector<ParametersGroup> getParameters() {
        return m_parameters;
    }

    public void setID(String p) {
        m_ID = p;
    }

    public String getID() {
        return m_ID;
    }

    @Override
    public String toString() {
        return m_title;
    }

    public String getCallingConvention() {
        return m_callConvention;
    }

    public void setCallingConvention(String s) {
        m_callConvention = s;
    }

    public void setHasInput(boolean f) {
        m_hasInput = f;
    }

    public void setHasOutput(boolean f) {
        m_hasOutput = f;
    }

    public boolean hasInput() {
        return m_hasInput;
    }

    public boolean hasOutput() {
        return m_hasOutput;
    }
    private String m_title;
    private String m_brief;
    private String m_authorName;
    private String m_authorEMail;
    private String m_exePath;
    private String m_ID;
    private boolean m_hasInput;
    private boolean m_hasOutput;
    private String m_callConvention;    
    private Vector<ParametersGroup> m_parameters;
}
