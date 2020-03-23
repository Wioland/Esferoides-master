package interfaces;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import funtions.FileFuntions;
import funtions.PropertiesFileFuntions;
import ij.IJ;
import ij.io.DirectoryChooser;

public class JMenuPropertiesFile extends JMenu implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JMenuPropertiesFile() {

		this.setText("Properties");
		this.setName("Properties");

		addMEnuItem("Current Directory", this);
		addMEnuItem("Update", this);

	}
	
	/**
	 * creates a jmenu without menuitems with the name given
	 * 
	 * @param name name of the jmenu
	 */
	public JMenuPropertiesFile(String name) {

		this.setText(name);
		this.setName(name);
	}

	/**
	 * Adds a new menu item into the JMenu
	 * 
	 * @param name	name of the menu(id) and the name to show
	 * @param actionListe	the action to perform
	 */
	public void addMEnuItem(String name, ActionListener actionListe) {
		JMenuItem mi = new JMenuItem(name);
		mi.setName(name);
		mi.addActionListener(actionListe);
		this.add(mi);
	}

	/**
	 *Actions to perform when a menu item of the Jmenu is click
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String menuNAme = ((JMenuItem) e.getSource()).getName();

		// Action to change the directory
		if (menuNAme == "Current Directory") {

			changeDirectory();

		} else {
			if (menuNAme == "Update") {
				FileFuntions.createUpdater(false);
			}
		}
	}

	public void changeDirectory() {
		GeneralView mainFrame = (GeneralView) this.getParent().getParent().getParent().getParent();
		String text = "The current directory is: \n" + mainFrame.getDir() + "\n Do you what to change it?";
		int op = JOptionPane.showConfirmDialog(mainFrame, text);

		if (op == 0) {

			DirectoryChooser dc = new DirectoryChooser("Select new directory");

			// Closed all the windows that aren't the main frame
			Window[] s = Window.getWindows();
			for (Window window : s) {
				if (window.getClass().equals(AlgorithmView.class)) {
					window.dispose();
				}
			}

			// Close the imageJ windows
			if (IJ.isWindows()) {
				IJ.run("Close All");
				if (IJ.isResultsWindow()) {
					IJ.selectWindow("Results");
					IJ.run("Close");
					IJ.selectWindow("ROI Manager");
					IJ.run("Close");
				}

			}

			mainFrame.paintMainFRame(dc.getDirectory());
			JOptionPane.showMessageDialog(mainFrame, "Directory changed to " + dc.getDirectory());

		} else {

			JOptionPane.showMessageDialog(mainFrame, "Directory not changed");

		}

	}

	
	/**
	 *  Action to add more allowed extensions
	 * 
	 */
	public void addFileExtension() {
		PropertiesFileFuntions prop = new PropertiesFileFuntions();
		String text = "The current extensions are: \n";
		String ext = prop.getProp().getProperty("imageFilesExtensions");
		if (ext != null) {
			List<String> list = getExtensions();

			for (String s : list) {
				text += s + " \n";
			}

			String seleccion = JOptionPane.showInputDialog(text);
			if (seleccion != null) {
				if (seleccion != "") {
					ext += "," + seleccion;
					prop.getProp().setProperty("imageFilesExtensions", ext);
					try {
						FileOutputStream out = new FileOutputStream(prop.getPath().getFile());
						prop.getProp().store(out, null);
						out.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						JOptionPane.showMessageDialog(null, "Error while doing the required action", "Error saving",
								JOptionPane.ERROR_MESSAGE);
					}

					addFileExtension();

				}
			}
		}
	}

	/**
	 * GEts the allowed file extension from the properties file
	 * 
	 * @return the list of allowed file extensions
	 */
	public static List<String> getExtensions() {
		List<String> list = null;
		PropertiesFileFuntions prop = new PropertiesFileFuntions();
		String ext = prop.getProp().getProperty("imageFilesExtensions");
		if (ext != null) {
			String[] extensionSplit = ext.split(",");
			list = Arrays.asList(extensionSplit);
		}
		return list;
	}
}
