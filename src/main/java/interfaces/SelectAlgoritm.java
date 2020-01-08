package interfaces;

import java.io.File;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import esferoides.EsferoideJ_;
import funtions.FileFuntions;
import funtions.PropertiesFileFuntions;
import ij.IJ;

public class SelectAlgoritm {

	private String dir;

	public SelectAlgoritm(String directory, ImageTreePanel folderView) {

		// EsferoideJ_ esfe= new EsferoideJ_();

		this.dir = directory;
		
		List<String> jarNames=FileFuntions.getPluginNames();
		
		JComboBox jcb = new JComboBox(jarNames.toArray());
		jcb.setEditable(true);
		JOptionPane.showMessageDialog(null, jcb, "select and algorith ", JOptionPane.QUESTION_MESSAGE);
		
		
		System.out.println(jcb.getSelectedItem());
		IJ.run(jcb.getSelectedItem().toString());
		
		
//		PropertiesFileFuntions properties = new PropertiesFileFuntions();
//		String jarDirectoryPath = properties.getProp().getProperty("jarDirectory");
//		File jardirectory = new File(jarDirectoryPath);
//		if (jardirectory.exists()) {
//			File[] listJar = jardirectory.listFiles();
//			String[] jarNames = new String[listJar.length];
//			int i = 0;
//			for (File file : listJar) {
//				if(file.getName().endsWith(".jar")) {
//					jarNames[i] = file.getName().replace(".jar", "");
//					i++;
//				}
//				
//			}
//			JComboBox jcb = new JComboBox(jarNames);
//			jcb.setEditable(true);
//			JOptionPane.showMessageDialog(null, jcb, "select and algorith ", JOptionPane.QUESTION_MESSAGE);
//			
//			
//			System.out.println(jcb.getSelectedItem());
			
			//IJ.doCommand("EsferoideJ_");
			//IJ.runPlugIn("EsferoideJ", "");
//		}

		// Methods.getAlgorithms();
		// IJ.run( "EsferoideJ");
		// IJ.run("EsferoideJ_.class");

//		Class<?> clazz = EsferoideJ_.class;
//		IJ.runPlugIn(clazz.getName(), "");

//		dir = esfe.getDir();
//		GeneralView ventana = new GeneralView(dir);

	}

}
