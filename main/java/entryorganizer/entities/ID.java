/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.entities;

import entryorganizer.datastorage.DataLoader;

/**
 *
 * @author teopetuk89
 */
public class ID {
    
    private int id; 
    private int offset;
    private int size;
    private transient int idOffset;

    public ID() {}
    
    public ID(int id, int offset, int size) {
        this.id = id;
        this.offset = offset;
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public int getOffset() {
        return offset;
    }

    public int getSize() {
        return size;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getIdOffset() {
        return idOffset;
    }

    public void setIdOffset(int idOffset) {
        this.idOffset = idOffset;
    }
        
}
