package interfaces;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import funtions.FileFuntions;
import funtions.Main;
import funtions.RoiFuntions;
import funtions.Utils;

public class ImageTreePanel extends JSplitPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTree tree;
	private String dir;
	private TabPanel folderView;
	private boolean selectAlgo;

	public ImageTreePanel(String directory, boolean selectAlgo) {

		this.dir = directory;
		this.selectAlgo = selectAlgo;
		setDividerSize(1);
		setContinuousLayout(true);

		//WE create the tree of the directory
		JPanel p = new JPanel();
		createTree();
		p.add(tree);// tree
		p.setAutoscrolls(true);

		folderView = new TabPanel(directory, selectAlgo);

		JScrollPane s2 = new JScrollPane(p); // Add the tree

		// Properties of the jsplitpane
		this.setRightComponent(folderView);
		this.setLeftComponent(s2);
		this.setOrientation(SwingConstants.VERTICAL);
		this.setVisible(true);

	}

	// GETTERS AND SETTERS
	public TabPanel getFolderView() {
		return folderView;
	}

	public void setFolderView(TabPanel folderView) {
		this.folderView = folderView;
	}

//METHODS

	/**
	 * Function that creates the tree directory from the directory given
	 */
	private void createTree() {

		File folder = new File(dir);

		// we create de root folder of the directory
		DefaultMutableTreeNode rootCarpet = new DefaultMutableTreeNode(dir);

		// We defiene the model in which add the nodes
		DefaultTreeModel modelo = new DefaultTreeModel(rootCarpet);

		// add the model to the tree, previously adding the root
		tree = new JTree(modelo);

		// we create the rest of nodes
		addChildTree(rootCarpet, folder, modelo);

		// we add the event to perform double click in a tree node
		tree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				doubleClickAction(me);
			}
		});
	}

	/**
	 * Action to perform when a click occurred in a tree node. If it is a folder : -
	 * one click shows in the tree it content - Double click tries to change the
	 * current directory If image: -opens it with imageJ
	 * 
	 * @param me mouse event
	 */
	private void doubleClickAction(MouseEvent me) {
		if (me.getClickCount() == 2 && !me.isConsumed()) {
			me.consume();
			TreePath tp = tree.getPathForLocation(me.getX(), me.getY());
			if (tp != null) {
				String path = FileFuntions.getPathSelectedTreeFile(tp);
				ij.WindowManager.closeAllWindows();

				if (tp.getPath().length > 0) {
					File fileSelected = new File(path);

					if (fileSelected.isFile()) {

						// open the imageJ

						String roiPath = RoiFuntions.getRoiPathPredicctions(path);
						RoiFuntions.showOriginalFilePlusRoi(path, roiPath);

					} else {
						if (!path.equals(dir)) { // if it isn't the current directory
							if (selectAlgo) { // if the directory isn't one of detecting esferoid
								int r = JOptionPane.showConfirmDialog(this,
										"The current images will be deleted if you change the current directory. Do you want to change the directory?",
										"WARNING", JOptionPane.YES_NO_OPTION);
								if (r == 0) { // we delete the temporal folder with the files created with the different
												// algorithms

									if (dir.endsWith(File.separator)) {
										FileFuntions.deleteFolder(new File(dir + "temporal"));
									} else {
										FileFuntions.deleteFolder(new File(dir + File.separator + "temporal"));
									}
									// we change the directory
									detectDirContentChange(path);
								} else {
									JOptionPane.showMessageDialog(this,
											"No changing the directory. Nothing to be done");
								}
							} else {

								detectDirContentChange(path);
							}

						}

					}

				}
			}

		}
	}

	/**
	 * Changed the directory to another one
	 * 
	 * @param path path of the directory to change
	 */
	public void detectDirContentChange(String path) {
		File folder = new File(path);
		List<String> resultTif = new ArrayList<String>();
		List<String> resultNd2 = new ArrayList<String>();
		List<String> resultTiff = new ArrayList<String>();
		String oldPath = this.dir;
		String detectedFiles = "";
		boolean switchFolder = true;

		if (path.endsWith(File.separator)) {
			this.dir = path;
		} else {
			this.dir = path + File.separator;
		}

		Utils.search(".*\\.tif", folder, resultTif);
		Utils.search(".*\\.tiff", folder, resultTiff);
		Utils.search(".*\\.nd2", folder, resultNd2);

		if (resultTiff.size() != 0) {
			detectedFiles += "Tiff ";
		}
		if (resultTif.size() != 0) {
			detectedFiles += "Tif ";
		}
		if (resultNd2.size() != 0) {
			detectedFiles += "ND2 ";
		}

		if (resultTif.size() != 0 || resultNd2.size() != 0) {
			JOptionPane.showMessageDialog(this, "Detected image files with the requered extension");
			Main.callProgram(dir, this);
			if (!selectAlgo) {
				switchFolder = changeDirActions(resultTiff, detectedFiles, oldPath, switchFolder);
			} else {
				switchFolder = true;
			}

		} else {
			JOptionPane.showMessageDialog(this, "Nothing to be done. Not changing to de selected folder");
			this.dir = oldPath;
			switchFolder = false;

		}

		if (switchFolder) {
			JOptionPane.showMessageDialog(this, "Changed the folder to " + dir);
			FileFuntions.addModificationDirectory(dir + "predictions");
		}
	}

	/**
	 * If the directory doesn't contain tiff files, but contains original files (nd2
	 * or tiff) asks to detect the esferoid
	 * 
	 * @param result        list with the path of the tiff files
	 * @param extensionFile the extension/s of files the folder contains
	 * @param oldPath       the path of the old directory
	 * @param switchFolder  boolean to know id the switch action was executed
	 * @return true if changed the folder false otherwise
	 */
	public boolean changeDirActions(List<String> result, String extensionFile, String oldPath, boolean switchFolder) {

		if (result.size() == 0) { // if there is no tiff files, there is no predictions done

			int n = JOptionPane.showConfirmDialog(this,
					"The folder contains " + extensionFile + " files do you want to use an Algorithm?",
					extensionFile + " files detected", JOptionPane.YES_NO_OPTION);

			if (n == 0) {
				Main.createGeneralViewOrNot(this, this.dir, true);
			} else {
				JOptionPane.showMessageDialog(this, "Nothing to be done. Not changing to the selected folder");
				this.dir = oldPath;
				switchFolder = false;
			}

		} else {

			repaintTabPanel(this.selectAlgo);
		}
		return switchFolder;
	}

	/**
	 * Gets the main frame that contains the imageTRee
	 * 
	 * @return the main frame
	 */
	public JFrame getJFrameGeneral() {
		return (JFrame) this.getParent().getParent().getParent().getParent();
	}

	/**
	 * Repaints the content of the directory (the tab)
	 * 
	 * @param selectAlgo true if you are detecting esferoids
	 */
	public void repaintTabPanel(boolean selectAlgo) {

		this.selectAlgo = selectAlgo;
		folderView = new TabPanel(this.dir, selectAlgo);
		this.setRightComponent(folderView);
		folderView.repaint();

	}

	/**
	 * Function to add the nodes to the tree Only shows the folders that contains
	 * files with the extensions given except from tiff
	 * 
	 * @param parentNode parent node
	 * @param parent     file in the node parent
	 * @param modelo     tree model
	 */
	private void addChildTree(DefaultMutableTreeNode parentNode, File parent, DefaultTreeModel modelo) {

		int index = 0;
		List<String> listExtensions = JMenuPropertiesFile.getExtensions();
		String extension;
		DefaultMutableTreeNode child;

		for (File f : parent.listFiles()) {

			extension = FileFuntions.extensionwithoutName(f.getAbsolutePath());

			if (f.isFile() && !f.getName().endsWith("xls") && listExtensions.contains(extension)) {
				child = new DefaultMutableTreeNode(f.getName());
				modelo.insertNodeInto(child, parentNode, index);
				index++;
			} else {
				if (f.isDirectory() && !(f.getName().equals("predictions") || f.getName().equals("temporal"))) {
					child = new DefaultMutableTreeNode(f.getName());
					modelo.insertNodeInto(child, parentNode, index);
					index++;
					addChildTree(child, f, modelo);
				}
			}

		}

	}

}
