/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package botoseis.mainGui.prmview;

public class OptionsParameterView extends ParameterView {

    public OptionsParameterView(ProcessParameter p) {
        super(p);

        m_input.setMinimumSize(new java.awt.Dimension(100, 20));
        m_input.setMaximumSize(new java.awt.Dimension(100, 20));
        m_input.setPreferredSize(new java.awt.Dimension(100, 20));

        String[] l = m_prm.optionValues.split(",");
        javax.swing.DefaultComboBoxModel cm = new javax.swing.DefaultComboBoxModel(l);
        m_input.setModel(cm);

        add(m_input);

        m_labelBrief.setText(m_prm.brief);
        add(m_labelBrief);
    }

    @Override
    public void setValue(String v) {
        try{
            m_input.setSelectedIndex(Integer.parseInt(v));
        }catch(NumberFormatException e){
            int id = 0;
            
            String[] l = m_prm.optionValues.split(",");
            
            for(int i = l.length-1; i >= 0; i--){
                if(l[i].equalsIgnoreCase(v)){
                    id = i;
                    break;
                }
            }
            m_input.setSelectedIndex(id);
        }
    }

    @Override
    public String getValue() {
        if (m_prm.optionsListSelectionType.equalsIgnoreCase(ProcessParameter.OPTIONS_SELECTION_TEXT)) {
            return m_input.getSelectedItem().toString();
        }        
        String s = String.format("%d", m_input.getSelectedIndex());
        return s;
    }
    
    @Override
    public String getCommandLine() {
        String ret = "";
        if (m_prm.keyvaluePair.equalsIgnoreCase(ProcessParameter.KEYVALUEPAIR)) {
            ret = m_prm.name + "=" + getValue();
        } else {
            ret = getValue();
        }
        return ret;
    }

    @Override
    public String getKeyValuePair() {
        return m_prm.name + "=" + getValue();
    }
    
    // Variables declaration
    private javax.swing.JComboBox m_input = new javax.swing.JComboBox();
}
