/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.gui.browser;

import entryorganizer.entities.wrappers.Wrapper;
import entryorganizer.gui.entitypanel.EntityPanel;
import entryorganizer.gui.entitypanel.WrapperPanel;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 *
 * @author Администратор
 */
public class KeyManager {
    
    private ContentPanel contentPanel;
    private BrowserManager browserManager;
    private Browser browser;
    
    private List<Caption> currentContent;

    private Caption selectedField;
    private List<Container> selectedStack = new ArrayList<Container>();
    //private List<Object> entitiesStack = new ArrayList<Object>();
    private boolean end;
    private InputMap savedMap;
        
    public static final KeyStroke NEXT = 
            KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, Event.CTRL_MASK);
    public static final KeyStroke PREVIOUS = 
            KeyStroke.getKeyStroke(KeyEvent.VK_UP, Event.CTRL_MASK);
    public static final KeyStroke EXPAND = 
            KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, Event.CTRL_MASK);
    public static final KeyStroke CONTRACT = 
            KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, Event.CTRL_MASK);
    public static final KeyStroke NEW_ELEMENT = 
            KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK);
    public static final KeyStroke EDIT_ELEMENT = 
            KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK);
    public static final KeyStroke NEXT_KEY = 
            KeyStroke.getKeyStroke(KeyEvent.VK_K, Event.CTRL_MASK);
    public static final KeyStroke SHOW_ELEMENT = 
            KeyStroke.getKeyStroke("ENTER");
    public static final KeyStroke SHOW_IN_NEW_BROWSER = 
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, Event.CTRL_MASK);
    public static final KeyStroke NEXT_BROWSER = 
            KeyStroke.getKeyStroke(KeyEvent.VK_TAB, Event.CTRL_MASK);
    public static final KeyStroke CLOSE_BROWSER = 
            KeyStroke.getKeyStroke(KeyEvent.VK_W, Event.CTRL_MASK);
    public static final KeyStroke NEW_BROWSER = 
            KeyStroke.getKeyStroke(KeyEvent.VK_B, Event.CTRL_MASK);
    
    public KeyManager(BrowserManager browserManager, ContentPanel contentPanel) {
        this.contentPanel = contentPanel;
        this.browserManager = browserManager;
        this.browser = browserManager.getBrowser();
        
        browser.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
                put(KeyManager.NEXT, "next_element");
        browser.getActionMap().put("next_element", new Next());
        browser.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
                put(KeyManager.PREVIOUS, "prev_element");
        browser.getActionMap().put("prev_element", new Previous());
        
        browser.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
                put(KeyManager.EXPAND, "expand");
        browser.getActionMap().put("expand", new Expand());
        browser.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
                put(KeyManager.CONTRACT, "contract");
        browser.getActionMap().put("contract", new Contract());
        
        
        browser.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
                put(KeyManager.NEW_ELEMENT, "new_element");
        browser.getActionMap().put("new_element", new New());
        
        browser.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
                put(KeyManager.EDIT_ELEMENT, "edit_element");
        browser.getActionMap().put("edit_element", new Edit());
        
        browser.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
                put(KeyManager.SHOW_ELEMENT, "show_element");
        browser.getActionMap().put("show_element", new Show());
        
        browser.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
                put(KeyManager.NEXT_KEY, "next_key");
        browser.getActionMap().put("next_key", new NextKey());
        
        browser.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
                put(KeyManager.SHOW_IN_NEW_BROWSER, "in_new_browser");
        browser.getActionMap().put("in_new_browser", new ShowInBrowser());
        
        browser.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
                put(KeyManager.NEW_BROWSER, "new_browser");
        browser.getActionMap().put("new_browser", new NewBrowser());
        browser.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
                put(KeyManager.CLOSE_BROWSER, "close_browser");
        browser.getActionMap().put("close_browser", new CloseBrowser());
        browser.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
                put(KeyManager.NEXT_BROWSER, "next_browser");
        browser.getActionMap().put("next_browser", new NextBrowser());
        savedMap = browser.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }  
    
    public void block(boolean block) {
        if (block) {
            InputMap emptyMap = new InputMap();
            browser.setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, emptyMap);
        } else {
            browser.setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, savedMap);
        }
    }
    
    public void step(boolean backward) {
        if (currentContent.isEmpty()) {
            return;
        }
        if (selectedField == null) {
            int i = backward ? currentContent.size() - 1 : 0;
            selectedField = currentContent.get(i);
            selectedStack.clear();
            selectedStack.add(selectedField);
            selectedField.select();
        } else {
            boolean finished = selectedField.getSelector().step(backward, selectedStack);
            if (finished) {
                selectedField.deselect();
                boolean end = stepField(backward);
                selectedField.select();
                selectedStack.clear();
                selectedStack.add(selectedField);
            }
        }
      //  createEntitiesStack();
    }
        
    private boolean stepField(boolean backward) {
        int i;
        if (selectedField == null) {
            if (backward) {
                i = currentContent.size() -1;
            } else {
                i = 0;
            }
        } else {
            int step = backward ? -1 : 1;
            i = currentContent.indexOf(selectedField) + step;                
        }

        if (i < 0 || i >= currentContent.size()) {
            return true;
        } else {
            selectedField = currentContent.get(i);
            selectedStack.clear();
            selectedStack.add(selectedField);
            return false;
        }
    }
        
    public void reload() {
        for(Container con : selectedStack) {
            con.getSelector().reset();
        }
        selectedStack.clear();
        if (selectedField != null) {
            selectedField.getSelector().reset();
            selectedField = null;
        }
    } 
    
    /*  
    private void createEntitiesStack() {
        entitiesStack.clear();
        for (Container con : selectedStack) {
            try {
                if (con instanceof Caption) {
                    Caption cap = (Caption) con;
                    entitiesStack.add(cap.getField());
                } else if (con instanceof WrapperPanel) {
                    entitiesStack.add(con.getHeading().getWrapper());
                }
            } catch (AbstractMethodError er) {
                
            }
        }
    } */
    
  /*  
    public void restoreFromStack() {
        selectedStack.clear();
        for (int i = 0; i < entitiesStack.size(); i++) {
            Object entity = entitiesStack.get(i);
            if (entity == null)
                break;
            
            Container conSearch = null;
            if (selectedStack.isEmpty()) {
                for (Caption cap : currentContent) {
                    if (cap.getWrapper() != null && cap.getWrapper().equals(entity)) {
                        conSearch = cap;
                        selectedField = cap;
                        break;
                    }
                }
            } else {
                Container<EntityPanel> con = selectedStack.get(selectedStack.size() - 1);
                for (EntityPanel ep : con.getPanels()) {
                    if (ep.getWrapper() != null && ep.getWrapper().equals(entity)) {
                        conSearch = (Container) ep;
                    }
                }
            }
            if (conSearch == null) {
                break;
            } else {
                if (!selectedStack.isEmpty()) {
                    selectedStack.get(selectedStack.size() - 1).getSelector().
                            setSelectedField(conSearch.getHeading());
                }
                selectedStack.add(conSearch);
            }
        }
        if (!selectedStack.isEmpty()) {
            selectedStack.get(selectedStack.size() - 1).getHeading().select();
        }
    }
*/    
    
    public void setCurrentContent(List<Caption> currentContent) {
        this.currentContent = currentContent;
    }

    public Caption getSelectedField() {
        return selectedField;
    }

    public void setSelectedField(Caption selectedField) {
        this.selectedField = selectedField;
    }
    
    public class Next extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            step(false);            
            if (selectedField == null || selectedField.getSelector().getSelectedField() == null)
                return;
            JPanel pan = (JPanel) selectedField.getSelector().getSelectedField();
            contentPanel.view(pan);
        }   
        
    }
    
    public class Previous extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            step(true);
            if (selectedField == null || selectedField.getSelector().getSelectedField() == null)
                return;
            JPanel pan = (JPanel) selectedField.getSelector().getSelectedField();
            contentPanel.view(pan);
        }
        
    }
    
    public class Expand extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (selectedField == null)
                return;
            EntityPanel selected = selectedField.getSelector().getSelectedField();
            if (selected == null)
                return;
            if (selected instanceof Container) {
                ((Container)selected).setShow(true);
                selected.reload();
            }        
        }
        
    }
    
    public class Contract extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (selectedStack.size() < 2)
                return;
            Container last = selectedStack.get(selectedStack.size() - 1);
            Container toFold;
            if (last.isShow()) {
                toFold = last;
            } else {
                toFold = selectedStack.get(selectedStack.size() - 2);
                selectedStack = selectedStack.subList(0, selectedStack.size() - 1);            
            }
            toFold.setShow(false);
            toFold.getSelector().reset();
            if (toFold instanceof EntityPanel) {
                EntityPanel ep = (EntityPanel) toFold;
                ep.reload();
                ep.select();
            }            
        }
        
    }
    
    public class Edit extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (selectedField == null) 
                return;
            EntityPanel s = selectedField.getSelector().getSelectedField();
            if (s == null || !(s instanceof WrapperPanel))
                return;
            WrapperPanel wp = (WrapperPanel) s;
            wp.edit();
        }
        
    }

    public class New extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            for (int i = selectedStack.size() - 1; i >=0; i--) {
                Container con = selectedStack.get(i);
                if (con instanceof Caption) {
                    Caption cap = (Caption) con;
                    cap.forgeNew();
                    break;
                }
            }
        }
        
    }
    
    public class Show extends AbstractAction {
        
        public void actionPerformed(ActionEvent e) {
            if (selectedField == null) 
                return;
            EntityPanel s = selectedField.getSelector().getSelectedField();
            if (s == null || !(s instanceof WrapperPanel))
                return;
            WrapperPanel wp = (WrapperPanel) s;
            browserManager.viewElement(wp.getWrapper());
        }        
        
    }
    
    public class ShowInBrowser extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            if (selectedField == null) 
                return;
            EntityPanel s = selectedField.getSelector().getSelectedField();
            if (s == null || !(s instanceof WrapperPanel))
                return;
            WrapperPanel wp = (WrapperPanel) s;
            browserManager.openNewBrowser(wp.getWrapper());
        }
        
    }
    
    public class NextKey extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            browser.nextKey();
        }
        
    }
    
    public class NextBrowser extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            browser.nextBrowser();
        }
        
    }
    
    public class NewBrowser extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            browser.openNewBrowser(null);
        }
        
    }
    
    public class CloseBrowser extends AbstractAction {

        public void actionPerformed(ActionEvent e) {
            browser.nextKey();
        }
        
    }
    
}
