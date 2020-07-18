/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package botoseis.mainGui.utils;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author gabriel
 */
public class RendererTree extends DefaultTreeCellRenderer implements TreeCellRenderer {

     public RendererTree() {
        super();
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
         super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

         DefaultNode node = (DefaultNode) value;
         if(node.getType() == DefaultNode.DEFUALT_TYPE){

         }

         if(node.getType() == DefaultNode.PROJECT_TYPE) {



         }

         if(node.getType() == DefaultNode.LINE_TYPE){
             setIcon(new javax.swing.ImageIcon(getClass().getResource("/botoseis/pics/line.gif")));
         }

         if(node.getType() == DefaultNode.FLOW_TYPE){
            setIcon(new javax.swing.ImageIcon(getClass().getResource("/botoseis/pics/process.gif")));
         }
         if(node.getType() == DefaultNode.GROUP_TYPE){

         }
         if(node.getType() == DefaultNode.PROCESS_TYPE){
            setIcon(new javax.swing.ImageIcon(getClass().getResource("/botoseis/pics/process.gif")));
         }

         return this;
     }




}
