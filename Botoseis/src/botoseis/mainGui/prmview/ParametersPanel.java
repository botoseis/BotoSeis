package botoseis.mainGui.prmview;

import botoseis.mainGui.workflows.ParametersGroup;
import botoseis.mainGui.workflows.ProcessModel;
import java.awt.GridBagConstraints;

import javax.swing.Box;

import java.io.*;
import java.util.Scanner;

/*
 * ParametersPanel.java
 *
 * Created on January 6, 2008, 8:57 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
public class ParametersPanel implements ParametersSource {

    public ParametersPanel(ProcessModel pProc) {

        m_prmPanels = new java.util.Vector<javax.swing.JPanel>();

        java.util.Vector<ParametersGroup> grps = pProc.getParameters();

        ParameterViewFactory pfac = new ParameterViewFactory();

        for (int i = 0; i < grps.size(); i++) {
            javax.swing.JPanel jPanel1 = new javax.swing.JPanel();

            jPanel1.setBorder(new javax.swing.border.TitledBorder(grps.get(i).getGroupName()));
            jPanel1.setLayout(new java.awt.BorderLayout());

            javax.swing.Box grpBox = javax.swing.Box.createVerticalBox();
            grpBox.setAlignmentX(Box.LEFT_ALIGNMENT);

            java.util.Vector<ProcessParameter> prmList = grps.get(i).getParameters();

            ProcessParameter prm;
            for (int j = 0; j < prmList.size(); j++) {
                prm = prmList.get(j);

                ParameterView npv = null;

                npv = pfac.createParameter(prm);

                npv.setAlignmentX(Box.LEFT_ALIGNMENT);

                grpBox.add(npv);
            }

            jPanel1.add(grpBox, java.awt.BorderLayout.WEST);
            m_prmPanels.add(jPanel1);
        }
    }

    public void loadFromFile(String path) {
        File myFile = new File(path);

        try {
            Scanner scanner = new Scanner(myFile);
            Scanner scanner2;

            java.util.Enumeration grps = m_prmPanels.elements();
            String line;
            while (grps.hasMoreElements()) {
                javax.swing.JPanel gPanel = (javax.swing.JPanel) grps.nextElement();
                String grpTitle = ((javax.swing.border.TitledBorder) gPanel.getBorder()).getTitle();
                String aLine = scanner.nextLine();
                scanner2 = new Scanner(aLine); // Read group title

                java.awt.Component[] grpsB = gPanel.getComponents();
                for (int i = 0; i < grpsB.length; i++) {
                    javax.swing.Box gbox = (javax.swing.Box) grpsB[i];
                    java.awt.Component[] prms = gbox.getComponents();
                    for (int j = 0; j < prms.length; j++) {
                        ParameterView pv = (ParameterView) prms[j];
                        aLine = scanner.nextLine();
                        scanner2 = new Scanner(aLine);
                        scanner2.useDelimiter("=");
                        String name = scanner2.next();
                        String value = "";
                        if (scanner2.hasNext()) {
                            value = scanner2.next();
                        }

                        pv.setValue(value);
                    }
                }
                scanner.nextLine(); // '/' group end mark

            }
        } catch (FileNotFoundException e) {
            javax.swing.JOptionPane.showMessageDialog(null, e.toString());
        }

    }

    public void saveToFile(String path) {
        File of = new File(path);
        FileWriter outF;
        try {
            outF = new FileWriter(of);
            java.util.Enumeration grps = m_prmPanels.elements();
            while (grps.hasMoreElements()) {
                javax.swing.JPanel gPanel = (javax.swing.JPanel) grps.nextElement();
                String grpTitle = ((javax.swing.border.TitledBorder) gPanel.getBorder()).getTitle();
                outF.write("&" + grpTitle + "\n");
                java.awt.Component[] grpsB = gPanel.getComponents();
                for (int i = 0; i < grpsB.length; i++) {
                    javax.swing.Box gbox = (javax.swing.Box) grpsB[i];
                    java.awt.Component[] prms = gbox.getComponents();
                    for (int j = 0; j < prms.length; j++) {
                        ParameterView pv = (ParameterView) prms[j];
                        outF.write(pv.getKeyValuePair());
                        outF.write("\n");
                    }
                }
                outF.write("/\n");
            }
            outF.close();
        } catch (IOException ex) {
        }
    }

    public void showParameters(javax.swing.JPanel hp) {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1;
        c.weighty = 0;
        for (int i = 0; i < m_prmPanels.size(); i++) {
            c.gridx = 0;
            c.gridy = i;
            if (i == m_prmPanels.size() - 1) {
                c.weighty = 1;
            }
            hp.add(m_prmPanels.get(i), c);
        }
    }

    public java.util.Vector<String> getParametersInline() {
        java.util.Vector<String> ret = new java.util.Vector<String>();

        java.util.Enumeration grps = m_prmPanels.elements();
        while (grps.hasMoreElements()) {
            javax.swing.JPanel gPanel = (javax.swing.JPanel) grps.nextElement();
            String grpTitle = ((javax.swing.border.TitledBorder) gPanel.getBorder()).getTitle();
            java.awt.Component[] grpsB = gPanel.getComponents();
            for (int i = 0; i < grpsB.length; i++) {
                javax.swing.Box gbox = (javax.swing.Box) grpsB[i];
                java.awt.Component[] prms = gbox.getComponents();
                for (int j = 0; j < prms.length; j++) {
                    ParameterView pv = (ParameterView) prms[j];
                    if (pv.getValue().length() > 0) {
                        String[] sv = pv.getCommandLine().split(" ");
                        for (int k = 0; k < sv.length; k++) {
                            ret.add(sv[k]);
                        }
                    }
                }
            }
        }

        return ret;
    }

    public ParametersSource clone(ProcessModel model) {
        ParametersPanel pp = new ParametersPanel(model);
        saveToFile(botoseis.mainGui.utils.Utils.getBotoseisROOT()+"/."+model.getID());
        pp.loadFromFile(botoseis.mainGui.utils.Utils.getBotoseisROOT()+"/."+model.getID());
        return pp;
    }


    // Variables declaration
    java.util.Vector<javax.swing.JPanel> m_prmPanels;
}
