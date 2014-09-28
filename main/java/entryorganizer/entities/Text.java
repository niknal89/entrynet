/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.entities;

import java.io.Serializable;

/**
 *
 * @author Администратор
 */
public class Text extends Field implements Serializable  {
    
    private static final long serialVersionUID = 26L;

    private String text;
    
    public Text() {}
    
    public Text(String name, String text) {
        super(name);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
}
