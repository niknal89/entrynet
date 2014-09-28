/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.entities.representations;

import entryorganizer.entities.EntityType;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Администратор
 */
public class RepresentationSet {
    
    private Map<EntityType, EntityRepresentation> representations = 
            new HashMap<EntityType, EntityRepresentation>();
    
    private String name;

    public RepresentationSet(String name) {
        this.name = name;
    }

    public Map<EntityType, EntityRepresentation> getRepresentations() {
        return representations;
    }

    public String getName() {
        return name;
    }
        
    public EntityRepresentation getRepresentation(EntityType et) {
        return representations.get(et);
    }
}
