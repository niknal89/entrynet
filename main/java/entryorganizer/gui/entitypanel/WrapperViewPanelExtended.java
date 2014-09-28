/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.gui.entitypanel;

import entryorganizer.Commander;
import entryorganizer.entities.wrappers.Tag;
import entryorganizer.entities.wrappers.Wrapper;
import entryorganizer.gui.Messenger;
import entryorganizer.gui.Resources;
import entryorganizer.gui.WrapLayout;
import entryorganizer.gui.browser.BrowserManager;
import entryorganizer.gui.browser.CaptionForge;
import entryorganizer.gui.entitypanel.TagViewPanel.TagViewContainer;
import entryorganizer.gui.entitypanel.WrapperViewPanel.WrapperPanelContainer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

/**
 *
 * @author Администратор
 */
public class WrapperViewPanelExtended extends ExtendedPanel {

    public interface WrapperViewPanelContainer extends ExtendedPanel.Container {
                
        public Messenger getMessenger();
        
        public void deleted(WrapperViewPanelExtended deleted);
        
        public void removed(WrapperViewPanelExtended removed);
        
        public void tagRemoved(WrapperViewPanelExtended panel, Tag tag);
        
        public void editWrapper(Wrapper wrapper);
        
    }
         
    private Commander commander;
    private WrapperViewPanelContainer container; 
    private Resources resources;
    
    private Wrapper wrapper;
    
    private List<TagViewPanel> tagPanels = new ArrayList<TagViewPanel>();
    private boolean selected;
    
    public WrapperViewPanelExtended(Wrapper wrapper, Commander commander, 
            BrowserManager browserManager,
            WrapperViewPanelContainer container) {
        super(browserManager, container);
        initComponents();
        this.commander = commander;
        this.resources = commander.getResources();
        this.wrapper = wrapper;
        this.container = container;
        this.setTransferHandler(container.getTransferHandler());
        this.setBorder(BorderFactory.createLineBorder(Color.black));
        
        mainPanel.initialize(wrapper, commander.getResources(), new WPC(this));
        mainPanel.setTransferHandler(container.getTransferHandler());        
        WrapperListener localListener = new WrapperListener(this);
        mainPanel.addMouseListener(localListener);
        mainPanel.getLabName().addMouseListener(localListener);
        
        WrapLayout layout = new WrapLayout(FlowLayout.LEADING);
        panTags.setLayout(layout);
        
        reload();
    }
    
    private boolean reloading;

    @Override
    public void reload() {
        reloading = true;
        int width = container.getSize().width - CaptionForge.TAB_WIDTH;
        this.setMaximumSize(new Dimension(width, Integer.MAX_VALUE));
        
        mainPanel.reload();
        
        panTags.removeAll();
        if (wrapper == null)
            return;
        List<Tag> tags = wrapper.getTags();
        for (Tag tag : tags) {
            TagViewPanel tvp = new TagViewPanel();
            tvp.initialize(tag, commander, browserManager, new TVC(this));
            tvp.addMouseListener(container.getMouseListener());
            tvp.setTransferHandler(container.getTransferHandler());
            panTags.add(tvp);
        }
        panTags.validate();
        
        super.reload();
        if (show) {
            butFold.setIcon(resources.getImage(Resources.FOLD));
        } else {
            butFold.setIcon(resources.getImage(Resources.UNFOLD));            
        }
        
        if (selected) {
            select();
        } else {
            deselect();
        }
                
        validate();
        revalidate();
        repaint();
        reloading = false;
    }
    
    @Override
    public String getContent() {
        if (wrapper == null) {
            return "";
        } else {
            return wrapper.getFullDescription();
        }
    }

    @Override
    public Wrapper getWrapper() {
        return wrapper;
    }

    @Override
    public void select() {
        selected = true;
        this.setBackground(resources.getSelectedColor());
        mainPanel.select();
        panTags.setBackground(resources.getSelectedColor());
        for (TagViewPanel tvp : tagPanels) {
            tvp.select();
        }
    }

    @Override
    public void deselect() {
        selected = false;
        this.setBackground(resources.getDefaultColor());
        mainPanel.deselect();
        panTags.setBackground(resources.getDefaultColor());
        for (TagViewPanel tvp : tagPanels) {
            tvp.deselect();
        }
    }
    
    @Override
    public String getField() {
        return container.getField();
    }
    
    public void edit() {
        container.editWrapper(wrapper);
    }
    
    @Override
    public JPanel getLinksPanel() {
        return panLinks;
    }
    
    private class TVC implements TagViewContainer {

        WrapperViewPanelExtended parent;
        
        public TVC(WrapperViewPanelExtended parent) {
            this.parent = parent;
        }        
        
        public void tagRemoved(Tag t) {
            wrapper.removeTag(t);
            reload();
            container.tagRemoved(parent, t);
        }

        public void tagDeleted(Tag t) {}

        public void editTag(Tag t) {}

        public boolean canDelete() {
            return false;
        }

        public Messenger getMessenger() {
            return container.getMessenger();
        }

        public String getField() {
            return "tag";
        }

        public WrapperPanel getParentPanel() {
            return parent;
        }
        
    }
    
    private class WPC implements WrapperPanelContainer {
                
        WrapperViewPanelExtended parent;
        
        public WPC(WrapperViewPanelExtended parent) {
            this.parent = parent;
        }
        
        public void reloaded() {
            if (!reloading) {
                reload();
            }
        }

        public void deleted(Wrapper wrapper) {            
            container.deleted(parent);
        }

        public void remove(Wrapper wrapper) {
            container.removed(parent);
        }
        
        public void editWrapper(Wrapper wrapper) {
            container.editWrapper(wrapper);
        }
        
    }
    
    private class WrapperListener implements MouseListener {
        
        private WrapperViewPanelExtended panel;

        public WrapperListener(WrapperViewPanelExtended panel) {
            this.panel = panel;
        }
        
        public void mouseClicked(MouseEvent me) {        
            if (me.getSource().equals(mainPanel.getLabName()) || 
                    me.getSource().equals(mainPanel)) {
                me.setSource(panel);
                container.getMouseListener().mouseClicked(me);
            } 
        }

        public void mousePressed(MouseEvent me) {
            if (me.getSource().equals(mainPanel.getLabName()) || 
                    me.getSource().equals(mainPanel)) {
                me.setSource(panel);
                container.getMouseListener().mousePressed(me);
            } 
        }

        public void mouseReleased(MouseEvent me) {
            me.setSource(panel);
            container.getMouseListener().mouseReleased(me);
        }

        public void mouseEntered(MouseEvent me) {
            me.setSource(panel);
            container.getMouseListener().mouseEntered(me);
    //        if (me.getSource().equals(labName)) {
       //         popupName.show(labName, 0, 15);    
       //     }            
        }

        public void mouseExited(MouseEvent me) {
            me.setSource(panel);
            container.getMouseListener().mouseExited(me);
      //      if (me.getSource().equals(labName)) {
       //         popupName.setVisible(false);    
        //    }  
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

        mainContainer = new javax.swing.JPanel();
        mainPanel = new entryorganizer.gui.entitypanel.WrapperViewPanel();
        butFold = new javax.swing.JButton();
        panTags = new javax.swing.JPanel();
        linksContainer = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(25, 0), new java.awt.Dimension(25, 0), new java.awt.Dimension(25, 0));
        panLinks = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        mainContainer.setLayout(new javax.swing.BoxLayout(mainContainer, javax.swing.BoxLayout.X_AXIS));

        mainPanel.setMaximumSize(new java.awt.Dimension(32767, 32767));
        mainContainer.add(mainPanel);

        butFold.setText("");
        butFold.setPreferredSize(new java.awt.Dimension(16, 16));
        butFold.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butFoldActionPerformed(evt);
            }
        });
        mainContainer.add(butFold);

        add(mainContainer);

        javax.swing.GroupLayout panTagsLayout = new javax.swing.GroupLayout(panTags);
        panTags.setLayout(panTagsLayout);
        panTagsLayout.setHorizontalGroup(
            panTagsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 244, Short.MAX_VALUE)
        );
        panTagsLayout.setVerticalGroup(
            panTagsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        add(panTags);

        linksContainer.setLayout(new javax.swing.BoxLayout(linksContainer, javax.swing.BoxLayout.X_AXIS));
        linksContainer.add(filler1);

        panLinks.setLayout(new javax.swing.BoxLayout(panLinks, javax.swing.BoxLayout.Y_AXIS));
        linksContainer.add(panLinks);

        add(linksContainer);
    }// </editor-fold>//GEN-END:initComponents

    private void butFoldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butFoldActionPerformed
        setShow(!show);
        reload();
    }//GEN-LAST:event_butFoldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butFold;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JPanel linksContainer;
    private javax.swing.JPanel mainContainer;
    private entryorganizer.gui.entitypanel.WrapperViewPanel mainPanel;
    private javax.swing.JPanel panLinks;
    private javax.swing.JPanel panTags;
    // End of variables declaration//GEN-END:variables
}
