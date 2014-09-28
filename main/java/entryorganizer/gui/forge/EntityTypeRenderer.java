/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.gui.forge;

import entryorganizer.entities.EntityType;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Администратор
 */
public class EntityTypeRenderer implements ListCellRenderer {
    
    public Component getListCellRendererComponent(JList jlist, Object o, int i, 
            boolean bln, boolean bln1) {
        if (o instanceof EntityType) 
            return new EntityTypeLabel((EntityType) o);
        else 
            return new JLabel(o.toString());
    }
        
}
