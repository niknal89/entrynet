/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entryorganizer.gui;

import entryorganizer.gui.entitypanel.TagViewPanel;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author teopetuk89
 */
public class TagRenderer implements ListCellRenderer {
    
    public Component getListCellRendererComponent(JList jlist, Object o, 
                int i, boolean bln, boolean bln1) {
            if (o instanceof TagViewPanel) {
                TagViewPanel tp = (TagViewPanel) o;
                if (bln) {
                    tp.setBackground(Color.YELLOW);
                } else {
                    tp.setBackground(Color.WHITE);
                }
                return tp;
            } else {
                return new JLabel(o.toString());
            }
        }
}
