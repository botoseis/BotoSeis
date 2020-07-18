/*
 * SVAxis.java
 * 
 * Project: BotoSeis
 *
 * Federal University of Para, Brazil.
 * Department of Geophysics
 */
package gfx;

/**
 * The SVAxis class represents an axis.
 * Its an adaptation of some functions related to axes drawing
 * found in some SU graphics programs.
 * 
 * @author Williams Lima
 */
public class SVAxis {

    public SVAxis(int orientation, int side, String title) {
        m_orientation = orientation;
        m_axisSide = side;
        m_title = title;
        m_titleFont = new java.awt.Font("Courier", java.awt.Font.PLAIN, 12);
        m_labelFont = new java.awt.Font("Courier", java.awt.Font.PLAIN, 12);
    }

    public void draw(java.awt.Graphics2D g, int x, int y, int width, int height) {
        switch (m_orientation) {
            case HORIZONTAL:
                drawHorizontalAxis(g, x, y, width, height);
                break;
            case VERTICAL:
                drawVerticalAxis(g, x, y, width, height);
                break;
        }
    }

    public void setAxisColor(java.awt.Color c) {
    }

    public void setLabelColor(java.awt.Color c) {
    }

    public void setTitleColor(java.awt.Color c) {
    }

    public void setOrientation(int h) {
        m_orientation = h;
    }

    public int getOrientation() {
        return m_orientation;
    }

    public int getAxisSide() {
        return m_axisSide;
    }

    public void setStyle(int s) {
        m_style = s;
    }

    public int getStyle() {
        return m_style;
    }

    public void setLimits(float xmin, float xmax) {
        m_xmin = xmin;
        m_xmax = xmax;
    }

    public float[] getLimits() {
        float v[] = {m_xmin, m_xmax};
        return v;
    }

    public float[] getLimitsInitial() {
        if (m_xmaxInitial != 0) {
            float v[] = {m_xminInitial, m_xmaxInitial};
            return v;
        } else {
            return getLimits();
        }
    }

    public void setLimitsInitial(float xmin, float xmax) {
        m_xmaxInitial = xmax;
        m_xminInitial = xmin;
    }

    public void setVisible(boolean f) {
    }

    public void setTitle(String title) {
        this.m_title = title;
    }

    private void drawHorizontalAxis(java.awt.Graphics g, int pX, int pY, int pWidth, int pHeight) {
        float xbeg = m_xmin;
        float xend = m_xmax;
        float pbeg = m_pbeg;
        float pend = m_pend;

        double amin = (xbeg < xend) ? xbeg : xend;
        double amax = (xbeg > xend) ? xbeg : xend;

        amin += pbeg;
        amax += pend;

        //if (m_dxtic == 0.0) {
        AuxSCAxis sc = new AuxSCAxis();
        sc.dxnum = 1.0;
        sc.fxnum = 0.0;
        sc.nxnum = pWidth / (8 * getLabelsCW(g));

        scaxis(amin, amax, sc);

        double dtic = m_dtic;
        double ntic = m_ntic;
        double fnum = m_fnum;

        dtic = sc.dxnum;
        ntic = sc.nxnum;
        fnum = sc.fxnum;
        //}        

        double scale = pWidth / (amax - amin);
        double base = pX - scale * (amin);

        int tw;
        int xa;
        int ya = pY;
        int yt = pY;

        /* determine tic size */
        int ticsize = AXIS_TICSIZE;

        int labelca = ticsize;
        int labelch = getLabelsCH(g);

        int ticb = 0;
        int numb = 0;
        int labelb = 0;

        switch (m_axisSide) {
            case AXIS_TOP:
                ticb = -ticsize;
                numb = ticb - AXIS_TICSIZE / 2;
                labelb = numb;
                ya = pY;
                yt = ya + labelb - labelch - AXIS_TICSIZE / 2;
                break;
            case AXIS_BOTTOM:
                ticb = ticsize;
                numb = ticb;
                labelb = numb + labelch;
                ya = pY + pHeight;
                yt = ya + labelb + labelch;
                break;
        }

        boolean grided = false;

        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;

        double azero = 0.0001 * (amax - amin);
        String str;
        int lstr;

        double dnum = dtic;

        for (double anum = fnum; anum <= amax; anum += dnum) {
            if (anum < amin) {
                continue;
            }

            xa = (int) (base + scale * anum);

            g2.setColor(m_axisColor);
            g2.drawLine(xa, ya, xa, ya + ticb);
            if (anum > -azero && anum < azero) {
                str = String.format("%1.0f", 0.0);
            } else {
                str = String.format("%1.0f", anum);
            }
            lstr = str.length();
            tw = lstr * getLabelsCW(g);
            g2.setColor(m_labelColor);
            g2.drawString(str, xa - tw / 2, ya + labelb);
        }

        g2.setStroke(new java.awt.BasicStroke(1));

        g2.setColor(m_axisColor);
        double dtic2 = dnum / ntic;
        for (double atic = fnum - ntic * dtic2 - dtic2; atic <= amax; atic += dtic2) {
            if (atic < amin) {
                continue;
            }
            xa = (int) (base + scale * atic);
            g2.drawLine(xa, ya, xa, ya + ticb / 2);
        }

        g2.setColor(m_titleColor);
        lstr = m_title.length();

        tw = lstr * getAxisTitleCW(g);
        g2.drawString(m_title, pX + pWidth / 2 - tw / 2, yt);

    }

    private void drawVerticalAxis(java.awt.Graphics g, int pX, int pY, int pWidth, int pHeight) {
        float xbeg = m_xmin;
        float xend = m_xmax;
        float pbeg = m_pbeg;
        float pend = m_pend;

        double amin = (xbeg < xend) ? xbeg : xend;
        double amax = (xbeg > xend) ? xbeg : xend;

        //if (m_dytic == 0.0) {
        AuxSCAxis sc = new AuxSCAxis();
        sc.dxnum = 1.0f;
        sc.fxnum = 0.0f;
        sc.nxnum = pHeight / (8 * getLabelsCW(g));

        scaxis(xbeg, xend, sc);

        double dtic = sc.dxnum;
        int ntic = sc.nxnum;
        double fnum = sc.fxnum;
        //}
        double dnum = dtic;

        double scale = pHeight / (xend + pend - xbeg - pbeg);
        double base = pY - scale * (xbeg + pbeg);

        int tw;
        int xa = pX;
        int ya = pY;

        /* determine tic size */
        int ticsize = AXIS_TICSIZE;

        int labelca = ticsize;
        int labelch = getLabelsCH(g);

        int ticb = -ticsize;
        int numb = ticb - ticsize / 4;
        int labelb = 0;

        if (m_style == AXIS_NORMAL) {
            scale = -pHeight / (xend + pend - xbeg - pbeg);
            base = pY + pHeight - scale * (xbeg + pbeg);
        }

        boolean grided = false;

        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;

        double azero = 0.0001 * (amax - amin);
        String str;
        int lstr;

        for (double anum = fnum; anum <= amax; anum += dnum) {
            if (anum < amin) {
                continue;
            }
            ya = (int) (base + scale * anum);

            g2.setColor(m_axisColor);
            g2.drawLine(xa, ya, xa + ticb, ya);
            if (anum > -azero && anum < azero) {
                str = String.format("%1.2f", 0.0);
            } else {
                str = String.format("%1.2f", anum);
            }
            lstr = str.length();
            tw = lstr * getLabelsCW(g);
            g2.setColor(m_labelColor);
            g2.drawString(str, xa + numb - tw, ya + labelca / 4);
        }

        g2.setStroke(new java.awt.BasicStroke(1));
        g2.setColor(m_axisColor);

        double dtic2 = dnum / ntic;
        for (double atic = fnum - ntic * dtic2 - dtic2; atic <= amax; atic += dtic2) {
            if (atic < amin) {
                continue;
            }
            ya = (int) (base + scale * atic);
            g2.drawLine(xa, ya, xa + ticb / 2, ya);
        }

        g2.setColor(m_titleColor);

        lstr = m_title.length();

        tw = lstr * getAxisTitleCW(g);

        java.awt.geom.AffineTransform t = g2.getTransform();

        g2.translate(pX - getLabelsCW(g) * 5 + numb, pY + pHeight / 2 + tw / 2);
        g2.rotate(-Math.PI / 2.0);
        g2.drawString(m_title, 0, 0);

        g2.setTransform(t);

    }

    private int getLabelsCW(java.awt.Graphics g) {
        java.awt.font.FontRenderContext frc = ((java.awt.Graphics2D) g).getFontRenderContext();
        java.awt.geom.Rectangle2D br = m_labelFont.getStringBounds("0", frc);
        return (int) br.getWidth();
    }

    private int getLabelsCH(java.awt.Graphics g) {
        java.awt.font.FontRenderContext frc = ((java.awt.Graphics2D) g).getFontRenderContext();
        java.awt.geom.Rectangle2D br = m_labelFont.getStringBounds("0", frc);
        return (int) br.getHeight();
    }

    private int getAxisTitleCH(java.awt.Graphics g) {
        java.awt.font.FontRenderContext frc = ((java.awt.Graphics2D) g).getFontRenderContext();
        java.awt.geom.Rectangle2D br = m_titleFont.getStringBounds("A", frc);
        return (int) br.getHeight();
    }

    private int getAxisTitleCW(java.awt.Graphics g) {
        java.awt.font.FontRenderContext frc = ((java.awt.Graphics2D) g).getFontRenderContext();
        java.awt.geom.Rectangle2D br = m_titleFont.getStringBounds("A", frc);
        return (int) br.getWidth();
    }

    // scaxis
    // Adapted from SU source codes
    // CREDITS
    //    CWP/SU
    @SuppressWarnings("empty-statement")
    private void scaxis(double x1, double x2, AuxSCAxis aux) {
        int n, i, iloga;
        double d, f, eps, a, b, xmin, xmax;
        double rdint[] = new double[4];

        /* Set readable intervals */
        rdint[0] = 1.0;
        rdint[1] = 2.0;
        rdint[2] = 5.0;
        rdint[3] = 10.0;

        /* Handle x1 == x2 as a special case */
        if (x1 == x2) {
            aux.nxnum = 1;
            aux.dxnum = 1.0f;
            aux.fxnum = x1;

            return;
        }

        /* determine minimum and maximum */
        xmin = (x1 < x2) ? x1 : x2;
        xmax = (x1 > x2) ? x1 : x2;

        /* get desired number of numbered values */
        n = aux.nxnum;
        n = (2 > n) ? 2 : n;

        /* determine output parameters, adjusted for roundoff */
        a = (xmax - xmin) / (double) (n - 1);
        iloga = (int) Math.log10((double) a);
        if (a < 1.0) {
            iloga = iloga - 1;
        }

        b = a / Math.pow(10.0, (double) iloga);
        for (i = 0; i < 3 && b >= Math.sqrt(rdint[i] * rdint[i + 1]); i++) {
            ;
        }

        d = rdint[i] * Math.pow(10.0, (double) iloga);
        f = ((int) (xmin / d)) * d - d;
        eps = 0.0001 * (xmax - xmin);

        while (f < (xmin - eps)) {
            f += d;
        }

        n = 1 + (int) ((xmax + eps - f) / d);

        /* set output parameters before returning */
        aux.nxnum = n;
        aux.dxnum = d;
        aux.fxnum = f;
    }

    // Auxiliary class to hold scaxis return values
    class AuxSCAxis {

        public int nxnum;
        public double dxnum;
        public double fxnum;
    }
    // Variables declaration
    int m_orientation;  // HORIZONTAL or VERTICAL
    int m_axisSide;     // TOP or BOTTOM, LEFT or RIGHT
    int m_style = AXIS_REVERSED;        // NORMAL or REVERSED
    float m_xmin;
    float m_xmax;
    private float m_xminInitial;
    private float m_xmaxInitial;
    int m_pbeg;
    int m_pend;
    double m_dtic;
    double m_ntic;
    double m_fnum;
    java.awt.Color m_axisColor = java.awt.Color.black;
    java.awt.Color m_labelColor = java.awt.Color.black;
    java.awt.Color m_titleColor = java.awt.Color.black;
    private java.awt.Font m_titleFont;
    private java.awt.Font m_labelFont;
    String m_title = "";
    // Constants
    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;
    public static final int AXIS_LEFT = 2;
    public static final int AXIS_RIGHT = 3;
    public static final int AXIS_TOP = 4;
    public static final int AXIS_BOTTOM = 5;
    public static final int AXIS_NORMAL = 6;
    public static final int AXIS_REVERSED = 7;
    private final int AXIS_TICSIZE = 5;
}
