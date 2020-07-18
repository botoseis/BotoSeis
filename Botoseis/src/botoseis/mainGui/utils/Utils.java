/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package botoseis.mainGui.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author gabriel
 */
public class Utils {
    private static String currentPath;

    public static String getDateFormated(Date date) {
        if (date == null)
            return "";
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return format.format(date);
    }

    public static String getBotoseisROOT(){
        return System.getenv("BOTOSEIS_ROOT").toString();
    }

    public static String getCurrentPath(){
        return currentPath == null ? "" : currentPath;
    }
    public static void setCurrentPath(String path){
        currentPath = path;
    }
    
    public static String getPathProcess(String path){
        String[] split  = path.split("/");
        String ret = "";
        ret += split[split.length-3];
        ret += "/"+split[split.length-2];
        ret += "/"+split[split.length-1];
        return ret;
    }

     public static void deleteFile(java.io.File file) {
        if (file.listFiles() != null) {
            java.io.File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteFile(files[i]);
                    if (file.listFiles().length < 1) {
                        file.delete();
                    }
                } else {
                    files[i].delete();
                }
            }
        }
        file.delete();
    }
}
