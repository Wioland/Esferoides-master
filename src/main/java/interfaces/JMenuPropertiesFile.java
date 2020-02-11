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

import funtions.PropertiesFileFuntions;
import ij.IJ;
import ij.io.DirectoryChooser;

public class JMenuPropertiesFile extends JMenu implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JMenuItem mi1, mi2;

	public JMenuPropertiesFile() {

		this.setText("Properties");

		mi1 = new JMenuItem("Current Directory");
		mi1.setName("Current Directory");
		mi1.addActionListener(this);
		this.add(mi1);

		mi2 = new JMenuItem("Extensions");
		mi2.setName("extensions");
		mi2.addActionListener(this);
		this.add(mi2);

	}

	/**
	 * Adds the Action to perform if you try to change the directory in the
	 * JMenuPanel And the action to add more allowed file extensions
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String menuNAme = ((JMenuItem) e.getSource()).getName();
		PropertiesFileFuntions prop = new PropertiesFileFuntions();

		GeneralView mainFrame = (GeneralView) this.getParent().getParent().getParent().getParent();

		// Action to change the directory
		if (menuNAme == "Current Directory") {

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

//Action to add more allowed extensions
		} else {
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

						actionPerformed(e);

					}
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
