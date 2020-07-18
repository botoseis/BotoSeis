package botoseis.mainGui.prmview;

/*
 * ParameterView.java
 *
 * Created on January 6, 2008, 5:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
public abstract class ParameterView extends javax.swing.Box {

    public ParameterView(ProcessParameter prm) {
        super(javax.swing.BoxLayout.X_AXIS);
        
        m_prm = new ProcessParameter();
        
        m_prm.brief = prm.brief;
        m_prm.defaultValue = prm.defaultValue;
        m_prm.keyvaluePair = prm.keyvaluePair;
        m_prm.name = prm.name;
        m_prm.optionValues = prm.optionValues;
        m_prm.optionsListSelectionType = prm.optionsListSelectionType;
        m_prm.type = prm.type;        
    }
    
    abstract public void setValue(String v);
    
    abstract public String getValue();
    
    abstract public String getKeyValuePair();
    
    abstract public String getCommandLine();
    

    // Variable declarations
    javax.swing.JLabel m_labelBrief = new javax.swing.JLabel();
    protected ProcessParameter m_prm;
}
