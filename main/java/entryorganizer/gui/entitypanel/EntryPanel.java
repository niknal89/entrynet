/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.gui.entitypanel;

import entryorganizer.Commander;
import entryorganizer.entities.wrappers.Entry;
import entryorganizer.entities.wrappers.Source;
import entryorganizer.entities.wrappers.Tag;
import entryorganizer.gui.Messenger;
import entryorganizer.gui.Resources;
import entryorganizer.gui.browser.BrowserManager;
import entryorganizer.gui.entitypanel.EntryForgePanel.EntryForgeContainer;
import entryorganizer.gui.entitypanel.EntryViewPanel.EntryViewContainer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseListener;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

/**
 *
 * @author teopetuk89
 */
public class EntryPanel extends ExtendedPanel {

    public interface EntryContainer extends ExtendedPanel.Container {
        
        public Entry getEntry();
        
        public Commander getCommander();
                
        public Messenger getMessenger();
        
        public void entryDeleted(EntryPanel e);
        
        public void entryRemoved(EntryPanel e);
        
        public void tagRemovedFromPanel(EntryPanel ep, Tag tag);
                
        public Dimension getSize();
        
    }
    
    private Entry entry;

    private Resources resources;
    private Commander commander;
    private EntryViewPanel viewPanel;
    private EntryForgePanel forgePanel;
    private EntryContainer container;
    
    private boolean editing;
   
    public EntryPanel(EntryContainer container, BrowserManager manager, String field) {
        super(manager, container);
       // this.setLayout(new FlowLayout(FlowLayout.LEADING));
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.entry = container.getEntry();
        this.resources = container.getCommander().getResources();
        this.commander = container.getCommander();
        this.container = container;
        EVC evc = new EVC(container.getTransferHandler(), this);
        viewPanel = new EntryViewPanel(container.getEntry(), evc, commander, manager);
        EFC efc = new EFC(this);
        forgePanel = new EntryForgePanel(efc, commander);
        this.add(viewPanel);
    }
    
    public void reload() {
        if (viewPanel != null) {
            viewPanel.reload();
        }
    }
    
    public void edit() {
        editing = true;
        forgePanel.activate(entry);            
        remove(viewPanel);
        add(forgePanel);
        repaint();
        revalidate();
        requestFocus();
    }
    
    @Override
    public String getContent() {
        if (entry != null) {
            return entry.getContent(true);
        } else {
            return null;
        }
    }

    @Override
    public void select() {
        this.setBackground(resources.getDefaultColor());
        if (viewPanel != null) {
            viewPanel.select();
        }
    }

    @Override
    public void deselect() {
        this.setBackground(resources.getSelectedColor());
        if (viewPanel != null) {
            viewPanel.deselect();
        }
    }

    @Override
    public Entry getWrapper() {
        return entry;
    }
        
    private void setForgeSize(int width) {
        if (forgePanel != null) {
            int height = forgePanel.getPreferredSize().height;
            Dimension dim = new Dimension(width, height);
            forgePanel.setMaximumSize(dim);
            forgePanel.setMinimumSize(dim);
            forgePanel.setPreferredSize(dim);
        }
    }

    @Override
    public String getField() {
        return container.getField();
    }

    @Override
    public void requestFocus() {
        if (editing) {
            forgePanel.requestFocus();
        }
    }
        
    @Override
    public JPanel getLinksPanel() {
        return viewPanel.getLinksPanel();
    }
        
    private class EVC implements EntryViewContainer {
        
        private TransferHandler handler;
        private EntryPanel entryPanel;
        
        public EVC(TransferHandler handler, EntryPanel ep) {
            this.handler = handler;
            this.entryPanel = ep;
        }
        
        @Override
        public void editEntry(Entry e) {
            entry = e;
            entryPanel.edit();
        }

        @Override
        public void entryDeleted(Entry e) {
            container.entryDeleted(entryPanel);
        }

        @Override
        public void entryRemoved(Entry e) {
            container.entryRemoved(entryPanel);
        }
        
        @Override
        public void tagBound() {
            entryPanel.validate();
        }

        @Override
        public void tagRemovedFromEntry(Tag tag) {
            entryPanel.validate();
            container.tagRemovedFromPanel(entryPanel, tag);
        }

        @Override
        public MouseListener getMouseListener() {
            return container.getMouseListener();
        }

        @Override
        public TransferHandler getTransferHandler() {
            return handler;
        }
        
        @Override
        public Messenger getMessenger() {
            return container.getMessenger();
        }

        public String getField() {
            return container.getField();
        }

        public Dimension getSize() {
            return container.getSize();
        }

        public void validate() {
            container.validate();
        }
    }

    private class EFC implements EntryForgeContainer {

        private EntryPanel parent;
        
        public EFC(EntryPanel ep) {
            this.parent = ep;
        }
        
        @Override
        public void entryForged(Entry e, boolean edited) {
            if (edited) {
                remove(forgePanel);
                add(viewPanel);
                viewPanel.reload();
            }
            parent.repaint();
            parent.revalidate();
            editing = false;
        }

        @Override
        public void cancel() {
            entryForged(entry, true);
        }

        @Override
        public Icon cancelIcon() {
            return resources.getImage(Resources.REMOVE);
        }

        @Override
        public void sourceSelected(Source s) {}

        
    }
   
}
