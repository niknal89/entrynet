/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.entities.representations;

import entryorganizer.entities.Entity;
import entryorganizer.entities.EntityType;
import entryorganizer.entities.Field;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Администратор
 */
public class EntityRepresentation extends Representation {
    
    private String name;
    private EntityType type;
    private List<Representation> field_order = new ArrayList<Representation>();
    
    private Entity toRepresent;

    public String get() {
        if (toRepresent == null) return "";
        StringBuilder sb = new StringBuilder();
        Representation previous = null;
        Representation current = null;
        Representation next = null;
        for (int i = 0; i < field_order.size(); i++) {
            if (i > 0) previous = current;
            if (i > 0) 
                current = next;
            else 
                current = field_order.get(i);
            if (i < field_order.size() - 1) 
                next = field_order.get(i + 1);
            else 
                next = null;
            
            if (current instanceof StringRepresentation) {
                StringRepresentation sr = (StringRepresentation) current;
                boolean last = false;
                if (i == field_order.size() - 1) last = true;
                sr.setArguments(previous, next, last);
            } else {
                FieldRepresentation fr = (FieldRepresentation) current;
                List<Field> list = new ArrayList<Field>();
                for (Field f : toRepresent.getFields()) {
                    if (f.getName().equals(fr.getField())) {
                        list.add(f);
                    }
                }
                fr.setArguments(list);
            }
            String res = current.get();
            sb.append(res);
        }
        return sb.toString();
    }
    
    public void setArguments(Entity e) {
        this.toRepresent = e;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(EntityType type) {
        this.type = type;
    }

    public void setField_order(List<Representation> field_order) {
        this.field_order = field_order;
    }

    public String getName() {
        return name;
    }

    public List<Representation> getField_order() {
        return field_order;
    }

    @Override
    public boolean isAvailable() {
        if (toRepresent == null)
            return false;
        else 
            return true;
    }
    
    
}
