/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.entities;

import java.io.Serializable;

/**
 *
 * @author teopetuk89
 */
public class Parameter extends Field implements Serializable  {

    private static final long serialVersionUID = 24L; 
    
    private int value;
    
    public Parameter() {}
    
    public Parameter(String name, int value) {
        super(name);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    
}
