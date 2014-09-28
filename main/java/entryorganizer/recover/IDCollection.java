/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.recover;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import entryorganizer.data.ID;

/**
 *
 * @author teopetuk89
 */
public class IDCollection extends ArrayList<ID> {

  //  private List<ID> idsToWrite = new ArrayList<ID>();
//    private boolean shouldWriteAll = false;
 //   private boolean shouldWrite = false;
    private File objectsFile;
    private IDManager idManager;

    public IDCollection(File objectsFile, IDManager idManager) {
        this.objectsFile = objectsFile;
        this.idManager = idManager;
    }
    /*
    public void allWritten() {
        shouldWriteAll = false;
        shouldWrite = false;
        idsToWrite.clear();
    }

    public void newWritten() {
        shouldWrite = false;
        idsToWrite.clear();
    } */

    public void idCreated(ID id) {
        super.add(id);
        idManager.writeIDs(true, id);
    }

    public void idChanged(ID id) {        
        idManager.writeIDs(false, id);
    }
/*
    public boolean shouldWriteAll() {
        return shouldWriteAll;
    }

    public boolean shouldWrite() {
        return shouldWrite;
    }

    public List<ID> getIdsToWrite() {
        return idsToWrite;
    }
*/
    @Override
    public boolean add(ID id) {
        if (id == null) return false;
        if (!this.contains(id)) {
            boolean result = super.add(id);
           /* if (result && !idsToWrite.contains(id)) {
                idsToWrite.add(id);
            }*/
            return result;
        } else {
            return false;
        }
    }

    @Override
    public boolean remove(Object o) {
        boolean success = super.remove(o);
       /* if (success && idsToWrite.contains(o)) {
            idsToWrite.remove(o);
        } */
        return success;
    }

    public File getObjectsFile() {
        return objectsFile;
    }
     
}
