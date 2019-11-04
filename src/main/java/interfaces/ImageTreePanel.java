package interfaces;

import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import esferoides.Utils;
import ij.io.DirectoryChooser;

public class ImageTreePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTree arbol;
	private String dir;
	private List<ImageIcon> listImages;
	private List<JButton> listImagesPrev;

	public ImageTreePanel(String directory) {
		this.dir = directory;

	}

	/*
	 * Crea el panel donde se muestran las imagenes de resultado
	 */
	private void createPanelImage(JPanel cont) {
		List<String> result = new ArrayList<String>();
		listImages = new ArrayList<ImageIcon>();
		listImagesPrev = new ArrayList<JButton>();
		
		

		File folder = new File(dir);
		Utils.search(".*\\.tiff", folder, result);
		Collections.sort(result);

		for (String name : result) {
			ImageIcon image = new ImageIcon(name);
			listImages.add(image);
		}
		
		listImagesPrev = getPreview(listImages,cont);
		

	}

	
	
	
	
	/* devuelve una imagen de tamaño 100x100 VISTA PREVIA */
	public List<JButton> getPreview(List<ImageIcon> listImages, JPanel cont) {
		
		List<JButton> listImagesPrev = new ArrayList<JButton>();
		

		for (ImageIcon image : listImages) {
			JButton button = new JButton(image);
			button.setSize(100, 1000);
			
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { // si se genera el click que muestre un visualizador de imagenes
					ViewImagesBigger viewImageBig= new ViewImagesBigger();							
				}
			});

			listImagesPrev.add(button);
			
			cont.add(button);
		}
		return listImagesPrev;

	}

	
	
	
	
	
	
	/*
	 * Funcion que crea la region dividida dentro de la pestaña de visualizacion de
	 * las imagenes
	 */

	private void createSplitFrame(JTabbedPane tP) {
		// create a panel
		JPanel p1 = new JPanel();
		JPanel p = new JPanel();

		p1.setLayout(new GridLayout(0, 0));
		
		
		createPanelImage(p1);// lista de imagenes
		p.add(arbol);// arbol

		JSplitPane panel1 = new JSplitPane(SwingConstants.VERTICAL, p1, p);

		panel1.setOrientation(SwingConstants.VERTICAL);

		tP.addTab("Images", panel1);
	}
	
	

	/*
	 * Funcion que crea el arbol del directorio seleccionado
	 */
	private void createTree() {

		File folder = new File(dir);

		// creamos las carpeta raiz del directorio
		DefaultMutableTreeNode rootCarpet = new DefaultMutableTreeNode(dir);
		System.out.println("la carpeta raiz va a ser " + dir);

		// Definimos el modelo donde se agregaran los nodos
		DefaultTreeModel modelo = new DefaultTreeModel(rootCarpet);

		// agregamos el modelo al arbol, donde previamente establecimos la raiz
		arbol = new JTree(modelo);
		// definimos los eventos
		arbol.getSelectionModel().addTreeSelectionListener(this);

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
			System.out.println(f.getParent());

			if (f.isDirectory()) {
				addChildTree(child, f, modelo);
			}
		}

	}

	

}
