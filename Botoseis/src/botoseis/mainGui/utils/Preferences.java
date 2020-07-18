/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package botoseis.mainGui.utils;

import java.io.*;
import java.util.HashMap;

/**
 *
 * @author gabriel
 */
public class Preferences {

    private String format;

    public static Preferences getPreferences() {
        Preferences p = new Preferences();
        p.format = "XDR";
        Object botov = System.getenv("BOTOSEIS_ROOT");
        try {
            File file = new File(botov.toString() + "/.cfg");
            if (file.exists()) {
                BufferedReader buff = new BufferedReader(new FileReader(file));
                HashMap<String, String> map = new HashMap();
                while (buff.ready()) {
                    String linha = buff.readLine();
                    if (linha.contains("=")) {
                        map.put(linha.split("=")[0], linha.split("=")[1]);
                    }
                }
                p.setFormat(map.get("format"));
                buff.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }

    public void writePreferences() {
        try {
            Object botov = System.getenv("BOTOSEIS_ROOT");
            File file = new File(botov.toString() + "/.cfg");
            BufferedWriter buff = new BufferedWriter(new FileWriter(file));
            buff.write("format="+getFormat());
            buff.newLine();
            buff.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the format
     */
    public String getFormat() {
        return format == null ? "" : format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.format = format;
    }
}
