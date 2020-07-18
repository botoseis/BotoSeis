package gfx;

import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;
/*
 * ColorScale.java
 *
 * Created on 12 de Agosto de 2007, 07:24
 *
 * Strongly based on SU codes, ximage.c.
 * 
 * Project: SUVis
 */

/**
 *
 * @author Williams Lima, williams_al@gmx.com
 */
public class SVColorScale extends SVActor {

    /** Creates a new instance of SciVisImage */
    public SVColorScale(int pBytesPerPixel, int pByteOrder) {
        m_style = SEISMIC;

        m_bytesPerPixel = pBytesPerPixel;
        m_byteOrder = pByteOrder;

        java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        m_gc = ge.getDefaultScreenDevice().getDefaultConfiguration();

        m_autoClip = true;
        m_autoBClip = true;
        m_autoWClip = true;

        m_autoPerc = true;
        m_autoBPerc = true;
        m_autoWPerc = true;
        m_autoBalance = true;

        m_data = null;
    }

    @Override
    public void paint(java.awt.Graphics g) {
        //   System.out.println("45" );
        if (m_isVisible) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
            if (m_imageOutOfDate) {
                dataToByte();
                m_czbi = newInterpBytes(m_nxb, m_nyb, m_czb, m_width,
                        m_height, false);
                m_img = createBufferedImage(m_czbi, m_width, m_height,
                        m_currColormapIndex);
                m_imageOutOfDate = false;
            }
            if (m_img != null) {
                g2.drawImage(m_img, m_x, m_y, null);
            }
        }
    }

    public BufferedImage getColorbar(int w, int h) {
        int base;
        float fact;
        short[] data = new short[w * h];

        if (m_bclip < m_wclip) {
            base = 256;
            fact = -256.0f;
        } else {
            base = 0;
            fact = 256.0f;
        }
        int ptr = 0;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                data[ptr++] = (short) (base + (fact * i) / h);
            }
        }

        BufferedImage img = createBufferedImage(data, w, h, m_currColormapIndex);

        return img;
    }

    @Override
    public void setStyle(int s) {
        if ((m_style != s) && (m_data != null)) {
            m_style = s;
            dataToByte();
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

        dataToByte();

        m_imageOutOfDate = true;

    }
    
    public void setColormap(int type, int map){
        if(type == RGB){
            setRGBColormap(map);
        }else{
            setHSVColormap(map);
        }
    }

    public void setRGBColormap(int map) {
        if (map < 0) {
            map = 0;
        }
        if (map > RGB_MAX) {
            map = RGB_MAX;
        }

        m_colormapType = RGB;
        m_currColormapIndex = map;
        m_imageOutOfDate = true;
    }

    public void setHSVColormap(int map) {
        if (map < 0) {
            map = 0;
        }
        if (map > HSV_MAX) {
            map = HSV_MAX;
        }
        m_colormapType = HSV;
        m_currColormapIndex = map;
        m_imageOutOfDate = true;
    }

    public void nextColormap() {
        m_currColormapIndex++;

        if (m_colormapType == RGB && m_currColormapIndex > RGB_MAX) {
            m_currColormapIndex = 0;
        }
        if (m_colormapType == HSV && m_currColormapIndex > HSV_MAX) {
            m_currColormapIndex = 0;
        }

        m_imageOutOfDate = true;
    }

    public void previousColormap() {
        m_currColormapIndex--;

        if (m_colormapType == RGB && m_currColormapIndex < 0) {
            m_currColormapIndex = RGB_MAX;
        }
        if (m_colormapType == HSV && m_currColormapIndex < 0) {
            m_currColormapIndex = HSV_MAX;
        }

        m_imageOutOfDate = true;
    }

    public void setAutoBalance(boolean flag) {
        m_autoBalance = flag;
        m_imageOutOfDate = true;
    }

    public void setPercParameters(float perc, float bperc, float wperc) {
        m_perc = perc;
        m_bperc = bperc;
        m_wperc = wperc;
        m_autoPerc = false;
        m_autoClip = true;
        m_imageOutOfDate = true;
    }
    ///coloquei em 15/04/09

    public void setImagePerc(float perc) {
        m_perc = perc;
        m_autoPerc = false;
        m_autoClip = true;
        m_imageOutOfDate = true;
    }
    
    public void setColorMapType(int type){
        m_colormapType = type;
    }

    public float getImagPerc() {
        return m_perc;
    }

    public float getImagbPerc() {
        return m_bperc;
    }

    public float getImagwperc() {
        return m_wperc;
    }

    public float getImagclip() {
        return m_clip;
    }

    public float getImagbclip() {
        return m_bclip;
    }

    public float getImagwclip() {
        return m_wclip;
    }

    public float getImagimagebalance() {
        return m_balance;
    }

    public void resetPercParameters() {
        m_autoPerc = false;

        m_imageOutOfDate = true;
    }

    public void setbalance(int balance) {
        m_balance = balance;
        m_autoBalance = false;
    }

    public int getCurrColorMapIndex(){
        return m_currColormapIndex;
    }
    
    public void setClipParameters(float clip, float bclip, float wclip) {
//           System.out.println("229 m_clip =" +  m_clip );
//           System.out.println("229  m_wclip"  + m_wclip );
//         System.out.println("229  m_bclip"  + m_bclip );
        m_clip = clip;
        m_bclip = bclip;
        m_wclip = wclip;
        m_autoClip = false;
//          System.out.println("229 m_clip =" +  m_clip );
//           System.out.println("229  m_wclip"  + m_wclip );

        m_imageOutOfDate = true;
    }

    public void resetClipParameters() {
        m_autoClip = true;
        m_imageOutOfDate = true;
    }

    private void dataToByte() {
        int iz, i1, i2, i1c, i2c, i1step, i2step;
        float zscale, zoffset, zi;

        int nz = m_n1 * m_n2;

        float perc = 100.0f;
        float bperc = 0;
        float clip = 0;
        float bclip = 0;
        float wperc = 0;
        float wclip = 0;

        if (m_autoClip) {
            float temp[] = new float[nz];

            int balance = 0;

            if (m_autoPerc) {
                perc = 100.0f;
                m_perc = perc;    //coloquei 13/04/09

            } else {
                perc = m_perc;
            }

            if (m_autoBalance) {
                balance = 0;
            } else {
                balance = m_balance;
            }
            m_balance = balance;  //coloquei 13/04/09
            if (balance == 0) {
                for (iz = 0; iz < nz; ++iz) {
                    temp[iz] = m_data[iz];
                }
            } else {
                for (iz = 0; iz < nz; ++iz) {
                    temp[iz] = Math.abs(m_data[iz]);
                }
            }

            /* End of modded code */
            if (m_autoBClip) {
                bperc = perc;

                if (!m_autoBPerc) {
                    bperc = m_bperc;
                }
                m_bperc = bperc;       //coloquei 13/04/09


                iz = (int) (nz * bperc / 100.0f);
                if (iz < 0) {
                    iz = 0;
                }
                if (iz > nz - 1) {
                    iz = nz - 1;
                }
                utils.Sorting.qkfind(iz, nz, temp);
                bclip = temp[iz];
            } else {
                bclip = m_bclip;
            }

            m_bclip = bclip;
            if (m_autoWClip) {
                wperc = 100.0f - perc;
                if (!m_autoWPerc) {
                    wperc = m_wperc;
                }
                iz = (int) (nz * wperc / 100.0f);
                if (iz < 0) {
                    iz = 0;
                }
                if (iz > nz - 1) {
                    iz = nz - 1;
                }
                utils.Sorting.qkfind(iz, nz, temp);
                /* Modded by GCP to balance bclip & wclip */
                if (balance == 0) {
                    wclip = temp[iz];
                } else {
                    wclip = -1 * bclip;
                }
                /* End of modded code */
            } else {
                wclip = m_wclip;
            }
            m_wclip = wclip;
            m_wperc = wperc;  //coloquei 13/04/09
            m_clip = clip;  //coloquei 13/04/09
        }

        /* adjust x1beg and x1end to fall on sampled values */
        m_i1beg = NINT((m_x1begb - m_f1) / m_d1);
        m_i1beg = (int) MAX(0.f, MIN(m_n1 - 1, m_i1beg));
        m_x1begb = m_f1 + m_i1beg * m_d1;
        m_i1end = NINT((m_x1endb - m_f1) / m_d1);
        m_i1end = (int) MAX(0.f, MIN(m_n1 - 1, m_i1end));
        m_x1endb = m_f1 + m_i1end * m_d1;

        /* adjust x2beg and x2end to fall on sampled values */
        m_i2beg = NINT((m_x2begb - m_f2) / m_d2);
        m_i2beg = (int) MAX(0.f, MIN(m_n2 - 1, m_i2beg));
        m_x2begb = m_f2 + m_i2beg * m_d2;
        m_i2end = NINT((m_x2endb - m_f2) / m_d2);
        m_i2end = (int) MAX(0, MIN(m_n2 - 1, m_i2end));
        m_x2endb = m_f2 + m_i2end * m_d2;

        /* allocate space for image bytes */
        m_n1c = 1 + Math.abs(m_i1end - m_i1beg);
        m_n2c = 1 + Math.abs(m_i2end - m_i2beg);

        m_cz = new short[m_n1c * m_n2c];

        m_nxb = m_nx = (m_style == NORMAL ? m_n1c : m_n2c);
        m_nyb = m_ny = (m_style == NORMAL ? m_n2c : m_n1c);

        m_ixb = m_iyb = 0;
        m_czb = m_cz;

        zscale = (wclip != bclip) ? 255.0f / (wclip - bclip) : 1.0e10f;
        zoffset = -bclip * zscale;
        i1step = (m_i1end > m_i1beg) ? 1 : -1;
        i2step = (m_i2end > m_i2beg) ? 1 : -1;

        int czp = 0;
        if (m_style == NORMAL) {
            for (i2c = 0, i2 = m_i2beg;
                    i2c < m_n2c; i2c++, i2 += i2step) {
                czp = m_n1c * m_n2c - (i2c + 1) * m_n1c;
                for (i1c = 0, i1 = m_i1beg;
                        i1c < m_n1c;
                        i1c++, i1 += i1step) {
                    zi = zoffset + m_data[i1 + i2 * m_n1] * zscale;
                    if (zi < 0.0f) {
                        zi = 0.0f;
                    }
                    if (zi > 255.0f) {
                        zi = 255.0f;
                    }
                    m_cz[czp] = (short) zi;
                    czp++;
                }
            }
        } else {
            for (i1c = 0, i1 = m_i1beg;
                    i1c < m_n1c; i1c++, i1 += i1step) {
                for (i2c = 0, i2 = m_i2beg;
                        i2c < m_n2c;
                        i2c++, i2 += i2step) {
                    zi = zoffset + m_data[i1 + i2 * m_n1] * zscale;
                    if (zi < 0.0f) {
                        zi = 0.0f;
                    }
                    if (zi > 255.0f) {
                        zi = 255.0f;
                    }
                    m_cz[czp] = (short) (zi);
                    czp++;
                }
            }
        }
        m_imageOutOfDate = true;
    }

    public BufferedImage createBufferedImage(
            short pBytes[], int pWidth, int pHeight, int cmap) {
        BufferedImage img = null;
        if ((pWidth > 0) && (pHeight > 0)) {
            int i;
            int rgbArray[] = new int[pWidth * pHeight];
            int[] tcpixels = null;
            if (m_colormapType == RGB) {
                tcpixels = m_cmap.getRGBTrueColorPixels(cmap);
            } else {
                tcpixels = m_cmap.getHSVTrueColorPixels(cmap);
            }
            for (i = 0; i < pBytes.length; i++) {
                if (m_bytesPerPixel == 3) {
                    int edn = m_byteOrder;
                    if (edn == LSBFirst) {
                        rgbArray[i] = tcpixels[pBytes[i]];
                    } else {
                        //rgbArray[i] = (byte)(tcpixels[pBytes[i]] >> 24);
                    }
                }
            }

            img = m_gc.createCompatibleImage(pWidth, pHeight,
                    BufferedImage.TYPE_INT_RGB);

            img.setRGB(0, 0, pWidth, pHeight, rgbArray, 0, pWidth);
        }
        return img;

    }

    private short[] newInterpBytes(int n1in, int n2in, short bin[],
            int n1out, int n2out, boolean useBlockInterp) /* JG */ {
        float d1in, d2in, d1out, d2out, f1in, f2in, f1out, f2out;

        f1in = f2in = f1out = f2out = 0.0f;
        d1in = d2in = 1.0f;
        d1out = d1in * (float) (n1in - 1) / (float) (n1out - 1);
        d2out = d2in * (float) (n2in - 1) / (float) (n2out - 1);
        short bout[] = new short[n1out * n2out];
        /* JG .... */
        if (!useBlockInterp) {
            intl2b(n1in, d1in, f1in, n2in, d2in, f2in, bin,
                    n1out, d1out, f1out, n2out, d2out, f2out, bout);
        } else {
            //    intl2b_block(n1in,d1in,f1in,n2in,d2in,f2in,bin,
            //          n1out,d1out,f1out,n2out,d2out,f2out,bout);
        }
        /* .... JG */
        return bout;
    }

    /*******************************************************************************
     * Notes:
     * The arrays zin and zout must passed as pointers to the first element of
     * a two-dimensional contiguous array of unsigned char values.
     *
     * Constant extrapolation of zin is used to compute zout for
     * output x and y outside the range of input x and y.
     *
     ******************************************************************************
     *
     * Author:  James Gunning, CSIRO Petroleum 1999. Hacked from
     * intl2b() by Dave Hale, Colorado School of Mines, c. 1989-1991
     *****************************************************************************/
    /**************** end self doc ********************************/
    private short[] intl2b_block(int nxin, float dxin, float fxin,
            int nyin, float dyin, float fyin, short zin[],
            int nxout, float dxout, float fxout,
            int nyout, float dyout, float fyout) /*****************************************************************************
     * blocky interpolation of a 2-D array of bytes: gridblock effect
     ******************************************************************************
     * Input:
     * nxin		number of x samples input (fast dimension of zin)
     * dxin		x sampling interval input
     * fxin		first x sample input
     * nyin		number of y samples input (slow dimension of zin)
     * dyin		y sampling interval input
     * fyin		first y sample input
     * zin		    array[nyin][nxin] of input samples (see notes)
     * nxout		number of x samples output (fast dimension of zout)
     * dxout		x sampling interval output
     * fxout		first x sample output
     * nyout		number of y samples output (slow dimension of zout)
     * dyout		y sampling interval output
     * fyout		first y sample output
     *
     * Output:
     *   return value		array[nyout][nxout] of output samples (see notes)
     ******************************************************************************
     * Notes:
     * The array zin must passed as pointers to the first element of
     * a two-dimensional contiguous array of unsigned char values.
     *
     * Constant extrapolation of zin is used to compute zout for
     * output x and y outside the range of input x and y.
     *
     * Mapping of bytes between arrays is done to preserve appearance of `gridblocks':
     * no smooth interpolation is performed.
     *
     *****************************************************************************/
    {
        int ixout, iyout, iin, jin;
        float xoff, yoff;
        short zout[] = new short[nxout * nyout];

        xoff =
                fxout + 0.5f * dxin - fxin;
        yoff =
                fyout + 0.5f * dyin - fyin;
        for (iyout = 0; iyout
                < nyout; iyout++) {
            jin = (int) ((iyout * dyout + yoff) / dyin);
            jin =
                    (int) MIN(nyin - 1, MAX(jin, 0));
            for (ixout = 0; ixout
                    < nxout; ixout++) {
                iin = (int) ((ixout * dxout + xoff) / dxin);
                iin =
                        (int) MIN(nxin - 1, MAX(iin, 0));
                zout[nxout * iyout + ixout] = zin[nxin * jin + iin];
            }

        }

        return zout;
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

    private void intl2b(int nxin, float dxin, float fxin,
            int nyin, float dyin, float fyin, short zin[],
            int nxout, float dxout, float fxout,
            int nyout, float dyout, float fyout, short zout[]) /*****************************************************************************
     * bilinear interpolation of a 2-D array of bytes
     ******************************************************************************
     * Input:
     * nxin		number of x samples input (fast dimension of zin)
     * dxin		x sampling interval input
     * fxin		first x sample input
     * nyin		number of y samples input (slow dimension of zin)
     * dyin		y sampling interval input
     * fyin		first y sample input
     * zin		array[nyin][nxin] of input samples (see notes)
     * nxout		number of x samples output (fast dimension of zout)
     * dxout		x sampling interval output
     * fxout		first x sample output
     * nyout		number of y samples output (slow dimension of zout)
     * dyout		y sampling interval output
     * fyout		first y sample output
     *
     * Output:
     * zout		array[nyout][nxout] of output samples (see notes)
     ******************************************************************************
     * Notes:
     * The arrays zin and zout must passed as pointers to the first element of
     * a two-dimensional contiguous array of unsigned char values.
     *
     * Constant extrapolation of zin is used to compute zout for
     * output x and y outside the range of input x and y.
     *
     * For efficiency, this function builds a table of interpolation
     * coefficents pre-multiplied by byte values.  To keep the table
     * reasonably small, the interpolation does not distinguish
     * between x and y values that differ by less than dxin/ICMAX
     * and dyin/ICMAX, respectively, where ICMAX is a parameter
     * defined above.
     ******************************************************************************
     * Author:  Dave Hale, Colorado School of Mines, 07/02/89
     * Modified:  Dave Hale, Colorado School of Mines, 05/30/90
     * Changed function to interpolate unsigned char
     * instead of signed char, since many color tables and
     * image processing functions (e.g., PostScript) require
     * bytes with a maximum range of 0 to 255.
     * Modified:  Dave Hale, Colorado School of Mines, 06/01/91
     * Changed computation of coefficient table to avoid
     * errors due to truncation in float to fix.  Old code
     * sometimes caused interpolated values to be less than
     * the minimum of the byte values being interpolated or
     * greater than the maximum of the values being interpolated.
     *****************************************************************************/
    {
        int ixout, iyout, ic, ib, iyin, iyinl = 1;
        float xout, yout, rxin, ryin, frac;
        int kzin[];
        int kic[];
        short temp1[];
        short temp2[];
        short temp[];

        short table[] = new short[NTABLE * 256];
        int tabled = 0;

        /* if not already built, build byte multiplication table */
        if (tabled == 0) {
            for (ic = 0; ic
                    <= ICMAX / 2; ++ic) {
                frac = (float) (ic) / (float) ICMAX;
                for (ib = 0; ib
                        < 256; ++ib) {
                    table[ic * 256 + ib] = (short) (frac * ib);
                    table[(ICMAX - ic) * 256 + ib] = (short) (ib - table[ic * 256 + ib]);
                }

            }
            tabled = 1;
        }

        /* get workspace */
        kzin = new int[nxout];
        kic =
                new int[nxout];

        temp1 =
                new short[nxout];
        temp2 =
                new short[nxout];

        /* pre-compute indices for fast 1-D interpolation along x axis */
        for (ixout = 0, xout = fxout;
                ixout
                < nxout;
                ixout++, xout += dxout) {
            rxin = (xout - fxin) / dxin;
            if (rxin <= 0) {
                kzin[ixout] = 0;
                kic[ixout] = 0;
            } else if (rxin >= nxin - 1) {
                kzin[ixout] = nxin - 2;
                kic[ixout] = ICMAX * 256;
            } else {
                kzin[ixout] = (int) rxin;
                frac =
                        rxin - (int) rxin;
                ic =
                        (int) (frac * ICMAX + 0.5);
                kic[ixout] = ic * 256;
            }

        }

        /* loop over output y */
        for (iyout = 0, yout = fyout;
                iyout
                < nyout; iyout++, yout += dyout) {

            /* compute index of input y, clipped to range of input y */
            ryin = MAX(0, MIN(nyin - 1, (yout - fyin) / dyin));
            iyin =
                    (int) MAX(0, MIN(nyin - 2, ryin));

            /* if output y is not between current input y */
            if (iyin != iyinl || iyout == 0) {

                /* if 2nd temporary vector is still useful */
                if (iyin == iyinl + 1 && iyout != 0) {

                    /* swap 2nd and 1st temp; compute 2nd temp */
                    temp = temp1;
                    temp1 =
                            temp2;
                    temp2 =
                            temp;
                    intl2bx(nxout, kzin, kic, ICMAX,
                            table, zin, (iyin + 1) * nxin, temp2, 0);
                    /* else if 1st temporary vector is still useful */
                } else if (iyin == iyinl - 1 && iyout != 0) {

                    /* swap 1st and 2nd temp; compute 1st temp */
                    temp = temp1;
                    temp1 =
                            temp2;
                    temp2 =
                            temp;

                    intl2bx(nxout, kzin, kic, ICMAX,
                            table, zin, iyin * nxin, temp1, 0);

                    /* else if neither 1st or 2nd temp is useful */
                } else {

                    /* compute 1st and 2nd temporary vectors */
                    intl2bx(nxout, kzin, kic, ICMAX,
                            table, zin, iyin * nxin, temp1, 0);

                    intl2bx(nxout, kzin, kic, ICMAX,
                            table, zin, (iyin + 1) * nxin, temp2, 0);

                }

                /* remember last index of input y */
                iyinl = iyin;
            }

            /* compute index of interpolation coefficient */
            frac = ryin - iyin;
            ic =
                    (int) (frac * ICMAX + 0.5f);

            /* linearly interpolate output vector by table lookup */
            intl2by(nxout, ic, ICMAX, table,
                    temp1, temp2, zout, iyout * nxout);

        }

    }

    private void intl2bx(int nxout, int kzin[], int kic[], int icmax,
            short table[], short zin[], int offset_zin, short zout[], int offset_zout) /****************************************************************************
     * interpolate between input x values (FOR INTERNAL USE by intl2b)
     ****************************************************************************/
    {
        int ixout, jzin, jic;

        for (ixout = 0; ixout
                < nxout; ixout++) {
            jzin = kzin[ixout];
            jic =
                    kic[ixout];
            zout[ixout + offset_zout] = table[icmax * 256 + (int) zin[jzin + offset_zin] - jic];
            zout[ixout + offset_zout] += table[(int) zin[jzin + 1 + offset_zin] + jic];
        }

    }

    private void intl2by(int nxout, int ic, int icmax, short table[],
            short temp1[], short temp2[], short zout[], int offset_zout) /****************************************************************************
     * interpolate between input y values (FOR INTERNAL USE by intl2b)
     ****************************************************************************/
    {
        int ixout;

        for (ixout = 0; ixout
                < nxout; ixout++) {
            zout[ixout + offset_zout] = table[(icmax - ic) * 256 + temp1[ixout]];
            zout[ixout + offset_zout] += table[ic * 256 + temp2[ixout]];
        }

    }

    public void testIntl2b() {
        int nxin, nyin, nxout, nyout, ixout, iyout;
        float dxin, fxin, dyin, fyin, fxout, dxout, fyout, dyout;
        short zin[] = new short[4];
        short zout[] = new short[16];

        nxin =
                2;
        dxin =
                1.0f;
        fxin =
                0.0f;
        nyin =
                2;
        dyin =
                1.0f;
        fyin =
                0.0f;
        nxout =
                4;
        dxout =
                dxin * (nxin - 1) / (nxout - 1);
        fxout =
                0.0f;
        nyout =
                4;
        dyout =
                dyin * (nyin - 1) / (nyout - 1);
        fyout =
                0.0f;

        zin[0 * nxin + 0] = 41;
        zin[0 * nxin + 1] = 99;
        zin[1 * nxin + 0] = 99;
        zin[1 * nxin + 1] = 99;

        intl2b(nxin, dxin, fxin, nyin, dyin, fyin, zin,
                nxout, dxout, fxout, nyout, dyout, fyout, zout);
        for (iyout = 0; iyout
                < nyout; iyout++) {
            for (ixout = 0; ixout
                    < nxout; ixout++) {
                System.out.println(String.format("zout[%d][%d] = %d\n",
                        iyout, ixout, zout[iyout * nxout + ixout]));
            }

        }
    }
//    class ZoomParameters {
//
//        public int nxb;
//        public int nyb;
//        public int ixb;
//        public int iyb;
//        public float x1b;
//        public float x2b;
//        public float y1b;
//        public float y2b;
//    }
    //
    GraphicsConfiguration m_gc = null;
    BufferedImage m_img;
    float m_wclip;
    float m_bclip;
    float m_perc;
    float m_bperc;
    float m_clip;
    float m_wperc;
    int m_i1beg;
    int m_i1end;
    int m_i2beg;
    int m_i2end;
    int m_n1c;
    int m_n2c;
    int m_balance;
    boolean m_autoClip;
    boolean m_autoBClip;
    boolean m_autoWClip;
    boolean m_autoPerc;
    boolean m_autoBPerc;
    boolean m_autoWPerc;
    boolean m_autoBalance;
    public static final int LSBFirst = 0;
    private SVColorMaps m_cmap = new SVColorMaps();
    private int m_bytesPerPixel = 3;
    private int m_byteOrder = LSBFirst;
    // Colormap
    public static final int RGB = 0;
    public static final int HSV = 1;
    private int m_colormapType = RGB;
    private int m_currColormapIndex = 2;
    //
    public int m_nx;
    public int m_ny;
    public int m_nxb;
    private int m_nyb;
    private int m_ixb;
    private int m_iyb;
    short[] m_czbi;
    short[] m_czb;
    short[] m_cz;
    // Constants
    private final int ICMAX = 99; /* must be odd, so that ICMAC-ic!=ic, for ic=0 to ICMAX/2! */

    public final int NTABLE = ICMAX + 1;
    public final int HSV_MAX = 13;
    public final int RGB_MAX = 11;
}
