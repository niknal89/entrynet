/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.gui.forge;

import entryorganizer.entities.wrappers.Wrapper;

/**
 *
 * @author Администратор
 */
public abstract class ForgeCallback {
    
    protected String fieldName;

    public ForgeCallback(String field) {
        this.fieldName = field;
    }
    
    public abstract void forgeCompleted(Wrapper w);
    
}
