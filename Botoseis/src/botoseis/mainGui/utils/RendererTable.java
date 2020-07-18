/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package botoseis.mainGui.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author gabriel
 */
public class RendererTable extends DefaultTableCellRenderer implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);


        if (c != null && value != null) {
            if (column == 2) {
                if (value.toString().equals(botoseis.mainGui.workflows.WorkflowJob.RUNNING)) {
                    c.setBackground(Color.BLUE);
                    c.setForeground(Color.green);
                } else {
                    if (value.toString().equals(botoseis.mainGui.workflows.WorkflowJob.ERROR)) {
                        c.setBackground(Color.RED);
                        c.setForeground(Color.BLACK);
                    } else {
                        if (value.toString().equals(botoseis.mainGui.workflows.WorkflowJob.COMPLETED)) {
                            c.setBackground(Color.BLUE);
                            c.setForeground(Color.YELLOW);
                        } else {
                            if (value.toString().equals(botoseis.mainGui.workflows.WorkflowJob.STOPPED)) {
                                c.setBackground(Color.ORANGE);
                                c.setForeground(Color.BLACK);
                            }
                        }
                    }
                }
                c.setFont(new Font("Arial", Font.BOLD, 12));
            } else {
                c.setFont(new Font("Arial", Font.PLAIN, 12));
                c.setBackground(Color.WHITE);
                c.setForeground(Color.BLACK);
            }
        }
        return c;

    }
}
