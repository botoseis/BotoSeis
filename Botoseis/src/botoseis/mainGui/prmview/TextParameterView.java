package botoseis.mainGui.prmview;

/*
 * TextParameterView.java
 *
 * Created on January 6, 2008, 10:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
public class TextParameterView extends ParameterView {

    /** Creates a new instance of TextParameterView */
    public TextParameterView(ProcessParameter p) {
        super(p);

        m_input.setMinimumSize(new java.awt.Dimension(200, 20));
        m_input.setMaximumSize(new java.awt.Dimension(200, 20));
        m_input.setPreferredSize(new java.awt.Dimension(200, 20));

        add(m_input);

        m_labelBrief.setText(m_prm.brief);
        add(m_labelBrief);
    }

    @Override
    public void setValue(String v) {
        m_input.setText(v);
    }

    @Override
    public String getValue() {
        return m_input.getText();
    }

    @Override
    public String getKeyValuePair() {
        return m_prm.name + "=" + m_input.getText();
    }

    @Override
    public String getCommandLine() {
        String ret = "";
        if (m_prm.keyvaluePair.equalsIgnoreCase(ProcessParameter.KEYVALUEPAIR)) {
            ret = m_prm.name + "=" + m_input.getText();
        }else{
            ret = m_input.getText();
        }
        return ret;
    }
    // Varaibles declaration
    private javax.swing.JTextField m_input = new javax.swing.JTextField();
}
