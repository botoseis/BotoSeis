/*
 * ContourMap.java
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
 * The class SVContourmao is a port of the code found in
 * the SU program suxcontour.
 * 
 * @author Williams Lima
 */
public class SVContourmap extends SVActor {

    /** Creates a new instance of ContourMap */
    public SVContourmap() {
        m_imageOutOfDate = true;
    }

    @Override
    public void paint(java.awt.Graphics g) {
        if (m_isVisible) {
            int i1, i2;
            float yscale, xscale;

            if (m_imageOutOfDate) {
                convertDataToImage();
                m_imageOutOfDate = false;
            }

            float ixX[] = new float[m_nx];
            float iyX[] = new float[m_ny];

            int x = m_x;
            int y = m_y;
            int width = m_width;
            int height = m_height;

            /* map x1 and x2 units to bitmap units */
            xscale = (width - 1) / (m_ix[m_ixb + m_nxb - 1] - m_ix[m_ixb]);
            yscale = (height - 1) / (m_iy[m_iyb + m_nyb - 1] - m_iy[m_iyb]);
            for (i1 = 0; i1 < m_nxb; i1++) {
                ixX[i1] = (m_ix[m_ixb + i1] - m_ix[m_ixb]) * xscale;
            }
            for (i2 = 0; i2 < m_nyb; i2++) {
                iyX[i2] = (m_iy[m_iyb + i2] - m_iy[m_iyb]) * yscale;
            }

            newContour(g, x, y, m_nxb, ixX, m_nyb, iyX, m_contourzb,
                    m_nc, m_c, m_cwidth, m_ccolor, m_cdash,
                    m_labelcf, m_labelcper, m_nlabelc, "fixed", "white");
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


    }

    private void convertDataToImage() {
        int i1, i2, iz, ic, i1beg, i2beg, i1end, i2end,
                i1min, i2min, i1max, i2max, i1step, i2step,
                i1c, i2c, n2c, n1c;

        float[] z = m_data;

        float x1[] = new float[m_n1];
        for (i1 = 0; i1 < m_n1; i1++) {
            x1[i1] = m_f1 + i1 * m_d1;
        }

        float[] x2 = new float[m_n2];
        for (i1 = 0; i1 < m_n2; i1++) {
            x2[i1] = m_f2 + i1 * m_d2;
        }

        float zmin, zmax;
        zmin = zmax = z[0];
        for (i2 = 0; i2 < m_n2; i2++) {
            iz=i2*m_n1;
            for (i1 = 0; i1 < m_n1; i1++, iz++) {
                zmin = MIN(zmin, z[iz]);
                zmax = MAX(zmax, z[iz]);
            }
        }

        float x1min, x1max;
        x1min = (m_d1 > 0.0) ? m_f1 : m_f1 + (m_n1 - 1) * m_d1;
        x1max = (m_d1 < 0.0) ? m_f1 : m_f1 + (m_n1 - 1) * m_d1;

        float x2min, x2max;
        x2min = (m_d2 > 0.0) ? m_f2 : m_f2 + (m_n2 - 1) * m_d2;
        x2max = (m_d2 < 0.0) ? m_f2 : m_f2 + (m_n2 - 1) * m_d2;

        x2min = x2max = x2[0];
        for (iz = 1; iz < m_n2; iz++) {
            x2min = MIN(x2min, x2[iz]);
            x2max = MAX(x2max, x2[iz]);
        }

        /* set contouring parameters */
        m_nc = 5;
        m_dc = (zmax - zmin) / (m_nc);
        m_fc = zmin + m_dc;
        for (ic = 0; ic < m_nc; ic++) {
            m_c[ic] = m_fc + ic * m_dc;
        }

        m_ccolor = new String[m_nc];
        for (ic = 0; ic < m_nc; ic++) {
            m_cwidth[ic] = 1.0f;
            m_cgray[ic] = 0.0f;
            m_cdash[ic] = 0.0f;
            m_ccolor[ic] = "none";
        }

        m_labelcf = 1;
        m_labelcper = 1;
        m_nlabelc = m_nc;
        m_labelcsize = 6;

        /* adjust x1beg and x1end to fall on sampled values */
        i1max = 0;
        i1min = m_n1;

        n1c = 0;
        for (i1 = 0; i1 < m_n1; i1++) {
            if (x1[i1] >= MIN(m_x1begb, m_x1endb) && x1[i1] <= MAX(m_x1begb,
                    m_x1endb)) {
                i1min = (int) MIN(i1, i1min);
                i1max = (int) MAX(i1, i1max);
                n1c++;
            }
        }
        if (m_x1begb <= m_x1endb) {
            i1beg = i1min;
            i1end = i1max;
        } else {
            i1beg = i1max;
            i1end = i1min;
        }

        m_x1begb = x1[i1beg];
        m_x1endb = x1[i1end];

        /* adjust x2beg and x2end to fall on sampled values */
        i2max = 0;
        i2min = m_n2;

        n2c = 0;
        for (i2 = 0; i2 < m_n2; i2++) {
            if (x2[i2] >= MIN(m_x2begb, m_x2endb) && x2[i2] <= MAX(m_x2begb,
                    m_x2endb)) {
                i2min = (int) MIN(i2, i2min);
                i2max = (int) MAX(i2, i2max);
                n2c++;
            }
        }
        if (m_x2begb <= m_x2endb) {
            i2beg = i2min;
            i2end = i2max;
        } else {
            i2beg = i2max;
            i2end = i2min;
        }

        m_contourz = new float[n1c * n2c];

        i1step = (i1end > i1beg) ? 1 : -1;
        i2step = (i2end > i2beg) ? 1 : -1;

        int contourzp;
        if (m_style == NORMAL) {
            i2 = i2beg;
            for (i2c = 0; i2c < n2c ; i2c++, i2 += i2step) {
                contourzp = n1c * n2c - (i2c + 1) * n1c;
                i1 = i1beg;
                for (i1c = 0; i1c < n1c; i1c++, i1 += i1step) {
                    m_contourz[contourzp++] = z[i1 + i2 * m_n1];
                }
            }
        } else {
            contourzp = 0;
            i1 = i1beg;
            for (i1c = 0; i1c < n1c; i1c++, i1 += i1step) {
                i2 = i2beg;
                for (i2c = 0; i2c <n2c; i2c++, i2 += i2step) {
                    m_contourz[contourzp++] = z[i1 + i2 * m_n1];
                }
            }
        }

        m_nxb = m_nx = (m_style == NORMAL ? n1c : n2c);
        m_nyb = m_ny = (m_style == NORMAL ? n2c : n1c);
        m_ixb = m_iyb = 0;
        m_contourzb = m_contourz;

        /* Generate a set of suitable plot coordinates
        (the actual scaling to X-windows coordinates will be done later
         */

        m_ix = new float[m_nx];
        m_iy = new float[m_ny];

        i1step = (i1end > i1beg) ? 1 : -1;
        i2step = (i2end > i2beg) ? 1 : -1;

        if (m_style == NORMAL) {
            i2 = i1beg;
            for (i1 = 0; i1 < m_nx; i1++, i2 += i1step) {
                m_ix[i1] = x1[i2];
            }
            i2 = i2end;
            for (i1 = 0; i1 < m_ny; i1++, i2 -= i2step) {
                m_iy[i1] = x2[i2];
            }
        } else {
            i2 = i2beg;
            for (i1 = 0; i1 < m_nx; i1++, i2 += i2step) {
                m_ix[i1] = x2[i2];
            }
            i2 = i1beg;
            for (i1 = 0; i1 < m_ny; i1++, i2 += i1step) {
                m_iy[i1] = x1[i2];
            }
        }
    }

    private void newContour(java.awt.Graphics pGC,
            int xbase, int ybase, int nx, float x[], int ny, float y[],
            float z[], int nc, float c[], float cwidth[], String ccolor[],
            float cdash[], int labelcf, int labelcper, int nlabelc,
            String labelcfont, String labelccolor) {
        float[] w;
        int ic, linewidth = 0;
        char dash[];
        char lcflag = 0;

        int i;

        /* zero w array for contour labeling*/
        w = new float[nx * ny];
        for (i = 0; i < nx * ny; i++) {
            w[i] = 0f;
        }

        /* shift the axes due to xbase and ybase */
        for (i = 0; i < nx; i++) {
            x[i] = x[i] + xbase;
        }
        for (i = 0; i < ny; i++) {
            y[i] = y[i] + ybase;
        }

        /*Set the color of the contour labels */
        java.awt.Color labelColor = labelColor = java.awt.Color.black;
        if (!labelccolor.equalsIgnoreCase("none")) {
        } else {
        }

        pGC.setColor(labelColor);

        /* Set the labeling font */
        java.awt.Font labelFont = new java.awt.Font("Arial", java.awt.Font.PLAIN,
                12);
        pGC.setFont(labelFont);

        /*loop over contours*/
        for (ic = 0; ic < nc; ic++) {
            /*Set the linestyle */
            if (nlabelc > 0) {
                if ((ic - labelcf + 1) % labelcper == 0 && ic >= labelcf - 1 && ic < labelcf - 1 + labelcper * nlabelc) {
                    linewidth = (int) cwidth[ic];
                    lcflag = 1;
                } else {
                    linewidth = (int) (0.25 * cwidth[ic]);
                    lcflag = 0;
                }
            }

            /*	(note that small line segments will mask the dash settings) */
            if (cdash[ic] != 0.0) {
                //dash[0] = (char) (cdash[ic]);
                //dash[1] = (char) (cdash[ic]);

                //      ((java.awt.Graphics2D) pGC).setStroke();
            } else {
                ((java.awt.Graphics2D) pGC).setStroke(new java.awt.BasicStroke(linewidth));
            }

            /*Set the color of the contour lines */
            java.awt.Color color = java.awt.Color.black;
            if (!ccolor[ic].equalsIgnoreCase("none")) {
            } else {
            }

            pGC.setColor(color);

            /* Let xContour draw the contours */
            xContour(pGC, c[ic], nx, x, ny, y, z, lcflag, labelcfont,
                    labelccolor, w);
        }

    }

    /**
     * Auxiliary function.
     *
     * Extracted from SU sources. Please look at $CWPROOT/src/xplot/main/xcontour.c for
     * detailed description of this function.
     */
    private void xContour(java.awt.Graphics pG, float cp, int nx, float x[], int ny, float y[], float z[],
            char lcflag, String lcf, String lcc, float w[]) {
        int ix, iy, non, startcell;
        float d;
        float xmin = MIN(x[0], x[nx - 1]), xmax = MAX(x[0], x[nx - 1]);
        float ymin = MIN(y[0], y[ny - 1]), ymax = MAX(y[0], y[ny - 1]);
        float xc = 0.0f, yc = 0.0f;	/* contour labeling centered at (xc,yc)	*/
        float xw, yw;	/* width and length of contour labeling */
        float xdmin, xdmax, ydmin, ydmax; /* range of contour	*/
        int id;	/* =0 if a point on contour has been used as (xc,yc) */
        int cells = 0;
        String str;
        float c = cp;
        ConnectParameters connPrm = new ConnectParameters();

        /* convert a number into a string */
        str = String.format("%g", c);

        /* determine length and width for printing the string */
        java.awt.font.FontRenderContext frc = ((java.awt.Graphics2D) pG).getFontRenderContext();
        //java.awt.geom.Rectangle2D br = m_labelsFont.getStringBounds(str, frc);
        xw = 20;//br.getWidth();

        yw = 20;//br.getHeight();

        /* restrict contour labeling from edges */
        for (iy = 0; iy < ny - 1; iy++) {
            for (ix = 0         , connPrm.cell = iy * nx; ix < nx - 1; ix++, connPrm.cell++) {
                if (x[ix] < xmin + 2.0 * xw || x[ix] > xmax - 2.0 * xw || y[iy] < ymin + yw || y[iy] > ymax - yw) {
                    w[(int) connPrm.cell] += 1.;
                }
            }
        }

        /* count intersections with cell boundaries */
        non = 0;

        /* find all the intersections */
        for (iy = 0; iy < ny; iy++) {
            for (ix = 0        , connPrm.cell= 
                 iy * nx  ; ix < nx; ix++, connPrm.cell++) {
                /* check for intersection with west edge of cell */
                if ((iy < ny - 1) && BTWN(c, z[(int) connPrm.cell], z[(int) connPrm.cell + nx])) {
                    z[(int) connPrm.cell] = SETW(z[(int) connPrm.cell]);
                    non++;
                } else {
                    z[(int) connPrm.cell] = CLRW(z[(int) connPrm.cell]);
                }

                /* check for intersection with south edge of cell */
                if ((ix < nx - 1) && BTWN(c, z[(int) connPrm.cell], z[(int) connPrm.cell + 1])) {
                    z[(int) connPrm.cell] = SETS(z[(int) connPrm.cell]);
                    non++;
                } else {
                    z[(int) connPrm.cell] = CLRS(z[(int) connPrm.cell]);
                }
            }
        }

        /* follow contours intersecting north boundary */
        for (ix = 0    , startcell = (ny - 1) * nx; (non > 0) && (ix < nx - 1); ix++, startcell++) {
            if (SSET(z[startcell]) != 0) {
                d = DELTA(c, z[startcell], z[startcell + 1]);
                connPrm.x0 = (1.0f - d) * x[ix] + d * x[ix + 1];
                connPrm.y0 = y[ny - 1];
                z[startcell] = CLRS(z[startcell]);
                non--;
                connPrm.cell = startcell - nx;
                id = 1;
                xdmin = xmax;
                xdmax = xmin;
                ydmin = ymax;
                ydmax = ymin;

                while (connect(pG, c, nx, x, ny, y, z, connPrm) != 0) {
                    connPrm.x0 = connPrm.xd;
                    connPrm.y0 = connPrm.yd;
                    non--;
                    if ((w[(int) connPrm.cell] < 0.5f) && (id != 0)) {
                        xc = connPrm.xd;
                        yc = connPrm.yd;
                        cells = (int) connPrm.cell;
                        id = 0;
                    }
                    xdmin = MIN(xdmin, connPrm.xd);
                    xdmax = MAX(xdmax, connPrm.xd);
                    ydmin = MIN(ydmin, connPrm.yd);
                    ydmax = MAX(ydmax, connPrm.yd);
                }
                if ((lcflag != 0) && (id == 0) && (xdmax + ydmax - xdmin - ydmin) > (xw + yw)) {
                    wcell(nx, x, ny, y, w, cells, xc, yc, xw, yw);
                    labelc(pG, xc - xw / 2, yc - yw / 2, xw, yw, str,
                            lcf, lcc);
                }
            }
        }

        /* follow contours intersecting east boundary */
        for (iy = 0    , startcell = nx - 1; (non > 0) && (iy < ny - 1); iy++, startcell += nx) {
            if (WSET(z[startcell]) != 0) {
                d = DELTA(c, z[startcell], z[startcell + nx]);
                connPrm.x0 = x[nx - 1];
                connPrm.y0 = (1.0f - d) * y[iy] + d * y[iy + 1];
                z[startcell] = CLRW(z[startcell]);
                non--;
                connPrm.cell = startcell - 1;
                id = 1;
                xdmin = xmax;
                xdmax = xmin;
                ydmin = ymax;
                ydmax = ymin;
                while (connect(pG, c, nx, x, ny, y, z, connPrm) != 0) {
                    connPrm.x0 = connPrm.xd;
                    connPrm.y0 = connPrm.yd;
                    non--;
                    if ((w[(int) connPrm.cell] < 0.5f) && (id != 0)) {
                        xc = connPrm.xd;
                        yc = connPrm.yd;
                        cells = (int) connPrm.cell;
                        id = 0;
                    }
                    xdmin = MIN(xdmin, connPrm.xd);
                    xdmax = MAX(xdmax, connPrm.xd);
                    ydmin = MIN(ydmin, connPrm.yd);
                    ydmax = MAX(ydmax, connPrm.yd);
                }
                if ((lcflag != 0) && (id == 0) && (xdmax + ydmax - xdmin - ydmin) > (xw + yw)) {
                    wcell(nx, x, ny, y, w, cells, xc, yc, xw, yw);
                    labelc(pG, xc - xw / 2, yc - yw / 2, xw, yw, str,
                            lcf, lcc);
                }
            }
        }

        /* follow contours intersecting south boundary */
        for (ix = 0  , startcell = 0; (non > 0) && (ix < nx - 1); ix++, startcell++) {
            if (SSET(z[startcell]) != 0) {
                d = DELTA(c, z[startcell], z[startcell + 1]);
                connPrm.x0 = (1.0f - d) * x[ix] + d * x[ix + 1];
                connPrm.y0 = y[0];
                z[startcell] = CLRS(z[startcell]);
                non--;
                connPrm.cell = startcell;
                id = 1;
                xdmin = xmax;
                xdmax = xmin;
                ydmin = ymax;
                ydmax = ymin;
                while (connect(pG, c, nx, x, ny, y, z, connPrm) != 0) {
                    connPrm.x0 = connPrm.xd;
                    connPrm.y0 = connPrm.yd;
                    non--;
                    if ((w[(int) connPrm.cell] < 0.5f) && (id != 0)) {
                        xc = connPrm.xd;
                        yc = connPrm.yd;
                        cells = (int) connPrm.cell;
                        id = 0;
                    }
                    xdmin = MIN(xdmin, connPrm.xd);
                    xdmax = MAX(xdmax, connPrm.xd);
                    ydmin = MIN(ydmin, connPrm.yd);
                    ydmax = MAX(ydmax, connPrm.yd);
                }
                if ((lcflag != 0) && (id == 0) && (xdmax + ydmax - xdmin - ydmin) > (xw + yw)) {
                    wcell(nx, x, ny, y, w, cells, xc, yc, xw, yw);
                    labelc(pG, xc - xw / 2, yc - yw / 2, xw, yw, str, lcf, lcc);
                }
            }
        }

        /* follow contours intersecting west boundary */
        for (iy = 0  , startcell = 0; (non > 0) && (iy < ny - 1); iy++, startcell += nx) {
            if (WSET(z[startcell]) != 0) {
                d = DELTA(c, z[startcell], z[startcell + nx]);
                connPrm.x0 = x[0];
                connPrm.y0 = (1.0f - d) * y[iy] + d * y[iy + 1];
                z[startcell] = CLRW(z[startcell]);
                non--;
                connPrm.cell = startcell;
                id = 1;
                xdmin = xmax;
                xdmax = xmin;
                ydmin = ymax;
                ydmax = ymin;
                while (connect(pG, c, nx, x, ny, y, z, connPrm) != 0) {
                    connPrm.x0 = connPrm.xd;
                    connPrm.y0 = connPrm.yd;
                    non--;
                    if ((w[(int) connPrm.cell] < 0.5) && (id != 0)) {
                        xc = connPrm.xd;
                        yc = connPrm.yd;
                        cells = (int) connPrm.cell;
                        id = 0;
                    }
                    xdmin = MIN(xdmin, connPrm.xd);
                    xdmax = MAX(xdmax, connPrm.xd);
                    ydmin = MIN(ydmin, connPrm.yd);
                    ydmax = MAX(ydmax, connPrm.yd);
                }
                if ((lcflag != 0) && (id == 0) && (xdmax + ydmax - xdmin - ydmin) > (xw + yw)) {
                    wcell(nx, x, ny, y, w, cells, xc, yc, xw, yw);
                    labelc(pG, xc - xw / 2, yc - yw / 2, xw, yw, str, lcf, lcc);
                }
            }
        }

        /* follow interior contours */
        for (iy = 1; iy < ny - 1; iy++) {
            for (ix = 0       , startcell = iy * nx; (non > 0) && (ix < nx - 1); ix++, startcell++) {
                /* check south edge of cell */
                if (SSET(z[startcell]) != 0) {
                    d = DELTA(c, z[startcell], z[startcell + 1]);
                    connPrm.x0 = (1.0f - d) * x[ix] + d * x[ix + 1];
                    connPrm.y0 = y[iy];

                    /* clear south edge where we started */
                    z[startcell] = CLRS(z[startcell]);
                    non--;

                    connPrm.cell = startcell;

                    /* if another intersection exists in this cell */
                    if (connect(pG, c, nx, x, ny, y, z, connPrm) != 0) {
                        connPrm.x0 = connPrm.xd;
                        connPrm.y0 = connPrm.yd;

                        /* set south edge so that we finish where we started */
                        z[startcell] = SETS(z[startcell]);
                        non++;

                        /* follow the contour */
                        id = 1;
                        xdmin = xmax;
                        xdmax = xmin;
                        ydmin = ymax;
                        ydmax = ymin;
                        while (connect(pG, c, nx, x, ny, y, z, connPrm) != 0) {
                            connPrm.x0 = connPrm.xd;
                            connPrm.y0 = connPrm.yd;
                            non--;
                            if ((w[(int) connPrm.cell] < 0.5) && (id != 0)) {
                                xc = connPrm.xd;
                                yc = connPrm.yd;
                                cells = (int) connPrm.cell;
                                id = 0;
                            }
                            xdmin = MIN(xdmin, connPrm.xd);
                            xdmax = MAX(xdmax, connPrm.xd);
                            ydmin = MIN(ydmin, connPrm.yd);
                            ydmax = MAX(ydmax, connPrm.yd);
                        }
                        if ((lcflag != 0) && (id == 0) && (xdmax + ydmax - xdmin - ydmin) > (xw + yw)) {
                            wcell(nx, x, ny, y, w, cells, xc, yc, xw, yw);
                            labelc(pG, xc - xw / 2, yc - yw / 2, xw, yw, str,
                                    lcf, lcc);
                        }
                    }
                }
            }
        }
    /* contour drawing is done */
    }

    private void wcell(int nx, float x[], int ny, float y[], float w[],
            int cell, float xc, float yc, float xw, float yw) {
        int ix = cell % nx, iy = cell / nx, ixl = ix, ixh = ix, iyl = iy, iyh = iy;

        while (x[ixl] >= xc - 2.0 * xw && x[ixl] <= xc + 2.0 * xw && ixl > 0) {
            ixl--;
        }
        while (x[ixh] >= xc - 2.0 * xw && x[ixh] <= xc + 2.0 * xw && ixh < nx - 1) {
            ixh++;
        }
        while (y[iyl] >= yc - 1.5 * yw && y[iyl] <= yc + 1.5 * yw && iyl > 0) {
            iyl--;
        }
        while (y[iyh] >= yc - 1.5 * yw && y[iyh] <= yc + 1.5 * yw && iyh < ny - 1) {
            iyh++;
        }

        for (iy = iyl; iy <= iyh; ++iy) {
            for (ix = ixl          , cell= 
                iy  * nx + ixl; ix <= ixh; ix++, cell++) {
                w[cell] += 1.0;
            }
        }

    }

    private void labelc(java.awt.Graphics pG, float x, float y,
            float xw, float yw, String str, String font, String color) {
        /*	XClearArea(dpy,drawarea,NINT(x),NINT(y),
        (unsigned int) NINT(xw),
        (unsigned int) NINT(yw),False);
         */
        /*  XDrawString(dpy, win, gcl, NINT(x), NINT(y + yw),
        str, (int) strlen(str));
        if (font - font) {
        color += 0;
        }
         */
    }

    /**
     * Auxiliary function.
     *
     * Extracted from SU sources. Please look at $CWPROOT/src/xplot/main/xcontour.c for
     * detailed description of this function.
     */
    int connect(java.awt.Graphics pG, float c, int nx, float x[], int ny, float y[], float z[],
            ConnectParameters prm) {

        int cell = (int) prm.cell;
        int ix = cell % nx;
        int iy = cell / nx;
        float d;

        /* if exiting north */
        if (SSET(z[cell + nx]) != 0) {
            cell += nx;
            iy++;

            d = DELTA(c, z[cell], z[cell + 1]);
            prm.xd = (1.0f - d) * x[ix] + d * x[ix + 1];
            prm.yd = y[iy];

            pG.drawLine((int) prm.x0, (int) prm.y0, (int) prm.xd, (int) prm.yd);

            z[cell] = CLRS(z[cell]);
            prm.cell = cell;
            if (iy < ny - 1) {
                return (1);
            } else {
                return (0);
            }

        /* else if exiting east */
        } else if (WSET(z[cell + 1]) != 0) {
            cell += 1;
            ix++;

            d = DELTA(c, z[cell], z[cell + nx]);
            prm.xd = x[ix];
            prm.yd = (1.0f - d) * y[iy] + d * y[iy + 1];

            pG.drawLine((int) prm.x0, (int) prm.y0, (int) prm.xd, (int) prm.yd);

            z[cell] = CLRW(z[cell]);
            prm.cell = cell;
            if (ix < nx - 1) {
                return (1);
            } else {
                return (0);
            }

        /* else if exiting south */
        } else if (SSET(z[cell]) != 0) {
            d = DELTA(c, z[cell], z[cell + 1]);
            prm.xd = (1.0f - d) * x[ix] + d * x[ix + 1];
            prm.yd = y[iy];

            pG.drawLine((int) prm.x0, (int) prm.y0, (int) prm.xd, (int) prm.yd);

            z[cell] = CLRS(z[cell]);
            prm.cell = cell - nx;
            if (iy > 0) {
                return (1);
            } else {
                return (0);
            }

        /* else if exiting west */
        } else if (WSET(z[cell]) != 0) {
            d = DELTA(c, z[cell], z[cell + nx]);
            prm.xd = x[ix];
            prm.yd = (1.0f - d) * y[iy] + d * y[iy + 1];

            pG.drawLine((int) prm.x0, (int) prm.y0, (int) prm.xd, (int) prm.yd);

            z[cell] = CLRW(z[cell]);
            prm.cell = cell - 1;
            if (ix > 0) {
                return (1);
            } else {
                return (0);
            }

        /* else if no intersection exists */
        } else {
            return (0);
        }

    }

    boolean BTWN(float a, float b, float c) {
        return (Math.min(b, c) <= a) && (a < Math.max(b, c));
    }

    private float DELTA(float a, float b, float c) {
        return (b != c ? (a - b) / (c - b) : 1.0f);
    }

    private float SETS(float z) {
        return Float.intBitsToFloat(Float.floatToIntBits(z) | SOUTH);
    }

    private float CLRS(float z) {
        return Float.intBitsToFloat(Float.floatToIntBits(z) & (~SOUTH));
    }

    private float SSET(float z) {
        return Float.intBitsToFloat(Float.floatToIntBits(z) & SOUTH);
    }

    private float SETW(float z) {
        return Float.intBitsToFloat(Float.floatToIntBits(z) | WEST);
    }

    private float CLRW(float z) {
        return Float.intBitsToFloat(Float.floatToIntBits(z) & (~WEST));
    }

    private float WSET(float z) {
        return Float.intBitsToFloat(Float.floatToIntBits(z) & WEST);
    }

    private float MAX(float x, float y) {
        return x > y ? x : y;
    }

    private float MIN(float x, float y) {
        return x < y ? x : y;
    }

    class ConnectParameters {

        float cell;
        float xd;
        float yd;
        float x0;
        float y0;
    }

    class ZoomParameters {

        public int nxb;
        public int nyb;
        public int ixb;
        public int iyb;
        public float x1b;
        public float x2b;
        public float y1b;
        public float y2b;
    }
    //
    private final int SOUTH = 01;
    private final int WEST = 02;
    private final int NCMAX = 1024;      /* Max number of contour lines */

    /* Data */
    float m_dc;
    float m_fc;
    float m_zmin;
    float m_zmax;
    float m_labelcsize;
    float m_cwidth[] = new float[NCMAX];
    float m_cgray[] = new float[NCMAX];
    float m_cdash[] = new float[NCMAX];
    float m_c[] = new float[NCMAX];
    int m_nx;
    int m_ny;
    int m_nxb;
    int m_nyb;
    int m_ixb;
    int m_iyb;
    String m_ccolor[];
    int m_nc;
    int m_labelcf;
    int m_nlabelc;
    int m_labelcper;
    float m_contourz[];
    float m_contourzb[];
    float m_contourzbp[];
    float m_ix[];
    float m_iy[];
}
