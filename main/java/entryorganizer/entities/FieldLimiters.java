/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.entities;

/**
 *
 * @author teopetuk89
 */
public class FieldLimiters {

    private int limit; 
    private String type;
    private String name;
    private String returnField;
    
    public static final String ANY_TYPE = "any";
    public static final String INTEGER = "int";
    public static final String STRING = "str";
    
    public FieldLimiters(int limit, String type, String name) {
        this.limit = limit;
        this.type = type;
        this.name = name;
    }

    public int getLimit() {
        return limit;
    }

    public String getType() {
        return type;
    }

    public String getReturnField() {
        return returnField;
    }

    public String getName() {
        return name;
    }

    public void setReturnField(String returnField) {
        this.returnField = returnField;
    }

    public boolean isSimpleType() {
        if (type.equals(INTEGER) || type.equals(STRING)) {
            return true;
        } else {
            return false;
        }
    }
}
