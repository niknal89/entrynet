/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import entryorganizer.EOLogger;
import entryorganizer.recover.IDManager;
import entryorganizer.recover.OldLogger;

/**
 *
 * @author teopetuk89
 */
public abstract class Entity implements Serializable {
    
    private static final long serialVersionUID = -10L;  
    
    private String uniqueName;    
    private int idInt;
    transient private ID id;
    transient protected IDManager idManager;
        
    static List<Entity> instances;

    public Entity() {}
        
    public Entity (ID id, IDManager idManager) {
        this.id = id;
        this.idManager = idManager;
        this.idInt = id.getId();
   }
      
    public void initialize(ID id, IDManager idCollection) {
        this.id = id;
        this.idManager = idCollection;
        this.idInt = id.getId();        
    }
    
    public final void write() {
        idManager.writeEntity(this);
    }
       
    public final void drop() {
        idManager.dropEntity(this);
        cutLinks();
    }
       
    public ID getId() {
       return id;
    }

    public void setID(ID id) {
        if (id != null) {
            this.id = id;
            this.idInt = id.getId();
            write();
        }
    }
    
    public Integer getIdInt() {
        return idInt;
    }

    public abstract String getInfo();

    public abstract String getName();

    abstract void cutLinks();
    
    abstract void cutLinksWith(Entity e);
    
  //  public transient TimeWatcher tw = new TimeWatcher();
    
    protected <E extends Entity> List<E> getList(List<Integer> ids, E e) {
        List<E> list = new ArrayList<E>();
        if (ids != null) {
            for (Integer i : ids) {
                Entity en = idManager.getEntity(i);
                if (en == null) {
                    OldLogger.idMismatch(en, i, e.getClass()); 
                    continue;
                }
                if (!en.getClass().equals(e.getClass())) {
                    OldLogger.idMismatch(en, i, e.getClass()); 
                    continue; 
                }
                E t = (E) en;
                list.add(t);
            }
        }
        return list;
    }
    
    protected <E extends Entity> List<Integer> toIDList(List<E> elist) {
        List<Integer> result = new ArrayList<Integer>();
        for (E e : elist) {
            result.add(e.getIdInt());
        }
        return result;
    }
     
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!o.getClass().equals(this.getClass())) {            
            return false;
        }
        Entity e = (Entity) o;
        if (e.getIdInt() == this.getIdInt()) {
            return true;
        } else {
            return false;
        }
    }


}
