package interfaces;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
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
import funtions.ShowTiff;
import funtions.Utils;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.Toolbar;
import ij.plugin.frame.RoiManager;
import loci.formats.FormatException;
import loci.plugins.BF;
import loci.plugins.in.ImporterOptions;

public class ImageTreePanel extends JSplitPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTree tree;
	private String dir;
	private TabPanel folderView;

	public ImageTreePanel(String directory) {

		this.dir = directory;
		setDividerSize(1);
		setContinuousLayout(true);

		// se crea el tree
		JPanel p = new JPanel();
		createTree();
		p.add(tree);// tree
		p.setAutoscrolls(true);

		folderView = new TabPanel(directory);
		// se crean los scrolls

		JScrollPane s2 = new JScrollPane(p); // se le aniade el tree

		// propiedades del jsplitpane
		this.setRightComponent(folderView);
		this.setLeftComponent(s2);
		this.setOrientation(SwingConstants.VERTICAL);
		this.setVisible(true);

	}

	public TabPanel getFolderView() {
		return folderView;
	}

	public void setFolderView(TabPanel folderView) {
		this.folderView = folderView;
	}

	// CREAR EL tree DE DIRECTORIOS

	/*
	 * Funcion que crea el tree del directorio seleccionado
	 */
	private void createTree() {

		File folder = new File(dir);

		// creamos las carpeta raiz del directorio
		DefaultMutableTreeNode rootCarpet = new DefaultMutableTreeNode(dir);
		// System.out.println("la carpeta raiz va a ser " + dir);

		// Definimos el modelo donde se agregaran los nodos
		DefaultTreeModel modelo = new DefaultTreeModel(rootCarpet);

		// agregamos el modelo al tree, donde previamente establecimos la raiz
		tree = new JTree(modelo);

		// definimos los eventos
		// tree.getSelectionModel().addTreeSelectionListener(this);

		// creamos el resto de nodos del tree
		addChildTree(rootCarpet, folder, modelo);

		// aniadimos el evento a realizar cuando se haga doble click en uno de los
		// componentes de l arbol
		tree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				doubleClickAction(me);
			}
		});
	}

	private void doubleClickAction(MouseEvent me) {
		if (me.getClickCount() == 2 && !me.isConsumed()) {
			me.consume();
			TreePath tp = tree.getPathForLocation(me.getX(), me.getY());
			String path = FileFuntions.getPathSelectedTreeFile(tp);
			ij.WindowManager.closeAllWindows();
			// System.out.println("Path de treepath " + tp.toString());

			if (tp.getPath().length > 0) {
				File fileSelected = new File(path);

				if (fileSelected.isFile()) {

					String fileName = fileSelected.getName();
					String extension = fileName.split("\\.")[1];
					// String nameFileOnly = fileName.toString().split("\\.")[0];

					if (extension.equals("nd2")) {
						// hacer que se abran en imagej
						// System.out.println(path);
						String roiPath = RoiFuntions.getRoiPathPredicctions(path);
						RoiFuntions.showNd2FilePlusRoi(path, roiPath);

					}
				} else {
					if (!path.equals(dir)) { // si no es el directorio en el que nos encontramos que

						File folder = new File(path);
						List<String> result = new ArrayList<String>();
						String oldPath = this.dir;
						boolean switchFolder = true;
						this.dir = path;

						Utils.search(".*\\.tiff", folder, result);
						if (result.size() == 0) {
							Utils.search(".*\\.nd2", folder, result);
							if (result.size() != 0) { // si solo tiene imagenes nd2 mostrar el selector de algoritmos

								int n = JOptionPane.showConfirmDialog(this,
										"The folder only contains ND2 files do you want to use an Algorithm?",
										"ND2 files detected", JOptionPane.YES_NO_OPTION);

								if (n == 0) {
									SelectAlgoritm sAl = new SelectAlgoritm(dir, this);
								} else {
									JOptionPane.showMessageDialog(this,
											"Nothing to be done. Not changing to de selected folder");
									this.dir = oldPath;
									switchFolder = false;
								}

							} else {// mostrar en la vista que no hay datos que mostrar quitar el tab panel

								repaintTabPanel();
							}

						} else { // si tiene imagenes tiff decirle que quiere realizar
							JOptionPane.showMessageDialog(this, "Detected Tiff files");
							Main.callProgram(dir, this);

						}

						if (switchFolder) {
							JOptionPane.showMessageDialog(this, "Changed the folder to " + dir);

						}

					}

				}

			}
		}
	}

	public void repaintTabPanel() {
		folderView = new TabPanel(this.dir);
		// se crean los scrolls
		// s.setViewportView(folderView);
		folderView.revalidate();
		folderView.repaint();

	}

//	private void showImageTree(String path, String fileName) {
//		List<ImageIcon> imaVer = new ArrayList<ImageIcon>();
//		List<String> listImagesName = new ArrayList<String>();
//		List<ImageIcon> listIm = new ArrayList<ImageIcon>();
//
//		File folder = new File(path);
//
//		Utils.search(".*\\.tiff", folder, listImagesName);
//		Collections.sort(listImagesName);
//
//		for (String name : listImagesName) {
//			ImageIcon im = ShowTiff.showTiffToImageIcon(name);
//			listIm.add(im);
//
//			if (name.contains(fileName)) {
//				imaVer.add(im);
//			}
//		}
//
//		// ViewImagesBigger vIb = new ViewImagesBigger(imaVer.get(0), listIm,dir);
//
//	}

	/*
	 * Funcion que aniade los nodos al tree
	 * 
	 */
	private void addChildTree(DefaultMutableTreeNode parentNode, File parent, DefaultTreeModel modelo) {

		int index = 0;

		for (File f : parent.listFiles()) {
			if (f.isFile() && f.getName().endsWith("nd2")) {
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(f.getName());
				modelo.insertNodeInto(child, parentNode, index);
				index++;
			} else {
				if (f.isDirectory()) {
					DefaultMutableTreeNode child = new DefaultMutableTreeNode(f.getName());
					modelo.insertNodeInto(child, parentNode, index);
					index++;
					addChildTree(child, f, modelo);
				}
			}

		}

	}

}
