/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.gui.browser;

import entryorganizer.Commander;
import entryorganizer.datastorage.DataManager;
import entryorganizer.entities.EntityType;
import entryorganizer.entities.FieldLimiters;
import entryorganizer.entities.Link;
import entryorganizer.entities.exception.EntityException;
import entryorganizer.entities.wrappers.Tag;
import entryorganizer.entities.wrappers.Wrapper;
import entryorganizer.gui.Resources;
import entryorganizer.gui.entitypanel.EntityPanel;
import entryorganizer.gui.entitypanel.ExtendedPanel;
import entryorganizer.gui.entitypanel.WrapperPanel;
import entryorganizer.gui.forge.ForgeCallback;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

/**
 *
 * @author Администратор
 */
public class Caption extends EntityPanel implements Container <WrapperPanel> {
    
    public interface CaptionContainer {
                        
        public void validate();
        
    }
    
    private Resources res;
    private DataManager dataManager;
    private Commander commander;
    private BrowserManager browserManager;
    private TransferHandler transferHandler;
    private MouseListener listener;
    private Selector selector;
    private CaptionForge forge;
    private CaptionContainer container;
    
    private FieldLimiters field;   
    private Wrapper wrapper;
    private List<Link> fields = new ArrayList<Link>();
    private List<WrapperPanel> panels = new ArrayList<WrapperPanel>();
    private boolean show;
    private boolean panelsAreFields;
    
    /**
     * Creates new form CaptionPanel
     */
    public Caption(Commander commander, CaptionContainer container, FieldLimiters field, 
            Wrapper parent, BrowserManager browserManager) {
        initComponents();
        this.container = container;
        this.field = field;
        this.wrapper = parent;
        this.commander = commander;
        this.res = commander.getResources();
        this.dataManager = commander.getDataManager();
        this.butAdd.setIcon(res.getImage(Resources.ADD));
        this.butFold.setIcon(res.getImage(Resources.UNFOLD));
        this.browserManager = browserManager;
        this.selector = new Selector(this);
        this.forge = new CaptionForge(browserManager, this, commander); 
    }    
    
    public void setFields(List<Link> fields) {
        this.fields = fields;
        panelsAreFields = false;
    }
    
    public void reload() {
        if (show) {
            if (!panelsAreFields) {
                panels.clear();
                for (Link l : fields) {          
                    Wrapper w = dataManager.loadLink(l, new Wrapper());
                    WrapperPanel wp = forge.forgeWrapperPanel(w, l.getName());
                    panels.add(wp);
                }     
            }
            
            butFold.setIcon(res.getImage(Resources.FOLD));
            panContent.removeAll();
            for (EntityPanel wp : panels) {
                panContent.add(wp);
            }
            panContent.revalidate();
            panContent.repaint();
            container.validate();
            for (EntityPanel wp : panels) {
                wp.reload();
            }
        } else {
            butFold.setIcon(res.getImage(Resources.UNFOLD));
            panContent.removeAll();
            panContent.revalidate();
            panContent.repaint();
        }
        if (field.getLimit() != -1 && field.getLimit() <= panels.size()) {
            butAdd.setEnabled(false);
        } else {
            butAdd.setEnabled(true);
        }
    }
        
    public void forgeNew() {
        EntityType type = commander.getEntityType(field.getType());
        if (!butFold.isEnabled() || type == null)
            return;    
        
        if (type.is(EntityType.TAG) || type.is(EntityType.ENTRY)) {
            Wrapper w = dataManager.forgeWrapper(type);
            forgePanel(w);
        } else {
            ForgeCallback fc = new ForgeCallback(field.getName()) {

                @Override
                public void forgeCompleted(Wrapper w) {
                    if (wrapper == null) {
                        return;
                    }
                    forgePanel(w);
                    browserManager.forgeCompleted();
                }
                
            };
            browserManager.forgeElement(type, fc);
        }
    }
    
    private void forgePanel(Wrapper w) {
        try {
            Link l = wrapper.link(w, field.getName());
            WrapperPanel panel = forge.forgeWrapperPanel(w, field.getName());
            fields.add(l);
            addField(panel);   
        } catch (EntityException ex) {
            browserManager.getMessenger().postMessage(ex.getMessage());
        }    
    }
    
    public FieldLimiters getField() {
        return field;
    }
       
    @Override
    public void select() {
        panHeading.setBackground(res.getSelectedColor());
    }
    
    @Override
    public void deselect() {
        panHeading.setBackground(res.getDefaultColor());
    }
    
    public void setText(String string) {
        labName.setText(string);
        this.validate();
    }

    public void setPanels(List<WrapperPanel> fields) {
        this.panels = fields;
    }

    @Override
    public List<WrapperPanel> getPanels() {
        return panels;
    }
    
    public void addField(WrapperPanel panel) {
        this.panels.add(panel);
        this.panContent.add(panel);
        this.panContent.revalidate();
        this.panContent.repaint();
    }
    
    public void removeField(WrapperPanel panel) {
        if (panel.getWrapper() != null) {
            Iterator<Link> it = fields.iterator();
            while (it.hasNext()) {
                Link l = it.next();
                if (l.getId() == panel.getWrapper().getID().getId()) {
                    it.remove();
                }
            }
        }
        this.panels.remove(panel);
        this.panContent.remove(panel);
        this.panContent.revalidate();
        this.panContent.repaint();
    }

    public void unlinkField(ExtendedPanel panel) {
        if (wrapper != null && panel.getWrapper() != null) {
            int id = panel.getWrapper().getWrapped().getIdInt();
            String field = panel.getField();
            wrapper.removeLink(id, field);
        }
        removeField(panel);
    }
    
    public void tagRemovedFromPanel(ExtendedPanel panel, Tag tag) {
        if (wrapper != null && wrapper.equals(tag)) {
            removeField(panel);
        }
    }
    
    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
        browserManager.showCaption(this, show);
    }

    public void setShowWithoutNotifying(boolean show) {
        this.show = show;
        reload();
    }
    
    public void setHandler(TransferHandler handler) {
        this.forge.setTransferHandler(handler);
    }

    public void setListener(MouseListener listener) {
        this.forge.setListener(listener); 
    }
    
    public Selector getSelector() {
        return selector;
    }

    public Wrapper getWrapper() {
        return wrapper;
    }
    
    public EntityPanel getHeading() {
        return this;
    }

    public JPanel getPanContent() {
        return panContent;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panHeading = new javax.swing.JPanel();
        labName = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        butAdd = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(15, 0), new java.awt.Dimension(15, 0), new java.awt.Dimension(15, 32767));
        butFold = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(25, 0), new java.awt.Dimension(25, 0), new java.awt.Dimension(25, 0));
        panContent = new javax.swing.JPanel();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        panHeading.setLayout(new javax.swing.BoxLayout(panHeading, javax.swing.BoxLayout.LINE_AXIS));

        labName.setText("");
        panHeading.add(labName);
        panHeading.add(filler2);

        butAdd.setText("");
        butAdd.setMaximumSize(new java.awt.Dimension(24, 24));
        butAdd.setMinimumSize(new java.awt.Dimension(24, 24));
        butAdd.setPreferredSize(new java.awt.Dimension(24, 24));
        butAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butAddActionPerformed(evt);
            }
        });
        panHeading.add(butAdd);
        panHeading.add(filler3);

        butFold.setText("");
        butFold.setMaximumSize(new java.awt.Dimension(24, 24));
        butFold.setMinimumSize(new java.awt.Dimension(24, 24));
        butFold.setPreferredSize(new java.awt.Dimension(24, 24));
        butFold.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butFoldActionPerformed(evt);
            }
        });
        panHeading.add(butFold);

        add(panHeading);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));
        jPanel1.add(filler1);

        panContent.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panContent.setLayout(new javax.swing.BoxLayout(panContent, javax.swing.BoxLayout.Y_AXIS));
        panContent.add(filler4);

        jPanel1.add(panContent);

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void butAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butAddActionPerformed
        forgeNew();
    }//GEN-LAST:event_butAddActionPerformed

    private void butFoldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butFoldActionPerformed
        setShow(!show);
        reload();
    }//GEN-LAST:event_butFoldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butAdd;
    private javax.swing.JButton butFold;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel labName;
    private javax.swing.JPanel panContent;
    private javax.swing.JPanel panHeading;
    // End of variables declaration//GEN-END:variables

}
