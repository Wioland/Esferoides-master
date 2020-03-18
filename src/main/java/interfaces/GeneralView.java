
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
import funtions.Utils;

public class GeneralView extends JFrame {

	private static final long serialVersionUID = 1L;
	// private String directory;
	private JMenuBar mb;
	private String dir;
	private ImageTreePanel imageTree;

	public GeneralView() {
		this.mb = new JMenuBar();
		// Window parameters
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(MAXIMIZED_BOTH);
		setVisible(true);
		setTitle("Detect esferoid program");
		setMinimumSize(new Dimension(1000, 700));
		toFront();
	}



	// GETTERS AND SETTERS
	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	
	
	public ImageTreePanel getImageTree() {
		return imageTree;
	}



	public void setImageTree(ImageTreePanel imageTree) {
		this.imageTree = imageTree;
	}



	// METHODS
	/**
	 * Paints the graphics of the main FRame
	 * 
	 * @param dc
	 *            path of the current directory
	 */
	public void paintMainFRame(String dc) {

		if (dc != null) {
			boolean selectAlgo = false;
			selectAlgo = Utils.optionAction();
			this.dir = dc;
			createContent(dc, selectAlgo);
			this.repaint();
		}

	}

	/**
	 * Creates the graphics content of the frame
	 * 
	 * @param directory
	 *            current directory
	 * @param selectAlgo
	 *            if you are detecting esferoid
	 */
	private void createContent(String directory, boolean selectAlgo) {
		OurProgressBar pb = new OurProgressBar(this);

		if (this.getContentPane().getComponentZOrder(imageTree) != -1) {
			this.getContentPane().remove(imageTree);
		}
		imageTree = new ImageTreePanel(directory, selectAlgo);

		getContentPane().add(imageTree);

		setVisible(true);
		pb.setVisible(false);
		pb.dispose();

		if (imageTree.getFolderView().isOriginalIma()) {
			int op = JOptionPane.showConfirmDialog((Component) null,
					"There aren´t Tiff files in this folder, but we detected files with the required extension. Do you want to detect the esferoid of this images?",
					"alert", JOptionPane.YES_NO_OPTION);
			if (op == 0) {

				imageTree.repaintTabPanel(!selectAlgo);
			}
		}
	}
	
	
	/**
	 * Generate the context of the main Jframe
	 * 
	 * @param directory	path of the directory selected by the user
	 * @param selectAlgo	if you are detecting esferoid
	 */
	public void createRestOfConttext(String directory, boolean selectAlgo) {

		this.dir = directory;
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {

				File deleteFile = new File(directory + File.separator + "temporal");
				FileFuntions.deleteFolder(deleteFile);

			}
		});

		setJMenuBar(mb);
		JMenuPropertiesFile menu = new JMenuPropertiesFile();
		mb.add(menu);

		createContent(directory, selectAlgo);

	}
	
	

	
}
