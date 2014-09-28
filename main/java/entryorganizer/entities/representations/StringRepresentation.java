/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.entities.representations;

/**
 *
 * @author Администратор
 */
public class StringRepresentation extends Representation {
        
    public enum Condition {
        
        next("next"), 
        previous("previous"), 
        both("both"), 
        not_last("not_last"), 
        none("");
        
        private String code;
        
        Condition(String code) {
            this.code = code;
        }
        
        public static Condition get(String code) {
            for (Condition con : Condition.values()) {
                if (con.code.equals(code)) {
                    return con;
                }
            }
            return null;
        }
        
    }
    
    private String string;
    private Condition condition;
    
    private Representation previous;
    private Representation next;
    private boolean last;

    public StringRepresentation() {
    }

    public String get() {
        if (isAvailable())
            return string;
        else 
            return "";
    }
    
    public void setArguments(Representation previous, 
            Representation next, boolean last) {
        this.last = last;
        this.previous = previous;
        this.next = next;
    }

    public void setString(String string) {
        this.string = string;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }
    
    @Override
    public boolean isAvailable() {
        switch(condition) {
            case next: 
                if (this.next == null || !this.next.isAvailable()) 
                    return false;
                break;
            case previous:
                if (previous == null || !previous.isAvailable()) 
                    return false;
                break;
            case both: 
                if (this.next == null || !this.next.isAvailable() ||
                        previous == null || !previous.isAvailable()) 
                    return false; 
                break;
            case not_last:
                if (last) 
                    return false;
                break;
        }
        return true;
    }
}
