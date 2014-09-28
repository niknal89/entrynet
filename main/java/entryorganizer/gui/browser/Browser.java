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
import entryorganizer.gui.Resources;
import entryorganizer.gui.WrapperTransferHandler;
import entryorganizer.gui.browser.ContentPanel.ContentContainer;
import entryorganizer.gui.entitypanel.EntityPanel;
import entryorganizer.gui.entitypanel.EntryPanel;
import entryorganizer.gui.entitypanel.EntryPanel.EntryContainer;
import entryorganizer.gui.entitypanel.SourceViewPanel;
import entryorganizer.gui.entitypanel.TagViewPanel;
import entryorganizer.gui.entitypanel.TagViewPanel.TagViewContainer;
import entryorganizer.gui.entitypanel.WrapperPanel;
import entryorganizer.gui.entitypanel.WrapperViewPanel;
import entryorganizer.gui.entitypanel.WrapperViewPanel.WrapperPanelContainer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;
import javax.swing.Scrollable;
import javax.swing.TransferHandler;

/**
 *
 * @author Администратор
 */
public class Browser extends javax.swing.JPanel {

    public interface BrowserContainer {
        
        public void openNewBrowser(Wrapper w);
        
        public void nextBrowser(Browser current);
        
        public void close(Browser current);
        
        public Messenger getMessenger();
        
    } 
    
    public static final String CURRENT_CONTAINER = "CurrentContainer";
    public static final String COMB_KEY = "KeyTags";
    
    private Wrapper viewedElement;
    private int selectedKey;
    
    private Commander commander;
    private Resources resources;
    private DataManager dataManager;
    private BrowserManager browserManager;
    private ContentML listener;
    private BrowserContainer container;    
    private WrapperTransferHandler transferHandler;
    private KeyTagListener keyTagListener;
    
    /**
     * Creates new form Browser
     */
    public Browser(Commander commander, BrowserContainer bc) {
        initComponents();
        this.commander = commander;
        this.dataManager = commander.getDataManager();
        this.resources = commander.getResources();
        this.listener = new ContentML();
        this.container = bc;
        this.transferHandler = new WrapperTransferHandler(new TransferManager(this));  
        this.browserManager = new BrowserManager(commander, this);   
        CurrentC cc = new CurrentC(this);
        contentPanel.initialize(commander, browserManager, cc);   
        
        this.combKey.setRenderer(new LCR());
        this.combKey.setEditable(true);
        this.combKey.setEditor(new CBE());
        keyTagListener = new KeyTagListener();
        combKey.setTransferHandler(transferHandler);
        combKey.setName(COMB_KEY);
        scrollCurrent.setTransferHandler(transferHandler);
        scrollCurrent.setName(CURRENT_CONTAINER);
        
        reload();
        reloadKeyTags();
    }
    
    public class TransferManager implements WrapperTransferHandler.TransferManager {

        private Browser browser;
        private JPopupMenu popupLink = new JPopupMenu();
        private List<JMenuItem> menuList = new ArrayList<JMenuItem>();
        
        private WrapperPanel transfered;
        private WrapperPanel toLink;
        
        public TransferManager(Browser browser) {
            this.browser = browser;
        }
        
        public void setViewedElement(Wrapper w) {
            browser.setViewedElement(w);
        }
        
        public void setKey(Tag t) {            
            t.setKey(true);
            reloadKeyTags();
        }
        
        public void transfer(WrapperPanel transfered, WrapperPanel toLink) {
            EntityType typeToLink = 
                toLink.getWrapper().getWrapped().getType();
            EntityType typeTransfered = transfered.getWrapper().getWrapped().getType();
            List<String> possibleFields = 
                    typeToLink.fieldsOfType(typeTransfered);
            if (possibleFields.isEmpty()) {
                return;
            } else if (possibleFields.size() == 1) {
                link(transfered, toLink, possibleFields.get(0));
            } else {
                popupLink = new JPopupMenu();
                menuList.clear();
                this.transfered = transfered;
                this.toLink = toLink;
                popupLink.add(new JLabel("link as:"));
                for (String field : possibleFields) {
                    JMenuItem menuItem = new JMenuItem(field);
                    menuItem.addActionListener(new LinkListener());
                    menuList.add(menuItem);
                    popupLink.add(menuItem);
                }
                browser.requestFocus();
                popupLink.setVisible(true);
                popupLink.show(toLink, 16, 16);
            }
        }
        
        private void link(WrapperPanel transfered, WrapperPanel toLink, String fieldName) {
            try {
                toLink.getWrapper().link(transfered.getWrapper(), fieldName);
            } catch (EntityException ex) {
                container.getMessenger().postMessage(ex.getMessage());
            }
            toLink.reload();
            transfered.reload();
        }
        
        private class LinkListener implements ActionListener {
            
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof JMenuItem) {
                    JMenuItem source = (JMenuItem) e.getSource();
                    String field = source.getText();
                    link(transfered, toLink, field);
                }
            }
            
        }
        
    }
    
    public void reload() {
        reloadViewedElement();
        contentPanel.reload();
    }
    /*
    public void refresh() {
        reloadViewedElement();
        contentPanel.refresh();
    } */
    
    private void reloadKeyTags() {
        combKey.removeActionListener(keyTagListener);
        combKey.removeAllItems();
        selectedKey = -1;
        List<Tag> keyTags = dataManager.getKeyTags();
        for (Tag t : keyTags) {
            TagViewPanel tp = new TagViewPanel();
            tp.initialize(t, commander, browserManager, new KeyTVC());
            int width = this.combKey.getWidth();
            int height = tp.getPreferredSize().height;
            tp.setPreferredSize(new Dimension(width, height));
            tp.setTransferHandler(transferHandler);
            tp.addMouseListener(listener);
            tp.setBackground(resources.getDefaultColor());
            combKey.addItem(tp);
        }
        combKey.addActionListener(keyTagListener);
    }
    
    private void reloadViewedElement() {
        ContentViewport pan = new ContentViewport();
        pan.setLayout(new BoxLayout(pan, BoxLayout.Y_AXIS));
        scrollCurrent.setViewportView(pan);
        if (viewedElement instanceof Tag) {
            CurrentTVC ctc = new CurrentTVC();
            TagViewPanel tvp = new TagViewPanel();
            tvp.initialize((Tag) viewedElement, commander, browserManager, ctc);
            tvp = tvp;
            tvp.setTransferHandler(transferHandler);
            tvp.addMouseListener(listener);
            tvp.setAlignmentX(Component.LEFT_ALIGNMENT);
            pan.add(tvp);
        } else if (viewedElement instanceof Source) {
            Source s = (Source) viewedElement;
            SourceViewPanel svp = new SourceViewPanel(s, null, commander);
            svp.setTransferHandler(transferHandler);
            svp.addMouseListener(listener);
            svp.setAlignmentX(Component.LEFT_ALIGNMENT);
            pan.add(svp);
        } else if (viewedElement instanceof Entry) {
            CurrentEC cec = new CurrentEC(this);
            EntryPanel ep = new EntryPanel(cec, browserManager, "");
            ep.setTransferHandler(transferHandler);
            ep.addMouseListener(listener);
            ep.setAlignmentX(Component.LEFT_ALIGNMENT);
            pan.add(ep);
        } else if (viewedElement == null) {            
            
        } else {
            WrapperViewPanel wvp = new WrapperViewPanel();
            wvp.initialize(viewedElement, resources, new CurrentWPC());
            wvp.setTransferHandler(transferHandler);
            wvp.addMouseListener(listener);            
            wvp.setAlignmentX(Component.LEFT_ALIGNMENT);
            wvp.getLabName().addMouseListener(new CurrentWML(wvp));
            pan.add(wvp);
        }
        Component c = Box.createVerticalGlue();
        c.setMaximumSize(new Dimension(1,
                         Integer.MAX_VALUE)); 

        pan.add(c); 
        pan.validate();      
        scrollCurrent.validate();
        contentPanel.setViewedElement(viewedElement);
    }
            
    public void setViewedElement(Wrapper w) {
        viewedElement = w;
        reload();
    }

    public ContentPanel getContentPanel() {
        return contentPanel;
    }
    
    public void openNewBrowser(Wrapper w) {
        container.openNewBrowser(w);
    }

    public void nextBrowser() {
        container.nextBrowser(this);
    }
    
    public void nextKey() {
        combKey.showPopup();
        selectedKey++;
        if (selectedKey >= combKey.getItemCount()) {
            selectedKey = 0;
        }
        if (combKey.getItemCount() != 0) {
            combKey.setSelectedIndex(selectedKey);
        }
    }
    
    public void close() {
        container.close(this);
    }
    
    private void selectKey() {
        TagViewPanel tvp = (TagViewPanel) combKey.getSelectedItem();
        selectedKey = combKey.getSelectedIndex();
        if (tvp != null) {
            setViewedElement(tvp.getWrapper());
        }
    }
    
    private class CurrentC implements ContentContainer {
        
        private Browser browser;
        
        public CurrentC(Browser browser) {
            this.browser = browser;
        }
        
        public void validate() {
            browser.validate();
        }
        
        public MouseListener getMouseListener() {
            return listener;
        }
        
        public TransferHandler getTransferHandler() {
            return transferHandler;
        }
        
        public void checkSearch() {
            
        }

        public Messenger getMessenger() {
            return container.getMessenger();
        }
        
    }
    
    private class CurrentTVC implements TagViewContainer {        
                
        public void tagDeleted(Tag t) {
            setViewedElement(null);            
        }
                
        public void editTag(Tag t) {
            
        }
                
        public void tagRemoved(Tag t) {
            setViewedElement(null);
        }

        public boolean canDelete() {
            return true;
        }
        
        public Messenger getMessenger() {
            return container.getMessenger();
        }

        public WrapperPanel getParentPanel() {
            return null;
        }

    }
    
    private class CurrentEC implements EntryContainer {

        private Browser browser;
        
        public CurrentEC(Browser browser) {
            this.browser = browser;
        }
        
        public Entry getEntry() {
            return (Entry) viewedElement;
        }

        public int getContainerWidth() {
            return scrollCurrent.getWidth();
        }

        public Commander getCommander() {
            return commander;
        }

        public TransferHandler getTransferHandler() {
            return transferHandler;
        }

        public void validate() {
            browser.validate();
        }

        public void entryDeleted(EntryPanel e) {
            setViewedElement(null);
        }

        public MouseListener getMouseListener() {
            return listener;
        }

        public Messenger getMessenger() {
            return container.getMessenger();
        }
        
        public void tagRemovedFromPanel(EntryPanel ep, Tag t) {
            
        }

        public void entryRemoved(EntryPanel e) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public String getField() {
            return "";
        }
        
        public Dimension getSize() {
            return panCurrent.getSize();
        }
        
    }
    
    private class CurrentWPC implements WrapperPanelContainer {
        
        public void reloaded() {
            
        }

        public void deleted(Wrapper wrapper) {
            setViewedElement(null);
        }

        public void remove(Wrapper wrapper) {
            setViewedElement(null);
        }
        
        public void editWrapper(Wrapper wrapper) {
            
        }

    }
    
    private class CurrentWML implements MouseListener {
        
        private WrapperViewPanel panel;

        public CurrentWML(WrapperViewPanel panel) {
            this.panel = panel;
        }
        
        public void mouseClicked(MouseEvent me) { 
            me.setSource(panel);
            listener.mouseClicked(me);
        }

        public void mousePressed(MouseEvent me) {
            me.setSource(panel);
            listener.mousePressed(me);
        }

        public void mouseReleased(MouseEvent me) {
            me.setSource(panel);
            listener.mouseReleased(me);
        }

        public void mouseEntered(MouseEvent me) {
            me.setSource(panel);
            listener.mouseEntered(me);
        }

        public void mouseExited(MouseEvent me) {
            me.setSource(panel);
            listener.mouseExited(me);
        }
    }
    
    private class KeyTVC implements TagViewContainer {

        public void tagDeleted(Tag t) {
        }

        public void editTag(Tag t) {}

        public void tagRemoved(Tag t) {            
            dataManager.setKey(t.getID().getId(), false);
            reloadKeyTags();
        }

        public boolean canDelete() {
            return false;
        }
        
        public Messenger getMessenger() {
            return container.getMessenger();
        }

        public WrapperPanel getParentPanel() {
            return null;
        }
        
    }
    
    private class ContentML implements MouseListener {

        private long lastClick = 0;
        private long lastPress = 0;
        private boolean lastIsDouble = false;
        private boolean drag;
        private JComponent draggedComponent;
        private JPopupMenu popupTag = new JPopupMenu();
        private JMenuItem menuKey = new JMenuItem("Сделать ключом");
        private JMenuItem menuLink = new JMenuItem("Связать с меткой");
        private JMenuItem menuBreakLink = new JMenuItem("Разорвать связь с меткой");
        private Wrapper selectedEntity;
        
        private static final int DOUBLE_CLICK_DELAY = 500;
        private static final int DRAG_DELAY = 0;
        
        public void ContentML() {
        }
        
        public void mouseClicked(MouseEvent me) {
            boolean doubleClick = false;
            long click = me.getWhen();
            if ((click - lastClick) < DOUBLE_CLICK_DELAY && !lastIsDouble) {
                doubleClick = true;
                lastIsDouble = true;
            } else {
                lastClick = click;  
                lastIsDouble = false;
            }

            if ((me.getSource() instanceof TagViewPanel) &&
                    !doubleClick) {
                singleClickFromTagPanel(me);
            }    

            if ((me.getSource() instanceof WrapperPanel && doubleClick) &&
                    me.getModifiers() != InputEvent.BUTTON3_MASK) {
                doubleClickFromViewPanel(me);
            }
        }

        public void mousePressed(MouseEvent me) {
            if (me.getSource() instanceof WrapperPanel) {
                drag = true;
                draggedComponent = (JComponent) me.getSource();
                lastPress = me.getWhen();
            }
        }

        public void mouseReleased(MouseEvent e) {
            drag = false;
        }

        public void mouseEntered(MouseEvent e) {}

        public void mouseExited(MouseEvent me) {
            if (drag && (me.getWhen() - lastPress > DRAG_DELAY)) {
                if (draggedComponent instanceof WrapperPanel) {
                    WrapperPanel wp = (WrapperPanel) draggedComponent;
                    TransferHandler h = wp.getTransferHandler();
                    if (h != null) {
                        h.exportAsDrag(wp, me, TransferHandler.LINK);
                    }
                } 
                drag = false;
            }
        }
        
        private void singleClickFromTagPanel(MouseEvent me) {            
            TagViewPanel source = (TagViewPanel) me.getSource(); 
            Tag selectedTag = null;
            if (source == null)
                return;
            
            selectedTag = source.getWrapper();
            selectedEntity = selectedTag;
            if (me.getModifiers() == InputEvent.BUTTON3_MASK) {
                popupTag = new JPopupMenu();
                
                if (selectedTag.isKey()) {
                    menuKey = new JMenuItem("Убрать из ключей");
                } else {
                    menuKey = new JMenuItem("Сделать ключом");
                }
                popupTag.add(menuKey);
                menuKey.addActionListener(new KeyTagListener());
                menuKey.setActionCommand("key");
                popupTag.show(source, me.getX(), me.getY());   
            }
            
        }
                    
        private void doubleClickFromViewPanel(MouseEvent me) {   
            Wrapper w = null;
            if (me.getSource() instanceof WrapperPanel) {
                WrapperPanel wp = (WrapperPanel) me.getSource();  
                w = wp.getWrapper();
            } 
            if (w == null) return;

            if ((me.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK) {
                if (w != null) {
                    container.openNewBrowser(w);
                } 
            } else {
                setViewedElement(w);
            }
        }
    
        private class KeyTagListener implements ActionListener {

            public void actionPerformed(ActionEvent e) {
                if (selectedEntity == null)
                    return;
                
                if (e.getActionCommand().equals("key") && selectedEntity instanceof Tag) {
                    Tag tag = (Tag) selectedEntity;
                    tag.setKey(!tag.isKey());
                    reloadKeyTags();
                }
            }
            
        }
        
    }
        
    private class LCR implements ListCellRenderer {

        LCR() {
            setOpaque(true);
        }
        
        @Override
        public Component getListCellRendererComponent(JList jlist, Object e, 
                    int i, boolean isSelected, boolean cellHasFocus) {
            if (e instanceof TagViewPanel) {
                return (TagViewPanel) e;
            } else {
                return new JLabel(e.toString());
            }
        }
            
    }
    
    private class CBE implements ComboBoxEditor {
        
            @Override
            public Component getEditorComponent() {
                return new JLabel("Ключевые метки");
            }

            @Override
            public void setItem(Object o) {
                
            }

            @Override
            public Object getItem() {                
                return new JLabel("Ключевые метки");
            }

            @Override
            public void selectAll() {}

            @Override
            public void addActionListener(ActionListener al) {}

            @Override
            public void removeActionListener(ActionListener al) {}
            
    }
    
    private class KeyTagListener implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent ae) {   
            selectKey();
        }
        
    }
    
    private class ContentViewport extends JPanel implements Scrollable {

        public Dimension getPreferredScrollableViewportSize() {
            Dimension d = getPreferredSize();
            return d;
        }

        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 10;
        }

        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 10;
        }

        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
        
        
    }
        
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollCurrent = new javax.swing.JScrollPane();
        panCurrent = new javax.swing.JPanel();
        fieldSearch = new javax.swing.JTextField();
        butSearch = new javax.swing.JButton();
        combKey = new javax.swing.JComboBox();
        contentPanel = new entryorganizer.gui.browser.ContentPanel();

        fieldSearch.setText("");
        fieldSearch.setPreferredSize(new java.awt.Dimension(341, 20));

        butSearch.setText("jButton1");

        combKey.setModel(new javax.swing.DefaultComboBoxModel(new String[] {}));
        combKey.setPreferredSize(new java.awt.Dimension(367, 20));

        javax.swing.GroupLayout panCurrentLayout = new javax.swing.GroupLayout(panCurrent);
        panCurrent.setLayout(panCurrentLayout);
        panCurrentLayout.setHorizontalGroup(
            panCurrentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panCurrentLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(panCurrentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panCurrentLayout.createSequentialGroup()
                        .addComponent(butSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(combKey, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        panCurrentLayout.setVerticalGroup(
            panCurrentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panCurrentLayout.createSequentialGroup()
                .addComponent(combKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panCurrentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(scrollCurrent, javax.swing.GroupLayout.DEFAULT_SIZE, 614, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panCurrent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panCurrent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scrollCurrent, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butSearch;
    private javax.swing.JComboBox combKey;
    private entryorganizer.gui.browser.ContentPanel contentPanel;
    private javax.swing.JTextField fieldSearch;
    private javax.swing.JPanel panCurrent;
    private javax.swing.JScrollPane scrollCurrent;
    // End of variables declaration//GEN-END:variables
}
