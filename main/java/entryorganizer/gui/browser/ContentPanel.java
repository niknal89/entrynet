/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.gui.browser;

import entryorganizer.Commander;
import entryorganizer.entities.EntityType;
import entryorganizer.entities.representations.RepresentationSet;
import entryorganizer.entities.wrappers.Wrapper;
import entryorganizer.gui.Messenger;
import static entryorganizer.gui.browser.Browser.CURRENT_CONTAINER;
import entryorganizer.gui.entitypanel.EntityPanel;
import entryorganizer.gui.entitypanel.ExtendedPanel;
import entryorganizer.gui.forge.Forge;
import entryorganizer.gui.forge.Forge.ForgeParent;
import entryorganizer.gui.forge.ForgeCallback;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.TransferHandler;

/**
 *
 * @author Администратор
 */
public class ContentPanel extends javax.swing.JPanel {
   
    public interface ContentContainer {
        
        public void validate();
        
        public MouseListener getMouseListener();
        
        public TransferHandler getTransferHandler();
        
        public Messenger getMessenger();
        
        public void checkSearch();
        
    }
    
    private Commander commander;
    private ContentContainer container;
    private BrowserManager browserManager;
    
    private TransferHandler transferHandler;
    private MouseListener listener;
    
    private KeyManager keyManager;
    
    private Wrapper viewedElement;    
    private ContentViewport currentViewport;    
    private List<Caption> currentContent = new ArrayList<Caption>();
    private List<String> fieldsToShowAlways = new ArrayList<String>();
    private Map<Wrapper, List<String>> fieldsToShow = 
            new LinkedHashMap<Wrapper, List<String>>();
    
    private int scrollPosition;
    private boolean positionMax;
     
    private static final int BAR_WIDTH = 19;
    
    public ContentPanel() {
        initComponents();    
    }
    
    public void initialize(Commander commander, BrowserManager manager, ContentContainer container) {
        this.commander = commander;
        this.listener = container.getMouseListener();
        this.container = container;
        this.transferHandler = container.getTransferHandler();
        this.keyManager = new KeyManager(manager, this);
        browserManager = manager;
        keyManager.setCurrentContent(currentContent);
        scrollContent.getVerticalScrollBar().addAdjustmentListener(new CPAdjustmentListener());
        fieldsToShowAlways.add("tag");
        
        setTransferHandler(transferHandler);
        setName(CURRENT_CONTAINER);
        reload();
    }
    
    public void reload() {
        loadNewPanels();
        showPanels();
        keyManager.reload();
    }
    /*
    public void refresh() {
        showPanels();
        keyManager.restoreFromStack();
    } */
    
    private void loadNewPanels() {
        currentContent.clear();
        if (viewedElement == null) {
            return;
        }
        if (viewedElement.getWrapped().getType() == null)
            return;
        currentContent.clear();
        currentContent.addAll(browserManager.getCaptions(viewedElement));
        for (Caption c : currentContent) {
            c.setHandler(transferHandler);
            c.setListener(listener);
            if (fieldsToShowAlways.contains(c.getField().getName())) {
                c.setShowWithoutNotifying(true);
            } else {
                c.setShowWithoutNotifying(false);
            }
        }
    }    
    
    protected void showPanels() {
        currentViewport = new ContentViewport(); 
        currentViewport.setLayout(new BoxLayout(currentViewport, BoxLayout.Y_AXIS));
        scrollContent.setViewportView(currentViewport);
       
        if (viewedElement == null) {
            return;
        }
                
        for (Caption caption : currentContent) {
            currentViewport.add(caption);
        }
        Component c = Box.createVerticalGlue();
        c.setMaximumSize(new Dimension(this.getWidth(),
                         Integer.MAX_VALUE)); 

        currentViewport.add(c);        
        
        container.checkSearch();
        currentViewport.validate();
        scrollContent.validate();
        for (Caption caption : currentContent) {            
            caption.reload();
        }
        for (Caption caption : currentContent) {
            checkFields(caption);
        }
        if (positionMax) {
            scrollPosition = scrollContent.getVerticalScrollBar().getMaximum();
        }
        currentViewport.validate();
        scrollContent.getVerticalScrollBar().setValue(scrollPosition);
    }
    
    private void checkFields(Caption caption) {
        for (EntityPanel ep : caption.getPanels()) {
            Wrapper w = ep.getWrapper();
            if (fieldsToShow.containsKey(w) && ep instanceof ExtendedPanel) {
                ExtendedPanel exp = (ExtendedPanel) ep;
                exp.setShow(true);
                exp.reload();
                List<String> allowedFields = fieldsToShow.get(w);
                for (Caption cap : exp.getPanels()) {
                    if (allowedFields.contains(cap.getField().getName())) {
                        cap.setShowWithoutNotifying(true);
                        checkFields(cap);
                    }
                }
            }
        }
    }
        
    public void forgeElement(Wrapper w) {
        Forge forge = forge();  
        forge.editElement(w, forgeEditing);
    }
    
    public void forgeNewElement(EntityType type, ForgeCallback callback) {
        Forge forge = forge();
        forge.forgeElementOfType(type, callback);
    }
    
    private ForgeCallback forgeEditing = new ForgeCallback("") {

        @Override
        public void forgeCompleted(Wrapper w) {
            reload();
            requestFocus();
        }

    };
    
    public void forgeCompleted() {
        scrollContent.setViewportView(currentViewport);
        currentViewport.validate();
        scrollContent.validate();
        keyManager.block(false);
        requestFocus();
    }
        
    private Forge forge() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
        Forge forge = new Forge(container.getMessenger(), commander, new FP());
        Dimension d = new Dimension(this.getWidth() - 10,
                this.getHeight() - 10);
        forge.setPreferredSize(d);
        p.add(forge);
        scrollContent.setViewportView(p);
        scrollContent.validate();
        keyManager.block(true);
        return forge;
    }
     
    protected Caption getCaption(String fieldName) {
        for (Caption caption : currentContent) {
            if (caption == null)
                continue;
            if (caption.getField().getName().equals(fieldName)) {
                return caption; 
            }
        }
        return null;
    }

    public void setViewedElement(Wrapper viewedElement) {
        this.viewedElement = viewedElement;
    }    
    
    private void allowField(String fieldName, boolean allow) {
        if (allow) {
            fieldsToShowAlways.add(fieldName);
        } else {
            fieldsToShowAlways.remove(fieldName);
        }
    }
    
    public void allowFieldForWrapper(String fieldName, Wrapper wrapper, boolean allow) {
        if (wrapper != null && wrapper.equals(viewedElement)) {
            allowField(fieldName, allow);
        } else {
            List<String> list = fieldsToShow.get(wrapper);
            if (list == null) list = new ArrayList<String>();
            if (allow) {
                list.add(fieldName);
            } else {
                list.remove(fieldName);
            }
            fieldsToShow.put(wrapper, list);
        }
    }
       
    public void view(JPanel pan) {        
        Rectangle bounds = pan.getBounds();
        bounds.y = getRealHeight(pan);
        Rectangle visible = scrollContent.getViewport().getViewRect();
        int newY; 
        if (bounds.height + 50 > visible.height) {
            newY = 0; 
        } else if (bounds.height > 50) {
            newY = 50;
        } else {
            newY = bounds.height;
        }
        if (bounds.y + bounds.height * 2 > visible.y + visible.height) {
            bounds.y += newY;
            scrollContent.getVerticalScrollBar().setValue(0);
            scrollContent.getViewport().scrollRectToVisible(bounds);
        } else if (bounds.y - bounds.height < visible.y) {
            bounds.y -= newY;
            scrollContent.getVerticalScrollBar().setValue(0);
            scrollContent.getViewport().scrollRectToVisible(bounds);
        }
    }
    
    private int getRealHeight(JPanel firstPanel) {
        int result = 0;
        Component panel = firstPanel;
        java.awt.Container parent = firstPanel.getParent();
        if (parent == null || currentViewport == null)
            return 0;
        do {
            result += panel.getBounds().y;
            panel = parent;
            parent = panel.getParent();
        } while (parent != null && !panel.equals(currentViewport));
        return result;
    }

    public Wrapper getViewedElement() {
        return viewedElement;
    }
    
    public Messenger getMessenger() {
        return container.getMessenger();
    }

    public ContentViewport getCurrentViewport() {
        return currentViewport;
    }
            
    private class FP implements ForgeParent {

        public void cancelForge() {
            reload();
        }

        public RepresentationSet getViewOrder() {
            return commander.getRepresentationSet("default");
        }

    }
    
    private class CPAdjustmentListener implements AdjustmentListener {
                
        public void adjustmentValueChanged(AdjustmentEvent e) {
            if (!e.getValueIsAdjusting()) {
                if (e.getValue() > 0) {
                    scrollPosition = e.getValue();
                    if (e.getAdjustable().getMaximum() == e.getValue()) {
                        positionMax = true;
                    } else {
                        positionMax = false;
                    }
                }
            }
        };
        
    }
        
    public class ContentViewport extends JPanel implements Scrollable {

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

        scrollContent = new javax.swing.JScrollPane();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollContent, javax.swing.GroupLayout.DEFAULT_SIZE, 882, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollContent, javax.swing.GroupLayout.DEFAULT_SIZE, 664, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollContent;
    // End of variables declaration//GEN-END:variables
}
