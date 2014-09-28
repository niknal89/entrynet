/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.datastorage;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import entryorganizer.entities.ID;

/**
 *
 * @author teopetuk89
 */
public class IDFactory {
    
    private File idFile;
    private List<ID> allIDs = new ArrayList<ID>();
    private int curID;
    
    private static final int FREE_PLACE_START = 4;
    
    protected IDFactory(File idFile) {
        this.idFile = idFile;
        try {
            if (!idFile.exists()) {          
                idFile.createNewFile();
                writeAllIDs();
            }  else {
                readIDs();
            }
        } catch (IOException ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    protected ID forgeID(int offset) {
        ID id = new ID(curID, offset, 0);
        allIDs.add(id);
        setIdOffset(id);
        curID++;
        writeIDs(true, id);
        return id;
    }
    
    boolean oneZ;
    
    protected ID forgeID(int offset, int idInt) {
        for (ID idCheck : allIDs) {
            if (idCheck.getId() == idInt) {
                System.out.println("trying to create existing id");
                return null;
            }
        } 
        ID id = new ID(idInt, offset, 0);
        allIDs.add(id);
        setIdOffset(id);
        curID = ++idInt;
        if (idInt == 0) {
            if (oneZ) {
                new String();
            }
            oneZ = true;
        } 
            
        writeIDs(true, id);
        return id;
    }
    
    protected void deleteID(ID id) {
        allIDs.remove(id);
        writeAllIDs();
    }
        
    private ID forgeIDWithoutWriting(int idInt, int offset, int size) {
        ID id = new ID(idInt, offset, size);        
        allIDs.add(id);
        this.setIdOffset(id);
        curID++;
        return id;
    }
    
    protected void writeIDs(boolean newId, ID... idsToWrite) {
        writeIDs(idFile, newId, idsToWrite);
    }
    
    private void writeIDs(File idFile, boolean newId, ID... idsToWrite) {
        try {            
            RandomAccessFile ras = new RandomAccessFile(idFile, "rw");
            FileChannel fchannel = ras.getChannel();
            if (newId) {
                ByteBuffer bb = ByteBuffer.allocate(4);
                bb.position(0);
                bb.putInt(allIDs.size());
                bb.flip();
                fchannel.write(bb, 0);
            }
            for (ID id : idsToWrite) {
                if (id.getId() == 0) {
                    new String();
                }
                ByteBuffer bb = ByteBuffer.allocate(12);
                bb.position(0);
                bb = bb.putInt(id.getId()).
                        putInt(id.getOffset()).
                        putInt(id.getSize());
                bb.flip();
                fchannel.write(bb, id.getIdOffset());
            }
            fchannel.close();
            ras.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void writeAllIDs() {
        writeAllIDs(idFile);
    }
    
    protected void writeAllIDs(File idFile) {
        try {
            FileOutputStream fstream = new FileOutputStream(idFile);
            FileChannel fchannel = fstream.getChannel();
            ByteBuffer bb = ByteBuffer.allocate(4 + 
                    allIDs.size() * 12);
            bb.position(0);
            bb.putInt(allIDs.size());
            freePlace = FREE_PLACE_START;
            for (int i = 0; i < allIDs.size(); i++) {
                ID id = allIDs.get(i);
                setIdOffset(id);
                bb.putInt(id.getId());
                bb.putInt(id.getOffset());
                bb.putInt(id.getSize());
            }
            bb.flip();
            fchannel.write(bb, 0);
            fchannel.close();
            fstream.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void readIDs() {
        int ids;
        try {
            FileInputStream fstream = new FileInputStream(idFile);
            try {
                DataInputStream dstream = new DataInputStream(fstream);
                try {
                    ids = dstream.readInt();
                    for (int i = 0; i < ids; i++) {
                        int idInt = dstream.readInt();
                        int offset = dstream.readInt();
                        int size = dstream.readInt();
                        ID id = forgeIDWithoutWriting(idInt, offset, size);
                        if (curID <= id.getId()) {
                            curID = id.getId() + 1;
                        }
                    }
                } finally {
                    dstream.close();
                }
            } finally {
                fstream.close();
            }
        } catch (Exception ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private int freePlace = FREE_PLACE_START;
    
    private void setIdOffset(ID id) {
        id.setIdOffset(freePlace);
        freePlace += 12;
    } 
    
    protected List<ID> getAllIDs() {
        return allIDs;
    }
    
    protected ID getID(int idInt) {
        for (ID idCheck : getAllIDs()) {
            if (idCheck.getId() == idInt) {
                return idCheck;
            }
        }
        return null;
    }
    
}
