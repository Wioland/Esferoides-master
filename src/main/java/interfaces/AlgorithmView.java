package interfaces;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

import edu.mines.jtk.sgl.BoundingBox;
import funtions.CreateListImageAlgori;
import funtions.ShowTiff;
import ij.ImagePlus;
import ij.io.Opener;
import ij.plugin.frame.RoiManager;

public class AlgorithmView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<File> images;
	private JButton selectedBu;
	private List<ImageIcon> imageIcoList;
	private String directory;
	private CreateListImageAlgori cLa;
	private List<ViewImagesBigger> openWindows;

	public AlgorithmView(File image, String dir) {
		// Parametros ventana

		setExtendedState(MAXIMIZED_BOTH);
		setTitle("Algorithm view selecter");
		this.setVisible(true);
		setMinimumSize(new Dimension(1000, 300));

		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				File folder = CreateListImageAlgori.getTemporalFolder();
				if (folder != null) {
					folder.delete();
				}

				// Cerrar el resto de ventanas que se hayan abierto a partir de esta
				ij.WindowManager.closeAllWindows(); // esto cierra todas las ventanas abiertas con imagej solamente
				if(openWindows!=null) {         // quedan las de visualizacion de las imagenes en grande 
					if(openWindows.size()>0) {
						for (ViewImagesBigger wind : openWindows) {
							wind.dispose();
						}
					}
				}									

			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}
		});
		
		openWindows= new ArrayList<ViewImagesBigger>();

		OurProgressBar pb = new OurProgressBar(this);

		cLa = new CreateListImageAlgori(image);

		imageIcoList = new ArrayList<ImageIcon>();
		String algoname = "";
		this.directory = dir;

		// crear las imagenes con todos los algoritmos
		images = cLa.createImagesAlgorithms();

		JPanel panelImage = new JPanel(new GridLayout(0, 3));
		JPanel panelButtons = new JPanel(new GridLayout(0, 1));
		panelImage.setAutoscrolls(true);

		int i = 0;

		for (File ima : images) {
			JButton imageView = new JButton();
			JLabel imageAlgori = new JLabel();
			// JPanel butLab = new JPanel(new GridLayout(2, 1));
			JPanel butLab = new JPanel(new GridBagLayout());
			// butLab.getLayout(). // ver alguna mejor para que esten bien los botones

			ImageIcon imagi = ShowTiff.showTiffToImageIcon(ima.getAbsolutePath());

			ImageIcon imageIcon = new ImageIcon(
					imagi.getImage().getScaledInstance(200, 200, java.awt.Image.SCALE_DEFAULT));

			imageIcoList.add(imagi);

			algoname = getAlgorithmName(ima);

			imageAlgori.setText("Used algorithm " + algoname);

			if (i % 2 == 0) {

				butLab.setBackground(Color.blue);

			} else {
				butLab.setBackground(Color.green);
			}
			i++;

			imageView.setIcon(imageIcon);
			imageView.setName(ima.getAbsolutePath());

			imageView.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent me) {
					mouseClick(me, imagi);
				}
			});

			butLab.add(imageView);
			butLab.add(imageAlgori);
			panelImage.add(butLab);

		}

		JButton saveImageBt = new JButton();
		JButton modifySelectionBu = new JButton();

		saveImageBt.setText("Save selected image");
		modifySelectionBu.setText("Modify selected image");

		addButtonListener(saveImageBt, modifySelectionBu, panelImage);

		panelButtons.add(saveImageBt);
		panelButtons.add(modifySelectionBu);
		JScrollPane s = new JScrollPane(panelImage);
		JSplitPane jSp = new JSplitPane();

		jSp.setOrientation(SwingConstants.VERTICAL);
		jSp.setLeftComponent(s);
		jSp.setRightComponent(panelButtons);
		jSp.setDividerLocation(1100 + jSp.getInsets().left);

		// aniadimos las componentes al jframe
		jSp.setVisible(true);
		getContentPane().add(jSp);
		pb.setVisible(false);
		pb.dispose();

	}

	public void mouseClick(MouseEvent me, ImageIcon imageIcon) {
		if (!me.isConsumed()) {
			switch (me.getClickCount()) {
			case 1:
				selectedBu = (JButton) me.getSource();
				selectedBu.setName(((JButton) me.getSource()).getName());
				break;
			case 2:
				me.consume();
				ViewImagesBigger vi = new ViewImagesBigger(imageIcon, imageIcoList, directory, true);
				this.openWindows.add(vi);
				break;

			default:
				break;
			}

		}

	}

	private void addButtonListener(JButton saveImageBt, JButton modifiSelectionBu, JPanel pIma) {

		saveImageBt.addActionListener(new ActionListener() {
			// si se genera el click guarda la imagen seleccionada
			public void actionPerformed(ActionEvent e) {
				if (selectedBu != null) {

					SaveImageAndDelete(selectedBu.getName());
				} else {
					JOptionPane.showMessageDialog(pIma, "Not image selected", "Warning", JOptionPane.WARNING_MESSAGE);
				}

			}

		});

		modifiSelectionBu.addActionListener(new ActionListener() {
			// si se genera el click se lleva a otra pestaña para modificar la seleccion
			public void actionPerformed(ActionEvent e) {
				if (selectedBu != null) {

					modifySeclection(selectedBu.getName());

				} else {
					JOptionPane.showMessageDialog(pIma, "Not image selected", "Warning", JOptionPane.WARNING_MESSAGE);
				}

			}

		});

	}

	private String getAlgorithmName(File ima) {

		String[] splitNameIma = ima.getName().split("_");
		String algoritmNameString = splitNameIma[splitNameIma.length - 1].replace(".tiff", "");
		return algoritmNameString;
	}

	private void SaveImageAndDelete(String filePath) {
		File ima = new File(filePath);
		String algoritmNameString = getAlgorithmName(ima);

		cLa.saveSelectedImage(ima, this.directory, algoritmNameString);
		this.dispose();
		CreateListImageAlgori.deleteTemporalFolder();
	}

	private void modifySeclection(String filename) {
		String fileRoi = filename.replace("tiff", "zip");

		ij.WindowManager.closeAllWindows();
		Opener op = new Opener();
		op.open(filename);

		RoiManager roi = new RoiManager();
		roi.runCommand("Open", fileRoi);

	}

}
