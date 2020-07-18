/*
 * GfxPanelColorbar.java
 *
 * Created on April 28, 2008, 1:50 PM
 *  
 * Project: BotoSeis
 * 
 * Federal University of Para.
 * Department of Geophysics
 */
package gfx;

/**
 *
 * @author Williams Lima
 */
public class GfxPanelColorbar extends javax.swing.JPanel {

    public GfxPanelColorbar(gfx.SVColorScale cs, int orientation) {
        m_csmap = cs;
        m_orientation = orientation;
    }

    @Override
    public void paint(java.awt.Graphics g) {
        int w = getWidth();
        int h = getHeight();

        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;

        if (m_orientation == VERTICAL) {
            g2.drawImage(m_csmap.getColorbar(h, w), null, this);
        } else {
            g2.rotate(-3 * Math.PI / 2);
            g2.translate(0, -w);
            g2.drawImage(m_csmap.getColorbar(h, w), null, this);
        }

    }

    // Variables declaration
    gfx.SVColorScale m_csmap;
    // Constants
    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;
    int m_orientation = VERTICAL;
}
