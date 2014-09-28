/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entryorganizer.data;

import java.io.Serializable;

/**
 *
 * @author teopetuk89
 */
public class SourceField implements Serializable {
    
    static final long serialVersionUID = 9L;
    
    private SourceFieldType type;
    private String fieldType; 
    private String value;
    private int number;
    
    private transient boolean inherited;
    
    public SourceField(SourceFieldType type) {
//        this.type = type;
        this.fieldType = type.getName();
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public SourceFieldType getType() {
        SourceFieldType sft = SourceFieldType.getSFT(fieldType);
        if (sft == null) {
            if (type != null) {
                sft = SourceFieldType.getSFT(type.getName());
            } else {
                System.out.println("SourceField: type " + fieldType + " returns null, field value " + value);
            }
        }
        return sft;
    }
    
    public boolean setValue(String value) {
        if (value.matches(getType().getRegex())) {
            this.value = value;
            return true;
        } else {
            return false;
        }
    }
    
    public String getValue() {
        return value;
    }
    
    public int getNumber() {
        return number;
    }
    
    public void setNumber(int number) {
        this.number = number;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof SourceField) {
            SourceField incoming = (SourceField) o;
            boolean good = true;
            if (incoming.getType() != null && 
                    !incoming.getType().equals(this.getType())) {
                good = false;
            }
            if (incoming.getValue() != null && this.getValue() != null) {
                if (!incoming.getValue().equals(this.getValue())) {
                    good = false;
                }
            }
            return good;
        } else {
            return false;
        }
    }
    
    public SourceField copy() {
        SourceField result = new SourceField(getType());
        result.setValue(value);
        result.setNumber(number);
        return result;
    }
    
    public SourceField parentField() {
        if (getType().isObjectPossible()) {
            SourceFieldType parentType = getType().getParentType();
            if (parentType != null) {
                SourceField result = new SourceField(parentType);
                result.setValue(value);
                return result;
            } else {
                return copy();
            }
        } else {
            return null;
        }
    } 
    
    public SourceField daughterField() {
        SourceFieldType inheritedType = getType().getDaughterType();
        if (inheritedType != null) {
            SourceField result = new SourceField(inheritedType);
            result.setValue(value);    
            return result;
        } else {
            return copy();
        }
    } 
    
    public void resetType() {
        if (type == null) 
            System.out.println("SourceField: type is null, field value " + value);
        this.fieldType = this.type.getName();
        if (fieldType == null) 
            System.out.println("SourceField: type name is null; " + type.toString() + "; field value " + value);
    }

    public boolean isInherited() {
        return inherited;
    }

    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }
    
    
}
