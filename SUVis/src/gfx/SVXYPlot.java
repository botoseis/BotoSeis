/*
 * SVXYPlot.java
 *
 * Created on January 10, 2008, 12:41 PM
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
public class SVXYPlot {

    public SVXYPlot() {
        pointsList = new java.util.Vector<SVPoint2D>();
    }

    public int getNumberOfPoint() {
        return pointsList.size();
    }

    public void update(float[] x, float[] y) {
        pointsList.clear();
        SVPoint2D p;
        for (int i = 0; i < x.length; i++) {
            p = new SVPoint2D();
            p.fx = x[i];
            p.fy = y[i];

            pointsList.add(p);
        }

    }

    public void clear() {
        pointsList.clear();
    }

    public void setLineStyle(int s) {
        lineStyle = s;
    }

    public void setPointsVisible(boolean f) {
        showPoints = f;
    }

    public void setDrawSize(int s) {
        drawSize = s;
    }

    public void setDrawColor(java.awt.Color color) {
        drawColor = color;
    }

    public void setVisible(boolean f) {
        m_isVisible = f;
    }

    public boolean isVisible() {
        return m_isVisible;
    }

    public void draw(java.awt.Graphics g, int x, int y, int pWidth, int pHeight,
            float xmin, float xmax, float ymin, float ymax) {

        float fx;
        float fy;

        int cx;
        int cy;
        int xa = 0,  ya = 0;

        if (m_isVisible) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;

            java.awt.BasicStroke bs = new java.awt.BasicStroke(drawSize);

            g2.setColor(drawColor);
            g2.setStroke(bs);


            for (int i = 0; i < pointsList.size(); i++) {
                fx = pointsList.get(i).fx;
                fy = pointsList.get(i).fy;

                if ((fx >= xmin) && (fx <= xmax) && (fy >= ymin) && (fy <= ymax)) {
                    cx = (int) (((fx - xmin) * pWidth) / (xmax - xmin) + x);
                    cy = (int) (((fy - ymin) * pHeight) / (ymax - ymin) + y);

                    if (showPoints) {
                        //g2.fillRect(cx - 3, cy - 3, 6, 6);                        
                        g2.fillOval(cx - 3, cy - 3, 6, 6);
                    }

                    switch (lineStyle) {
                        case SOLID:
                            if (i > 0) {
                                g2.drawLine(xa, ya, cx, cy);
                            }
                            xa = cx;
                            ya = cy;
                            break;
                    }
                }
            }
        }
    }
    // Variables declaration
    java.util.Vector<SVPoint2D> pointsList;
    private boolean showPoints = true;
    private int lineStyle = SOLID;
    private int drawSize = 1;
    private java.awt.Color drawColor = java.awt.Color.black;
    boolean m_isVisible = true;
    // Constants
    public static final int INVISIBLE = 1;
    public static final int SOLID = 2;
}
