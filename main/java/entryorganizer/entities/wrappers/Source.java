/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.entities.wrappers;

import entryorganizer.Commander;
import entryorganizer.entities.Entity;
import entryorganizer.entities.exception.EntityException;

/**
 *
 * @author Администратор
 */
public class Source extends Wrapper {

    public Source() {}
    
    public Source(Commander commander, Entity wrapped) {
        super(commander, wrapped);
        this.commander = commander;
    }
    
    public void addEntry(Entry e) throws EntityException {
        link(e, "extract");
    }
    
}
