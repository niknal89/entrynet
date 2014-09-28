/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.datastorage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import entryorganizer.Commander;
import entryorganizer.entities.EntityType;
import entryorganizer.entities.FieldLimiters;
import entryorganizer.entities.representations.EntityRepresentation;
import entryorganizer.entities.representations.FieldRepresentation;
import entryorganizer.entities.representations.Representation;
import entryorganizer.entities.representations.RepresentationSet;
import entryorganizer.entities.representations.StringRepresentation;
import entryorganizer.entities.representations.StringRepresentation.Condition;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Администратор
 */
public class RepresentationLoader {
    
    private String path;
    private String setsPath;
    private Loader loader = new Loader();
    private Commander commander;
    
    private List<EntityRepresentation> loaded;
    private List<RepresentationSet> loadedSets;

    public RepresentationLoader(String path, String setsPath, Commander commander) {
        this.path = path;
        this.setsPath = setsPath;
        this.commander = commander;
    }
    
    public void load() throws IOException {
        String content = loader.loadFile(path);
        GsonBuilder gsonb = new GsonBuilder();
        gsonb.registerTypeAdapter(EntityRepresentation.class, 
                new RepresentationLoader.RepDes());
        Gson gson = gsonb.create();
        EntityRepresentation[] arr = gson.fromJson(content, EntityRepresentation[].class);
        loaded = new ArrayList<EntityRepresentation>(Arrays.asList(arr));
        loaded.removeAll(Collections.singleton(null));
        for (EntityRepresentation er : loaded) {
            for (Representation r : er.getField_order()) {
                if (r instanceof FieldRepresentation) {
                    FieldRepresentation fr = (FieldRepresentation) r;
                    for (EntityRepresentation erCheck : loaded) {
                        if (erCheck.getName().equals(fr.getRepresentationStr())) {
                            fr.setRepresentation(erCheck);
                            break;
                        }
                    } 
                }
            }
        }
        
        String content2 = loader.loadFile(setsPath);
        gsonb = new GsonBuilder();
        gsonb.registerTypeAdapter(RepresentationSet.class, 
                new RepresentationLoader.RepSetDes());
        gson = gsonb.create();
        RepresentationSet[] arr2 = gson.fromJson(content2, RepresentationSet[].class);
        loadedSets = new ArrayList<RepresentationSet>(Arrays.asList(arr2));
        loadedSets.removeAll(Collections.singleton(null));
    }

    public List<EntityRepresentation> getLoaded() {
        return loaded;
    }

    public List<RepresentationSet> getLoadedSets() {
        return loadedSets;
    }
    
    private class RepDes implements 
            JsonDeserializer<EntityRepresentation> {
        
        @Override
        public EntityRepresentation deserialize(JsonElement json, Type typeOfT, 
                JsonDeserializationContext context) {
            JsonObject jo = json.getAsJsonObject();
            JsonElement je = jo.get("name");
            if (je == null) return null;

            EntityRepresentation er = new EntityRepresentation();
            er.setName(jo.get("name").getAsString());
            
            je = jo.get("type");
            if (je != null) {
                String type = je.getAsString();
                EntityType et = commander.getEntityType(type);
                er.setType(et);
            }
                        
            je = jo.get("field_order");          
            if (je != null) {
                List<Representation> fields = new ArrayList<Representation>();
                Iterator<JsonElement> it = je.getAsJsonArray().iterator();
                while (it.hasNext()) {
                    je = it.next();
                    Representation r = null;
                    JsonElement check = je.getAsJsonObject().get("field");
                    if (check == null) {
                        r = loadStringRep(je);
                    } else {
                        r = loadFieldRep(je);
                    }
                    fields.add(r);
                }
                er.setField_order(fields);
            }
            return er;
        }
        
        private FieldRepresentation loadFieldRep(JsonElement je) {
            FieldRepresentation fr = new FieldRepresentation(commander.getDataManager());
            fr.setField(
                    je.getAsJsonObject().get("field").getAsString());
            JsonElement value = je.getAsJsonObject().get("representation");
            if (value != null) fr.setRepresentationStr(value.getAsString());
            value = je.getAsJsonObject().get("repeat");
            if (value != null) fr.setRepeat(value.getAsBoolean());
            value = je.getAsJsonObject().get("first_prefix");
            if (value != null) fr.setFirst_prefix(value.getAsString());
            value = je.getAsJsonObject().get("prefix");
            if (value != null) fr.setPrefix(value.getAsString());
            value = je.getAsJsonObject().get("postfix");
            if (value != null) fr.setPostfix(value.getAsString());
            value = je.getAsJsonObject().get("last_postfix");
            if (value != null) fr.setLast_postfix(value.getAsString());
            value = je.getAsJsonObject().get("length");
            if (value != null) fr.setLength(value.getAsInt());
            return fr;
        }
        
        private StringRepresentation loadStringRep(JsonElement je) {
            StringRepresentation sr = new StringRepresentation();
            sr.setString(
                    je.getAsJsonObject().get("string").getAsString());
            JsonElement value = je.getAsJsonObject().get("condition");
            if (value == null) {
                sr.setCondition(Condition.none);
                return sr;
            }
            StringRepresentation.Condition condition =
                    StringRepresentation.Condition.get(value.getAsString());
            sr.setCondition(condition);
            return sr;
        }
        
      }
    
    private class RepSetDes implements 
            JsonDeserializer<RepresentationSet> {

        public RepresentationSet deserialize(JsonElement json, Type typeOfT, 
                JsonDeserializationContext jdc) throws JsonParseException {
            JsonObject jo = json.getAsJsonObject();
            JsonElement je = jo.get("name");
            if (je == null) return null;
            
            RepresentationSet rs = new RepresentationSet(je.getAsString());
            je = jo.get("representations");
            if (je != null) {                
                Iterator<JsonElement> it = je.getAsJsonArray().iterator();
                while (it.hasNext()) {
                    je = it.next();
                    JsonObject jsonRep = je.getAsJsonObject();
                    String type = jsonRep.get("type").getAsString();
                    String representation = jsonRep.get("representation").getAsString();
                    EntityType et = commander.getEntityType(type);
                    EntityRepresentation rep = commander.getRepresentation(representation);
                    if (et == null || rep == null) {
                        System.out.println("For repSet " + rs.getName() + " either type or representation has a wrong name; type " + type + ", rep " + representation);
                    }
                    rs.getRepresentations().put(et, rep);
                }
            }
            return rs;
        }
        
    }
    
}
