/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author teopetuk89
 */
public class EntityType {
    
    public static final String TAG = "tag";   
    public static final String SOURCE = "source";   
    public static final String ENTRY = "entry";   
    public static final String PERSON = "person";   
    
    private String name;
    private EntityType parent;
    private boolean allowSearch = true;
    private List<FieldLimiters> allowedFields = 
            new ArrayList<FieldLimiters>(); 
    
    public EntityType() {}
    
    public EntityType(EntityType parent, List<FieldLimiters> allowedFields) {
        this.parent = parent;
        if (parent != null) {
            this.allowedFields.addAll(parent.getAllowedFields());
        }
        this.allowedFields.addAll(allowedFields);
    }
    
    public EntityType getParent() {
        return parent;
    }

    private List<FieldLimiters> getAllowedFields() {
        return allowedFields;
    }

    public void setParent(EntityType parent) {
        this.parent = parent;
    }

    public void setAllowedFields(List<FieldLimiters> allowedFields) {
        this.allowedFields = allowedFields;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
        
    public List<String> fieldsOfType(EntityType check) {
        List<String> result = new ArrayList<String>();
        List<FieldLimiters> inheritedFields = getInheritedFields();
        List<EntityType> checkParents = check.getParents();
        checkParents.add(check);
        for (FieldLimiters fl : inheritedFields) {
            if (fl == null || fl.getType() == null)
                new String();
            for (EntityType et : checkParents) {
                if (fl.getType().equals(et.getName())) {
                    result.add(fl.getName());
                }
            }
        }
        return result;
    }
    
    public boolean hasParent(EntityType typeName) {
        return isParent(this, typeName);
    }
    
    private boolean isParent(EntityType child, EntityType parent) {
        boolean hasParent = true;
        do {
            if (child.equals(parent)) {
                return true;
            } 
            EntityType next = child.getParent();
            if (next == null) {
                hasParent = false;
            } else {
                child = next;
            }
        } while (hasParent);
        return false;
    }

    public boolean isAllowSearch() {
        return allowSearch;
    }

    public void setAllowSearch(boolean allowSearch) {
        this.allowSearch = allowSearch;
    }

    public FieldLimiters getLimiters(String fieldName) {
        for (FieldLimiters fl : getInheritedFields()) {
            if (fl.getName().equals(fieldName))
                return fl;
        };
        return null;
    }
    
    public List<FieldLimiters> getInheritedFields() {
        List<FieldLimiters> result = 
                new ArrayList<FieldLimiters>();
        EntityType type = this;
        do {
            result.addAll(type.getAllowedFields());
            type = type.getParent();
        } while (type != null);
        return result;
    }
    
    private List<EntityType> getParents() {
        List<EntityType> result = new ArrayList<EntityType>();
        EntityType type = this;
        while (type != null) {
            type = type.getParent();
            if (type != null)
                result.add(type);
        }
        return result;
    }
    
    public boolean is(String name) {
        List<EntityType> list = getParents();
        list.add(this);
        for (EntityType et : list) {
            if (et.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EntityType))
            return false;
        EntityType et = (EntityType) obj;
        if (this.getName().equals(et.getName())) {
            return true;
        } else {
            return false;
        }
    }
}
