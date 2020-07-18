package usrdata;
/*
 * SUSection.java 
 *
 * Created on 28 de Agosto de 2007, 16:46
 *
 * Project: BotoSeis
 *
 * Federal University of Para.
 * Department of Geophysics
 *  
 */

import java.io.DataInputStream;
import java.util.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import javax.swing.JOptionPane;

/**
 * The SUSection class represents a colection of data
 * in the SU format.
 * 
 * @author Williams Lima
 */
public class SUSection {

    /** Creates a new instance of SUSection */
    public SUSection() {
        preStakcData = false;
        amplitude = 1;
        eof = false;
        m_xdr = true;
    
    }

    public void addTrace(SUTrace pTrace) {
        m_traces.add(pTrace);
    }

    public float[] getData() {
        float[] ret = null;
        if (m_traces.size() > 0) {
            int ns = m_traces.get(0).getHeader().ns;
            ret = new float[ns * m_traces.size()];
            for (int i = 0; i < m_traces.size(); i++) {
                float[] tdata = m_traces.get(i).getData();
                if (tdata != null) {
                    for (int t = 0; t < tdata.length; t++) {
                        ret[i * ns + t] = tdata[t] * amplitude;
                    }
                }
            }
        }

        return ret;
    }

    public java.util.Vector<SUTrace> getTraces() {
        return m_traces;
    }

    public int[] getListOfCDPs() {
        int[] ret = null;

        int s = m_traces.size();
        if (s > 0) {
            ret = new int[s];
            for (int i = 0; i < s; i++) {
                ret[i] = m_traces.get(i).getHeader().cdp;
            }
        }

        return ret;
    }

    public int getSCALCO() {
        if (m_traces.size() > 0) {
            return m_traces.get(0).getHeader().scalco;
        }
        return 0;
    }

    public int getN1() {
        if (m_traces.size() > 0) {
            return m_traces.get(0).getHeader().ns;
        }
        return 0;
    }

    public float getF1() {
        if (m_traces.size() > 0) {
            return m_traces.get(0).getHeader().f1;
        }

        return 0;
    }

    public float getD1() {
        if (m_traces.size() > 0) {
            float hd1 = m_traces.get(0).getHeader().d1;
            float hdt = m_traces.get(0).getHeader().dt;
            float d1;
            if (hd1 != 0f) {
                d1 = hd1;
            } else if (hdt != 0f) {
                d1 = hdt / 1000000.0f;
            } else {
                if (m_seismic) {
                    d1 = 0.004f;
                } else { /* non-seismic data */
                    d1 = 1.0f;
                }
            }

            if (Math.abs(d1) <= 0.1E-20) {
                d1 = 1.0f;
            }

            return d1;
        }
        return 0;
    }

    public int getN2() {
        if (m_traces.size() > 0) {
            return m_traces.size();
        }

        return 0;
    }

    public float getF2() {
        if (m_traces.size() > 0) {
            float hf2 = m_traces.get(0).getHeader().f2;
            float f2;
            if (hf2 != 0) {
                f2 = hf2;
            } else if (m_traces.get(0).getHeader().tracr != 0) {
                f2 = (float) m_traces.get(0).getHeader().tracr;
            } else if (m_traces.get(0).getHeader().tracl != 0) {
                f2 = (float) m_traces.get(0).getHeader().tracl;
            } else if (m_seismic) {
                f2 = 1.0f;
            } else {
                f2 = 0.0f;
            }

            return f2;
        }

        return 0;
    }

    public float getD2() {
        if (m_traces.size() > 0) {
            float d2 = (m_traces.get(0).getHeader().d2 != 0f) ? m_traces.get(0).getHeader().d2 : 1.0f;
            return d2;
        }
        return 0;
    }

    public float getDELRT() {
        if (m_traces.size() > 0) {
            float delrt = m_traces.get(0).getHeader().delrt;
            return delrt;
        }
        return -1.0f;
    }

    public void readFromFile(FileInputStream pInputFile, int pNumTraces) {
        SUTrace tr = null;
        if (!isPreStakcData()) {
            for (int i = 0; i < pNumTraces; i++) {
                tr = new SUTrace();
                tr.readFromFile(pInputFile, false,m_xdr);
                m_traces.add(tr);
            }
        } else {
        }
    }

    public boolean isEmpty() {
        if (m_traces.size() > 0) {
            return false;
        }

        return true;
    }
    
    public void setFormat(String format){
        m_xdr = true;
        
        if(format.equalsIgnoreCase("no-xdr")){
            m_xdr = false;
        }
    }

    public void readFromInputStream(InputStream input) {

        if(isEof())
            return;

        SUTrace trace = new SUTrace();
        trace.readFromFile(input, false,m_xdr);
        m_traces.clear();
        boolean flag = false;
        try {
            if (!isPreStakcData()) {
                m_traces.add(trace);
                while (input.available() > 0) {
                    trace = new SUTrace();
                    trace.readFromFile(input, false,m_xdr);
                    m_traces.add(trace);
                }
            } else {
                if (oldTrace != null) {
                    m_traces.add(oldTrace);
                }
                m_traces.add(trace);
                int skey, ekey;
                ekey = skey = m_traces.get(0).getHeader().getValue(pkey);
                while (input.available() > 0 && !flag) {
                    trace = new SUTrace();
                    trace.readFromFile(input, false,m_xdr);
                    ekey = trace.getHeader().getValue(pkey);
                    if (skey == ekey) {
                        m_traces.add(trace);
                    } else {
                        oldTrace = trace;
                        flag = true;
                    }
                }
                if(!flag){
                    JOptionPane.showMessageDialog(null, "End of file!");
                    eof = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Cant read file!", "Alert",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }

    }

    public void readFromFile(String pFilePath) {
        try {

            System.out.println("Start....");
            long d = System.currentTimeMillis();
            java.io.File inpF = new java.io.File(pFilePath);
            if (inpF.canRead()) {
                java.io.FileInputStream ifStream = new java.io.FileInputStream(inpF);
                SUTrace tr = null;
                tr = new SUTrace();
                tr.readFromFile(ifStream, false,m_xdr);
                m_traces.add(tr);
                long ntraces = inpF.length() / (240 + tr.getHeader().ns * 4);
                for (int i = 0; i < ntraces - 1; i++) {
                    tr = new SUTrace();
                    tr.readFromFile(ifStream, false,m_xdr);
                    m_traces.add(tr);
                }
            } else {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "Cant read file: " + pFilePath, "Alert",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
            }
            long d2 = System.currentTimeMillis();
            Date data = new Date(d2 - d);
            System.out.println("Stop...");
            System.out.println(data.getSeconds());
        } catch (java.io.FileNotFoundException exp) {
        }
    }

    public void writeToFile(FileOutputStream pOutputFile) {
        for (int i = 0; i < m_traces.size(); i++) {
            m_traces.get(i).writeToFile(pOutputFile,m_xdr);
        }
    }

    public void setTraces(Vector<SUTrace> vector) {
        m_traces = (Vector<SUTrace>) vector.clone();
    }

    /**
     * @return the preStakcData
     */
    public boolean isPreStakcData() {
        return preStakcData;
    }

    /**
     * @param preStakcData the preStakcData to set
     */
    public void setPreStakcData(boolean preStakcData) {
        this.preStakcData = preStakcData;
    }

    /**
     * @return the pkey
     */
    public String getPkey() {
        return pkey;
    }

    /**
     * @param pkey the pkey to set
     */
    public void setPkey(String pkey) {
        this.pkey = pkey;
    }
    // Variables declaration
    private Vector<SUTrace> m_traces = new Vector<SUTrace>();
    private boolean m_seismic = true;
    private boolean eof;
    private boolean preStakcData;
    private String pkey;
    private SUTrace oldTrace;
    private float amplitude;
    int m_n1;
    /**< Number of samples in 1st dimension. */
    int m_n2;
    /**< Number of samples in 2nd dimension. */
    float m_f1;
    /**< First value in 1st dimension. */
    float m_f2;
    /**< First value in 2nd dimension. */
    float m_d1;
    /**< Sampling interval in 1st dimension. */
    float m_d2;
    /** xdr format */
    boolean m_xdr;

    /**
     * @return the eof
     */
    public boolean isEof() {
        return eof;
    }
    /**< Sampling interval in 2nd dimension. */
}
