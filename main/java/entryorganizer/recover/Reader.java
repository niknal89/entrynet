/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entryorganizer.recover;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import entryorganizer.data.SourceFieldType;
import entryorganizer.data.SourceFieldValueType;
import entryorganizer.data.SourceType;

/**
 *
 * @author teopetuk89
 */
public class Reader {
  
    private String sourceTypePath;
    private String sourceFieldTypePath;
    private String sourceFieldValueTypePath;
    
       
    public Reader(String sourceTypePath, String sourceFieldTypePath,
            String sourceFieldValueTypePath) {
        this.sourceTypePath = sourceTypePath;
        this.sourceFieldTypePath = sourceFieldTypePath;
        this.sourceFieldValueTypePath = sourceFieldValueTypePath;       
    }
    
    public void load() {
        try {
            String content = loadFile(sourceFieldValueTypePath);
            parseValueType(content);
            content = loadFile(sourceFieldTypePath);
            parseFieldType(content);
            content = loadFile(sourceTypePath);
            parseSourceType(content);
        } catch (IOException ex) {
            Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    
    private String loadFile(String fileName) throws IOException {         
        String content;
        URL res = getClass().getResource(fileName);           
        byte[] encoded = null;
        try {
            encoded = Files.readAllBytes(Paths.get(res.toURI()));
        } catch (URISyntaxException ex) {
            Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
        }
        content = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(encoded)).toString();        
        return content;
    }
    
    private void parseValueType(String content) {
        Gson gson = new Gson(); 
        SourceFieldValueType[] arr = gson.fromJson(content, SourceFieldValueType[].class);
        for (SourceFieldValueType sfvt : arr) {
            if (sfvt != null)
                SourceFieldValueType.addSFVT(sfvt);
        }        
    }
    
    private void parseFieldType(String content) {
        GsonBuilder gsonb = new GsonBuilder();
        gsonb.registerTypeAdapter(SourceFieldType.class, new FieldTypeDeserializer());
        Gson gson = gsonb.create();
        SourceFieldType[] arr = gson.fromJson(content, SourceFieldType[].class);
        for (SourceFieldType sft : arr) {
            if (sft != null)
                SourceFieldType.addSFT(sft);
        }
    }
    
    private class FieldTypeDeserializer implements JsonDeserializer<SourceFieldType> {
      
       @Override
      public SourceFieldType deserialize(JsonElement json, Type typeOfT, 
              JsonDeserializationContext context) {
          JsonObject jo = json.getAsJsonObject();
          JsonElement je = jo.get("name");
          if (je == null) return null;
          SourceFieldType sft = new SourceFieldType(je.getAsString());
          sft.setRuName(jo.get("ruName").getAsString());
          sft.setSeveralPossible(jo.get("severalPossible").getAsBoolean());
          sft.setObjectPossible(jo.get("objectPossible").getAsBoolean());
          je = jo.get("prefix");
          if (je != null) sft.setPrefix(je.getAsString());
          je = jo.get("postfix");
          if (je != null) sft.setPostfix(je.getAsString());
          je = jo.get("parentFieldType");
          if (je != null) sft.setParentFieldType(je.getAsString());
          je = jo.get("valueType");
          SourceFieldValueType sfvt = SourceFieldValueType.getSFVT(
                 je.getAsString());
          sft.setValueType(sfvt);
          je = jo.get("dependentTypes");          
          if (je != null) {
              ArrayList<String> dt = new ArrayList<String>();
              Iterator<JsonElement> it = je.getAsJsonArray().iterator();
              while (it.hasNext()) {
                  je = it.next();
                  String typeName = je.getAsJsonObject().get("typeName").getAsString();
                  dt.add(typeName);
              }
              sft.setDependentTypes(dt);
          }
          return sft;
      }
    }
    
    private void parseSourceType(String content) {
        GsonBuilder gsonb = new GsonBuilder();
        gsonb.registerTypeAdapter(SourceType.class, new SourceTypeDeserializer());
        Gson gson = gsonb.create();
        SourceType[] arr = gson.fromJson(content, SourceType[].class);
        for (SourceType st : arr) {
            if (st != null)
                SourceType.addST(st);
        }
    }
    
    private class SourceTypeDeserializer implements JsonDeserializer<SourceType> {
        @Override
        public SourceType deserialize(JsonElement json, Type typeOfT, 
              JsonDeserializationContext context) {
          JsonObject jo = json.getAsJsonObject();
          JsonElement je = jo.get("name");
          if (je == null) {
              return null;
          }
          SourceType st = new SourceType(je.getAsString());
          je = jo.get("ruName");
          st.setRuName(je.getAsString());
          Iterator<JsonElement> it = jo.get("fields").getAsJsonArray().iterator();
          while (it.hasNext()) {
              jo = it.next().getAsJsonObject();              
              String fieldTypeName = jo.get("fieldName").getAsString();
              SourceFieldType sft = SourceFieldType.getSFT(fieldTypeName);
              if (sft != null) {
                st.addFieldType(sft);
                if (jo.get("mainField") != null) {
                    st.setMainField(sft);
                }
              }
          }
          return st;
      }
    } 
    
}
