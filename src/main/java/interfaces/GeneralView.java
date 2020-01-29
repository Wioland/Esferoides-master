
package interfaces;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import funtions.FileFuntions;

public class GeneralView extends JFrame {

	private static final long serialVersionUID = 1L;
	//private String directory;
	private JMenuBar mb;

	public GeneralView(String directory,boolean selectAlgo) {

		//this.directory = directory;
		this.mb = new JMenuBar();

		// Parametros ventana
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(MAXIMIZED_BOTH);
		setVisible(true);
		setTitle("Main Frame");
		setMinimumSize(new Dimension(1000, 700));
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				
				File deleteFile = new File(directory + File.separator + "temporal");
				FileFuntions.deleteTemporalFolder(deleteFile);

			}
		});

		setJMenuBar(mb);
		JMenuPropertiesFile menu = new JMenuPropertiesFile();
		mb.add(menu);

		OurProgressBar pb = new OurProgressBar(this);
		ImageTreePanel imageTree = new ImageTreePanel(directory,selectAlgo);
		getContentPane().add(imageTree);

		setVisible(true);
		pb.setVisible(false);
		pb.dispose();

		if (imageTree.getFolderView().isOriginalIma()) {
			int op = JOptionPane.showConfirmDialog((Component) null,
					"There aren´t Tiff files in this folder, but we detected files with the required extension. Do you want to detect the esferoid of this images?",
					"alert", JOptionPane.YES_NO_OPTION);
			if (op == 0) {
				//Hacer que no cree otro JPanel Main, sino que haga un update del tabpanel k ya tenemos
				//new GeneralView(directory, true);
				
				imageTree.repaintTabPanel(!selectAlgo);
			}
		}
	}

}
