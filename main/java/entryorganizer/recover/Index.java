/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entryorganizer.recover;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author teopetuk89
 */
public class Index {

    private String folderPath;
    private String name;
    private File file;
    private List<Integer> values = new ArrayList<Integer>();
    
    public Index(String folderPath, String name) {
        if (!folderPath.endsWith(File.separator)) {
            this.folderPath = folderPath + File.separator;
        } else {
            this.folderPath = folderPath;
        }
        this.name = name;
        initialize();
        readAll();
    }
    
    private void initialize() {
        file = new File(folderPath + name);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(Index.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void readAll() {        
        try {
            FileInputStream fstream = new FileInputStream(file);
                try {
                    DataInputStream dstream = new DataInputStream(fstream);
                    int amount = dstream.readInt();
                    try {
                        for (int i = 0; i < amount; i++) {
                            int key = dstream.readInt();  
                            values.add(key);
                        }
                    } finally {
                        dstream.close();
                    }
                } finally {
                    fstream.close();
                }
            
        } catch(Exception ex) {
            Logger.getLogger(IDManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeAll() {           
        try {
            FileOutputStream fstream = new FileOutputStream(file);
            FileChannel fchannel = fstream.getChannel();
            ByteBuffer bb = ByteBuffer.allocate(4 + values.size() * 4);
            bb.position(0);
            bb.putInt(values.size());
            for (int i = 0; i < values.size(); i++) {
                bb.putInt(values.get(i));
            }
            bb.flip();
            fchannel.write(bb, 0);
            fchannel.close();
            fstream.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IDManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(IDManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        /*
        try {
            File backup = new File(file.getAbsolutePath() + "1");
            backup.createNewFile();
            FileOutputStream fstream = new FileOutputStream(backup);
            fstream.write(valuesList.size());
            try {
                ObjectOutputStream ostream = new ObjectOutputStream(fstream);
                try {
                    for (Integer i : values.keySet()) {
                       // Object o = values.get(i);
                        ostream.writeInt(i);
                       // ostream.writeObject(o);
                    }
                    ostream.flush();
                    file.delete();
                    backup.renameTo(file);
                } finally {
                    ostream.close();
                }
            } finally {
                fstream.close();
            }
        } catch(Exception ex) {
            Logger.getLogger(IDManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
        
    }
        
    private void writeNewValue(int id) { 
        try {            
            long fileLength = file.length();
            RandomAccessFile ras = new RandomAccessFile(file, "rw");
            FileChannel fchannel = ras.getChannel();            
            ByteBuffer bb = ByteBuffer.allocate(4);
            bb.position(0);
            bb.putInt(values.size());
            bb.flip();
            fchannel.write(bb, 0);
            
            bb = ByteBuffer.allocate(4);
            bb = bb.putInt(id);
            bb.flip();
            fchannel.write(bb, fileLength);
                    
            fchannel.close();
            ras.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IDManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(IDManager.class.getName()).log(Level.SEVERE, null, ex);
        }     
    } 
    
    public void add(int key) {
        values.add(key);
        writeNewValue(key);
    }
    
    public void remove(int key) {
        values.remove((Integer) key);
        writeAll();
    }
    
    public List<Integer> getValues() {
        return values;
    }

    protected void rewrite(File folder) {
        if (!folder.getAbsolutePath().endsWith(File.separator)) {
            this.folderPath = folder.getAbsolutePath() + File.separator;
        } else {
            this.folderPath = folder.getAbsolutePath();
        }
        initialize();
        writeAll();
    }

    public void deleteAll() {
        values.clear();
        writeAll();
    }
    
}
