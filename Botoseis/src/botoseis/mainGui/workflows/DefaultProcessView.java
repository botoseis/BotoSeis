package botoseis.mainGui.workflows;

import botoseis.mainGui.utils.Utils;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;

/*
 * 
 */
public class DefaultProcessView extends javax.swing.JPanel {

    DefaultProcessView(WorkflowProcess pProc, javax.swing.JPanel prmHostPanel, botoseis.mainGui.workflows.WorkflowView wv) {
        super();
        this.m_workView = wv;
        m_prmHostPanel = prmHostPanel;
        m_proc = pProc;

        setLayout(null);
        setSize(BlockWidth, BlockHeight);

        setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        m_title = new javax.swing.JLabel(pProc.getTitle());
        m_title.setBounds(2, 2, 119, 20);
        m_title.setForeground(new java.awt.Color(255, 255, 102));
        m_title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        m_title.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        m_title.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                onTitleMouseClicked(evt);
            }
        });

        add(m_title);
        setColor(BlockColor);
        if (!pProc.isReviewed()) {
            uncomment();
        } else {
            comment();
        }
    }

    public void setSelected(boolean f) {
        if (f) {
            m_prmHostPanel.removeAll();
            ((javax.swing.border.TitledBorder) m_prmHostPanel.getBorder()).setTitle(m_proc.getTitle());
            m_title.setBorder(javax.swing.BorderFactory.createEtchedBorder(
                    javax.swing.border.EtchedBorder.RAISED, java.awt.Color.red, java.awt.Color.red));
            m_proc.getParametersSource().showParameters(m_prmHostPanel);
            JLabel jl = new JLabel(Utils.getPathProcess(m_workView.getModel().getHomedir()));
            jl.setFont(new Font("Times new roman", Font.BOLD, 16));
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.ipady = 0;       //reset to default
            c.weighty = 1.0;   //request any extra vertical space
            c.anchor = GridBagConstraints.PAGE_END; //bottom of space
            c.insets = new Insets(10, 0, 0, 0);  //top padding
            c.gridx = 0;       //aligned with button 2
            c.gridwidth = 2;   //2 columns wide
            c.gridy = 100;       //third row
            m_prmHostPanel.add(jl,c);
            m_prmHostPanel.updateUI();
        } else {
            m_title.setBorder(javax.swing.BorderFactory.createEtchedBorder(
                    javax.swing.border.EtchedBorder.RAISED));
        }
    }

    private void onTitleMouseClicked(java.awt.event.MouseEvent evt) {
        evt.setSource(this);
        processMouseEvent(evt);
    }
    //-------------------------------

    public WorkflowProcess getWorkflowProcess() {
        return m_proc;
    }

    public void setColor(Color color) {
        this.setBackground(color);
    }

    public void comment() {
        m_title.setForeground(new Color(0, 0, 0));
        m_title.setEnabled(false);

        setColor(Color.lightGray);
        setEnabled(false);

        m_proc.setReviewed(true);
    }

    public void uncomment() {
        m_title.setForeground(new java.awt.Color(255, 255, 102));
        m_title.setEnabled(true);
        //m_title.setForeground(new java.awt.Color(255, 255, 102));
        m_proc.setReviewed(false);
        setEnabled(true);
        setColor(BlockColor);
    }

    public botoseis.mainGui.workflows.WorkflowView getWorkflowView() {
        return m_workView;
    }    //-------------------------------//
    //
    public static final int BlockWidth = 122;
    public static final int BlockHeight = 35;
    public static final java.awt.Color BlockColor = new java.awt.Color(96, 119, 160);
    //-------------------------------
    public static final java.awt.Color BlockReviewedColor = new java.awt.Color(158, 211, 208);
    //-------------------------------//
    private javax.swing.JLabel m_title;
    private botoseis.mainGui.workflows.WorkflowView m_workView;
    WorkflowProcess m_proc;
    javax.swing.JPanel m_prmHostPanel;
}
