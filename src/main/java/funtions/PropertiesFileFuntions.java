package funtions;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.swing.JOptionPane;

/**
 * Functions for working with the .properties file
 * 
 * @author Yolanda
 *
 */
public class PropertiesFileFuntions {

	// Properties of the properties file
	private Properties prop;
	// Program.properties path
	private URL path = getClass().getClassLoader().getResource("program.properties");

	public PropertiesFileFuntions() {
		prop = getPropertyDirectory(this.path);
	}

	public PropertiesFileFuntions(URL propertiesFile) {
		prop = getPropertyDirectory(propertiesFile);
	}

	// GETTERS Y SETTERS

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

	// METHODS

	/**
	 * Initialize Properties with the resource file given in the URL
	 * 
	 * @param path the path of the resource file
	 * @return properties initialized
	 */
	public Properties getPropertyDirectory(URL path) {

		prop = new Properties();
		InputStream is = null;

		try {
			is = path.openStream();
			prop.load(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(Utils.mainFrame,
					"Error getting the properties file. \n  Please check if you have "
							+ "the jar file with the update folder and its files",
					"Error saving", JOptionPane.ERROR_MESSAGE);

		}
		return prop;

	}

}
