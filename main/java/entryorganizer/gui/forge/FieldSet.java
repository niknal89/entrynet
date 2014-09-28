/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.gui.forge;

import entryorganizer.Commander;
import entryorganizer.datastorage.DataManager;
import entryorganizer.entities.Entity;
import entryorganizer.entities.EntityType;
import entryorganizer.entities.Field;
import entryorganizer.entities.FieldLimiters;
import entryorganizer.entities.Link;
import entryorganizer.entities.Parameter;
import entryorganizer.entities.Text;
import entryorganizer.entities.representations.EntityRepresentation;
import entryorganizer.entities.representations.FieldRepresentation;
import entryorganizer.entities.representations.Representation;
import entryorganizer.entities.representations.RepresentationSet;
import entryorganizer.entities.wrappers.Wrapper;
import entryorganizer.gui.Messenger;
import entryorganizer.gui.forge.LinkPanel.LinkPanelContainer;
import entryorganizer.gui.forge.ValuePanel.ValuePanelContainer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author Администратор
 */
public class FieldSet extends FieldManager {
    
    public interface FieldSetContainer extends FieldManagerContainer {
        
        public EntityType getType();
        
    }
    
    private LinkedHashMap<String, List<FieldManager>> structure = 
            new LinkedHashMap<String, List<FieldManager>>(); 
    private EntityType type;
    private Wrapper wrapper;
    private JPanel mainPanel;
        
    private RepresentationSet rSet;
    private Commander commander;
    private DataManager dataManager;
    private Messenger messenger;
    private FieldSetContainer container;    
        
    private static final Dimension BUT_ADD_PANEL_SIZE = new Dimension(200, 20);
    
    public FieldSet(JPanel mainPanel, Commander commander, FieldSetContainer container) {
        super(null, container);
        this.commander = commander;
        this.dataManager = commander.getDataManager();
        this.type = container.getType();
        this.mainPanel = mainPanel;
        this.container = container;
    }
    
    public FieldSet(LinkPanel lp, Commander commander, FieldSetContainer container) {
        super(lp, container);
        this.commander = commander;
        this.dataManager = commander.getDataManager();
        this.type = container.getType();
        this.container = container;
    }
       
    public void reload() {
        rebuild();
        setNextPrevious();
        FieldManager first = getFirst();
        container.selectField(first);
    }
    
    protected void setNextPrevious() {
        List<FieldManager> sequence = new ArrayList<FieldManager>();
        for (String field : structure.keySet()) {
            sequence.addAll(structure.get(field));
        }
        FieldManager localNext;
        FieldManager localPrevious;
        for (int i = 0; i < sequence.size(); i++) {
            FieldManager current = sequence.get(i);
            if (i == 0) {
                localPrevious = previous;
            } else {
                localPrevious = sequence.get(i - 1);
            }
            if (i == sequence.size() - 1) {
                localNext = next; 
            } else {
                localNext = sequence.get(i + 1);
            }
            current.setNext(localNext);
            current.setPrevious(localPrevious);
        }      
        if (next != null)
            next.setPrevious(this);
        if (previous != null)
            previous.setNext(this);
    }
    
    private void rebuild() {
        List<String> fieldOrder = getFieldOrder();  
        LinkedHashMap<String, List<FieldManager>> newStructure = 
                new LinkedHashMap<String, List<FieldManager>>();
        
        for (String field : fieldOrder) {
            List<FieldManager> list = loadField(field);
            newStructure.put(field, list);
        }
        structure.clear();
        structure = newStructure;
        layoutFields();
    }
    
    private List<String> getFieldOrder() {
        EntityRepresentation rep = rSet.getRepresentation(type);
        if (rep == null)
            return new ArrayList<String>();
        List<String> fieldOrder = new ArrayList<String>();
        for (Representation rCheck : rep.getField_order()) {
            if (rCheck instanceof FieldRepresentation) {
                String fieldName = 
                        ((FieldRepresentation) rCheck).getField();
                fieldOrder.add(fieldName);
            }
        }
        return fieldOrder;
    }
        
    private List<FieldManager> loadField(String fieldName) {
        List<FieldManager> result = new ArrayList<FieldManager>();
        List<FieldManager> createdFields = structure.get(fieldName);
        if (createdFields == null) {
            createdFields = new ArrayList<FieldManager>();
        }
        result.addAll(createdFields);
        if (wrapper != null) {
            for (FieldManager fs : createdFields) {
                fs.fieldPanel.setParent(wrapper);
            }
        }
        
        // check for existing fields
        if (wrapper != null) {
            List<Field> fields = entity().getFields(fieldName);
            for (Field field : fields) {
                FieldManager fieldManager = null;
                for (FieldManager fmCheck : createdFields) {
                    if (fmCheck.getField() == null)
                        continue;
                    if (fmCheck.getField().equals(field)) {
                        fieldManager = fmCheck;
                        break;
                    }
                }
                if (fieldManager == null) {
                    fieldManager = forgeFieldManager(field);
                    if (fieldManager != null)
                        result.add(fieldManager);
                }
            }
        }
        
        if (result.isEmpty()) {
            FieldManager fm = forgeFieldManager(fieldName);
            if (fm != null)
                result.add(fm);
        }
                
        for (FieldManager fm : result) {
            if (fm instanceof FieldSet) {
                ((FieldSet) fm).reload();
            }
        }
        return result;
    }
            
    private FieldManager forgeFieldManager(Field field) {
        FieldLimiters fl = type.getLimiters(field.getName());
        if (fl == null){
            return null;
        }
        String newType = fl.getType();
        boolean severalPossible = !(fl.getLimit() == 1);
        FieldManager result = null;
        if (field instanceof Link) {
            Link l = (Link) field;
            LinkPanel lp = new LinkPanel(l, commander, new LPC());
            Wrapper w = dataManager.loadLink(l, new Wrapper());
            if (w == null) 
                return null;
            
            lp.setEntityToLink(w);
            EntityType newEType = commander.getEntityType(newType);
            FSContainer fsc = new FSContainer(newEType);
            FieldSet fs = new FieldSet(lp, commander, fsc);
            fs.setEntity(w);
            fs.setrSet(rSet);
            fs.setMessenger(messenger);
            result = fs;
        } else if (field instanceof Text) {
            Text t = (Text) field;
            VPC vpc = new VPC(severalPossible, entity(), this);
            TextPanel tp = new TextPanel(t, vpc);
            result = new FieldManager(tp, new FMContainer());
        } else if (field instanceof Parameter) {
            Parameter p = (Parameter) field;            
            VPC vpc = new VPC(severalPossible, entity(), this);
            ParameterPanel pp = new ParameterPanel(p, vpc);
            result = new FieldManager(pp, new FMContainer());
        }
        if (wrapper != null)
            result.getFieldPanel().setParent(wrapper);
        return result;
    }
    
    private FieldManager forgeFieldManager(String fieldName) {
        FieldLimiters fl = type.getLimiters(fieldName);
        if (fl == null){
            return null;
        }
        String newType = fl.getType();
        boolean severalPossible = !(fl.getLimit() == 1);
        FieldManager result = null;
        if (newType.equals("str")) {
            VPC vpc = new VPC(severalPossible, entity(), this);
            TextPanel tp = new TextPanel(fieldName, vpc);
            result = new FieldManager(tp, new FMContainer());
        } else if (newType.equals("int")) {
            VPC vpc = new VPC(severalPossible, entity(), this);
            ParameterPanel pp = new ParameterPanel(fieldName, vpc);
            result = new FieldManager(pp, new FMContainer());
        } else {
            LinkPanel lp = new LinkPanel(fieldName, commander, new LPC());
            EntityType newET = commander.getEntityType(newType);
            if (newET == null)
                return null;
            FSContainer fsc = new FSContainer(newET);
            FieldSet fs = new FieldSet(lp, commander, fsc);
            fs.setrSet(rSet);
            fs.setMessenger(messenger);
            result = fs;
        }
        if (wrapper != null)
            result.getFieldPanel().setParent(wrapper);
        return result;
    }

    private void layoutFields() {
        if (mainPanel != null) {            
            mainPanel.removeAll();
        } else if (fieldPanel != null) {
            LinkPanel lp = (LinkPanel) fieldPanel;
            lp.removeChildren();
        }    
        
        for (String field : structure.keySet()) {
            List<FieldManager> list = structure.get(field);
            for (FieldManager fm : list) {
                addToPanel(fm.getFieldPanel());
            }
            
            FieldLimiters fl = type.getLimiters(field);
            if (fl.getLimit() != 1) {
                JPanel panAddPanel = new JPanel();
                panAddPanel.setLayout(new BoxLayout(panAddPanel, BoxLayout.X_AXIS));
                JButton butAddPanel = new JButton("add " + field);
                butAddPanel.setActionCommand(field);
                butAddPanel.addActionListener(new AddPanelListener());
                butAddPanel.setMaximumSize(BUT_ADD_PANEL_SIZE);
                panAddPanel.add(butAddPanel);
                panAddPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
                if (fieldPanel != null) 
                    ((LinkPanel) fieldPanel).addChild(panAddPanel);
                else 
                    mainPanel.add(panAddPanel);
            }            
        }   
        
        if (mainPanel != null) {
            Component c = Box.createVerticalGlue();
            c.setMaximumSize(new Dimension(1, Integer.MAX_VALUE)); 

        }
        
        if (mainPanel != null) {     
            mainPanel.revalidate();            
        } else if (fieldPanel != null) {
            fieldPanel.revalidate();            
        }
    }
    
    private void addToPanel(JComponent comp) {
        if (mainPanel != null) {
            mainPanel.add(comp);
        } else if (fieldPanel != null) {
            ((LinkPanel)fieldPanel).addChild(comp);
        }
    }
        
    @Override
    public void save() {
        if (wrapper == null) {
            boolean hasFields = false;
            for (String field : structure.keySet()) {
                List<FieldManager> list = structure.get(field);
                for (FieldManager fm : list) {
                    if (!fm.isEmpty())
                        hasFields = true;
                }
            }
            if (!hasFields)
                return;
            wrapper = commander.getDataManager().forgeWrapper(type);     
            resetEntity();
        }
        
        if (fieldPanel != null) {
            fieldPanel.saveField();
        }   
        
        for (String fieldName : structure.keySet()) {
            List<FieldManager> list = structure.get(fieldName);
            for (FieldManager fm : list) {                
                fm.save();
            }
        } 
    }
    
    private void resetEntity() {
        if (fieldPanel != null) {
            ((LinkPanel)fieldPanel).setEntityToLink(wrapper);
        }
        for (String fieldName : structure.keySet()) {
            List<FieldManager> list = structure.get(fieldName);
            for (FieldManager fm : list) {
                if (wrapper != null)
                    fm.getFieldPanel().setParent(wrapper);
            }
        } 
    }
            
    public void setType(EntityType et) {
        type = et;
        if (wrapper != null) {
            System.out.println("trying to change entityType of an edited entity");
        }
        reload();
    }

    public void setEntity(Wrapper wrapper) {
        this.wrapper = wrapper;
        resetEntity();
    }

    public void setrSet(RepresentationSet rSet) {
        this.rSet = rSet;
    }

    public void setMessenger(Messenger messenger) {
        this.messenger = messenger;
    }

    public Wrapper getEntity() {
        return wrapper;
    }

    public EntityType getType() {
        return type;
    }

    @Override
    public boolean isEmpty() {
        for (String field : structure.keySet()) {
            List<FieldManager> list = structure.get(field);
            for (FieldManager fmCheck : list) {
                if (!fmCheck.isEmpty())
                    return false;
            }
        }
        return true;
    }
    
    private void removeFieldPanel(FieldPanel fp) {
        FieldManager fs = null;
        for (FieldManager fsCheck : structure.get(fp.getName())) {
            if (fsCheck.getFieldPanel().equals(fp)) {
                fs = fsCheck;
                break;
            }
        }
        if (fs == null)
            return;
        if (fs.getPrevious() != null)
            fs.getPrevious().setNext(fs.getNext());
        if (fs.getNext() != null)
            fs.getNext().setPrevious(previous);
        structure.get(fp.getName()).remove(fs);                
        reload();
        if (fs.getPrevious() != null) {
            container.selectField(fs.getPrevious());
        } else if (fs.getNext() != null) {
            container.selectField(fs.getNext());
        }
    }
    
    private void addPanel(String fieldName) {       
        FieldManager newSet = forgeFieldManager(fieldName);
        structure.get(fieldName).add(newSet);
        reload();
        container.selectField(newSet);
    }
  
    private void addLinkPanel(FieldSet fieldSet) {
        if (fieldSet.getFieldPanel() == null)
            return;
        String field = fieldSet.getFieldPanel().getName();
        if (field == null) 
            return;             
        FieldLimiters fl = type.getLimiters(field);
        if (fl.getLimit() == 1)
            return;        
        addPanel(field);
    }  
    
    private Entity entity() {
        if (wrapper == null) {
            return null;
        } else {
            return wrapper.getWrapped();
        }
    }

    @Override
    public void setNext(FieldManager next) {
        super.setNext(next); 
        if (next == null)
            return;
        FieldManager last = getLast();
        if (last == null)
            return;
        last.setNext(next); 
    }

    @Override
    public void setPrevious(FieldManager previous) {
        super.setPrevious(previous);
        if (previous == null)
            return;
        FieldManager first = getFirst();
        if (first == null)
            return;
        first.setPrevious(previous);
    }
    
    @Override
    public FieldManager getFirst() {
        Iterator<String> it = structure.keySet().iterator();
        if (!it.hasNext())
            return null;
        String firstField = it.next();
        if (firstField == null)
            return null;
        List<FieldManager> list = structure.get(firstField);
        if (list.isEmpty()) 
            return null;
        return list.get(0);
    }

    @Override
    public FieldManager getLast() {
        Iterator<String> it = structure.keySet().iterator();
        String lastField = null;
        while (it.hasNext()) 
            lastField = it.next();
        if (lastField == null)
            return null;
        List<FieldManager> list = structure.get(lastField);
        if (list.isEmpty()) 
            return null;
        return list.get(list.size() - 1);
    }
    
    private class AddPanelListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (! (e.getSource() instanceof JButton)) 
                return;            
            String fieldName = e.getActionCommand();  
            addPanel(fieldName);            
        }
        
    }
    
    private class AL implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            
            if (e.getActionCommand().equals(ValuePanel.DELETE_SOURCE_FIELD)) {
                
                FieldButton fb = (FieldButton) e.getSource();
                FieldPanel fp = fb.getParentPanel();
                fp.removeField();
                removeFieldPanel(fp);
                
            }
        }
        
    }
    
    private class VPC implements ValuePanelContainer {

        private boolean severalPossible;
        private Entity entity;
        private AL actionListener;
        private FieldSet parent;
        
        public VPC(boolean slp, Entity entity, FieldSet parent) {
            this.severalPossible = slp;
            this.actionListener = new AL();
            this.parent = parent;
        }
        
        public void addWrapper(Wrapper w) {
            structure.clear();
            setEntity(w);
            reload();
            if (parent.getFieldPanel() == null) {
                if (parent.getFirst() != null) {
                    container.selectField(parent.getFirst());
                }
            } else {
                if (parent.getFirst() != null) {
                    container.selectField(parent.getFirst());
                }
            }
        }

        public void postMessage(String s) {
            messenger.postMessage(s);
        }

        public void focusGained(ValuePanel sfp) {
            
        }

        public boolean searchPossible() {
            return type.isAllowSearch();
        }
        
        public boolean severalPossible() {
            return severalPossible;
        }

        public Entity getParentEntity() {
            return entity;
        }

        public ActionListener getActionListener() {
            return actionListener;
        }

        public Commander getCommander() {
            return commander;
        }

        public EntityType getParentType() {
            return type;
        }
        
    }
    
    private class LPC implements LinkPanelContainer {
        
        public void remove(LinkPanel panel) {
            panel.removeField();
            removeFieldPanel(panel);
        }
        
        public void delete(LinkPanel panel) {
            panel.deleteField();
            removeFieldPanel(panel);
        }
        
    }

    private class FSContainer extends FMContainer implements FieldSetContainer {
        
        private EntityType type;

        public FSContainer(EntityType type) {
            this.type = type;
        }
        
        public EntityType getType() {
            return type;
        }
                
    }
    
    private class FMContainer implements FieldManagerContainer {
        
        public void enterPressed() {
            container.enterPressed();
        }

        public void selectField(FieldManager fm) {
            container.selectField(fm);
        }

        public void cancel() {
            container.cancel();
        }
        
        public FieldSet getParent() {
            return getThis();
        }
        
        public void addField(FieldManager fm) {    
            if (fm.getFieldPanel() == null)
                return;
            String field = fm.getFieldPanel().getName();
            if (field == null) 
                return;            
            FieldLimiters fl = type.getLimiters(field);
            if (fl.getLimit() == 1)
                return;
            addPanel(field);
        }

        public void addElement(FieldManager fm) {
            if (container.getParent() != null) {
                container.getParent().addLinkPanel(getThis());
            }
        }
        
    }
    
    private FieldSet getThis() {
        return this;
    }
    
}
