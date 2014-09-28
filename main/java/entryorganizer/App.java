package entryorganizer;

import entryorganizer.entities.exception.IDReadException;
import entryorganizer.entities.wrappers.Wrapper;
import entryorganizer.gui.MainFrame;
import entryorganizer.recover.IDManager;
import entryorganizer.recover.Recoverer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        launch();
  //      recover();
       
    }
    
    private static void launch() {
        Commander comm = new Commander("configs.txt", "/home/niknal/EMFiles");
        MainFrame mf = new MainFrame(comm);
        mf.setVisible(true);      
    }
    
    private static void recover() {
        Commander comm = new Commander("configs.txt", "С:\\EMFiles\\");
        Recoverer rec = new Recoverer("D:\\EntryManagerFiles\\", comm);
        try {
            rec.recover();
        } catch (Exception ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }     
    }
}
