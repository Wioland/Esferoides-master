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

import com.sleepycat.persist.model.DeleteAction;

import funtions.Main;
import funtions.ShowTiff;
import funtions.Utils;
import ij.ImagePlus;
import loci.formats.FormatException;
import loci.plugins.BF;

public class ImageTreePanel extends JSplitPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTree tree;
	private String dir;
	private TabPanel folderView;
	private JScrollPane s;

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
		s = new JScrollPane(folderView);// se le añade el tabpanel
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

			if (tp.getPath().length > 0) {
				File fileSelected = new File(path);

				if (fileSelected.isFile()) {

					String fileName = tp.getPath()[tp.getPathCount() - 1].toString();
					String extension = tp.getPath()[tp.getPathCount() - 1].toString().split("\\.")[1];
					String nameFileOnly = tp.getPath()[tp.getPathCount() - 1].toString().split("\\.")[0];
					// Opener op = new Opener();

//					System.out.println("la ruta es " + path);
//					System.out.println("archivo " + fileName);
//					System.out.println("extension del archivo " + extension);

					if (extension.equals("nd2")) {
						// hacer que se abran en imagej
						System.out.println(path);

						ImagePlus[] imps;
						try {
//							ImporterOptions options =new ImporterOptions();
//							options.setWindowless(true);
//							options.setId(path);
//							options.setOpenAllSeries(true);
//							imps = BF.openImagePlus(options);
							imps = BF.openImagePlus(path);
							ImagePlus imp = imps[0];
							imp.show();
						} catch (FormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

//			
//						ImagePlus j = op.openUsingBioFormats(path + fileName);
//						if (j != null) {
//							j.show();
//
//						} else {
//							System.out.println("no puedo mostrar la imagen esta es nula ");
//						}

//						IJ.run("Bio-Formats Importer",
//								"open=C:/Users/yomendez/Desktop/Esferoides/2x/ctrl_1_14.nd2 autoscale color_mode=Default rois_import=[ROI manager] view=Hyperstack stack_order=XYCZT");

						ij.WindowManager.closeAllWindows();
						// op.open(path + nameFileOnly + "_pred.tiff");
						// IJ.run("Bio-Formats Windowless Importer",
						// "open=C:/Users/yomendez/Desktop/Esferoides/2x/ctrl_1_14.nd2");

//						IJ.setTool("freehand");
//						RoiManager roi = new RoiManager();
//						// roi.runCommand("Open", path + "\\" + fileName);
//						ImagePlus imp = new ImagePlus(path + "\\" + fileName);
//						roi.runCommand(imp, "Measure");
//						// roi.runCommand("Measure");

						// roi.actionPerformed(new ActionEvent(roi, 7, "Measure"));
						// ResultsTable rt = ij.plugin.filter.Analyzer.getResultsTable();
						// rt.show("Measures");

						// ij.measure.ResultsTable();
						// ResultsTable roiResults = new ResultsTable();
//						if() { // si se cierra la imagen que se cierre el zip
//							
//							roi.close();
//						}

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
		s.setViewportView(folderView);
		s.revalidate();
		s.repaint();

	}

	private String getPathSelectedTreeFile(TreePath tp) {
		String path = "";

		for (int i = 0; i < tp.getPathCount(); i++) {
			path += tp.getPath()[i].toString();
			if (i > 0 && i != (tp.getPathCount() - 1)) {

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

		// ViewImagesBigger vIb = new ViewImagesBigger(imaVer.get(0), listIm,dir);

	}

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
