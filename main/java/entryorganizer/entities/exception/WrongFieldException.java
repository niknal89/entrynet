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
public class WrongFieldException extends EntityException {

    public WrongFieldException(Entity entity, String fieldName) {
        super(entity, "tried to add non-allowed field " + fieldName);
    }
    
}
