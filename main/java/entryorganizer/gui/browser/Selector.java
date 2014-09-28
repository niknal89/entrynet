/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.gui.browser;

import entryorganizer.gui.entitypanel.EntityPanel;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Администратор
 */
public class Selector {
    
    private Container container;
    private EntityPanel selectedField;

    public Selector(Container container) {
        this.container = container;
        selectedField = container.getHeading();
    }
            
    public boolean step(boolean backward, List<Container> stack) {        
        List<EntityPanel> panels = new ArrayList<EntityPanel>();
        panels.add(container.getHeading());
        panels.addAll(container.getPanels());        
                
        boolean goOn = true;
        if (selectedField instanceof Container) {
            Container c = (Container) selectedField;
            if (!selectedField.equals(container)) { 
                if (c.isShow()) {
                    goOn = c.getSelector().step(backward, stack);
                }
            } else if (!c.isShow()) {
                selectedField = null;
            }
        }
        
        if (goOn && selectedField != null) {
            if (!selectedField.equals(container.getHeading())) {
                stack.remove(selectedField);
            }
            selectedField.deselect();
            int step = backward ? -1 : 1;
            int i = panels.indexOf(selectedField) + step;

            if (i >= panels.size() || i <= -1) {
                selectedField = null;
            } else {
                selectedField = panels.get(i);            
            }        
        }
        
        if (selectedField == null) {
            selectedField = container.getHeading();
            return true;
        } else if (!goOn) {
            return false;
        } else {
            if (selectedField instanceof Container && !selectedField.equals(container))
                stack.add((Container) selectedField);
            selectedField.select();
            return false;
        }
        
    }
    
    public EntityPanel getSelectedField() {
        if (selectedField instanceof Container) {
            if (selectedField.equals(container)) {
                return selectedField;
            } else {
                return ((Container) selectedField).getSelector().getSelectedField();
            }
        } else {        
            return selectedField;
        }
    }
    
    public void setSelectedField(EntityPanel ep) {
        if (ep == null || container.equals(ep) || container.getPanels().contains(ep)) {
            selectedField = ep;
        }
    }
    
    public void reset() {
        if (selectedField != null) {
            selectedField.deselect();
        }
        selectedField = container.getHeading();
    }

}
