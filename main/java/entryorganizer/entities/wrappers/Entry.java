/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.entities.wrappers;

import entryorganizer.Commander;
import entryorganizer.entities.Entity;
import entryorganizer.entities.Link;
import entryorganizer.entities.exception.EntityException;
import entryorganizer.entities.exception.WrongFieldException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Администратор
 */
public class Entry extends Wrapper {
    
    public static final int SHORT_CONTENT_LENGTH = 250;

    public Entry() {}
    
    public Entry(Commander commander, Entity wrapped) {
        super(commander, wrapped);
    }
    
    public void setContent(String text) {
        try {
            dataManager.forgeText(wrapped, "content", text);
        } catch (WrongFieldException ex) {
            Logger.getLogger(Entry.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getContent(boolean full) {
        String text = wrapped.getText("content");
        if (!full && text.length() > SHORT_CONTENT_LENGTH) 
            text = text.substring(0, SHORT_CONTENT_LENGTH);
        return text;
    }
    
    public void setPageStart(int start_page) {
        try {
            dataManager.forgeParameter(wrapped, "page_start", start_page);
        } catch (WrongFieldException ex) {
            Logger.getLogger(Entry.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int getPageStart() {
        Integer result = wrapped.getParameter("page_start");
        if (result == null) 
            return 0;
        else 
            return result;
    }

    public void setPageEnd(int pageEnd) {
        try {
            dataManager.forgeParameter(wrapped, "page_end", pageEnd);
        } catch (WrongFieldException ex) {
            Logger.getLogger(Entry.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int getPageEnd() {
        Integer result = wrapped.getParameter("page_end");
        if (result == null) 
            return 0;
        else 
            return result;
    }
    
    public Source getSource() {
        Link l = wrapped.getLink("source");
        if (l != null) {
            Source s = dataManager.loadLink(l, new Source());
            return s;
        } else {
            return null;
        }
    }
    
    public void setSource(Source source) throws EntityException {
        link(source, "source");
    }
    
}
