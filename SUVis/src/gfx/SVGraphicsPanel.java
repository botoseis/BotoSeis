/*
 * SVGraphicsPanel.java
 *
 * Created on January 10, 2008, 12:41 PM
 * 
 * Project: BotoSeis
 *
 * Federal University of Para.
 * Department of Geophysics
 */
package gfx;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerListener;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.event.AncestorListener;

/**
 *
 * @author Williams Lima
 */
public class SVGraphicsPanel extends javax.swing.JPanel {

    public SVGraphicsPanel() {
        super(new BorderLayout());
        panelH = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelV = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelCenter = new SVGraphicsPanelBar();
        this.add(panelCenter, BorderLayout.CENTER);
        this.add(panelH, BorderLayout.SOUTH);
        this.add(panelV, BorderLayout.EAST);

//        this.setViewportView(panelCenter);
//        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

    }

    public void addPanels(JScrollBar hBar, JScrollBar vBar) {
        getPanelH().add(hBar);
        getPanelV().add(vBar);
        ;
        validate();
    }

    public void addXYPlot(SVXYPlot plot) {
        panelCenter.addXYPlot(plot);
    }

    public void setAxisPanelX(AxisPanel ap){
        panelCenter.setAxisPanelX(ap);
    }
    public void setAxisPanelY(AxisPanel ap){
        panelCenter.setAxisPanelY(ap);
    }
    @Deprecated
    public void setAxisX(SVAxis axis) {
        panelCenter.setAxisX(axis);
    }
    @Deprecated
    public void setAxisY(SVAxis axis) {
        panelCenter.setAxisY(axis);
    }

    public void setPlotType(int t) {
        panelCenter.setPlotType(t);
    }

    public void addActor(SVActor a) {
        panelCenter.addActor(a);

    }

    public Vector<SVActor> getActors() {
        return panelCenter.getActors();
    }

    public void removeAllActors() {
        panelCenter.removeAllActors();
        clearPanels();
    }

    public void removeAllXYPlot() {
        panelCenter.removeAllXYPlot();
    }

    public void activateZoom(boolean flag) {
        panelCenter.activateZoom(flag);
    }

    public void activateSelection() {
        panelCenter.activateSelection();
    }

    public void setCrossLinesColor(java.awt.Color c) {
        panelCenter.setCrossLinesColor(c);
    }

    public void showCrossLines(boolean s) {
        panelCenter.showCrossLines(s);
    }

    public void setRubberBandColor(java.awt.Color c) {
        panelCenter.setRubberBandColor(c);
    }

    public SVPoint2D getMouseLocation() {
        return panelCenter.getMouseLocation();
    }

    public void setAxesLimits(float x1min, float x1max, float x2min, float x2max) {
        panelCenter.setAxesLimits(x1min, x1max, x2min, x2max);
    }

    public float[] getAxisLimits() {
        return panelCenter.getAxisLimits();
    }


    //override methods of events
    @Override
    public synchronized void addComponentListener(ComponentListener l) {
        panelCenter.addComponentListener(l);
    }

    @Override
    public synchronized void addMouseListener(MouseListener l) {
        panelCenter.addMouseListener(l);
    }

    @Override
    public synchronized void addMouseMotionListener(MouseMotionListener l) {
        panelCenter.addMouseMotionListener(l);
    }

    @Override
    public void addAncestorListener(AncestorListener listener) {
        panelCenter.addAncestorListener(listener);
    }

    @Override
    public synchronized void addContainerListener(ContainerListener l) {
        panelCenter.addContainerListener(l);
    }

    @Override
    public synchronized void addFocusListener(FocusListener l) {
        panelCenter.addFocusListener(l);
    }

    @Override
    public void addHierarchyBoundsListener(HierarchyBoundsListener l) {
        panelCenter.addHierarchyBoundsListener(l);
    }

    @Override
    public void addHierarchyListener(HierarchyListener l) {
        panelCenter.addHierarchyListener(l);
    }

    @Override
    public synchronized void addInputMethodListener(InputMethodListener l) {
        panelCenter.addInputMethodListener(l);
    }

    @Override
    public synchronized void addMouseWheelListener(MouseWheelListener l) {
        if (panelCenter != null) {
            panelCenter.addMouseWheelListener(l);
        }
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (panelCenter != null) {
            panelCenter.addPropertyChangeListener(listener);
        }

    }

    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (panelCenter != null) {
            panelCenter.addPropertyChangeListener(propertyName, listener);
        }
    }

    @Override
    public synchronized void addVetoableChangeListener(VetoableChangeListener listener) {
        panelCenter.addVetoableChangeListener(listener);
    }

 

    public void clearPanels() {
        getPanelH().removeAll();
        getPanelV().removeAll();
        getPanelH().setPreferredSize(new Dimension(this.getWidth(), 0));
        getPanelV().setPreferredSize(new Dimension(0, getHeight()));
        validate();
    }

    /**
     * @return the panelH
     */
    public JPanel getPanelH() {
        return panelH;
    }

    /**
     * @return the panelV
     */
    public JPanel getPanelV() {
        return panelV;
    }
    private JPanel panelH;
    private JPanel panelV;
    private boolean visibleScrollBar;
    SVGraphicsPanelBar panelCenter;
    public static int SIZE_SCROLLBAR = 15;

    /**
     * @return the visibleScrollBar
     */
    public boolean isVisibleScrollBar() {
        return visibleScrollBar;
    }

    /**
     * @param visibleScrollBar the visibleScrollBar to set
     */
    public void setVisibleScrollBar(boolean visibleScrollBar) {
        this.visibleScrollBar = visibleScrollBar;       
    }

}
