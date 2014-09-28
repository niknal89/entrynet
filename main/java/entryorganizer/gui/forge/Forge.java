/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entryorganizer.gui.forge;

import entryorganizer.Commander;
import entryorganizer.entities.EntityType;
import entryorganizer.entities.representations.RepresentationSet;
import entryorganizer.entities.wrappers.Wrapper;
import entryorganizer.gui.Messenger;
import entryorganizer.gui.forge.FieldSet.FieldSetContainer;
import java.awt.Container;
import java.awt.Event;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 *
 * @author teopetuk89
 */
public class Forge extends javax.swing.JPanel {
   
    public interface ForgeParent {
        
        void cancelForge();
                
        RepresentationSet getViewOrder();
        
    }
    
    public interface ElementParent {
        
        String getField();
        
        void forgeCompleted(Wrapper wrapper);
        
    }
    
    private ForgeParent parent;
    private Commander commander;
    private ForgeCallback forgeCompleted;
    
    private Messenger messenger;
    private boolean forging;
    private boolean editing;
    private FieldSet fields;
    private JPanel fieldContainer;
    
    private static final String CREATE = "Создать";
    private static final String SAVE = "Сохранить";
    private static final KeyStroke nextSourceType = 
            KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, Event.CTRL_MASK);
    private static final KeyStroke prevSourceType = 
            KeyStroke.getKeyStroke(KeyEvent.VK_UP, Event.CTRL_MASK);
    
    /**
     * Creates new form SourceForgePanel
     */
    public Forge(Messenger messenger, Commander commander, ForgeParent parent) {
        initComponents();
     //   KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
     //   manager.addKeyEventDispatcher(new SourceForgeDispatcher());
        this.messenger = messenger;
        this.parent = parent;
        this.commander = commander;
        
        listType.setRenderer(new EntityTypeRenderer());
        listType.removeAllItems();
        for (EntityType et : commander.getEntityTypes()) {
            if (et.is(EntityType.TAG) || 
                    et.is(EntityType.ENTRY))
                continue;
            listType.addItem(et); 
        }
        checkButtons();
        
        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
                put(nextSourceType, "nextSourceType");
        this.getActionMap().put("nextSourceType", new NextAction());
        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
                put(prevSourceType, "prevSourceType");
        this.getActionMap().put("prevSourceType", new PreviousAction());
        
        /*
        scrollForgedEntity.getActionMap().put("unitScrollUp", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("sourceForge: up");
            }});
        scrollForgedEntity.getActionMap().put("unitScrollDown", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {                
                System.out.println("sourceForge: down");
        }});*/
    }
    
    public void forgeElementOfType(EntityType type, ForgeCallback callback) {
        forgeCompleted = callback;
        lockListType(type);
        startForge(null);
        checkButtons();
    }

    public void editElement(Wrapper wrapper, ForgeCallback callback) {
        if (wrapper == null) {
            editing = false; 
        } else { 
            editing = true;
            lockListType(wrapper.getWrapped().getType());
        }        
        forgeCompleted = callback;
        startForge(wrapper);
        checkButtons();
    }
      
    private void startForge(Wrapper wrapper) {        
        forging = true;
        fieldContainer = new JPanel();
        fieldContainer.setLayout(new BoxLayout(fieldContainer, BoxLayout.Y_AXIS));
        EntityType et;
        FieldsContainer fc;
        if (wrapper == null) {
            et = (EntityType) listType.getSelectedItem();
            fc = new FieldsContainer(this, et);
            fields = new FieldSet(fieldContainer, commander, fc);
        } else {
            et = wrapper.getWrapped().getType();
            fc = new FieldsContainer(this, et);
            fields = new FieldSet(fieldContainer, commander, fc);
            fields.setEntity(wrapper);
        }
        fields.setMessenger(messenger);
        fields.setrSet(parent.getViewOrder());
        scrollForgedEntity.setViewportView(fieldContainer);
        fields.reload();
        scrollForgedEntity.validate();
    }
    
    private void forge() {
        if (fields != null) {            
            fields.save();
        }
        scrollForgedEntity.setViewportView(new JPanel());
        scrollForgedEntity.validate();
        if (forgeCompleted != null) {
            forgeCompleted.forgeCompleted(fields.getEntity());
            forgeCompleted = null;
        }
        forging = false;
        fields = null;
    }
     
    private void cancelForge() {
        forging = false;
        fields = null;
        scrollForgedEntity.setViewportView(new JPanel());
        scrollForgedEntity.validate();
    }
    
    private void checkButtons() {
        if (isForging() == true) {
            butCancel.setEnabled(true);
            butNewSource.setText(SAVE);
        } else {
            butCancel.setEnabled(false);
            butNewSource.setText(CREATE);
        }
    }
             
    private void lockListType(EntityType type) {
        listType.removeAllItems();
        for (EntityType typeCheck : commander.getEntityTypes()) {
            if (typeCheck.hasParent(type)) {
                listType.addItem(typeCheck);
            }
        }
        listType.requestFocus();
    }    
       
    public boolean isForging() {
         return forging;
     }
             
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        listType = new javax.swing.JComboBox();
        scrollForgedEntity = new javax.swing.JScrollPane();
        butNewSource = new javax.swing.JButton();
        butCancel = new javax.swing.JButton();

        listType.setModel(new javax.swing.DefaultComboBoxModel(new String[] {}));
        listType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listTypeActionPerformed(evt);
            }
        });

        butNewSource.setText("Создать");
        butNewSource.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNewSourceActionPerformed(evt);
            }
        });

        butCancel.setText("Отмена");
        butCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollForgedEntity, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(butNewSource, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(listType, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(listType, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollForgedEntity, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butNewSource, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void listTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listTypeActionPerformed
        EntityType type = (EntityType) listType.getSelectedItem();
        if (fields != null)
            fields.setType(type);
    }//GEN-LAST:event_listTypeActionPerformed

    private void butNewSourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butNewSourceActionPerformed
        if (forging) {
            forge();
        } else {
            startForge(null);
        }
    }//GEN-LAST:event_butNewSourceActionPerformed

    private void butCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCancelActionPerformed
        cancelForge();
        parent.cancelForge();
    }//GEN-LAST:event_butCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butCancel;
    private javax.swing.JButton butNewSource;
    private javax.swing.JComboBox listType;
    private javax.swing.JScrollPane scrollForgedEntity;
    // End of variables declaration//GEN-END:variables
  
    private class NextAction extends AbstractAction {
        
        @Override
        public void actionPerformed(ActionEvent ae) {  
            if (!listType.isEnabled()) {
                return;
            }
            int i = listType.getSelectedIndex();
            i++;
            if (i < listType.getItemCount()) {
                listType.setSelectedIndex(i);
            } else {
                listType.setSelectedIndex(0);
            }
        }            
            
    }
    
    private class PreviousAction extends AbstractAction {
        
        @Override
        public void actionPerformed(ActionEvent ae) {   
            if (!listType.isEnabled()) {
                return;
            }  
            int i = listType.getSelectedIndex();
            i--;
            if (i > -1) {
                listType.setSelectedIndex(i);
            } else {
                listType.setSelectedIndex(listType.getItemCount() - 1);
            }
        }    
        
    }
    
    private class FieldsContainer implements FieldSetContainer {
        
        private Forge forge;
        private EntityType type;

        public FieldsContainer(Forge forge, EntityType type) {
            this.forge = forge;
            this.type = type;
        }

        public EntityType getType() {
            return type;
        }

        public void enterPressed() {
            if (forging) {
                forge();
            } else {
                startForge(null);
            }
        }

        public void selectField(FieldManager fm) {
            if (fm == null) {
                forge.requestFocus();
                return;
            }
            if (fm.getFieldPanel() == null) 
                return; 
            fm.getFieldPanel().requestFocus();
            Rectangle bounds = fm.getFieldPanel().getBounds();
            
            Container parent = fm.getFieldPanel().getParent();
            while (parent != null && !parent.equals(fieldContainer)) {
                Rectangle parentBounds = parent.getBounds();
                bounds.y += parentBounds.y;
                parent = parent.getParent();
            }
            
            Rectangle visible = scrollForgedEntity.getViewport().getViewRect();
            
            boolean scrollRequired = 
                (visible.y + visible.height) < (bounds.y + bounds.height) ||
                    visible.y > bounds.y;
            if (scrollRequired) {
                scrollForgedEntity.getVerticalScrollBar().setValue(0);
                scrollForgedEntity.getViewport().scrollRectToVisible(bounds);
            }
        }

        public void cancel() {
            cancelForge();
            parent.cancelForge();
        }
        
        public FieldSet getParent() {
            return null;
        }

        public void addElement(FieldManager fm) {}

        public void addField(FieldManager fm) {}
        
    }
    
}
