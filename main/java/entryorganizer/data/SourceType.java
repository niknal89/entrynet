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
public class SourceType implements Serializable {
   
    private String ruName;
    private String name;
    private List<SourceFieldType> fields = new ArrayList<SourceFieldType>();
    private SourceFieldType mainField; 
    private boolean preDefined;
   
    static final long serialVersionUID = 6L;
    private static List<SourceType> listST = new ArrayList<SourceType>();
    
    public SourceType(String name) {
        this.name = name;
        this.preDefined = true;
    }
    
    public boolean fieldPermitted(SourceFieldType type) {
        for (SourceFieldType typePermitted : fields) {
            if (type.equals(typePermitted)) return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public List<SourceFieldType> getFields() {
        return fields;
    }

    public void addFieldType(SourceFieldType sft) {
       fields.add(sft);
    }
        
    @Override
    public boolean equals(Object o) {
        if (o instanceof SourceType) {
            SourceType st = (SourceType) o;
            if (st.getName().equals(this.getName())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
        
    public static void addST(SourceType st) {
        listST.add(st);
    }

    public static List<SourceType> getListST() {
        return listST;
    }
    
    public static SourceType getST(String name) {
        if (name != null) {
            for (SourceType st : listST) {
                String stName = st.getName();
                if (name.equals(stName)) {
                    return st;
                }
            }
        }
        return null;
    }

    public String getRuName() {
        return ruName;
    }

    public void setRuName(String ruName) {
        this.ruName = ruName;
    }

    public SourceFieldType getMainField() {
        return mainField;
    }

    public void setMainField(SourceFieldType mainField) {
        this.mainField = mainField;
    }
    
    public boolean isPreDefined() {
        return preDefined;
    }
}
