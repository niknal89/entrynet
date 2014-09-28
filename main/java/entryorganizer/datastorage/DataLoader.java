/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.datastorage;

import entryorganizer.entities.exception.IDReadException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import entryorganizer.entities.Entity;
import entryorganizer.entities.ID;

/**
 *
 * @author teopetuk89
 */
public class DataLoader {
        
    private IDFactory idFactory;    
    
    public static final int CLUSTER_SIZE = 1024;
    
    protected DataLoader(IDFactory idFactory) {
        this.idFactory = idFactory;
    }
    
    protected <E extends Entity> E read(ID id, File objects) 
            throws IDReadException {
        try {            
            FileInputStream fis = new FileInputStream(objects);   
            fis.skip(id.getOffset());
            byte[] objBytes = new byte[id.getSize()];
            int bytesRead = fis.read(objBytes);
            if (bytesRead > 0) {
                ByteArrayInputStream bis = new ByteArrayInputStream(objBytes);                
                ObjectInputStream iis = new ObjectInputStream(bis); 
                Object obj = iis.readObject();
                iis.close();
                bis.close();
                fis.close();
                E e = (E) obj;
                e.setId(id);
                return e;
            } else {
                return null;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ID.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            throw new IDReadException(ex, id);
        }
        return null;
    } 
    
    protected int estimateClusterChangeAndPrepareWrite(Entity entity) 
            throws IOException {
        if (preparedStream != null) {
            cancelWrite();
        }
        ID id = entity.getId();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();   
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(entity);
        oos.flush();  
        preparedStream = bos;

        
        int oldClusters = getClusters(id.getSize());
        int newClusters = getClusters(bos.size());
        return newClusters-oldClusters;        
    }    
    
    private ByteArrayOutputStream preparedStream;
    
    protected void cancelWrite() throws IOException {
        if (preparedStream != null) {
            preparedStream.close();
            preparedStream = null;
        }
    }
    
    protected void write(ID id, File objects)
            throws IOException {
        setIDSize(id, preparedStream.size());

        RandomAccessFile ras = new RandomAccessFile(objects, "rw");
        ras.getChannel().write(ByteBuffer.wrap(preparedStream.toByteArray()), id.getOffset());
        preparedStream.close();
        preparedStream = null;
        ras.close();
    }     
    
    private void setIDSize(ID id, int newSize) {
        int oldSize = id.getSize();
        if (oldSize == newSize) {
            return;
        }
        id.setSize(newSize);
        idFactory.writeIDs(false, id);
    }
    
    protected int getClusters(int size) {
        int clusters =
                ((int) Math.ceil((double) size / (double) CLUSTER_SIZE));
        return clusters;
    }
    
}
