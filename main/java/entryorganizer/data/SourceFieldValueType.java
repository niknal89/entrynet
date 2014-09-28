/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entryorganizer.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import entryorganizer.recover.OldLogger;

/**
 *
 * @author teopetuk89
 */
public class SourceFieldValueType implements Serializable {
    
    private String name;
    private String regex;
  
    static final long serialVersionUID = 7L;
    
    public SourceFieldValueType(String name) {
        this.name = name;
    }
    
    public String getRegex() {
        return regex;
    }

    public String getName() {
        return name;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }
  
    private static List<SourceFieldValueType> listSFVT = new ArrayList<SourceFieldValueType>();
    
    public static void addSFVT(SourceFieldValueType sfvt) {
        listSFVT.add(sfvt);
    }
    
    public static SourceFieldValueType getSFVT(String name) {
        for (SourceFieldValueType sfvt : listSFVT) {
            if (sfvt.getName().equals(name))
                return sfvt;
        }
        OldLogger.error("SourceFieldValueType " + name + " not found");
        return null;
    }
}
