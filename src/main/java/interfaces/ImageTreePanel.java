package interfaces;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import funtions.ShowTiff;
import funtions.Utils;

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

				System.out.println("la ruta es " + path);
				System.out.println("archivo " + fileName);
				System.out.println("extension del archivo " + extension);

				switch (extension) {
				case "tiff":
					showImageTree(path, fileName);
					break;

				case "nd2":

					break;
				case "zip":

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
		}

		return path;

	}

	private void showImageTree(String path, String fileName) {
		List<ImageIcon> imaVer = new ArrayList<ImageIcon>();
		List<String> listImagesName = new ArrayList<String>();
		List<ImageIcon> listIm = new ArrayList<ImageIcon>();

		File folder = new File(path);
		System.out.println("nombre del archivo "+ fileName);
		
		Utils.search(".*\\.tiff", folder, listImagesName);
		Collections.sort(listImagesName);

		for (String name : listImagesName) {
			ImageIcon im = ShowTiff.showTiffToImageIcon(name);
			listIm.add(im);
			System.out.println("name "+name);
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
