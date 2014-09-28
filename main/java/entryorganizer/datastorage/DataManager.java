/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.datastorage;

import entryorganizer.entities.exception.IDReadException;
import entryorganizer.Commander;
import entryorganizer.entities.Entity;
import entryorganizer.entities.exception.EntityException;
import entryorganizer.entities.EntityType;
import entryorganizer.entities.Field;
import entryorganizer.entities.FieldLimiters;
import entryorganizer.entities.ID;
import entryorganizer.entities.Link;
import entryorganizer.entities.Parameter;
import entryorganizer.entities.Text;
import entryorganizer.entities.exception.WrongFieldException;
import entryorganizer.entities.wrappers.Entry;
import entryorganizer.entities.wrappers.Source;
import entryorganizer.entities.wrappers.Tag;
import entryorganizer.entities.wrappers.Wrapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author teopetuk89
 */
public class DataManager {
    
    private DataLoader dataLoader;
    private Commander commander;
    
    private IDFactory idFactory;
    
    private File bigFile;
    private File idFile;
    private File backupFolder;
    
    private int curBigFileSize;
    
    private List<Index> indexes = new ArrayList<Index>();
    private Index keyIndex;
    private Index noTagIndex;
    private Index allTagIndex;
    private Map<EntityType, Index> typeIndexes = new HashMap<EntityType, Index>();
    
    private List<Wrapper> loadedEntities = new ArrayList<Wrapper>();
 //   private List<Integer> freeClusters = new ArrayList<Integer>();
    
    public DataManager(String file, Commander commander) {     
        this.commander = commander;
        if (!file.endsWith(File.separator))
            file = file + File.separator;
//        this.rootFolder = new File(file);
        this.idFile = new File(file, "ids");       
        idFactory = new IDFactory(idFile);
        this.dataLoader = new DataLoader(idFactory); 
        this.bigFile = new File(file + "objects");
        this.backupFolder = new File(file + "backup" + File.separator);
        backupFolder.mkdir();
        try {
            if (!bigFile.exists()) {
                bigFile.createNewFile();  
            }
        } catch (IOException ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        File indexFolder = new File(file + "indexes" + File.separator);
        if (!indexFolder.exists()) {
            indexFolder.mkdir();
        }
        for (EntityType et : commander.getEntityTypes()) {
            Index index = new Index(indexFolder.getPath(), et.getName());
            typeIndexes.put(et, index);
            indexes.add(index);
        }
        
        keyIndex = new Index(indexFolder.getPath(),  "keys");
        noTagIndex = new Index(indexFolder.getPath(), "noTag");
        allTagIndex = new Index(indexFolder.getPath(), "allTag");
        
        indexes.add(keyIndex);
        indexes.add(noTagIndex);
        indexes.add(allTagIndex);
        curBigFileSize = (int) Math.ceil((double) bigFile.length()
                / (double) DataLoader.CLUSTER_SIZE) * DataLoader.CLUSTER_SIZE;
    }
    
    public <W extends Wrapper> W loadLink(Link link, W wrapper) {
        W result = null;
        int idInt = link.getId();
        result = loadEntity(idInt, wrapper);
        if (result == null) return null;
        link.setLink(result.getWrapped());
        return result;
    }    
    
    public <W extends Wrapper> W loadEntity(int idInt, W clazz) {
        Wrapper w = loadEntity(idInt);
        if (w == null) {
         //   System.out.println("could not load entity of id " + idInt);
            return null;
        }
        if (clazz.getClass().isInstance(w)) {
            return (W) w;
        } else {
   //         System.out.println("entity of id " + idInt + " is not class " + clazz.getClass().getSimpleName() +
   //                     ", class " + w.getClass().getSimpleName() + " instead");
            return null;
        }
    }
    
    private Wrapper loadEntity(int idInt) {
        for (Wrapper w : loadedEntities) {
            if (w.getWrapped().getIdInt() == idInt) {
                return w;
            }
        }        
        
        ID id = idFactory.getID(idInt);
        
        if (id == null) {
            return null;
        }        
        Entity e = null;
        
        try {
            e = (Entity) dataLoader.read(id, bigFile);
            /*  if (e != null) {
            e.initialize(id, this);
            allEntities.add(e);
            } */
        } catch (IDReadException ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
        }   
        if (e == null) {
            System.out.println("no entity found for id " + id.getId() + " offset " + id.getOffset());
            return null;
        }
        EntityType et = commander.getEntityType(e.getTypeStr());
        e.setType(et);
        Wrapper w = wrap(e);
        loadedEntities.add(w);
        return w;
    }
    
    public void writeEntity(Entity e) {
        writeEntity(e, bigFile);
    }
    
    private void writeEntity(Entity e, File file) {
        if (e != null) {
            try {
                ID id = e.getId();
                if (id == null) {
                    id = idFactory.forgeID(curBigFileSize);
                    e.setId(id);
                } 

                if (isEnd(id)) {
                    int clusterChange = dataLoader.estimateClusterChangeAndPrepareWrite(e);
                    dataLoader.write(id, file);
                    curBigFileSize += clusterChange * DataLoader.CLUSTER_SIZE;
                    return;
                } 

                int clusterChange = dataLoader.estimateClusterChangeAndPrepareWrite(e);
                if (clusterChange > 0) {
                    id.setOffset(curBigFileSize);
                    idFactory.writeIDs(false, id);
                    dataLoader.write(id, file);
                    curBigFileSize += dataLoader.getClusters(id.getSize()) * DataLoader.CLUSTER_SIZE;
                    return;
                } 

                dataLoader.write(id, file);         
            } catch (IOException ex) {
                Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void dropEntity(Entity e) {
        loadedEntities.remove(e);
    }
    
    private boolean isEnd(ID id) {
        int idEnd = id.getOffset() + 
                    DataLoader.CLUSTER_SIZE * dataLoader.getClusters(id.getSize());
        if (curBigFileSize <= idEnd) 
            return true;
        else 
            return false;
    }
    /*
    private int getFreeClusters(ID id) {
        int idEnd = id.getOffset() + 
                    DataLoader.CLUSTER_SIZE * dataLoader.getClusters(curBigFileSize);
        if (curBigFileSize <= idEnd) 
            return -1;       
        
        int result = 0;
        Integer nextCluster = getBeginCluster(id) + 
                dataLoader.getClusters(id.getSize());
        boolean free = true;
        do {
            if (freeClusters.contains(nextCluster)) {
                result++;
                nextCluster++;
            } else {
                free = false;
            }
        } while (free);       
        return result;
    }
    
    private int getBeginCluster(ID id) {
        return dataLoader.getClusters(id.getOffset() + 1) - 1;
    }
    */
    public void deleteEntity(Wrapper w) {
        idFactory.deleteID(w.getID());
        for (Index index : indexes) {
            index.remove(w.getID().getId());
        }
        this.loadedEntities.remove(w);
     }
    
    public void deleteEntity(int idInt) {
        Wrapper w = loadEntity(idInt);
        ID id = idFactory.getID(idInt);
        if (id == null)
            return;
        idFactory.deleteID(id);
        for (Index index : indexes) {
            index.remove(idInt);
        }
        this.loadedEntities.remove(w);
    }
        
    public Tag forgeTag(String name) {
        Entity e = forgeEntity(commander.getEntityType(EntityType.TAG));
        Tag tag = new Tag(commander, e);
        tag.setName(name);
        return tag;
    }
    
    public Entry forgeEntry(String content) {
        Entity e = forgeEntity(commander.getEntityType(EntityType.ENTRY));
        Entry entry = new Entry(commander, e);
        entry.setContent(content);
        loadedEntities.add(entry);
        return entry;
    }
    
    public Source forgeSource(EntityType type) {
        Entity e = forgeEntity(type);
        Source source = new Source(commander, e);
        loadedEntities.add(source);
        return source;
    }
    
    public Wrapper forgeWrapper(EntityType type) {        
        Entity e = forgeEntity(type);
        Wrapper w = wrap(e);
        loadedEntities.add(w);
        return w;
    }
    
    public Wrapper forgeWrapper(EntityType type, int idInt) {        
        Entity e = forgeEntity(type, idInt);
        Wrapper w = new Wrapper(commander, e);
        loadedEntities.add(w);
        return w;
    }
    
    private Entity forgeEntity(EntityType type, int idInt) {
        ID id = idFactory.forgeID(curBigFileSize, idInt);
        Entity e = new Entity(id);
        e.setType(type);
        writeEntity(e);
        checkIndexes(e);        
        return e;
    }
    
    private Entity forgeEntity(EntityType type) {
        ID id = idFactory.forgeID(curBigFileSize);
        Entity e = new Entity(id);
        e.setType(type);
        writeEntity(e);
        checkIndexes(e);        
        return e;
    }
    
    private Wrapper wrap(Entity e) {
        EntityType type = e.getType();
        EntityType tag = commander.getEntityType(EntityType.TAG);
        EntityType source = commander.getEntityType(EntityType.SOURCE);
        EntityType entry = commander.getEntityType(EntityType.ENTRY);
        if (type == null) {
            return new Wrapper(commander, e);
        } else if (type.hasParent(tag)) {
            return new Tag(commander, e);
        } else if (type.hasParent(source)) {
            return new Source(commander, e);
        } else if (type.hasParent(entry)) {
            return new Entry(commander, e);
        } else {
            return new Wrapper(commander, e);
        }
    }
    
    private void checkIndexes(Entity e) {
        EntityType type = e.getType();
        Index i = typeIndexes.get(type);
        if (i == null)
            new String();
        i.add(e.getIdInt());
    }
    
    public Link forgeLink(Entity entity, String name, Entity link) throws
            WrongFieldException {
        Link l = forgeLink(entity, name, link.getIdInt());
        l.setLink(link);
        return l;
    }
    
    public Link forgeLink(Entity entity, String field, int link) throws
            WrongFieldException {
        FieldLimiters fl = entity.getType().getLimiters(field);
        if (fl == null) 
            throw new WrongFieldException(entity, field);
        for (Field f : entity.getFields(field)) {
            if (f instanceof Link) {
                Link l = (Link) f;
                if (l.getId() == link) {
                    return l;
                }
            }
        }
        Link l = new Link(field, link);
        if (fl.getLimit() == 1) 
            removeFields(entity, field);
        entity.addField(l);
        writeEntity(entity);
        return l;
    }
    
    public Parameter forgeParameter(Entity e, String name, int i) throws
            WrongFieldException {
        Parameter param = new Parameter(name, i);
        FieldLimiters fl = e.getType().getLimiters(name);
        if (fl == null)
            throw new WrongFieldException(e, name); 
        if (fl.getLimit() == 1) removeFields(e, name);
        e.addField(param);
        writeEntity(e);
        return param;
    }
    
    public Text forgeText(Entity e, String name, String string) throws 
            WrongFieldException {
        Text result = new Text(name, string);
        FieldLimiters fl = e.getType().getLimiters(name);
        if (fl == null) 
            throw new WrongFieldException (e, name);
        if (fl.getLimit() == 1) 
            removeFields(e, name);
        e.addField(result);
        writeEntity(e);
        return result;
    }
    
    public void changeLink(Entity e, Link l, Entity change) {
        l.setId(change.getIdInt());
        l.setLink(change);
        writeEntity(e);
    }
    
    public void changeParameter(Entity e, Parameter p, int change) {
        p.setValue(change);
        writeEntity(e);
    }
    
    public void changeText(Entity e, Text t, String change) {
        t.setText(change);
        writeEntity(e);
    }
    
    public void removeFields(Entity e, String fieldName) {
        List<Field> fields = new ArrayList<Field>(e.getFields());
        boolean removed = false;
        for (Field fCheck : fields) {
            if (fCheck.getName().equals(fieldName)) {
                e.removeField(fCheck);
                removed = true;
            }
        } 
        if (removed)
            writeEntity(e);
    }
    
    public void removeField(Entity e, Field field) {
        if (!e.getFields().contains(field))
            return;
        e.removeField(field);
        writeEntity(e);
    }
    
    public void setKey(int id, boolean key) {
        if (key) {
            keyIndex.add(id);
        } else {
            keyIndex.remove(id);
        }
    }
    
    public boolean isKey(int id) {
        return keyIndex.has(id);
    }
    
    public List<Wrapper> getEntitiesOfType(EntityType entityType) {
        List<Wrapper> result = new ArrayList<Wrapper>();
        Index index = typeIndexes.get(entityType);        
        for (Integer i : index.getValues()) {
            Wrapper w = loadEntity(i);
            result.add(w);
        }
        return result;
    }
    
    public List<Tag> getKeyTags() {
        List<Tag> result = new ArrayList<Tag>(); 
        for (Integer i : keyIndex.getValues()) {
            Tag t = loadEntity(i, new Tag());
            if (t != null)
                result.add(t);
        }
        return result;
    }
    
    public List<Wrapper> getLoaded() {
        return loadedEntities;
    }
    
    public void backup() {
        idFactory.writeAllIDs(new File(backupFolder, "ids"));
        File backupBigFile = new File(backupFolder, "objects");
        for (ID id : idFactory.getAllIDs()) {
            System.out.println(id.getOffset());
            Wrapper w = loadEntity(id.getId());
            if (w == null) {
                continue;
            }
            writeEntity(w.getWrapped(), backupBigFile);
        }
        for (Index i : indexes) {
            i.rewrite(backupFolder);
        }
    }
    
    private int not0;
    private int is0;
    
    public void createIndexes() {
        for (ID id : idFactory.getAllIDs()) {
            Wrapper w = loadEntity(id.getId());
            if (w != null)
                checkIndexes(w.getWrapped());            
        }
    }
    
}
