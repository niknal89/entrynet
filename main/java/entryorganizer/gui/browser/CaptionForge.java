/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.gui.browser;

import entryorganizer.Commander;
import entryorganizer.datastorage.DataManager;
import entryorganizer.entities.EntityType;
import entryorganizer.entities.exception.EntityException;
import entryorganizer.entities.wrappers.Entry;
import entryorganizer.entities.wrappers.Source;
import entryorganizer.entities.wrappers.Tag;
import entryorganizer.entities.wrappers.Wrapper;
import entryorganizer.gui.Messenger;
import entryorganizer.gui.entitypanel.EntryPanel;
import entryorganizer.gui.entitypanel.SourceViewPanelExtended;
import entryorganizer.gui.entitypanel.TagPanel;
import entryorganizer.gui.entitypanel.WrapperPanel;
import entryorganizer.gui.entitypanel.WrapperViewPanelExtended;
import static java.awt.Component.LEFT_ALIGNMENT;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import javax.swing.TransferHandler;

/**
 *
 * @author Администратор
 */
public class CaptionForge {
    
    private TransferHandler handler;
    private MouseListener listener;
    private BrowserManager browserManager; 
    private Commander commander;
    private DataManager dataManager;
    
    private Caption caption;
    
    public static final int TAB_WIDTH = 25;

    public CaptionForge(BrowserManager manager, Caption caption, Commander commander) {
        this.browserManager = manager;
        this.caption = caption;
        this.commander = commander;
        this.dataManager = commander.getDataManager();
    }
    
    public WrapperPanel forgeWrapperPanel(Wrapper w, String field) {
        if (w instanceof Tag) {
            TagPanel tp = forgeTagPanel((Tag) w);
            return tp;
        } else if (w instanceof Source) {
            Source s = (Source) w;
            SVPEC svpec = new SVPEC(field);
            SourceViewPanelExtended svpe = 
                    new SourceViewPanelExtended(s, svpec, commander, browserManager);
            svpe.setTransferHandler(handler);
            svpe.addMouseListener(listener); 
            svpe.setAlignmentX(LEFT_ALIGNMENT);       
            return svpe;
        } else if (w instanceof Entry) {
            EntryPanel ep = forgeEntryPanel((Entry) w, field);
            return ep;
        } else {
            WVPEC wvpec = new WVPEC(field);
            WrapperViewPanelExtended wvp = 
                    new WrapperViewPanelExtended(w, commander, browserManager, wvpec);
            wvp.setAlignmentX(LEFT_ALIGNMENT);   
            return wvp;
        }
    }   
        
    public TagPanel forgeTagPanel(Tag tag) {
        TC tc = new TC(tag);
        TagPanel tp = new TagPanel(tc, browserManager);
        tp.setAlignmentX(LEFT_ALIGNMENT);
        tp.setTransferHandler(handler);
        tp.addMouseListener(listener);
        return tp;
    }
    
    public EntryPanel forgeEntryPanel(Entry entry, String field) {
        EC ec = new EC(entry, field);
        EntryPanel ep = new EntryPanel(ec, browserManager, field);
        ep.setAlignmentX(LEFT_ALIGNMENT);   
        return ep;
    }
        
    public void setTransferHandler(TransferHandler handler) {
        this.handler = handler;
    }

    public void setListener(MouseListener listener) {
        this.listener = listener;
    }
    
    public class TC implements TagPanel.TagContainer {

        private Tag tag;
        
        public TC(Tag tag) {
            this.tag = tag;
        }
        
        public Tag getTag() {
            return tag;
        }

        public Commander getCommander() {
            return commander;
        }

        public boolean isTagBindActive() {
            return true;
        }
        
        public void tagDeleted(TagPanel tp) {
            caption.removeField(tp);
        }
        
        public void tagRemoved(TagPanel t) {
            caption.unlinkField(t);
        }

        public Messenger getMessenger() {
            return browserManager.getMessenger();
        }

        public String getField() {
            return "tag";
        }

        public MouseListener getMouseListener() {
            return listener;
        }

        public TransferHandler getTransferHandler() {
            return handler;
        }
        
        public Dimension getSize() {
            return caption.getSize();
        }

        public void validate() {
            browserManager.validateContent();
        }
    }
        
    public class EC implements EntryPanel.EntryContainer {

        private Entry entry;
        private String field;
        
        public EC(Entry entry, String field) {
            this.entry = entry;
            this.field = field;
        }
        
        public Entry getEntry() {
            return entry;
        }

        public Commander getCommander() {
            return commander;
        }

        public TransferHandler getTransferHandler() {
            return handler;
        }

        public void entryDeleted(EntryPanel panel) {
            caption.removeField(panel);
        }
        
        public void entryRemoved(EntryPanel panel) {
            caption.unlinkField(panel);
        }

        public MouseListener getMouseListener() {
            return listener;
        }

        public Messenger getMessenger() {
            return browserManager.getMessenger();
        }

        public String getField() {
            return field;
        }
        
        public void tagRemovedFromPanel(EntryPanel ep, Tag tag) {
            caption.tagRemovedFromPanel(ep, tag);
        }
        
        public Dimension getSize() {
            return caption.getSize();
        }

        public void validate() {
            browserManager.validateContent();
        }
    }
        
    public class SVPEC implements SourceViewPanelExtended.SVPEContainer {
                
        private String field;

        public SVPEC(String field) {
            this.field = field;
        }
        
        public void forgeSource(Source s) {
            browserManager.forgeElement(s);
        }
        
        public void sourceDeleted(SourceViewPanelExtended panel) {
            caption.removeField(panel);
        }
        
        public void sourceRemoved(SourceViewPanelExtended panel) {
            caption.unlinkField(panel);
        }
        
        public void tagRemovedFromPanel(SourceViewPanelExtended sp, Tag tag) {
            caption.tagRemovedFromPanel(sp, tag);
        }
        
        public TransferHandler transferHandler() {
            return handler;
        }
        
        public MouseListener mouseListener() {
            return listener;
        }

        public Messenger getMessenger() {
            return browserManager.getMessenger();
        }
        
        public String getField() {
            return field;
        }

        public MouseListener getMouseListener() {
            return listener;
        }

        public TransferHandler getTransferHandler() {
            return handler;
        }
        
        public Dimension getSize() {
            return caption.getSize();
        }

        public void validate() {
            browserManager.validateContent();
        }
    }
    
    public class WVPEC implements WrapperViewPanelExtended.WrapperViewPanelContainer {
        
        private String field;

        public WVPEC(String field) {
            this.field = field;
        }
        
        public TransferHandler getTransferHandler() {
            return handler;
        }

        public MouseListener getMouseListener() {
            return listener;
        }

        public Messenger getMessenger() {
            return browserManager.getMessenger();
        }

        public void deleted(WrapperViewPanelExtended panel) {
            caption.removeField(panel);
        }

        public void removed(WrapperViewPanelExtended panel) {
            caption.unlinkField(panel);
        }

        public void tagRemoved(WrapperViewPanelExtended panel, Tag tag) {
            caption.tagRemovedFromPanel(panel, tag);
        }
        
        public String getField() {
            return field;
        }
        
        public void editWrapper(Wrapper w) {
            browserManager.forgeElement(w);
        }

        public Dimension getSize() {
            return caption.getSize();
        }

        public void validate() {
            browserManager.validateContent();
        }
    }
    
}
