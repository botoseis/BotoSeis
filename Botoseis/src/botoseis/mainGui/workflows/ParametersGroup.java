package botoseis.mainGui.workflows;

import botoseis.mainGui.prmview.ProcessParameter;
import java.util.Vector;
/*
 * ParametersGroup.java
 *
 * Created on December 27, 2007, 12:05 AM
 * 
 */

public class ParametersGroup {        
    public ParametersGroup(){
        m_plist = new Vector<ProcessParameter>();
    }
    
    public ParametersGroup(String name, String desc) {
        m_name = name;
        m_desc = desc;
        m_plist = new Vector<ProcessParameter>();
    }   
    
    public void addParameter(ProcessParameter p){
        m_plist.add(p);
    }
    
    public void removeParameter(String pName){
        for(int i = 0; i < m_plist.size(); i++){
            if(m_plist.get(i).name.equalsIgnoreCase(pName)){
                m_plist.remove(i);
                break;
            }
        }
    }
    
    public Vector<ProcessParameter> getParameters(){
        return m_plist;
    }
    
    public void setDescription(String desc){
        m_desc = desc;
    }
    
    public String getDescription(){
        return m_desc;
    }
    
    public void setGroupName(String pName){
        m_name = pName;
    }
    
    public String getGroupName(){
        return m_name;
    }
    
    private Vector<ProcessParameter> m_plist;
    private String m_name;
    private String m_desc;
}
