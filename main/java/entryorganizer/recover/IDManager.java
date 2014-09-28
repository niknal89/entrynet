package entryorganizer.recover;

import entryorganizer.data.Entity;
import entryorganizer.data.SourceField;
import entryorganizer.data.Content;
import entryorganizer.data.SourceType;
import entryorganizer.data.Author;
import entryorganizer.data.ID;
import entryorganizer.data.Source;
import entryorganizer.data.Tag;
import entryorganizer.data.SourceFieldType;
import entryorganizer.data.Entry;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author teopetuk89
 */
public class IDManager {

    private IDCollection allIDs;
    private List<Entity> allEntities = new ArrayList<Entity>();  
    public Index keyIndex;
    public Index sourceIndex;
    private Index noTagIndex;
    private Index allTagIndex;
    private Index authorIndex;
    private List<Index> indexes = new ArrayList<Index>();
    private File idFile;
    private File bigFile;
    private File backupFolder;
    private int curID;
    private int curBigFileSize;

    public static final int CLUSTER_SIZE = 1024;

    private class IDComparator implements Comparator<ID> {

        @Override
        public int compare(ID id1, ID id2) {
                return (int) Math.signum(id1.getOffset() - id2.getOffset());
        }

    }

    private class AuthorComparator implements Comparator<Author> {

        @Override
        public int compare(Author t, Author t1) {
            String l1 = t.getShortName();
            String l2 = t1.getShortName();
            int i = l1.compareToIgnoreCase(l2);
            return (int) Math.signum(i);
        }

    }

    private class SourceComparator implements Comparator <Source> {

        @Override
        public int compare(Source t, Source t1) {
            String s;
            String s1;
            List<Author> l = t.getAuthors();
            List<Author> l1 = t1.getAuthors();
            int i = t.getName().compareToIgnoreCase(t1.getName());
            if (i == 0) return 1;
            return (int) Math.signum(i);
        }

    }

    public IDManager(String file) {
        if (!file.endsWith(File.separator))
            file = file + File.separator;
//        this.rootFolder = new File(file);
        this.idFile = new File(file + "ids");
        this.bigFile = new File(file + "objects");
        this.backupFolder = new File(file + "backup" + File.separator);
        backupFolder.mkdir();
        try {
            if (!idFile.exists()) {
                bigFile.createNewFile();
                allIDs = new IDCollection(bigFile, this);
                idFile.createNewFile();
                writeAllIDs();
            }  else {
                allIDs = new IDCollection(bigFile, this);
                readIDs();
            }
        } catch (IOException ex) {
            Logger.getLogger(IDManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        keyIndex = new Index(file,  "keys");
        sourceIndex = new Index(file, "sources");
        noTagIndex = new Index(file, "noTag");
        allTagIndex = new Index(file, "allTag");
        authorIndex = new Index(file, "authors");
        indexes.add(keyIndex);
        indexes.add(sourceIndex);
        indexes.add(noTagIndex);
        indexes.add(allTagIndex);
        indexes.add(authorIndex);
        curBigFileSize = (int) Math.ceil((double) bigFile.length()
                / (double) CLUSTER_SIZE) * CLUSTER_SIZE;
    }

    public Entity getEntity(int idInt) {
        synchronized(allEntities) {
            for (Entity e : allEntities) {
                if (e.getIdInt() == idInt) {
                    return e;
                }
            }
        }
        ID id = null;
        for (ID idCheck : allIDs) {
            if (idCheck.getId() == idInt) {
                id = idCheck;
                break;
            }
        }
        if (id == null) {
            return null;
        }
        Entity e = null;
        try {
            e = (Entity) id.read();
            if (e != null) {
                e.initialize(id, this);
                allEntities.add(e);
            }
        } catch (IDReadException ex) {
            OldLogger.error(ex.getClass().getName() + " " + ex.getMessage());
        }   
        return e;
    }
    
    private void addToIndex(Entity e) {
        if (e instanceof Source) {
            sourceIndex.add(e.getIdInt());
        } else if (e instanceof Author) {
            authorIndex.add(e.getIdInt());
        } else if (e instanceof Tag) {
            Tag t = (Tag) e;
            allTagIndex.add(e.getIdInt());
            if (t.isKey()) {
                keyIndex.add(e.getIdInt());
            }
        } else if (e instanceof Entry) {
            Entry en = (Entry) e;
            if (!en.hasTags() && !en.hasSource()) {
                noTagIndex.add(e.getIdInt());
            }
        }
    }

    private void deleteDoubleTag(Entity e) {
        /*List<Tag> tags;
        if (e instanceof Tag) {
            Tag t = (Tag) e;
            tags = t.getTags();
            for (Tag tCheck : tags) {
                for (Tag tCheck2 : tags) {
                    if (tCheck.tCheck2) {
                        t.removeTag(t);
                    }
                }
            }
        } else if (e instanceof Entry) {
            Entry en = (Entry) e;
            tags = en.getTags();
        } else if (e instanceof Source) {
            Source s = (Source) e;
            tags = s.getTags();
        } else {
            return;
        }*/
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
                        ID id = ID.forgeIDWithoutWriting(idInt, offset, size, allIDs);
                        id.setIdOffsetByOrdinalNumber(i);
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
            Logger.getLogger(IDManager.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            Logger.getLogger(IDManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(IDManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void writeAllIDs() {
        writeAllIDs(idFile);
    }
    
    private void writeAllIDs(File idFile) {
        try {
            FileOutputStream fstream = new FileOutputStream(idFile);
            FileChannel fchannel = fstream.getChannel();
            ByteBuffer bb = ByteBuffer.allocate(4 + allIDs.size() * 12);
            bb.position(0);
            bb.putInt(allIDs.size());
            for (int i = 0; i < allIDs.size(); i++) {
                ID id = allIDs.get(i);
                id.setIdOffsetByOrdinalNumber(i);
                bb.putInt(id.getId());
                bb.putInt(id.getOffset());
                bb.putInt(id.getSize());
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
    }
    
    public void dropEntity(Entity e) {
        allEntities.remove(e);        
    }

    public void addEntity(Entity e) {
        if (e != null && !allIDs.contains(e)) {
            ID id = forgeID();
            e.setID(id);
        }
    }

    protected void deleteEntity(Entity e) {
        if (e instanceof Source) {
            sourceIndex.remove(e.getIdInt());
        } else if (e instanceof Author) {
            authorIndex.remove(e.getIdInt());
        } else if (e instanceof Tag) {
            Tag t = (Tag) e;
            allTagIndex.remove(t.getIdInt());
            if (t.isKey()) {
                keyIndex.remove(t.getIdInt());
            }
        } else if (e instanceof Entry) {
            Entry en = (Entry) e;
            if (!en.hasTags()) {
                noTagIndex.remove(en.getIdInt());
            }
        }
        allIDs.remove(e.getId());
        allEntities.remove(e);
        writeAllIDs();
    }

    public List<Tag> getTags(String tagName) {
        List<ID> ids = new ArrayList<ID>();
        List<Tag> tags = new ArrayList<Tag>();
        ids.addAll(allIDs);
        for (Entity e : allEntities) {
            if (isTag(e, tagName)) {
                tags.add((Tag) e);
            } else {
                ids.remove(e.getId());
            }
        }
        for (ID id : ids) {
            try {
                Entity e = id.read();
                if (isTag(e, tagName)) {
                    tags.add((Tag) e);
                } else {
                    dropEntity(e);
                }
            } catch (IDReadException ex) {
                OldLogger.error(ex.getClass().getName() + " " + ex.getMessage());
            }
        }
        return tags;
    }
    
    private boolean isTag(Entity e, String tagName) {
        if (e == null) {
            return false;
        }
        if (!(e instanceof Tag) || e instanceof Author) {
            return false;
        }
        Tag t = (Tag) e;
        if (t.getName().toLowerCase().contains(tagName.toLowerCase())) {
            return true;
        } else {
            return false;
        }
    }
    
    protected ID forgeID() {
        ID id = new ID(allIDs);
        id.setId(curID);
        curID++;
        id.setOffset(curBigFileSize);
        return id;
    }

    public Tag forgeTag(String tagName) {
        ID id = forgeID();
        Tag t = new Tag(id, this);
        t.setName(tagName);
        allEntities.add(t);
        id.write(t, bigFile);
        curBigFileSize += id.getClusters() * CLUSTER_SIZE;
        allTagIndex.add(id.getId());
        return t;
    }

    public Entry forgeEntry(String content) {
        ID contentID = forgeID();
        Content c = new Content(contentID, this);
        writeEntity(c);
        curBigFileSize += contentID.getClusters() * CLUSTER_SIZE;

        ID id = forgeID();
        Entry e = new Entry(id, contentID.getId(), this);
        writeEntity(e);
        e.saveContent(content);
        allEntities.add(e);
        curBigFileSize += id.getClusters() * CLUSTER_SIZE;
        return e;
    }

    public Source forgeSource(SourceType sourceType) {
        ID id = forgeID();
        Source source = new Source(id, this);
        source.setSourceType(sourceType);
        allEntities.add(source);
        writeEntity(source);
        curBigFileSize += id.getClusters() * CLUSTER_SIZE;
        sourceIndex.add(id.getId());
        return source;
    }

    public Source createSourceFromField(SourceField field, List<SourceField> allFields) {
        Source s = forgeSource(null);   
        if (field.getType().getParentSourceType() != null) {
            s.setSourceType(field.getType().getParentSourceType());
        } 
        SourceField mainField = field.parentField();
        s.setMainField(mainField);
        for (SourceFieldType sft : mainField.getType().getDependentTypes()) {
            for (SourceField sfCheck : allFields) {
                if (sfCheck == null) System.out.println("field null");
                if (sfCheck.getType() == null) System.out.println("field " + sfCheck.getValue() + " type null");
                if (sfCheck.getType().equals(sft)) {
                    s.addSourceField(sfCheck.copy());
                }
            }
        } 
        return s;
    }
    
    public Author forgeAuthor(String lastName, String firstName,
            String patronymic) {
        ID id = forgeID();
        Author auth = new Author(id, this);
        auth.setAuthorName(lastName, firstName, patronymic);
        allEntities.add(auth);
        writeEntity(auth);
        curBigFileSize += id.getClusters() * CLUSTER_SIZE;
        authorIndex.add(id.getId());
        return auth;
    }

    public void writeEntity(Entity e) {
        if (e != null) {
            ID id = e.getId();
            if (id == null) {
                id = forgeID();
            }
            int clusters = id.getClusters();
            boolean sizeExceeded = id.write(e, bigFile);
            if (sizeExceeded) {
                int clustersToPush = id.getClusters() - clusters;
                Collections.sort(allIDs, new IDComparator());
                int idIndex = allIDs.indexOf(id);
                if (idIndex < allIDs.size() - 1) {
                    rewriteBigFile(clustersToPush, (id.getOffset() + (clusters
                            * CLUSTER_SIZE)));
                    rewriteIDs(clustersToPush, idIndex);
                }
                curBigFileSize += clustersToPush * CLUSTER_SIZE;
                id.write(e, bigFile);
            }
        }
    }

    private void rewriteIDs(int clustersToPush, int indexStart) {
        for (int i = indexStart + 1; i < allIDs.size(); i++) {
            ID id = allIDs.get(i);
            id.setOffset(id.getOffset() + clustersToPush * CLUSTER_SIZE);
        }
    }

    private void rewriteBigFile(int clustersToPush, int startPushing) {
        try {
            FileInputStream fis = new FileInputStream(bigFile);
            fis.skip(startPushing);
            byte[] bytes = new byte[(int) bigFile.length() - startPushing];
            int bytesRead = fis.read(bytes);
            RandomAccessFile ras = new RandomAccessFile(bigFile, "rw");
            ras.getChannel().write(ByteBuffer.wrap(bytes), startPushing +
                    clustersToPush * CLUSTER_SIZE);
            ras.close();
            fis.close();
        } catch (IOException ex) {
            Logger.getLogger(ID.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ID getID(int id) {
        for (ID idCheck : allIDs) {
            if (idCheck == null) continue;
            if (idCheck.getId() == id)
                return idCheck;
        }
        return null;
    }

    public List<ID> getIDs(){
        return allIDs;
    }

    public void changeKeyTag(int id, boolean key){
        if (key) {
            keyIndex.add(id);
        } else {
            keyIndex.remove(id);
        }
    }

    public List<Tag> getKeyTags() {
        List<Tag> result = new ArrayList<Tag>();
        List<Integer> ids = new ArrayList<Integer>();
        ids.addAll(keyIndex.getValues());
     /*   for (Entity e : allEntities) {
            if (ids.contains(e.getIdInt())) {
                if (!(e instanceof Tag)) {
                     EOLogger.idMismatch(e, e.getIdInt(), Tag.class);
                     continue;
                 }
                ids.remove(new Integer(e.getId().getId()));
                result.add((Tag) e);
            }
        } */
        if (ids.size() > 0) {
            for (int id : ids) {
                for (ID idCheck : allIDs) {
                     if (idCheck.getId() == id) {
                         Entity e = getEntity(id);
                         if (!(e instanceof Tag)) {
                             OldLogger.idMismatch(e, id, Tag.class);
                             break;
                         }
                         result.add((Tag) e);
                     }
                }
            }
        }
        return result;
    }

    public Collection<Source> getSources(String nameBegin, boolean nonTypedAllowed, 
            SourceFieldType mainFieldType) {
        List<Integer> ids = new ArrayList<Integer>();
        ids.addAll(sourceIndex.getValues());
        TreeSet<Source> result = new TreeSet<Source>(new SourceComparator());
        
        for (int idint : ids) {
            if (idint == 9916) {
                String s = new String();
            }
             Entity e = getEntity(idint);
             if (e instanceof Source) {
                Source s = (Source) e;
                if (checkSource(s, nonTypedAllowed, mainFieldType, nameBegin)) {
                    result.add(s);
                }
             } else {
                 OldLogger.idMismatch(e, idint, Source.class);
             }
        }        
        return result;
    }
    
    private boolean checkSource(Source s, boolean nonTypedAllowed, 
            SourceFieldType mainFieldType, String nameBegin) {
        if (!nonTypedAllowed && s.getSourceType() == null) return false;;
        if (mainFieldType != null) {
            if (s.getMainField() != null) {
                if (!s.getMainField().getType().equals(mainFieldType)) 
                    return false;
                String name = s.getMainField().getValue();
                if (!fits(nameBegin, name)) 
                    return false;
                return true;
            } else {
                return false;
            }
        } else {
            String name = s.getName();
            if (!fits(nameBegin, name)) 
                return false;
            return true;
        }
    }

    public Collection<Author> getAuthors(String nameBegin) {
        List<Integer> ids = new ArrayList<Integer>();
        ids.addAll(authorIndex.getValues());
        TreeSet<Author> result = new TreeSet<Author>(new AuthorComparator());
        
        for (int idint : ids) {
            Entity e = getEntity(idint);
            if (e instanceof Author) {
                Author a = (Author) e;
                if (!fits(nameBegin, a.getName())) {
                    continue;
                }
                result.add((Author) e);
            } else {
                OldLogger.idMismatch(e, idint, Author.class);
            }
        }
        return result;
    }

    private boolean fits(String nameBegin, String name) {
        if (nameBegin.isEmpty()) return true;
        if (name.length() < nameBegin.length()) {
            return false;
        }
        String comp = name.substring(0, nameBegin.length());
        if (!comp.equalsIgnoreCase(nameBegin)) {
            return false;
        }
        return true;
    }

    public ArrayList<Tag> getAllTags() {
        ArrayList<Tag> result = new ArrayList<Tag>();
        for (int id : allTagIndex.getValues()) {
            for (ID idCheck : allIDs) {
                if (idCheck.getId() == id) {
                    result.add((Tag) getEntity(id));
                }
            }
        }
        return result;
    }

    public List<Entry> getUnreadEntries() {
        List<Entry> result = new ArrayList<Entry>();
        for (int id : noTagIndex.getValues()) {
            for (ID idCheck : allIDs) {
                if (idCheck.getId() == id) {
                    Entity e = getEntity(id);
                    if (e instanceof Entry) {
                        result.add((Entry) e);
                    } else {
                        OldLogger.idMismatch(e, id, Entry.class);
                    }
                }
            }
        }
        return result;
    }

    public void entryHasTag(Entry e) {
        noTagIndex.remove(e.getId().getId());
    }

    public void entryHasNoTag(Entry e) {
        noTagIndex.add(e.getId().getId());
    }

    public void reforgeAuthor(Author a) {
        String name = a.getFirstName();
        a.setFirstName(a.getLastName());
        a.setLastName(name);
    }

    public boolean removeID(int id) {
        ID toRemove = null;
        for (ID idCheck : allIDs) {
            if (idCheck.getId() == id) {
                toRemove = idCheck;
            }
        }
        if (toRemove != null) {
            allIDs.remove(toRemove);
            writeAllIDs();
            return true;
        } else {
            return false;
        }
    }

    public void backup() {
        writeAllIDs(new File(backupFolder, "ids"));
        File backupBigFile = new File(backupFolder, "objects");
        for (ID id : allIDs) {
            System.out.println(id.getOffset());
            try {
                Entity e = id.read();
                if (e == null) {
                    continue;
                }
                id.write(e, backupBigFile);
            } catch (IDReadException ex) {
                System.err.println(ex.getMessage());
               // ex.printStackTrace();
            }            
        }
        for (Index i : indexes) {
            i.rewrite(backupFolder);
        }
    }

    public int getCurID() {
        return curID;
    }

}
