package funtions;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ShowTiff {

	public static ImageIcon showTiffToImageIcon(String path) {
		FileInputStream in;
		
		
		BufferedImage image = null;
        try
        {
        	File f= new File(path);
             image = ImageIO.read(f);
             String[] formatNames = ImageIO.getReaderFormatNames();
             for (String s: formatNames)
                System.out.println(s);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        ImageIcon imaIco = new ImageIcon(image);
		
		return imaIco;
		
	}

	



}
