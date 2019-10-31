package interfaces;

import java.awt.Image;
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

import esferoides.Utils;
import ij.io.DirectoryChooser;

public class ShowResults extends JFrame implements TreeSelectionListener {

	private JTree arbol;
	private JFrame frame;
	private String dir;

	// luego poner como parametro un directorio, que es el que la persona selecciona
	// para realizar los calculos
	// tambien dejar uno con la seleccion del directorio por si lo que se quiere es
	// visualizar unos datos que ya se habia creado anteriormente
	public void showResults() {

		DirectoryChooser dc = new DirectoryChooser("Select the folder containing the nd2 images");
		dir = dc.getDirectory();

		// creamos el marco que muestra los resultados
		frame = new JFrame("Results");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 300);

		// tabla de pestañas dentro del contenedor
		JTabbedPane tP = new JTabbedPane();

		// si se le quiere poner a las pestañas un icono
		// ImageIcon icon = createImageIcon(" ");

		// creamos el arbol de directorios
		createTree();

		// creamos la lista de imagenes a mostrar
		createPanelImage();

		// crear el cotenido de la pestania de mostrar contenido de la carpeta-arbol
		// directorios

		createSplitFrame(tP);

		// add panel
		frame.add(tP);

		frame.show();

	}

	/*
	 * Crea el panel donde se muestran las imagenes de resultado
	 */
	private void createPanelImage() {
		List<String> result = new ArrayList<String>();
		List<ImageIcon> listImages = new ArrayList<ImageIcon>();
		List<ImageIcon> listImagesPrev = new ArrayList<ImageIcon>();

		File folder = new File(dir);
		Utils.search(".*\\.tiff", folder, result);
		Collections.sort(result);

		for (String name : result) {
			ImageIcon image = new ImageIcon(name);
			listImages.add(image);
		}
		listImagesPrev = getPreview(listImages);
		
		for (ImageIcon imageprev : listImagesPrev) {
			imageprev.addActionListener(this);
		}
		
		
	}

	/* devuelve una imagen de tamaño 100x100 VISTA PREVIA */
	public List<ImageIcon> getPreview(List<ImageIcon> listImages) {
		List<ImageIcon> listImagesPrev = new ArrayList<ImageIcon>();

		for (ImageIcon image : listImages) {
			Image prev = image.getImage().getScaledInstance(100, 100, Image.SCALE_AREA_AVERAGING);
			listImagesPrev.add(new ImageIcon(prev));
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

		p1.add(); // lista de imagenes
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
	
	
	private void PhotoMouseClicked(MouseEvent evt) {
        
    }

	@Override
	public void valueChanged(TreeSelectionEvent e) {

	}

}
