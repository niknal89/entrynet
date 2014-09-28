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
public class Author extends Tag {
    
    static final long serialVersionUID = -20L;
    
    private List<SourceField> name;
    private String lastName; 
    private String firstName; 
    private String patronymic; 
    private String comment;
    
    public static final SourceFieldType[] NAME_FIELDS_AUTHOR = new SourceFieldType[]{
        SourceFieldType.author(),
        SourceFieldType.authorName(),
        SourceFieldType.authorPatronymic()
    };
    
    public static final SourceFieldType[] NAME_FIELDS_EDITOR = new SourceFieldType[]{
        SourceFieldType.editor(),
        SourceFieldType.editorName(),
        SourceFieldType.editorPatronymic()
    };
    
    public Author(){}
    
    public Author(ID id, IDManager idCollection) {
        super(id, idCollection);
    }
    
    public String getShortName() {
        StringBuffer result = new StringBuffer();
        if (lastName != null) {
            result.append(lastName);
        } 
        if (firstName != null) {
            result.append(" " + firstName.charAt(0) + ".");
        } 
        if (patronymic != null) {
            result.append(" " + patronymic.charAt(0) + ".");
        } 
        return result.toString();
    }

    @Override
    public String getName() {
        StringBuffer result = new StringBuffer();
        if (lastName != null) {
            result.append(lastName);
        } 
        if (firstName != null) {
            result.append(", " + firstName);
        } 
        if (patronymic != null) {
            result.append(" " + patronymic);
        } 
        return result.toString();
    }
    
    public void setAuthorName(String lastName, String firstName, String patronymic) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.patronymic = patronymic;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof Author) {
            Author a = (Author) o;
            if (this.getLastName() != null) {
                if (!this.getLastName().equals(a.getLastName()) ||
                        a.getLastName() == null) return false;
            }
            if (this.getFirstName() != null) {
                if (!this.getFirstName().equals(a.getFirstName()) ||
                        a.getFirstName() == null) return false;
            }
            if (this.getPatronymic() != null) {
                if (!this.getPatronymic().equals(a.getPatronymic()) ||
                        a.getPatronymic() == null) return false;
            }
            return true;
            /*
            List<SourceField> sList = this.getAuthorName();
            List<SourceField> sListCheck = a.getAuthorName();
            boolean good = true;
            for (SourceField sf : sList) {
                boolean sfGood = false;
                for (SourceField sfCheck : sListCheck) {
                    if (sf.equals(sfCheck)) {
                        sfGood = true;
                    }
                }
                if (!sfGood) {
                    good = false;
                }
            } 
            return good;*/
        } else {
            return false;
        }
    }
    
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
        write();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        write();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        write();
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
        write();
    }

    @Override
    public String getInfo() {
        return this.getClass().getSimpleName() + " " + this.getShortName();
    }

    @Override
    public void addSource(Source s) {
        if (!sourceIDs.contains(s.getIdInt())) {
            sourceIDs.add(s.getIdInt());
            write();
        }
        if (!s.hasEntity(this)) {
            s.addAuthor(this);
        }
    }

    public void addSourceEditor(Source s) {
        if (!sourceIDs.contains(s.getIdInt())) {
            sourceIDs.add(s.getIdInt());
            write();
        }
        if (!s.hasEntity(this)) {
            s.addEditor(this);
        }
    }
    
    public void removeSourceEditor(Source s) {
        if (sourceIDs.contains(s.getIdInt())) {
            sourceIDs.remove(s.getIdInt());
            write();
        }
        if (s.hasEntity(this)) {
            s.removeEditor(this);
        }
    }
    
    public List<SourceField> getNameAsFields() {
        List<SourceField> result = new ArrayList<SourceField>();
        result.addAll(name);
        for (SourceField sf : result) { 
            sf.setInherited(true);
        }
        return result;
    }
}
