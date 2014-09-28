/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entryorganizer.gui.forge;

import entryorganizer.Commander;
import entryorganizer.entities.Entity;
import entryorganizer.entities.EntityType;
import entryorganizer.entities.Field;
import entryorganizer.entities.Parameter;
import entryorganizer.entities.Text;
import entryorganizer.entities.wrappers.Wrapper;
import entryorganizer.gui.Resources;
import entryorganizer.gui.searchfield.SearchEntityField;
import entryorganizer.gui.searchfield.SearchEntityField.SearcherContainer;
import entryorganizer.gui.searchfield.SearchField;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;

/**
 *
 * @author teopetuk89
 */
public abstract class ValuePanel <F extends Field, V extends Object> extends 
        FieldPanel <F, V> {

    public interface ValuePanelContainer {

        public void addWrapper(Wrapper w);
        public void postMessage(String s);
        public void focusGained(ValuePanel sfp);
        public boolean searchPossible();
        public boolean severalPossible();
        public Entity getParentEntity();
        public EntityType getParentType();
        public ActionListener getActionListener();
        public Commander getCommander();
        
    }
        
    private FieldButton butDelete;
    private Icon deleteIcon;
    private ValuePanelContainer container;
    private Commander commander;
    private SearchField searcher;
    
    public static final String ADD_SOURCE_FIELD = "add_source_field";
    public static final String DELETE_SOURCE_FIELD = "delete_source_field";
        
    
    /**
     * Creates new form SourceFieldPanel
     */
    public ValuePanel(String name, ValuePanelContainer container) {
                super(name, container.getCommander().getDataManager());
        initialize(container, container.getCommander());
    }
    
    public ValuePanel(F field, ValuePanelContainer container) {
        super(field, container.getCommander().getDataManager());
        this.commander = container.getCommander();
        initialize(container, commander); 
        String value = "";
        if (field instanceof Text) {
            value = ((Text) field).getText();
        } else if (field instanceof Parameter) {
            value = "" + ((Parameter) field).getValue();
        }
        setValue(value);
    }
        
    private void initialize(ValuePanelContainer container, Commander commander) {
        initComponents();
        deleteIcon = commander.getResources().getImage(Resources.DELETE);
        this.container = container;
        this.fieldValue.addFocusListener(new SFPFocusListener(this));
        if (container.searchPossible()) {
            SearchEntityField sef = 
                            new SearchEntityField(commander, new SC(), fieldValue);
            sef.setSearchRequirements(container.getParentType(), name);
            this.searcher = sef;
        }
        reload();
    }

    public void reload() {
        labName.setText(name);
        
        if (butDelete == null) {
            butDelete = new FieldButton(deleteIcon, this);
            butDelete.setActionCommand(DELETE_SOURCE_FIELD);
            butDelete.addActionListener(container.getActionListener());
            add(butDelete);
        }          

        setFocusTraversalKeysEnabled(false);
        if (butDelete != null)
            butDelete.setFocusTraversalKeysEnabled(false);
        fieldValue.setFocusTraversalKeysEnabled(false);
    }
    
    @Override
    public void requestFocus() {
        fieldValue.requestFocusInWindow();
    }
    
    @Override
    public void addKeyListener(KeyListener listener) {
        fieldValue.addKeyListener(listener);
    }
           
    private class SFPFocusListener implements FocusListener {
        
        private ValuePanel sfp;
        
        public SFPFocusListener(ValuePanel sfp) {
            this.sfp = sfp;
        }

        public ValuePanel getSfp() {
            return sfp;
        }
                
        @Override
        public void focusGained(FocusEvent fe) {
            sfp.container.focusGained(sfp);
        }

        @Override
        public void focusLost(FocusEvent fe) {}
        
    }
    
    private class SC implements SearcherContainer {
        
        public void selected(Wrapper e) {
            container.addWrapper(e);
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

        labName = new javax.swing.JLabel();
        fieldValue = new javax.swing.JTextField();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));

        setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        setMaximumSize(new java.awt.Dimension(2147483647, 25));
        setMinimumSize(new java.awt.Dimension(200, 25));
        setPreferredSize(new java.awt.Dimension(400, 25));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));

        labName.setText("");
        add(labName);

        fieldValue.setText("");
        fieldValue.setMinimumSize(new java.awt.Dimension(300, 19));
        add(fieldValue);
        add(filler1);
        add(filler2);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField fieldValue;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JLabel labName;
    // End of variables declaration//GEN-END:variables
    
    public String getValueStr() {
        return fieldValue.getText();        
    }
        
    public void setValue(String s) {
      /*  if (fieldValue.getText().isEmpty() && 
                 type != SourceFieldType.authorName() && 
                   type != SourceFieldType.authorPatronymic() && 
                   type != SourceFieldType.editorName() && 
                   type != SourceFieldType.editorPatronymic()) {
            butDelete = new SourceFieldButton(deleteIcon, this);
            butDelete.setActionCommand(DELETE_SOURCE_FIELD);
            butDelete.addActionListener(container);
            add(butDelete);
        } */
        fieldValue.removeCaretListener(searcher);
        fieldValue.setText(s);
        fieldValue.addCaretListener(searcher);
        reload();
    }

}