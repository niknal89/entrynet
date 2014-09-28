/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.gui;

import entryorganizer.entities.EntityType;
import entryorganizer.entities.wrappers.Tag;
import entryorganizer.entities.wrappers.Wrapper;
import entryorganizer.gui.browser.Browser;
import entryorganizer.gui.entitypanel.SourceViewPanel;
import entryorganizer.gui.entitypanel.TagPanel;
import entryorganizer.gui.entitypanel.TagViewPanel;
import entryorganizer.gui.entitypanel.WrapperPanel;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.TransferHandler;

/**
 *
 * @author Администратор
 */
public class WrapperTransferHandler extends TransferHandler {
    
    public interface TransferManager {
        
        public void setViewedElement(Wrapper w);
        
        public void setKey(Tag t);
        
        public void transfer(WrapperPanel transferred, WrapperPanel toLink);
        
    }
      
    private TransferManager transferManager;

    public WrapperTransferHandler(TransferManager transferManager) {
        this.transferManager = transferManager;
    }
    
    @Override
    public int getSourceActions(JComponent c) {      
        return TransferHandler.LINK;
    }
    
    @Override
    protected Transferable createTransferable(JComponent c) {
        if (c instanceof WrapperPanel) {
            WrapperPanel w = (WrapperPanel) c;
            WrapperDataFlavor df = new WrapperDataFlavor(w.getWrapper().getWrapped().getType());
            WrapperTransferable t = new WrapperTransferable(df, w);
            return t;
        } 
        return null;
    }
    
    @Override
    public boolean canImport(TransferHandler.TransferSupport info) { 
        if (info.getComponent() instanceof TagViewPanel) {
            TagViewPanel tvp = (TagViewPanel) info.getComponent();
            if (tvp.getParent() == null) 
                return false;
        } else if (info.getComponent() instanceof SourceViewPanel) {
            SourceViewPanel svp = (SourceViewPanel) info.getComponent();
            if (svp.getParent() == null)
                return false;
            else 
                return true;
        }
        if (info.getComponent() instanceof WrapperPanel) {
            Wrapper w = ((WrapperPanel) info.getComponent()).getWrapper();
            if (info.getDataFlavors().length < 1) 
                return false;
            if (!(info.getDataFlavors()[0] instanceof WrapperDataFlavor))
                return false;
            if (w == null || w.getWrapped().getTypeStr() == null) 
                return false;
            WrapperDataFlavor wdf = (WrapperDataFlavor) info.getDataFlavors()[0];
            List<String> fields = wdf.type.fieldsOfType(w.getWrapped().getType());
            if (fields.isEmpty())
                return false;
            else 
                return true;
        } else if (info.getComponent() instanceof JScrollPane) {
            JScrollPane container = (JScrollPane) info.getComponent();
            if (container.getName() != null && 
                    !container.getName().equals(Browser.CURRENT_CONTAINER))
                return false;
            else
                return true;
        } else if (info.getComponent() instanceof JComboBox) {
            JComboBox combo = (JComboBox) info.getComponent();
            if (combo.getName() != null && !combo.getName().equals(Browser.COMB_KEY))
                return false;
            WrapperDataFlavor wdf = (WrapperDataFlavor) info.getDataFlavors()[0];
            if (wdf.type.getName().equals(EntityType.TAG)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    
    @Override
    public boolean importData(TransferHandler.TransferSupport info) {
        if (info.getDataFlavors().length < 1) 
            return false;
        if (!(info.getDataFlavors()[0] instanceof WrapperDataFlavor))
            return false;
        WrapperDataFlavor wdf = (WrapperDataFlavor) info.getDataFlavors()[0];
        WrapperPanel transfered = null;
        try {
            transfered = (WrapperPanel) info.getTransferable().getTransferData(wdf);
        } catch (UnsupportedFlavorException ex) {
            Logger.getLogger(WrapperTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WrapperTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        if (info.getComponent() instanceof TagViewPanel) {
            TagViewPanel tvp = (TagViewPanel) info.getComponent();
            if (tvp.getParent() == null) {
                transferManager.transfer(transfered, tvp);
            } else {
                transferManager.transfer(transfered, tvp.getParentPanel());
            }
        } else if (info.getComponent() instanceof SourceViewPanel) {
            SourceViewPanel svp = (SourceViewPanel) info.getComponent();
            
        } else if (info.getComponent() instanceof WrapperPanel) {
            transferManager.transfer(transfered, (WrapperPanel) info.getComponent());
        } else if (info.getComponent() instanceof JScrollPane) {
            JScrollPane container = (JScrollPane) info.getComponent();
            if (!container.getName().equals(Browser.CURRENT_CONTAINER))
                return false;
            transferManager.setViewedElement(transfered.getWrapper());
        } else if (info.getComponent() instanceof JComboBox) {
            JComboBox combo = (JComboBox) info.getComponent();
            if (!combo.getName().equals(Browser.COMB_KEY))
                return false;
            if (transfered == null) 
                return false;
            Wrapper w = transfered.getWrapper();
            if (w == null || !(w instanceof Tag)) 
                return false;
            Tag t = (Tag) w;
            transferManager.setKey(t);
        }
        return false;
    }
    
    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {}
    
    private class WrapperTransferable implements Transferable {
     
        private DataFlavor dataFlavor;
        private WrapperPanel transferedPanel;
        
        public WrapperTransferable(DataFlavor flavor, WrapperPanel wp) {
            this.dataFlavor = flavor;
            this.transferedPanel = wp;
        }
                
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{dataFlavor};
        }

        public boolean isDataFlavorSupported(DataFlavor df) {
            if (df.equals(dataFlavor)) {
                return true;
            }
            return false;
        }

        public Object getTransferData(DataFlavor df) 
                throws UnsupportedFlavorException, IOException {
            if (df.equals(dataFlavor)) {
                return transferedPanel;
            } else {
                return null;
            }
        }
        
        public WrapperPanel getWrapper() {
            return transferedPanel;
        }
    }          
    
    private class WrapperDataFlavor extends DataFlavor {
        
        private EntityType type;
        
        WrapperDataFlavor(EntityType type) {
            super(DataFlavor.javaJVMLocalObjectMimeType + 
                    "; class=entryorganizer.entities.wrappers.Wrapper", "Wrapper");
            this.type = type;
        }
        
    }
            
}
