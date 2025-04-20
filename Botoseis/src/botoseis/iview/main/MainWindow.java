/*
 * MainWindow.java
 *
 * Created on November 25, 2008, 9:03 PM
 */
package botoseis.iview.main;

import botoseis.iview.dialogs.DialogGain;
import botoseis.iview.dialogs.DialogHeaderTrace;
import botoseis.iview.dialogs.DialogParametersImage;
import botoseis.iview.utils.PicksFileIO;
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
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import usrdata.SUHeader;
import usrdata.SUSection;
import usrdata.SUTrace;

/**
 *
 * @author williams
 */
public class MainWindow extends javax.swing.JFrame {

    /**
     * Creates new form MainWindow
     */
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
        System.out.println("USING FORMAT: " + preferences.getFormat());

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

            @Override
            public void mouseClicked(MouseEvent event) {
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
                    dlgHeader.setLocation(event.getXOnScreen(), event.getYOnScreen());
                    dlgHeader.setVisible(true);
                }

                SVPoint2D mouseLocation = gfxPanelCDP.getMouseLocation();
                System.err.println();
                printt("\nmouse: clicked");
                printt("mouseLocation:");
                printt("  fx: " + mouseLocation.fx);
                printt("  fy: " + mouseLocation.fy);
                printt("  ix: " + mouseLocation.ix);
                printt("  iy: " + mouseLocation.iy);

                float XlenghtHI = section.getF2();  // = H(1).sx / scalco

                float distanciax = section.getD2();
                float delrt = section.getF1();  // primeiro tempo do matlab = 0

                float dt = section.getD1(); // = dt do matlab = 0.004

                int XMatr = Math.round((mouseLocation.fx - XlenghtHI) / distanciax) + 1;
                int YMatr = Math.round((mouseLocation.fy - delrt) / dt) + 1;

                int n_linhamaximo = section.getN1();
                int n = YMatr - 1 + (XMatr - 1) * (n_linhamaximo);

                int trace = n / section.getN1();

                printt("section.getF2() ≡ XlenghtHI:  " + XlenghtHI);
                printt("section.getD2() ≡ distanciax: " + distanciax);
                printt("section.getF1() ≡ delrt:      " + delrt);
                printt("section.getD1() ≡ dt          " + dt);
                printt("XMatr: " + XMatr);
                printt("YMatr: " + YMatr);
                printt("section.getN1() ≡ n_linhamaximo: " + n_linhamaximo);
                printt("n:     " + n);
                printt("trace: " + trace);
                SUHeader h = section.getTraces().get(trace).getHeader();
//                printt(String.format("fldr: %d tracf: %d cdp: %d ep: %d offset: %d  time: %.2f", h.fldr, h.tracf, h.cdp, h.ep, h.offset, mouseLocation.fy));

                if (!toggleButtonPicks.isSelected()) {
                    return;
                }

                switch (event.getButton()) {
                    case java.awt.event.MouseEvent.BUTTON1:
                        addActualPick(mouseLocation);
                        break;
                    case java.awt.event.MouseEvent.BUTTON2:
                        removePickIfNearMouseLocation(mouseLocation);
                        break;
                }
            }

            @Override
            public void mousePressed(MouseEvent event) {
                printt("\nMOUSE: pressed");

                if (btnZoom.isSelected()) {
                    p1Zoom = gfxPanelCDP.getMouseLocation();
                }
                switch (event.getButton()) {
                    case MouseEvent.BUTTON1:
                        isLeftMouseButtonPressed = true;
                        printt("MOUSE: left button pressed");
                        break;
                    case MouseEvent.BUTTON3:
                        isRightMouseButtonPressed = true;
                        printt("MOUSE: right button pressed");
                        break;

                }
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent event) {
                printt("\nMOUSE: released");
                SVPoint2D mouseLocation = gfxPanelCDP.getMouseLocation();

                if (btnZoom.isSelected()) {
                    p2Zoom = gfxPanelCDP.getMouseLocation();
                    onGraphicsPanelMouseReleased(event);
                }

                if (isPrevisualizingWithPreviewPick) {
                    // add the current temporary pick as an actual pick
                    addActualPick(gfxPanelCDP.getMouseLocation());
                    isPrevisualizingWithPreviewPick = false;
                    isMovingExistingPick = false;
                }

                switch (event.getButton()) {
                    case MouseEvent.BUTTON1:
                        printt("MOUSE: left button released");

                        isLeftMouseButtonPressed = false;
                        break;
                    case MouseEvent.BUTTON3:
                        printt("MOUSE: right button released");
                        isRightMouseButtonPressed = false;

                        if (toggleButtonPicks.isSelected()) {
                            // Picks eraser line
                            isDrawingEraserLine = false;
                            picksEraserLineGraphicalPlot.setVisible(false);
                            removePicksAtRange(picksEraserLineStart.fx, mouseLocation.fx);
                            break;
                        }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                getGfxPanelCDP().requestFocus();
            }

            @Override
            public void mouseExited(MouseEvent e) {
//                throw new UnsupportedOperationException("Not supported yet.");
            }
        });

        gfxPanelCDP.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent event) {

                if (!toggleButtonPicks.isSelected()) {
                    return;
                }

                SVPoint2D mouseLocation = getGfxPanelCDP().getMouseLocation();
                if (isLeftMouseButtonPressed) {
                    // Move existing pick
                    //   Remove pick at mouse location at start of dragging
                    //   and preview pick will work as if moving existing pick
                    if (!isMovingExistingPick && !isPrevisualizingWithPreviewPick) {
                        findPickNearMouseLocation(mouseLocation).ifPresent(pick -> {
                            isMovingExistingPick = true;
                            picksListActual.remove(pick);
                            printt("Removed pick at mouse location for moving existing pick");
                        });
                    }
                    // Preview pick
                    //   Continously update preview pick while dragging mouse
                    updateTemporaryPreviewPick(mouseLocation.fx, mouseLocation.fy);
                    isPrevisualizingWithPreviewPick = true;
                } else if (isRightMouseButtonPressed) {
                    if (isDrawingEraserLine) {
                        SVPoint2D picksEraserLineEnd = mouseLocation;
                        updatePicksEraserLinePlot(picksEraserLineStart, picksEraserLineEnd);
                    } else {
                        printt("Starting picks eraser line");
                        picksEraserLineStart = mouseLocation;
                        updatePicksEraserLinePlot(picksEraserLineStart, picksEraserLineStart);
                        picksEraserLineGraphicalPlot.setVisible(true);
                        isDrawingEraserLine = true;
                    }
                }
            }

            @Override
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

//        gfxPanelCDP.addMouseListener(new java.awt.event.MouseAdapter() {
//            @Override
//            public void mouseClicked(java.awt.event.MouseEvent evt) {
//                
//            }
//        });
        picksGraphicalPlot.setLineStyle(SVXYPlot.SOLID);
        picksGraphicalPlot.setPointsVisible(true);
        picksGraphicalPlot.setDrawColor(java.awt.Color.RED);
        picksGraphicalPlot.setDrawSize(2);
        gfxPanelCDP.addXYPlot(picksGraphicalPlot);

        picksEraserLineGraphicalPlot.setLineStyle(SVXYPlot.SOLID);
        picksEraserLineGraphicalPlot.setPointsVisible(false);
        picksEraserLineGraphicalPlot.setDrawColor(java.awt.Color.MAGENTA);
        picksEraserLineGraphicalPlot.setDrawSize(3);
        gfxPanelCDP.addXYPlot(picksEraserLineGraphicalPlot);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
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
        jSeparator2 = new javax.swing.JToolBar.Separator();
        toggleButtonPicks = new javax.swing.JToggleButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuExit = new javax.swing.JMenuItem();
        menuView = new javax.swing.JMenu();
        menuViewImage = new javax.swing.JMenuItem();
        menuViewWiggle = new javax.swing.JMenuItem();
        menuViewContour = new javax.swing.JMenuItem();
        menuViewImageWiggle = new javax.swing.JMenuItem();
        menuViewImageContour = new javax.swing.JMenuItem();
        menuPicking = new javax.swing.JMenu();
        menuItemSavePicks = new javax.swing.JMenuItem();
        menuItemOpenPickFile = new javax.swing.JMenuItem();
        menuItemNewPickFile = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
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

        colorbarPanel.setBackground(java.awt.SystemColor.control);
        colorbarPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        colorbarPanel.setLayout(new java.awt.GridLayout(1, 0));

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
        jToolBar1.add(jSeparator2);

        toggleButtonPicks.setText("Picking");
        toggleButtonPicks.setFocusable(false);
        toggleButtonPicks.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toggleButtonPicks.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toggleButtonPicks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonPicksActionPerformed(evt);
            }
        });
        jToolBar1.add(toggleButtonPicks);

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

        menuPicking.setText("Picking");

        menuItemSavePicks.setText("Save current picks");
        menuItemSavePicks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemSavePicksActionPerformed(evt);
            }
        });
        menuPicking.add(menuItemSavePicks);

        menuItemOpenPickFile.setText("FB open picks file...");
        menuItemOpenPickFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemOpenPickFileActionPerformed(evt);
            }
        });
        menuPicking.add(menuItemOpenPickFile);

        menuItemNewPickFile.setText("FB new picks file...");
        menuItemNewPickFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemNewPickFileActionPerformed(evt);
            }
        });
        menuPicking.add(menuItemNewPickFile);

        jMenuItem1.setText("HZ open picks file..");
        menuPicking.add(jMenuItem1);

        jMenuItem2.setText("HZ new picks file...");
        menuPicking.add(jMenuItem2);

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
                        .addComponent(panelStatusbar, javax.swing.GroupLayout.DEFAULT_SIZE, 763, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 740, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(0, 0, 0))
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
    printt("formWindowOpened()");
    if (!stackData) {
        setModeView("wiggle");
    } else {
        setModeView("image");
    }
//    showView();  // unecessary since setModeView() always calls showView()
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
    printt("btnNextActionPerformed()");

    // Save picks from current gather in memory
    // ----------------------------------------
    printt("  section.getTraces().get(0).getHeader().fldr: " + section.getTraces().get(0).getHeader().fldr);
    printt("  treeMapPicks.size(): " + mapOfPickLists.size());
    if (picksListActual.size() > 1) {
        int currentGatherKey = section.getTraces().get(0).getHeader().fldr;
        mapOfPickLists.put(currentGatherKey, (ArrayList<SVPoint2D>) picksListActual.clone());
        printt("saved to memory ep " + currentGatherKey);
    }
    // Save picks from current gather in file
    // --------------------------------------
    if (picksListActual.size() > 1) {
        PicksFileIO.savePicksFromCurrentGather(picksPath, picksListActual, section);
    }

    // TODO
    // ----
    printt("  section.getTraces().size()                      = " + section.getTraces().size());
    printt("  mapSection.size()                               = " + mapSection.size());
    printt("  mapSection.lastIndexOf(section.getTraces())     = " + mapSection.lastIndexOf(section.getTraces()));
    printt("  mapSection.lastIndexOf(section.getTraces()) + 1 = " + String.valueOf(mapSection.lastIndexOf(section.getTraces()) + 1));
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

    // Load picks from new gather if they exist
    // ----------------------------------------
    // section now has changed to another gather
    // lets load picks from it if it exists
    tryLoadPicksFromCurrentGather();

    showView();
}//GEN-LAST:event_btnNextActionPerformed

private void btnPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousActionPerformed
    printt("btnPreviousActionPerformed()");

    // Save picks from current gather in memory
    // ----------------------------------------
    printt("  section.getTraces().get(0).getHeader().ep: " + section.getTraces().get(0).getHeader().ep);
    printt("  hashMapPicks.size(): " + mapOfPickLists.size());
    if (picksListActual.size() > 1) {
        int current_ep = section.getTraces().get(0).getHeader().ep;
        mapOfPickLists.put(current_ep, (ArrayList<SVPoint2D>) picksListActual.clone());
    }
    // Save picks from current gather in file
    // --------------------------------------
    if (picksListActual.size() > 1) {
        PicksFileIO.savePicksFromCurrentGather(picksPath, picksListActual, section);
    }

    // TODO
    // ----
    printt("  section.getTraces().size()                  = " + section.getTraces().size());
    printt("  mapSection.size()                           = " + mapSection.size());
    printt("  mapSection.indexOf(section.getTraces())     = " + mapSection.indexOf(section.getTraces()));
    printt("  mapSection.indexOf(section.getTraces()) - 1 = " + String.valueOf(mapSection.indexOf(section.getTraces()) - 1));
    if (mapSection.indexOf(section.getTraces()) > 0) {
        section.setTraces(mapSection.get(mapSection.indexOf(section.getTraces()) - 1));
    }

    // Load picks from new gather if they exist
    // ----------------------------------------
    // section now has changed to another gather
    // lets load picks from it if it exists
    clearPicks();
    tryLoadPicksFromCurrentGather();

    showView();
}//GEN-LAST:event_btnPreviousActionPerformed

    private void tryLoadPicksFromCurrentGather() {
        int currentGatherKey = section.getTraces().get(0).getHeader().fldr;
        // try to load from memory
        ArrayList<SVPoint2D> picks = mapOfPickLists.get(currentGatherKey);
        if (picks != null) {
            picksListActual = picks;
            updatePicksPlotFromList(picksListActual);
        } else {
            // otherwise try to load from file
            picks = PicksFileIO.loadPicksFromGather(picksPath, currentGatherKey, section);
            if (!picks.isEmpty()) {
                picksListActual = picks;
                updatePicksPlotFromList(picksListActual);
            }
        }
    }

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

    public static Path showFileCreationDialog(JFrame parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose location to create new file");
        fileChooser.setApproveButtonText("Create");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int userSelection = fileChooser.showSaveDialog(parent);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().toPath();
        }

        return null;
    }

    public static void showWarning(String message) {
        JOptionPane.showMessageDialog(
                null,
                message,
                "Warning",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void toggleButtonPicksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleButtonPicksActionPerformed
        // TODO add your handling code here:
        if (picksPath == null) {
            showWarning("no pick file was opened");
            toggleButtonPicks.setSelected(false);
        }
    }//GEN-LAST:event_toggleButtonPicksActionPerformed

    private void menuItemOpenPickFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemOpenPickFileActionPerformed
        // TODO add your handling code here:
        picksPath = chooseFile();
        if (picksPath != null) {
            mapOfPickLists.clear();
            tryLoadPicksFromCurrentGather();
            toggleButtonPicks.setSelected(true);
        }
    }//GEN-LAST:event_menuItemOpenPickFileActionPerformed

    private void menuItemNewPickFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemNewPickFileActionPerformed
        printt("menuItemNewPickFileActionPerformed(evt)");

        Path selectedPath = showFileCreationDialog(this);

        if (selectedPath == null) {
            return;
        }

        mapOfPickLists.clear();
        clearPicks();
        picksPath = selectedPath;
    }//GEN-LAST:event_menuItemNewPickFileActionPerformed

    private void menuItemSavePicksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemSavePicksActionPerformed
        printt("btnSavePicksActionPerformed(evt)");

        if (picksPath == null) {
            showWarning("no pick file was opened");
            return;
        }

        if (picksListActual.size() < 2) {
            printt("  Unable to save a single pick. Cancelling operation.");
            return;
        }
        PicksFileIO.savePicksFromCurrentGather(picksPath, picksListActual, section);
    }//GEN-LAST:event_menuItemSavePicksActionPerformed

    private static Path chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().toPath();
        } else {
            return null; // No file selected or operation canceled
        }
    }

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
        printt(String.format("setModeView(\"%s\")", modeview));
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
        printt("showView()");
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
        printt("showImage()");
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
        m_csActor.setColormap(m_currMapType, m_currMapColor);
        m_gfxPanelColorbar = new GfxPanelColorbar(m_csActor, GfxPanelColorbar.HORIZONTAL);
        colorbarPanel.removeAll();
        colorbarPanel.add(m_gfxPanelColorbar);

        getGfxPanelCDP().addActor(m_csActor);
        applyGain(m_csActor);

    }

    public void setAxis() {
        printt("setAxis()");
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
        printt(String.format("showWiggle(%b)", value));
//        printt("  section.getN1(): " + section.getN1());
//        printt("  section.getN2(): " + section.getN2());
//        printt("  section.getF1(): " + section.getF1());
//        printt("  section.getF2(): " + section.getF2());
//        printt("  section.getD1(): " + section.getD1());
//        printt("  section.getD2(): " + section.getD2());
//        printt("  section.getData().length: " + section.getData().length);        
//        printt(Arrays.toString(Arrays.copyOfRange(section.getData(), 0, 501)));
        int n1 = section.getN1();
        int n2 = section.getN2();
        float f1 = section.getF1();
        float f2 = section.getF2();
        float d1 = section.getD1();
        float d2 = section.getD2();
        getGfxPanelCDP().setAxesLimits(f1, f1 + n1 * d1, f2, f2 + n2 * d2);
        m_timeAxis.setLimits(f1, f1 + n1 * d1);
        m_cdpOffsetAxis.setLimits(f2, f2 + n2 * d2);
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
        printt("initData()");
        section = new usrdata.SUSection();
        updatePreferences();
        section.setPreStakcData(!stackData);
        if (pkey != null) {
            section.setPkey(pkey);
        }
        section.readFromInputStream(System.in);
        mapSection.add((Vector<SUTrace>) section.getTraces().clone());
//        loadedGatherList.add((List<SUTrace>) section.getTraces().clone());
    }

    public void updatePreferences() {
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
                printt("run()");
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
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JMenuItem menuExit;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuItem menuItemNewPickFile;
    private javax.swing.JMenuItem menuItemOpenPickFile;
    private javax.swing.JMenuItem menuItemSavePicks;
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
    private javax.swing.JToggleButton toggleButtonPicks;
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
    ArrayList<ArrayList<SVPoint2D>> mapSectionPicks = new ArrayList<>();
    TreeMap<Integer, ArrayList<SVPoint2D>> mapOfPickLists = new TreeMap<>();
    List<List<SUTrace>> loadedGatherList = new ArrayList<>();
    DialogHeaderTrace dlgHeader;
    DialogGain dlgGain;
    int m_currMapColor;
    int m_currMapType;
    Preferences preferences;
    List<SVPoint2D> m_currentPicks = new ArrayList<>();
    SVXYPlot picksGraphicalPlot = new SVXYPlot();
    SVXYPlot picksEraserLineGraphicalPlot = new SVXYPlot();
    ArrayList<SVPoint2D> picksListActual = new ArrayList<>();
    SVPoint2D picksEraserLineStart = new SVPoint2D();
    private boolean isLeftMouseButtonPressed = false;
    private boolean isRightMouseButtonPressed = false;
    private boolean isPrevisualizingWithPreviewPick = false;
    private boolean isMovingExistingPick = false;
    private boolean isDrawingEraserLine = false;
    private Path picksPath = null;

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

    public static void printt(Object x) {
        System.err.println(x);
    }

    private void addVelocityPick(float v, float t, int vx, int vy) {
        gfx.SVPoint2D p = new gfx.SVPoint2D();

        p.fx = v;
        p.fy = t;
        p.ix = vx;
        p.iy = vy;

        m_currentPicks.add(p);
        updateVelocityPicksCurve(0, 0);
    }

    private void updatePicksEraserLinePlot(SVPoint2D start, SVPoint2D end) {
        float[] pointsX = new float[2];
        float[] pointsY = new float[2];
        pointsX[0] = start.fx;
        pointsX[1] = end.fx;
        pointsY[0] = start.fy;
        pointsY[1] = end.fy;
//        printt(String.format("START (%.1f,%.1f)    END (%.1f, %.1f)", pointsX[0], pointsX[1], pointsY[0], pointsY[1]));
        picksEraserLineGraphicalPlot.update(pointsX, pointsY);
        gfxPanelCDP.repaint();
    }

    private void clearPicks() {
        picksListActual.clear();
        picksGraphicalPlot.clear();
        gfxPanelCDP.repaint();
    }

    private void updatePicksPlotFromList(List<SVPoint2D> picksList) {
        float[] picksX = new float[picksList.size()];
        float[] picksY = new float[picksList.size()];
        for (int i = 0; i < picksList.size(); i++) {
            picksX[i] = picksList.get(i).fx;
            picksY[i] = picksList.get(i).fy;
        }
        picksGraphicalPlot.update(picksX, picksY);
        gfxPanelCDP.repaint();
    }

    private void addActualPick(SVPoint2D mouseLocation) {
        printt("addActualPick(...)");
        picksListActual.add(mouseLocation);
        picksListActual.sort(SVPoint2DComparator.getInstance());
        updatePicksPlotFromList(picksListActual);
    }

    private void updateTemporaryPreviewPick(float fx, float fy) {
        List<SVPoint2D> picksListPreview = new ArrayList<>(picksListActual);
        final SVPoint2D pickTemporaryPreview = new SVPoint2D();
        pickTemporaryPreview.fx = fx;
        pickTemporaryPreview.fy = fy;

        // Insert pick at correct position to maintain the list sorted
        int insertionPoint = Collections.binarySearch(picksListPreview, pickTemporaryPreview, SVPoint2DComparator.getInstance());
        if (insertionPoint < 0) {
            insertionPoint = -insertionPoint - 1;
        }
        picksListPreview.add(insertionPoint, pickTemporaryPreview);

        updatePicksPlotFromList(picksListPreview);
    }

    private static boolean isPickNearMouseLocation(SVPoint2D pick, SVPoint2D mouseLocation) {
        final float EPS_X = 0.037f;
        final float EPS_Y = 0.037f;
        return (Math.abs(pick.fx - mouseLocation.fx) <= EPS_X) && (Math.abs(pick.fy - mouseLocation.fy) <= EPS_Y);
    }

    private Optional<SVPoint2D> findPickNearMouseLocation(SVPoint2D mouseLocation) {
        return picksListActual.stream()
                .filter(pick -> isPickNearMouseLocation(pick, mouseLocation))
                .findFirst();
    }

    private void removePicksAtRange(float start, float end) {
        final float EPS_X = 0.037f;
        float correctStart;
        float correctEnd;
        if (start < end) {
            correctStart = start - EPS_X;
            correctEnd = end + EPS_X;
        } else {
            correctStart = end - EPS_X;
            correctEnd = start + EPS_X;
        }

        picksListActual.removeIf(
                pick -> pick.fx >= correctStart && pick.fx <= correctEnd
        );

        updatePicksPlotFromList(picksListActual);
    }

    private void removePickIfNearMouseLocation(SVPoint2D mouseLocation) {
        Iterator<SVPoint2D> iterator = picksListActual.iterator();

        while (iterator.hasNext()) {
            SVPoint2D pick = iterator.next();
            if (isPickNearMouseLocation(pick, mouseLocation)) {
                printt("FOUND PICK TO REMOVE");
                printt(String.format("pick.fx: %f, mouseLocation.fx: %f", pick.fx, mouseLocation.fx));
                iterator.remove();
                updatePicksPlotFromList(picksListActual);
                break;
            }
        }
    }

    private void updateVelocityPicksCurve(float v, float t) {
        if (m_currentPicks != null && !m_currentPicks.isEmpty()) {

            float[] lm = gfxPanelCDP.getAxisLimits();
//            float[] lm2 = dvwnd.gfxPanelCDP.getAxisLimits();

            float ymin = lm[0];
            float ymax = lm[1];
//            float ymin2 = lm2[0];
//            float ymax2 = lm2[1];
//            float xmin2 = lm2[2];
//            float xmax2 = lm2[3];

            Vector<SVPoint2D> picksList = new Vector<>();
            SVPoint2D npick = null;

            for (int i = 0; i < m_currentPicks.size(); i++) {
                npick = new gfx.SVPoint2D();
                npick.fx = m_currentPicks.get(i).fx;
                npick.fy = m_currentPicks.get(i).fy;
                picksList.add(npick);
            }

            // if (m_cursorOverSemblancemap || m_cursorOverCVS) {
            npick = new gfx.SVPoint2D();
            npick.fx = v;
            npick.fy = t;
            picksList.add(npick);

            // Sort velocity picks, increasing time
            gfx.SVPoint2D[] pa = new gfx.SVPoint2D[picksList.size()];
            picksList.toArray(pa);

            int np = picksList.size();
            float[] x = new float[np + 2];
            float[] y = new float[np + 2];
            float[] x2 = new float[2];
            float[] y2 = new float[2];

            x[0] = pa[0].fx;
            y[0] = ymin;

            for (int i = 0; i < np; i++) {
                x[i + 1] = pa[i].fx;
                y[i + 1] = pa[i].fy;
            }

            x[np + 1] = x[np];
            y[np + 1] = ymax;

//            x2[0] = (m_curCDP);
//            x2[1] = (m_curCDP);
//            y2[0] = ymin2;
//            y2[1] = ymax2;
            printt("pa[0].fx: " + String.valueOf(pa[0].fx));
            printt("pa[0].fy: " + String.valueOf(pa[0].fx));
            printt("x: " + Arrays.toString(x));
            printt("y: " + Arrays.toString(y));
            picksGraphicalPlot.update(x, y);
            panelCDP.repaint();

        }
    }

    private static class SVPoint2DComparator implements java.util.Comparator<gfx.SVPoint2D> {

        private static SVPoint2DComparator instance;

        private SVPoint2DComparator() {
        }

        public static SVPoint2DComparator getInstance() {
            if (instance == null) {
                instance = new SVPoint2DComparator();
            }
            return instance;
        }

        @Override
        public int compare(SVPoint2D o1, SVPoint2D o2) {
            return Float.compare(o1.fx, o2.fx);
        }
    }

}
