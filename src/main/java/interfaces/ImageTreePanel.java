package interfaces;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import funtions.Utils;

public class ImageTreePanel extends JSplitPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTree arbol;
	private String dir;

	public ImageTreePanel(String directory) {

		this.dir = directory;
		setDividerSize(1);
		setContinuousLayout(true);

		JPanel p1 = new JPanel();
		JPanel p = new JPanel();

		p1.add(new TabPanel(directory));// se le añade el tabpanel
		createTree();
		p.add(arbol);// arbol

		p1.setAutoscrolls(true);
		p.setAutoscrolls(true);
		JScrollPane s = new JScrollPane(p);
		JScrollPane s2 = new JScrollPane(p1);

		this.setRightComponent(s2);
		this.setLeftComponent(s);
		this.setOrientation(SwingConstants.VERTICAL);
		this.setVisible(true);

	}

	// CREAR EL ARBOL DE DIRECTORIOS

	/*
	 * Funcion que crea el arbol del directorio seleccionado
	 */
	private void createTree() {

		File folder = new File(dir);

		// creamos las carpeta raiz del directorio
		DefaultMutableTreeNode rootCarpet = new DefaultMutableTreeNode(dir);
		// System.out.println("la carpeta raiz va a ser " + dir);

		// Definimos el modelo donde se agregaran los nodos
		DefaultTreeModel modelo = new DefaultTreeModel(rootCarpet);

		// agregamos el modelo al arbol, donde previamente establecimos la raiz
		arbol = new JTree(modelo);

		// definimos los eventos
		// arbol.getSelectionModel().addTreeSelectionListener(this);

		// creamos el resto de nodos del arbol
		addChildTree(rootCarpet, folder, modelo);

	}

	/*
	 * Funcion que aniade los nodos al arbol
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
