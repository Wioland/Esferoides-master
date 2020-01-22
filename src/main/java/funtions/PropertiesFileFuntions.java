package funtions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.swing.JOptionPane;

import ij.io.DirectoryChooser;

public class PropertiesFileFuntions {

	private Properties prop;
	private URL path = getClass().getClassLoader().getResource("program.properties");

	public PropertiesFileFuntions() {
		prop = getPropertyDirectory(this.path);
	}

	public Properties getProp() {
		return prop;
	}

	public void setProp(Properties prop) {
		this.prop = prop;
	}

	public URL getPath() {
		return path;
	}

	public void setPath(URL path) {
		this.path = path;
	}

	public void cheeckJarDirectoryChange() {

		String text = "There is no jar directory assigned to the program or the one assigned no longer exist. Do you want to add one now?";

		if (prop != null) {
			if (prop.getProperty("jarDirectory") == "") {
				changeJarDirectory("", text);
			} else {
				File directory = new File(prop.getProperty("jarDirectory"));
				if (!directory.exists()) {
					changeJarDirectory(prop.getProperty("jarDirectory"), text);
				}
			}
		} else {

			JOptionPane.showMessageDialog(null, "properties file not found");
		}

	}

	public void changeJarDirectory(String dirname, String text) {
		FileOutputStream out;
		try {
			Path resourceDirectory = Paths.get("src", "main", "resources");
			String dir = resourceDirectory.toString();
			out = new FileOutputStream(path.getFile());
			prop.setProperty("jarDirectory", dir);
			prop.store(out, null);
			out.close();
			//JOptionPane.showMessageDialog(null, "directory jar changed");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Properties getPropertyDirectory(URL path) {

		prop = new Properties();
		InputStream is = null;

		try {
			is = path.openStream();
			prop.load(is);
			is.close();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		return prop;

	}
}
