/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FlowWindow.java
 *
 * Created on Sep 17, 2011, 6:17:26 PM
 */

package botoseis.mainGui.temp;

import botoseis.mainGui.utils.DefaultNode;
import botoseis.mainGui.workflows.WorkflowView;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

/**
 *
 * @author gabriel
 */
public class FlowWindow extends javax.swing.JInternalFrame implements InternalFrameListener {
    private boolean open;
    /** Creates new form FlowWindow */
    public FlowWindow() {
        initComponents();
    }
    public FlowWindow(String title, MainWindow mw) {
        super(title);
        initComponents();
        mainWindow = mw;
        workflowView = new WorkflowView(mw.getParmsPanel());
        panelMain.add(workflowView);
        this.addInternalFrameListener(this);
        
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelMain = new javax.swing.JPanel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setVisible(true);
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameActivated(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
        });
        getContentPane().setLayout(new java.awt.GridLayout(1, 0));

        panelMain.setLayout(new java.awt.GridLayout(1, 0));
        getContentPane().add(panelMain);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated
        mainWindow.getFlowsPanel().getDesktopManager().activateFrame(this);
        mainWindow.setCurrentFlow(workflowView);
}//GEN-LAST:event_formInternalFrameActivated


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel panelMain;
    // End of variables declaration//GEN-END:variables
    private WorkflowView workflowView;
    MainWindow mainWindow;

    /**
     * @return the workflowView
     */
    public // End of variables declaration
    WorkflowView getWorkflowView() {
        return workflowView;
    }

    /**
     * @param workflowView the workflowView to set
     */
    public void setWorkflowView(WorkflowView workflowView) {
        this.workflowView = workflowView;
    }


    @Override
    public void internalFrameOpened(InternalFrameEvent e) {

    }

    @Override
    public void internalFrameClosing(InternalFrameEvent e) {
        for(DefaultNode key : mainWindow.getMapFlowWindow().keySet()){
            if(mainWindow.getMapFlowWindow().get(key).equals(this)){
                mainWindow.getMapFlowWindow().remove(key);
                setOpen(false);
            }
        }
    }

    @Override
    public void internalFrameClosed(InternalFrameEvent e) {

    }

    @Override
    public void internalFrameIconified(InternalFrameEvent e) {

    }

    @Override
    public void internalFrameDeiconified(InternalFrameEvent e) {

    }

    @Override
    public void internalFrameActivated(InternalFrameEvent e) {

    }

    @Override
    public void internalFrameDeactivated(InternalFrameEvent e) {

    }

    /**
     * @return the open
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * @param open the open to set
     */
    public void setOpen(boolean open) {
        this.open = open;
    }
}
