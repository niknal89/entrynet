/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.gui.browser;

import entryorganizer.gui.entitypanel.EntityPanel;
import java.util.List;

/**
 *
 * @author Администратор
 */
public interface Container <P extends EntityPanel> {
    
    public void setShow(boolean show);
    
    public boolean isShow();
    
    public List<P> getPanels();
    
    public EntityPanel getHeading();
    
    public Selector getSelector();
}
