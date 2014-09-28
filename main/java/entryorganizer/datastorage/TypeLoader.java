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
import entryorganizer.entities.EntityType;
import entryorganizer.entities.FieldLimiters;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author teopetuk89
 */
public class TypeLoader {
    
    private String dataTypePath;
    private String typePath;
               
    private Loader loader;
    
    private List<EntityType> entityTypes = new ArrayList<EntityType>();
    
    public TypeLoader(String dataTypePath, String typePath) {
        this.dataTypePath = dataTypePath;
        this.typePath = typePath;     
        this.loader = new Loader();
    }

    public void load() throws IOException {
        entityTypes = parseEntityTypes();
    }
        
    private List<EntityType> parseEntityTypes() throws IOException {       
        String content = loader.loadFile(typePath);
        GsonBuilder gsonb = new GsonBuilder();
        gsonb.registerTypeAdapter(EntityType.class, 
                new EntityTypeDeserializer());
        Gson gson = gsonb.create();
        EntityType[] arr = gson.fromJson(content, EntityType[].class);
        List<EntityType> list = new ArrayList<EntityType>(Arrays.asList(arr));
        list.removeAll(Collections.singleton(null));
        return list;
    }
    
    private class EntityTypeDeserializer implements JsonDeserializer<EntityType> {
        
        private List<EntityType> types = new ArrayList<EntityType>();
        
        private EntityTypeDeserializer() {
        }
        
        @Override
        public EntityType deserialize(JsonElement json, Type typeOfT, 
                JsonDeserializationContext context) {
            JsonObject jo = json.getAsJsonObject();
            JsonElement je = jo.get("name");
            if (je == null) return null;

            EntityType type = new EntityType();
            type.setName(jo.get("name").getAsString());
            
            je = jo.get("parent");
            if (je != null) {
                String parentName = je.getAsString();
                for (EntityType et : types) {
                    if (et.getName().equals(parentName)) {
                        type.setParent(et);
                    }
                }
                if (type.getParent() == null) {
//                    throw
                }
            }
            
            je = jo.get("allow_search");
            if (je != null) {
                type.setAllowSearch(je.getAsBoolean());
            }
            
            je = jo.get("allowedFields");          
            if (je != null) {
                List<FieldLimiters> allowedFields = 
                        new ArrayList<FieldLimiters>();
                Iterator<JsonElement> it = je.getAsJsonArray().iterator();
                while (it.hasNext()) {
                    je = it.next();
                    String fieldName = 
                            je.getAsJsonObject().get("field").getAsString();
                    int limit = 
                            je.getAsJsonObject().get("limit").getAsInt();
                    String typeName = 
                            je.getAsJsonObject().get("type").getAsString();
                    FieldLimiters fl = new FieldLimiters(limit, typeName, fieldName);
                    JsonElement je1 = je.getAsJsonObject().get("return_field");
                    if (je1 != null)
                        fl.setReturnField(je1.getAsString());
                    allowedFields.add(fl);
                }
                type.setAllowedFields(allowedFields);
            }
            
            if (type == null) 
                new String();
            
            types.add(type);
            return type;
        }
        
      }

    public List<EntityType> getEntityTypes() {
        return entityTypes;
    }
    
} 
    
    

