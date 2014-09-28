/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.gui.forge;

import entryorganizer.entities.Field;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.JTextField;

/**
 *
 * @author Администратор
 */
public class FieldManager {
    
    public interface FieldManagerContainer {
        
        public FieldSet getParent();
        
        public void enterPressed();
        
        public void cancel();
        
        public void selectField(FieldManager fm);
        
        public void addField(FieldManager fm);
        
        public void addElement(FieldManager fm);
    }
    
    protected FieldPanel fieldPanel;
    protected FieldManager previous; 
    protected FieldManager next;
    
    private FieldManagerContainer container;
        
    public FieldManager(FieldPanel fp, FieldManagerContainer container) {
        this.fieldPanel = fp;
        this.container = container;
        if (fieldPanel instanceof ValuePanel) {
            ((ValuePanel) fieldPanel).addKeyListener(new FSKeyListener(this));
        }
    }

    public void setPrevious(FieldManager previous) {
        if (previous != null)
            this.previous = previous.getLast();
    }
    
    public void setNext(FieldManager next) {
        if (next != null)
            this.next = next.getFirst();
    }
        
    public FieldManager getPrevious() {
        return previous;
    }

    public FieldManager getNext() {
        return next;
    }

    public FieldPanel getFieldPanel() {
        return fieldPanel;
    }
    
    public Field getField() {
        if (fieldPanel == null) return null;
        return fieldPanel.getField();
    }
    
    public boolean isEmpty() {
        if (fieldPanel.getValue() == null)
            return true;
        else 
            return false;
    }
   
    public void save() {
        fieldPanel.saveField();
    }    
    
    public class FSKeyListener implements KeyListener {
        
        private FieldManager fieldManager;

        public FSKeyListener(FieldManager fieldManager) {
            this.fieldManager = fieldManager;
        }
        
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_TAB &&
                    e.getModifiersEx() != InputEvent.ALT_DOWN_MASK &&
                    e.getSource() instanceof JTextField) {
                if ((e.getModifiersEx() == InputEvent.CTRL_DOWN_MASK || 
                            e.getModifiersEx() == 65664)) {
                    skipField(e, true);
                } else {
                    skipField(e, false);
                }
                e.consume();
            } else if (e.getKeyCode() == KeyEvent.VK_ENTER &&
                    (e.getModifiersEx() == InputEvent.CTRL_DOWN_MASK || 
                            e.getModifiersEx() == 65664)) {
                container.enterPressed();
            } else if (e.getKeyCode() == KeyEvent.VK_Z &&
                    (e.getModifiersEx() == InputEvent.CTRL_DOWN_MASK || 
                            e.getModifiersEx() == 65664)) {
                container.cancel();
            } else if (e.getKeyCode() == KeyEvent.VK_F &&
                    e.getModifiersEx() == InputEvent.CTRL_DOWN_MASK &&
                    e.getSource() instanceof JTextField) {
                container.addField(fieldManager);
            } else if (e.getKeyCode() == KeyEvent.VK_E &&
                    e.getModifiersEx() == InputEvent.CTRL_DOWN_MASK &&
                    e.getSource() instanceof JTextField) {
                container.addElement(fieldManager);
            } 
        }

        private void skipField(KeyEvent e, boolean backward) {
            FieldManager skipTo = null;
            if (backward) {
                skipTo = previous;
            } else {
                skipTo = next;
            }
            if (skipTo == null) {
                return;
            }
            
            container.selectField(skipTo);
        }    

        public void keyReleased(KeyEvent e) {}
        
        public void keyTyped(KeyEvent e) {}
               
    }
    
    public FieldManager getFirst() {
        return this;
    }
    
    public FieldManager getLast() {
        return this;
    }
    
    public FieldSet getParent() {
        return container.getParent();
    }
}
