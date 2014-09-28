/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entryorganizer.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author teopetuk89
 */
public class SourceFieldType implements Serializable {
        
    static final long serialVersionUID = 8L;
    
    private String name;
    private String ruName;
    private String prefix;
    private String postfix;
    private SourceFieldValueType valueType;
    private boolean severalPossible;
    private boolean objectPossible;
    private String parentSourceType;
    private String parentFieldType;
    private List<String> dependentTypes;
    
    
    public SourceFieldType(String name) {
        this.name = name;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof SourceFieldType) {
            SourceFieldType sft = (SourceFieldType) o;
            if (sft.getName().equals(name))
                return true;
            else 
                return false;
        } else {
            return false;
        }
    }
    
    public String getRegex() {
        return valueType.getRegex();
    }

    public String getName() {
        return name;
    }

    public boolean isSeveralPossible() {
        return severalPossible;
    }

    public String getPostfix() {
        return postfix;
    }

    public SourceFieldValueType getValueType() {
        return valueType;
    }
   
    public void setValueType(SourceFieldValueType valueType) {
        this.valueType = valueType;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSeveralPossible(boolean severalPossible) {
        this.severalPossible = severalPossible;
    }

    public boolean isObjectPossible() {
        return objectPossible;
    }

    public void setObjectPossible(boolean objectPossible) {
        this.objectPossible = objectPossible;
    }

    public void setPostfix(String postfix) {
        this.postfix = postfix;
    }

    public String getRuName() {
        return ruName;
    }

    public void setRuName(String ruName) {
        this.ruName = ruName;
    }

    public List<SourceFieldType> getDependentTypes() {
        ArrayList<SourceFieldType> result = new ArrayList<SourceFieldType>();
        if (dependentTypes != null) {
            for (String sftName : dependentTypes) {
                SourceFieldType sft = getSFT(sftName);
                result.add(sft);
            }
        }
        return result;
    }

    public void setDependentTypes(List<String> dependentTypes) {
        this.dependentTypes = dependentTypes;
    }
    
    public boolean hasDependentField(SourceField sf) {
        for (String name : dependentTypes) {
            if (name.contentEquals(sf.getType().getName())) {
                return true;
            }
        }
        return false;
    }
    
    public void setParentFieldType(String fieldName) {
        this.parentFieldType = fieldName;
    }
    
    public SourceFieldType getParentType() {
        return getSFT(parentFieldType);
    /*    if (sft != null) {
            return sft;
        } else {
            return this;
        } */
    }
    
    public SourceType getParentSourceType() {
        return SourceType.getST(parentSourceType);
    }
    
    public SourceFieldType getDaughterType() {
        for (SourceFieldType sft : listSFT) {
            if (sft.getParentType() != null && sft.getParentType().equals(this)) {
                return sft;
            }
        }
        return this;
    }
    
    public static SourceFieldType title() {return getSFT("title");}
    public static SourceFieldType place() {return getSFT("place");}
    public static SourceFieldType author() {return getSFT("author");}
    public static SourceFieldType authorName() {return getSFT("authorName");}
    public static SourceFieldType authorPatronymic() {return getSFT("authorPatronymic");}
    public static SourceFieldType editor() {return getSFT("editor");}
    public static SourceFieldType editorName() {return getSFT("editorName");}
    public static SourceFieldType editorPatronymic() {return getSFT("editorPatronymic");}
        
    public static List<SourceFieldType> listSFT = new ArrayList<SourceFieldType>();
    
    public static void addSFT(SourceFieldType sft) {
        listSFT.add(sft);
    }
    
    public static SourceFieldType getSFT(String s) {
        for (SourceFieldType sft : listSFT) {
            if (sft.getName().equals(s))
                return sft;
        }
      //  System.err.println("SourceFieldType " + s + " not found");
        return null;
    }
    
}
