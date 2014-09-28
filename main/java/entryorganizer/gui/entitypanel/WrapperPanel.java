/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.gui.entitypanel;

import entryorganizer.entities.wrappers.Wrapper;
import javax.swing.JPanel;

/**
 *
 * @author teopetuk89
 */
public abstract class WrapperPanel <W extends Wrapper> extends EntityPanel {
        
    public abstract String getContent();
    
    public abstract void edit();
    
    @Override
    public abstract W getWrapper();
    
}
