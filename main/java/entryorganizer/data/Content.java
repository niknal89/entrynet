/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entryorganizer.data;

import entryorganizer.recover.IDManager;

/**
 *
 * @author teopetuk89
 */
public class Content extends Entity {

    static final long serialVersionUID = 5L;
    private String content;
    
    public Content() {}
    
    public Content(ID id, IDManager idCollection) {
        super(id, idCollection);
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
        write();
    }

    @Override
     void cutLinksWith(Entity e) {}

    @Override
    void cutLinks() {}

    @Override
    public String getInfo() {
        return this.getClass().getSimpleName() + getName();
    }

    @Override
    public String getName() {
        String cont = this.getContent();
        if (cont.length() > 100) {
            return cont.substring(0, 100);
        } else {
            return cont;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Content)) {
            return false;
        }
        Content c = (Content) o;
        if (c.getContent() == null) {
            if (this.getContent() == null) {
                return true;
            } else {
                return false;
            }
        }
        if (c.getContent().equals(this.getContent())) {
            return true;
        } else {
            return false;
        }

    }

}
