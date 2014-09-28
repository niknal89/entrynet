/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author teopetuk89
 */
public class Entity implements Serializable {
    
    private static final long serialVersionUID = 20L; 
    
    private List<Field> fields = new ArrayList<Field>();
    private transient ID id;
    private int idInt;
    private transient EntityType type;
    private String typeStr;
    
    public Entity() {}
    
    public Entity(ID id) {
        this.id = id;
        this.idInt = id.getId();
    }
    
    public void setType(EntityType type) {
        this.type = type;
        this.typeStr = type.getName();
    }

    public void setTypeStr(String typeStr) {
        this.typeStr = typeStr;
    }
    
    public EntityType getType() {
        return type;
    }

    public String getTypeStr() {
        return typeStr;
    }

    public List<Field> getFields(String fieldName) {
        List<Field> result = new ArrayList<Field>();
        for (Field f : fields) {
            if (f.getName().equals(fieldName)) {
                result.add(f);
            }
        }
        return result;
    }
    
    public List<Field> getFields() {
        return fields;
    }

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }
    
    public int getIdInt() {
        return idInt;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public void setIdInt(int idInt) {
        this.idInt = idInt;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Entity)) {
            return false;
        }
        Entity e = (Entity) obj;
        if (e.idInt == this.idInt) {
            return true;
        } else {
            return false;
        }
    }

    public void addField(Field field) {
        fields.add(field);
    }   
    
    public void removeField(Field field) {
        fields.remove(field);
    }
    
    public Field getField(String fieldName) {
        for (Field f : fields) {
            if (f.getName().equals(fieldName)) {
                return f;
            }
        }
        return null;
    }
    
    public String getText(String textName) {
        Field f = getField(textName);
        if (f instanceof Text) {
            Text t = (Text) f;
            return t.getText();
        } else { 
            return "";
        }
    }
    
    public Link getLink(String linkName) {
        Field f = getField(linkName);
        if (f instanceof Link) {
            return (Link) f;
        } else { 
            return null;
        }
    }
    
    public Integer getParameter(String parameterName) {
        Field f = getField(parameterName);
        if (f instanceof Parameter) {
            return ((Parameter) f).getValue();
        } else { 
            return null;
        }        
    }
    
}
