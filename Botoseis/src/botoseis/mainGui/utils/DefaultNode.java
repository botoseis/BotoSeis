/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package botoseis.mainGui.utils;



import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.tree.DefaultMutableTreeNode;
import botoseis.mainGui.usrproject.UserProject;

/**
 *
 * 
 */



public class DefaultNode extends DefaultMutableTreeNode implements MouseListener {


    public static int DEFUALT_TYPE = 000000;
    public static int PROJECT_TYPE = 111111;
    public static int LINE_TYPE = 222222;
    public static int FLOW_TYPE = 333333;
    public static int GROUP_TYPE = 444444;
    public static int PROCESS_TYPE = 555555;

    public static UserProject getProject(DefaultNode n) {
        if(n.getType() == DefaultNode.PROJECT_TYPE){
            return (UserProject) n.getUserObject();
        }else{
            return getProject((DefaultNode) n.getParent());
        }
    }


    private int type;

    public DefaultNode() {
        super();
    }

   public DefaultNode(String nome , int type) {
       super(nome);
       this.type = type;
   }

   public DefaultNode(String nome, boolean arg1){
       super(nome, arg1);
   }

    public DefaultNode(Object arg , int type) {

        super(arg);
        this.type = type;

    }




    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    public void mouseClicked(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mousePressed(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseReleased(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseEntered(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseExited(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

   

}