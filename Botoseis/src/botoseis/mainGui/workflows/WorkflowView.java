package botoseis.mainGui.workflows;

import botoseis.mainGui.workflows.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Observable;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import botoseis.mainGui.main.MainWindow;
import botoseis.mainGui.workflows.WorkflowProcess;
import botoseis.mainGui.utils.DefaultNode;

/*
 * WorkflowView.java
 *
 * Created on January 5, 2008, 4:21 PM
 *
 * Graphical representation of a workflow.
 */
public class WorkflowView extends javax.swing.JPanel implements java.util.Observer {

    public WorkflowView(javax.swing.JPanel prmPanel) {
        m_state = SelectionMode;
        m_insertMarkPosition = -1;
        m_prmPanel = prmPanel;

        setLayout(null);

        addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {

                m_insertMarkPosition = -1;
                int bx;
                int by;
                for (int i = 0; i < m_procList.size(); i++) {
                    bx = m_procList.get(i).getLocation().x;
                    by = m_procList.get(i).getLocation().y;

                    if ((evt.getPoint().x >= bx)
                            && (evt.getPoint().x <= (bx + DefaultProcessView.BlockWidth))
                            && (evt.getPoint().y <= by)) {
                        m_insertMarkPosition = i;
                        repaint();
                        break;
                    }
                }

                if (evt.getButton() == MouseEvent.BUTTON3) {
                    JPopupMenu jpm = new JPopupMenu();
                    JMenuItem jmi = new JMenuItem("Paste processes");
                    jmi.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            DefaultNode node = MainWindow.NODECOPY;
                            if (node != null) {
                                if (node.getType() == DefaultNode.FLOW_TYPE) {
                                    WorkflowModel wm = (WorkflowModel) node.getUserObject();
                                    for (int i = 0; i < wm.getProcList().size(); i++) {
                                        if (m_insertMarkPosition > 0) {
                                            m_model.addProcess(wm.getProcList().get(i).clone(), m_insertMarkPosition);
                                            m_insertMarkPosition++;
                                        } else {
                                            m_model.addProcess(wm.getProcList().get(i).clone());
                                        }
                                    }
                                }

                            }
                        }
                    });
                    jpm.add(jmi);
                    jpm.show(evt.getComponent(), evt.getX(), evt.getY());
                }

                repaint();
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
            }
        });

        m_procList = new java.util.Vector<botoseis.mainGui.workflows.DefaultProcessView>();

        m_selectedProcess = null;

    }

    public int getSelectedProcessIndex() {
        for (int t = 0; t < m_procList.size(); t++) {
            if (m_selectedProcess == m_procList.get(t)) {
                return t;
            }
        }
        return -1;
    }

    public static javax.swing.JPanel getParametersPanel() {
        return m_prmPanel;
    }

    public int getInsertMarkposition() {
        return m_insertMarkPosition;
    }

    public void clear() {
        m_model = null;
        m_procList.clear();
        removeAll();
        repaint();
    }

    public void setModel(botoseis.mainGui.workflows.WorkflowModel m) {
        if (m != null) {
            m_model = m;
            m_model.addObserver(this);

            m_procList.clear();
            removeAll();

            java.util.Enumeration en = m.processes();
            while (en.hasMoreElements()) {
                WorkflowProcess w = (WorkflowProcess) en.nextElement();
                DefaultProcessView pv = new DefaultProcessView(w, m_prmPanel, this);
                add(pv);
                m_procList.add(pv);

                pv.addMouseListener(new java.awt.event.MouseAdapter() {
                    //-------------------------------

                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        super.mouseClicked(evt);

                        DefaultProcessView procView = (DefaultProcessView) evt.getSource();
                        onProcessMouseClicked(evt);
                        if (evt.getButton() == MouseEvent.BUTTON3) {
                            
                            JPopupMenu jpm = new JPopupMenu();
                            JMenu_Item jmi;
                            if (procView.getWorkflowProcess().isReviewed()) {
                                jmi = new JMenu_Item("Uncomment", procView);
                                jmi.setAction(new AbstractAction() {

                                    public void actionPerformed(ActionEvent e) {
                                        JMenu_Item jm_i = (JMenu_Item) e.getSource();
                                        jm_i.getProcessView().getWorkflowProcess().setReviewed(false);
                                        jm_i.getProcessView().uncomment();
                                    }
                                });
                                jmi.setText("Uncomment");

                            } else {
                                jmi = new JMenu_Item("Comment", procView);
                                jmi.setAction(new AbstractAction() {

                                    public void actionPerformed(ActionEvent e) {
                                        JMenu_Item jm_i = (JMenu_Item) e.getSource();
                                        jm_i.getProcessView().getWorkflowProcess().setReviewed(true);
                                        jm_i.getProcessView().comment();
                                    }
                                });
                                jmi.setText("Comment");

                            }
                            JMenu_Item remove = new JMenu_Item("Remove", procView);
                            remove.setToolTipText("Remove");
                            remove.addActionListener(new ActionListener() {

                                public void actionPerformed(ActionEvent e) {
                                    JMenu_Item jm_i = (JMenu_Item) e.getSource();
                                    int removeProcess = jm_i.getProcessView().getWorkflowView().getSelectedProcessIndex();
                                    jm_i.getProcessView().getWorkflowView().m_model.removeProcess(removeProcess);

                                    if (m_insertMarkPosition == removeProcess) {
                                        if (removeProcess == jm_i.getProcessView().getWorkflowView().getSizeProcessList()) {
                                            m_insertMarkPosition = -1;
                                        }
                                    }
                                    if (m_insertMarkPosition > removeProcess) {
                                        m_insertMarkPosition = m_insertMarkPosition - 1;
                                    }


                                }
                            });

                            JMenuItem process = new JMenuItem("----Process----");
                            process.setBackground(new Color(177, 195, 186));
                            process.setForeground(Color.BLACK);
                            process.setEnabled(false);



                            jpm.add(process);
                            jpm.addSeparator();
                            jpm.add(jmi);
                            jpm.addSeparator();
                            jpm.add(remove);
                            jpm.show(procView, evt.getX(), evt.getY());
                            //
                        } 

                    }
                    //-------------------------------//

                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        onProcessMouseEntered(evt);
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        onProcessMouseExited(evt);
                    }
                });
            }
        } else {
            m_procList.clear();
            removeAll();
        }
        repaint();

    }

    @Override
    public void paint(java.awt.Graphics g) {

        super.paint(g);


        if (m_procList.size() != 0) {
            repositionProcesses();

            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;

            g2.setStroke(new java.awt.BasicStroke(2));
            g.setColor(java.awt.Color.black);

            int px = BlocksStartX;
            int py = BlocksStartY - 20;
            int py2;

            int apx = 0;

            int arrw = 5;

            int cx = 0;
            int cy = 0;
            for (int i = 0; i < m_procList.size(); i++) {
                px = m_procList.get(i).getLocation().x
                        + m_procList.get(i).getWidth() / 2;
                py2 = m_procList.get(i).getLocation().y;

                if (i == m_insertMarkPosition) {
                    cx = px;
                    cy = (py2 - py) / 2;
                }

                if (py2 < py) {
                    g.drawLine(apx, py, apx, py + 20);
                    g.drawLine(apx, py + 20, (apx + px) / 2, py + 20);
                    g.drawLine((apx + px) / 2, py + 20, (apx + px) / 2, py2 - 20);
                    g.drawLine((apx + px) / 2, py2 - 20, px, py2 - 20);
                    g.drawLine(px, py2 - 20, px, py2);
                    py = BlocksStartY - 20;
                }

                //
                if (i > 0) {
                    g.drawLine(px, py, px, py2);
                    // Draw arrow
                    g.drawLine(px - arrw, py2 - arrw, px, py2);
                    g.drawLine(px + arrw, py2 - arrw, px, py2);
                }
                py = py2 + DefaultProcessView.BlockHeight;
                apx = px;

                if (m_insertMarkPosition == i) {

                    // g.fillOval(px-arrw-8, py2-arrw-10, 8, 8);
                    ImageIcon img = new javax.swing.ImageIcon(getClass().getResource("/botoseis/pics/seta01.png"));
                    g.drawImage(img.getImage(), px - arrw - 30, py2 - arrw - 10, null);

                }
            }
            if (m_procList.size() > 0) {
                py2 = py + 50;
                g.drawLine(px, py, px, py2);
                // Draw arrow
                g.drawLine(px - arrw, py2 - arrw, px, py2);
                g.drawLine(px + arrw, py2 - arrw, px, py2);

                // Draw insert mark
                int cw = 8;
                int ch = 8;
                //g.drawOval(cx, cy, cw, ch);
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        m_procList.clear();
        removeAll();

        WorkflowModel wm = (WorkflowModel) o;

        java.util.Enumeration en = wm.processes();
        while (en.hasMoreElements()) {
            WorkflowProcess w = (WorkflowProcess) en.nextElement();
            DefaultProcessView pv = new DefaultProcessView(w, m_prmPanel, this);
            add(pv);
            m_procList.add(pv);

            pv.addMouseListener(new java.awt.event.MouseAdapter() {
                //-------------------------------

                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    super.mouseClicked(evt);

                    DefaultProcessView procView = (DefaultProcessView) evt.getSource();

                    if (evt.getButton() == MouseEvent.BUTTON3) {
                        onProcessMouseClicked(evt);
                        JPopupMenu jpm = new JPopupMenu();
                        JMenu_Item jmi;
                        if (procView.getWorkflowProcess().isReviewed()) {
                            jmi = new JMenu_Item("Uncomment", procView);
                            jmi.setAction(new AbstractAction() {

                                public void actionPerformed(ActionEvent e) {
                                    JMenu_Item jm_i = (JMenu_Item) e.getSource();
                                    jm_i.getProcessView().getWorkflowProcess().setReviewed(false);
                                    jm_i.getProcessView().uncomment();
                                }
                            });
                            jmi.setText("Uncomment");

                        } else {
                            jmi = new JMenu_Item("Comment", procView);
                            jmi.setAction(new AbstractAction() {

                                public void actionPerformed(ActionEvent e) {
                                    JMenu_Item jm_i = (JMenu_Item) e.getSource();
                                    jm_i.getProcessView().getWorkflowProcess().setReviewed(true);
                                    jm_i.getProcessView().comment();

                                }
                            });
                            jmi.setText("Comment");

                        }
                        JMenu_Item remove = new JMenu_Item("Remove", procView);
                        remove.setToolTipText("Remove");
                        remove.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                JMenu_Item jm_i = (JMenu_Item) e.getSource();
                                int removeProcess = jm_i.getProcessView().getWorkflowView().getSelectedProcessIndex();
                                jm_i.getProcessView().getWorkflowView().m_model.removeProcess(removeProcess);
                                if (m_insertMarkPosition == removeProcess) {
                                    if (removeProcess == jm_i.getProcessView().getWorkflowView().getSizeProcessList()) {
                                        m_insertMarkPosition = -1;
                                    }
                                }
                                if (m_insertMarkPosition > removeProcess) {
                                    m_insertMarkPosition = m_insertMarkPosition - 1;
                                }
                            }
                        });

                        JMenuItem process = new JMenuItem("----Process----");
                        process.setBackground(new Color(177, 195, 186));
                        process.setForeground(Color.BLACK);
                        process.setEnabled(false);



                        jpm.add(process);
                        jpm.addSeparator();
                        jpm.add(jmi);
                        jpm.addSeparator();
                        jpm.add(remove);
                        jpm.show(procView, evt.getX(), evt.getY());
                        //
                    } else {
                        onProcessMouseClicked(evt);
                    }

                }
                //-------------------------------//

                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    onProcessMouseEntered(evt);
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    onProcessMouseExited(evt);
                }
            });
        }

        repaint();
    }

    public void onProcessMouseClicked(java.awt.event.MouseEvent evt) {
        DefaultProcessView s = (DefaultProcessView) evt.getSource();
        for (int i = 0; i < m_procList.size(); i++) {
            m_procList.get(i).setSelected(false);
        }
        s.setSelected(true);
        m_selectedProcess = s;
    }

    public void setSelectedProcess(int i) {
        m_procList.get(i).setSelected(true);
        m_selectedProcess = m_procList.get(i);
    }

    public void onProcessMouseEntered(java.awt.event.MouseEvent evt) {
    }

    public void onProcessMouseExited(java.awt.event.MouseEvent evt) {
    }

    private void repositionProcesses() {
        int w = getWidth();
        int h = getHeight();

        int padx = 20;
        int pady = 20;

        int dx = DefaultProcessView.BlockWidth + padx;
        int dy = DefaultProcessView.BlockHeight;

        int px = BlocksStartX;
        int py = BlocksStartY;

        for (int i = 0; i < m_procList.size(); i++) {
            m_procList.get(i).setLocation(px - DefaultProcessView.BlockWidth / 2,
                    py);
            py += DefaultProcessView.BlockHeight + pady;
            if ((py + DefaultProcessView.BlockHeight) > h) {
                py = BlocksStartY;
                px += dx;
            }
        }

    }

    public WorkflowModel getModel() {
        return m_model;
    }

    public int getSizeProcessList() {
        return m_procList.size();
    }
    // States
    private static final int ProcessAditionMode = 1;
    private static final int SelectionMode = 2;
    private int m_state;
    //
    private int m_insertMarkPosition;
    private WorkflowModel m_model;
    private java.util.Vector<botoseis.mainGui.workflows.DefaultProcessView> m_procList;
    private String m_title;
    private int BlocksStartX = DefaultProcessView.BlockWidth;
    private int BlocksStartY = 50;
    private static javax.swing.JPanel m_prmPanel;
    private DefaultProcessView m_selectedProcess;
}
//-------------------------------
class JMenu_Item extends JMenuItem {

    private DefaultProcessView processView;

    public JMenu_Item() {
    }

    public JMenu_Item(String arg0, DefaultProcessView dpv) {
        super(arg0);
        this.setText(arg0);
        this.processView = dpv;
    }

    /**
     * @return the proView
     */
    public DefaultProcessView getProcessView() {
        return processView;
    }

    /**
     * @param proView the proView to set
     */
    public void setProcessView(DefaultProcessView proView) {
        this.processView = proView;
    }
}
//-------------------------------//

