/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.gui.forge;

import entryorganizer.Commander;
import entryorganizer.entities.Parameter;
import entryorganizer.entities.exception.WrongFieldException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Администратор
 */
public class ParameterPanel extends ValuePanel <Parameter, Integer> {
    
    public ParameterPanel(String name, ValuePanelContainer container) {
        super(name, container);
    }
        
    public ParameterPanel(Parameter parameter, ValuePanelContainer container) {
        super(parameter, container);
        this.setValue("" + parameter.getValue());
    }
    
    @Override
    public Integer getValue() {
        String str = getValueStr();
        if (str == null || str.isEmpty()) return null;
        try {
            int i = Integer.parseInt(str);
            return i;
        } catch (NumberFormatException ex) {
            return null;
        }
    }
    
    @Override
    public void saveField() {
        if (parent == null) 
            return;
        if (field == null) {
            if (!(getValue() == null)) {
                try {
                    dataManager.forgeParameter(parent.getWrapped(), name, getValue());
                } catch (WrongFieldException ex) {
                    Logger.getLogger(ParameterPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            if (getValue() == null) {
                removeField();
            } else {
                dataManager.changeParameter(parent.getWrapped(), field, getValue());
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
