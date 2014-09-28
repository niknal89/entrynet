/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.entities.exception;

/**
 *
 * @author Администратор
 */
public class NoIDException extends Exception {
    
    public NoIDException(int id) {
        super("no ID with number " + id);
    }
        
}
