package usrdata;
/*
 * SUHeader.java
 *
 * Created on 23 de Agosto de 2007, 19:51
 *
 * Project: BotoSeis
 * 
 * Federal University of Para.
 * Department of Geophysics
 */

import java.io.*;

/**
 * The SUHeader class represents a SU(segy) trace header.
 * 
 * @author Williams Lima
 */
public class SUHeader {

    /** Creates a new instance of SUHeader */
    public SUHeader() {
        scalco = 0;
        tracl = 0;
        tracr = 0;
        fldr = 0;
        tracf = 0;
        ep = 0;
        cdp = 0;
        cdpt = 0;
        trid = 0;
        nvs = 0;
        nhs = 0;
        duse = 0;
        offset = 0;
        gelev = 0;
        selev = 0;
        sdepth = 0;
        gdel = 0;
        sdel = 0;
        swdep = 0;
        gwdep = 0;
        scalel = 0;
        sx = 0;
        sy = 0;
        gx = 0;
        gy = 0;
        counit = 0;
        wevel = 0;
        swevel = 0;
        sut = 0;
        gut = 0;
        sstat = 0;
        gstat = 0;
        tstat = 0;
        laga = 0;
        lagb = 0;
        delrt = 0;
        muts = 0;
        mute = 0;
        ns = 0;
        dt = 0;
        gain = 0;
        igc = 0;
        igi = 0;
        corr = 0;
        sfs = 0;
        sfe = 0;
        slen = 0;
        styp = 0;
        stas = 0;
        stae = 0;
        tatyp = 0;
        afilf = 0;
        afils = 0;
        /*     nofilf;
        nofils;
        lcf;
        hcf;
        lcs;
        hcs;
        year;
        day;
        hour;
        minute;
        sec;
        timbas;
        trwf;
        grnors;
        grnofr;
        grnlof;
        gaps;
        otrav;
        d1;
        f1;
        d2;
        f2;
        ungpow;
        unscale;
         */
        ntr = 0;
        mark = 0;
        shortpad = 0;
    }

    public void readFromFile(InputStream pIn) {
        tracl = NumericIO.readInt(pIn);
        tracr = NumericIO.readInt(pIn);
        fldr = NumericIO.readInt(pIn);
        tracf = NumericIO.readInt(pIn);
        ep = NumericIO.readInt(pIn);
        cdp = NumericIO.readInt(pIn);
        cdpt = NumericIO.readInt(pIn);
        trid = NumericIO.readShort(pIn);
        nvs = NumericIO.readShort(pIn);
        nhs = NumericIO.readShort(pIn);
        duse = NumericIO.readShort(pIn);
        offset = NumericIO.readInt(pIn);
        gelev = NumericIO.readInt(pIn);
        selev = NumericIO.readInt(pIn);
        sdepth = NumericIO.readInt(pIn);
        gdel = NumericIO.readInt(pIn);
        sdel = NumericIO.readInt(pIn);
        swdep = NumericIO.readInt(pIn);
        gwdep = NumericIO.readInt(pIn);
        scalel = NumericIO.readShort(pIn);
        scalco = NumericIO.readShort(pIn);
        sx = NumericIO.readInt(pIn);
        sy = NumericIO.readInt(pIn);
        gx = NumericIO.readInt(pIn);
        gy = NumericIO.readInt(pIn);
        counit = NumericIO.readShort(pIn);
        wevel = NumericIO.readShort(pIn);
        swevel = NumericIO.readShort(pIn);
        sut = NumericIO.readShort(pIn);
        gut = NumericIO.readShort(pIn);
        sstat = NumericIO.readShort(pIn);
        gstat = NumericIO.readShort(pIn);
        tstat = NumericIO.readShort(pIn);
        laga = NumericIO.readShort(pIn);
        lagb = NumericIO.readShort(pIn);
        delrt = NumericIO.readShort(pIn);
        muts = NumericIO.readShort(pIn);
        mute = NumericIO.readShort(pIn);
        ns = NumericIO.readUShort(pIn);
        dt = NumericIO.readUShort(pIn);
        gain = NumericIO.readShort(pIn);
        igc = NumericIO.readShort(pIn);
        igi = NumericIO.readShort(pIn);
        corr = NumericIO.readShort(pIn);
        sfs = NumericIO.readShort(pIn);
        sfe = NumericIO.readShort(pIn);
        slen = NumericIO.readShort(pIn);
        styp = NumericIO.readShort(pIn);
        stas = NumericIO.readShort(pIn);
        stae = NumericIO.readShort(pIn);
        tatyp = NumericIO.readShort(pIn);
        afilf = NumericIO.readShort(pIn);
        afils = NumericIO.readShort(pIn);
        nofilf = NumericIO.readShort(pIn);
        nofils = NumericIO.readShort(pIn);
        lcf = NumericIO.readShort(pIn);
        hcf = NumericIO.readShort(pIn);
        lcs = NumericIO.readShort(pIn);
        hcs = NumericIO.readShort(pIn);
        year = NumericIO.readShort(pIn);
        day = NumericIO.readShort(pIn);
        hour = NumericIO.readShort(pIn);
        minute = NumericIO.readShort(pIn);
        sec = NumericIO.readShort(pIn);
        timbas = NumericIO.readShort(pIn);
        trwf = NumericIO.readShort(pIn);
        grnors = NumericIO.readShort(pIn);
        grnofr = NumericIO.readShort(pIn);
        grnlof = NumericIO.readShort(pIn);
        gaps = NumericIO.readShort(pIn);
        otrav = NumericIO.readShort(pIn); //
        /* cwp local assignments */
        d1 = NumericIO.readFloat(pIn);
        f1 = NumericIO.readFloat(pIn);
        d2 = NumericIO.readFloat(pIn);
        f2 = NumericIO.readFloat(pIn);
        ungpow = NumericIO.readFloat(pIn);
        unscale = NumericIO.readFloat(pIn);
        ntr = NumericIO.readInt(pIn);
        mark = NumericIO.readShort(pIn);
        shortpad = NumericIO.readShort(pIn);

        for (int i = 0; i < 14; i++) {
            unass[i] = NumericIO.readShort(pIn);
        }

    }
    public void readFromFileXDR(InputStream pIn) {
        tracl = NumericIO.readSwapInt(pIn);   
        tracr = NumericIO.readSwapInt(pIn);
        fldr = NumericIO.readSwapInt(pIn);
        tracf = NumericIO.readSwapInt(pIn);
        ep = NumericIO.readSwapInt(pIn);
        cdp = NumericIO.readSwapInt(pIn);
        cdpt = NumericIO.readSwapInt(pIn);
        trid = NumericIO.readSwapShort(pIn);
        nvs = NumericIO.readSwapShort(pIn);
        nhs = NumericIO.readSwapShort(pIn);
        duse = NumericIO.readSwapShort(pIn);
        offset = NumericIO.readSwapInt(pIn);
        gelev = NumericIO.readSwapInt(pIn);
        selev = NumericIO.readSwapInt(pIn);
        sdepth = NumericIO.readSwapInt(pIn);
        gdel = NumericIO.readSwapInt(pIn);
        sdel = NumericIO.readSwapInt(pIn);
        swdep = NumericIO.readSwapInt(pIn);
        gwdep = NumericIO.readSwapInt(pIn);
        scalel = NumericIO.readSwapShort(pIn);
        scalco = NumericIO.readSwapShort(pIn);
        sx = NumericIO.readSwapInt(pIn);
        sy = NumericIO.readSwapInt(pIn);
        gx = NumericIO.readSwapInt(pIn);
        gy = NumericIO.readSwapInt(pIn);
        counit = NumericIO.readSwapShort(pIn);
        wevel = NumericIO.readSwapShort(pIn);
        swevel = NumericIO.readSwapShort(pIn);
        sut = NumericIO.readSwapShort(pIn);
        gut = NumericIO.readSwapShort(pIn);
        sstat = NumericIO.readSwapShort(pIn);
        gstat = NumericIO.readSwapShort(pIn);
        tstat = NumericIO.readSwapShort(pIn);
        laga = NumericIO.readSwapShort(pIn);
        lagb = NumericIO.readSwapShort(pIn);
        delrt = NumericIO.readSwapShort(pIn);
        muts = NumericIO.readSwapShort(pIn);
        mute = NumericIO.readSwapShort(pIn);
        ns = NumericIO.readSwapUShort(pIn);
        dt = NumericIO.readSwapUShort(pIn);
        gain = NumericIO.readSwapShort(pIn);
        igc = NumericIO.readSwapShort(pIn);
        igi = NumericIO.readSwapShort(pIn);
        corr = NumericIO.readSwapShort(pIn);
        sfs = NumericIO.readSwapShort(pIn);
        sfe = NumericIO.readSwapShort(pIn);
        slen = NumericIO.readSwapShort(pIn);
        styp = NumericIO.readSwapShort(pIn);
        stas = NumericIO.readSwapShort(pIn);
        stae = NumericIO.readSwapShort(pIn);
        tatyp = NumericIO.readSwapShort(pIn);
        afilf = NumericIO.readSwapShort(pIn);
        afils = NumericIO.readSwapShort(pIn);
        nofilf = NumericIO.readSwapShort(pIn);
        nofils = NumericIO.readSwapShort(pIn);
        lcf = NumericIO.readSwapShort(pIn);
        hcf = NumericIO.readSwapShort(pIn);
        lcs = NumericIO.readSwapShort(pIn);
        hcs = NumericIO.readSwapShort(pIn);
        year = NumericIO.readSwapShort(pIn);
        day = NumericIO.readSwapShort(pIn);
        hour = NumericIO.readSwapShort(pIn);
        minute = NumericIO.readSwapShort(pIn);
        sec = NumericIO.readSwapShort(pIn);
        timbas = NumericIO.readSwapShort(pIn);
        trwf = NumericIO.readSwapShort(pIn);
        grnors = NumericIO.readSwapShort(pIn);
        grnofr = NumericIO.readSwapShort(pIn);
        grnlof = NumericIO.readSwapShort(pIn);
        gaps = NumericIO.readSwapShort(pIn);
        otrav = NumericIO.readSwapShort(pIn); //
        /* cwp local assignments */
        d1 = NumericIO.readSwapFloat(pIn);
        f1 = NumericIO.readSwapFloat(pIn);
        d2 = NumericIO.readSwapFloat(pIn);
        f2 = NumericIO.readSwapFloat(pIn);
        ungpow = NumericIO.readSwapFloat(pIn);
        unscale = NumericIO.readSwapFloat(pIn);
        ntr = NumericIO.readSwapInt(pIn);
        mark = NumericIO.readSwapShort(pIn);
        shortpad = NumericIO.readSwapShort(pIn);

        for (int i = 0; i < 14; i++) {
            unass[i] = NumericIO.readSwapShort(pIn);
        }

    }

    public void writeToFile(FileOutputStream pOut) {
        NumericIO.writeInt(pOut, tracl);
        NumericIO.writeInt(pOut, tracr);
        NumericIO.writeInt(pOut, fldr);
        NumericIO.writeInt(pOut, tracf);
        NumericIO.writeInt(pOut, ep);
        NumericIO.writeInt(pOut, cdp);
        NumericIO.writeInt(pOut, cdpt);
        NumericIO.writeShort(pOut, trid);
        NumericIO.writeShort(pOut, nvs);
        NumericIO.writeShort(pOut, nhs);
        NumericIO.writeShort(pOut, duse);
        NumericIO.writeInt(pOut, offset);
        NumericIO.writeInt(pOut, gelev);
        NumericIO.writeInt(pOut, selev);
        NumericIO.writeInt(pOut, sdepth);
        NumericIO.writeInt(pOut, gdel);
        NumericIO.writeInt(pOut, sdel);
        NumericIO.writeInt(pOut, swdep);
        NumericIO.writeInt(pOut, gwdep);
        NumericIO.writeShort(pOut, scalel);
        NumericIO.writeShort(pOut, scalco);
        NumericIO.writeInt(pOut, sx);
        NumericIO.writeInt(pOut, sy);
        NumericIO.writeInt(pOut, gx);
        NumericIO.writeInt(pOut, gy);
        NumericIO.writeShort(pOut, counit);
        NumericIO.writeShort(pOut, wevel);
        NumericIO.writeShort(pOut, swevel);
        NumericIO.writeShort(pOut, sut);
        NumericIO.writeShort(pOut, gut);
        NumericIO.writeShort(pOut, sstat);
        NumericIO.writeShort(pOut, gstat);
        NumericIO.writeShort(pOut, tstat);
        NumericIO.writeShort(pOut, laga);
        NumericIO.writeShort(pOut, lagb);
        NumericIO.writeShort(pOut, delrt);
        NumericIO.writeShort(pOut, muts);
        NumericIO.writeShort(pOut, mute);
        NumericIO.writeUShort(pOut, ns); // unsigned
        NumericIO.writeUShort(pOut, dt); // unsigned
        NumericIO.writeShort(pOut, gain);
        NumericIO.writeShort(pOut, igc);
        NumericIO.writeShort(pOut, igi);
        NumericIO.writeShort(pOut, corr);
        NumericIO.writeShort(pOut, sfs);
        NumericIO.writeShort(pOut, sfe);
        NumericIO.writeShort(pOut, slen);
        NumericIO.writeShort(pOut, styp);
        NumericIO.writeShort(pOut, stas);
        NumericIO.writeShort(pOut, stae);
        NumericIO.writeShort(pOut, tatyp);
        NumericIO.writeShort(pOut, afilf);
        NumericIO.writeShort(pOut, afils);
        NumericIO.writeShort(pOut, nofilf);
        NumericIO.writeShort(pOut, nofils);
        NumericIO.writeShort(pOut, lcf);
        NumericIO.writeShort(pOut, hcf);
        NumericIO.writeShort(pOut, lcs);
        NumericIO.writeShort(pOut, hcs);
        NumericIO.writeShort(pOut, year);
        NumericIO.writeShort(pOut, day);
        NumericIO.writeShort(pOut, hour);
        NumericIO.writeShort(pOut, minute);
        NumericIO.writeShort(pOut, sec);
        NumericIO.writeShort(pOut, timbas);
        NumericIO.writeShort(pOut, trwf);
        NumericIO.writeShort(pOut, grnors);
        NumericIO.writeShort(pOut, grnofr);
        NumericIO.writeShort(pOut, grnlof);
        NumericIO.writeShort(pOut, gaps);
        NumericIO.writeShort(pOut, otrav);
        /* cwp local assignments */
        NumericIO.writeFloat(pOut, d1);
        NumericIO.writeFloat(pOut, f1);
        NumericIO.writeFloat(pOut, d2);
        NumericIO.writeFloat(pOut, f2);
        NumericIO.writeFloat(pOut, ungpow);
        NumericIO.writeFloat(pOut, unscale);
        NumericIO.writeInt(pOut, ntr);
        NumericIO.writeShort(pOut, mark);
        NumericIO.writeShort(pOut, shortpad);

        for (int i = 0; i < 14; i++) {
            NumericIO.writeShort(pOut, unass[i]);
        }

    }
    public void writeToFileXDR(FileOutputStream pOut) {
        NumericIO.writeSwapInt(pOut, tracl);
        NumericIO.writeSwapInt(pOut, tracr);
        NumericIO.writeSwapInt(pOut, fldr);
        NumericIO.writeSwapInt(pOut, tracf);
        NumericIO.writeSwapInt(pOut, ep);
        NumericIO.writeSwapInt(pOut, cdp);
        NumericIO.writeSwapInt(pOut, cdpt);
        NumericIO.writeSwapShort(pOut, trid);
        NumericIO.writeSwapShort(pOut, nvs);
        NumericIO.writeSwapShort(pOut, nhs);
        NumericIO.writeSwapShort(pOut, duse);
        NumericIO.writeSwapInt(pOut, offset);
        NumericIO.writeSwapInt(pOut, gelev);
        NumericIO.writeSwapInt(pOut, selev);
        NumericIO.writeSwapInt(pOut, sdepth);
        NumericIO.writeSwapInt(pOut, gdel);
        NumericIO.writeSwapInt(pOut, sdel);
        NumericIO.writeSwapInt(pOut, swdep);
        NumericIO.writeSwapInt(pOut, gwdep);
        NumericIO.writeSwapShort(pOut, scalel);
        NumericIO.writeSwapShort(pOut, scalco);
        NumericIO.writeSwapInt(pOut, sx);
        NumericIO.writeSwapInt(pOut, sy);
        NumericIO.writeSwapInt(pOut, gx);
        NumericIO.writeSwapInt(pOut, gy);
        NumericIO.writeSwapShort(pOut, counit);
        NumericIO.writeSwapShort(pOut, wevel);
        NumericIO.writeSwapShort(pOut, swevel);
        NumericIO.writeSwapShort(pOut, sut);
        NumericIO.writeSwapShort(pOut, gut);
        NumericIO.writeSwapShort(pOut, sstat);
        NumericIO.writeSwapShort(pOut, gstat);
        NumericIO.writeSwapShort(pOut, tstat);
        NumericIO.writeSwapShort(pOut, laga);
        NumericIO.writeSwapShort(pOut, lagb);
        NumericIO.writeSwapShort(pOut, delrt);
        NumericIO.writeSwapShort(pOut, muts);
        NumericIO.writeSwapShort(pOut, mute);
        NumericIO.writeSwapUShort(pOut, ns); // unsigned
        NumericIO.writeSwapUShort(pOut, dt); // unsigned
        NumericIO.writeSwapShort(pOut, gain);
        NumericIO.writeSwapShort(pOut, igc);
        NumericIO.writeSwapShort(pOut, igi);
        NumericIO.writeSwapShort(pOut, corr);
        NumericIO.writeSwapShort(pOut, sfs);
        NumericIO.writeSwapShort(pOut, sfe);
        NumericIO.writeSwapShort(pOut, slen);
        NumericIO.writeSwapShort(pOut, styp);
        NumericIO.writeSwapShort(pOut, stas);
        NumericIO.writeSwapShort(pOut, stae);
        NumericIO.writeSwapShort(pOut, tatyp);
        NumericIO.writeSwapShort(pOut, afilf);
        NumericIO.writeSwapShort(pOut, afils);
        NumericIO.writeSwapShort(pOut, nofilf);
        NumericIO.writeSwapShort(pOut, nofils);
        NumericIO.writeSwapShort(pOut, lcf);
        NumericIO.writeSwapShort(pOut, hcf);
        NumericIO.writeSwapShort(pOut, lcs);
        NumericIO.writeSwapShort(pOut, hcs);
        NumericIO.writeSwapShort(pOut, year);
        NumericIO.writeSwapShort(pOut, day);
        NumericIO.writeSwapShort(pOut, hour);
        NumericIO.writeSwapShort(pOut, minute);
        NumericIO.writeSwapShort(pOut, sec);
        NumericIO.writeSwapShort(pOut, timbas);
        NumericIO.writeSwapShort(pOut, trwf);
        NumericIO.writeSwapShort(pOut, grnors);
        NumericIO.writeSwapShort(pOut, grnofr);
        NumericIO.writeSwapShort(pOut, grnlof);
        NumericIO.writeSwapShort(pOut, gaps);
        NumericIO.writeSwapShort(pOut, otrav);
        /* cwp local assignments */
        NumericIO.writeSwapFloat(pOut, d1);
        NumericIO.writeSwapFloat(pOut, f1);
        NumericIO.writeSwapFloat(pOut, d2);
        NumericIO.writeSwapFloat(pOut, f2);
        NumericIO.writeSwapFloat(pOut, ungpow);
        NumericIO.writeSwapFloat(pOut, unscale);
        NumericIO.writeSwapInt(pOut, ntr);
        NumericIO.writeSwapShort(pOut, mark);
        NumericIO.writeSwapShort(pOut, shortpad);

        for (int i = 0; i < 14; i++) {
            NumericIO.writeSwapShort(pOut, unass[i]);
        }

    }
    /* header keys */
    public int tracl;
    public int tracr;
    public int fldr;
    public int tracf;
    public int ep;
    public int cdp;
    public int cdpt;
    public short trid;
    public short nvs;
    public short nhs;
    public short duse;
    public int offset;
    public int gelev;
    public int selev;
    public int sdepth;
    public int gdel;
    public int sdel;
    public int swdep;
    public int gwdep;
    public short scalel;
    public short scalco;
    public int sx;
    public int sy;
    public int gx;
    public int gy;
    public short counit;
    public short wevel;
    public short swevel;
    public short sut;
    public short gut;
    public short sstat;
    public short gstat;
    public short tstat;
    public short laga;
    public short lagb;
    public short delrt;
    public short muts;
    public short mute;
    public char ns; // unsigned short
    public char dt; // unsigned short
    public short gain;
    public short igc;
    public short igi;
    public short corr;
    public short sfs;
    public short sfe;
    public short slen;
    public short styp;
    public short stas;
    public short stae;
    public short tatyp;
    public short afilf;
    public short afils;
    public short nofilf;
    public short nofils;
    public short lcf;
    public short hcf;
    public short lcs;
    public short hcs;
    public short year;
    public short day;
    public short hour;
    public short minute;
    public short sec;
    public short timbas;
    public short trwf;
    public short grnors;
    public short grnofr;
    public short grnlof;
    public short gaps;
    public short otrav;
    /* cwp local assignments */
    public float d1;
    public float f1;
    public float d2;
    public float f2;
    public float ungpow;
    public float unscale;
    public int ntr;
    public short mark;
    public short shortpad;
    public short unass[] = new short[14];

    public int getValue(String pkey) {
        if (pkey == null) {
            return fldr;
        }
        if (pkey.equals("cdp")) {
            return cdp;
        } else {
            if (pkey.equals("offset")) {
                return offset;
            } else {
                if (pkey.equals("tracf")) {
                    return tracf;
                } else {
                    if (pkey.equals("ep")) {
                        return ep;
                    } else {
                        if (pkey.equals("fldr")) {
                            return fldr;
                        }
                    }
                }
            }

        }
        return fldr;
    }
}
