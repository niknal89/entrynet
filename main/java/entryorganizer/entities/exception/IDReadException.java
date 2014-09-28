/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.entities.exception;

import java.io.IOException;
import entryorganizer.entities.ID;

/**
 *
 * @author teopetuk89
 */
public class IDReadException extends IOException {

    private Exception cause;
    private ID id;

    public IDReadException(Exception ex, ID id) {
       this.cause = ex;
       this.id = id;
    }

    @Override
    public void printStackTrace() {
        if (cause != null)
            cause.printStackTrace();
    }

    @Override
    public String getMessage() {
        String msg = getIDDescription();
        return msg + super.getMessage();
    }

    private String getIDDescription() {
        if (id != null) {
            return "ID " + id.getId() + ", offset " + id.getOffset() + ", size " +
                id.getSize() + ": ";
        } else {
            return "ID is null: ";
        }
    }
}
