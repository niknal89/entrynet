/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.data;

import java.util.ArrayList;
import java.util.List;
import entryorganizer.recover.IDManager;

/**
 *
 * @author teopetuk89
 */
public class Tag extends Entity implements Tagged {

    static final long serialVersionUID = 1L;
    
    private String name;
    private List<Integer> tagIDs = new ArrayList<Integer>();
    protected List<Integer> sourceIDs = new ArrayList<Integer>();
    private List<Integer> entryIDs = new ArrayList<Integer>();
    boolean key;
     
    public Tag() {}
    
    public Tag(ID id, IDManager idCollection) {
        super(id, idCollection);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof Tag) {
            Tag t = (Tag) o;
            if (t.getName() == null || this.getName() == null) return false;
            if (t.getName().equals(this.getName())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    public void addEntry(Entry entry) {
        if (!entryIDs.contains(entry.getIdInt())) {
            entryIDs.add((Integer) entry.getIdInt());     
            write();
        }
        if (!entry.hasEntity(this)) {
            entry.addTag(this);
        }
    }
    
    public void removeEntry(Entry entry) {
        if (entryIDs.contains(entry.getIdInt())) {
            entryIDs.remove((Integer) entry.getIdInt());
            write();
        }
        if (entry.hasEntity(this)) {
            entry.removeTag(this);
        }
    }

    @Override
    public void addTag(Tag tag) {        
        if (!tagIDs.contains(tag.getIdInt())) {
            tagIDs.add((Integer) tag.getIdInt());        
            write();
            System.out.println("Tag added " + tag.getName());
        }
        if (!tag.hasEntity(this))  {
            tag.addTag(this);
        }   
    }
    
    @Override
    public boolean removeTag(Tag tag) { 
        boolean result = false;
        if (tagIDs.contains(tag.getIdInt())) {
            result = tagIDs.remove((Integer) tag.getIdInt());            
            write();
        }
        if (tag.hasEntity(this)) {
            tag.removeTag(this);
        }
        return result;
    }
    
    public void addSource(Source s) {        
        if (!sourceIDs.contains(s.getIdInt())) {
            sourceIDs.add((Integer) s.getIdInt());
            write();
        }
        if (!s.hasEntity(this)) {
            s.addTag(this);
        }
    }
    
    public void removeSource(Source s) {
        sourceIDs.remove((Integer) s.getIdInt());
        if (s.hasEntity(this)) {
            s.removeTag(this);
        }
        write();
    }

    public void addLink(Tagged t) {
        t.addToTag(this);
    }
    
    @Override
    public void addToTag(Tag t) {
        t.addTag(this);
    }
    
    @Override
    public List<Tag> getTags() {
        return getList(tagIDs, new Tag());
    }    
    
    public boolean hasEntity(Entity e) {
        List<Integer> list = new ArrayList<Integer>();
        list.addAll(tagIDs);
        list.addAll(entryIDs);
        list.addAll(sourceIDs);
        for (Integer i : list) {
            if (i != null && i.equals(e.getIdInt())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        write();
    }

    public boolean isKey() {
        return key;
    }

    public void setKey(boolean key) {
        this.key = key;
        idManager.changeKeyTag(this.getIdInt(), key);
        write();
    }
        
    public List<Entry> getEntries() {
        return getList(entryIDs, new Entry());
    }
   
    public List<Source> getSources() {
        Source s = new Source();
        return getList(sourceIDs, s);
    }

    @Override
    public void cutLinksWith(Entity e) {
        if (e instanceof Entry) {
            entryIDs.remove((Integer) e.getIdInt());
        } else if (e instanceof Source) {
            sourceIDs.remove((Integer) e.getIdInt());
        } else if (e instanceof Tag) {
            tagIDs.remove((Integer) e.getIdInt());
        }
    }

    @Override
    void cutLinks() {
        List<Integer> links = new ArrayList<Integer>();
        links.addAll(tagIDs);
        links.addAll(sourceIDs);
        links.addAll(entryIDs);
        for (Integer i : links) {
            Entity e = idManager.getEntity(i);
            if (e != null) {
                e.cutLinksWith(this);
            }
        }
        write();
    }

    @Override
    public String getInfo() {
        return this.getClass().getSimpleName() + " " + this.getName();
    }

    public List<Integer> getTagIDs() {
        return tagIDs;
    }

    public List<Integer> getSourceIDs() {
        return sourceIDs;
    }

    public List<Integer> getEntryIDs() {
        return entryIDs;
    }
    
    
}
