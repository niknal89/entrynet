/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.gui.entitypanel;

import javax.swing.JPanel;
import entryorganizer.entities.wrappers.Wrapper;

/**
 *
 * @author Администратор
 */
public abstract class EntityPanel extends JPanel {
    
    public abstract void reload();
    
    public abstract void select();
    
    public abstract void deselect();
    
    public abstract Wrapper getWrapper();
    
}
