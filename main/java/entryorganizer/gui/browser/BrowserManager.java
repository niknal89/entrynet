/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.gui.browser;

import entryorganizer.Commander;
import entryorganizer.entities.EntityType;
import entryorganizer.entities.Field;
import entryorganizer.entities.FieldLimiters;
import entryorganizer.entities.Link;
import entryorganizer.entities.wrappers.Tag;
import entryorganizer.entities.wrappers.Wrapper;
import entryorganizer.gui.Messenger;
import entryorganizer.gui.browser.Caption.CaptionContainer;
import entryorganizer.gui.entitypanel.ExtendedPanel;
import entryorganizer.gui.entitypanel.WrapperPanel;
import entryorganizer.gui.forge.ForgeCallback;
import static java.awt.Component.LEFT_ALIGNMENT;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Администратор
 */
public class BrowserManager {
    
    private Commander commander;
    private Browser browser;

    public BrowserManager(Commander commander, Browser browser) {
        this.commander = commander;
        this.browser = browser;
    }
    
    public List<Caption> getCaptions(Wrapper wrapper) {
        List<Caption> result = new ArrayList<Caption>();
        List<FieldLimiters> fields =
                wrapper.getWrapped().getType().getInheritedFields();
        for (FieldLimiters fl : fields) {
            Caption c = loadField(fl.getName(), fl, wrapper);            
            if (c != null) {
                result.add(c);
            }
        }                
        return result;
    }
    
    private Caption loadField(String fieldName, FieldLimiters fl,
                    Wrapper wrapper) {
        if (fl.getType().equals(FieldLimiters.STRING) || 
                fl.getType().equals(FieldLimiters.INTEGER))
            return null;
        List<Field> fields = wrapper.getWrapped().getFields(fieldName);
        Caption caption = forgeCaption(fieldName, wrapper, fl);
        List<Link> links = new ArrayList<Link>();
        for (Field f : fields) {
            links.add((Link) f);
        }
        caption.setFields(links);
        return caption; 
    }
        
    private Caption forgeCaption(String string, Wrapper wrapper, FieldLimiters field) {
        Caption lab = new Caption(commander, new CC(), field, wrapper, this);
        lab.setText(string);
        lab.setAlignmentX(LEFT_ALIGNMENT);
        return lab;
    }
    
    public Messenger getMessenger() {
        return browser.getContentPanel().getMessenger();
    }

    public Browser getBrowser() {
        return browser;
    }
    
    public void forgeElement(Wrapper wrapper) {
        browser.getContentPanel().forgeElement(wrapper);
    }
    
    public void forgeElement(EntityType type, ForgeCallback callback) {
        browser.getContentPanel().forgeNewElement(type, callback);
    }
    
    public void forgeCompleted() {
        this.getBrowser().getContentPanel().forgeCompleted();
    }
    
    public void openNewBrowser(Wrapper w) {
        browser.openNewBrowser(w);
    }
    
    public void viewElement(Wrapper wrapper) {
        browser.setViewedElement(wrapper);
    }
    
    public void showCaption(Caption caption, boolean show) {
        browser.getContentPanel().allowFieldForWrapper(
                caption.getField().getName(), caption.getWrapper(), show);
    }
    
    public void validateContent() {
        browser.getContentPanel().getCurrentViewport().validate();        
    }
        
    private class CC implements CaptionContainer {

        public void validate() {
            validateContent();
        }
        
    }
    
}
