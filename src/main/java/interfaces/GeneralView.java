
package interfaces;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import funtions.ExcelActions;
import funtions.FileFuntions;
import funtions.Utils;

public class GeneralView extends JFrame {

	private static final long serialVersionUID = 1L;
	// private String directory;
	private JMenuBar mb;
	private String dir;
	private ImageTreePanel imageTree;
	private Thread t;
	private List<Timer> timers;
	private int timeTaskExcel = 60;
	private int timeTaskImages = 60;

	public GeneralView() {
		this.mb = new JMenuBar();
		this.timers = new ArrayList<Timer>();
		// Window parameters
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(MAXIMIZED_BOTH);
		setVisible(true);
		setTitle("Detect esferoid program");
		setMinimumSize(new Dimension(1000, 700));
		toFront();
	}

	// GETTERS AND SETTERS
	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public ImageTreePanel getImageTree() {
		return imageTree;
	}

	public void setImageTree(ImageTreePanel imageTree) {
		this.imageTree = imageTree;
	}

	public List<Timer> getTimers() {
		return timers;
	}

	public void setTimers(List<Timer> timers) {
		this.timers = timers;
	}

	// METHODS
	/**
	 * Paints the graphics of the main FRame
	 * 
	 * @param dc
	 *            path of the current directory
	 */
	public void paintMainFRame(String dc) {

		if (dc != null) {
			boolean selectAlgo = false;
			selectAlgo = Utils.optionAction();
			if (selectAlgo) {
				selectAlgo = FileFuntions.isOriginalImage(new File(dc));
			}
			this.dir = dc;
			createContent(dc, selectAlgo);
			this.repaint();
		}

	}

	/**
	 * Creates the graphics content of the frame
	 * 
	 * @param directory
	 *            current directory
	 * @param selectAlgo
	 *            if you are detecting esferoid
	 */
	private void createContent(String directory, boolean selectAlgo) {
		OurProgressBar pb = new OurProgressBar(this);
		cancelTimersCurrentDir();
		t = new Thread() {
			public void run() {
				if (getContentPane().getComponentZOrder(imageTree) != -1) {
					getContentPane().remove(imageTree);
				}
				imageTree = new ImageTreePanel(directory, selectAlgo);

				getContentPane().add(imageTree);

				setVisible(true);
				pb.setVisible(false);
				pb.dispose();
				
				if(!selectAlgo){
					returnTheTimers(imageTree.getFolderView());
				}
				

				if (imageTree.getFolderView().isOriginalIma()) {
					int op = JOptionPane.showConfirmDialog((Component) null,
							"There aren´t Tiff files in this folder, but we detected files with the required extension. Do you want to detect the esferoid of this images?",
							"alert", JOptionPane.YES_NO_OPTION);
					if (op == 0) {

						imageTree.repaintTabPanel(!selectAlgo);
					}
				}
				
				t.interrupt();
			}
		};

		t.start();
		
	}

	/**
	 * Generate the context of the main Jframe
	 * 
	 * @param directory
	 *            path of the directory selected by the user
	 * @param selectAlgo
	 *            if you are detecting esferoid
	 */
	public void createRestOfConttext(String directory, boolean selectAlgo) {

		this.dir = directory;

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {

				File deleteFile = new File(directory + File.separator + "temporal");
				FileFuntions.deleteFolder(deleteFile);

			}
		});

		setJMenuBar(mb);
		JMenuPropertiesFile menu = new JMenuPropertiesFile();
		mb.add(menu);

		createContent(directory, selectAlgo);

	}

	/*
	 * A tener en cuenta cuando se tiene ya jFrame general y se cambia de
	 * directorio y se  generan los los nuevos archivos en la opción
	 * detectEsferoid 
	 */
	public void cancelTimersCurrentDir() {

		// si ya teniamos timers creados para otro directorio, los paramos y
		// eliminamos y pasamos a crear los del actual
		if (!this.getTimers().isEmpty()) {
			for (Timer t : this.getTimers()) {
				t.cancel();;
			}
			this.getTimers().clear();
			System.out.println("Se ha parado el timer");
		}
	}

	public void returnTheTimers(TabPanel tab) {
		System.out.println("Se han reanudado los timers");
		
		Timer tIma=FileFuntions.imagescheckWithTime(tab, timeTaskImages);
		Timer tExcel=ExcelActions.excelcheckWithTime(tab, dir, timeTaskExcel);
		
		this.getTimers().add(tExcel);
		this.getTimers().add(tIma);
	}

}
