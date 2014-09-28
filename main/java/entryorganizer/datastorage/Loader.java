/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entryorganizer.datastorage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author teopetuk89
 */
public class Loader {
    
    public String loadFile(String fileName) throws IOException {         
        String content;
        URL res = getClass().getResource(fileName);           
        byte[] encoded = null; 
        try {
            encoded = Files.readAllBytes(Paths.get(res.toURI()));
        } catch (URISyntaxException ex) {
            Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, null, ex);
        }
        content = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(encoded)).toString();        
        return content;
    }
}
