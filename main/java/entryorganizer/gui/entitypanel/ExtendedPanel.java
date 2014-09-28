/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.gui.entitypanel;

import entryorganizer.gui.browser.BrowserManager;
import entryorganizer.gui.browser.Caption;
import entryorganizer.gui.browser.Container;
import entryorganizer.gui.browser.Selector;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import javax.swing.border.Border;

/**
 *
 * @author Администратор
 */
public abstract class ExtendedPanel extends WrapperPanel implements Container <Caption> {
    
    public interface Container {
        
        public String getField();
        
        public MouseListener getMouseListener(); 
        
        public TransferHandler getTransferHandler();
        
        public Dimension getSize();
        
        public void validate();
    }
    
    protected boolean show;
    protected BrowserManager browserManager;
    private Selector selector;
    protected Container container;

    private List<Caption> fields = new ArrayList<Caption>();
    
    public ExtendedPanel(BrowserManager browserManager, Container container) {
        this.browserManager = browserManager;
        this.selector = new Selector(this);
        this.container = container;
    }
        
    public void reload() {
        getLinksPanel().removeAll();
        if (show) {
            fields = browserManager.getCaptions(getWrapper());
            for (Caption c : fields) {
                getLinksPanel().add(c);
                c.setListener(container.getMouseListener());
                c.setHandler(container.getTransferHandler());
            }
        } 
        getLinksPanel().validate();
        container.validate();
    }
    
    public void setShow(boolean show) {
        this.show = show;
        if (show) {
            Border beveled = BorderFactory.createRaisedBevelBorder();
            getLinksPanel().setBorder(beveled);
        } else {
            Border empty = BorderFactory.createEmptyBorder();
            getLinksPanel().setBorder(empty);
        }
        getLinksPanel().repaint();
    }

    public boolean isShow() {
        return show;
    }
    
    public abstract JPanel getLinksPanel();
        
    public abstract String getField();
    
    @Override
    public List<Caption> getPanels() {
        return fields;
    }
    
    @Override 
    public EntityPanel getHeading() {
        return this;
    }
    
    @Override
    public Selector getSelector() {
        return selector;
    }
}
