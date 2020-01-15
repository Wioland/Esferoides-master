package interfaces;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import funtions.PropertiesFileFuntions;
import ij.IJ;

public class JMenuPropertiesFile extends JMenu implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JMenuItem mi1, mi2;

	public JMenuPropertiesFile() {

		this.setText("Properties");

		mi1 = new JMenuItem("Jar directory");
		mi1.setName("Jar directory");
		mi1.addActionListener(this);
		this.add(mi1);

		mi2 = new JMenuItem("extensions");
		mi2.setName("extensions");
		mi2.addActionListener(this);
		this.add(mi2);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String nameJar = ((JMenuItem) e.getSource()).getName();
		PropertiesFileFuntions prop = new PropertiesFileFuntions();

		if (nameJar == "Jar directory") {

			String text = "The current directory is: \n" + prop.getProp().getProperty("jarDirectory")
					+ "\n Do you what to change it?";
			prop.changeJarDirectory(prop.getProp().getProperty("jarDirectory"), text);
			System.setProperty("plugins.dir", prop.getProp().getProperty("jarDirectory"));
			IJ.run("Refresh Menus", "");

		} else {
			String text = "The current extensions are: \n";
			String ext = prop.getProp().getProperty("imageFilesExtensions");
			if (ext != null) {
				List<String> list =getExtensions();

				for (String s : list) {
					text += s + " \n";
				}

				String seleccion = JOptionPane.showInputDialog(text);
				if (seleccion != null) {
					if (seleccion != "") {
						ext+=","+seleccion;
						prop.getProp().setProperty("imageFilesExtensions",ext);
						try {
							FileOutputStream out = new FileOutputStream(prop.getPath().getFile());
							prop.getProp().store(out, null);
							out.close();
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						
						
						actionPerformed(e);

					}
				}
			}

		}
	}
	
	public static List<String> getExtensions(){
		List<String> list=null;
		PropertiesFileFuntions prop = new PropertiesFileFuntions();
		String ext = prop.getProp().getProperty("imageFilesExtensions");
		if (ext != null) {
			String[] extensionSplit = ext.split(",");
			list = Arrays.asList(extensionSplit);
	}
		return list;
	}
}