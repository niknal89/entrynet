/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entryorganizer.gui;

import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.DefaultEditorKit;

/**
 *
 * @author teopetuk89
 */
public class CopyPasteMenu extends JPopupMenu {
    
    private JMenuItem copy = new JMenuItem(new DefaultEditorKit.CopyAction());
    private JMenuItem paste = new JMenuItem(new DefaultEditorKit.PasteAction());
    
    public CopyPasteMenu() {
       copy.setText("Копировать");
       paste.setText("Вставить");
       copy.setMnemonic(KeyEvent.VK_C);
       paste.setMnemonic(KeyEvent.VK_P);
       this.add(copy);
       this.add(paste);
    }
    
}
