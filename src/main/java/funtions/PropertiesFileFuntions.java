package funtions;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.swing.JOptionPane;

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

//	/**
//	 * Checks if there is a jar directory in the resource file or if it still
//	 * exist In case not having directory or not existing it ask you to change
//	 * the directory
//	 */
//	public void cheeckJarDirectoryChange() {
//
//		String text = "There is no jar directory assigned to the program or the one assigned no longer exist. Do you want to add one now?";
//
//		if (prop != null) {
//			if (prop.getProperty("jarDirectory") == "") {
//				changeJarDirectory("", text);
//			} else {
//				File directory = new File(prop.getProperty("jarDirectory"));
//				if (!directory.exists()) {
//					changeJarDirectory(prop.getProperty("jarDirectory"), text);
//				}
//			}
//		} else {
//
//			JOptionPane.showMessageDialog( Utils.mainFrame, "properties file not found");
//		}
//
//	}
//
//	/**
//	 * Changes the current jar directory in the resource file for the giving one
//	 * 
//	 * @param dirname
//	 *            path of the new directory
//	 * @param text
//	 *            Text of the message shown
//	 */
//	public void changeJarDirectory(String dirname, String text) {
//		FileOutputStream out;
//		try {
//
//			Path resourceDirectory = Paths.get("src", "main", "resources");
//			String dir = resourceDirectory.toString();
//			out = new FileOutputStream(path.getFile());
//
//			prop.setProperty("jarDirectory", dir);
//			prop.store(out, null);
//			out.close();
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			JOptionPane.showMessageDialog( Utils.mainFrame, "Error changing the Jar directory", "Error saving",
//					JOptionPane.ERROR_MESSAGE);
//		}
//
//	}

	/**
	 * Initialize Properties with the resource file given in the URL
	 * 
	 * @param path
	 *            the path of the resource file
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
			JOptionPane.showMessageDialog( Utils.mainFrame, "Error getting the properties file. \n  Please check if you have"
					+ "the jar file with the update folder and its files", "Error saving",
					JOptionPane.ERROR_MESSAGE);

		}
		return prop;

	}

}
