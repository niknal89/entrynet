/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.gui.entitypanel;

import entryorganizer.Commander;
import entryorganizer.entities.wrappers.Tag;
import entryorganizer.gui.Messenger;
import entryorganizer.gui.Resources;
import entryorganizer.gui.browser.BrowserManager;
import entryorganizer.gui.browser.CaptionForge;
import entryorganizer.gui.entitypanel.TagForgePanel.TagForgeContainer;
import entryorganizer.gui.entitypanel.TagViewPanel.TagViewContainer;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

/**
 *
 * @author Администратор
 */
public class TagPanel extends ExtendedPanel {

    public interface TagContainer extends ExtendedPanel.Container {

        public Tag getTag();
        
        public Commander getCommander();
        
        public Messenger getMessenger();
        
        public void tagDeleted(TagPanel t);
        
        public void tagRemoved(TagPanel t);        
        
    }
    
    private Tag tag;
    private boolean editing;
    
    private Resources resources;
    private Commander commander;
    private TagForgePanel tfp;
    private TagContainer container;
    
    public TagPanel(TagContainer container, BrowserManager manager) {
        super(manager, container);
        initComponents();
        this.tag = container.getTag();
        this.resources = container.getCommander().getResources();
        this.commander = container.getCommander();
        this.container = container;
        TVC tvc = new TVC(this);
        mainPanel.initialize(tag, commander, manager, tvc);
        reload();
    }
    
    @Override
    public void reload() {
    //   int width = container.getSize().width - CaptionForge.TAB_WIDTH;
      //  this.setMaximumSize(new Dimension(width, Integer.MAX_VALUE));
        super.reload();
        if (show) {
            butFold.setIcon(resources.getImage(Resources.FOLD));
        } else {
            butFold.setIcon(resources.getImage(Resources.UNFOLD));            
        }
        validate();
        revalidate();
        repaint();
    }
    
    @Override
    public String getContent() {
        if (tag != null) {
            return tag.getName();
        }
        return null;
    }

    @Override
    public Tag getWrapper() {
        return tag;
    }

    @Override
    public void select() {
        this.setBackground(resources.getDefaultColor());
        if (mainPanel != null) {
            mainPanel.select();
        }
    }

    @Override
    public void deselect() {
        this.setBackground(resources.getDefaultColor());
        if (mainPanel != null) {
            mainPanel.deselect();
        }
    }
    
    public void editTag(Tag t) {        
        if (tfp == null) {
            tfp = new TagForgePanel(new TFC(), commander);
        }
        tfp.activate(t);
        mainContainer2.remove(mainPanel);
        mainContainer2.add(tfp);
        mainContainer2.repaint();
        mainContainer2.revalidate();
        editing = true;
    }

    @Override
    public void addMouseListener(MouseListener ml) {
        mainPanel.addMouseListener(ml);
    }
    
    @Override
    public void setTransferHandler(TransferHandler th) {
        mainPanel.setTransferHandler(th);
    }
        
    public void edit() {
        editTag(tag);
        requestFocus();
    }

    @Override
    public void requestFocus() {
        if (editing) {
            tfp.requestFocus();
        }
    }
    
    @Override
    public JPanel getLinksPanel() {
        return panLinks;
    }

    @Override
    public String getField() {
        return "tag";
    }
    
    private class TVC implements TagViewContainer {

        private TagPanel tp;
        
        public TVC(TagPanel tp) {
            this.tp = tp;
        }
        
        public void tagDeleted(Tag t) {
            container.tagDeleted(tp);
        }

        public void editTag(Tag t) {
            tp.editTag(t);
        }

        public void tagRemoved(Tag t) {
            container.tagRemoved(tp);
        }

        public boolean canDelete() {
            return true;
        }
                
        public Messenger getMessenger() {
            return container.getMessenger();
        }

        public String getField() {
            return "tag";
        }

        public WrapperPanel getParentPanel() {
            return tp;
        }
        
    }
        
    private class TFC implements TagForgeContainer {
        
        @Override
        public void tagForged(Tag t) {
            mainContainer2.remove(tfp);
            mainContainer2.add(mainPanel);
            mainPanel.reload();
            mainContainer2.revalidate();
            editing = false;
        }

        @Override
        public Messenger getMessenger() {
            return container.getMessenger();
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
        mainContainer2 = new javax.swing.JPanel();
        mainPanel = new entryorganizer.gui.entitypanel.TagViewPanel();
        butFold = new javax.swing.JButton();
        linksContainer = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(25, 0), new java.awt.Dimension(25, 0), new java.awt.Dimension(25, 0));
        panLinks = new javax.swing.JPanel();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        mainContainer.setLayout(new javax.swing.BoxLayout(mainContainer, javax.swing.BoxLayout.X_AXIS));

        mainContainer2.setLayout(new javax.swing.BoxLayout(mainContainer2, javax.swing.BoxLayout.Y_AXIS));
        mainContainer2.add(mainPanel);

        mainContainer.add(mainContainer2);

        butFold.setText("");
        butFold.setMaximumSize(new java.awt.Dimension(16, 16));
        butFold.setMinimumSize(new java.awt.Dimension(16, 16));
        butFold.setPreferredSize(new java.awt.Dimension(16, 16));
        butFold.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butFoldActionPerformed(evt);
            }
        });
        mainContainer.add(butFold);

        add(mainContainer);

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
    private javax.swing.JPanel mainContainer2;
    private entryorganizer.gui.entitypanel.TagViewPanel mainPanel;
    private javax.swing.JPanel panLinks;
    // End of variables declaration//GEN-END:variables
}
