/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.gui.searchfield;


import entryorganizer.Commander;
import entryorganizer.datastorage.DataManager;
import entryorganizer.entities.wrappers.Wrapper;
import entryorganizer.entities.EntityType;
import entryorganizer.entities.Field;
import entryorganizer.entities.Text;
import entryorganizer.gui.searchfield.SearchField.Searcher;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

/**
 *
 * @author teopetuk89
 */
public class SearchEntityField extends SearchField implements ActionListener,
        KeyListener {

    private EntityType searchedType;
    private String searchedField;
    private Commander commander;
    private DataManager dataManager;

    private SearcherContainer container;
    private JPopupMenu popupVariants = new JPopupMenu();
    private List<EntityMenu> menuList = new ArrayList<EntityMenu>();

    private EntitySearcher searcher;

    private class EntitySearcher implements Searcher {

        KeyListener keyListener;
        ActionListener actionListener;

        private EntitySearcher(KeyListener keyListener,
                ActionListener actionListener) {
            this.keyListener = keyListener;
            this.actionListener = actionListener;
        }

        @Override
        public void search(String text) {
            popupVariants.setVisible(false);
            popupVariants.removeAll();
            menuList.clear();
            
            Collection<Wrapper> entities = 
                    getEntities(searchedType, searchedField, text);
            for (Wrapper w : entities) {
                String menuName = "";
                menuName = w.getShortDescription();
                EntityMenu menu = new EntityMenu(w, menuName);
                menu.addKeyListener(keyListener);
                menu.addActionListener(actionListener);
                menuList.add(menu);
                popupVariants.add(menu);
            }
                
            popupVariants.setVisible(true);
            popupVariants.show(fieldSearch, 16, 16);
            fieldSearch.requestFocusInWindow();
        }

        @Override
        public void clearSearch() {
            popupVariants.setVisible(false);
        }

    }
    
    private class EntityMenu extends JMenuItem {

        private Wrapper entity;

        EntityMenu(Wrapper entity, String name) {
            super(name);
            this.entity = entity;
        }

        Wrapper getEntity() {
            return entity;
        }
    }

    public interface SearcherContainer {

        public void selected(Wrapper e);

    }
    
    public SearchEntityField(Commander commander, SearcherContainer container,
            JTextField fieldSearch) {
        super(fieldSearch, null);
        initialize(commander, container, fieldSearch);
    }

    private void initialize(Commander commander, SearcherContainer container,
            JTextField fieldSearch) {
        this.commander = commander;
        this.dataManager = commander.getDataManager();
        this.container = container;
        this.fieldSearch = fieldSearch;
        fieldSearch.addKeyListener(this);
        popupVariants.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
        popupVariants.setEnabled(true);
        popupVariants.setFocusable(true);
        fieldSearch.setComponentPopupMenu(popupVariants);
        searcher = new EntitySearcher(this, this);
        setSearcher(searcher);
    }

    public void setSearchRequirements(EntityType searchedType, 
            String searchedField) {
        this.searchedType = searchedType;
        this.searchedField = searchedField;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() instanceof EntityMenu) {
            container.selected(((EntityMenu) ae.getSource()).getEntity());
        }
    }
    
    @Override
    public void keyTyped(KeyEvent arg0) {}
    
    @Override
    public void keyReleased(KeyEvent arg0) {}

    @Override
    public void keyPressed(KeyEvent arg0) {
        if (popupVariants.isVisible()) {
            if (arg0.getKeyCode() == KeyEvent.VK_DOWN) {
                selectedIndex++;  
                checkIndex();
                select();        
            } else if (arg0.getKeyCode() == KeyEvent.VK_UP) {
                selectedIndex--;
                checkIndex();
                select();        
            } else if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
                for (EntityMenu em : menuList) {
                    if (em.isArmed()) {
                        container.selected(em.getEntity());  
                        searcher.clearSearch();
                        break;
                    }
                }                        
            } else if (arg0.getKeyCode() == KeyEvent.VK_TAB) {
                searcher.clearSearch();
            }
        }
    }

    private void checkIndex() {
        if (selectedIndex >= menuList.size()) {
            selectedIndex = 0; 
        } else if (selectedIndex < 0) {
            selectedIndex = menuList.size() - 1;
        }
    }

    private void select() {
        for (JMenuItem menu : menuList) {
            menu.setArmed(false);
        }
        if (selectedIndex < menuList.size()) {
            JMenuItem menu = menuList.get(selectedIndex);
            if (menu != null) {
                menu.setArmed(true);
            }
        }
    }

    private Collection<Wrapper> getEntities(EntityType type, String fieldName, String searchedText) {
        Collection<Wrapper> result = new ArrayList<Wrapper>();
        Collection<Wrapper> typeList = dataManager.getEntitiesOfType(type);
        for (Wrapper e : typeList) {
            Field f = e.getWrapped().getField(fieldName);
            if (f != null && f instanceof Text) {
                Text t = (Text) f;
                if (t.getText() == null) 
                    continue;
                if (t.getText().length() < searchedText.length())
                    continue;
                String comp = t.getText().substring(0, searchedText.length());
                if (comp.equalsIgnoreCase(searchedText)) {
                    result.add(e);
                }
            }
        }
        return result;
    }
    
}
