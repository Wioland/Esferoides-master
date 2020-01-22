
package interfaces;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import funtions.FileFuntions;

public class GeneralView extends JFrame {

	private static final long serialVersionUID = 1L;
	private String directory;
	private JMenuBar mb;

	public GeneralView(String directory) {

		this.directory = directory;
		this.mb = new JMenuBar();

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

		setJMenuBar(mb);
		JMenuPropertiesFile menu = new JMenuPropertiesFile();
		mb.add(menu);

		OurProgressBar pb = new OurProgressBar(this);
		ImageTreePanel imageTree = new ImageTreePanel(directory);
		getContentPane().add(imageTree);

		setVisible(true);
		pb.setVisible(false);
		pb.dispose();

		if (imageTree.getFolderView().isOriginalIma()) {
			int op = JOptionPane.showConfirmDialog((Component) null,
					"There aren´t Tif files in this folder, but we detected Nd2 files. Do you want to detect the esferoid of this images?",
					"alert", JOptionPane.YES_NO_OPTION);
			if (op == 0) {
				SelectAlgoritm seletAl = new SelectAlgoritm(this.directory, imageTree);
			}
		}
	}

}
