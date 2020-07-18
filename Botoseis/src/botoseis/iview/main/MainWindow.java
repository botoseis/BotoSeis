/*
 * MainWindow.java
 *
 * Created on November 25, 2008, 9:03 PM
 */
package botoseis.iview.main;

import botoseis.iview.dialogs.DialogGain;
import botoseis.iview.dialogs.DialogHeaderTrace;
import botoseis.iview.dialogs.DialogParametersImage;
import botoseis.mainGui.utils.Preferences;
import gfx.SVActor;
import gfx.AxisPanel;
import gfx.GfxPanelColorbar;
import gfx.SVColorScale;
import gfx.SVPoint2D;
import gfx.SVXYPlot;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import usrdata.SUHeader;
import usrdata.SUSection;
import usrdata.SUTrace;

/**
 *
 * @author  williams
 */
public class MainWindow extends javax.swing.JFrame {

    /** Creates new form MainWindow */
    public MainWindow() {
        initComponents();

        panelCDP.add(gfxPanelCDP);
        gfxPanelCDP.setVisibleScrollBar(true);

        m_timeAxis = new gfx.SVAxis(gfx.SVAxis.VERTICAL, gfx.SVAxis.AXIS_LEFT, "Time (s)");
        m_cdpOffsetAxis = new gfx.SVAxis(gfx.SVAxis.HORIZONTAL, gfx.SVAxis.AXIS_TOP, "Offset (km)");

        m_timeAxis.setLimits(0.0f, 1.0f);
        m_cdpOffsetAxis.setLimits(0.0f, 1.0f);

        mHeader = new SVXYPlot();
        mHeader.setLineStyle(gfx.SVXYPlot.SOLID);
        mHeader.setPointsVisible(true);
        mHeader.setDrawColor(java.awt.Color.red);
        mHeader.setDrawSize(1);
        mHeader.setVisible(true);
        gfxPanelCDP.addXYPlot(mHeader);
        
        preferences = Preferences.getPreferences();
        System.out.println("USING FORMAT: "+preferences.getFormat());


        m_currMapColor = 2;

        AxisPanel panelT = new AxisPanel(m_timeAxis);
        panelA.add(panelT);

        AxisPanel panelU = new AxisPanel(m_cdpOffsetAxis);
        panelB.add(panelU);

        gfxPanelCDP.setAxisPanelX(panelU);
        gfxPanelCDP.setAxisPanelY(panelT);

        gfxPanelCDP.addKeyListener(new java.awt.event.KeyAdapter() {

            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (m_csActor != null) {
                    switch (e.getKeyChar()) {
                        case 'r':
                            m_csActor.setColorMapType(m_csActor.RGB);
                            m_csActor.nextColormap();
                            m_currMapColor = m_csActor.getCurrColorMapIndex();
                            m_currMapType = SVColorScale.RGB;
                            break;
                        case 'h':
                            m_csActor.setColorMapType(m_csActor.HSV);
                            m_csActor.nextColormap();
                            m_currMapColor = m_csActor.getCurrColorMapIndex();
                            m_currMapType = SVColorScale.HSV;
                            break;
                        case 'R':
                            m_csActor.setColorMapType(m_csActor.RGB);
                            m_csActor.previousColormap();
                            m_currMapColor = m_csActor.getCurrColorMapIndex();
                            m_currMapType = SVColorScale.RGB;
                            break;
                        case 'H':
                            m_csActor.setColorMapType(m_csActor.HSV);
                            m_csActor.previousColormap();
                            m_currMapColor = m_csActor.getCurrColorMapIndex();
                            m_currMapType = SVColorScale.HSV;
                            break;
                        default:
                            m_gfxPanelColorbar = new GfxPanelColorbar(m_csActor, GfxPanelColorbar.HORIZONTAL);
                            colorbarPanel.removeAll();
                            colorbarPanel.add(m_gfxPanelColorbar);
                            break;
                    }
                    repaint();
                }
                switch (e.getKeyChar()) {
                    case 'n':
                        btnNextActionPerformed(null);
                        break;
                    case 'b':
                        btnPreviousActionPerformed(null);
                        break;
                }

            }
        });

        gfxPanelCDP.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent e) {
                if (btnHeader.isSelected()) {
                    //throw new UnsupportedOperationException("Not supported yet.");
                    SVPoint2D p = getGfxPanelCDP().getMouseLocation();
                    float XlengthHI = section.getF2();//=H(1).sx/scalco

                    float distanciax = section.getD2();
                    float delrt = section.getF1();// = primeiro tempo do matlab = 0

                    float dt = section.getD1();//=dt do matlab = 0.004

                    int XMatr = Math.round((p.fx - XlengthHI) / distanciax) + 1;
                    int YMatr = Math.round((p.fy - delrt) / dt) + 1;

                    int n_linhamaximo = section.getN1();
                    int n = YMatr - 1 + (XMatr - 1) * (n_linhamaximo);

                    int trace = n / section.getN1();

                    float fx[] = new float[section.getN1()];
                    float fy[] = new float[section.getN1()];

                    float lm[] = getGfxPanelCDP().getAxisLimits();

                    for (int i = 0; i < section.getN1(); i++) {

                        fx[i] = (int) p.fx;
                        fy[i] = 1 + i * section.getD2() - 100;
//                        System.out.println(fx[i]+"    "+fy[i]);
                    }
//                    System.out.println(lm[0]+"   "+lm[1]+"   "+lm[2]+"  "+lm[3]);

                    getmHeader().update(fx, fy);

                    dlgHeader.setVisible(false);
                    dlgHeader.updateHeaders(section.getTraces().get(trace).getHeader());
                    dlgHeader.setLocation(e.getXOnScreen(), e.getYOnScreen());
                    dlgHeader.setVisible(true);
                }


            }

            public void mousePressed(MouseEvent e) {
//                throw new UnsupportedOperationException("Not supported yet.");
                if (btnZoom.isSelected()) {
                    p1Zoom = gfxPanelCDP.getMouseLocation();
                }
            }

            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (btnZoom.isSelected()) {
                    p2Zoom = gfxPanelCDP.getMouseLocation();
                    onGraphicsPanelMouseReleased(e);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                getGfxPanelCDP().requestFocus();
            }

            public void mouseExited(MouseEvent e) {
//                throw new UnsupportedOperationException("Not supported yet.");
            }
        });


        gfxPanelCDP.addMouseMotionListener(new MouseMotionListener() {

            public void mouseDragged(MouseEvent e) {
            }

            public void mouseMoved(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
                SVActor act = null;

                if (wiggle) {
                    act = m_wgActor;
                } else {
                    if (image) {
                        act = m_csActor;
                    } else {
                        if (contour) {
                            act = m_cmActor;
                        }
                    }
                }
                SVPoint2D p = getGfxPanelCDP().getMouseLocation();
                float XlengthHI = section.getF2();//=H(1).sx/scalco

                float distanciax = section.getD2();
                float delrt = section.getF1();// = primeiro tempo do matlab = 0

                float dt = section.getD1();//=dt do matlab = 0.004

                int XMatr = Math.round((p.fx - XlengthHI) / distanciax) + 1;
                int YMatr = Math.round((p.fy - delrt) / dt) + 1;

                int n_linhamaximo = section.getN1();
                int n = YMatr - 1 + (XMatr - 1) * (n_linhamaximo);

                int trace = n / section.getN1();
                if (trace < section.getTraces().size()) {
                    SUHeader h = section.getTraces().get(trace).getHeader();
                    tfBar.setText(String.format("fldr: %d tracf: %d cdp: %d ep: %d offset: %d  time: %.2f  amp: %.7f ", h.fldr, h.tracf, h.cdp, h.ep, h.offset, p.fy, act.getData()[n]));
                }
//                System.out.println(p.ix+"  "+p.iy+"  "+p.fx+" "+p.fy);
            }
        });

        imageperc = 99;
        imagebalance = 100;
        wigbperc = 99;
//        System.out.println(panelPkey.getSize());
        panelPkey.setLayout(new BorderLayout());
        panelPkey.setSize(new Dimension(74, 40));
        panelPkey.setPreferredSize(new Dimension(74, 40));
//        panelPkey.setSize(new Dimension(74, 40));

        dlgHeader = new DialogHeaderTrace(this, false);
        dlgGain = new DialogGain(this, true);



    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelStatusbar = new javax.swing.JPanel();
        tfBar = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        panelB = new javax.swing.JPanel();
        panelCDP = new javax.swing.JPanel();
        panelA = new javax.swing.JPanel();
        colorbarPanel = new javax.swing.JPanel();
        panelPkey = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnZoom = new javax.swing.JToggleButton();
        btnPrevious = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnHeader = new javax.swing.JToggleButton();
        btnGain = new javax.swing.JButton();
        btnClip = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuExit = new javax.swing.JMenuItem();
        menuView = new javax.swing.JMenu();
        menuViewImage = new javax.swing.JMenuItem();
        menuViewWiggle = new javax.swing.JMenuItem();
        menuViewContour = new javax.swing.JMenuItem();
        menuViewImageWiggle = new javax.swing.JMenuItem();
        menuViewImageContour = new javax.swing.JMenuItem();
        menuAuto = new javax.swing.JMenu();
        menuPicking = new javax.swing.JMenu();
        menuHelp = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        panelStatusbar.setLayout(new java.awt.GridLayout(1, 0));
        panelStatusbar.add(tfBar);

        panelB.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelB.setLayout(new javax.swing.BoxLayout(panelB, javax.swing.BoxLayout.LINE_AXIS));

        panelCDP.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelCDP.setLayout(new javax.swing.BoxLayout(panelCDP, javax.swing.BoxLayout.LINE_AXIS));

        panelA.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelA.setLayout(new javax.swing.BoxLayout(panelA, javax.swing.BoxLayout.LINE_AXIS));

        colorbarPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        colorbarPanel.setLayout(new java.awt.GridLayout());

        panelPkey.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout panelPkeyLayout = new javax.swing.GroupLayout(panelPkey);
        panelPkey.setLayout(panelPkeyLayout);
        panelPkeyLayout.setHorizontalGroup(
            panelPkeyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 70, Short.MAX_VALUE)
        );
        panelPkeyLayout.setVerticalGroup(
            panelPkeyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 36, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(panelA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelPkey, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(colorbarPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 687, Short.MAX_VALUE)
                    .addComponent(panelCDP, javax.swing.GroupLayout.DEFAULT_SIZE, 687, Short.MAX_VALUE)
                    .addComponent(panelB, javax.swing.GroupLayout.DEFAULT_SIZE, 687, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelPkey, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelB, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE))
                .addGap(8, 8, 8)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelA, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
                    .addComponent(panelCDP, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(colorbarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jToolBar1.setRollover(true);

        btnZoom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/botoseis/pics/zoom3.png"))); // NOI18N
        btnZoom.setToolTipText("Zoom");
        btnZoom.setFocusable(false);
        btnZoom.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnZoom.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnZoom.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                btnZoomItemStateChanged(evt);
            }
        });
        jToolBar1.add(btnZoom);

        btnPrevious.setIcon(new javax.swing.ImageIcon(getClass().getResource("/botoseis/pics/Back24.gif"))); // NOI18N
        btnPrevious.setFocusable(false);
        btnPrevious.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrevious.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrevious);

        btnNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/botoseis/pics/Forward24.gif"))); // NOI18N
        btnNext.setFocusable(false);
        btnNext.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNext.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNext);
        jToolBar1.add(jSeparator1);

        btnHeader.setIcon(new javax.swing.ImageIcon(getClass().getResource("/botoseis/pics/header.png"))); // NOI18N
        btnHeader.setFocusable(false);
        btnHeader.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnHeader.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnHeader.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                btnHeaderItemStateChanged(evt);
            }
        });
        jToolBar1.add(btnHeader);

        btnGain.setText("Gain");
        btnGain.setFocusable(false);
        btnGain.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGain.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGainActionPerformed(evt);
            }
        });
        jToolBar1.add(btnGain);

        btnClip.setText("Clip");
        btnClip.setFocusable(false);
        btnClip.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClip.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClipActionPerformed(evt);
            }
        });
        jToolBar1.add(btnClip);

        jMenu1.setText("File");

        menuExit.setText("Exit");
        menuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuExitActionPerformed(evt);
            }
        });
        jMenu1.add(menuExit);

        jMenuBar1.add(jMenu1);

        menuView.setText("View");

        menuViewImage.setText("Image");
        menuViewImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuViewImageActionPerformed(evt);
            }
        });
        menuView.add(menuViewImage);

        menuViewWiggle.setText("Wiggle");
        menuViewWiggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuViewWiggleActionPerformed(evt);
            }
        });
        menuView.add(menuViewWiggle);

        menuViewContour.setText("Contour");
        menuViewContour.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuViewContourActionPerformed(evt);
            }
        });
        menuView.add(menuViewContour);

        menuViewImageWiggle.setText("Image & Wiggle");
        menuViewImageWiggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuViewImageWiggleActionPerformed(evt);
            }
        });
        menuView.add(menuViewImageWiggle);

        menuViewImageContour.setText("Image & contour");
        menuViewImageContour.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuViewImageContourActionPerformed(evt);
            }
        });
        menuView.add(menuViewImageContour);

        jMenuBar1.add(menuView);

        menuAuto.setMnemonic('P');
        menuAuto.setText("Auto"); // NOI18N
        jMenuBar1.add(menuAuto);

        menuPicking.setText("Picking");
        jMenuBar1.add(menuPicking);

        menuHelp.setText("Help");
        jMenuBar1.add(menuHelp);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(panelStatusbar, javax.swing.GroupLayout.DEFAULT_SIZE, 757, Short.MAX_VALUE))
                    .addComponent(jToolBar1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 572, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelStatusbar, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
    if (!stackData) {
        setModeView("wiggle");
    } else {
        setModeView("image");
    }
    showView();
    repaint();
}//GEN-LAST:event_formWindowOpened

private void menuViewImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuViewImageActionPerformed
    getGfxPanelCDP().removeAllActors();
    setModeView("image");
    repaint();
}//GEN-LAST:event_menuViewImageActionPerformed

private void menuViewWiggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuViewWiggleActionPerformed
        setModeView("wiggle");
        repaint();
}//GEN-LAST:event_menuViewWiggleActionPerformed

private void menuViewContourActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuViewContourActionPerformed
        setModeView("contour");
        repaint();
}//GEN-LAST:event_menuViewContourActionPerformed

private void menuViewImageWiggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuViewImageWiggleActionPerformed
        setModeView("wiggle,image");
        repaint();
}//GEN-LAST:event_menuViewImageWiggleActionPerformed

private void menuViewImageContourActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuViewImageContourActionPerformed
        setModeView("contour,image");
        repaint();
}//GEN-LAST:event_menuViewImageContourActionPerformed

private void btnZoomItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_btnZoomItemStateChanged
        if (evt.getStateChange() == evt.SELECTED) {
            getGfxPanelCDP().activateZoom(true);
        } else {
            getGfxPanelCDP().activateZoom(false);
        }
}//GEN-LAST:event_btnZoomItemStateChanged

private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed


        if (mapSection.lastIndexOf(section.getTraces()) < 0 || (mapSection.lastIndexOf(section.getTraces()) + 1) == mapSection.size()) {
            if (!section.isEof()) {
                section.readFromInputStream(System.in);
                mapSection.add((Vector<SUTrace>) section.getTraces().clone());
                if (mapSection.size() > saveSection) {
                    mapSection.remove(0);
                }
            }
        } else {
            section.setTraces(mapSection.get(mapSection.lastIndexOf(section.getTraces()) + 1));
        }

        showView();

}//GEN-LAST:event_btnNextActionPerformed

private void btnPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousActionPerformed
        // TODO add your handling code here:

        if (mapSection.indexOf(section.getTraces()) > 0) {
            section.setTraces(mapSection.get(mapSection.indexOf(section.getTraces()) - 1));
        }
        showView();

}//GEN-LAST:event_btnPreviousActionPerformed

private void btnHeaderItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_btnHeaderItemStateChanged
        if (evt.getStateChange() == ItemEvent.DESELECTED) {
            float fx[] = new float[0];
            float fy[] = new float[0];
            getmHeader().update(fx, fy);
            getGfxPanelCDP().repaint();
            dlgHeader.dispose();
        }
}//GEN-LAST:event_btnHeaderItemStateChanged

private void btnGainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGainActionPerformed
        // TODO add your handling code here:

        dlgGain.setVisible(true);
        if (dlgGain.isApply()) {
            showView();
        }
}//GEN-LAST:event_btnGainActionPerformed

private void menuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuExitActionPerformed
        System.exit(0);
}//GEN-LAST:event_menuExitActionPerformed

private void btnClipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClipActionPerformed
        // TODO add your handling code here:
        DialogParametersImage dlgparamimag = new DialogParametersImage(this, true, m_csActor, m_wgActor, panelCDP);

        if (m_csActor != null) {
            imageperc = m_csActor.getImagPerc();
            imagebalance = (int) m_csActor.getImagimagebalance();
        }
        dlgparamimag.setimage_perc(imageperc);
        dlgparamimag.setimage_balance(imagebalance);
        if (m_wgActor != null) {
            wigbperc = m_wgActor.getwigbperc();
        }
        dlgparamimag.setwigb_perc(wigbperc);
        dlgparamimag.setVisible(true);
        //   dlgparamimag.setVisible(true);
        int verifyimage = 0;
        verifyimage = dlgparamimag.getParameterVerifyimage();

        if (verifyimage == 1) {
            imageperc = dlgparamimag.getimage_perc();
            imagebalance = (int) dlgparamimag.getimage_balance();
            wigbperc = dlgparamimag.getwigb_perc();
            if (m_wgActor != null) {
                m_wgActor.setPercParameters(wigbperc);//, wigbclip);
            }
            if (m_csActor != null) {

                if ((imageperc < 0) || (imageperc > 100)) {
                    imageperc = 100.0f;
                }
                m_csActor.setImagePerc(imageperc);
                if ((imagebalance != 1) && (imagebalance != 0)) {
                    imagebalance = 0;
                }
            }
            panelCDP.repaint();
        }
}//GEN-LAST:event_btnClipActionPerformed

    private void onGraphicsPanelMouseReleased(MouseEvent e) {
        float lm[] = getGfxPanelCDP().getAxisLimits();
//        System.out.println(lm[0] + " " + lm[1] + " " + lm[2] + " " + lm[3]);
//        System.out.println(section.getF2()+"  "+section.getN2()+"  "+section.getD2());

        SVActor act = null;

        if (wiggle) {
            act = m_wgActor;
        } else {
            if (image) {
                act = m_csActor;
            } else {
                if (contour) {
                    act = m_cmActor;
                }
            }
        }
        System.out.println(skey);
        System.out.println(section.getTraces().size());
        int trc1 = act.getTraceAt(p1Zoom.fx, p1Zoom.fy);
        int trc2 = act.getTraceAt(p2Zoom.fx, p2Zoom.fy);
        System.out.println(trc1 + "   " + trc2);
        if (trc1 == trc2) {
            trc1 = 0;
            trc2 = section.getTraces().size() - 1;
        }

        System.out.println(getSkeyValueAt(trc1) + "  " + getSkeyValueAt(trc2));
        m_cdpOffsetAxis.setLimits(getSkeyValueAt(trc1), getSkeyValueAt(trc2));

        m_timeAxis.setLimits(lm[0], lm[1]);


        panelA.repaint();
        panelB.repaint();
    }

    public void setModeView(String modeview) {
        wiggle = image = contour = false;
        String split[] = modeview.split(",");
        for (int i = 0; i < split.length; i++) {
            String mode = split[i];
            if (mode.equals("wiggle")) {
                wiggle = true;
            } else {
                if (mode.equals("image")) {
                    image = true;
                } else {
                    contour = true;
                }
            }
        }
        showView();
    }

    private void showView() {
        getGfxPanelCDP().removeAllActors();
        if (image) {
            showImage();
        }
        if (wiggle) {
            showWiggle(image);
        }
        if (contour) {
            showContour();
        }
        getGfxPanelCDP().repaint();

    }

    private void showImage() {
        int n1 = section.getN1();
        int n2 = section.getN2();
        float f1 = section.getF1();
        float f2 = section.getF2();
        float d1 = section.getD1();
        float d2 = section.getD2();



        getGfxPanelCDP().setAxesLimits(f1, f1 + n1 * d1, f2, f2 + n2 * d2);
        m_timeAxis.setLimits(f1, f1 + n1 * d1);
//        m_cdpOffsetAxis.setLimits(f2, f2 + n2 * d2);

        setAxis();

        m_csActor = new gfx.SVColorScale(3, gfx.SVColorScale.NORMAL);
        m_csActor.setData(section.getData(), n1, f1, d1, n2, f2, d2);
        m_csActor.setImagePerc(imageperc);
        m_csActor.setbalance(imagebalance);
        m_csActor.setColormap(m_currMapType,m_currMapColor);
        m_gfxPanelColorbar = new GfxPanelColorbar(m_csActor, GfxPanelColorbar.HORIZONTAL);
        colorbarPanel.removeAll();
        colorbarPanel.add(m_gfxPanelColorbar);

        getGfxPanelCDP().addActor(m_csActor);
        applyGain(m_csActor);

    }

    public void setAxis() {
        if (!stackData) {
            float min = 0, max = 0;
            int key = 0;
            if (pkey == null || pkey.equals("")) {
                pkey = "ep";
            }
            if (skey == null || skey.equals("")) {
                skey = "tracf";
            }
            key = section.getTraces().get(0).getHeader().getValue(pkey);

            min = section.getTraces().get(0).getHeader().getValue(skey);
            max = section.getTraces().get(section.getTraces().size() - 1).getHeader().getValue(skey);

            m_cdpOffsetAxis.setLimits(min, max);
            m_cdpOffsetAxis.setLimitsInitial(min, max);
            m_cdpOffsetAxis.setTitle(skey.toUpperCase());
            panelPkey.removeAll();

            JLabel lpkey = new JLabel(pkey.toUpperCase());
            lpkey.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);


            JLabel lvalue = new JLabel(String.valueOf(key));
            lvalue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

            panelPkey.add(lpkey, BorderLayout.NORTH);
            panelPkey.add(lvalue, BorderLayout.SOUTH);
            panelPkey.updateUI();
//            m_cdpOffsetAxis.setTitle(pkey.toUpperCase() + " (" + key + ")");
        } else {

            int min = 0, max = 0;
//            if (pkey != null && pkey.equals("offset")) {
//                min = section.getTraces().get(0).getHeader().offset;
//                max = section.getTraces().get(section.getTraces().size() - 1).getHeader().offset;
//                skey = "offset";
//                if (min == max) {
//                    JOptionPane.showMessageDialog(null, "Offset invalid! \nAssuming pkey: cdp");
//                    min = section.getTraces().get(0).getHeader().cdp;
//                    max = section.getTraces().get(section.getTraces().size() - 1).getHeader().cdp;
//                    pkey = "cdp";
//                    skey = "cdp";
//                }
//            } else {
//                pkey = "cdp";
//                skey = "cdp";
//                m_cdpOffsetAxis.setTitle(skey.toUpperCase());
//                min = section.getTraces().get(0).getHeader().cdp;
//                max = section.getTraces().get(section.getTraces().size() - 1).getHeader().cdp;
//            }
            if (pkey == null || pkey.trim().equals("")) {
                skey = pkey = "cdp";
            }
            min = section.getTraces().get(0).getHeader().getValue(skey);
            max = section.getTraces().get(section.getTraces().size() - 1).getHeader().getValue(skey);
            if (min == max) {
                JOptionPane.showMessageDialog(null, pkey + " invalid! \nAssuming pkey: cdp");
                pkey = skey = "cdp";
            }
            min = section.getTraces().get(0).getHeader().getValue(skey);
            max = section.getTraces().get(section.getTraces().size() - 1).getHeader().getValue(skey);
            m_cdpOffsetAxis.setLimits(min, max);
            m_cdpOffsetAxis.setLimitsInitial(min, max);
        }
        panelB.repaint();
    }

    private void showWiggle(boolean value) {

        int n1 = section.getN1();
        int n2 = section.getN2();
        float f1 = section.getF1();
        float f2 = section.getF2();
        float d1 = section.getD1();
        float d2 = section.getD2();
        getGfxPanelCDP().setAxesLimits(f1, f1 + n1 * d1, f2, f2 + n2 * d2);
        m_timeAxis.setLimits(f1, f1 + n1 * d1);
        //m_cdpOffsetAxis.setLimits(f2, f2 + n2 * d2);
//        System.out.println("  " + n1 + "  " + n2 + "  " + f1 + "  " + f2 + "  " + d1 + "  " + d2 + "  ");
        setAxis();
        m_wgActor = new gfx.SVWiggle();
        m_wgActor.setData(section.getData(), n1, f1, d1, n2, f2, d2);
        m_wgActor.setPercParameters(wigbperc);
        getGfxPanelCDP().addActor(m_wgActor);
        if (value) {
            m_wgActor.applyGain(false, 0, 0, 1, false, false, 0, 0, 0, 1, false, false, false, true, 1, 1, 0, false);
        }
        colorbarPanel.removeAll();
        applyGain(m_wgActor);
    }

    private void showContour() {

        int n1 = section.getN1();
        int n2 = section.getN2();
        float f1 = section.getF1();
        float f2 = section.getF2();
        float d1 = section.getD1();
        float d2 = section.getD2();

        setAxis();

        getGfxPanelCDP().setAxesLimits(f1, f1 + n1 * d1, f2, f2 + n2 * d2);
        m_timeAxis.setLimits(f1, f1 + n1 * d1);
//        m_cdpOffsetAxis.setLimits(f2, f2 + n2 * d2);

        m_cmActor = new gfx.SVContourmap();
        m_cmActor.setData(section.getData(), n1, f1, d1, n2, f2, d2);

        getGfxPanelCDP().addActor(m_cmActor);
        applyGain(m_cmActor);

    }

    private void applyGain(SVActor actor) {
        if (dlgGain.isApply()) {
            Boolean panel = dlgGain.getBooleanValue("panel");
            Float tpow = dlgGain.getFloatValue("tpow");
            Float epow = dlgGain.getFloatValue("epow");
            Float gpow = dlgGain.getFloatValue("gpow");
            Boolean agc = dlgGain.getBooleanValue("agc");
            Boolean gagc = dlgGain.getBooleanValue("gagc");
            Float wagc = dlgGain.getFloatValue("wagc");
            Float trap = dlgGain.getFloatValue("trap");
            Float clip = dlgGain.getFloatValue("clip");
            Float qclip = dlgGain.getFloatValue("gclip");
            Boolean qbal = dlgGain.getBooleanValue("qbal");
            Boolean pbal = dlgGain.getBooleanValue("pbal");
            Boolean mbal = dlgGain.getBooleanValue("mbal");
            Boolean maxbal = dlgGain.getBooleanValue("maxbal");
            Float scale = dlgGain.getFloatValue("scale");
            Float norm = dlgGain.getFloatValue("norm");
            Float bias = dlgGain.getFloatValue("bias");
            Boolean jon = dlgGain.getBooleanValue("jon");

            actor.applyGain(panel, tpow, epow, gpow, agc,
                    gagc, wagc, trap, clip, qclip,
                    qbal, pbal, mbal, maxbal, scale,
                    norm, bias, jon);
        }
    }

    private void initData() {
        section = new usrdata.SUSection();
        updatePreferences();
        section.setPreStakcData(!stackData);
        if (pkey != null) {
            section.setPkey(pkey);
        }
        section.readFromInputStream(System.in);
        mapSection.add((Vector<SUTrace>) section.getTraces().clone());


    }
    
    public void updatePreferences(){
        section.setFormat(preferences.getFormat());
    }

    private void parseCommandLine(String args[]) {
        for (int i = 0; i < args.length; i++) {
            String[] key = args[i].split("=");
            if ("key".equalsIgnoreCase(key[0])) {
                wndKey = key[1];
            } else if ("min".equalsIgnoreCase(key[0])) {
                wndMin = key[1];
            } else if ("max".equalsIgnoreCase(key[0])) {
                wndMax = key[1];
            } else if ("abs".equalsIgnoreCase(key[0])) {
                wndAbs = key[1];
            } else if ("j".equalsIgnoreCase(key[0])) {
                wndJ = key[1];
            } else if ("s".equalsIgnoreCase(key[0])) {
                wndS = key[1];
            } else if ("count".equalsIgnoreCase(key[0])) {
                wndCount = key[1];
            } else if ("reject".equalsIgnoreCase(key[0])) {
                wndReject = key[1];
            } else if ("accept".equalsIgnoreCase(key[0])) {
                wndAccept = key[1];
            } else if ("dt".equalsIgnoreCase(key[0])) {
                wndDt = key[1];
            } else if ("tmin".equalsIgnoreCase(key[0])) {
                wndTMin = key[1];
            } else if ("tmax".equalsIgnoreCase(key[0])) {
                wndTMax = key[1];
            } else if ("itmin".equalsIgnoreCase(key[0])) {
                wndItMin = key[1];
            } else if ("itmax".equalsIgnoreCase(key[0])) {
                wndItMax = key[1];
            } else if ("itmin".equalsIgnoreCase(key[0])) {
                wndNt = key[1];
            } else if ("sortkeys".equalsIgnoreCase(key[0])) {
                sortKeys = key[1];
            } else if ("stack".equalsIgnoreCase(key[0])) {
                stack = key[1];
            } else if ("pkey".equalsIgnoreCase(key[0])) {
                pkey = key[1];
            } else if ("skey".equalsIgnoreCase(key[0])) {
                skey = key[1];
            } else if ("saveSec".equalsIgnoreCase(key[0])) {
                saveSection = new Integer(key[1]);
            }
        }
        saveSection = (saveSection == null) ? 11 : saveSection + 1;

        keyMapSection = 0;

        if (stack == null || stack.equals("no")) {
            stackData = false;
        } else {
            stackData = true;
        }
    }

    public int getSkeyValueAt(int iTrace) {
        if (iTrace > section.getTraces().size() - 1) {
            iTrace = section.getTraces().size() - 1;
        }
        SUTrace trace = section.getTraces().get(iTrace);

        if (skey.equals("tracf")) {
            return trace.getHeader().tracf;
        } else {
            if (skey.equals("offset")) {
                return trace.getHeader().offset;
            } else {
                if (skey.equals("cdp")) {
                    return trace.getHeader().cdp;
                } else {
                    if (skey.equals("fldr")) {
                        return trace.getHeader().fldr;
                    }
                }
            }
        }

        return 0;
    }

    public void setCommandLine(String args[]) {
        parseCommandLine(args);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(final String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                MainWindow wnd = new MainWindow();
                wnd.setCommandLine(args);
                wnd.initData();
                wnd.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClip;
    private javax.swing.JButton btnGain;
    private javax.swing.JToggleButton btnHeader;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrevious;
    private javax.swing.JToggleButton btnZoom;
    private javax.swing.JPanel colorbarPanel;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JMenu menuAuto;
    private javax.swing.JMenuItem menuExit;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenu menuPicking;
    private javax.swing.JMenu menuView;
    private javax.swing.JMenuItem menuViewContour;
    private javax.swing.JMenuItem menuViewImage;
    private javax.swing.JMenuItem menuViewImageContour;
    private javax.swing.JMenuItem menuViewImageWiggle;
    private javax.swing.JMenuItem menuViewWiggle;
    private javax.swing.JPanel panelA;
    private javax.swing.JPanel panelB;
    private javax.swing.JPanel panelCDP;
    private javax.swing.JPanel panelPkey;
    private javax.swing.JPanel panelStatusbar;
    private javax.swing.JTextField tfBar;
    // End of variables declaration//GEN-END:variables
    private gfx.SVGraphicsPanel gfxPanelCDP = new gfx.SVGraphicsPanel();
    GfxPanelColorbar m_gfxPanelColorbar;
    gfx.SVAxis m_timeAxis;
    gfx.SVAxis m_cdpOffsetAxis;
    private gfx.SVXYPlot mHeader;
    gfx.SVColorScale m_csActor;
    gfx.SVWiggle m_wgActor;
    gfx.SVContourmap m_cmActor;
    SVPoint2D p1Zoom = new SVPoint2D();
    SVPoint2D p2Zoom = new SVPoint2D();
    // Windowing options
    String wndKey = "";
    String wndMin = "";
    String wndMax = "";
    String wndAbs = "";
    String wndJ = "";
    String wndS = "";
    String wndCount = "";
    String wndReject = "";
    String wndAccept = "";
    // Vertical windowing
    String wndDt = "";
    String wndTMin = "";
    String wndTMax = "";
    String wndItMin = "";
    String wndItMax = "";
    String wndNt = "";
    // Sort options
    String sortKeys = "";
    // Data options
    String stack = "";
    String pkey = "";
    String skey = "";
    Integer saveSection;
    boolean inputDefault, stackData;
    boolean image, wiggle, contour;
    private SUSection section;
    int imagebalance;
    int keyMapSection;
    float imageperc;
    float wigbperc;
    ArrayList<Vector<SUTrace>> mapSection = new ArrayList<Vector<SUTrace>>();
    DialogHeaderTrace dlgHeader;
    DialogGain dlgGain;
    int m_currMapColor;
    int m_currMapType;
    Preferences preferences;

    /**
     * @return the mHeader
     */
    public gfx.SVXYPlot getmHeader() {
        return mHeader;
    }

    /**
     * @param mHeader the mHeader to set
     */
    public void setmHeader(gfx.SVXYPlot mHeader) {
        this.mHeader = mHeader;
    }

    /**
     * @return the gfxPanelCDP
     */
    public gfx.SVGraphicsPanel getGfxPanelCDP() {
        return gfxPanelCDP;
    }

    /**
     * @param gfxPanelCDP the gfxPanelCDP to set
     */
    public void setGfxPanelCDP(gfx.SVGraphicsPanel gfxPanelCDP) {
        this.gfxPanelCDP = gfxPanelCDP;
    }
}
