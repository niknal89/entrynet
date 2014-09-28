/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.recover;

import entryorganizer.data.Entity;

/**
 *
 * @author teopetuk89
 */
public class OldLogger {


    private static boolean info = true;
    private static boolean idMismatch = false;
    private static boolean error = true;

    public static void info(String s) {
        if (info) {
            System.out.println(s);
        }
    }

    public static void idMismatch(Entity e, int id, Class requiredClass) {
        if (idMismatch) {
            StringBuilder sb = new StringBuilder();
            sb.append("ID ");
            sb.append(id); 
            sb.append(" didnt return ");
            sb.append(requiredClass.getSimpleName());
            sb.append("; instead, returned ");
            if (e == null) {
                sb.append("null");
            } else {
                sb.append(e.toString());
            }           
        }
    }

    public static void error(String s) {
        if (error) {
            System.err.println(s);
        }
    }

}
