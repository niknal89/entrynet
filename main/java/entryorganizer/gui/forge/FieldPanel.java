/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.gui.forge;

import entryorganizer.datastorage.DataManager;
import entryorganizer.entities.Entity;
import entryorganizer.entities.Field;
import entryorganizer.entities.wrappers.Wrapper;

/**
 *
 * @author Администратор
 */
public abstract class FieldPanel <F extends Field, V extends Object> extends javax.swing.JPanel {
    
    protected F field;
    protected String name;
    
    protected Wrapper parent;
    protected DataManager dataManager;
    
    public FieldPanel(F field, DataManager dataManager) {
        this.field = field;
        this.name = field.getName();
        this.dataManager = dataManager;
    }
    
    public FieldPanel(String name, DataManager dataManager) {
        this.name = name;
        this.dataManager = dataManager;
    }
    
    public F getField() {
        return field;
    }
    
    public String getName() {
        return name;
    }
    
    public void setParent(Wrapper e) {
        this.parent = e;
    }
    
    public abstract void saveField();
    
    public abstract void removeField();
    
    public abstract void deleteField();
    
    public abstract V getValue();
}
