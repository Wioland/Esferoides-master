package interfaces;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import funtions.ExcelActions;
import funtions.FileFuntions;
import funtions.Utils;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.ImageWindow;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;
import loci.plugins.in.ImporterOptions;

/**
 * JPanel to shoe the image whit the roi and allows interact with it
 * 
 * @author Yolanda
 *
 */
public class RoiModifyView extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String pathImage;
	private String roiPath;
	private ImagePlus imp;
	private boolean mainFrame;
	private TabPanel tp;
	private AlgorithmView al;
	private ImageWindow panelIma;
	private String[] toolsOptions = { "rectangle", "oval", "polygon", "freehand" };
	private JButton btnClose;
	private JButton deleteBu;
	private JButton resetBu;
	private RoiManager roi;
	private JScrollPane scrollPanel;
	private boolean hasRoi;
	private JPanel imageCanvas;

	public RoiModifyView(String pathImage, String roiPath) {

		this.tp = Utils.mainFrame.getImageTree().getFolderView();
		this.pathImage = pathImage;
		this.roiPath = roiPath;
		this.mainFrame = true;

		paintContent(Utils.mainFrame);
	}

	public RoiModifyView(String pathImage, String roiPath, AlgorithmView al) {

		this.al = al;
		this.pathImage = pathImage;
		this.roiPath = roiPath;
		this.mainFrame = false;

		paintContent(al);
	}

	// GETTERS Y SETTERS
	public JButton getBtnClose() {
		return btnClose;
	}

	public void setBtnClose(JButton btnClose) {
		this.btnClose = btnClose;
	}

	public String getRoiPath() {
		return roiPath;
	}

	public void setRoiPath(String roiPath) {
		this.roiPath = roiPath;
	}

	// METHODS
	/**
	 * PAints the content of this JPanel
	 */
	private void paintContent(JFrame f) {
		JPanel panelButtons = new JPanel(new GridLayout(0, 1));

		OurProgressBar pb = new OurProgressBar(f, false);

		Thread t = new Thread() {
			public void run() {
				setLayout(new GridLayout(0, 2));
				setLayout(new GridBagLayout());

				JComboBox<String> combo = new JComboBox<String>();

				combo.addItem(toolsOptions[0]);
				combo.addItem(toolsOptions[1]);
				combo.addItem(toolsOptions[2]);
				combo.addItem(toolsOptions[3]);
				combo.setSelectedItem(toolsOptions[3]);

				// Acction when JComboBox changes the selected item .
				combo.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						String itemSeleecionado = combo.getSelectedItem().toString();
						IJ.setTool(itemSeleecionado);

					}
				});
				resetBu = new JButton("RESTORE ROI");
				resetBu.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						resstoreRoi();

					}
				});
				deleteBu = new JButton("DELETE ROI");
				deleteBu.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						deleteRoi();

					}
				});

				JButton modifyBu = new JButton("MODIFY ROI");
				modifyBu.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						modifyCurrentRoiAndSAve();

					}
				});
				JButton closeBu = new JButton("CLOSE");
				closeBu.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						btnClose.doClick();

					}
				});

				paintImageModify();
				IJ.setTool("freehand");

				panelButtons.add(combo);
				panelButtons.add(resetBu);
				panelButtons.add(modifyBu);
				panelButtons.add(deleteBu);
				panelButtons.add(closeBu);

				GridBagConstraints constraints = new GridBagConstraints();
				constraints.fill = GridBagConstraints.BOTH;

				constraints.weightx = 1;
				constraints.weighty = 1;
				constraints.gridx = 0;
				constraints.gridy = 0;

				add(scrollPanel, constraints);

				constraints.weightx = 0;
				constraints.weighty = 0;
				constraints.gridx = 1;
				constraints.gridy = 0;
				add(panelButtons, constraints);

				if (!hasRoi) {
					deleteBu.setEnabled(false);
					resetBu.setEnabled(false);
				}
				pb.dispose();

				addXTotab();
			}

		};

		t.start();

	}

	/**
	 * Set the selected roi to the first element of the Roi manager
	 */
	protected void resstoreRoi() {
		roi.select(0);
	}

	/**
	 * Modify the current roi of the image and saves it
	 */
	protected void modifyCurrentRoiAndSAve() {

		Roi newRoi = imp.getRoi();

		if (newRoi != null) {
			if (!compareSameRoi()) {
				roi.select(0);
				roi.runCommand(imp, "Delete");
				roi.select(0);

				String originalName = FileFuntions.namewithoutExtension(this.pathImage);
				roi.runCommand(imp, "Update");
				File aux = new File(this.pathImage);
				String dir = aux.getAbsolutePath().replace(aux.getName(), "");
				ArrayList<Integer> goodRows = new ArrayList<Integer>();
				roi.runCommand(imp, "Measure");
				ResultsTable rt = ResultsTable.getResultsTable();
				rt.getResultsWindow().setVisible(false);

				File excel = null;

				if (this.mainFrame) {
					Utils.showResultsAndSaveNormal(dir, this.pathImage, imp, roi, goodRows);

					String excelpath = aux.getAbsolutePath().replace(aux.getName(), "results.xls");
					excel = new File(excelpath);

				} else { // If the parent is algorithm view

					String nameClass = FileFuntions.namewithoutExtension(roiPath);
					nameClass = nameClass.replace(originalName + "_", "");
					Utils.showResultsAndSave(dir, this.pathImage, imp, roi, goodRows, nameClass, true);

					String excelpath = this.roiPath.replace(".zip", "_results.xls");
					excel = new File(excelpath);
				}

				File roiFile = new File(this.roiPath);
				File dirFile = new File(this.roiPath.replace(roiFile.getName(), ""));
				dirFile.setLastModified(roiFile.lastModified());

				try {
					if (excel.exists()) { // if the excel file exist we modify it
						HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(excel));
						HSSFSheet sheet = workbook.getSheet("Results");
						if (mainFrame) {
							ExcelActions.getchangeExcelRowFromResultTable(goodRows, sheet, originalName, excel,
									workbook);
						}
						workbook.close();
					} else { // if it does not exist we create it
						ExcelActions ex = new ExcelActions(rt, Utils.mainFrame.getDir());
						ex.convertToExcel();
					}

					if (this.mainFrame) {
						JOptionPane.showMessageDialog(Utils.mainFrame, "The new Roi was save");

					} else { // If the parent is algorithm view

						JOptionPane.showMessageDialog(al,
								"The new Roi was save, but this change is only temporal.For making this change permanent, please use the 'SAVE BUTTON' of the main window");
						al.modifyImageList();
					}
					deleteBu.setEnabled(true);
					resetBu.setEnabled(true);
					rt.reset();
					rt.getResultsWindow().close();

					repaintImageModify();

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				roi.select(1);
				roi.runCommand(imp, "Delete");
				resstoreRoi();
				JOptionPane.showMessageDialog(null, "The current selection is null or it is the same as the saved one");
			}

		} else {
			JOptionPane.showMessageDialog(null, "The current selection is null or it is the same as the saved one");
			resstoreRoi();
		}

	}

	/**
	 * Opens the image that the roi is going to be modidy and its roi
	 */
	private void paintImageModify() {

		openImage();
		imageCanvas = new JPanel();
		imageCanvas.add(panelIma.getCanvas());
		scrollPanel = new JScrollPane(imageCanvas);

		roi = new RoiManager(false);

		if ((new File(roiPath)).exists()) {
			roi.runCommand("Open", roiPath);
			roi.select(0);
			hasRoi = true;
		} else {
			hasRoi = false;
			JOptionPane.showMessageDialog(null, "No Roi file associated with this image");
		}

	}

	/**
	 * Opens the imagen that is in the parameter pathImage
	 */
	private void openImage() {
		ImporterOptions options = null;
		imp = FileFuntions.openImageIJ(this.pathImage, options);
		imp.show();
		panelIma = imp.getWindow();
		panelIma.setVisible(false);
	}

	/**
	 * Repaint the imagen and the roi open
	 */
	private void repaintImageModify() {

		ij.WindowManager.closeAllWindows();

		openImage();

		imageCanvas.remove(0);
		imageCanvas.add(panelIma.getCanvas());
		imageCanvas.repaint();
		this.scrollPanel.repaint();
		this.repaint();
		resstoreRoi();

	}

	/**
	 * Deletes the current Roi
	 */
	protected void deleteRoi() {
		int op = JOptionPane.showConfirmDialog(null,
				"This action will erase the current Roi of the image. Are you sure you want to proceed?",
				"Delete Roi confirm", JOptionPane.YES_NO_OPTION);

		if (op == 0) {
			// delete the tiff
			String originalName = FileFuntions.namewithoutExtension(this.roiPath);
			File aux = new File(this.roiPath);
			aux.delete();
			// delete the zip
			String imagePath = this.roiPath.replace(".zip", "_pred.tiff");
			aux = new File(imagePath);
			aux.delete();

			if (this.mainFrame) {
				imagePath = Utils.mainFrame.getDir();
				if (!imagePath.endsWith(File.separator)) {
					imagePath += File.separator;
				}
				imagePath += "results.xls";
				aux = new File(imagePath);

				ExcelActions.deleteRow(aux, originalName);

			} else {
				imagePath = imagePath.replace("_pred.tiff", "_results.xls");
				aux = new File(imagePath);
				aux.delete();
				al.deleteImageList();
			}
			roi.runCommand(imp, "Delete");
			JOptionPane.showMessageDialog(null, "Roi delete");

			if (!mainFrame) {
				this.btnClose.doClick();
			}

		}

	}

	/**
	 * Actions to do when this tab is close
	 */
	protected void closeRoiModifyAction() {

		Roi newRoi = imp.getRoi();

		if (newRoi != null) {
			if (!compareSameRoi()) {
				int op = JOptionPane.showConfirmDialog(Utils.mainFrame,
						"The currrent changes are not save, they will disapear. Do you want to save them now?",
						"Save changes", JOptionPane.YES_NO_OPTION);
				if (op == 0) {
					modifyCurrentRoiAndSAve();
				}
			}
		}
	}

	/**
	 * Compare if the save and the current roi in the image are the same
	 * 
	 * @return true in case it is the same
	 */
	private boolean compareSameRoi() {
		boolean equal = true;
		roi.runCommand(imp, "Add");
		roi.select(0);

		roi.runCommand(imp, "Measure");
		ResultsTable rt = ResultsTable.getResultsTable();
		rt.getResultsWindow().setVisible(false);

		roi.select(1);
		roi.runCommand(imp, "Measure");

		// Gets the measures of the current roi on the image and the save one and
		// compare them
		int rows = rt.getCounter();
		List<String[]> listRows = new ArrayList<String[]>();

		for (int i = rows; i > 0; i--) {
			String[] s = rt.getRowAsString(i - 1).split(",");
			if (s.length == 1) {
				s = rt.getRowAsString(i - 1).split("\t");
			}

			if (s[1].equals("")) {
				rt.deleteRow(i - 1);
			}
			listRows.add(s);

		}

		for (int j = 1; j < listRows.get(0).length; j++) {
			if (!listRows.get(0)[j].equals(listRows.get(1)[j])) {
				equal = false;
				break;
			}
		}

		rt.reset();
		rt.getResultsWindow().close();
		return equal;
	}

	/**
	 * Adds the "X" button to a tab in a tabpanel
	 */
	private void addXTotab() {

		// Gets the name of the tab and add the title
		String nombreImagen = (new File(this.pathImage).getName());
		String title = "Modify " + nombreImagen;
		int index = -1;

		if (this.mainFrame) {
			tp.add(title, this);
			tp.setSelectedIndex(tp.indexOfTab(title));
			index = tp.indexOfTab(title);
		} else {
			al.repaintContent();
			al.getTabbedPanel().add(title, this);
			al.getTabbedPanel().setSelectedIndex(al.getTabbedPanel().indexOfTab(title));
			index = al.getTabbedPanel().indexOfTab(title);
		}

		// create the "X" buttton

		JPanel pnlTab = new JPanel(new GridBagLayout());
		pnlTab.setOpaque(false);
		JLabel lblTitle = new JLabel(title);
		btnClose = new JButton("x");

		// Add the title and the button side by side in a panel
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;

		pnlTab.add(lblTitle, gbc);

		gbc.gridx++;
		gbc.weightx = 0;
		pnlTab.add(btnClose, gbc);

		// add the panel with button "X" and name to the tabpanel to create the
		// tab
		if (mainFrame) {
			tp.setTabComponentAt(index, pnlTab);
		} else {
			al.getTabbedPanel().setTabComponentAt(index, pnlTab);
		}

		// Adds the action to perform to the "X" button
		btnClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				closeTab(e);
			}
		});
	}

	/**
	 * Action to do when closing the tab
	 * 
	 * @param evt event
	 */
	public void closeTab(ActionEvent evt) {

		JButton bu = (JButton) evt.getSource();
		if (bu.getParent() != null) {
			closeRoiModifyAction();
			panelIma.close();

			if (tp != null) {
				tp.setRoiModifyTab(null);
				tp.remove(tp.indexOfTabComponent(bu.getParent()));
			} else {
				if (al != null) {
					al.setRoiModifyView(null);
					al.getTabbedPanel().remove(al.getTabbedPanel().indexOfTabComponent(bu.getParent()));

				}
			}

		}
	}

}
