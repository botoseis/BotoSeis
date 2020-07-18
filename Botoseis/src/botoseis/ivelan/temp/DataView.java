/*
 * MainWindow.java
 *
 * Created on November 25, 2008, 9:03 PM
 */
package botoseis.ivelan.temp;

import botoseis.iview.dialogs.DialogGain;
import botoseis.iview.dialogs.DialogHeaderTrace;
import gfx.SVActor;
import gfx.AxisPanel;
import gfx.GfxPanelColorbar;
import gfx.SVColorScale;
import gfx.SVPoint2D;
import gfx.SVXYPlot;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import usrdata.SUTrace;

/**
 *
 * @author williams
 */
public class DataView extends javax.swing.JFrame {

    /**
     * Creates new form MainWindow
     */
    public DataView(MainWindow main) {
        initComponents();
        mw = main;
        panelCDP.add(gfxPanelCDP);
        gfxPanelCDP.setVisibleScrollBar(true);

        m_timeAxis = new gfx.SVAxis(gfx.SVAxis.VERTICAL, gfx.SVAxis.AXIS_LEFT, "Time (s)");
        m_cdpOffsetAxis = new gfx.SVAxis(gfx.SVAxis.HORIZONTAL, gfx.SVAxis.AXIS_TOP, "CDP");

        m_timeAxis.setLimits(0.0f, 1.0f);
        m_cdpOffsetAxis.setLimits(0.0f, 1.0f);

        mHeader = new SVXYPlot();
        mHeader.setLineStyle(gfx.SVXYPlot.SOLID);
        mHeader.setPointsVisible(true);
        mHeader.setDrawColor(java.awt.Color.red);
        mHeader.setDrawSize(1);
        mHeader.setVisible(true);
        gfxPanelCDP.addXYPlot(mHeader);
        m_currMapColor = 2;



        lastVelocity.setLineStyle(gfx.SVXYPlot.SOLID);
        lastVelocity.setPointsVisible(false);
        lastVelocity.setDrawColor(java.awt.Color.green);
        lastVelocity.setVisible(false);
        lastVelocity.setDrawSize(2);


        velocity.setLineStyle(gfx.SVXYPlot.SOLID);
        velocity.setPointsVisible(false);
        velocity.setDrawColor(java.awt.Color.black);
        velocity.setVisible(true);
        velocity.setDrawSize(2);

        gfxPanelCDP.addXYPlot(lastVelocity);
        gfxPanelCDP.addXYPlot(velocity);


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
                            m_csActor.setColorMapType(SVColorScale.RGB);
                            m_csActor.nextColormap();
                            m_currMapColor = m_csActor.getCurrColorMapIndex();
                            m_currMapType = SVColorScale.RGB;
                            break;
                        case 'h':
                            m_csActor.setColorMapType(SVColorScale.HSV);
                            m_csActor.nextColormap();
                            m_currMapColor = m_csActor.getCurrColorMapIndex();
                            m_currMapType = SVColorScale.HSV;
                            break;
                        case 'R':
                            m_csActor.setColorMapType(SVColorScale.RGB);
                            m_csActor.previousColormap();
                            m_currMapColor = m_csActor.getCurrColorMapIndex();
                            m_currMapType = SVColorScale.RGB;
                            break;
                        case 'H':
                            m_csActor.setColorMapType(SVColorScale.HSV);
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
                    m_gfxPanelColorbar.repaint();
                }


            }
        });

        gfxPanelCDP.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent e) {
                SVPoint2D sv = gfxPanelCDP.getMouseLocation();
                int trc = m_csActor.getTraceAt(sv.fx, sv.fy);


                int cdp = (int) ((mw.m_cdpMin) + (trc * d2));
                System.out.println(trc + " " + cdp + " " + d2);
                mw.workOnCDP(cdp);
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(java.awt.event.MouseEvent e) {
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

                act = m_csActor;


                SVPoint2D p = getGfxPanelCDP().getMouseLocation();
                float XlengthHI = f2;//=H(1).sx/scalco

                float distanciax = d2;
                float delrt = f1;// = primeiro tempo do matlab = 0

                float dt = d1;//=dt do matlab = 0.004

                int XMatr = Math.round((p.fx - XlengthHI) / distanciax) + 1;
                int YMatr = Math.round((p.fy - delrt) / dt) + 1;

                int n_linhamaximo = n1;
                int n = YMatr - 1 + (XMatr - 1) * (n_linhamaximo);

                int trace = n / n1;

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
        m_currMapType = SVColorScale.HSV;

        dlgHeader = new DialogHeaderTrace(this, false);
        dlgGain = new DialogGain(this, true);



    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        panelB = new javax.swing.JPanel();
        panelCDP = new javax.swing.JPanel();
        panelA = new javax.swing.JPanel();
        colorbarPanel = new javax.swing.JPanel();
        panelPkey = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuSave = new javax.swing.JMenuItem();
        menuExit = new javax.swing.JMenuItem();
        menuView = new javax.swing.JMenu();
        menuSmoothVelocity = new javax.swing.JMenuItem();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridLayout(1, 0));

        panelB.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelB.setLayout(new javax.swing.BoxLayout(panelB, javax.swing.BoxLayout.LINE_AXIS));

        panelCDP.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelCDP.setLayout(new javax.swing.BoxLayout(panelCDP, javax.swing.BoxLayout.LINE_AXIS));

        panelA.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelA.setLayout(new javax.swing.BoxLayout(panelA, javax.swing.BoxLayout.LINE_AXIS));

        colorbarPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        colorbarPanel.setLayout(new javax.swing.BoxLayout(colorbarPanel, javax.swing.BoxLayout.LINE_AXIS));

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
                    .addComponent(colorbarPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 699, Short.MAX_VALUE)
                    .addComponent(panelCDP, javax.swing.GroupLayout.DEFAULT_SIZE, 699, Short.MAX_VALUE)
                    .addComponent(panelB, javax.swing.GroupLayout.DEFAULT_SIZE, 699, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelPkey, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelB, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE))
                .addGap(8, 8, 8)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelA, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
                    .addComponent(panelCDP, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(colorbarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        getContentPane().add(jPanel3);

        jMenu1.setText("File");

        menuSave.setText("Save");
        menuSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSaveActionPerformed(evt);
            }
        });
        jMenu1.add(menuSave);

        menuExit.setText("Exit");
        menuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuExitActionPerformed(evt);
            }
        });
        jMenu1.add(menuExit);

        jMenuBar1.add(jMenu1);

        menuView.setText("Edit");

        menuSmoothVelocity.setText("Smooth velocity field");
        menuSmoothVelocity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSmoothVelocityActionPerformed(evt);
            }
        });
        menuView.add(menuSmoothVelocity);

        jMenuBar1.add(menuView);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
//    if (!stackData) {
//        setModeView("wiggle");
//    } else {
//        setModeView("image");
//    }
//    showView();
//    repaint();
}//GEN-LAST:event_formWindowOpened

private void menuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuExitActionPerformed
    setVisible(false);
}//GEN-LAST:event_menuExitActionPerformed

private void menuSmoothVelocityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSmoothVelocityActionPerformed
    SmoothDialog dlg = new SmoothDialog(null, true);

    dlg.setVisible(true);

    if (dlg.isValid()) {
        int wcdp = dlg.getCdpWindow();
        wndA = 1 + (int) Math.floor(wcdp / d2);

        float wt = dlg.getTimeWindow();
        wndB = 1 + (int) Math.floor(wt / d1);

        int d = dlg.getCdpsInterval();
//        dcdp = (int) (Math.floor(d / d2) * d2);
        dcdp = d;
        dt = dlg.getTimeInterval();
        dtime = (float) (Math.floor(dt / d1) * d1);

        int ncdps = 0;
//        float cdpMax = f2 + (n2 - 1) * d2;
        float cdpMax = mw.m_cdpMax;
        float val;
        do {
            val = f2 + ncdps * dcdp;
            ncdps++;
        } while (val <= cdpMax);

        ncdps--;

        ntime = 0;
        float tmax = f1 + (n1 - 1) * d1;
        do {
            val = f1 + ntime * dtime;
            ntime++;
        } while (val <= tmax);
        ntime--;

        float d1out = dtime;
        float d2out = dcdp;


        //resampleData(m_data, temp, m_n1, m_f1, m_d1, m_n2, m_f2, m_d2,
        //		 ntime, m_f1, d1out, ncdps, m_f2, d2out);

        float[][] data = new float[ncdps][ntime];
        float[] tdata = new float[ncdps * ntime];
        data = getDataInterp();

        int nskipCdp = (int) (dcdp / d2 - 1);
        int nskipTime = (int) (dtime / d1 - 1);
        smoothData(m_data, data, n2, n1, ncdps, ntime, nskipCdp, nskipTime, wndA, wndB);
        for (int i = 0; i < ncdps; i++) {
            for (int j = 0; j < ntime; j++) {
                tdata[i * ntime + j] = data[i][j];
//                System.out.print(tdata[i * ntime + j]+" ");
            }
//            System.out.println("");
        }

//        System.out.println(ntime + " " + f1 + " " + d1 + " " + n2 
//                + " " + f2 + " " + d2 + " " + n1 + " " + f1 + " " 
//                + dtime + " " + ncdps + " " + f2 + " " + dcdp+" "+d1out+" "+d2out);
        float[][] temp = new float[ncdps][m_data[0].length];
        resampleData(data, temp, ntime, f1, d1out, n2, f2, d2,
                n1, f1, d1, ncdps, f2, d2out);

//                m_currData = data;

        m_data = temp;
        tdata = new float[ncdps * n1];
        for (int i = 0; i < ncdps; i++) {
            for (int j = 0; j < n1; j++) {
                tdata[i * n1 + j] = m_data[i][j];
            }
        }

//        m_currF1 = f1;
//        n1 = m_currN1 = ntime;
//        d1 = m_currD1 = d1out;
//
//        m_currF2 = f2;
//        n2 = m_currN2 = ncdps;
//        d2 = m_currD2 = d2out;
        getGfxPanelCDP().setAxesLimits(f1, f1 + n1 * d1, f2, f2 + (n2 - 1) * d2);
        m_timeAxis.setLimits(f1, f1 + n1 * d1);
//        m_cdpOffsetAxis.setLimits(f2, f2 + n2 * d2);


        m_cdpOffsetAxis.setLimits(f2, d2 * n2);
        m_cdpOffsetAxis.setLimitsInitial(f2, d2 * f2);

        panelB.repaint();

        m_csActor = new gfx.SVColorScale(3, gfx.SVColorScale.NORMAL);
        m_csActor.setData(tdata, n1, f1, d1, n2, f2, d2);
        m_csActor.setImagePerc(imageperc);
        m_csActor.setColormap(m_currMapType, m_currMapColor);
        getGfxPanelCDP().addActor(m_csActor);
        m_gfxPanelColorbar = new GfxPanelColorbar(m_csActor, GfxPanelColorbar.HORIZONTAL);
        colorbarPanel.removeAll();
        colorbarPanel.add(m_gfxPanelColorbar);


        repaint();
    }
}//GEN-LAST:event_menuSmoothVelocityActionPerformed

private void menuSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSaveActionPerformed
    try {
        JFileChooser dlg = new JFileChooser("Save");
        int status = dlg.showSaveDialog(null);

        if (status == JFileChooser.APPROVE_OPTION) {
            InputCdps ic = new InputCdps(this);
            ic.setVisible(true);
            String as = ic.jt1.getText().trim().equals("") ? mw.m_cdpMin + "" : ic.jt1.getText();
            int icdp = new Integer(as);

            as = ic.jt2.getText().trim().equals("") ? (mw.m_cdpInterval * m_currN2) + "" : ic.jt2.getText().trim();
            int ecdp = new Integer(as);

            int ncdps = (ecdp - icdp) + 1;

            int m_ns = mw.m_ns;
            float m_dt = mw.m_dt;
            float[][] data2 = new float[ncdps][m_ns];

            float[][] data = new float[mw.m_velocityPicks.size()][m_ns];
            int i2 = 0;
            for (Integer key : mw.m_velocityPicks.keySet()) {
                Vector<gfx.SVPoint2D> picks = mw.m_velocityPicks.get(key);
                Collections.sort(picks, new Comparator<gfx.SVPoint2D>() {

                    @Override
                    public int compare(SVPoint2D arg0, SVPoint2D arg1) {
                        Float o1 = new Float(arg0.fy);
                        Float o2 = new Float(arg1.fy);
                        return o1.compareTo(o2);
                    }
                });
                if (picks.size() > 0) {
                    int nv = picks.size();
                    nv += 2;
                    float[] ta = new float[nv];
                    float[] va = new float[nv];

                    ta[0] = 0.0f;
                    va[0] = picks.get(0).fx;
                    ta[nv - 1] = m_ns * m_dt;
                    va[nv - 1] = picks.get(picks.size() - 1).fx;

                    int k = 1;
                    for (int iv = 0; iv < picks.size(); iv++) {
                        ta[k] = picks.get(iv).fy;
                        va[k] = picks.get(iv).fx;
                        System.out.println(ta[k] + " ########## " + va[k]);
                        k++;
                    }

                    // Create interpolated data

                    for (int it = 0; it < m_ns; it++) {
                        data[i2][it] = interpLinear(it * m_dt, ta, va);

                    }
                    i2++;
                }
            }

            if (dtime != 0) {
                float[][] ddata = new float[data.length][ntime];

                int nskipCdp = (int) (dcdp / d2 - 1);
                int nskipTime = (int) (dtime / d1 - 1);
                smoothData(data, ddata, data.length, n1, data.length, ntime, nskipCdp, nskipTime, wndA, wndB);

                System.out.println(ntime + " " + f1 + " " + d1 + " " + n2 + " "
                        + f2 + " " + d2 + " " + n1 + " " + f1 + " " + dtime + " "
                        + ncdps + " " + f2 + " " + dcdp);
                float[][] temp = new float[data.length][m_data[0].length];
                resampleData(ddata, temp, ntime, f1, dtime, n2, f2, d2,
                        n1, f1, dt, data.length, f2, dcdp);
                data = temp.clone();

            }



            for (int i = 0; i < ncdps; i++) {
                int inc = mw.m_cdpInterval - 1;
                int v = 0;
                v = (i / mw.m_cdpInterval) * mw.m_cdpInterval;
                int v2 = v + mw.m_cdpInterval;

                if (v == 0) {

                    for (int it = 0; it < m_ns; it++) {
                        data2[i][it] = data[0][it];
                    }
                } else {
                    if (v == mw.m_velocityPicks.lastKey()) {
                        for (int it = 0; it < m_ns; it++) {
                            data2[i][it] = data[mw.m_velocityPicks.size() - 1][it];
                        }
                    } else {
                        for (int it = 0; it < m_ns; it++) {
                            float dx = (float) ((data[(v2 / mw.m_cdpInterval) - 1][it] - data[(v / mw.m_cdpInterval) - 1][it]) / (mw.m_cdpInterval - 1.0));
                            int d = i - v - 1;
                            data2[i][it] = data[(v / mw.m_cdpInterval) - 1][it] + (dx * d);

                        }
                    }
                }
            }


//            if (dtime != 0) {
//                float[][] ddata = new float[ncdps][ntime];
//               
//                int nskipCdp = (int) (dcdp / d2 - 1);
//                int nskipTime = (int) (dtime / d1 - 1);
//                smoothData(data2, ddata, ncdps, n1, ncdps, ntime, nskipCdp, nskipTime, wndA, wndB);               
//
//                System.out.println(ntime + " " + f1 + " " + d1 + " " + n2 + " " 
//                        + f2 + " " + d2 + " " + n1 + " " + f1 + " " + dtime + " " 
//                        + ncdps + " " + f2 + " " + dcdp);
//                float[][] temp = new float[ncdps][m_data[0].length];
//                resampleData(ddata, temp, ntime, f1, d1, n2, f2, d2,
//                        n1, f1, dt, ncdps, f2, dcdp);
//                data2 = temp.clone();
//
//            }

            java.io.File sFile = dlg.getSelectedFile();
            java.io.FileOutputStream fs = new java.io.FileOutputStream(sFile);

            SUTrace tr = new SUTrace();

            tr.getHeader().f1 = m_currF1;
            tr.getHeader().d1 = mw.m_dt;
            tr.getHeader().f2 = m_currF2;
            tr.getHeader().d2 = m_currD2;
            tr.getHeader().ns = (char) n1;

            float[] data3 = new float[m_currN1];
            float a;
            for (int i = 0; i < ncdps; i++) {
                for (int j = 0; j < m_currN1; j++) {
                    data3[j] = data2[i][j];
                }
                tr.getHeader().cdp = (int) icdp + i;
                tr.setData(data3);
                tr.writeToFile(fs, true);
            }

            fs.close();

            // Save same sampling of the original data
//            float[][] rsData = new float[ncdps][n1];
//
//            resampleData(data2, rsData,
//                    m_currN1, m_currF1, m_currD1,
//                    m_currN2, m_currF2, m_currD2,
//                    n1, f1, d1,
//                    n2, f2, d2);
//
//            sFile = new java.io.File("velmodel-mig.su");
//            fs = new java.io.FileOutputStream(sFile);
//
//            tr = new SUTrace();
//
//            tr.getHeader().f1 = f1;
//            tr.getHeader().d1 = mw.m_dt;
//            tr.getHeader().f2 = mw.m_cdpMin;
//            tr.getHeader().d2 = d2;
//            tr.getHeader().ns = (char) n1;
//
//            data3 = new float[n1];
//
//            for (int i = 0; i < ncdps; i++) {
//                for (int j = 0; j < n1; j++) {
//                    data3[j] = rsData[i][j];
//                }
//                tr.getHeader().cdp = (int) icdp + i;
//                tr.setData(data3);
//                tr.writeToFile(fs, true);
//            }


//            SUTrace tr = new SUTrace();
//
//            tr.getHeader().f1 = m_currF1;
//            tr.getHeader().d1 = m_currD1;
//            tr.getHeader().f2 = m_currF2;
//            tr.getHeader().d2 = m_currD2;
//            tr.getHeader().ns = (char) m_currN1;
//
//            float[] data = new float[m_currN1];
//
//            float a;
//            for (int i = 0; i < m_currN2; i++) {
//                for (int j = 0; j < m_currN1; j++) {
//                    data[j] = m_data[i][j];
//                }
//                tr.getHeader().cdp = (int) (m_currF2 + i * m_currD2);
//                tr.setData(data);
//                tr.writeToFile(fs);
//            }
//
//            fs.close();
//
//            // Save same sampling of the original data
//            float[][] rsData = new float[n2][n1];
//
//            resampleData(m_data, rsData,
//                    m_currN1, m_currF1, m_currD1,
//                    m_currN2, m_currF2, m_currD2,
//                    n1, f1, d1,
//                    n2, f2, d2);
//
//            sFile = new java.io.File("velmodel-mig.su");
//            fs = new java.io.FileOutputStream(sFile);
//
//            tr = new SUTrace();
//
//            tr.getHeader().f1 = f1;
//            tr.getHeader().d1 = d1;
//            tr.getHeader().f2 = f2;
//            tr.getHeader().d2 = d2;
//            tr.getHeader().ns = (char) n1;
//
//            data = new float[n1];
//
//            for (int i = 0; i < n2; i++) {
//                for (int j = 0; j < n1; j++) {
//                    data[j] = rsData[i][j];
//                }
//                tr.getHeader().cdp = (int) (f2 + i * d2);
//                tr.setData(data);
//                tr.writeToFile(fs);
//            }

//            fs.close();
        }
    } catch (Exception ex) {
        ex.printStackTrace();
    }// TODO add your handling code here:
}//GEN-LAST:event_menuSaveActionPerformed

    public float[][] getDataInterp() {

        int icdp = mw.m_cdpMin;
        int ecdp = mw.m_cdpMax;
        int ncdps = (ecdp - icdp) + 1;

        int m_ns = mw.m_ns;
        float m_dt = mw.m_dt;
        float[][] data2 = new float[ncdps][m_ns];

        float[][] data = new float[mw.m_velocityPicks.size()][m_ns];
        int i2 = 0;
        for (Integer key : mw.m_velocityPicks.keySet()) {
            Vector<gfx.SVPoint2D> picks = mw.m_velocityPicks.get(key);
            Collections.sort(picks, new Comparator<gfx.SVPoint2D>() {

                @Override
                public int compare(SVPoint2D arg0, SVPoint2D arg1) {
                    Float o1 = new Float(arg0.fy);
                    Float o2 = new Float(arg1.fy);
                    return o1.compareTo(o2);
                }
            });
            if (picks.size() > 0) {
                int nv = picks.size();
                nv += 2;
                float[] ta = new float[nv];
                float[] va = new float[nv];

                ta[0] = 0.0f;
                va[0] = picks.get(0).fx;
                ta[nv - 1] = m_ns * m_dt;
                va[nv - 1] = picks.get(picks.size() - 1).fx;

                int k = 1;
                for (int iv = 0; iv < picks.size(); iv++) {
                    ta[k] = picks.get(iv).fy;
                    va[k] = picks.get(iv).fx;
                    System.out.println(ta[k] + " ########## " + va[k]);
                    k++;
                }

                // Create interpolated data

                for (int it = 0; it < m_ns; it++) {
                    data[i2][it] = interpLinear(it * m_dt, ta, va);

                }
                i2++;
            }
        }

        if (dtime != 0) {
            float[][] ddata = new float[data.length][ntime];

            int nskipCdp = (int) (dcdp / d2 - 1);
            int nskipTime = (int) (dtime / d1 - 1);
            smoothData(data, ddata, data.length, n1, data.length, ntime, nskipCdp, nskipTime, wndA, wndB);

            System.out.println(ntime + " " + f1 + " " + d1 + " " + n2 + " "
                    + f2 + " " + d2 + " " + n1 + " " + f1 + " " + dtime + " "
                    + ncdps + " " + f2 + " " + dcdp);
            float[][] temp = new float[data.length][m_data[0].length];
            resampleData(ddata, temp, ntime, f1, dtime, n2, f2, d2,
                    n1, f1, dt, data.length, f2, dcdp);
            data = temp.clone();

        }



        for (int i = 0; i < ncdps; i++) {
            int inc = mw.m_cdpInterval - 1;
            int v = 0;
            v = (i / mw.m_cdpInterval) * mw.m_cdpInterval;
            int v2 = v + mw.m_cdpInterval;

            if (v == 0) {

                for (int it = 0; it < m_ns; it++) {
                    data2[i][it] = data[0][it];
                }
            } else {
                if (v == mw.m_velocityPicks.lastKey()) {
                    for (int it = 0; it < m_ns; it++) {
                        data2[i][it] = data[mw.m_velocityPicks.size() - 1][it];
                    }
                } else {
                    for (int it = 0; it < m_ns; it++) {
                        float dx = (float) ((data[(v2 / mw.m_cdpInterval) - 1][it] - data[(v / mw.m_cdpInterval) - 1][it]) / (mw.m_cdpInterval - 1.0));
                        int d = i - v - 1;
                        data2[i][it] = data[(v / mw.m_cdpInterval) - 1][it] + (dx * d);

                    }
                }
            }
        }

        return data2;
    }

    private void resampleData(float[][] input, float[][] output,
            int n1, float f1, float d1,
            int n2, float f2, float d2,
            int n1Out, float f1Out, float d1Out,
            int n2Out, float f2Out, float d2Out) {

        float[][] buff = new float[n2][n1Out];

        float[] ta = new float[n1];
        float[] va = new float[n1];

        // Interpolate along trace samples
        for (int icdp = 0; icdp < n2; icdp++) {
            for (int it = 0; it < n1; it++) {
                ta[it] = f1 + it * d1;
                va[it] = input[icdp][it];
            }
            for (int it = 0; it < n1Out; it++) {
                buff[icdp][it] = interpLinear(f1 + it * d1Out, ta, va);
            }
        }

        // Interpolate along cdps
        float[] x = new float[n2];
        float[] v = new float[n2];

        for (int it = 0; it < n1Out; it++) {
            for (int icdp = 0; icdp < n2; icdp++) {
                x[icdp] = f2 + icdp * d2;
                v[icdp] = buff[icdp][it];
            }
            for (int icdp = 0; icdp < n2Out; icdp++) {
                output[icdp][it] = interpLinear(f2 + icdp * d2Out, x, v);
            }
        }
    }

    float interpLinear(float u, float[] x, float[] y) {
        int len = x.length;
        float ret = 0.0f;

        // simple linear seach
        for (int i = 0; i < len - 1; i++) {
            if ((u >= x[i]) && (u <= x[i + 1])) {
                ret = y[i] + (u - x[i]) * (y[i + 1] - y[i]) / (x[i + 1] - x[i]);
                break;
            }
        }

        if (u < x[0]) {
            return y[0];
        }
        if (u > x[len - 1]) {
            return y[len - 1];
        }

        return ret;
    }

    /**
     * Apply a moving average smoothing to the original data
     *
     */
    private void smoothData(float[][] input, float[][] output,
            int n1, int n2, int n1Out, int n2Out,
            int nskipCdp, int nskipTime, int wndA, int wndB) {
        float sum;
        int nsum;

        int i;
        int j;

        for (int iout = 0; iout < n1Out; iout++) {
            i = iout * (nskipCdp + 1);
            for (int jout = 0; jout < n2Out; jout++) {
                j = jout * (nskipTime + 1);
                // Loop over input data
                sum = 0.0f;
                nsum = 0;
                for (int m = i - wndA; m <= i + wndA; m++) {
                    if ((m >= 0) && (m < n1)) {
                        for (int n = j - wndB; n <= j + wndB; n++) {
                            if ((n >= 0) && (n < n2)) {
                                sum += input[m][n];
                                nsum++;
                            }
                        }
                    }
                }
                if (nsum > 1) {
                    output[iout][jout] = sum / (nsum - 1);
                } else {
                    output[iout][jout] = sum;
                }
            }
        }
    }

    public void setFirstPlot(float[][] pData, float f1, float d1, int n1, float f2, float d2, int n2) {
        float[] data = new float[n1 * n2];
        for (int i = 0; i < n2; i++) {
            for (int j = 0; j < n1; j++) {
                data[(i * n1) + j] = pData[i][j];
            }
        }
        m_data = pData;

        this.f1 = f1;
        this.d1 = d1;
        this.n1 = n1;
        this.f2 = f2;
        this.d2 = d2;
        this.n2 = n2;
        m_currF1 = f1;
        m_currD1 = d1;
        m_currN1 = n1;

        m_currF2 = f2;
        m_currD2 = d2;
        m_currN2 = n2;
        float xmin = f2;
        float xmax = f2 + (n2 - 1) * d2;
        float ymin = f1;
        float ymax = f1 + n1 * d1;

//        System.out.println("f1 "+f1+" d1"+d1+" n1 "+n1+" f2 "+f2+" d2 "+d2+" n2 "+n2 );
        System.out.println("N1: " + n1 + " N2: " + n2 + " F1: " + f1 + " F2 " + f2 + " D1: " + d1 + " D2: " + d2);
        getGfxPanelCDP().setAxesLimits(f1, (f1 + n1) * d1, f2, f2 + n2 * d2);
        m_timeAxis.setLimits(f1, (f1 + n1) * d1);
//        m_cdpOffsetAxis.setLimits(f2, f2 + n2 * d2);


        m_cdpOffsetAxis.setLimits(f2, d2 * n2);
        m_cdpOffsetAxis.setLimitsInitial(f2, d2 * f2);

        panelB.repaint();

        m_csActor = new gfx.SVColorScale(3, gfx.SVColorScale.NORMAL);
        m_csActor.setData(data, n1, f1, d1, n2, f2, d2);
        m_csActor.setImagePerc(imageperc);
//        m_csActor.setbalance(imagebalance);
        m_csActor.setColormap(m_currMapType, m_currMapColor);

        getGfxPanelCDP().addActor(m_csActor);
        m_gfxPanelColorbar = new GfxPanelColorbar(m_csActor, GfxPanelColorbar.HORIZONTAL);

        colorbarPanel.removeAll();
        colorbarPanel.add(m_gfxPanelColorbar);


    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel colorbarPanel;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JMenuItem menuExit;
    private javax.swing.JMenuItem menuSave;
    private javax.swing.JMenuItem menuSmoothVelocity;
    private javax.swing.JMenu menuView;
    private javax.swing.JPanel panelA;
    private javax.swing.JPanel panelB;
    public javax.swing.JPanel panelCDP;
    private javax.swing.JPanel panelPkey;
    // End of variables declaration//GEN-END:variables
    gfx.SVGraphicsPanel gfxPanelCDP = new gfx.SVGraphicsPanel();
    GfxPanelColorbar m_gfxPanelColorbar;
    gfx.SVXYPlot lastVelocity = new gfx.SVXYPlot();
    gfx.SVXYPlot velocity = new gfx.SVXYPlot();
    gfx.SVAxis m_timeAxis;
    gfx.SVAxis m_cdpOffsetAxis;
    private gfx.SVXYPlot mHeader;
    gfx.SVColorScale m_csActor;
    gfx.SVWiggle m_wgActor;
    gfx.SVContourmap m_cmActor;
    SVPoint2D p1Zoom = new SVPoint2D();
    SVPoint2D p2Zoom = new SVPoint2D();
    float m_data[][];
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
    int imagebalance;
    int keyMapSection;
    float imageperc;
    float wigbperc;
    ArrayList<Vector<SUTrace>> mapSection = new ArrayList<Vector<SUTrace>>();
    DialogHeaderTrace dlgHeader;
    DialogGain dlgGain;
    int n1;
    int n2;
    int m_currMapType;
    int m_currMapColor;
    float f1;
    float f2;
    float d1;
    float d2;
    float m_currF1;
    float m_currD1;
    int m_currN1;
    float m_currF2;
    float m_currD2;
    int m_currN2;
    MainWindow mw;
    int wndA;
    int wndB;
    int dcdp;
    float dtime;
    int ntime;
    float dt;

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

class InputCdps extends JDialog {

    JTextField jt1 = new JTextField();
    JTextField jt2 = new JTextField();

    public InputCdps(DataView dv) {
        super(dv, true);
        setMinimumSize(new Dimension(300, 100));
        setLayout(new BorderLayout());
        JPanel jp = new JPanel();
        jp.setBorder(new TitledBorder("Cdps"));
        jp.setLayout(new GridLayout(1, 4));
        jp.add(new JLabel("Min:"));
        jp.add(jt1);
        jp.add(new JLabel("Max:"));
        jp.add(jt2);
        add(jp, BorderLayout.CENTER);
        JButton jb = new JButton("OK");
        jb.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                dispose();
            }
        });
        add(jb, BorderLayout.SOUTH);

    }
}