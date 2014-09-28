/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.gui.entitypanel;

import com.jidesoft.swing.StyledLabel;
import entryorganizer.Commander;
import entryorganizer.entities.exception.EntityException;
import entryorganizer.entities.wrappers.Entry;
import entryorganizer.entities.wrappers.Source;
import entryorganizer.entities.wrappers.Tag;
import entryorganizer.gui.Messenger;
import entryorganizer.gui.Resources;
import entryorganizer.gui.WrapLayout;
import entryorganizer.gui.browser.BrowserManager;
import entryorganizer.gui.browser.CaptionForge;
import entryorganizer.gui.entitypanel.SourceViewPanel.SourceContainer;
import entryorganizer.gui.entitypanel.TagViewPanel.TagViewContainer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;

/**
 *
 * @author Администратор
 */
public class EntryViewPanel extends ExtendedPanel {

    public interface EntryViewContainer extends ExtendedPanel.Container {
        
        public void editEntry(Entry e);
        public void entryDeleted(Entry e);
        public void entryRemoved(Entry e);
        public void tagBound();
        public void tagRemovedFromEntry(Tag tag);
        public Messenger getMessenger();
    }

    private Entry entry;    
    
    private List<TagViewPanel> tagPanels = new ArrayList<TagViewPanel>();
    
    private Commander commander;
    private Resources resources;
    private boolean full;
    
    private EntryViewContainer container;
    private MouseListener tagPanelListener;
    private EntryViewPanel.SL labContents;
    private TransferHandler transferHandler;
    private boolean selected;

    /**
     * Creates new form EntryViewPanel
     */
    public EntryViewPanel(Entry entry, EntryViewContainer container, 
           Commander commander, BrowserManager manager) {
        super(manager, container);
        this.entry = entry;
        this.resources = commander.getResources();
        this.commander = commander;
        this.container = container;
        this.tagPanelListener = container.getMouseListener();
        this.transferHandler = container.getTransferHandler();

        initComponents();
        sourceViewPanel.init(entry.getSource(), new EntryViewPanel.LocalListener(sourceViewPanel), 
                commander, container.getTransferHandler(), sc);
        sourceViewPanel.addMouseListener(tagPanelListener);
        sourceViewPanel.setTransferHandler(transferHandler);
        
        butEntry.setIcon(resources.getImage(Resources.ENTRY));
        butDelete.setIcon(resources.getImage(Resources.DELETE));
        butRemove.setIcon(resources.getImage(Resources.UNLINK));
         
        labContents = new EntryViewPanel.SL();
        labContents.addMouseListener(new LabListener());
        labContents.setLineWrap(true);
        panLabel.add(labContents);
        
        panTags.setLayout(new WrapLayout(FlowLayout.LEADING));
        this.setTransferHandler(container.getTransferHandler());  
        reload();
    }
    
    @Override
    public void reload() {
        int width = container.getSize().width - CaptionForge.TAB_WIDTH;
        this.setMaximumSize(new Dimension(width, Integer.MAX_VALUE));
        
        panTags.removeAll();
        tagPanels.clear();
        for (Tag t : entry.getTags()) {
            TagViewPanel tp = new TagViewPanel();
            tp.initialize(t, commander, browserManager, tc);
            tp.addMouseListener(tagPanelListener);   
            tp.setTransferHandler(transferHandler);
            panTags.add(tp);
            tagPanels.add(tp);
        }
        panTags.validate();
        
        if (entry.getPageEnd() > 0 && entry.getPageStart() > 0) {
            labPage.setText("С. " + entry.getPageStart() + "-" + entry.getPageEnd());
        } else if (entry.getPageStart() > 0) {
            labPage.setText("С. " + entry.getPageStart());
        } else {
            labPage.setText("");
        }    
        
        sourceViewPanel.setSource(entry.getSource());
        sourceViewPanel.validate();
        
        panLabel.removeAll();
        String content = entry.getContent(full);
        if (!full && content.length() == Entry.SHORT_CONTENT_LENGTH) {
            content =          
                content + " ..." +
                System.getProperty("line.separator") + "Читать далее";
        } 
        labContents.setText(content);
        panLabel.add(labContents);
        Component c = Box.createVerticalGlue();
        panLabel.add(c);
        panLabel.validate();
        
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
    }
    
    public void edit() {
        container.editEntry(entry);
    }
    
    private class LabListener implements MouseListener {

        public void mouseClicked(MouseEvent me) {
            if (me.getSource().equals(labContents)) {
                full = true;
                reload();
            }
        }
        
        public void mousePressed(MouseEvent me) {}
        
        public void mouseReleased(MouseEvent me) {}    
        
        public void mouseEntered(MouseEvent me) {}

        public void mouseExited(MouseEvent me) { 
        }

    } 
        
    public void bindToTag(Tag t) {
        try {
            entry.addTag(t);
        } catch (EntityException ex) {
            container.getMessenger().postMessage(ex.getMessage());
        }
        container.tagBound();
        reload();
    }    
    
    public void setSource(Source s) {
        try {        
            entry.setSource(s);
        } catch (EntityException ex) {
            container.getMessenger().postMessage(ex.getMessage());
        }
        reload();
    }
        
    @Override
    public String getContent() {
        if (entry != null) {
            return entry.getContent(full);
        } else {
            return null;
        }
    }

    @Override
    public void select() {
        this.selected = true;
        this.setBackground(resources.getSelectedColor());
        panLabel.setBackground(resources.getSelectedColor());
        panContent.setBackground(resources.getSelectedColor());
        panButtons1.setBackground(resources.getSelectedColor());
        panButtons2.setBackground(resources.getSelectedColor());
        panSourcePage.setBackground(resources.getSelectedColor());
        panTags.setBackground(resources.getSelectedColor());
        for (TagViewPanel tvp : tagPanels) {
            tvp.select();
        }
        sourceViewPanel.select();
    }

    @Override
    public void deselect() {
        selected = false;
        this.setBackground(resources.getDefaultColor());
        panLabel.setBackground(resources.getDefaultColor());
        panContent.setBackground(resources.getDefaultColor());
        panButtons1.setBackground(resources.getDefaultColor());
        panButtons2.setBackground(resources.getDefaultColor());
        panSourcePage.setBackground(resources.getDefaultColor());
        panTags.setBackground(resources.getDefaultColor());
        for (TagViewPanel tvp : tagPanels) {
            tvp.deselect();
        }
        sourceViewPanel.deselect();
    }

    @Override
    public Entry getWrapper() {
        return entry;
    }

    public String getField() {
        return container.getField();
    }

    @Override
    public JPanel getLinksPanel() {
        return panLinks;
    }
        
    private EntryViewPanel getThis() {
        return this;
    }
    
    private class SL extends StyledLabel {

        @Override
        public void setPreferredWidth(int preferredWidth) {
            super.setPreferredWidth(preferredWidth); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setPreferredSize(Dimension preferredSize) {
            super.setPreferredSize(preferredSize); 
        }
        
    }
    
    private SourceContainer sc = new SourceContainer() {

        public void removeSource(Source s) {
            if (entry.getSource() != null) {
                if (entry.getSource().equals(s)) {
                    setSource(null);
                }
            }
        }

        public WrapperPanel getParentPanel() {
            return getThis();
        }

    };

    private TagViewContainer tc = new TagViewContainer() {

        public void tagRemoved(Tag t) {
            entry.removeTag(t);
            t.removeLink(entry);
            container.tagRemovedFromEntry(t);
            reload();
        }

        public Icon removerIcon() {
            return resources.getImage(Resources.UNLINK);
        }

        @Override
        public void tagDeleted(Tag t) {}

        @Override
        public void editTag(Tag t) {
        }

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
            return getThis();
        }

    };
       
    private class LocalListener implements MouseListener {
        
        private SourceViewPanel panel;

        public LocalListener(SourceViewPanel panel) {
            this.panel = panel;
        }
        
        public void mouseClicked(MouseEvent me) {
            me.setSource(panel);
            container.getMouseListener().mouseClicked(me);
        }
        
        public void mousePressed(MouseEvent me) {
            me.setSource(panel);
            container.getMouseListener().mousePressed(me);
        }
        
        public void mouseReleased(MouseEvent me) {        
            me.setSource(panel);
            container.getMouseListener().mouseReleased(me);
        }    
        
        public void mouseEntered(MouseEvent me) {        
            me.setSource(panel);
            container.getMouseListener().mouseEntered(me);
        }

        public void mouseExited(MouseEvent me) {
            me.setSource(panel);
            container.getMouseListener().mouseExited(me);            
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

        panContent = new javax.swing.JPanel();
        panButtons1 = new javax.swing.JPanel();
        butEntry = new javax.swing.JButton();
        butRemove = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        panLabel = new javax.swing.JPanel();
        panButtons2 = new javax.swing.JPanel();
        butFold = new javax.swing.JButton();
        butDelete = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        panSourcePage = new javax.swing.JPanel();
        sourceViewPanel = new entryorganizer.gui.entitypanel.SourceViewPanel();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        labPage = new javax.swing.JLabel();
        panTags = new javax.swing.JPanel();
        panLinksContainer = new javax.swing.JPanel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(25, 0), new java.awt.Dimension(25, 0), new java.awt.Dimension(25, 0));
        panLinks = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        panContent.setLayout(new javax.swing.BoxLayout(panContent, javax.swing.BoxLayout.LINE_AXIS));

        panButtons1.setLayout(new javax.swing.BoxLayout(panButtons1, javax.swing.BoxLayout.Y_AXIS));

        butEntry.setText("");
        butEntry.setMaximumSize(new java.awt.Dimension(16, 16));
        butEntry.setMinimumSize(new java.awt.Dimension(16, 16));
        butEntry.setPreferredSize(new java.awt.Dimension(16, 16));
        butEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butEntryActionPerformed(evt);
            }
        });
        panButtons1.add(butEntry);

        butRemove.setText("");
        butRemove.setMaximumSize(new java.awt.Dimension(16, 16));
        butRemove.setMinimumSize(new java.awt.Dimension(16, 16));
        butRemove.setPreferredSize(new java.awt.Dimension(16, 16));
        butRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butRemoveActionPerformed(evt);
            }
        });
        panButtons1.add(butRemove);
        panButtons1.add(filler1);

        panContent.add(panButtons1);

        panLabel.setLayout(new javax.swing.BoxLayout(panLabel, javax.swing.BoxLayout.Y_AXIS));
        panContent.add(panLabel);

        panButtons2.setLayout(new javax.swing.BoxLayout(panButtons2, javax.swing.BoxLayout.Y_AXIS));

        butFold.setText("");
        butFold.setMaximumSize(new java.awt.Dimension(16, 16));
        butFold.setMinimumSize(new java.awt.Dimension(16, 16));
        butFold.setPreferredSize(new java.awt.Dimension(16, 16));
        butFold.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butFoldActionPerformed(evt);
            }
        });
        panButtons2.add(butFold);

        butDelete.setText("");
        butDelete.setMaximumSize(new java.awt.Dimension(16, 16));
        butDelete.setMinimumSize(new java.awt.Dimension(16, 16));
        butDelete.setPreferredSize(new java.awt.Dimension(16, 16));
        butDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butDeleteActionPerformed(evt);
            }
        });
        panButtons2.add(butDelete);
        panButtons2.add(filler2);

        panContent.add(panButtons2);

        add(panContent);

        panSourcePage.setLayout(new javax.swing.BoxLayout(panSourcePage, javax.swing.BoxLayout.X_AXIS));
        panSourcePage.add(sourceViewPanel);
        panSourcePage.add(filler4);

        labPage.setText("");
        panSourcePage.add(labPage);

        add(panSourcePage);
        add(panTags);

        panLinksContainer.setLayout(new javax.swing.BoxLayout(panLinksContainer, javax.swing.BoxLayout.LINE_AXIS));
        panLinksContainer.add(filler3);

        panLinks.setLayout(new javax.swing.BoxLayout(panLinks, javax.swing.BoxLayout.Y_AXIS));
        panLinksContainer.add(panLinks);

        add(panLinksContainer);
    }// </editor-fold>//GEN-END:initComponents

    private void butRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butRemoveActionPerformed
         container.entryRemoved(entry);
    }//GEN-LAST:event_butRemoveActionPerformed

    private void butDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butDeleteActionPerformed
        entry.delete();
        container.entryDeleted(entry);
        entry = null;
    }//GEN-LAST:event_butDeleteActionPerformed

    private void butEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butEntryActionPerformed
        container.editEntry(entry);
    }//GEN-LAST:event_butEntryActionPerformed

    private void butFoldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butFoldActionPerformed
        setShow(!show);
        reload();
    }//GEN-LAST:event_butFoldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butDelete;
    private javax.swing.JButton butEntry;
    private javax.swing.JButton butFold;
    private javax.swing.JButton butRemove;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.JLabel labPage;
    private javax.swing.JPanel panButtons1;
    private javax.swing.JPanel panButtons2;
    private javax.swing.JPanel panContent;
    private javax.swing.JPanel panLabel;
    private javax.swing.JPanel panLinks;
    private javax.swing.JPanel panLinksContainer;
    private javax.swing.JPanel panSourcePage;
    private javax.swing.JPanel panTags;
    private entryorganizer.gui.entitypanel.SourceViewPanel sourceViewPanel;
    // End of variables declaration//GEN-END:variables
}
