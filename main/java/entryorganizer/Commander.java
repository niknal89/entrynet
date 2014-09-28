/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer;

import com.google.gson.Gson;
import entryorganizer.datastorage.Configs;
import entryorganizer.datastorage.DataManager;
import entryorganizer.datastorage.Loader;
import entryorganizer.datastorage.RepresentationLoader;
import entryorganizer.datastorage.TypeLoader;
import entryorganizer.entities.EntityType;
import entryorganizer.entities.representations.EntityRepresentation;
import entryorganizer.entities.representations.RepresentationSet;
import entryorganizer.gui.Resources;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author teopetuk89
 */
public class Commander {
    
    private DataManager dataManager;
    private TypeLoader typeLoader; 
    private RepresentationLoader representationLoader;
    private Loader loader = new Loader();
    private Configs configs;
    private Resources resources;
    
    private List<EntityType> entityTypes = new ArrayList<EntityType>();
    
    public Commander(String configPath, String dataPath) {
        loadConfigs(configPath);
        typeLoader = new TypeLoader(configs.getDataTypesPath(), 
                configs.getEntityTypesPath());
        try {
            typeLoader.load();
            entityTypes = typeLoader.getEntityTypes();
        } catch (IOException ex) {
            Logger.getLogger(Commander.class.getName()).log(Level.SEVERE, null, ex);
        }
        dataManager = new DataManager(dataPath, this);
        representationLoader = new RepresentationLoader(configs.getRepresentationsPath(), 
            configs.getRepresentationSetsPath(), this);
        
        try {
            representationLoader.load();
        } catch (IOException ex) {
            Logger.getLogger(Commander.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        resources = new Resources();
        resources.loadImages();
    }
    
    private void loadConfigs(String configPath) {
        try {
            String content = loader.loadFile(configPath);
            Gson gson = new Gson(); 
            configs = gson.fromJson(content, Configs.class);
        } catch (IOException ex) {
            Logger.getLogger(Commander.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
        
    public List<EntityType> getEntityTypes() {
        return entityTypes;
    }    

    public EntityType getEntityType(String name) {
        for (EntityType et : entityTypes) {
            if (et.is(name)) {
                return et;
            }
        }
        return null;
    }
    
    public EntityRepresentation getRepresentation(String name) {
        for (EntityRepresentation r : representationLoader.getLoaded()) {
            if (r.getName().equals(name))
                return r;
        }
        return null;
    }
    
    public RepresentationSet getRepresentationSet(String name) {
        for (RepresentationSet rs : representationLoader.getLoadedSets()) {
            if (rs.getName().equals(name))
                return rs;
        }
        return null;
    }
    
    public DataManager getDataManager() {
        return dataManager;
    }

    public Resources getResources() {
        return resources;
    }
        
}
