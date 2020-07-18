package usrdata;
/*
 * SUSectionEnsemble.java 
 *
 * Created on 28 de Agosto de 2007, 16:58
 *
 * Project: BotoSeis
 *
 * Federal University of Para.
 * Department of Geophysics
 * 
 */
import java.io.IOException;
import java.util.Vector;
import java.io.FileInputStream;

/**
 *
 * @author Williams Lima
 */
public class SUSectionEnsemble {

    /** Creates a new instance of SUSectionEnsemble */
    public SUSectionEnsemble() {
    }

    public static int getGatherSorting(String pFilePath, Vector<Integer> pIDs, Vector<Integer> pTracesPerSection) {
        if (isSortedBy("CDP", pFilePath, pIDs, pTracesPerSection)) {
            return CDP;
        } else if (isSortedBy("CS", pFilePath, pIDs, pTracesPerSection)) {
            return CS;
        } else if (isSortedBy("CR", pFilePath, pIDs, pTracesPerSection)) {
            return CR;
        } else if (isSortedBy("CO", pFilePath, pIDs, pTracesPerSection)) {
            return CO;
        }

        return Unknown;
    }

    private static boolean isSortedBy(String pKey, String pFilePath, Vector<Integer> pIDs, Vector<Integer> pTracesPerSection) {
        try {
            FileInputStream dataFile = new FileInputStream(pFilePath);

            int keyListCount = 0;
            Vector<Integer> keyListIDs = new Vector<Integer>();
            Vector<Integer> listTracesPerKey = new Vector<Integer>();

            int currKeyValue = 0;
            int keyValue = 0;
            boolean sorted = true;

            SUTrace tr = new SUTrace();

            while (dataFile.available() > 0) {
                tr.readFromFile(dataFile, true);

                if (pKey.equalsIgnoreCase("CDP")) {
                    keyValue = tr.getHeader().cdp;
                } else if (pKey.equalsIgnoreCase("CS")) {
                    keyValue = tr.getHeader().sx;
                } else if (pKey.equalsIgnoreCase("CR")) {
                    keyValue = tr.getHeader().gx;
                } else if (pKey.equalsIgnoreCase("CO")) {
                    keyValue = tr.getHeader().offset;
                }

                if (keyValue == 0) {
                    sorted = false;
                    break;
                }

                if (keyValue == currKeyValue) {
                    int v = listTracesPerKey.get(keyListCount - 1);
                    v++;
                    listTracesPerKey.set(keyListCount - 1, v);
                } else {
                    currKeyValue = keyValue;

                    if (keyListCount > 0) {
                        for (int i = 0; i < keyListCount; i++) {
                            if (keyListIDs.get(i) == currKeyValue) {
                                sorted = false;
                                break;
                            }
                        }
                        if (!sorted) {
                            break;
                        }
                    }

                    keyListCount++;
                    keyListIDs.add(keyValue);
                    listTracesPerKey.add(1);
                }
            }
            if (sorted) {
                for (int i = 0; i < keyListIDs.size(); i++) {
                    pIDs.add(keyListIDs.get(i));
                }
                for (int i = 0; i < listTracesPerKey.size(); i++) {
                    pTracesPerSection.add(listTracesPerKey.get(i));
                }
                return true;
            }
        } catch (IOException e) {
            System.out.println("SUSectionEnsemble.isSortedBy");
            System.out.println(e.toString());
        }

        return false;

    }
    Vector<SUSection> m_sections = new Vector<SUSection>();
    public static final int Unknown = 0;
    public static final int AsRecorded = 1;
    public static final int CDP = 2;
    public static final int SingleFoldContinuous = 3;
    public static final int HorizontallyStacked = 4;
    public static final int CS = 5;
    public static final int CR = 6;
    public static final int CO = 7;
    public static final int CMP = 8;
}
