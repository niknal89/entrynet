/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.entities.wrappers;

import entryorganizer.Commander;
import entryorganizer.datastorage.DataManager;
import entryorganizer.entities.Entity;
import entryorganizer.entities.Field;
import entryorganizer.entities.Link;
import entryorganizer.entities.Text;
import entryorganizer.entities.exception.EntityException;
import entryorganizer.entities.exception.WrongFieldException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Администратор
 */
public class Tag extends Wrapper {

    public Tag() {}
    
    public Tag(Commander commander, Entity wrapped) {
        super(commander, wrapped);
    }
    
    public String getName() {
        String text = wrapped.getText("name");
        return text;
    }
    
    public void setName(String name) {
        try {
            dataManager.forgeText(wrapped, "name", name);
        } catch (WrongFieldException ex) {
            Logger.getLogger(Tag.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setLink(Wrapper link) throws EntityException {
        link(link, "link");
    }
    
    public void setKey(boolean key) {
        dataManager.setKey(wrapped.getIdInt(), key);
    }
    
    public boolean isKey() {
        return dataManager.isKey(wrapped.getIdInt());
    }
            
    public void removeLink(Wrapper link) {
        this.removeLink(link.getID().getId(), "link");
    }
    
}
