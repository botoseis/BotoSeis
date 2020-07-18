/*
 * AxisPanel.java
 *
 * Created on April 28, 2008, 1:50 PM
 * 
 * Project: BotoSeis
 * 
 * Federal University of Para.
 * Department of Geophysics.
 */
package gfx;



/**
 *
 * @author Williams Lima
 */
public class AxisPanel extends javax.swing.JPanel {

    public AxisPanel(gfx.SVAxis axis) {
        m_axis = axis;
        hScrollBarActive = false;
        vScrollBarActive = false;
    }

    @Override
    public void paint(java.awt.Graphics g) {
        try {
            int w = isHScrollBarActive() ?  getWidth() - SVGraphicsPanel.SIZE_SCROLLBAR : getWidth();
            int h = isVScrollBarActive() ? getHeight() - SVGraphicsPanel.SIZE_SCROLLBAR : getHeight() ;

            int axisX = 0;
            int axisY = 0;
            int axisW = 0;
            int axisH = 0;

            switch (m_axis.getAxisSide()) {
                case gfx.SVAxis.AXIS_LEFT:
                    axisX = w;
                    axisY = 0;
                    axisW = w;
                    axisH = h;
                    break;
                case gfx.SVAxis.AXIS_TOP:
                    axisX = 0;
                    axisY = h;
                    axisW = w;
                    axisH = h;
                    break;
                case gfx.SVAxis.AXIS_RIGHT:
                    axisX = 0;
                    axisY = 0;
                    axisW = w;
                    axisH = h;
                    break;
                case gfx.SVAxis.AXIS_BOTTOM:
                    axisX = 0;
                    axisY = 0;
                    axisW = w;
                    axisH = h;
                    break;
            }

            m_axis.draw((java.awt.Graphics2D) g, axisX, axisY, axisW, axisH);

        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, e);
        }
    }

    public gfx.SVAxis getAxis() {
        return m_axis;
    }
    // Variables declaration
    gfx.SVAxis m_axis = null;
    private boolean hScrollBarActive;
    private boolean vScrollBarActive;

    /**
     * @return the scrollBarActive
     */
    public boolean isHScrollBarActive() {
        return hScrollBarActive;
    }

    /**
     * @param scrollBarActive the scrollBarActive to set
     */
    public void setHScrollBarActive(boolean scrollBarActive) {
        this.hScrollBarActive = scrollBarActive;
    }

    /**
     * @return the vScrollBarActive
     */
    public boolean isVScrollBarActive() {
        return vScrollBarActive;
    }

    /**
     * @param vScrollBarActive the vScrollBarActive to set
     */
    public void setVScrollBarActive(boolean vScrollBarActive) {
        this.vScrollBarActive = vScrollBarActive;
    }
   
}
