
package interfaces;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import funtions.CreateListImageAlgori;
import funtions.FileFuntions;
import funtions.Utils;
import ij.ImageJ;
import ij.io.DirectoryChooser;

public class GeneralView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String directory;

	public GeneralView(String directory) {
		ImageJ imageJFrame = new ImageJ();
		imageJFrame.setVisible(false);
		this.directory = directory;

		// Parametros ventana
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(MAXIMIZED_BOTH);
		setVisible(true);
		setTitle("Main Frame");
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

				File deleteFile = new File(directory + File.separator + "temporal");
				FileFuntions.deleteTemporalFolder(deleteFile);
				imageJFrame.dispose();

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

		if (imageTree.getFolderView().isNd2Ima()) {
			int op = JOptionPane.showConfirmDialog((Component) null,
					"There aren´t Tiff files in this folder, but we detected Nd2 files. Do you want to detect the esferoid of this images?",
					"alert", JOptionPane.YES_NO_OPTION);
			if (op == 0) {
				SelectAlgoritm seletAl = new SelectAlgoritm(this.directory, imageTree);
			}
		}
	}

}
