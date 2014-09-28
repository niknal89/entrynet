/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entryorganizer.gui.entitypanel;

import com.jidesoft.swing.StyledLabel;
import entryorganizer.entities.wrappers.Wrapper;
import entryorganizer.gui.Resources;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.JLabel;

/**
 *
 * @author teopetuk89
 */
public class WrapperViewPanel extends WrapperPanel <Wrapper> {

    public interface WrapperPanelContainer {
        
        public void reloaded();
        
        public void deleted(Wrapper wrapper);
        
        public void remove(Wrapper wrapper);
        
        public void editWrapper(Wrapper wrapper);
        
    }
    
    private Wrapper wrapper;
    private Resources resources;
    private WrapperPanelContainer container;
    private StyledLabel labName;
    
    private boolean show;    
    
    /**
     * Creates new form AuthorViewPanel
     */
    public WrapperViewPanel() {
        initComponents();
    }

    public void initialize(Wrapper wrapper, Resources resources, WrapperPanelContainer container) {
        this.wrapper = wrapper;
        this.container = container;
        labName = new StyledLabel();
        if (wrapper != null) {
            labName.setText(wrapper.getShortDescription());
        }
        labName.setLineWrap(true);
        panLabel.add(labName);
        panLabel.add(Box.createVerticalGlue());
        this.resources = resources;
        butWrapper.setIcon(resources.getImage(Resources.WRAPPER));
        butRemove.setIcon(resources.getImage(Resources.UNLINK));
    }
    
    public Wrapper getWrapper() {
        return wrapper;
    }
    
    private boolean parentCall;
    
    public void reload() {
        container.reloaded();
        panLabel.removeAll();
        if (wrapper != null) {
            labName.setText(wrapper.getShortDescription());            
        }
        panLabel.add(labName);
        panLabel.add(Box.createVerticalGlue());
        panLabel.validate();
    }
    
    public void edit() {
        container.editWrapper(wrapper);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        butWrapper = new javax.swing.JButton();
        butRemove = new javax.swing.JButton();
        panLabel = new javax.swing.JPanel();

        setMaximumSize(new java.awt.Dimension(32767, 15));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.X_AXIS));

        butWrapper.setText("");
        butWrapper.setMaximumSize(new java.awt.Dimension(16, 16));
        butWrapper.setMinimumSize(new java.awt.Dimension(16, 16));
        butWrapper.setPreferredSize(new java.awt.Dimension(16, 16));
        butWrapper.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butWrapperActionPerformed(evt);
            }
        });
        add(butWrapper);

        butRemove.setText("");
        butRemove.setMaximumSize(new java.awt.Dimension(16, 16));
        butRemove.setMinimumSize(new java.awt.Dimension(16, 16));
        butRemove.setPreferredSize(new java.awt.Dimension(16, 16));
        butRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butRemoveActionPerformed(evt);
            }
        });
        add(butRemove);

        panLabel.setLayout(new javax.swing.BoxLayout(panLabel, javax.swing.BoxLayout.Y_AXIS));
        add(panLabel);
    }// </editor-fold>//GEN-END:initComponents

    private void butWrapperActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butWrapperActionPerformed
        container.editWrapper(wrapper);
    }//GEN-LAST:event_butWrapperActionPerformed

    private void butRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butRemoveActionPerformed
        container.remove(wrapper);
    }//GEN-LAST:event_butRemoveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butRemove;
    private javax.swing.JButton butWrapper;
    private javax.swing.JPanel panLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public String getContent() {
        if (wrapper != null)
            return wrapper.getFullDescription();
        else 
            return "";
    }

    @Override
    public void select() {
        this.setBackground(resources.getSelectedColor());
        panLabel.setBackground(resources.getSelectedColor());
    }

    @Override
    public void deselect() {
        this.setBackground(resources.getDefaultColor());
        panLabel.setBackground(resources.getDefaultColor());
    }
    
    public JLabel getLabName() {
        return labName;
    }

}
