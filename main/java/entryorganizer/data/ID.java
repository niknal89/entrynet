/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entryorganizer.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import entryorganizer.recover.IDCollection;
import entryorganizer.recover.IDManager;
import entryorganizer.recover.IDReadException;

/**
 *
 * @author teopetuk89
 */
public class ID<E extends Entity> implements Serializable {
    
    private int id; 
    private int offset;
    private int size;
    private transient IDCollection idCollection;
    private transient int idOffset;
    private transient boolean saved;

    public ID() {}

    public ID (IDCollection idCollection) {
        this.idCollection = idCollection;
        idOffset = (idCollection.size()) * 12 + 4;   // the id is not added to the collection yet so its (size) not (size - 1)
        idCollection.idCreated(this);
    }
    
    public E read() throws IDReadException {
        try {            
            FileInputStream fis = new FileInputStream(
                    idCollection.getObjectsFile());   
            fis.skip(offset);
            byte[] objBytes = new byte[size];
            int bytesRead = fis.read(objBytes);
            if (bytesRead > 0) {
                ByteArrayInputStream bis = new ByteArrayInputStream(objBytes);                
                ObjectInputStream iis = new ObjectInputStream(bis); 
                Object obj = iis.readObject();
                iis.close();
                bis.close();
                fis.close();
                E e = (E) obj;
                return e;
            } else {
                return null;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ID.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
           // System.err.println("Error at ID " + id + " offset " + offset +
           //         " length " + size);
           // Logger.getLogger(ID.class.getName()).log(Level.SEVERE, null, ex);
            throw new IDReadException(ex, this);
        }
        return null;
    }
    
    public boolean write(Object o, File file) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();   
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(o);
            oos.flush();  
                       
            if (size < bos.size() && bos.size() > IDManager.CLUSTER_SIZE) {
                setSize(bos.size());
                bos.close();
                return true;
            }
            
            setSize(bos.size());
            
            RandomAccessFile ras = new RandomAccessFile(file, "rw");
            ras.getChannel().write(ByteBuffer.wrap(bos.toByteArray()), offset);
            bos.close();
            ras.close();
        } catch (IOException ex) {            
            Logger.getLogger(ID.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return false;
    } 
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (this.id != id) {
            this.id = id;
            idCollection.idChanged(this);
        }
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        if (this.offset != offset) {
            this.offset = offset;
            idCollection.idChanged(this);
        }
    }    

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        if (this.size != size) {
            this.size = size;
            idCollection.idChanged(this);
        }
    }
    
    public int getClusters() {
        int clusters =
                ((int) Math.ceil((double) size / (double) IDManager.CLUSTER_SIZE));
        return clusters;
    }

    public int getIdOffset() {
        return idOffset;
    }

    public void setIdOffsetByOrdinalNumber(int idOffset) {
        this.idOffset = idOffset * 12 + 4;
    } 
    
    public static ID forgeIDWithoutWriting(int idInt, int offset, int size, 
            IDCollection idCol) {
        ID id = new ID();
        id.id = idInt;
        id.offset = offset;
        id.size = size;
        id.idCollection = idCol;
        idCol.add(id);
        return id;
    }
}
