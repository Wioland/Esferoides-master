package funtions;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import esferoides.Methods;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.io.DirectoryChooser;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import interfaces.GeneralView;
import interfaces.ImageTreePanel;
import interfaces.JMenuPropertiesFile;

public class Utils {

	public static GeneralView mainFrame = null;

	/**
	 * Method to search the list of files that satisfies a pattern in a folder and
	 * it child folders. The list of files is stored in the result list.
	 * 
	 * @param pattern pattern that the files must have
	 * @param folder  folder to look for the files
	 * @param result  array with the path of the files that have the pattern
	 */
	public static void search(final String pattern, final File folder, List<String> result, int deep) {
		if (deep != -1) {
			for (final File f : folder.listFiles()) {

				if (f.isDirectory()) {
					search(pattern, f, result, deep - 1);
				}

				if (f.isFile()) {
					if (f.getName().toUpperCase().matches(pattern.toUpperCase())) {
						result.add(f.getAbsolutePath());
					}
				}

			}
		}

	}

	public static void search(final String nameFolder, final String patternName, final File folder, List<String> result,
			int deep) {
		if (deep != -1) {
			for (final File f : folder.listFiles()) {

				if (f.isDirectory()) {
					if (!f.getName().equals(nameFolder)) {
						search(nameFolder, patternName, f, result, deep - 1);
					} else {
						for (final File fo : f.listFiles()) {
							if (fo.isFile()) {
								if (fo.getName().toUpperCase().matches(patternName.toUpperCase())) {
									result.add(fo.getAbsolutePath());
								}
							}
						}
					}
				}

			}
		}

	}

	public static void searchFolders(File parentDir, List<String> folderList, int deep) {
		if (deep != -1) {
			for (final File f : parentDir.listFiles()) {

				if (f.isDirectory()) {
					if (!f.getName().equals("predictions") && !f.getName().equals("temporal")) {
						searchFolders(f, folderList, deep - 1);
						folderList.add(f.getAbsolutePath());
					}

				}

			}
		}
	}

	/**
	 * Method to search the list of files that satisfies a pattern in a folder. The
	 * list of files is stored in the result list.
	 * 
	 * @param pattern pattern that the files must have
	 * @param folder  folder to look for the files
	 * @param result  array with the path of the files that have the pattern
	 */
	public static void searchDirectory(final String pattern, final File folder, List<String> result) {
		if (folder.listFiles() != null) {
			for (final File f : folder.listFiles()) {

				if (f.isFile()) {
					if (f.getName().matches(pattern)) {
						result.add(f.getAbsolutePath());
					}
				}

			}
		}

	}

	public static void searchFoldersName(File parentDir, String folderName, List<String> listResult, int deep) {
		if (deep != -1) {
			for (final File f : parentDir.listFiles()) {

				if (f.isDirectory()) {
					if (f.getName().equals(folderName)) {

						listResult.add(f.getAbsolutePath());
					} else {
						searchFoldersName(f, folderName, listResult, deep - 1);
					}

				}

			}
		}

	}

	/**
	 * Check if there is any file with the pattern given
	 * 
	 * @param pattern type of file to search
	 * @param folder  the folder in with want to search the files
	 * @return true id there is a file with that pattern
	 */
	public static boolean containsExtension(final String pattern, final File folder) {
		for (final File f : folder.listFiles()) {

			if (f.isFile()) {
				if (f.getName().matches(pattern)) {
					return true;
				}
			}

		}
		return false;
	}

	/**
	 * 
	 * Method to draw the results stored in the roi manager into the image, and then
	 * save the image in a given directory. Since we know that there is only one
	 * esferoide per image, we only keep the ROI with the biggest area stored in the
	 * ROI Manager.
	 * 
	 * @param dir       path of the directory to save the files
	 * @param name      name of the image
	 * @param imp1      image in witch the roi was detected
	 * @param rm        roi manager that contains the information of the roi image
	 * @param goodRows  row of the result table we wants to save
	 * @param nameClass name of the algorithm used to creates the roi
	 * @param temp      true if we are creating files in the temporal folder
	 */
	public static synchronized void showResultsAndSave(String dir, String name, ImagePlus imp1, RoiManager rm,
			ArrayList<Integer> goodRows, String nameClass, boolean temp) {

		System.out.println(
				"HE entrado en  save ............................................................................");

		IJ.run(imp1, "RGB Color", "");
		File folder;
		name = name.substring(0, name.lastIndexOf("."));
		name = name.replace(dir, "");
		folder = new File(dir + "predictions");

		if (!temp && !folder.exists()) {
			folder.mkdir();
		} else {

			folder = new File(dir + "temporal");
			if (!nameClass.equals("")) {
				name += "_" + nameClass.substring(nameClass.lastIndexOf(".") + 1);
			}

			folder.mkdir();
		}

		ImageStatistics stats = null;
		double[] vFeret;
		double perimeter = 0;
		if (rm != null) {
			rm.setVisible(false);

			keepBiggestROI(rm);
			rm.runCommand("Show None");
			rm.runCommand("Show All");

			Roi[] roi = rm.getRoisAsArray();

			if (roi.length != 0) {
				System.out.println("Roi length " + roi.length);
				SelectionModify sM = new SelectionModify(imp1);
				imp1.setRoi(rm.getRoi(0));

				rm.runCommand(imp1, "Delete");
				rm.addRoi(sM.fitSpline());
				roi = rm.getRoisAsArray();

				rm.runCommand(imp1, "Draw");
				rm.runCommand("Save", folder.getAbsolutePath() + File.separator + name + ".zip");
				rm.close();
				// saving the roi
				// compute the statistics (without calibrate)
				stats = roi[0].getStatistics();

				vFeret = roi[0].getFeretValues();
				perimeter = roi[0].getLength();
				Calibration cal = imp1.getCalibration();
				double pw, ph;
				if (cal != null) {
					pw = cal.pixelWidth;
					ph = cal.pixelHeight;
				} else {
					pw = 1.0;
					ph = 1.0;
				}
				// calibrate the measures
				double area = stats.area * pw * ph;
				double w = imp1.getWidth() * pw;
				double h = imp1.getHeight() * ph;
				double aFraction = area / (w * h) * 100;
				double perim = perimeter * pw;

				ResultsTable rt = ResultsTable.getResultsTable();
				int nrows = Analyzer.getResultsTable().getCounter();
				goodRows.add(nrows - 1);

				rt.setPrecision(2);
				rt.setLabel(name, nrows - 1);
				rt.addValue("Area", area);
				rt.addValue("Area Fraction", aFraction);
				rt.addValue("Perimeter", perim);
				double circularity = perimeter == 0.0 ? 0.0 : 4.0 * Math.PI * (area / (perim * perim));
				if (circularity > 1.0) {
					circularity = 1.0;
				}
				rt.addValue("Circularity", circularity);
				rt.addValue("Diam. Feret", vFeret[0]);
				rt.addValue("Angle. Feret", vFeret[1]);
				rt.addValue("Min. Feret", vFeret[2]);
				rt.addValue("X Feret", vFeret[3]);
				rt.addValue("Y Feret", vFeret[4]);

			}

			IJ.saveAs(imp1, "Tiff", folder.getAbsolutePath() + File.separator + name + "_pred.tiff");

			ExcelActions.saveExcel(goodRows, folder);

		}

		Utils.mainFrame.getPb().changeActualElementeText();

	}

	/**
	 * 
	 * Method to obtain the area from a polygon. Probably, there is a most direct
	 * method to do this.
	 * 
	 * @param p the polygon we want to know the area
	 * @return the area of the poligon
	 */
	public static final double getArea(Polygon p) {
		if (p == null)
			return Double.NaN;
		int carea = 0;
		int iminus1;
		for (int i = 0; i < p.npoints; i++) {
			iminus1 = i - 1;
			if (iminus1 < 0)
				iminus1 = p.npoints - 1;
			carea += (p.xpoints[i] + p.xpoints[iminus1]) * (p.ypoints[i] - p.ypoints[iminus1]);
		}
		return (Math.abs(carea / 2.0));
	}

	/**
	 * Method to keep the ROI with the biggest area stored in the ROIManager, the
	 * rest of ROIs are deleted.
	 * 
	 * @param rm roi manager with all the measures
	 */
	public static void keepBiggestROI(RoiManager rm) {
		if (rm != null) {
			Roi[] rois = rm.getRoisAsArray();

			if (rois.length >= 1) {
				rm.runCommand("Select All");
				rm.runCommand("Delete");

				Roi biggestROI = rois[0];

				for (int i = 1; i < rois.length; i++) {

					if (getArea(biggestROI.getPolygon()) < getArea(rois[i].getPolygon())) {

						biggestROI = rois[i];
					}

				}
				rm.addRoi(biggestROI);

			}
		}
	}

	/**
	 * Get the files from a directory with a given extension
	 * 
	 * @param format the extension of the file
	 * @param result array with the path of the files with that extension in the
	 *               folder
	 * @return the path of the directory
	 */
	public static String getByFormat(String format, List<String> result) {
		// We ask the user for a directory with nd2 images.

		DirectoryChooser dc = new DirectoryChooser("Select the folder containing the " + format + " images");

		if (dc.getDirectory() != null) {
			String dir = dc.getDirectory();
			// We store the list of nd2 files in the result list.
			File folder = new File(dir);

			Utils.search(".*\\." + format, folder, result, 1);
			Collections.sort(result);
			return dir;
		}

		return null;

	}

	/**
	 * Counts the pixels below the threshold
	 * 
	 * @param imp1      imagePlus to process
	 * @param threshold threshold
	 * @return number of pixels under the threshold
	 */
	public static int countBelowThreshold(ImagePlus imp1, int threshold) {

		ImageProcessor ip = imp1.getProcessor();
		int[] histogram = ip.getHistogram();

		int countpixels = 0;
		for (int i = 0; i < threshold; i++) {
			if (i < histogram.length) {
				countpixels = countpixels + histogram[i];
			} else {
				break;
			}

		}

		return countpixels;

	}

	/**
	 * Counts between the threshold
	 * 
	 * @param imp1       imagePlus to process
	 * @param threshold1 upper threshold
	 * @param threshold2 under threshold
	 * @param num        num to check the threshold
	 * @return true if in the middle of the threshold
	 */
	public static boolean countBetweenThresholdOver(ImagePlus imp1, int threshold1, int threshold2, int num) {

		ImageProcessor ip = imp1.getProcessor();
		int[] histogram = ip.getHistogram(256);
		ImageStatistics is = ip.getStatistics();
		double min = is.min;
		double max = is.max;
		double range = (max - min) / 256;

		int i = 0;
		double pos = min;
		while (pos < threshold1) {
			pos = pos + range;
			i++;

		}

		while (pos < threshold2) {
			if (histogram[i] < num) {
				return true;
			}
			i++;
			pos = pos + range;
		}

		return false;

	}

	/**
	 * Gets the current directory the one that the user has selected and the program
	 * in
	 * 
	 * @return path of the current directory
	 */
	public static String getCurrentDirectory() {

		return mainFrame.getDir();

	}

	// METHODS

	/**
	 * Creates the Main frame or looks if there is a current one to repaint
	 * 
	 * @param dc     working directory
	 * @param geView main JFrame of the program
	 */
	public static void callProgram(String dc) {

		if (dc != null) {

			mainFrame.cancelTimersCurrentDir();
			mainFrame.setDir(dc);
			boolean b = mainFrame.checkOriginalAndAskProcess(dc);
			createGeneralViewOrNot(dc, b);
		}
	}

	public static boolean optionAction() {
		boolean detect = true;

		List<String> result = new ArrayList<String>();
		Utils.search(".*\\.tiff", new File(getCurrentDirectory()), result, 2);

		if (!result.isEmpty()) {
			detect = false;
		}
		return detect;
	}

	/**
	 * Checks if there is a main frame. if it is you repaint the tabpanel if not you
	 * create a new one
	 * 
	 * @param geView     main JFrame of the program
	 * @param dc         the path of the current directory
	 * @param selectAlgo true if you select previously detect esferoide and false
	 *                   otherwise
	 */
	public static void createGeneralViewOrNot(String dc, boolean selectAlgo) {

		ImageTreePanel folderView = mainFrame.getImageTree();
		if (folderView == null) { // if there isn't a main/GenearalWiew Jframe
									// open we create a new one
			mainFrame.createRestOfConttext(dc, selectAlgo);

		} else {
			// We repaint the tab panel with the new content
			if (folderView.getDir() != dc) {
				folderView.setDir(dc);
			}
			folderView.repaintTabPanel(selectAlgo);

		}

	}

	public static boolean menuItemActive(String nameMenuItem) {
		boolean isActive = false;

		Component[] menulist = mainFrame.getMb().getComponents();
		for (Component component : menulist) {
			JMenuPropertiesFile jm = (JMenuPropertiesFile) component;

			if (jm.getListMenus().containsKey(nameMenuItem)) {
				isActive = jm.getListMenus().get(nameMenuItem).isEnabled();
				break;
			}

		}

		return isActive;

	}

	public static void changeUsedAlgoritms() {

		JDialog jDia = new JDialog(mainFrame);

		String sus = Methods.getAlgorithms()[0];
		String col = Methods.getAlgorithms()[1];
		String hv1 = Methods.getAlgorithms()[2];
		String hv2 = Methods.getAlgorithms()[3];
		String tv1 = Methods.getAlgorithms()[4];
		String tbg = Methods.getAlgorithms()[5];
		String hfs = Methods.getAlgorithms()[6];
		String tp = Methods.getAlgorithms()[7];

		String fluoSave = hv2;
		String tifSave = hv2;
		String nd2Save = tbg;
		String jpgSave = tp;

		URL urlUpdater = FileFuntions.getProgramProps();
		if (urlUpdater != null) {
			PropertiesFileFuntions propUpdater = new PropertiesFileFuntions(urlUpdater);

			fluoSave = propUpdater.getProp().getProperty("SelectFluoAlgo");
			tifSave = propUpdater.getProp().getProperty("SelectTifAlgo");
			nd2Save = propUpdater.getProp().getProperty("SelectNd2Algo");
			jpgSave = propUpdater.getProp().getProperty("SelectJpgAlgo");

			boolean changed = FileFuntions.checkSavedAlgoPropertiesFile(fluoSave, tifSave, nd2Save, jpgSave);
			if (changed) {
				propUpdater = new PropertiesFileFuntions(urlUpdater);
				fluoSave = propUpdater.getProp().getProperty("SelectFluoAlgo");
				tifSave = propUpdater.getProp().getProperty("SelectTifAlgo");
				nd2Save = propUpdater.getProp().getProperty("SelectNd2Algo");
				jpgSave = propUpdater.getProp().getProperty("SelectJpgAlgo");
			}
		}

		JComboBox<String> tiffFluoCombobox = new JComboBox<String>();
		tiffFluoCombobox.addItem(sus);
		tiffFluoCombobox.addItem(col);
		tiffFluoCombobox.addItem(hv1);
		tiffFluoCombobox.addItem(hv2);
		tiffFluoCombobox.addItem(hfs);
		tiffFluoCombobox.setSelectedItem(fluoSave);

		JComboBox<String> tiffCombobox = new JComboBox<String>();
		tiffCombobox.addItem(hv1);
		tiffCombobox.addItem(hv2);
		tiffCombobox.setSelectedItem(tifSave);

		JComboBox<String> nd2Combobox = new JComboBox<String>();
		nd2Combobox.addItem(tbg);
		nd2Combobox.addItem(tv1);
		nd2Combobox.setSelectedItem(nd2Save);

		JComboBox<String> jpgCombobox = new JComboBox<String>();
		jpgCombobox.addItem(tp);
		jpgCombobox.setSelectedItem(jpgSave);

		JLabel tiffFluoLAbel = new JLabel("Tiff fluo:");
		JLabel tiffLAbel = new JLabel("Tiff:");
		JLabel nd2Label = new JLabel("ND2:");
		JLabel jpgLabel = new JLabel("JPG:");

		JPanel tiffPanel = new JPanel(new GridLayout(0, 2));
		JPanel tifFluoPanel = new JPanel(new GridLayout(0, 2));
		JPanel nd2Panel = new JPanel(new GridLayout(0, 2));
		JPanel jpgPanel = new JPanel(new GridLayout(0, 2));
		JPanel principalPanel = new JPanel(new GridLayout(5, 0));

		tifFluoPanel.add(tiffFluoLAbel);
		tifFluoPanel.add(tiffFluoCombobox);

		tiffPanel.add(tiffLAbel);
		tiffPanel.add(tiffCombobox);

		nd2Panel.add(nd2Label);
		nd2Panel.add(nd2Combobox);

		jpgPanel.add(jpgLabel);
		jpgPanel.add(jpgCombobox);

		JButton saveButton = new JButton("Save configuration");
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (FileFuntions.changedCombox((String) tiffFluoCombobox.getSelectedItem(),
						(String) tiffCombobox.getSelectedItem(), (String) nd2Combobox.getSelectedItem(),
						(String) jpgCombobox.getSelectedItem())) {

					FileFuntions.saveAlgorithmConfi((String) tiffFluoCombobox.getSelectedItem(),
							(String) tiffCombobox.getSelectedItem(), (String) nd2Combobox.getSelectedItem(),
							(String) jpgCombobox.getSelectedItem());

					JOptionPane.showMessageDialog(mainFrame, "Changes saved");
					jDia.dispose();
				} else {
					JOptionPane.showMessageDialog(mainFrame, "No changes detected");
				}
			}
		});

		jDia.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {

				if (FileFuntions.changedCombox((String) tiffFluoCombobox.getSelectedItem(),
						(String) tiffCombobox.getSelectedItem(), (String) nd2Combobox.getSelectedItem(),
						(String) jpgCombobox.getSelectedItem())) {

					int op = JOptionPane.showConfirmDialog(jDia,
							"The current changes have not been save. \n Do you want to do it now?", "Confirm save",
							JOptionPane.YES_NO_OPTION);

					if (op == 0) {
						saveButton.doClick();
					}

				}

			}

		});

		principalPanel.add(tifFluoPanel);
		principalPanel.add(tiffPanel);
		principalPanel.add(nd2Panel);
		principalPanel.add(jpgPanel);
		principalPanel.add(saveButton);

		jDia.add(principalPanel);
		jDia.setVisible(true);
		jDia.pack();
	}

	public static synchronized void showResultsAndSaveNormal(String dir, String name, ImagePlus imp1, RoiManager rm,
			ArrayList<Integer> goodRows) {

		System.out.println(
				"HE entrado en  save normal ............................................................................");

		IJ.run(imp1, "RGB Color", "");
		File folder;
		name = name.substring(0, name.lastIndexOf("."));
		name = name.replace(dir, "");

		if (name.contains(File.separator)) {
			String subFolder = name.substring(0, name.lastIndexOf(File.separator) + 1);
			name = name.substring(name.lastIndexOf(File.separator) + 1, name.length());
			dir += subFolder;
		}
		folder = new File(dir + "predictions");

		if (!folder.exists()) {
			folder.mkdir();
		}

		ImageStatistics stats = null;
		double[] vFeret;
		double perimeter = 0;
		if (rm != null) {
			rm.setVisible(false);

			keepBiggestROI(rm);
			rm.runCommand("Show None");
			rm.runCommand("Show All");

			Roi[] roi = rm.getRoisAsArray();

			if (roi.length != 0) {
				System.out.println("Roi length " + roi.length);
				SelectionModify sM = new SelectionModify(imp1);
				imp1.setRoi(rm.getRoi(0));

				rm.runCommand(imp1, "Delete");
				rm.addRoi(sM.fitSpline());
				roi = rm.getRoisAsArray();

				rm.runCommand(imp1, "Draw");
				rm.runCommand("Save", folder.getAbsolutePath() + File.separator + name + ".zip");
				rm.close();

				// saving the roi
				// compute the statistics (without calibrate)
				stats = roi[0].getStatistics();

				vFeret = roi[0].getFeretValues();
				perimeter = roi[0].getLength();
				Calibration cal = imp1.getCalibration();
				double pw, ph;
				if (cal != null) {
					pw = cal.pixelWidth;
					ph = cal.pixelHeight;
				} else {
					pw = 1.0;
					ph = 1.0;
				}
				// calibrate the measures
				double area = stats.area * pw * ph;
				double w = imp1.getWidth() * pw;
				double h = imp1.getHeight() * ph;
				double aFraction = area / (w * h) * 100;
				double perim = perimeter * pw;

				ResultsTable rt = ResultsTable.getResultsTable();
				int nrows = Analyzer.getResultsTable().getCounter();
				goodRows.add(nrows - 1);

				rt.setPrecision(2);
				rt.setLabel(name, nrows - 1);
				rt.addValue("Area", area);
				rt.addValue("Area Fraction", aFraction);
				rt.addValue("Perimeter", perim);
				double circularity = perimeter == 0.0 ? 0.0 : 4.0 * Math.PI * (area / (perim * perim));
				if (circularity > 1.0) {
					circularity = 1.0;
				}
				rt.addValue("Circularity", circularity);
				rt.addValue("Diam. Feret", vFeret[0]);
				rt.addValue("Angle. Feret", vFeret[1]);
				rt.addValue("Min. Feret", vFeret[2]);
				rt.addValue("X Feret", vFeret[3]);
				rt.addValue("Y Feret", vFeret[4]);

			}

			IJ.saveAs(imp1, "Tiff", folder.getAbsolutePath() + File.separator + name + "_pred.tiff");
		}
		if (!Utils.mainFrame.getPb().getTextMaxElements().equals("?")) {
			Utils.mainFrame.getPb().changeActualElementeText();
		}

	}

	public static File download(final URL url, String location) {

		try {
			URLConnection urlConnection = url.openConnection();
			InputStream inputStream = urlConnection.getInputStream();
			String p = url.getFile();
			String name = p.substring(p.lastIndexOf("/") + 1, p.length());

			System.out.println("Nombre del archivo: " + name);

			File downloadFile = new File(location + File.separator + name);
			FileOutputStream fileOutputStream = new FileOutputStream(downloadFile);
			try {
				System.out.println("Descargando...");

				byte[] buffer = new byte[1024];
				int len = 0;
				int off = 0;
				while ((len = inputStream.read(buffer)) >= 0) {
					fileOutputStream.write(buffer, off, len);
					fileOutputStream.flush();
				}
				System.out.println("Descarga completada: " + location);

				return downloadFile;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					fileOutputStream.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
