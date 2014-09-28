/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.gui.forge;

import javax.swing.JLabel;
import entryorganizer.entities.EntityType;

/**
 *
 * @author Администратор
 */

public class EntityTypeLabel extends JLabel {

    EntityType entityType;

    public EntityTypeLabel(EntityType entityType) {
        this.entityType = entityType;
        this.setText(entityType.getName());
    }
    
}
