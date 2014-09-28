/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.entities.wrappers;

import entryorganizer.Commander;
import entryorganizer.datastorage.DataManager;
import entryorganizer.entities.Entity;
import entryorganizer.entities.EntityType;
import entryorganizer.entities.Field;
import entryorganizer.entities.FieldLimiters;
import entryorganizer.entities.ID;
import entryorganizer.entities.Link;
import entryorganizer.entities.exception.EntityException;
import entryorganizer.entities.representations.EntityRepresentation;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Администратор
 */
public class Wrapper {
    
    protected Commander commander;
    protected DataManager dataManager;
    protected Entity wrapped;

    public Wrapper() {}
    
    public Wrapper(Commander commander, Entity wrapped) {
        this.commander = commander;
        this.wrapped = wrapped;
        this.dataManager = commander.getDataManager();
    }
    
    public ID getID() {
        return wrapped.getId();
    }
    
    public Entity getWrapped() {
        return wrapped;
    }
    
    public boolean hasLink(Wrapper w) {
        for (Field f : wrapped.getFields()) {
            if (f instanceof Link) {
                Link l = (Link) f;
                if (l.getId() == w.getID().getId()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Link link(Wrapper w, String fieldName) throws EntityException {
        FieldLimiters fl = wrapped.getType().getLimiters(fieldName);
        if (w == null) {
            if (fl.getLimit() == 1) {
                dataManager.removeFields(wrapped, fieldName);
            }
            return null;
        }
        Link l = dataManager.forgeLink(wrapped, fieldName, w.getWrapped());
        String returnField = fl.getReturnField();
        if (returnField != null) {
            dataManager.forgeLink(w.getWrapped(), returnField, wrapped);
        }
        return l;
    }
    
    public void removeLink(int id, String fieldName) {
        List<Field> list = new ArrayList<Field>(wrapped.getFields());
        for (Field f : list) {
            if (!f.getName().equals(fieldName)) 
                continue;
            Link l = (Link) f;
            if (l.getId() != id) 
                continue;
            dataManager.removeField(wrapped, f);
            Wrapper linked = dataManager.loadLink(l, new Wrapper());
            FieldLimiters fl = wrapped.getType().getLimiters(fieldName);
            if (fl == null) return;
            String returnField = fl.getReturnField();
            if (returnField == null) return;
            linked.removeLink(wrapped.getIdInt(), returnField);
        }
    }
    
    public void delete() {        
        for(Field f : wrapped.getFields()) {
            if (!(f instanceof Link))
                continue;            
            Link link = (Link) f;
            Wrapper linkedTo = dataManager.loadEntity(link.getId(), new Wrapper());
            if (linkedTo == null)
                continue;
            List<Field> fields = new ArrayList<Field>(linkedTo.getWrapped().getFields());
            for (Field f2 : fields) {
                if (! (f2 instanceof Link))
                    continue;
                Link l2 = (Link) f2;
                if (l2.getId() == wrapped.getIdInt()) {
                    dataManager.removeField(linkedTo.getWrapped(), f2);
                }
            }
        }
        dataManager.deleteEntity(this);
    }
    
    public void addTag(Tag t) throws EntityException {
        link(t, "tag");
    }
    
    public void removeTag(Tag t) {
        removeLink(t.getID().getId(), "tag");
    }
    
    public List<Tag> getTags() {
        List<Tag> result = new ArrayList<Tag>();
        for (Field f : wrapped.getFields()) {
            if (f.getName().equals("tag") && f instanceof Link) {
                Link l = (Link) f;
                Tag t = dataManager.loadLink(l, new Tag());
                if (t != null)
                    result.add(t);
            }
        }
        return result;
    }
    
    public String getShortDescription() {
        EntityType et = wrapped.getType();
        EntityRepresentation er = 
                commander.getRepresentationSet("short").getRepresentation(et);
        if (er == null) {
            return "";
        } else {
            er.setArguments(wrapped);
            return er.get();
        }
    }
    
    public String getFullDescription() {
        EntityType et = wrapped.getType();
        EntityRepresentation er = 
                commander.getRepresentationSet("default").getRepresentation(et);
        if (er == null) {
            return "";
        } else {
            er.setArguments(wrapped);
            return er.get();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Wrapper))
            return false;
        Wrapper w = (Wrapper) o;
        EntityType et = w.getWrapped().getType();
        if (!(et.equals(this.wrapped.getType())))
            return false;
        if (w.getID().getId() == this.getWrapped().getIdInt()) {
            return true;
        } else {
            return false;
        }
    }
    
    
}
