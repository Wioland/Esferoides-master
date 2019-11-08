package interfaces;

import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import funtions.ShowTiff;
import funtions.Utils;
import ij.ImagePlus;
import ij.io.Opener;
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

	public ImageTreePanel(String directory) {

		this.dir = directory;
		setDividerSize(1);
		setContinuousLayout(true);

		// se crea el tree
		JPanel p = new JPanel();
		createTree();
		p.add(tree);// tree
		p.setAutoscrolls(true);

		// se crean los scrolls
		JScrollPane s = new JScrollPane(new TabPanel(directory));// se le añade el tabpanel
		JScrollPane s2 = new JScrollPane(p); // se le aniade el tree

		// propiedades del jsplitpane
		this.setRightComponent(s);
		this.setLeftComponent(s2);
		this.setOrientation(SwingConstants.VERTICAL);
		this.setVisible(true);

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
			String path = getPathSelectedTreeFile(tp);

			System.out.println("Path de treepath " + tp.toString());

			if (tp.getPath().length > 1) {
				String fileName = tp.getPath()[tp.getPathCount() - 1].toString();
				String extension = tp.getPath()[tp.getPathCount() - 1].toString().split("\\.")[1];
				String nameFileOnly = tp.getPath()[tp.getPathCount() - 1].toString().split("\\.")[0];
				Opener op = new Opener();

				System.out.println("la ruta es " + path);
				System.out.println("archivo " + fileName);
				System.out.println("extension del archivo " + extension);

				switch (extension) {
				case "tiff":
					// mostrar por el visualizador
					// showImageTree(path, fileName);

					// mostrar con imagej
					ij.WindowManager.closeAllWindows();
					op.open(path + fileName);

					break;

				// hacer que se abran en imagej
				case "nd2":
					System.out.println(path+fileName);
					
//					try {
//					
//						ImagePlus[] imps;
//						try {
//							imps = BF.openImagePlus(path+fileName);
//							ImagePlus imp = imps[0];
//							System.out.println(imps.length);
//							imp.show();
//						} catch (FormatException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					
//						
//						
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					
//		
					ImagePlus j = op.openUsingBioFormats(path + fileName);
					if (j != null) {
						j.show();

					} else {
						System.out.println("no puedo mostrar la imagen esta es nula ");
					}

					break;

				case "zip":

					ij.WindowManager.closeAllWindows();
					op.open(path + nameFileOnly + "_pred.tiff");

					RoiManager roi = new RoiManager();
					roi.runCommand("Open", path + "\\" + fileName);

//					if() { // si se cierra la imagen que se cierre el zip
//						
//						roi.close();
//					}
//					
					break;

				default:
					break;
				}
			} else {
				if (!path.equals(dir)) { // si no es el directorio en el que nos encontramos que
					System.out.println("se ha clicado en una carpeta"); // cambie a ese

				}
			}

		}

	}

	private String getPathSelectedTreeFile(TreePath tp) {
		String path = "";

		for (int i = 0; i < tp.getPathCount() - 1; i++) {
			path += tp.getPath()[i].toString();
			if (i > 0) {
				path += "\\";

			}
		}

		return path;

	}

	private void showImageTree(String path, String fileName) {
		List<ImageIcon> imaVer = new ArrayList<ImageIcon>();
		List<String> listImagesName = new ArrayList<String>();
		List<ImageIcon> listIm = new ArrayList<ImageIcon>();

		File folder = new File(path);

		Utils.search(".*\\.tiff", folder, listImagesName);
		Collections.sort(listImagesName);

		for (String name : listImagesName) {
			ImageIcon im = ShowTiff.showTiffToImageIcon(name);
			listIm.add(im);

			if (name.contains(fileName)) {
				imaVer.add(im);
			}
		}

		ViewImagesBigger vIb = new ViewImagesBigger(imaVer.get(0), listIm);

	}

	/*
	 * Funcion que aniade los nodos al tree
	 * 
	 */
	private void addChildTree(DefaultMutableTreeNode parentNode, File parent, DefaultTreeModel modelo) {

		int index = 0;

		for (File f : parent.listFiles()) {
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(f.getName());
			modelo.insertNodeInto(child, parentNode, index);
			index++;
			// System.out.println(f.getParent());

			if (f.isDirectory()) {
				addChildTree(child, f, modelo);
			}
		}

	}

}
