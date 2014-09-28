/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.entities;

import java.io.Serializable;

/**
 *
 * @author Администратор
 */
public class Link extends Field implements Serializable  {
    
    private static final long serialVersionUID = 25L;

    private int id;
    private transient Entity link;
    
    public Link(String name, int id) {
        super(name);
        this.id = id;
    }

    public Entity getLink() {
        return link;
    }

    public void setLink(Entity link) {
        this.link = link;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
}
