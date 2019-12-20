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
			if (tp != null) {
				String path = FileFuntions.getPathSelectedTreeFile(tp);
				ij.WindowManager.closeAllWindows();
				// System.out.println("Path de treepath " + tp.toString());

				if (tp.getPath().length > 0) {
					File fileSelected = new File(path);

					if (fileSelected.isFile()) {

						// hacer que se abran en imagej

						String roiPath = RoiFuntions.getRoiPathPredicctions(path);
						RoiFuntions.showOriginalFilePlusRoi(path, roiPath);

					} else {
						if (!path.equals(dir)) { // si no es el directorio en el que nos encontramos que

							File folder = new File(path);
							List<String> resultTif = new ArrayList<String>();
							List<String> resultNd2 = new ArrayList<String>();
							List<String> resultTiff = new ArrayList<String>();
							String oldPath = this.dir;
							String detectedFiles = "";
							boolean switchFolder = true;
							this.dir = path;

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
								changeDirActions(resultTiff, detectedFiles, oldPath, switchFolder);
							} else {
								JOptionPane.showMessageDialog(this,
										"Nothing to be done. Not changing to de selected folder");
								this.dir = oldPath;
								switchFolder = false;

							}

							if (switchFolder) {
								JOptionPane.showMessageDialog(this, "Changed the folder to " + dir);
								FileFuntions.addModificationDirectory(dir);
							}

						}

					}

				}
			}

		}
	}

	public void changeDirActions(List<String> result, String extensionFile, String oldPath, boolean switchFolder) {
		if (result.size() == 0) { // si no tiene imagenes tiff, es decir no se han hecho predicciones

			int n = JOptionPane.showConfirmDialog(this,
					"The folder contains " + extensionFile + " files do you want to use an Algorithm?",
					extensionFile + " files detected", JOptionPane.YES_NO_OPTION);

			if (n == 0) {
				SelectAlgoritm sAl = new SelectAlgoritm(dir, this);
			} else {
				JOptionPane.showMessageDialog(this, "Nothing to be done. Not changing to the selected folder");
				this.dir = oldPath;
				switchFolder = false;
			}

		} else {// Mostrar los tiff

			repaintTabPanel();
		}
	}

	public void repaintTabPanel() {

		folderView = new TabPanel(this.dir);
		// se crean los scrolls
		// s.setViewportView(folderView);
		this.setRightComponent(folderView);
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
			if (f.isFile() && !f.getName().endsWith("xls")) {
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(f.getName());
				modelo.insertNodeInto(child, parentNode, index);
				index++;
			} else {
				if (f.isDirectory() && !f.getName().contentEquals("predictions")) {
					DefaultMutableTreeNode child = new DefaultMutableTreeNode(f.getName());
					modelo.insertNodeInto(child, parentNode, index);
					index++;
					addChildTree(child, f, modelo);
				}
			}

		}

	}

}
