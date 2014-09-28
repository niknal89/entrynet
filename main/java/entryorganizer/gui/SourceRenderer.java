/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entryorganizer.gui;

import entryorganizer.gui.entitypanel.SourceViewPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;

/**
 *
 * @author teopetuk89
 */
public class SourceRenderer implements ListCellRenderer {
    
        public Component getListCellRendererComponent(JList jlist, Object o, 
                int i, boolean bln, boolean bln1) {
            if (o instanceof SourceViewPanel) {
                SourceViewPanel tp = (SourceViewPanel) o;
                if (bln) {
                    tp.setBackground(Color.YELLOW);
                } else {
                    tp.setBackground(Color.WHITE);
                }
     //           tp.addMouseListener(mouseListener);
                return tp;
            } else {
                return new JLabel(o.toString());
            }
        }    
}
