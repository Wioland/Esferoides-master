package interfaces;

import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

import ij.IJ;
import ij.io.Opener;

public class ViewImagesBigger extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public  ViewImagesBigger(Icon image , List<ImageIcon> listImages) {

		setExtendedState(MAXIMIZED_BOTH);
		setVisible(true);
		setTitle("Image view");
		
		String pruebaMuestraImagen="C:\\Users\\yomendez\\Desktop\\Esferoides\\2x\\ctrl_2_28_pred.tiff";
		String dire="C:\\Users\\yomendez\\Desktop\\Esferoides\\2x";
		String nombre="ctrl_2_28_pred.tiff";

		IJ.open(pruebaMuestraImagen);
		 
		// Se aniade la imagen
		JLabel labelImage = new JLabel();
		//labelImage.setIcon(image);
		labelImage.setIcon(new ImageIcon(pruebaMuestraImagen));
		labelImage.setVisible(true);

		// se aniaden los botones para poder pasar las imagenes
		JButton backBu = new JButton();
		JButton forwardBu = new JButton();
		backBu.setText("<");
		forwardBu.setText(">");

		// contenedor de botones y puesta en orden de estos
		JPanel panelButtons = new JPanel();

		panelButtons.setLayout(new GridLayout(0, 4));
		panelButtons.add(backBu);
		panelButtons.add(forwardBu);

		JSplitPane jSp = new JSplitPane();
		jSp.setLeftComponent(labelImage);
		jSp.setRightComponent(panelButtons);
		jSp.setOrientation(SwingConstants.HORIZONTAL);

		// aniadimos las componentes al jframe
		jSp.setVisible(true);
		this.add(jSp);
		this.setVisible(true);

	}



}
