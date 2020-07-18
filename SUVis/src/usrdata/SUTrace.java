package usrdata;
/*
 * SUTrace.java
 *
 * Created on 25 de Agosto de 2007, 11:30
 *
 * Project: BotoSeis
 *
 * Federal University of Para. Department of Geophysics
 */

import java.io.*;

/**
 * The SUTrace class represents a SU trace (Header plus data)
 *
 * @author Williams Lima
 */
public class SUTrace {

    /**
     * Creates a new instance of SUTrace
     */
    public SUTrace() {
    }

    public void setData(float pData[]) {
        m_data = new float[pData.length];
        for (int i = 0; i < m_data.length; i++) {
            m_data[i] = pData[i];
        }
    }

    public float[] getData() {
        float ret[] = null;

        if (m_data != null) {
            if (m_data.length > 0) {
                ret = new float[m_data.length];
                for (int i = 0; i < ret.length; i++) {
                    ret[i] = m_data[i];
                }
            }
        }

        return ret;
    }

    public void setHeader(SUHeader pHeader) {
        m_header = pHeader;
    }

    public SUHeader getHeader() {
        return m_header;
    }

    public void readFromFile(InputStream pInput, boolean pSkipData) {
        try {
            m_header.readFromFile(pInput);
            char ns = m_header.ns;
            if (ns > 0) {
                if (pSkipData) {
                    pInput.skip(ns * Float.SIZE / 8);
                } else {
                    m_data = new float[ns];
                    for (char i = 0; i < ns; i++) {

                        m_data[i] = NumericIO.readFloat(pInput);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("SUTrace.readFromFile");
            System.out.println("\t" + e.toString());
        }
    }

    public void readFromFile(InputStream pInput, boolean pSkipData, boolean xdrFlag) {
        try {
            if (xdrFlag) {
                m_header.readFromFileXDR(pInput);
            } else {
                m_header.readFromFile(pInput);
            }
            char ns = m_header.ns;
            if (ns > 0) {
                if (pSkipData) {
                    pInput.skip(ns * Float.SIZE / 8);
                } else {
                    m_data = new float[ns];
                    for (char i = 0; i < ns; i++) {
                        if (xdrFlag) {
                            m_data[i] = NumericIO.readSwapFloat(pInput);
                        } else {
                            m_data[i] = NumericIO.readFloat(pInput);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("SUTrace.readFromFile");
            System.out.println("\t" + e.toString());
        }
    }

    public void writeToFile(FileOutputStream pOutput) {
        m_header.writeToFile(pOutput);
        char ns = m_header.ns;
        try {
            for (char i = 0; i < ns; i++) {
                NumericIO.writeFloat(pOutput, m_data[i]);
            }
        } catch (NullPointerException ex) {
        }
    }

    public void writeToFile(FileOutputStream pOutput, boolean xdrFlag) {
        if(xdrFlag){
            m_header.writeToFileXDR(pOutput);
        }else{
        m_header.writeToFile(pOutput);
        }
        char ns = m_header.ns;
        try {
            for (char i = 0; i < ns; i++) {
                if (xdrFlag) {
                    NumericIO.writeSwapFloat(pOutput, m_data[i]);
                } else {
                    NumericIO.writeFloat(pOutput, m_data[i]);
                }
            }
        } catch (NullPointerException ex) {
        }
    }
    // Variables declaration
    private SUHeader m_header = new SUHeader();
    private float m_data[] = null;
}
