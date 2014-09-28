/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.data;

import entryorganizer.recover.IDManager;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author teopetuk89
 */
public class Entry extends Entity implements Tagged {
    
    static final long serialVersionUID = 3L;
    public static final int SHORT_CONTENT_LENGTH = 250;
    
    private Integer sourceID;
    private int pageStart;
    private int pageEnd;
    private List<Integer> tagIDs = new ArrayList<Integer>();
    
    private Integer contentID;
    private String shortContent = ""; // TODO: transient! fucking transient!!!
    private transient Content content;
    
    public Entry() {}
    
    public Entry(ID id, int contentID, IDManager idCollection) {
        super(id, idCollection);
        this.contentID = contentID;
        this.shortContent = "";
    }
  
    public String readContent(boolean full) {
        if (full) {
            if (content == null) {
                try {
                    Entity e = idManager.getEntity(contentID);
                    content = (Content) e;
                } catch (ClassCastException ex) {
                    System.out.println("for some fucking reason there is an entry whos id is of a content");
                }
            }
            if (content == null) return "";
            return content.getContent();    
        } else {
            return shortContent;
        }
    }

    public void saveContent(String contentStr) {
        if (content == null) {            
            content = (Content) idManager.getEntity(contentID);
            content.initialize(idManager.getID(contentID), idManager);
        }
        content.setContent(contentStr);
        if (contentStr.length() >= SHORT_CONTENT_LENGTH) {
            shortContent = contentStr.substring(0, SHORT_CONTENT_LENGTH);
        } else {
            shortContent = contentStr;
        }
        write();
    }
    
    public void setSource(Source source) {
        if (source != null) {
            this.sourceID = source.getIdInt();
            if (!source.hasEntity(this)) {
                source.addEntry(this);
            }
        } else {
            this.sourceID = null;
        }
        write();
    }

    public Source getSource() {
        if (sourceID != null) {
            Entity e = idManager.getEntity(sourceID);
            if (e instanceof Source) {
                return (Source) e;
            } else {
                return null;
            }
        }
        return null;
    }
    
    public boolean hasSource() {
        if (sourceID == null) {
            return false;
        } else {
            return true;
        }
    }
    
    public void setPageStart(int page) {
        this.pageStart = page;
        write();
    }
    
    public void setPageEnd(int page) {
        this.pageEnd = page;
        write();
    }

    public int getPageStart() {
        return pageStart;
    }

    public int getPageEnd() {
        return pageEnd;
    }
    
    public boolean hasEntity(Entity e) {
        if (e != null) {
            List<Integer> list = new ArrayList<Integer>();
            list.addAll(tagIDs);
            list.add(sourceID);
            for (Integer i : list) {
                if (i != null) {
                    if (i == e.getIdInt())
                        return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }
    
    public boolean hasTags() {
        if (tagIDs.isEmpty()) {
            return false;
        } else {
            return true; 
        }
    }
    
    public void addTag(Tag tag) {        
        if (!tagIDs.contains(tag.getIdInt())) {
            if (tagIDs.isEmpty()) {
                idManager.entryHasTag(this);
            }
            tagIDs.add((Integer) tag.getIdInt());
            write();
        }        
        List<Entry> entries = tag.getEntries();
        if (!entries.contains(this)) {
            tag.addEntry(this);
        }   
    }
    
    public boolean removeTag(Tag tag) {
        boolean result = tagIDs.remove((Integer) tag.getIdInt());
        if (!tag.getEntries().contains(this)) {
            tag.removeEntry(this);
        }
        write();
        if (tagIDs.isEmpty()) {
            idManager.entryHasNoTag(this);
        }
        return result;
    }
    
    public List<Tag> getTags() {
        return getList(tagIDs, new Tag());
    }

    public int getTagQuantity() {
        if (tagIDs != null) {
            return tagIDs.size();
        } else {
            return 0;
        }
    }

    @Override
    void cutLinks() {
        List<Integer> links = new ArrayList<Integer>();
        links.addAll(tagIDs);
        links.add(sourceID);
        for (Integer i : links) {
            if (i != null) {
                Entity e = idManager.getEntity(i);
                e.cutLinksWith(this);
            }
        }
        Entity e = idManager.getEntity(contentID);
        e.cutLinksWith(this);
        //e.delete();
        content = null;
    }

    @Override
    void cutLinksWith(Entity e) {
        if (e instanceof Tag) {
            tagIDs.remove((Integer) e.getIdInt());
        } else if (e.getIdInt() == sourceID) {
            sourceID = null;
        } else if (e == content) {
            content = null;
        }  
        write();
    }

    public void addToTag(Tag t) {
        t.addEntry(this);
    }

    @Override
    public String getInfo() {
        return this.getClass().getSimpleName() + "; " + getName();
    }

    public String getName() {
        String cont = this.readContent(false);
        if (cont == null) {
            return pageStart + "-" + pageEnd;
        }
        if (cont.length() < 100) {
            return cont;
        } else {
            return cont.substring(0, 100);
        }
    }

    public Integer getSourceID() {
        return sourceID;
    }

    public List<Integer> getTagIDs() {
        return tagIDs;
    }
    
    
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Entry)) {
            return false;
        }
        Entry e = (Entry) o;
        if (pageStart != e.pageStart ) return false;
        if (pageEnd != e.pageEnd ) return false;
        if (contentID != null && e.contentID != null &&
                !contentID.equals(e.contentID)) return false;
        if (sourceID != null && e.sourceID != null &&
                !sourceID.equals(e.sourceID)) return false;

        for (Integer i : tagIDs) {
           boolean has = false;
           if (i == null) continue;
           for (Integer i1 : e.tagIDs) {
               if (i1 != null && i1.equals(i)) {
                   has = true;
               }
           }
           if (!has) {
               return false;
           }
        }

        for (Integer i : e.tagIDs) {
           boolean has = false;
           if (i == null) continue;
           for (Integer i1 : tagIDs) {
               if (i1 != null && i1.equals(i)) {
                   has = true;
               }
           }
           if (!has) {
               return false;
           }
        }

        return true;
    } 
}
