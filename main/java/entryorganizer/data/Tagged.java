/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entryorganizer.data;

import java.util.List;

/**
 *
 * @author teopetuk89
 */
public interface Tagged {
    
    public List<Tag> getTags();
    
    public void addTag(Tag t);
    
    public boolean removeTag(Tag tag);
    
    public void addToTag(Tag t);
    
}
