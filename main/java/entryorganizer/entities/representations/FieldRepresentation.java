/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.entities.representations;

import entryorganizer.datastorage.DataManager;
import entryorganizer.entities.Field;
import entryorganizer.entities.Link;
import entryorganizer.entities.Parameter;
import entryorganizer.entities.Text;
import entryorganizer.entities.wrappers.Wrapper;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Администратор
 */
public class FieldRepresentation extends Representation {
    
    private String field;
    private EntityRepresentation representation;
    private String representationStr;
    private boolean repeat;
    private String first_prefix;
    private String prefix;
    private String postfix;
    private String last_postfix;
    private int length;
    private DataManager dataManager;

    private List<Field> toRepresent = new ArrayList<Field>();

    public FieldRepresentation(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public void setRepresentation(EntityRepresentation representation) {
        this.representation = representation;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public void setFirst_prefix(String first_prefix) {
        this.first_prefix = first_prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setPostfix(String postfix) {
        this.postfix = postfix;
    }

    public void setLast_postfix(String last_postfix) {
        this.last_postfix = last_postfix;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getRepresentationStr() {
        return representationStr;
    }

    public void setRepresentationStr(String representationStr) {
        this.representationStr = representationStr;
    }
    
    public String get() {
        if (!isAvailable()) return "";
        StringBuilder sb = new StringBuilder();
        if (first_prefix != null)
            sb.append(first_prefix);
        for (int i = 0; i < toRepresent.size(); i++) {
            Field f = toRepresent.get(i);
            if (prefix != null)
                sb.append(prefix);
            String value = getValue(f);
            if (length > 0 && value.length() > length)
                value = value.substring(0, length);
            sb.append(value);
            if (last_postfix != null && i == toRepresent.size() - 1) {
                sb.append(last_postfix);
            } else if (postfix != null) {
                sb.append(postfix);
            }
            if (!repeat) break;
        }
        return sb.toString();
    }
    
    private String getValue(Field f) {
        if (f instanceof Text) {
            String text = ((Text) f).getText();
            if (text == null)
                return "";
            else 
                return text;
        } else if (f instanceof Parameter) {
            int i = ((Parameter) f).getValue();
            return "" + i;
        } else {
            Link l = (Link) f;
            if (representation == null) return "";
            Wrapper w = dataManager.loadLink(l, new Wrapper());
            representation.setArguments(w.getWrapped());
            return representation.get();
        }
    }
    
    public String getField() {
        return field;
    }
    
    public void setArguments(List<Field> toRepresent) {
        this.toRepresent = toRepresent;
    }
    
    public boolean isAvailable() {
        if (toRepresent.isEmpty())
            return false;
        return true;
    }
    
}
