/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entryorganizer.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

/**
 *
 * @author teopetuk89
 */
public class Resources {
    
    private String path;
    private HashMap<String, ImageIcon> images = new HashMap<String, ImageIcon>();

    private Color defaultColor = UIManager.getColor("Panel.background");
    private Color selectedColor = Color.WHITE;

    public static final String TAG = "tag.png";
    public static final String ENTRY = "entry.png";
    public static final String SOURCE = "source.png";
    public static final String SEARCH = "search.png";
    public static final String AUTHOR = "author.png";
    public static final String DELETE = "delete.png";
    public static final String UNLINK = "unlink.png";
    public static final String REMOVE = "remove.png";
    public static final String WRAPPER = "wrapper.png";
    public static final String UNFOLD = "unfold.png";
    public static final String FOLD = "fold.png";
    public static final String ADD = "add.png";
    
    public Resources() {
        path = "images" + File.separator;
    /*    if (path.endsWith(File.separator)) {
            this.path = path;
        } else {
            this.path = path + File.separator;
        } */
    }
    
    public void loadImages() {
        Scanner s = null;
        
        URL res = getClass().getResource("images/images.txt");
        try {
            s = new Scanner(
              /*      new BufferedReader(
                        new FileReader("graphics.txt")) */
                        res.openStream()
                    );
            while (s.hasNext()) {
                String st = s.next();
                readText(st);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }
    
    private int expectedArg;
    private String filename;
    private int width;
    private int height;
    private int srcWidth;
    private int srcHeight;
    
    private void readText(String s) {
        switch (expectedArg) {
            case 0: filename = s; break;
            case 1: width = Integer.parseInt(s); break;
            case 2: height = Integer.parseInt(s); break;
            case 3: srcWidth = Integer.parseInt(s); break;
            case 4: srcHeight = Integer.parseInt(s); break;
        }
        expectedArg++;
        if (expectedArg >= 5) {
            expectedArg = 0;
         //   BufferedImage bi = loadImage();
            URL res = getClass().getResource(path + filename);
            ImageIcon ii = new ImageIcon(res);
            images.put(filename, ii);
        }
    }
    
    private BufferedImage loadImage() {
        BufferedImage bi = new BufferedImage
                (width, height, BufferedImage.TYPE_BYTE_INDEXED);
        
        URL res = getClass().getResource(path + filename);
        Graphics2D g2d = bi.createGraphics();
        try {
            g2d.drawImage(ImageIO.read(new File(res.getFile())
              /*  (PATH_GRAPHICS + filename) */                    
                    ),
                0, 0, width, height,
                0, 0, srcWidth, srcHeight,
                null);
        } catch(Exception e) {
                System.err.println("URL error!");
        }
        return bi;
} 
    
    public Map<String, ImageIcon> getImages() {
        return images;
    }
    
    public ImageIcon getImage(String name) {
        return images.get(name);
    }

    public Color getDefaultColor() {
        return defaultColor;
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

}