
package interfaces;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import funtions.CreateListImageAlgori;
import ij.io.DirectoryChooser;

public class GeneralView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String directory;

	public GeneralView(String directory) {

		this.directory = directory;

		// Parametros ventana
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(MAXIMIZED_BOTH);
		setVisible(true);
		setTitle("Results");
		setMinimumSize(new Dimension(1000, 700));
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

		OurProgressBar pb = new OurProgressBar(this);
		ImageTreePanel imageTree = new ImageTreePanel(directory);
		getContentPane().add(imageTree);

		setVisible(true);
		pb.setVisible(false);
		pb.dispose();
	}

	// PRUEBAS

	public static void main(String[] args) {

		DirectoryChooser dc = new DirectoryChooser("Select the folder containing the nd2 images");

		if (dc.getDirectory() != null) {
			int selection = JOptionPane.showOptionDialog(null, "Select an option", "Option selecter",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, // null para icono por
																					// defecto.
					new Object[] { "Use algoritm", "View results" }, // null para YES, NO y CANCEL
					"Use algoritm");

			switch (selection) {
			case 0:
				SelectAlgoritm seletAl= new SelectAlgoritm();
				break;
			case 1:
				GeneralView ventana = new GeneralView(dc.getDirectory());
				break;
			default:
				break;
			}

			
		}

//		CreateListImageAlgori j= new CreateListImageAlgori();
//		try {
//			j.iniA(j.getClasses("esferoides"));
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}
}
