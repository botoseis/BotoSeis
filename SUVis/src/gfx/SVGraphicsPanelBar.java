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


import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.JScrollBar;

/**
 *
 * @author Williams Lima
 */
public  class SVGraphicsPanelBar extends javax.swing.JPanel
        implements java.awt.event.MouseMotionListener,
        java.awt.event.MouseListener,
        java.awt.event.ComponentListener {

    public SVGraphicsPanelBar() {
        m_actorsList = new java.util.Vector<SVActor>();

        m_imageOutOfDate = true;
        m_zoomActive = false;
        m_zooming = false;
        m_crossLinesColor = java.awt.Color.black;
        m_rubberColor = java.awt.Color.black;

        addMouseListener(this);
        addMouseMotionListener(this);
        addComponentListener(this);

        m_xyPlots = new java.util.Vector<SVXYPlot>();

        m_backgroundColor = java.awt.Color.white;
        m_gridStyle = GRID_NONE;

        m_showCrossLines = false;
        m_cursorCoord = new SVPoint2D();

        setAxesStyle(gfx.SVActor.SEISMIC);
        setGridColor(java.awt.Color.green);
        setGridStyle(SVGraphicsPanelBar.GRID_NONE);
        setBackground(java.awt.Color.white);

        m_plotType = NONE;

    }

    public void setAxisPanelX(AxisPanel ap) {
        this.m_axisPanelX = ap;
    }

    public void setAxisPanelY(AxisPanel ap) {
        this.m_axisPanelY = ap;
    }

    @Deprecated
    public void setAxisX(SVAxis axis) {
        m_axisX = axis;
    }

    @Deprecated
    public void setAxisY(SVAxis axis) {
        m_axisY = axis;
    }

    public SVAxis getAxisY() {
        if (m_axisPanelY != null) {
            return m_axisPanelY.getAxis();
        } else {
            return m_axisY;
        }
    }

    public void setPlotType(int t) {
        m_plotType = t;
    }

    public void addActor(SVActor a) {
        m_actorsList.add(a);
        a.setStyle(m_style);

        m_imageOutOfDate = true;
    }

    public Vector<SVActor> getActors() {
        return m_actorsList;
    }

    public void removeAllActors() {
        m_actorsList.clear();
    }

    public void addXYPlot(SVXYPlot plot) {
        m_xyPlots.add(plot);
    }

    public void removeAllXYPlot() {
        m_xyPlots.clear();
    }

    public void activateZoom(boolean flag) {
        m_zoomActive = flag;
    }

    public void activateSelection() {
        m_selectionActive = true;
    }

    public void setCrossLinesColor(java.awt.Color c) {
        m_crossLinesColor = c;
    }

    public void showCrossLines(boolean s) {
        m_showCrossLines = s;
        if (!s) {
            m_eraseOldCrossLines = false;
        }
    }

    public void setRubberBandColor(java.awt.Color c) {
        m_rubberColor = c;
    }

    public SVPoint2D getMouseLocation() {
        SVPoint2D p = new SVPoint2D();
        p.fx = m_cursorCoord.fx;
        p.fy = m_cursorCoord.fy;
        p.ix = m_cursorCoord.ix;
        p.iy = m_cursorCoord.iy;

        return p;
    }

    // Axes properties
    public void setAxesStyle(int s) {
        m_style = s;

        for (int i = 0; i < m_actorsList.size(); i++) {
            m_actorsList.get(i).setStyle(s);
        }
    }

    /**
     * 
     * @param x1min
     * @param x1max
     * @param x2min
     * @param x2max
     */
    public void setAxesLimits(float x1min, float x1max, float x2min, float x2max) {
        m_x1beg = m_x1begb = x1min;
        m_x1end = m_x1endb = x1max;

        m_x2beg = m_x2begb = x2min;
        m_x2end = m_x2endb = x2max;

        if (getAxisY() != null) {
            getAxisY().setLimits(x1min, x1max);
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "SVGraph");
        }
    }

    /**
     * 
     * @return ret [0] = x1beg, [1] = x1end, [2] = x2beg, [3] = x2end
     */
    public float[] getAxisLimits() {
        float[] ret = new float[4];

        ret[0] = m_x1begb;
        ret[1] = m_x1endb;
        ret[2] = m_x2begb;
        ret[3] = m_x2endb;

        return ret;
    }

    public float[] getValueAtCursor() {
        float[] ret = new float[3];

        ret[0] = m_cursorCoord.fx;
        ret[1] = m_cursorCoord.fy;

        ret[2] = m_actorsList.get(0).getDataAt(ret[1], ret[0]);

        return ret;
    }

    public void setGridStyle(int s) {
        m_gridStyle = s;
    }

    public void setGridColor(java.awt.Color c) {
        m_gridColor = c;
    }

    public void getSelection(SVPoint2D p1, SVPoint2D p2) {
        if ((m_rubberX - m_axesBox.x) < 0) {
            m_rubberX = m_axesBox.x;
        }
        if ((m_rubberX + m_rubberW) > (m_axesBox.x + m_axesBox.width)) {
            m_rubberW = m_axesBox.width - m_rubberX;
        }
        if ((m_rubberY - m_axesBox.y) < 0) {
            m_rubberY = m_axesBox.y;
        }
        if ((m_rubberY + m_rubberH) > (m_axesBox.y + m_axesBox.height)) {
            m_rubberH = m_axesBox.height - m_rubberY;
        }

        p1.ix = m_rubberX;
        p1.iy = m_rubberY;
        windowToWorld(p1);

        p2.ix = m_rubberX + m_rubberW;
        p2.iy = m_rubberY + m_rubberH;
        windowToWorld(p2);
    }

    @Override
    public void paint(java.awt.Graphics g) {
        int w = getWidth();
        int h = getHeight();

        m_axesBox.x = 0;
        m_axesBox.y = 0;
        m_axesBox.width = w;
        m_axesBox.height = h;

        int WMIN = 50;
        int HMIN = 50;

        g.setColor(getBackground());
        g.fillRect(0, 0, w, h);

        g.setColor(java.awt.Color.black);

        if ((w > WMIN) && (h > HMIN)) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;

            for (int i = 0; i < m_actorsList.size(); i++) {
                if (m_actorsList.get(i).isImageOutdated()) {
                    m_actorsList.get(i).setViewport(0, 0, w, h, m_x1begb, m_x1endb, m_x2begb, m_x2endb);
                }
                m_actorsList.get(i).paint(g);
            }

            for (int i = 0; i < m_xyPlots.size(); i++) {
                m_xyPlots.get(i).draw(g, m_axesBox.x, m_axesBox.y,
                        m_axesBox.width, m_axesBox.height,
                        m_x2begb, m_x2endb, m_x1begb, m_x1endb);
            }

            g2.setStroke(new java.awt.BasicStroke(1));
            g2.setColor(m_boundBoxColor);
            g2.drawRect(m_axesBox.x, m_axesBox.y, m_axesBox.width,
                    m_axesBox.height);

        }

    //m_imageOutOfDate = false;
    }

    // Private methods    
    public void mouseDragged(MouseEvent e) {
        m_cursorCoord.ix = e.getX();
        m_cursorCoord.iy = e.getY();
        windowToWorld(m_cursorCoord);
        if (m_zooming || m_onSelect) {
            java.awt.Graphics2D g = (java.awt.Graphics2D) getGraphics();
            g.setColor(m_rubberColor);
            g.setXORMode(java.awt.Color.white);

            // Erase old rubber box
            g.drawLine(m_rubberX, m_rubberY, m_rubberX + m_rubberW, m_rubberY);
            g.drawLine(m_rubberX + m_rubberW, m_rubberY, m_rubberX + m_rubberW,
                    m_rubberY + m_rubberH);
            g.drawLine(m_rubberX + m_rubberW, m_rubberY + m_rubberH, m_rubberX,
                    m_rubberY + m_rubberH);
            g.drawLine(m_rubberX, m_rubberY + m_rubberH, m_rubberX, m_rubberY);

            m_rubberW = e.getX() - m_rubberX;
            m_rubberH = e.getY() - m_rubberY;
            // Draw new rubber box                       
            g.drawLine(m_rubberX, m_rubberY, m_rubberX + m_rubberW, m_rubberY);
            g.drawLine(m_rubberX + m_rubberW, m_rubberY, m_rubberX + m_rubberW,
                    m_rubberY + m_rubberH);
            g.drawLine(m_rubberX + m_rubberW, m_rubberY + m_rubberH, m_rubberX,
                    m_rubberY + m_rubberH);
            g.drawLine(m_rubberX, m_rubberY + m_rubberH, m_rubberX, m_rubberY);

        }

    }

    public void mouseMoved(MouseEvent e) {
        int oldX = m_cursorCoord.ix;
        int oldY = m_cursorCoord.iy;

        m_cursorCoord.ix = e.getX();
        m_cursorCoord.iy = e.getY();
        windowToWorld(m_cursorCoord);
        if (m_showCrossLines) {
            java.awt.Graphics2D g = (java.awt.Graphics2D) getGraphics();
            g.setColor(m_crossLinesColor);
            g.setXORMode(java.awt.Color.white);
            if (m_eraseOldCrossLines) {
                // Erase old cross lines
                g.drawLine(0, oldY, getWidth(), oldY);
                g.drawLine(oldX, 0, oldX, getHeight());
            }
            // Draw new cross lines
            g.drawLine(0, e.getY(), getWidth(), e.getY());
            g.drawLine(e.getX(), 0, e.getX(), getHeight());
            m_eraseOldCrossLines = true;
        }
    }

    public void mouseClicked(MouseEvent e) {
       System.out.println("Limits: "+getAxisLimits()[0]+" "+getAxisLimits()[1]+" "+getAxisLimits()[2]+" "+getAxisLimits()[3]);
    }

    public void mousePressed(MouseEvent e) {
        if (m_zoomActive || m_selectionActive) {
            m_rubberX = e.getX();
            m_rubberY = e.getY();
            m_rubberW = 0;
            m_rubberH = 0;

            int oldX = m_cursorCoord.ix;
            int oldY = m_cursorCoord.iy;

            if (m_showCrossLines) {
                java.awt.Graphics2D g = (java.awt.Graphics2D) getGraphics();
                g.setColor(m_crossLinesColor);
                g.setXORMode(java.awt.Color.white);
                if (m_eraseOldCrossLines) {
                    // Erase old cross lines
                    g.drawLine(0, oldY, getWidth(), oldY);
                    g.drawLine(oldX, 0, oldX, getHeight());
                    m_eraseOldCrossLines = false;
                }
            }

            if (m_zoomActive) {
                m_zooming = true;
            }
            m_onSelect = m_selectionActive;
        }
    }

    public void mouseReleased(MouseEvent e) {
        m_selectionActive = false;
        m_onSelect = false;
        if (m_zooming) {

//            double factorX = 0;
//            double factorY = 0;
//            if( (m_cursorCoord.ix-m_rubberX) != 0 || (m_cursorCoord.iy-m_rubberY) != 0 ){
//             factorX = getWidth()/(m_cursorCoord.ix-m_rubberX);
//             factorY = getHeight()/(m_cursorCoord.iy-m_rubberY);
//             this.setPreferredSize(new Dimension((int)(getWidth()*factorX),(int) (getHeight()*factorY)));
//            }else{
//                this.setPreferredSize(new Dimension(this.getParent().getWidth(),this.getParent().getHeight()));
//            }                        

            if ((Math.abs(m_rubberW) < 4) || (Math.abs(m_rubberH) < 4)) {
                m_x1begb = m_x1beg;
                m_x1endb = m_x1end;
                m_x2begb = m_x2beg;
                m_x2endb = m_x2end;
            } else {
                // clip
                int p2X = m_cursorCoord.ix;
                int p2Y = m_cursorCoord.iy;

                int p1X = m_rubberX;
                int p1Y = m_rubberY;

                int rw = Math.abs(p2X - m_rubberX);
                int rh = Math.abs(p2Y - m_rubberY);

                int cx = p1X;
                int cy = p1Y;

                if (p2X < p1X) {
                    cx = p2X;
                }

                if (p2Y < p1Y) {
                    cy = p2Y;
                }
                // 
                if (m_style == SVActor.SEISMIC) {
                    SVPoint2D p = new SVPoint2D();
                    p.ix = cx;
                    p.iy = cy;
                    windowToWorld(p);
                    float x1beg = p.fy;
                    float x2beg = p.fx;
                    p.ix = cx + rw;
                    p.iy = cy + rh;
                    windowToWorld(p);
                    float x1end = p.fy;
                    float x2end = p.fx;
                    m_x1begb = x1beg;
                    m_x1endb = x1end;
                    m_x2begb = x2beg;
                    m_x2endb = x2end;

                } else {
                    SVPoint2D p = new SVPoint2D();
                    p.ix = cx;
                    p.iy = cy;
                    windowToWorld(p);
                    float x1beg = p.fx;
                    float x2end = p.fy;
                    p.ix = cx + rw;
                    p.iy = cy + rh;
                    windowToWorld(p);
                    float x1end = p.fx;
                    float x2beg = p.fy;
                    m_x1begb = x1beg;
                    m_x1endb = x1end;
                    m_x2begb = x2beg;
                    m_x2endb = x2end;
                }
            }
            final Float scala = 100000f;
            SVGraphicsPanel svg = ((SVGraphicsPanel) this.getParent());
            if (svg.isVisibleScrollBar()) {
                if ((m_cursorCoord.ix - m_rubberX) != 0 || (m_cursorCoord.iy - m_rubberY) != 0) {
                    m_axisPanelX.setHScrollBarActive(true);
                    m_axisPanelY.setVScrollBarActive(true);
                    svg.clearPanels();
                    svg.getPanelH().setPreferredSize(new Dimension(this.getWidth(), SVGraphicsPanel.SIZE_SCROLLBAR));
                    svg.getPanelV().setPreferredSize(new Dimension(SVGraphicsPanel.SIZE_SCROLLBAR, getHeight()));
                    int vMax = (int) ((m_x1begb *scala) + ( (m_x1end*scala) - (m_x1endb *scala))) +50 ;
                    int hMax = (int) ((m_x2begb *scala) + ((m_x2end*scala ) - (m_x2endb *scala))) -500 ;
                    int hVal = ((int) (m_x2begb*scala )) >= hMax ? hMax : (int) (m_x2begb *scala);                    
//                    System.out.println("vMax: "+vMax+" hMax: "+hMax);
//                    System.out.println("vMin: "+ (m_x1beg*scala )+" hMin: "+ m_x2beg*scala);
//                    System.out.println("vVal: "+ (m_x1begb*scala )+" hVal: "+hVal);
//                    System.out.println(m_x1beg+" "+m_x1end+" "+m_x2beg+" "+m_x2end+" " +
//                            "\n"+m_x1begb+" "+m_x1endb+" "+m_x2begb+" "+m_x2endb);
                    JScrollBar hBar = new JScrollBar(JScrollBar.HORIZONTAL,hVal , 5, (int) (m_x2beg*scala), hMax);
                    JScrollBar vBar = new JScrollBar(JScrollBar.VERTICAL, (int) (m_x1begb*scala ), 5,(int) (m_x1beg*scala), vMax);
                    hBar.addAdjustmentListener(new AdjustmentListener() {

                        @Override
                        public void adjustmentValueChanged(AdjustmentEvent e) {
                            float newVal = (e.getValue() /scala.floatValue()) - m_x2begb;
                            m_x2begb = m_x2begb + newVal ;
                            m_x2endb = m_x2endb + newVal ;
                            m_eraseOldCrossLines = true;
                            componentResized(null);
                            m_imageOutOfDate = true;
                            float[] v= m_axisPanelX.getAxis().getLimitsInitial();
                            float dv2 = ((v[1]-v[0]+1))/(m_x2end -m_x2beg);
                            float xmin = (((m_x2begb-m_x2beg)*dv2)+v[0]);
                            float xmax = (((m_x2endb-m_x2beg)*dv2)+v[0]);                           
                            m_axisPanelX.getAxis().setLimits(xmin, xmax);
                            m_axisPanelX.getParent().repaint();                            
                            repaint();
                        }
                    });
                    vBar.addAdjustmentListener(new AdjustmentListener() {

                        @Override
                        public void adjustmentValueChanged(AdjustmentEvent e) {
                            float newVal = (e.getValue()/scala.floatValue() ) - m_x1begb;
                            m_x1begb = m_x1begb + newVal;
                            m_x1endb = m_x1endb + newVal;
                            m_eraseOldCrossLines = true;
                            componentResized(null);
                            m_imageOutOfDate = true;
                            m_axisPanelY.getAxis().setLimits(m_x1begb, m_x1endb + 0.01f);
                            m_axisPanelY.getParent().repaint();
                            repaint();
                        }
                    });
                    hBar.setPreferredSize(new Dimension(svg.getWidth() - SVGraphicsPanel.SIZE_SCROLLBAR, svg.SIZE_SCROLLBAR));
                    vBar.setPreferredSize(new Dimension(SVGraphicsPanel.SIZE_SCROLLBAR, svg.getHeight() - svg.SIZE_SCROLLBAR));
                    svg.addPanels(hBar, vBar);
                } else {
                    m_axisPanelX.setHScrollBarActive(false);
                    m_axisPanelY.setVScrollBarActive(false);
                    m_axisPanelY.getAxis().setLimits(m_x1beg, m_x1end);
                    m_axisPanelX.getAxis().setLimits(m_x2beg, m_x2end);
                    m_axisPanelX.getParent().repaint();
                    svg.clearPanels();
                }
            }
            m_eraseOldCrossLines = true;
            m_zooming = false;
            componentResized(null);
        }
        m_imageOutOfDate = true;
        repaint();

    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void componentResized(ComponentEvent arg0) {
        int w = getWidth();
        int h = getHeight();
        for (int i = 0; i < m_actorsList.size(); i++) {
            m_actorsList.get(i).setViewport(0, 0, w, h, m_x1begb, m_x1endb, m_x2begb, m_x2endb);
        }
        m_imageOutOfDate = true;
        SVGraphicsPanel svg = ((SVGraphicsPanel) this.getParent());
        if (svg.getPanelH().getComponents().length > 0) {
            JScrollBar hBar = (JScrollBar) svg.getPanelH().getComponent(0);
            JScrollBar vBar = (JScrollBar) svg.getPanelV().getComponent(0);
            hBar.setPreferredSize(new Dimension(svg.getWidth() - SVGraphicsPanel.SIZE_SCROLLBAR, svg.SIZE_SCROLLBAR));
            vBar.setPreferredSize(new Dimension(SVGraphicsPanel.SIZE_SCROLLBAR, svg.getHeight() - svg.SIZE_SCROLLBAR));
            svg.getPanelH().updateUI();
            svg.getPanelV().updateUI();
        }
        repaint();

    }

    public void componentMoved(ComponentEvent arg0) {
    }

    public void componentShown(ComponentEvent arg0) {
    }

    public void componentHidden(ComponentEvent arg0) {
    }

    private boolean windowToWorld(SVPoint2D p) {


        float xmin, xmax;

        if (m_axesBox != null) {
            xmin = m_x2begb;
            xmax = m_x2endb;

            if (m_style == SVActor.NORMAL) {
                xmin = m_x1begb;
                xmax = m_x1endb;
            }

            int h = m_axesBox.width;

            int x, y;
            x = m_axesBox.x;
            y = m_axesBox.y;

            p.fx = xmin + (xmax - xmin) * (p.ix - x) / h;

            if ((p.fx < xmin) || (p.fx > xmax)) {
                return false;
            }

            xmin = m_x1begb;
            xmax = m_x1endb;

            if (m_style == SVActor.NORMAL) {
                xmin = m_x2begb;
                xmax = m_x2endb;
            }

            h = m_axesBox.height;

            x = m_axesBox.x;
            y = m_axesBox.y;

            if (getAxisY().getStyle() == SVAxis.AXIS_NORMAL) {
                p.fy = xmin + (xmax - xmin) * (h - (p.iy - y)) / h;
            } else {
                p.fy = xmin + (xmax - xmin) * (p.iy - y) / h;
            }

            if ((p.fy < xmin) || (p.fy > xmax)) {
                return false;
            }
        }
        return true;

    }    // Variable declarations
    java.util.Vector<SVXYPlot> m_xyPlots;
    java.util.Vector<SVActor> m_actorsList;
    gfx.SVColorScale m_colorScale = null;
    private java.awt.Color m_backgroundColor;
    private boolean m_showCrossLines;
    private boolean m_eraseOldCrossLines;
    private SVPoint2D m_cursorCoord;
    // Grid properties    
    private int m_gridStyle;
    private java.awt.Color m_gridColor;
    public static final int GRID_NONE = 0;
    public static final int GRID_SOLID = 1;
    public static final int GRID_DOT = 2;
    public static final int GRID_DASH = 3;
    // Axis properties
    java.awt.Rectangle m_axesBox = new java.awt.Rectangle();
    // Axes limits
    float m_x1beg = 0.0f;
    float m_x1end = 1.0f;
    float m_x1begb = 0.0f;
    float m_x1endb = 1.0f;
    float m_p1beg = 0.0f;
    float m_p1end = 0.0f;
    int m_n1tic = 0;
    double m_d1tic = 0.0;
    double m_f1num = 0.0;
    //
    float m_x2beg = 0.0f;
    float m_x2end = 1.0f;
    float m_x2begb = 0.0f;
    float m_x2endb = 1.0f;
    float m_p2beg = 0.0f;
    float m_p2end = 0.0f;
    int m_n2tic = 0;
    double m_d2tic = 0.0;
    double m_f2num = 0.0;
    private boolean m_imageOutOfDate;
    private int m_style;
    private java.awt.Color m_crossLinesColor;
    // Zoom
    private boolean m_zoomActive;
    private boolean m_zooming;
    private boolean m_selectionActive;
    private boolean m_onSelect;
    private java.awt.Color m_rubberColor;
    private int m_rubberX;
    private int m_rubberY;
    private int m_rubberW;
    private int m_rubberH;
    // Legend
    private short[] m_dataLegend;
    private int m_lx;
    private int m_ly;
    private int m_lwidth;
    private int m_lheight;
    //
    java.awt.Color m_boundBoxColor = java.awt.Color.black;
    //
    private int m_plotType;
    gfx.SVAxis m_axisX = null;
    gfx.SVAxis m_axisY = null;
    gfx.AxisPanel m_axisPanelX = null;
    gfx.AxisPanel m_axisPanelY = null;
    // Constants    
    private final int START_ZOOM = 1;
    private final int END_ZOOM = 2;
    double zoom;
    //
    public static final int NONE = 0;
    public static final int WIGGLE = 1;
    public static final int COLORSCALE = 2;
    public static final int CONTOUR = 3;
    public static final int WIGGLE_AND_COLORSCALE = 4;
    public static final int CONTOUR_AND_COLORSCALE = 5;
    public static final int CURVE = 6;
}
