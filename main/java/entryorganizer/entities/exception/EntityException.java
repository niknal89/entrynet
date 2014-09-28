/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.entities.exception;

import entryorganizer.entities.Entity;

/**
 *
 * @author Администратор
 */
public class EntityException extends Exception {
    
    private Entity entity;
    
    public EntityException(Entity entity, String problemDescription) {
        super("entity " + entity.getTypeStr() == null ? "" : entity.getTypeStr() + 
                " id " + entity.getIdInt() + " encountered problem: " + 
                problemDescription);
        this.entity = entity;
    }
}
