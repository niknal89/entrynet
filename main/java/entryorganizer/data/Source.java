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
public class Source extends Entity implements Tagged {
    
    static final long serialVersionUID = 2L;
    
    transient SourceType sourceType;
    String type;
    List<SourceField> fields = new ArrayList<SourceField>();
    SourceField mainField;
    String comment;
    List<Integer> tagIDs = new ArrayList<Integer>();
    List<Integer> entryIDs = new ArrayList<Integer>();
    List<Integer> authorIDs = new ArrayList<Integer>();
    List<Integer> editorIDs = new ArrayList<Integer>();    
    List<Integer> parentSourceIDs = new ArrayList<Integer>();
       
    public Source() {}
    
    public Source(ID id, IDManager idCollection) {
        super(id, idCollection);
    } 
    
    @Override
    public String getInfo() {
        return this.getClass().getSimpleName() + "; ";
    }
        
    @Override
    public boolean equals(Object o) {
   //     tw.startMeasure();
        if (!(o instanceof Source)) {
            return false;
        }
        Source s = (Source) o;
    //    tw.measureSplit("Source equals: check class");
        if (type != null && s.type != null && !s.type.equals(this.type)){
            if (type != s.type) {
                return false;
            }
        }
     //   tw.measureSplit("Source equals: check type");
        if (comment != null && s.comment != null && !s.comment.equals(this.comment)){
            if (comment != s.comment) {
                return false;
            }
        }
//        tw.measureSplit("Source equals: check comment");
        for (SourceField sf : fields) {
            if (!s.fields.contains(sf)) {
                return false;
            }
        }
        for (SourceField sf : s.fields) {
            if (!fields.contains(sf)) {
                return false;
            }
        }
   //     tw.measureSplit("Source equals: check fields");

        if (tagIDs != null) {
            for (Integer i : tagIDs) {
               boolean has = false;
               if (i == null) continue;
               for (Integer i1 : s.tagIDs) {
                   if (i1 != null && i1.equals(i)) {
                       has = true;
                   }
               }
               if (!has) {
                   return false;
               }
            }
        }
    //    tw.measureSplit("Source equals: check tags");

        if (entryIDs != null) {
            for (Integer i : entryIDs) {
               boolean has = false;
               if (i == null) continue;
               for (Integer i1 : s.entryIDs) {
                   if (i1 != null && i1.equals(i)) {
                       has = true;
                   }
               }
               if (!has) {
                   return false;
               }
            }
        }
  //      tw.measureSplit("Source equals: check entries");

        if (authorIDs != null) {
            for (Integer i : authorIDs) {
               boolean has = false;
               if (i == null) continue;
               for (Integer i1 : s.authorIDs) {
                   if (i1 != null && i1.equals(i)) {
                       has = true;
                   }
               }
               if (!has) {
                   return false;
               }
            }
        }
  //      tw.measureSplit("Source equals: check authors");

        if (editorIDs != null) {
            for (Integer i : editorIDs) {
               boolean has = false;
               if (i == null) continue;
               for (Integer i1 : s.editorIDs) {
                   if (i1 != null && i1.equals(i)) {
                       has = true;
                   }
               }
               if (!has) {
                   return false;
               }
            }
        }
    //    tw.measureSplit("Source equals: check editors");
        return true;
    } 
    
    @Override
    public String getName() {
        if (sourceType == null) {
            sourceType = SourceType.getST(type);
        }
        StringBuilder sb = new StringBuilder(); 
        List<Author> authors = new ArrayList<Author>();
        List<Author> editors = new ArrayList<Author>();
        if (authorIDs != null) {
            authors = getList(authorIDs, new Author());
        }
        if (editorIDs != null) { 
            editors = getList(editorIDs, new Author());
        }
        
        for (Author a : authors) {
            sb.append(a.getName() + ", ");
        }
        for (Author a : editors) {
            sb.append("ред. " + a.getName() + ", ");
        }
        
        if (mainField != null) {
            sb.append(mainField.getValue() + "; ");
        }
        
        for (SourceField field : fields) 
                sb.append(field.getValue() + ", ");
        
        if (sb.length() > 0) {
            sb.replace(sb.length() - 2, sb.length(), "");
        }
        return sb.toString();
    }
    
    public boolean addSourceField(SourceField field) {
        if (sourceType != null && !sourceType.fieldPermitted(field.getType())) {        
            return false;
        }    
        for (SourceFieldType sft : Author.NAME_FIELDS_AUTHOR) {
            if (field.getType().equals(sft)) {
                return false;
            }
        }
        for (SourceFieldType sft : Author.NAME_FIELDS_EDITOR) {
            if (field.getType().equals(sft)) {
                return false;
            }
        }
        if (field.getType().isSeveralPossible()) {
            fields.add(field);
            write();
            return true;
        } else {
            for (SourceField fieldExisting : fields) {
                if (fieldExisting.getType().equals(field.getType())) {
                    return false;
                } 
            }
            fields.add(field);
            write();
            return true;
        }
    }
    
    public boolean removeSourceField(SourceField sf) {
//        if (fields.size() <= sf || sf < -1) {
  //          return false;
    //    } else {            
            boolean success = fields.remove(sf);
            if (success)
                write();
            return success;
      //  }
    }
        
    public String getDescription() {
        return comment;
    }
    
    public String getTitle() {
        for (SourceField sf : fields) {
            if (sf.getType().equals(SourceFieldType.title())) {
                return sf.getValue();
            }
        }
        return "";
    }

    public void setDescription(String comment) {
        this.comment = comment;
        write();
    }

    public SourceType getSourceType() {
        if (sourceType != null) {
            return sourceType;
        } else {
            return SourceType.getST(type);
        }
    }
    
    public String getSourceTypeRaw() {
        return type;
    }

    public void setSourceType(SourceType sourceType) {
        if (sourceType != null) {
            this.type = sourceType.getName();
        } else {
            this.type = null;
        }
        this.sourceType = sourceType;
        write();
    }

    public void setSourceType(String sourceType) {
        this.type = sourceType;
        this.sourceType = SourceType.getST(sourceType);
        write();
    }
        
    public void clearFields() {
        fields.clear();
    }
    
    @Override
    void cutLinks() {
        for (Integer i : tagIDs) {
            Tag t = (Tag) idManager.getEntity(i);
            t.cutLinksWith(this);
        }
    }

    @Override
    void cutLinksWith(Entity e) {
        if (e instanceof Author) {
            authorIDs.remove((Integer) e.getIdInt());
            editorIDs.remove((Integer) e.getIdInt());
        } else if (e instanceof Tag) {
            tagIDs.remove((Integer) e.getIdInt()); 
        }
        write();
    }    
    
    public boolean hasEntity(Entity e) {
        List<Integer> list = new ArrayList<Integer>();
        if (tagIDs != null) list.addAll(tagIDs);
        if (entryIDs != null) list.addAll(entryIDs);
        if (authorIDs != null) list.addAll(authorIDs);
        if (editorIDs != null) list.addAll(editorIDs);
        for (Integer i : list) {
            if (i == null) continue;
            if (i == e.getIdInt())
                return true;
        }
        return false;
    }
    
    @Override
    public void addTag(Tag t) {
        if (!tagIDs.contains(t.getIdInt())) {
            tagIDs.add((Integer) t.getIdInt());
            write();
        }
        if (!t.hasEntity(this)) {
            t.addSource(this);
        }
    }
    
    @Override
    public boolean removeTag(Tag t) {
        boolean success =
            tagIDs.remove((Integer) t.getIdInt());
        if (t.hasEntity(this)) {
            t.removeSource(this);
        }
        write();
        return success;
    }
    
    public List<Author> getAuthors() {        
        return getList(authorIDs, new Author());
    }
    
    public List<Author> getEditors() {
        return getList(editorIDs, new Author());
    }
    
    public List<Entry> getEntries() {
        return getList(entryIDs, new Entry());
    }
    
    public void setAuthors(List<Author> authors) {
        authorIDs = toIDList(authors);
        for (Author a : authors) {
            if (!a.hasEntity(this)) {
                a.addSource(this);
            }
        }
        write();
    }
    
    public void addAuthor(Author a) {        
        if (!authorIDs.contains(a.getIdInt())) {
            authorIDs.add((Integer) a.getIdInt());
            write();
        }
        if (!a.hasEntity(this)) {
            a.addSource(this);
        } 
    }
    
    public void removeAuthor(Author a) {
        if (authorIDs.contains(a.getIdInt())) {
            authorIDs.remove((Integer) a.getIdInt());
            write();
        }
        if (a.hasEntity(this)) {
            a.removeSource(this);
        } 
    }
    
    public void addEditor(Author e) {             
        if (!editorIDs.contains(e.getIdInt())) {
            editorIDs.add((Integer) e.getIdInt());
            write();
        }
        if (!e.hasEntity(this)) {
            e.addSourceEditor(this);
        }         
    }
    
    public void removeEditor(Author e) {             
        if (editorIDs.contains(e.getIdInt())) {
            editorIDs.remove((Integer) e.getIdInt());
            write();
        }
        if (e.hasEntity(this)) {
            e.removeSourceEditor(this);
        }         
    }
    
    public void addEntry(Entry e) {
        if (entryIDs == null) {
            entryIDs = new ArrayList<Integer>();
        }
        if (!entryIDs.contains(e.getIdInt())) {
            entryIDs.add((Integer) e.getIdInt());
            write();
        }
        if (!e.getSource().equals(this)) {
            e.setSource(this);
        }
    }
    
    public void setEditors(List<Author> editors) {
        editorIDs = toIDList(editors);
        for (Author a : editors) {
            if (!a.hasEntity(this)) {
                a.addSource(this);
            }
        }
        write();
    }
    
    public boolean addParentSource(Source s) {
        parentSourceIDs.add(s.getIdInt());
        if (s.getMainField() != null) {
            List<SourceFieldType> fieldsToInherit = 
                    s.getMainField().getType().getDependentTypes();
            List<SourceField> toRemove = new ArrayList<SourceField>();
            for (SourceField sf : fields) {
                SourceFieldType sft = sf.getType();
                if (!sft.isSeveralPossible() && fieldsToInherit.contains(sft)) {
                    toRemove.add(sf);
                }
            }
            fields.removeAll(toRemove);
        }
        write();
        return true;
    }
    
    public void removeParentSource(Source s) {
        if (parentSourceIDs.contains(s.getIdInt())) {
            parentSourceIDs.remove((Integer) s.getIdInt());
            write();
        }        
    }
    
    public List<Source> getParentSources() {
        return getList(parentSourceIDs, new Source());
    }
    
    @Override
    public List<Tag> getTags() {
        return getList(tagIDs, new Tag());
    }

    @Override
    public void addToTag(Tag t) {
        t.addSource(this);
    }

    public void setMainField(SourceField mf) {
        this.mainField = mf;
        write();
    }

    public SourceField getMainField() {
        return mainField;
    }
    
    public List<SourceField> getFieldsToInherit(SourceFieldType sft) {
        List<SourceField> result = new ArrayList<SourceField>();
        if (mainField != null) {
            List<SourceFieldType> dependentSFT = mainField.getType().getDependentTypes();
            for (SourceField sf : fields) {
                if (sft != null && !sf.getType().equals(sft)) {
                    continue;
                }
                if (dependentSFT.contains(sf.getType())) {
                    SourceField inherited = sf.daughterField();
                    inherited.setInherited(true);
                    result.add(inherited);
                }
            }
        }
        return result;
    }    
        
    public List<SourceField> getFields() {
        return fields;
    }

    public List<Integer> getTagIDs() {
        return tagIDs;
    }

    public List<Integer> getEntryIDs() {
        return entryIDs;
    }

    public List<Integer> getAuthorIDs() {
        return authorIDs;
    }

    public List<Integer> getEditorIDs() {
        return editorIDs;
    }

    public List<Integer> getParentSourceIDs() {
        return parentSourceIDs;
    }


}
