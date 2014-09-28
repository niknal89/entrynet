/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.gui.forge;

import entryorganizer.Commander;
import entryorganizer.entities.Text;
import entryorganizer.entities.exception.WrongFieldException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Администратор
 */
public class TextPanel extends ValuePanel <Text, String> {
    
    public TextPanel(String name, ValuePanelContainer container) {
        super(name, container);
    }
    
    public TextPanel(Text text, ValuePanelContainer container) {
        super(text, container);
        this.setValue(text.getText());
    }
    
    @Override 
    public String getValue() {
        String value = super.getValueStr();
        if (value == null) return null;
        if (value.isEmpty()) return null;
        return value;
    }

    @Override
    public void saveField() {
        if (parent == null) 
            return;
        if (field == null) {
            if (!(getValueStr().equals(""))) {
                try {
                    dataManager.forgeText(parent.getWrapped(), name, getValue());
                } catch (WrongFieldException ex) {
                    Logger.getLogger(TextPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            if (getValueStr().equals("")) {
                removeField();
            } else {
                dataManager.changeText(parent.getWrapped(), field, getValue());
            }
        }
    }

    @Override
    public void removeField() {
        if (parent == null)
            return;
        if (field == null) 
            return;
        dataManager.removeField(parent.getWrapped(), field);
    }
    
    @Override
    public void deleteField() {
        this.removeField();
    }
    
}
