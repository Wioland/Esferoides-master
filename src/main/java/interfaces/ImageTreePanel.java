package interfaces;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

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
	private Thread t;

	public ImageTreePanel(String directory, boolean selectAlgo) {

		this.dir = directory;
		this.selectAlgo = selectAlgo;
		setDividerSize(1);
		setContinuousLayout(true);

		// WE create the tree of the directory
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

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	// METHODS

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
						if (!path.equals(dir)) { // if it isn't the current
													// directory
							if (selectAlgo) { // if the directory isn't one of
												// detecting esferoid
								int r = JOptionPane.showConfirmDialog(this,
										"The current images will be deleted if you change the current directory. Do you want to change the directory?",
										"WARNING", JOptionPane.YES_NO_OPTION);
								if (r == 0) { // we delete the temporal folder
												// with the files created with
												// the different
												// algorithms

									if (dir.endsWith(File.separator)) {
										FileFuntions.deleteFolder(new File(dir + "temporal"));
									} else {
										FileFuntions.deleteFolder(new File(dir + File.separator + "temporal"));
									}
									// we change the directory
									FileFuntions.changeDirectory(path, true);
								} else {
									JOptionPane.showMessageDialog(this,
											"No changing the directory. Nothing to be done");
								}
							} else {

								FileFuntions.changeDirectory(path, true);
							}

						}

					}

				}
			}

		}
	}

	/**
	 * Repaints the content of the directory (the tab)
	 * 
	 * @param selectAlgo true if you are detecting esferoids
	 */
	public void repaintTabPanel(boolean selectAlgo) {
		repainTabNoTimers(selectAlgo);
		Utils.mainFrame.returnTheTimers(folderView);
	}
	
	public void repainTabNoTimers(boolean selectAlgo) {
		OurProgressBar pb = new OurProgressBar(Utils.mainFrame);
		this.selectAlgo = selectAlgo;

		t = new Thread() {
			@Override
			public void run() {

				folderView = new TabPanel(dir, selectAlgo);
				setRightComponent(folderView);
				folderView.repaint();
				Utils.mainFrame.setDir(dir);

			

				pb.dispose();
				t.interrupt();
			}
		};
		t.start();

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
		List<String> listExtensions = FileFuntions.getExtensions();
		String extension;
		DefaultMutableTreeNode child;

		for (File f : parent.listFiles()) {

			extension = FileFuntions.extensionwithoutName(f.getAbsolutePath());

			if (f.isFile() && !f.getName().endsWith("xls") && listExtensions.contains(extension.toLowerCase())) {
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
