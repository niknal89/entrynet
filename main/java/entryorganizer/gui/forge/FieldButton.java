/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.gui.forge;

import javax.swing.Icon;
import javax.swing.JButton;

/**
 *
 * @author Администратор
 */
public class FieldButton extends JButton {
    
    private ValuePanel parentPanel;
        
    public FieldButton(String caption, ValuePanel parentPanel) {
        super(caption);
        this.parentPanel = parentPanel;
    }

    public FieldButton(Icon icon, ValuePanel parentPanel) {
        super(icon);
        this.parentPanel = parentPanel;
    }

    public ValuePanel getParentPanel() {
        return parentPanel;
    }
        
}
