/*
 * SVWiggle.java
 *
 * Created on January 10, 2008, 12:41 PM
 *
 * Project: BotoSeis
 * 
 * Federal University of Para.
 * Department of Geophysics
 */
package gfx;

import java.awt.GraphicsConfiguration;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author Williams Lima
 */
public class SVWiggle extends SVActor {

    public SVWiggle() {
        
        m_xcur = 1.0f;
        xc = new float[NS * 2];
        zc = new float[NS * 2];
        m_style = SEISMIC;

        m_computeSamplingValues = true;
    }



    @Override
    public void paint(java.awt.Graphics g) {
        if (m_isVisible) {
            if (m_data != null) {
                drawWiggles(g);
            }
            m_imageOutOfDate = false;
        }
    }

    @Override
    public void setData(float pData[], int pN1, float pF1, float pD1,
            int pN2, float pF2, float pD2) {

        m_n1 = pN1;
        m_f1 = pF1;
        m_d1 = pD1;

        m_n2 = pN2;
        m_f2 = pF2;
        m_d2 = pD2;

        m_x1beg = m_f1;
        m_x1end = m_f1 + m_d1 * m_n1;

        m_x2beg = m_f2;
        m_x2end = m_f2 + m_d2 * m_n2;

        m_x1begb = m_x1beg;
        m_x1endb = m_x1end;
        m_x2begb = m_x2beg;
        m_x2endb = m_x2end;

        m_data = pData;

        m_imageOutOfDate = true;

    }

    @Override
    public void setStyle(int s) {
        if ((m_style != s) && (m_data != null)) {
            m_style = s;
        }

    }

    private void drawWiggles(java.awt.Graphics g) {
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;

        int nz = m_n1 * m_n2;

        float perc = 100.0f;
        float clip = 0;

        if (m_autoClip) {
            float temp[] = new float[nz];

            if (m_autoPerc) {
                perc = 100.0f;
            } else {
                perc = m_perc;
            }

            int iz;

            for (iz = 0; iz < nz; ++iz) {
                temp[iz] = Math.abs(m_data[iz]);
            }

            iz = (int) (nz * perc / 100.0);

            if (iz < 0) {
                iz = 0;
            }
            if (iz > nz - 1) {
                iz = nz - 1;
            }
            utils.Sorting.qkfind(iz, nz, temp);
            clip = temp[iz];
        }

        if ((m_width > 0) && (m_height > 0)) {

            int i1beg, i1end, i2, i, n2in;

            float[] atz = new float[m_data.length];

            for (i = 0; i < m_data.length; i++) {
                atz[i] = m_data[i];
            }

            if (m_data.length != 0) {
                float x2min, x2max;

                m_x2 = new float[m_n2];
                for (i = 0; i < m_n2; i++) {
                    m_x2[i] = m_f2 + i * m_d2;
                }

                x2min = x2max = m_x2[0];
                for (i2 = 1; i2 < m_n2; i2++) {
                    x2min = MIN(x2min, m_x2[i2]);
                    x2max = MAX(x2max, m_x2[i2]);
                }

                /* determine number of traces that fall within axis 2 bounds */
                x2min = MIN(m_x2begb, m_x2endb);
                x2max = MAX(m_x2begb, m_x2endb);
                for (i2 = 0, n2in = 0; i2 < m_n2; i2++) {
                    if ((m_x2[i2] >= x2min) && (m_x2[i2] <= x2max)) {
                        n2in++;
                    }
                }

                /* determine pads for wiggle excursion along axis 2 */
                m_xcur = 1.0f;
                m_xcur = Math.abs(m_xcur);
                if (n2in > 1) {
                    m_xcur *= (x2max - x2min) / (n2in - 1);
                }

                m_p2beg = 0;//(m_x2endb >= m_x2begb) ? -m_xcur : m_xcur;

                m_p2end = 0;//(m_x2endb >= m_x2begb) ? m_xcur : -m_xcur;

                //if (m_computeSamplingValues) {
                /* adjust x1beg and x1end to fall on sampled values */
                i1beg = NINT((m_x1begb - m_f1) / m_d1);
                i1beg = (int) MAX(0f, MIN(m_n1 - 1, i1beg));
                m_x1begb = m_f1 + i1beg * m_d1;
                i1end = NINT((m_x1endb - m_f1) / m_d1);
                i1end = (int) MAX(0f, MIN(m_n1 - 1, i1end));
                m_x1endb = m_f1 + i1end * m_d1;
                m_i1beg = i1beg;
                m_i1end = i1end;
                //}

                /* determine first sample and number of samples to plot */
                int if1p = (int) MIN(m_i1beg, m_i1end);
                int n1p = (int) MAX(m_i1beg, m_i1end) - if1p + 1;

                /* determine box size for first and second dimensions */
                int x1size = (m_style == NORMAL) ? m_width : m_height;
                int x2size = (m_style == NORMAL) ? m_height : m_width;

                /* determine scale and offset to map x2 units to plot units */
                float pscale = x2size / (m_x2endb + m_p2end - m_x2begb - m_p2beg);
                float poffset = -(m_x2begb + m_p2beg) * pscale;
                float pxcur = m_xcur * pscale;

                /* determine plot coordinates of first and last samples */
                int p1fz = (int) ((m_x1endb > m_x1begb) ? 0.0 : x1size);
                int p1lz = (int) ((m_x1endb > m_x1begb) ? x1size : 0.0);

                /* draw traces */
                int iz = 0;

                AffineTransform t = g2.getTransform();
                g2.translate(m_x, m_y);
                if (m_style == NORMAL) {
                    g2.rotate(-Math.PI / 2);
                    g2.translate(-m_height, 0);
                }

                for (i2 = 0; i2 < m_n2; i2++, iz += m_n1) {
                    // skip traces not in bounds
                    if (m_x2[i2] < x2min || m_x2[i2] > x2max) {
                        continue;
                    }
                    // determine x2 plot coordinate of trace
                    float px2 = poffset + m_x2[i2] * pscale;
                    // plot one trace
                    polyWiggle(g2, n1p, iz + if1p, atz, -clip, clip,
                            0.0f, px2,
                            px2 - pxcur, px2 + pxcur,
                            p1fz, p1lz, 1, "");
                }
                g2.setTransform(t);
            }

        }
    }

    private void polyWiggle(java.awt.Graphics2D g, int n, int iz, float[] z, float zmin, float zmax, float zbase,
            float x2, float yzmin, float yzmax, float xfirst, float xlast, int ifill,
            String tracecolor) /*************************************************************************
     * Williams: This code was adapted from psWiggle
     *
    polyWiggle - draw polygonal wiggle-trace with (optional) area-fill
     **************************************************************************
    Inputs:
    n		number of samples to draw
    z		array to draw
    zmin		z values below zmin will be clipped
    zmax		z values above zmax will be clipped
    zbase		z values between zbase and either zmin or zmax will be filled
    yzmin		y-coordinate corresponding to zmin
    yzmax		y-coordinate corresponding to zmax
    xfirst		x-coordinate corresponding to z[0]
    xlast		x-coordinate corresponding to z[n-1]
    fill		= 0 for no fill
    > 0 for fill between zbase and zmax
    < 0 for fill between zbase and zmin
    +2 for fill solid between zbase and zmax grey between zbase and zmin
    -2 for fill solid between zbase and zmin grey between zbase and zmax
    SHADING: 2<= abs(fill) <=5   abs(fill)=2 light grey  abs(fill)=5 black
    tracecolor	pointer to trace color, needed to restore from grey plotting
     **************************************************************************
    NOTES:
     * Williams:
     *     Please look at original psWiggle.c
     *     Original psWiggle credits:
     *        Author:  Dave Hale, Colorado School of Mines, 07/03/89
     ***************************************************************************/
    {
        int ic, nc, k1, k2, il;
        float shade;
        float xscale, xbias, zscale, zbias, xl, zl, zeps, dz;

        /* set up scale and bias factors for plot coordinates */
        xscale = (n > 1) ? (xlast - xfirst) / (n - 1) : 1.0f;
        xbias = xfirst;
        zscale = (zmax != zmin) ? (yzmax - yzmin) / (zmax - zmin) : 1.0f;
        zbias = (zmax != zmin) ? yzmin - zmin * zscale : 0.5f * (yzmin + yzmax);

        /* determine small z used to eliminate useless linetos */
        zeps = ZEPS * ((zmax > zmin) ? (zmax - zmin) : zmin - zmax);

        AffineTransform t = g.getTransform();

        g.translate(x2, 0);
        zbias = 0;

        /* draw array in segments of NS samples with 2 sample overlap */
        Path2D.Float path = new Path2D.Float();

        for (k1 = k2 = 0; k2 < (n - 1); k1 += NS - 2) {

            k2 = k1 + NS - 1;
            if (k2 >= n) {
                k2 = n - 1;
            }

            /* if filling */
            if (ifill != 0) {
                /* APPLY GREY SHADING if abs(ifill)>=2           */
                if (Math.abs(ifill) >= 2) {
                    /* clip trace values depending on sign of fill */
                    if (ifill < 0) {
                        nc = yclip(k2 - k1 + 1, 1.0f, (float) k1, k1 + iz, z,
                                zbase, zmax, xc, zc);
                    } else {
                        nc = yclip(k2 - k1 + 1, 1.0f, (float) k1, k1 + iz, z,
                                zmin, zbase, xc, zc);
                    }

                    /* set shading color to grey for opposite of fill */
                    /* ifill=2 light grey   ifill=5 black       */
                    shade = 1 - 0.2f * ((float) Math.abs(ifill));

                    if (shade < 0.0f) {
                        shade = 0.0f;
                    }

                    g.setColor(new java.awt.Color(shade, shade, shade));

                    /* make disconnected subpaths for each area to fill */
                    Path2D.Float fillPath = new Path2D.Float();

                    xl = xc[0];
                    zl = zbase;
                    for (ic = 0; ic < nc; ic++) {

                        /* if current z is not the base z */
                        if (zc[ic] != zbase) {

                            /* if last z was the base z, start subpath */
                            if (zl == zbase) {
                                fillPath.moveTo(zbias + zl * zscale, xbias + xl * xscale);
                            }

                            /* extend subpath to current z */
                            fillPath.lineTo(zbias + zc[ic] * zscale, xbias + xc[ic] * xscale);

                            /* else, if current z is the base z */
                        } else {
                            /* if last z was not the base z, end subpath */
                            if (zl != zbase) {
                                fillPath.lineTo(zbias + zc[ic] * zscale,
                                        xbias + xc[ic] * xscale);
                            }
                        }

                        /* remember last x and z */
                        xl = xc[ic];
                        zl = zc[ic];
                    }

                    /* if last z was not the base z, extend subpath to base z */
                    if (zl != zbase) {
                        fillPath.lineTo(zbias + zbase * zscale, xbias + xl * xscale);
                    }

                    /* fill the wiggle */
                    g.fill(path);


                    /* restore trace color  */
                    //setcolor(tracecolor);
                }  /*  endif GREY SHADING       */

                /* clip trace values depending on sign of fill */
                if (ifill > 0) {
                    nc = yclip(k2 - k1 + 1, 1.0f, (float) k1, k1 + iz, z, zbase,
                            zmax, xc, zc);
                } else {
                    nc = yclip(k2 - k1 + 1, 1.0f, (float) k1, k1 + iz, z, zmin,
                            zbase, xc, zc);
                }

                /* make disconnected subpaths for each area to fill */
                Path2D.Float fPath = new Path2D.Float();
                xl = xc[0];
                zl = zbase;

                for (ic = 0; ic < nc; ic++) {
                    /* if current z is not the base z */
                    if (zc[ic] != zbase) {

                        /* if last z was the base z, start subpath */
                        if (zl == zbase) {
                            g.fill(fPath);

                            fPath = new Path2D.Float();
                            fPath.moveTo(zbias + zl * zscale, xbias + xl * xscale);
                        }

                        /* extend subpath to current z */
                        fPath.lineTo(zbias + zc[ic] * zscale, xbias + xc[ic] * xscale);

                        /* else, if current z is the base z */
                    } else {
                        /* if last z was not the base z, end subpath */
                        if (zl != zbase) {
                            fPath.lineTo(zbias + zc[ic] * zscale,
                                    xbias + xc[ic] * xscale);
                        }
                    }

                    /* remember last x and z */
                    xl = xc[ic];
                    zl = zc[ic];
                }

                /* if last z was not the base z, extend subpath to base z */
                if (zl != zbase) {
                    fPath.lineTo(zbias + zbase * zscale, xbias + xl * xscale);
                }

                /* fill the wiggle */

                g.fill(fPath);


            }  /* end fill block  */

            /* clip trace values between zmin and zmax */
            nc = yclip(k2 - k1 + 1, 1.0f, (float) k1, k1 + iz, z, zmin, zmax, xc, zc);

            /* stroke trace values, avoiding linetos for nearly constant z */
            g.setColor(java.awt.Color.black);
            path.moveTo(zbias + zc[0] * zscale, xbias + xc[0] * xscale);
            il = 0;
            zl = zc[0];
            for (ic = 1; ic < nc; ic++) {
                dz = zc[ic] - zl;
                if (dz < -zeps || dz > zeps) {
                    if (il != ic - 1) {
                        path.lineTo(zbias + zc[ic - 1] * zscale, xbias + xc[ic - 1] * xscale);
                    }
                    path.lineTo(zbias + zc[ic] * zscale, xbias + xc[ic] * xscale);

                    il = ic;
                    zl = zc[ic];
                }
            }
            if (il != nc - 1) {
                path.lineTo(zbias + zc[nc - 1] * zscale, xbias + xc[nc - 1] * xscale);
            }
            g.draw(path);
        }

        /* restore graphics state */
        g.setTransform(t);
    }

    /* The following code was adapted from SU sources.
     * I the credits.
     */
    /* Copyright (c) Colorado School of Mines, 2008.*/
    /* All rights reserved.                       */
    /*********************** self documentation **********************/
    /*****************************************************************************
    YCLIP - Clip a function y(x) defined by linear interpolation of the
    uniformly sampled values:  y(fx), y(fx+dx), ..., y(fx+(nx-1)*dx).
    Returns the number of samples in the clipped function.
    yclip		clip a function y(x) defined by linear interplolation of
    uniformly sampled values
     ******************************************************************************
    Function Prototype:
    int yclip (int nx, float dx, float fx, float y[], float ymin, float ymax,
    float xc[], float yc[]);
     ******************************************************************************
    Input:
    nx		number of x (and y) values
    dx		x sampling interval
    fx		first x
    y		array[nx] of uniformly sampled y(x) values
    ymin		minimum y value; must not be greater than ymax
    ymax		maximum y value; must not be less than ymin
    Output:
    xc		array[?] of x values for clipped y(x)
    yc		array[?] of y values for clipped y(x)
    Returned:	number of samples in output arrays xc and yc
     ******************************************************************************
    Notes:
    The output arrays xc and yc should contain space 2*nx values, which
    is the maximum possible number (nc) of xc and yc returned.
     ******************************************************************************
    Author:  Dave Hale, Colorado School of Mines, 07/03/89
     *****************************************************************************/
    /**************** end self doc ********************************/
    private int yclip(int nx, float dx, float fx, int ybase, float y[], float ymin, float ymax,
            float xc[], float yc[]) /*****************************************************************************
    Clip a function y(x) defined by linear interpolation of the
    uniformly sampled values:  y(fx), y(fx+dx), ..., y(fx+(nx-1)*dx).
    Returns the number of samples in the clipped function.
     ******************************************************************************
    Input:
    nx		number of x (and y) values
    dx		x sampling interval
    fx		first x
    y		array[nx] of uniformly sampled y(x) values
    ymin		minimum y value; must not be greater than ymax
    ymax		maximum y value; must not be less than ymin
    Output:
    xc		array[?] of x values for clipped y(x)
    yc		array[?] of y values for clipped y(x)
    Returned:	number of samples in output arrays xc and yc
     ******************************************************************************
    Notes:
    The output arrays xc and yc should contain space 2*nx values, which
    is the maximum possible number (nc) of xc and yc returned.
     ******************************************************************************
    Author:  Dave Hale, Colorado School of Mines, 07/03/89
     *****************************************************************************/
    /*
     * Williams: The ybase was added to use with y[].
     */ {
        int ix, nc;
        float xix, yix, yixm1;

        xix = fx;
        yix = y[ybase + 0];
        nc = 0;
        xc[nc] = xix;
        if (yix < ymin) {
            yc[nc++] = ymin;
        } else if (yix > ymax) {
            yc[nc++] = ymax;
        } else {
            yc[nc++] = yix;
        }
        for (ix = 1; ix < nx; ix++) {
            yixm1 = yix;
            xix += dx;
            yix = y[ybase + ix];
            if (yixm1 < ymin) {
                if (yix >= ymin) {
                    xc[nc] = xix - dx * (yix - ymin) / (yix - yixm1);
                    yc[nc++] = ymin;
                    if (yix <= ymax) {
                        xc[nc] = xix;
                        yc[nc++] = yix;
                    } else {
                        xc[nc] = xix - dx * (yix - ymax) / (yix - yixm1);
                        yc[nc++] = ymax;
                    }
                }
            } else if (yixm1 > ymax) {
                if (yix <= ymax) {
                    xc[nc] = xix - dx * (yix - ymax) / (yix - yixm1);
                    yc[nc++] = ymax;
                    if (yix >= ymin) {
                        xc[nc] = xix;
                        yc[nc++] = yix;
                    } else {
                        xc[nc] = xix - dx * (yix - ymin) / (yix - yixm1);
                        yc[nc++] = ymin;
                    }
                }
            } else {
                if (yix < ymin) {
                    xc[nc] = xix - dx * (yix - ymin) / (yix - yixm1);
                    yc[nc++] = ymin;
                } else if (yix > ymax) {
                    xc[nc] = xix - dx * (yix - ymax) / (yix - yixm1);
                    yc[nc++] = ymax;
                } else {
                    xc[nc] = xix;
                    yc[nc++] = yix;
                }
            }
        }
        if (yix < ymin) {
            xc[nc] = xix;
            yc[nc++] = ymin;
        } else if (yix > ymax) {
            xc[nc] = xix;
            yc[nc++] = ymax;
        }
        return nc;
    }

    public float getwigbperc() {
        return m_perc;
    }

    public void setPercParameters(float perc) {//, float clip){
        m_autoPerc = false;
        m_autoClip = true;
        m_perc = perc;
        m_imageOutOfDate = true;
    }

    private int NINT(float x) {
        return (int) (x > 0.0 ? x + 0.5 : x - 0.5);
    }

    private float MAX(float x, float y) {
        return x > y ? x : y;
    }

    private float MIN(float x, float y) {
        return x < y ? x : y;
    }

    class ZoomParameters {

        public float x1b;
        public float x2b;
        public float y1b;
        public float y2b;
    }
    // Variables declaration
    BufferedImage m_img = null;
    GraphicsConfiguration m_gc = null;
    float[] m_x2;
    int m_i1beg;
    int m_i1end;
    float m_xcur;
    int m_interp;
    int m_wt;
    int m_va;
    float m_perc;
    float m_clip;
    boolean m_autoClip = true;
    boolean m_autoPerc = true;
    boolean m_outDated = true;
    boolean m_computeSamplingValues;
    //
       /* small number used to eliminate useless linetos */
    private final float ZEPS = 0.001f;

    /* length of segment to keep current path length under limit */
    private final int NS = 200;
    private float[] xc;
    private float[] zc;
}
