/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.recover;

import entryorganizer.Commander;
import entryorganizer.data.Author;
import entryorganizer.data.Content;
import entryorganizer.data.Entity;
import entryorganizer.data.Entry;
import entryorganizer.data.ID;
import entryorganizer.data.Source;
import entryorganizer.data.SourceField;
import entryorganizer.data.Tag;
import entryorganizer.datastorage.DataManager;
import entryorganizer.entities.EntityType;
import entryorganizer.entities.Field;
import entryorganizer.entities.Text;
import entryorganizer.entities.wrappers.Wrapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Администратор
 */
public class Recoverer {
    
    private String oldEMFiles;
    private IDManager idManager;
    private DataManager dataManager;
    private Commander commander;

    public Recoverer(String oldEMFiles, Commander comm) {
        this.oldEMFiles = oldEMFiles;
        idManager = new IDManager(oldEMFiles);
        this.dataManager = comm.getDataManager();
        commander = comm;
        Reader r = new Reader("SourceType.txt", "SourceFieldType.txt", "SourceFieldValueType.txt");
        r.load();
    }
    
    private int idCount;
    
    public void recover() throws IDReadException, Exception {
        List<ID> oldIDs = idManager.getIDs();
        idCount = idManager.getCurID();
        
        for (ID oldID : oldIDs) {
            Entity oldEntity = idManager.getEntity(oldID.getId());
            if (oldEntity == null) 
                continue;
            Wrapper newEntity = null;
            Class c = oldEntity.getClass();
            
            if (c.equals(Author.class)) {
                
                Author a = (Author) oldEntity;
                newEntity = forgeEntity("person", oldID.getId());
                dataManager.forgeText(newEntity.getWrapped(), "first_name", a.getFirstName());
                dataManager.forgeText(newEntity.getWrapped(), "last_name", a.getLastName());
                if (a.getPatronymic() != null) {
                    dataManager.forgeText(newEntity.getWrapped(), "other_name", a.getPatronymic());
                }
                for (Integer i : a.getEntryIDs()) {
                    dataManager.forgeLink(newEntity.getWrapped(), "description", i);
                }
                for (Integer i : a.getSourceIDs()) {
                    dataManager.forgeLink(newEntity.getWrapped(), "source_written", i);
                }
                for (Integer i : a.getTagIDs()) {
                    dataManager.forgeLink(newEntity.getWrapped(), "tags", i);
                }
                
            } else if (c.equals(Tag.class)) {
                
                Tag t = (Tag) oldEntity;
                newEntity = forgeEntity("tag", oldID.getId());
                dataManager.forgeText(newEntity.getWrapped(), "name", t.getName());
                for (Integer i : t.getEntryIDs()) {
                    dataManager.forgeLink(newEntity.getWrapped(), "entry", i);
                }
                for (Integer i : t.getSourceIDs()) {
                    dataManager.forgeLink(newEntity.getWrapped(), "source", i);
                }
                for (Integer i : t.getTagIDs()) {
                    dataManager.forgeLink(newEntity.getWrapped(), "tag", i);
                }
                
            } else if (c.equals(Entry.class)) {
                
                Entry e = (Entry) oldEntity;
                newEntity = forgeEntity("entry", oldID.getId());
                dataManager.forgeText(newEntity.getWrapped(), "content", e.readContent(true));
                dataManager.forgeParameter(newEntity.getWrapped(), "page_start", e.getPageStart());
                dataManager.forgeParameter(newEntity.getWrapped(), "page_end", e.getPageEnd());
                if (e.getSourceID() != null)
                    dataManager.forgeLink(newEntity.getWrapped(), "source", e.getSourceID());
                for (Integer i : e.getTagIDs()) {
                    dataManager.forgeLink(newEntity.getWrapped(), "tag", i);
                }
                
            } else if (c.equals(Content.class)) {
                
            } else if (c.equals(Source.class)) {
                
                Source s = (Source) oldEntity;
                restoreSource(s);
                
            } else {
                throw new Exception("Unprepared to parse class " + c.getSimpleName() +
                        " for entity " + oldEntity.toString());
            }
            
        }
        
        for (Wrapper jArticle : jArticles.keySet()) {
            String jName = jArticles.get(jArticle);
            for (String jNameCheck : journals.keySet()) {
                if (jName.equals(jNameCheck)) {
                    Wrapper journal = 
                            journals.get(jNameCheck);
                    dataManager.forgeLink(journal.getWrapped(), "article", jArticle.getWrapped().getIdInt());
                    dataManager.forgeLink(jArticle.getWrapped(), "journal", journal.getWrapped().getIdInt());
                }
            }
        }
        
        for (int key : idManager.keyIndex.getValues()) {
            dataManager.setKey(key, true);
        }
    }
    
    private Wrapper forgeEntity(String entityType, int id) throws Exception {        
        EntityType et = commander.getEntityType(entityType);
        if (et == null) 
            throw new Exception("bad entity type: " + entityType);
        return dataManager.forgeWrapper(et, id);
    }
    
    int sobrSoch = 0;
    
    private void restoreSource(Source s) throws Exception {
        String st = s.getSourceTypeRaw();
        if (st == null) {
            if (s.getMainField() != null) {
                st = s.getMainField().getType().getName();
            }
        }
        String entityType = null;
        
        if (st == null) {
            entityType = "source";
            new String();
        } else if (st.equals("book")) {
            entityType = "book";
        } else if (st.equals("bookPart")) {
            entityType = "book_article";
        } else if (st.equals("journalArticle")) {
            entityType = "journal_article";
        } else if (st.equals("newspaperArticle")) {
            entityType = "newspaper_article";
        } else if (st.equals("journal\"")) {
            entityType = "journal";
        } else if (st.equals("archive_paper")) {
            entityType = "item";
        } else if (st.equals("internetArticle")) {
            entityType = "internet_article";
        } else if (st.equals("")) {
            entityType = "source";
        } else if (st.contains("bookPart")) {
            entityType = "book_article";
        } else if (st.contains("journalArticle")) {
            entityType = "journal_article";
        } else if (st.contains("book")) {
            entityType = "book";
        } else if (st.contains("journal")) {
            entityType = "journal";
        } else if (st.contains("newspaperArticle")) {
            entityType = "newspaper_article";
        } else if (st.contains("archive_paper")) {
            entityType = "item";
        } else if (st.contains("internetArticle")) {
            entityType = "internet_article";
        } else if (st.equals("volume_number")) {
            entityType = "source";
        } else if (st.equals("publisher")) {
            entityType = "publisher";
        } else if (st.equals("place")) {
            entityType = "place";
        } else if (st.equals("series")) {
            entityType = "series";
        } else if (st.equals("newspaper_title")) {
            entityType = "newspaper";
        } else if (st.equals("archive")) {
            entityType = "archive";
        } else if (st.equals("fund")) {
            entityType = "fund";
        } else if (st.equals("file")) {
            entityType = "file";
        } else if (st.equals("title")) {
            entityType = "book";
        } else if (st.equals("author")) {
            entityType = "person";
        } else if (st.equals("editor")) {
            entityType = "person";
        } else {
            throw new Exception("strange source type: " + st + ", unable to recognize");
        }
        
        Wrapper newEntity = 
                forgeEntity(entityType, s.getIdInt());
        
        if (s.getAuthorIDs() != null)
            for (Integer i : s.getAuthorIDs()) {
                dataManager.forgeLink(newEntity.getWrapped(), "author", i);
            }
        if (s.getEditorIDs() != null)
            for (Integer i : s.getEditorIDs()) {
                dataManager.forgeLink(newEntity.getWrapped(), "editor", i);
            }
        if (s.getEntryIDs() != null)
            for (Integer i : s.getEntryIDs()) {
                dataManager.forgeLink(newEntity.getWrapped(), "extract", i);
            }
        if (s.getTagIDs() != null)
            for (Integer i : s.getTagIDs()) {
                dataManager.forgeLink(newEntity.getWrapped(), "tag", i);
            }
        
        if (st == null) {
            
        } else if (entityType.equals("book")) {
            restoreBook(s, newEntity);
        } else if (entityType.equals("book_article")) {
            restoreBookPart(s, newEntity);
        } else if (entityType.equals("journal_article")) {
            restoreJournalArticle(s, newEntity);
        } else if (entityType.equals("newspaper_article")) {
            restoreNewspaperArticle(s, newEntity);
        } else if (entityType.equals("journal")) {
            restoreJournal(s, newEntity);
        } else if (entityType.equals("item")) {
            restoreArchivePaper(s, newEntity);
        } else if (entityType.equals("internet_article")) {
            restoreInternetArticle(s, newEntity);
        } else if (entityType.equals("person")) {
            restorePerson(s, newEntity);
        }
        
       /* for (SourceField sf : s.getFields()) {
            restoreMainField(sf, newEntity, s);
        }
        
        
        savedEntity = null; */
    }
    
    private void restoreBook(Source s, 
            Wrapper newS) throws Exception {
        for (SourceField sf : s.getFields()) {
            String type = sf.getType().getName();
            Wrapper newEntity = null;
            
            if (type.equals("title")) {
                
                dataManager.forgeText(newS.getWrapped(), "title", sf.getValue());
                
            } else if (type.equals("volumes")) {
                
                dataManager.forgeParameter(newS.getWrapped(), "volume_number", Integer.parseInt(sf.getValue()));
                
            } else if (type.equals("volume_number")) {
                
                newEntity = forgeEntity("volume", ++idCount);
                SourceField title = searchField(s, "volume_title");
                if (title != null)
                    dataManager.forgeText(newEntity.getWrapped(), "volume_title", title.getValue());
                dataManager.forgeLink(newEntity.getWrapped(), "book", newS.getWrapped().getIdInt());
                dataManager.forgeLink(newS.getWrapped(), "volume", newEntity.getID().getId());
                
            } else if (type.equals("edition") || 
                   type.equals("place") ||
                   type.equals("publisher") ||
                   type.equals("year")) {
                
                newEntity = forgeEntity("edition", ++idCount);
                SourceField ed = searchField(s, "edition");
                if (ed != null)
                    dataManager.forgeText(newEntity.getWrapped(), "edition", ed.getValue());
                SourceField pl = searchField(s, "place");
                if (pl != null) {
                    Wrapper newPl = 
                            searchExisting("place", "name", pl.getValue());
                    if (newPl == null) {
                        newPl = forgeEntity("place", ++idCount);
                        dataManager.forgeText(newPl.getWrapped(), "name", pl.getValue());
                    }
                    dataManager.forgeLink(newEntity.getWrapped(), "place", newPl.getWrapped().getIdInt());
                }
                SourceField pu = searchField(s, "publisher");
                if (pu != null) {
                    Wrapper newPu = 
                            searchExisting("publisher", "title", pu.getValue());
                    if (newPu == null) {
                        newPu = forgeEntity("publisher", ++idCount);
                        dataManager.forgeText(newPu.getWrapped(), "title", pu.getValue());
                    }
                    dataManager.forgeLink(newEntity.getWrapped(), "publisher", newPu.getWrapped().getIdInt());
                    dataManager.forgeLink(newPu.getWrapped(), "book", newEntity.getWrapped().getIdInt());
                }
                SourceField yr = searchField(s, "year");
                if (yr != null) 
                    dataManager.forgeParameter(newEntity.getWrapped(), "year", Integer.parseInt(yr.getValue()));

                dataManager.forgeLink(newS.getWrapped(), "edition", newEntity.getWrapped().getIdInt());
                dataManager.forgeLink(newEntity.getWrapped(), "book", newS.getWrapped());

            } else if (type.equals("libraryCode")) {
                
                newEntity = forgeEntity("library_code", ++idCount);
                dataManager.forgeText(newEntity.getWrapped(), "code", sf.getValue());
                dataManager.forgeLink(newS.getWrapped(), "library_code", newEntity.getWrapped().getIdInt());
                
            } else if (type.equals("series")) {
                
                newEntity = searchExisting("series", "title", sf.getValue());
                if (newEntity == null) {
                    newEntity = forgeEntity("series", ++idCount);
                    dataManager.forgeText(newEntity.getWrapped(), "title", sf.getValue());
                }
                dataManager.forgeLink(newEntity.getWrapped(), "book", newS.getWrapped().getIdInt());
                dataManager.forgeLink(newS.getWrapped(), "series", newEntity.getWrapped());
                
            } else if (type.equals("series_number")) {
                
                dataManager.forgeParameter(newS.getWrapped(), "series_number", Integer.parseInt(sf.getValue()));
                
            }
                    
            
        }
    }
    
    private void restoreBookPart(Source s, 
            Wrapper newS) throws Exception {
        SourceField titleField = null;
        for (SourceField sf : s.getFields()) {
            String type = sf.getType().getName();
            Wrapper newEntity = null;
            
            if (type.equals("pages")) {
                
                String[] pages = sf.getValue().split("-");
                dataManager.forgeParameter(newS.getWrapped(), "page_start", Integer.parseInt(pages[0]));
                if (pages.length > 1)
                    dataManager.forgeParameter(newS.getWrapped(), "page_end", Integer.parseInt(pages[1]));
                
            } else if (type.equals("title")) {
                
                dataManager.forgeText(newS.getWrapped(), "title", sf.getValue());
                titleField = sf;
                
            } else if (type.equals("book_title")) {
                
                sf.setFieldType("title");
                
            }
        }
        
        if (titleField != null)
            s.removeSourceField(titleField);
        
        Wrapper book = 
                forgeEntity("book", ++idCount);
        restoreBook(s, book);
        List<Field> fields = new ArrayList<Field>(newS.getWrapped().getFields());
        for (Field f : fields) {
            if (f.getName().equals("editor")) {
                book.getWrapped().addField(f);
                dataManager.writeEntity(book.getWrapped());
                newS.getWrapped().removeField(f);
                dataManager.writeEntity(newS.getWrapped());
            }
        }
        dataManager.forgeLink(newS.getWrapped(), "book", book.getWrapped().getIdInt());
        dataManager.forgeLink(book.getWrapped(), "article", newS.getWrapped().getIdInt());
    }
    
    private Map<Wrapper, String> jArticles = 
            new HashMap<Wrapper, String>();
    private Map<String, Wrapper> journals = 
            new HashMap<String, Wrapper>();    
    
    private void restoreJournalArticle(Source s, Wrapper newS) throws Exception {
      
        for (SourceField sf : s.getFields()) {
            String type = sf.getType().getName();
            Wrapper newEntity = null;
            
            if (type.equals("title")) {
                
                dataManager.forgeText(newS.getWrapped(), "title", sf.getValue());
            
            } else if (type.equals("volume")) {
                
                dataManager.forgeParameter(newS.getWrapped(), "volume", Integer.parseInt(sf.getValue()));
                
            } else if (type.equals("issue")) {
                
                dataManager.forgeText(newS.getWrapped(), "issue", sf.getValue());
                
            } else if (type.equals("year")) {
                
                dataManager.forgeParameter(newS.getWrapped(), "year", Integer.parseInt(sf.getValue()));
            
            } else if (type.equals("pages")) {
                
                String[] pages = sf.getValue().split("-");
                dataManager.forgeParameter(newS.getWrapped(), "page_start", Integer.parseInt(pages[0]));
                if (pages.length > 1)
                    dataManager.forgeParameter(newS.getWrapped(), "page_end", Integer.parseInt(pages[1]));
                
            } else if (type.equals("journal_title")) {
                
                jArticles.put(newS, sf.getValue());
                
            }
        }
    }
    
    private void restoreNewspaperArticle(Source s, Wrapper newS) throws Exception {
        
        Wrapper newspaper = null;
        String libraryCode = null;
        
        for (SourceField sf : s.getFields()) {
            String type = sf.getType().getName();
            Wrapper newEntity = null;
        
            if (type.equals("title")) {
                
                dataManager.forgeText(newS.getWrapped(), "title", sf.getValue());
            
            } else if (type.equals("newspaper_title")) {
                
                newEntity = searchExisting("newspaper", "title", sf.getValue());
                if (newEntity == null) {
                    newEntity = forgeEntity("newspaper", ++idCount);
                }
                newspaper = newEntity;
                dataManager.forgeLink(newEntity.getWrapped(), "article", newS.getWrapped().getIdInt());
                dataManager.forgeLink(newS.getWrapped(), "newspaper", newEntity.getWrapped().getIdInt());
                
            } else if (type.equals("issue")) {
                
                dataManager.forgeText(newS.getWrapped(), "issue", sf.getValue());
                
            } else if (type.equals("year")) {
                
                dataManager.forgeParameter(newS.getWrapped(), "year", Integer.parseInt(sf.getValue()));
            
            } else if (type.equals("pages")) {
                
                String[] pages = sf.getValue().split("-");
                dataManager.forgeParameter(newS.getWrapped(), "page_start", Integer.parseInt(pages[0]));
                if (pages.length > 1)
                    dataManager.forgeParameter(newS.getWrapped(), "page_end", Integer.parseInt(pages[1]));
                
            } else if (type.equals("date")) {
                
                dataManager.forgeText(newS.getWrapped(), "date", sf.getValue());
            
            } else if (type.equals("libraryCode")) {
                
                libraryCode = sf.getValue();
                        
            }
            
            if (libraryCode != null && newspaper != null) {
                Wrapper code = 
                        forgeEntity("library_code", ++idCount);
                dataManager.forgeText(code.getWrapped(), "code", libraryCode);
                dataManager.forgeLink(newspaper.getWrapped(), "library_code", code.getWrapped().getIdInt());
            }
        }
    }
    
    private void restoreJournal(Source s, Wrapper newS) throws Exception {
        for (SourceField sf : s.getFields()) {
            String type = sf.getType().getName();
            Wrapper newEntity = null;
            
            if (type.equals("journal_title")) {
                
                dataManager.forgeText(newS.getWrapped(), "title", sf.getValue());
                journals.put(sf.getValue(), newS);
                
            } else if (type.equals("libraryCode")) {
                
                newEntity = forgeEntity("library_code", ++idCount);
                dataManager.forgeText(newEntity.getWrapped(), "code", sf.getValue());
                dataManager.forgeLink(newS.getWrapped(), "library_code", newEntity.getWrapped().getIdInt());
                
            }
        }
    }
    
    private void restoreArchivePaper(Source s, Wrapper newS) throws Exception {
        Wrapper archive = null;
        Wrapper fund = null;
        Wrapper file = null;
        for (SourceField sf : s.getFields()) {
            String type = sf.getType().getName();
            Wrapper newEntity = null;
            
            if (type.equals("archive")) {
                
                archive = forgeEntity("archive", ++idCount);
                dataManager.forgeText(archive.getWrapped(), "title", sf.getValue());
                
            } else if (type.equals("fund")) {
                
                fund = forgeEntity("fund", ++idCount);
                dataManager.forgeText(fund.getWrapped(), "title", sf.getValue());
                
            } else if (type.equals("file")) {
                
                file = forgeEntity("file", ++idCount);
                dataManager.forgeText(file.getWrapped(), "file", sf.getValue());
                
            } else if (type.equals("item")) {
                
                dataManager.forgeText(newS.getWrapped(), "title", sf.getValue());
                
            }            
        }
        if (archive != null && fund != null && file != null) {
            dataManager.forgeLink(archive.getWrapped(), "fund", fund.getWrapped().getIdInt());
            dataManager.forgeLink(fund.getWrapped(), "archive", archive.getWrapped().getIdInt());
            dataManager.forgeLink(fund.getWrapped(), "file", file.getWrapped().getIdInt());
            dataManager.forgeLink(file.getWrapped(), "fund", fund.getWrapped().getIdInt());            
            dataManager.forgeLink(newS.getWrapped(), "file", file.getWrapped().getIdInt());
            dataManager.forgeLink(file.getWrapped(), "item", newS.getWrapped().getIdInt());
        }
    }
    
    private void restoreInternetArticle(Source s, Wrapper newS) throws Exception {
        for (SourceField sf : s.getFields()) {
            String type = sf.getType().getName();
            Wrapper newEntity = null;
        
            if (type.equals("title")) {
                
                dataManager.forgeText(newS.getWrapped(), "title", sf.getValue());
                
            } else if (type.equals("URL")) {
                
                dataManager.forgeText(newS.getWrapped(), "URL", sf.getValue());
                
            }        
        }
    }
       
    private void restorePerson(Source s, Wrapper newS) throws Exception {
        for (SourceField sf : s.getFields()) {
            String type = sf.getType().getName();
            Wrapper newEntity = null;
            
            if (type.equals("author") || type.equals("editor")) {
                
                dataManager.forgeText(newS.getWrapped(), "last_name", sf.getValue());
                
            } else if (type.equals("authorName") || type.equals("editorName")) {
                
                dataManager.forgeText(newS.getWrapped(), "first_name", sf.getValue());
                                
            } else if (type.equals("authorPatronymic") || type.equals("editorPatronymic")) {
                
                dataManager.forgeText(newS.getWrapped(), "other_name", sf.getValue());
                                
            }
        }
    }
     
    private SourceField searchField(Source source, String fieldName) {
        for (SourceField sf : source.getFields()) {
            if (sf.getType().getName().equals(fieldName)) {
                return sf;
            }
        }
        return null;
    }
    
    private Wrapper searchExisting(String type, String field, String value) {
        for (Wrapper e : dataManager.getLoaded()) {
            if (!e.getWrapped().getType().getName().equals(type))
                continue;
            for (Field f : e.getWrapped().getFields()) {
                if (f.getName().equals(field) && f instanceof Text) {
                    Text t = (Text) f;
                    if (t.getText().equals(value)) {
                        return e;
                    }
                    break;
                }
            }
        }
        return null;
    }
    
}
