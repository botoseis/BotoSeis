/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package botoseis.mainGui.prmview;

import botoseis.mainGui.utils.Utils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FilePathParameterView extends ParameterView {

    public FilePathParameterView(ProcessParameter p) {
        super(p);
        
        m_input.setMinimumSize(new java.awt.Dimension(200, 20));
        m_input.setMaximumSize(new java.awt.Dimension(200, 20));
        m_input.setPreferredSize(new java.awt.Dimension(200, 20));

        add(m_input);


        javax.swing.JButton btn = new javax.swing.JButton("...");

        btn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ev) {
                javax.swing.JFileChooser jfc = new javax.swing.JFileChooser(
                        new File(m_input.getText().trim().equals("") ? Utils.getCurrentPath() :
                             new File(m_input.getText()).exists() ? m_input.getText() : Utils.getCurrentPath()));
                int ret = jfc.showOpenDialog(null);
                if (ret == javax.swing.JFileChooser.APPROVE_OPTION) {
                    m_input.setText(jfc.getSelectedFile().toString());
                    Utils.setCurrentPath(m_input.getText());
                }
            }
        });

        add(btn);
        
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
    
    // Variables declaration
    private javax.swing.JTextField m_input = new javax.swing.JTextField();

}
