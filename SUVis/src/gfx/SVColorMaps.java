/*
 * SciVisColormapRGB.java
 *
 * Created on 12 de Agosto de 2007, 08:36
 *
 * Project: BotoSeis
 * 
 * Federal University of Para, Brazil.
 * Department of Geophysics
 */
package gfx;

import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;
import utils.Sorting;

/**
 * The SVColorMaps class represents various colormaps that
 * can be used for data visualization using color scale like
 * the SU program suximage.
 * 
 * @author Williams Lima
 */
public class SVColorMaps {

    /** Creates a new instance of SciVisColormapRGB */
    public SVColorMaps() {
    }

  
    
    public float getMinimumDataValue()
    {
	float min = 1.0E8f;
	for(int i = 0; i < m_data.length; i++){
	    if(min > m_data[i]){
		min = m_data[i];
	    }
	}
	
	return min;
    }

    public float getMaximumDataValue()
    {
	float max = -1.0E8f;
	for(int i = 0; i < m_data.length; i++){
	    if(max < m_data[i]){
		max = m_data[i];
	    }
	}

	return max;
    }

    public void increaseBclip()
    {
	float max = getMaximumDataValue();
	float min = getMinimumDataValue();

	m_bclip += Math.abs(max - min)/20.0f;

	if(m_bclip > max){
	    m_bclip = max;
	}

	m_autoBClip = false;

	dataToByte(m_data);

	m_imageOutOfDate = true;
    }

    public void decreaseBclip()
    {
	float max = getMaximumDataValue();
	float min = getMinimumDataValue();

	m_bclip -= Math.abs(max - min)/20.0f;

	if(m_bclip < min){
	    m_bclip = min;
	}
	
	m_autoBClip = false;

	dataToByte(m_data);

	m_imageOutOfDate = true;
    }

    public void increaseWclip()
    {
	float max = getMaximumDataValue();
	float min = getMinimumDataValue();

	m_wclip += Math.abs(max - min)/20.0f;

	if(m_wclip > max){
	    m_wclip = max;
	}

	m_autoWClip = false;

	dataToByte(m_data);

	m_imageOutOfDate = true;
    }

    public void decreaseWclip()
    {
	float max = getMaximumDataValue();
	float min = getMinimumDataValue();

	m_wclip -= Math.abs(max - min)/20.0f;

	if(m_wclip < min){
	    m_wclip = min;
	}
	
	m_autoWClip = false;

	dataToByte(m_data);

	m_imageOutOfDate = true;
    }

    public void setBclip(float value)
    {
	m_bclip = value;
	m_autoBClip = false;

	dataToByte(m_data);

	m_imageOutOfDate = true;
    }

    public float getBclip()
    {
	return m_bclip;
    }

    public void setWclip(float value)
    {
	m_wclip = value;
	m_autoWClip = false;

	dataToByte(m_data);

	m_imageOutOfDate = true;
    }

    public float getWclip()
    {
	return m_wclip;
    }

    public void setPerc(float perc){
	m_autoPerc = false;
	m_perc = perc;
	dataToByte(m_data);
	
        m_imageOutOfDate = true;
    }

    public void scaleData(float value)
    {
	float [] tmp = new float[m_data.length];
	for(int i = 0; i < m_data.length; i++){
	    tmp[i] = m_data[i]*value;
	}
	dataToByte(tmp);
    }
    
    public void setViewport(float xmin, float xmax, float ymin, float ymax) {
	
	if (m_style == SEISMIC) {
            m_x1begb = ymin;
            m_x1endb = ymax;
            m_x2begb = xmin;
            m_x2endb = xmax;
        } else {
            m_x1begb = xmin;
            m_x1endb = xmax;
            m_x2begb = ymin;
            m_x2endb = ymax;
        }

	if(m_data != null){
	    dataToByte(m_data);
	}

        m_imageOutOfDate = true;

    }


   

    public void clear(){
	m_data = null;
	m_czb = null;
	m_czbi = null;
	m_img = null;
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
    
    public void setStyle(int s) {
        if ((m_style != s) && (m_data != null)) {
            m_style = s;
            //dataToByte();
        }
	
    }

    public float getDataAt(float x1, float x2) {
        float ret = 0.0f;
	
	return ret;
    }
    
    public void updateData(float [][] pData, int idata, float pF1, float pD1, int pN1, float pF2, float pD2, int pN2) {
	
	int ndata = pN1*pN2;

	if(m_data == null){
	    m_data = new float[ndata];
	}else if((m_n1*m_n2) != ndata){
	    m_data = new float[ndata];
	}

	for(int i = 0; i < ndata; i++){
	    m_data[i] = pData[idata][i];
	}

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
	
	m_bclip = getMaximumDataValue();
	m_wclip = getMinimumDataValue();
	
        dataToByte(m_data);
	
        m_imageOutOfDate = true;
	
    }
    
    public void updateData(float [] pData, float pF1, float pD1, int pN1, float pF2, float pD2, int pN2) {
	
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
	
	if(m_data == null){
	    m_data = new float[pData.length];
	}else if(m_data.length != pData.length){
	    m_data = new float[pData.length];
	}

	for(int i = 0; i < pData.length; i++){
	    m_data[i] = pData[i];
	}
	
	m_bclip = getMaximumDataValue();
	m_wclip = getMinimumDataValue();
	
        dataToByte(pData);
	
        m_imageOutOfDate = true;
	
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

    public static int getRGBMax(){
	return RGB_MAX;
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
    
    public static int getHSVMax(){
	return HSV_MAX;
    }

    public void nextColormap() {
        m_currColormapIndex++;
	
        if (m_currColormapIndex > RGB_MAX) {
            m_currColormapIndex = 0;
        }
	
        m_imageOutOfDate = true;
    }
    
    public void previousColormap() {
        m_currColormapIndex--;
	
        if (m_currColormapIndex < 0) {
            m_currColormapIndex = RGB_MAX;
        }
	
        m_imageOutOfDate = true;
    }
    
    private void dataToByte(float [] pData) {
	try{
	    int iz, i1, i2, i1c, i2c, i1step, i2step;
	    float zscale, zoffset, zi;
	    
	    int nz = m_n1 * m_n2;
	    
	    float perc = 100.0f;
	    float bperc = 0;
	    float clip = 0;
	    float bclip = 0;
	    float wperc = 0;
	    float wclip = 0;
	    
	    //if (m_autoClip) {
		//float temp[] = new float[nz];
		if(m_temp == null){
		    m_temp = new float[nz];
		}else if(m_temp.length != nz){
		    m_temp = new float[nz];
		}
		
		int balance = 0;
		
		if (m_autoPerc) {
		    perc = 100.0f;
		} else {
		    perc = m_perc;
		}
		
		if (m_autoBalance) {
		    balance = 0;
		} else {
		    balance = m_balance;
		}
		
		if (balance == 0) {
		    for (iz = 0; iz < nz; ++iz) {
			m_temp[iz] = pData[iz];//m_data[iz];
		    }
		} else {
		    for (iz = 0; iz < nz; ++iz) {
			m_temp[iz] = Math.abs(pData[iz]/*m_data[iz]*/);
		    }
		}
		
		/* End of modded code */
		if (m_autoBClip) {
		    bperc = perc;
		    if (!m_autoBPerc) {
			bperc = m_bperc;
		    }
		    
		    iz = (int) (nz * bperc / 100.0f);
		    if (iz < 0) {
			iz = 0;
		    }
		    if (iz > nz - 1) {
			iz = nz - 1;
		    }
		    Sorting.qkfind(iz, nz, m_temp);
		    bclip = m_temp[iz];
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
		    Sorting.qkfind(iz, nz, m_temp);
		    /* Modded by GCP to balance bclip & wclip */
		    if (balance == 0) {
			wclip = m_temp[iz];
		    } else {
			wclip = -1 * bclip;
		    }
		    /* End of modded code */
		} else {
		    wclip = m_wclip;
		}
		m_wclip = wclip;
		//	    } else {
		//m_bclip = bclip;
		//m_wclip = -clip;
		//	    }
	    
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
	    
	    if(m_cz == null){
		m_cz = new short[m_n1c * m_n2c];
	    }else if(m_cz.length != m_n1c*m_n2c){
		m_cz = new short[m_n1c * m_n2c];
	    }
	    
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
		for (i2c = 0, i2 = m_i2beg; i2c < m_n2c; i2c++, i2 += i2step) {
		    czp = m_n1c * m_n2c - (i2c + 1) * m_n1c;
		    for (i1c = 0, i1 = m_i1beg; i1c < m_n1c; i1c++, i1 += i1step) {
			zi = zoffset + /*m_data*/pData[i1 + i2 * m_n1] * zscale;
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
		for (i1c = 0, i1 = m_i1beg; i1c < m_n1c; i1c++, i1 += i1step) {
		    for (i2c = 0, i2 = m_i2beg; i2c < m_n2c; i2c++, i2 += i2step) {
			zi = zoffset + /*m_data*/pData[i1 + i2 * m_n1] * zscale;
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
	}catch(OutOfMemoryError e){
	    javax.swing.JOptionPane.showMessageDialog(null, "Out of memory.");
	    
	}
    }
    
    public BufferedImage createBufferedImage(short pBytes[], int pWidth, int pHeight, int cmap) {
        BufferedImage img = null;

        if ((pWidth > 0) && (pHeight > 0)) {
            int i;
            int rgbArray[] = new int[pWidth * pHeight];
            int [] tcpixels = null;
            if (m_colormapType == RGB) {
                tcpixels = getRGBTrueColorPixels(cmap);
            } else {
                tcpixels = getHSVTrueColorPixels(cmap);
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
	    
            img = m_gc.createCompatibleImage(pWidth, pHeight, BufferedImage.TYPE_INT_RGB);
	    img.setRGB(0, 0, pWidth, pHeight, rgbArray, 0, pWidth);
        }
        return img;
    }
    
    private short[] newInterpBytes(int n1in, int n2in, short bin[], int n1out,
				   int n2out, boolean useBlockInterp) /* JG */ {
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
    
    private short[] intl2b_block(int nxin, float dxin, float fxin,
				 int nyin, float dyin, float fyin, short zin[],
				 int nxout, float dxout, float fxout,
				 int nyout, float dyout, float fyout) {
        
	int ixout, iyout, iin, jin;
        float xoff, yoff;
        short zout[] = new short[nxout * nyout];
	
        xoff = fxout + 0.5f * dxin - fxin;
        yoff = fyout + 0.5f * dyin - fyin;
        for (iyout = 0; iyout < nyout; iyout++) {
            jin = (int) ((iyout * dyout + yoff) / dyin);
            jin = (int) MIN(nyin - 1, MAX(jin, 0));
            for (ixout = 0; ixout < nxout; ixout++) {
                iin = (int) ((ixout * dxout + xoff) / dxin);
                iin = (int) MIN(nxin - 1, MAX(iin, 0));
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
			int nyout, float dyout, float fyout, short zout[]) {
        
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
            for (ic = 0; ic <= ICMAX / 2; ++ic) {
                frac = (float) (ic) / (float) ICMAX;
                for (ib = 0; ib < 256; ++ib) {
                    table[ic * 256 + ib] = (short) (frac * ib);
                    table[(ICMAX - ic) * 256 + ib] = (short) (ib - table[ic * 256 + ib]);
                }
            }
            tabled = 1;
        }
	
        /* get workspace */
        kzin = new int[nxout];
        kic = new int[nxout];
	
        temp1 = new short[nxout];
        temp2 = new short[nxout];
	
        /* pre-compute indices for fast 1-D interpolation along x axis */
        for (ixout = 0, xout = fxout; ixout < nxout; ixout++, xout += dxout) {
            rxin = (xout - fxin) / dxin;
            if (rxin <= 0) {
                kzin[ixout] = 0;
                kic[ixout] = 0;
            } else if (rxin >= nxin - 1) {
                kzin[ixout] = nxin - 2;
                kic[ixout] = ICMAX * 256;
            } else {
                kzin[ixout] = (int) rxin;
                frac = rxin - (int) rxin;
                ic = (int) (frac * ICMAX + 0.5);
                kic[ixout] = ic * 256;
            }
        }
	
        /* loop over output y */
        for (iyout = 0, yout = fyout; iyout < nyout; iyout++, yout += dyout) {
            /* compute index of input y, clipped to range of input y */
            ryin = MAX(0, MIN(nyin - 1, (yout - fyin) / dyin));
            iyin = (int) MAX(0, MIN(nyin - 2, ryin));
	    
            /* if output y is not between current input y */
            if (iyin != iyinl || iyout == 0) {
                /* if 2nd temporary vector is still useful */
                if (iyin == iyinl + 1 && iyout != 0) {
                    /* swap 2nd and 1st temp; compute 2nd temp */
                    temp = temp1;
                    temp1 = temp2;
                    temp2 = temp;
                    intl2bx(nxout, kzin, kic, ICMAX, table, zin, (iyin + 1) * nxin, temp2, 0);
		    /* else if 1st temporary vector is still useful */
                } else if (iyin == iyinl - 1 && iyout != 0) {
                    /* swap 1st and 2nd temp; compute 1st temp */
                    temp = temp1;
                    temp1 = temp2;
                    temp2 = temp;
		    
                    intl2bx(nxout, kzin, kic, ICMAX, table, zin, iyin * nxin, temp1, 0);
		    
		    /* else if neither 1st or 2nd temp is useful */
                } else {
                    /* compute 1st and 2nd temporary vectors */
                    intl2bx(nxout, kzin, kic, ICMAX, table, zin, iyin * nxin, temp1, 0);
                    intl2bx(nxout, kzin, kic, ICMAX, table, zin, (iyin + 1) * nxin, temp2, 0);
                }
		
                /* remember last index of input y */
                iyinl = iyin;
            }
	    
            /* compute index of interpolation coefficient */
            frac = ryin - iyin;
            ic = (int) (frac * ICMAX + 0.5f);
	    
            /* linearly interpolate output vector by table lookup */
            intl2by(nxout, ic, ICMAX, table, temp1, temp2, zout, iyout * nxout);
        }
    }
    
    private void intl2bx(int nxout, int kzin[], int kic[], int icmax,
			 short table[], short zin[], int offset_zin, short zout[], int offset_zout) {

        int ixout, jzin, jic;
	
        for (ixout = 0; ixout < nxout; ixout++) {
            jzin = kzin[ixout];
            jic = kic[ixout];
            zout[ixout + offset_zout] = table[icmax * 256 + (int) zin[jzin + offset_zin] - jic];
            zout[ixout + offset_zout] += table[(int) zin[jzin + 1 + offset_zin] + jic];
        }
    }
    
    private void intl2by(int nxout, int ic, int icmax, short table[],
			 short temp1[], short temp2[], short zout[], int offset_zout) {

        int ixout;
	
        for (ixout = 0; ixout < nxout; ixout++) {
            zout[ixout + offset_zout] = table[(icmax - ic) * 256 + temp1[ixout]];
            zout[ixout + offset_zout] += table[ic * 256 + temp2[ixout]];
        }
    }
    
    public void testIntl2b() {
        int nxin, nyin, nxout, nyout, ixout, iyout;
        float dxin, fxin, dyin, fyin, fxout, dxout, fyout, dyout;
        short zin[] = new short[4];
        short zout[] = new short[16];
	
        nxin = 2;
        dxin = 1.0f;
        fxin = 0.0f;
        nyin = 2;
        dyin = 1.0f;
        fyin = 0.0f;
        nxout = 4;
        dxout = dxin * (nxin - 1) / (nxout - 1);
        fxout = 0.0f;
        nyout = 4;
        dyout = dyin * (nyin - 1) / (nyout - 1);
        fyout = 0.0f;
	
        zin[0 * nxin + 0] = 41;
        zin[0 * nxin + 1] = 99;
        zin[1 * nxin + 0] = 99;
        zin[1 * nxin + 1] = 99;
	
        intl2b(nxin, dxin, fxin, nyin, dyin, fyin, zin, nxout, dxout,
	       fxout, nyout, dyout, fyout, zout);
        for (iyout = 0; iyout < nyout; iyout++) {
            for (ixout = 0; ixout < nxout; ixout++) {
                System.out.println(String.format("zout[%d][%d] = %d\n",
						 iyout, ixout, zout[iyout * nxout + ixout]));
            }
	    
        }
    }

    /**
     * Colormaps definitions section
     *
     */
    int [] getRGBTrueColorPixels(int pCMap) {
        
        int red = 0;
        int green = 0;
        int blue = 0;
	
        /* Build the 1st ramp */
        int ih;
        int npixels = 256;
        int half = npixels/2;
        int truecolor_pixel[] = new int[256];
        
        for (ih = 0; ih < half; ++ih) {
            red = (int) (c_rgb[pCMap][0][0] + (c_rgb[pCMap][1][0] - c_rgb[pCMap][0][0]) *
			 ((float) ih) / ((float) half));
            green = (int) (c_rgb[pCMap][0][1] + (c_rgb[pCMap][1][1] - c_rgb[pCMap][0][1]) *
			   ((float) ih) / ((float) half));
            blue = (int) (c_rgb[pCMap][0][2] + (c_rgb[pCMap][1][2] - c_rgb[pCMap][0][2]) *
			  ((float) ih) / ((float) half));
	    
            truecolor_pixel[ih] = 0;
            truecolor_pixel[ih] = blue | (green << 8) | (red << 16);
        }
	
        /* Build the 2nd ramp */
        for (ih = half; ih < npixels; ++ih) {
            red = (int) (c_rgb[pCMap][1][0] + (c_rgb[pCMap][2][0] - c_rgb[pCMap][1][0]) *
			 ((float) (ih - half)) / ((float) half));
            green = (int) (c_rgb[pCMap][1][1] + (c_rgb[pCMap][2][1] - c_rgb[pCMap][1][1]) *
			   ((float) (ih - half)) / ((float) half));
            blue = (int) (c_rgb[pCMap][1][2] + (c_rgb[pCMap][2][2] - c_rgb[pCMap][1][2]) *
			  ((float) (ih - half)) / ((float) half));
	    
            truecolor_pixel[ih] = 0;
            truecolor_pixel[ih] = blue | (green << 8) | (red << 16);
        }
	
        return truecolor_pixel;
    }
    
    int [] getHSVTrueColorPixels(int pCMap) {        
	
        float [] hsv = new float[3];
        float [] rgb = new float[3];    
        
        /* Build the 1st ramp  */
        int ih;
        int npixels = 256;        
        int half = npixels/2;
        int [] truecolor_pixel = new int[npixels];
        
        int r,g,b;
        for (ih = 0; ih < half; ++ih) {
            hsv[0] = (c_hsv[pCMap][0][0] + (c_hsv[pCMap][1][0] - c_hsv[pCMap][0][0]) * ((float) ih) / ((float) half));
            hsv[1] = (c_hsv[pCMap][0][1] + (c_hsv[pCMap][1][1] - c_hsv[pCMap][0][1]) * ((float) ih) / ((float) half));
            hsv[2] = (c_hsv[pCMap][0][2] + (c_hsv[pCMap][1][2] - c_hsv[pCMap][0][2]) * ((float) ih) / ((float) half));
	    
            hsv2rgb(hsv, rgb);                     
            
            r = (int)(rgb[0]*255);
            g = (int)(rgb[1]*255);
            b = (int)(rgb[2]*255);
            truecolor_pixel[ih] = 0;
            truecolor_pixel[ih] = b | (g << 8) | (r << 16);
        }
	
        /* Build the 2nd ramp */
        for (ih = half; ih < npixels; ++ih) {
            hsv[0] = (c_hsv[pCMap][1][0] + (c_hsv[pCMap][2][0] - c_hsv[pCMap][1][0]) *
		      ((float) (ih - half)) / ((float) half));
            hsv[1] = (c_hsv[pCMap][1][1] + (c_hsv[pCMap][2][1] - c_hsv[pCMap][1][1]) *
		      ((float) (ih - half)) / ((float) half));
            hsv[2] = (c_hsv[pCMap][1][2] + (c_hsv[pCMap][2][2] - c_hsv[pCMap][1][2]) *
		      ((float) (ih - half)) / ((float) half));
            
            hsv2rgb(hsv, rgb);                     
            
            r = (int)(rgb[0]*255);
            g = (int)(rgb[1]*255);
            b = (int)(rgb[2]*255);
            
            truecolor_pixel[ih] = 0;
            truecolor_pixel[ih] = b | (g << 8) | (r << 16);
        }
	
        return truecolor_pixel;
    }
    
    void hsv2rgb(float [] in, float [] out) {
        float m1, m2;
	
        if (in[2] <= 0.5f) {
            m2 = in[2] * (1.0f + in[1]);
        } else {
            m2 = in[2] + in[1] - in[2] * in[1];
        }
        
        m1 = 2 * in[2] - m2;
	
        if (in[1] == .0f) {
            out[0] = out[1] = out[2] = in[2];
        }else{
            out[0] = rgbvalue(m1, m2, in[0] + 120.0f);
            out[1] = rgbvalue(m1, m2, in[0]);
            out[2] = rgbvalue(m1, m2, in[0] - 120.0f);
            
            if(out[0] > 1.0f){
                out[0] = 1.0f;
            }
            if(out[1] > 1.0f){
                out[1] = 1.0f;
            }
            if(out[2] > 1.0f){
                out[2] = 1.0f;
            }
        }
    }
    
    float rgbvalue(float n1, float n2, float hue) {
        while (hue > 360.0f) {
            hue -= 360.0f;
        }
        while (hue < 0.0f) {
            hue += 360.0f;
        }
        if (hue < 60.0f) {
            return n1 + (n2 - n1) * hue / 60.0f;
        } else if (hue < 180.0f) {
            return n2;
        } else if (hue < 240.0f) {
            return n1 + (n2 - n1) * (240.0f - hue) / 60.0f;
        }
	
        return n1;
    }
    
    /* End of colormaps definition section */
    
    // Variables declaration
    
    /* Colormaps */
    
    /* define hue, saturation, lightness values */
    private final float [] HSV_BLACK  = {0.0f, 0.00f, 0.00f};
    private final float [] HSV_GRAY   = {0.0f, 0.00f, 0.50f};
    private final float [] HSV_WHITE  = {0.0f, 0.00f, 1.00f};
    private final float [] HSV_HUE1   = {240.0f, 1.00f, 0.50f};
    private final float [] HSV_HUE2   = {120.0f, 1.00f, 0.50f};
    private final float [] HSV_HUE3   = {0.0f, 1.00f, 0.50f};
    private final float [] HSV_DRED   = {0.0f, 1.00f, 0.50f};
    private final float [] HSV_BROWN  = {30.0f, 1.00f, 0.30f};
    private final float [] HSV_GREEN  = {140.0f, 1.00f, 0.50f};
    private final float [] HSV_BLUE   = {240.0f, 1.00f, 0.70f};
    private final float [] HSV_YELLOW = {70.0f, 1.00f, 0.50f};

    private final float c_hsv[][][] = {
        {HSV_WHITE, HSV_GRAY, HSV_BLACK},
        {HSV_HUE1, HSV_HUE2, HSV_HUE3},
        {HSV_HUE3, HSV_HUE2, HSV_HUE1},
        {HSV_BROWN, HSV_GREEN, HSV_BLUE},
        {HSV_DRED, HSV_WHITE, HSV_BLUE},
        {HSV_BLUE, HSV_WHITE, HSV_DRED},
        {HSV_WHITE, HSV_DRED, HSV_BLUE},
        {HSV_WHITE, HSV_GREEN, HSV_BLUE},
        {HSV_BLUE, HSV_DRED, HSV_WHITE},
        {HSV_BLUE, HSV_GREEN, HSV_WHITE},
        {HSV_BLUE, HSV_WHITE, HSV_GREEN},
        {HSV_YELLOW, HSV_DRED, HSV_BROWN},
        {HSV_BROWN, HSV_DRED, HSV_YELLOW},
        {HSV_DRED, HSV_YELLOW, HSV_BROWN}};

    /* define red, green, blue values */
    private final int [] RGB_BLACK    = {0x00, 0x00, 0x00};
    private final int [] RGB_WHITE    = {0xff, 0xff, 0xff};
    private final int [] RGB_GRAY     = {0x80, 0x80, 0x80};
    private final int [] RGB_ORANGE   = {0xff, 0x80, 0x00};
    private final int [] RGB_RED      = {0xe0, 0x00, 0x50};
    private final int [] RGB_BLUE     = {0x00, 0x40, 0xc0};
    private final int [] RGB_GREEN    = {0x06, 0x5b, 0x3f};
    private final int [] RGB_BROWN    = {0x72, 0x5b, 0x3f};
    private final int [] RGB_REDBROWN = {0xa0, 0x40, 0x00};
    private final int [] RGB_GRAY2    = {0xb0, 0xb0, 0xb0};
    private final int [] RGB_LGRAY    = {0xf0, 0xf0, 0xf0};
    private final int [] RGB_LBLUE    = {0x55, 0x9c, 0xe0};
    private final int [] RGB_YELLOW   = {0xd0, 0xb0, 0x20};

    private final int c_rgb[][][] = {
        {RGB_BLACK, RGB_GRAY, RGB_WHITE},
        {RGB_RED, RGB_LGRAY, RGB_BLUE},
        {RGB_RED, RGB_LGRAY, RGB_GREEN},
        {RGB_BROWN, RGB_LGRAY, RGB_BLUE},
        {RGB_BROWN, RGB_LGRAY, RGB_GREEN},
        {RGB_REDBROWN, RGB_LGRAY, RGB_BLUE},
        {RGB_REDBROWN, RGB_LGRAY, RGB_GREEN},
        {RGB_ORANGE, RGB_LGRAY, RGB_BLUE},
        {RGB_ORANGE, RGB_LGRAY, RGB_GREEN},
        {RGB_BROWN, RGB_GRAY2, RGB_GREEN},
        {RGB_BROWN, RGB_GRAY2, RGB_BLUE},
        {RGB_BROWN, RGB_YELLOW, RGB_BLUE}};

    /* Other variables */

    GraphicsConfiguration m_gc = null;
    BufferedImage m_img;
    float m_wclip;
    float m_bclip;
    float m_perc;
    float m_bperc;
    float m_wperc;
   
    int m_style;
    boolean m_imageOutOfDate;
    float m_data[] = null;
    int m_n1;
    int m_n2;
    float m_f1;
    float m_f2;
    float m_d1;
    float m_d2;
    float m_x1beg;
    float m_x1end;
    float m_x2beg;
    float m_x2end;
    float m_x1begb;
    float m_x1endb;
    float m_x2begb;
    float m_x2endb;
    float m_p2beg;
    float m_p2end;    
    int m_x;
    int m_y;
    int m_width;
    int m_height;
    boolean m_isVisible = true;
    
    // Constants
    public static int SEISMIC = 1;
    public static int NORMAL = 0;

    public static final int COLORMAP = 1;

    static float [] m_temp = null;

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
    
    private int m_bytesPerPixel = 3;
    private int m_byteOrder = LSBFirst;
    
    // Colormap
    private static final int RGB = 0;
    private static final int HSV = 1;
    private int m_colormapType = RGB;
    private int m_currColormapIndex = 1;
    //
    public int m_nx;
    public int m_ny;
    public int m_nxb;
    private int m_nyb;
    private int m_ixb;
    private int m_iyb;
    
    short [] m_czbi = null;
    short [] m_czb = null;
    short [] m_cz = null;

    // Constants
    private final int ICMAX = 99; /* must be odd, so that ICMAC-ic!=ic, for ic=0 to ICMAX/2! */

    private final int NTABLE = ICMAX + 1;
    public static final int HSV_MAX = 13;
    public static final int RGB_MAX = 11;
}

