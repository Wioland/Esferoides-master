package interfaces;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import esferoides.Methods;
import funtions.FileFuntions;
import funtions.RoiFuntions;
import funtions.Utils;
import ij.IJ;

/**
 * JFrame for showing the new images created with all the algorithms and
 * interact with them
 * 
 * @author Yolanda
 *
 */
public class AlgorithmView extends JFrame {

	private static final long serialVersionUID = 1L;

	private JButton selectedBu;
	private List<ImageIcon> imageIcoList;
	private String directory;
	private File image;
	private ShowImages panelImage;
	private ViewImagesBigger vi;
	private JPanel jSp;
	private Thread t;
	private RoiModifyView roiModifyView = null;
	private JTabbedPane tabbedPanel = null;

	public AlgorithmView(File image) {
		// Window parameters
		Utils.mainFrame.getMb().setEnabled(false);

		setExtendedState(MAXIMIZED_BOTH);
		setTitle("Algorithm view selecter");
		this.setVisible(true);
		setMinimumSize(new Dimension(1000, 300));

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closeActions();
			}

			public void windowClosed(WindowEvent e) {
				closeActions();
			}
		});

		this.image = image;
		if (image.getAbsolutePath().contains("predictions")) {
			this.directory = image.getAbsolutePath().replace("predictions" + File.separator + image.getName(), "");
		} else {
			this.directory = image.getAbsolutePath().replace(image.getName(), "");
		}

		jSp = new JPanel(new GridBagLayout());
		this.add(jSp);

		OurProgressBar pb = new OurProgressBar(this, true);
		Utils.mainFrame.setPb(pb);
		t = new Thread() {
			public void run() {
				initilice();
			}
		};

		t.start();

	}

	// GETTERS Y SETTERS
	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public File getImage() {
		return image;
	}

	public void setImage(File image) {
		this.image = image;
	}

	public JButton getSelectedBu() {
		return selectedBu;
	}

	public void setSelectedBu(JButton selectedBu) {
		this.selectedBu = selectedBu;
	}

	public RoiModifyView getRoiModifyView() {
		return roiModifyView;
	}

	public void setRoiModifyView(RoiModifyView roiModifyView) {
		this.roiModifyView = roiModifyView;
	}

	public Thread getT() {
		return t;
	}

	public void setT(Thread t) {
		this.t = t;
	}

	public JTabbedPane getTabbedPanel() {
		return tabbedPanel;
	}

	public void setTabbedPanel(JTabbedPane tabbedPanel) {
		this.tabbedPanel = tabbedPanel;
	}

	public JPanel getjSp() {
		return jSp;
	}

	public void setjSp(JPanel jSp) {
		this.jSp = jSp;
	}

	// METHODS
	/**
	 * Initializes the Algorithm view JFrame with the content created
	 */
	public void initilice() {
		String path = RoiFuntions.getOriginalFilePathFromPredictions(this.image.getAbsolutePath());

		List<String> result = new ArrayList<String>();
		result.add(path);

		new Methods(directory, result);

		JPanel panelButtons = new JPanel(new GridLayout(0, 1));

		panelImage = new ShowImages(directory + "temporal", this,
				FileFuntions.namewithoutExtension(image.getAbsolutePath()));
		imageIcoList = panelImage.getImageIcon();
		panelImage.setAutoscrolls(true);

		if (panelImage.getListImages().size() == 0) {
			Utils.mainFrame.getPb().dispose();
			JOptionPane.showMessageDialog(this, "Nothing detected in the image given");

			this.dispose();
		}

		JButton saveImageBt = new JButton();
		JButton modifySelectionBu = new JButton();

		saveImageBt.setText("Save selected image");
		modifySelectionBu.setText("Modify selected image");

		addButtonListener(saveImageBt, modifySelectionBu, panelImage);

		panelButtons.add(saveImageBt);
		panelButtons.add(modifySelectionBu);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;

		JScrollPane s = new JScrollPane(panelImage);

		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;

		jSp.add(s, constraints);

		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.gridx = 1;
		constraints.gridy = 0;
		jSp.add(panelButtons, constraints);

		// add the components to the Jframe
		Utils.mainFrame.getPb().setVisible(false);
		Utils.mainFrame.getPb().dispose();
		jSp.setVisible(true);
		this.getContentPane().add(jSp);
		jSp.repaint();

		IJ.run("Close All");
		this.pack();

		if (!Utils.mainFrame.getMb().isEnabled()) {
			Utils.mainFrame.getMb().setEnabled(true);
		}

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	/**
	 * Action to perform when a button with an image is clicked. - One click select
	 * the button and makes it the selected - Two clicks opens a comparer in the
	 * JFRame to compare with the image in predictions
	 * 
	 * @param me        Mouse event
	 * @param imageIcon ImageIco in the button clicked
	 */
	public void mouseClick(MouseEvent me, ImageIcon imageIcon) {
		if (!me.isConsumed()) {
			switch (me.getClickCount()) {
			case 1:
				if (selectedBu != null) {
					selectedBu.setBackground(null);
					selectedBu.setContentAreaFilled(true);
					selectedBu.setOpaque(false);
				}
				selectedBu = (JButton) me.getSource();
				selectedBu.setName(((JButton) me.getSource()).getName());
				selectedBu.setBackground(Color.yellow);
				selectedBu.setContentAreaFilled(false);
				selectedBu.setOpaque(true);
				break;
			case 2:
				me.consume();
				if (vi == null) {
					vi = new ViewImagesBigger(imageIcon, imageIcoList, this, false);
					addComparer(vi);
				} else {
					vi.getJPComparer().setLabelImageIcon(imageIcon, imageIcon.getDescription());

				}

				break;

			default:
				break;
			}

		}

	}

	/**
	 * Adds the comparer interface to the JFRame and deletes the JScrollpanel with
	 * all the images
	 * 
	 * @param vi the JPanel with the comparer interface
	 */
	public void addComparer(ViewImagesBigger vi) {

		this.vi = vi;

		GridBagConstraints constraints = new GridBagConstraints();

		this.jSp.remove(this.jSp.getComponentAt(1, 0));

		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1;
		constraints.weighty = 1;

		constraints.gridx = 0;
		constraints.gridy = 0;

		this.jSp.add(vi.getJPComparer(), constraints);
		this.pack();

		jSp.requestFocusInWindow();

		jSp.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					vi.getJPComparer().getBackButton().doClick();

					break;

				case KeyEvent.VK_RIGHT:
					vi.getJPComparer().getForwarButtonButton().doClick();

					break;

				default:
					break;
				}

			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});

	}

	/**
	 * 
	 * Gets the button associated with the given image
	 * 
	 * @param imagPath image path
	 * @return A JButton associated with the image given
	 */
	public JButton getButtonFromImage(String imagPath) {

		return panelImage.getListImagesPrev().get(imagPath);

	}

	/**
	 * adds the action to perform when the save button is clicked adds the action to
	 * perform when the modify button is clicked
	 * 
	 * @param saveImageBt       The button to add the save action
	 * @param modifiSelectionBu the button to add the modify action
	 * @param pIma              panel with the buttons with the images to select
	 */
	private void addButtonListener(JButton saveImageBt, JButton modifiSelectionBu, JPanel pIma) {

		// SAve action
		saveImageBt.addActionListener(new ActionListener() {
			// if click saves the selected image
			public void actionPerformed(ActionEvent e) {
				if (selectedBu != null) {

					SaveImageAndDelete(selectedBu.getName());
				} else {
					JOptionPane.showMessageDialog(pIma, "Not image selected", "Warning", JOptionPane.WARNING_MESSAGE);
				}

			}

		});

		// Modify action
		modifiSelectionBu.addActionListener(new ActionListener() {
			// if click new imageJ window to modify the roi selection
			public void actionPerformed(ActionEvent e) {
				if (selectedBu != null) {

					modifySeclection(selectedBu.getName());

				} else {
					JOptionPane.showMessageDialog(pIma, "Not image selected", "Warning", JOptionPane.WARNING_MESSAGE);
				}

			}

		});

	}

	/**
	 * SAves the image given in predictions folder, changing it with the current one
	 * already in the folder predictions if the comparer is open exchange the
	 * original image with this new one
	 * 
	 * @param filePath
	 */
	private void SaveImageAndDelete(String filePath) {
		if (this.roiModifyView != null) {
			String nameSelectedIma = FileFuntions.namewithoutExtension(filePath).replace("_pred", "");
			String nameModifyIma = FileFuntions.namewithoutExtension(roiModifyView.getRoiPath());
			if (nameSelectedIma.equals(nameModifyIma)) {
				roiModifyView.closeRoiModifyAction();
			}
		}
		File ima = new File(filePath);
		String saveDir = ima.getAbsolutePath().replace("temporal" + File.separator + ima.getName(), "predictions");
		boolean save = FileFuntions.saveSelectedImage(ima, saveDir);
		if (vi != null && save) {
			vi.getJPComparer().setOriginalImaLbIcon(vi.getJPComparer().getLabelImageIcon(),
					vi.getJPComparer().getLabelImage().getName());
		}

	}

	/**
	 * Modufy the roi selection
	 * 
	 * @param filename the image in with you modify the roi selection
	 */
	private void modifySeclection(String filename) {
		String fileRoi = filename.replace("_pred.tiff", ".zip");

		String originalPath = RoiFuntions.getoriginalFilePathFromTempralTiff(filename);

		ij.WindowManager.closeAllWindows();

		if (this.roiModifyView != null) {
			this.roiModifyView.getBtnClose().doClick();
		}
		this.roiModifyView = new RoiModifyView(originalPath, fileRoi, this);

	}

	/**
	 * Actions to do when this JFram is closed
	 */
	private void closeActions() {
		File folder = Methods.getTemporalFolder();
		if (folder != null) {
			FileFuntions.deleteFolder(folder);
		}

		if (!Utils.mainFrame.getMb().isEnabled()) {
			Utils.mainFrame.getMb().setEnabled(true);
		}

	}

	/**
	 * Repaints the content of the JFrame. It creates a tabbed panel to the JFrame
	 * and adds to it the current content if the tabbed were null
	 */
	public void repaintContent() {

		if (this.tabbedPanel == null) {
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.fill = GridBagConstraints.BOTH;
			constraints.weightx = 1;
			constraints.weighty = 1;

			constraints.gridx = 0;
			constraints.gridy = 0;

			this.tabbedPanel = new JTabbedPane();
			this.tabbedPanel.add("Images", this.jSp.getComponentAt(1, 0));
			this.jSp.remove(this.jSp.getComponentAt(1, 0));
			this.jSp.add(tabbedPanel, constraints);

			tabbedPanel.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {

					if (tabbedPanel.getTitleAt(tabbedPanel.getSelectedIndex()).equals("Images")) {
						jSp.requestFocusInWindow();
					}

				}
			});
		}

		this.repaint();

	}

	/**
	 * Deletes an image from the current view and repaints it. The current view
	 * could be a viewImagesBigger or a scrollview.
	 */
	public void deleteImageList() {
		if (vi != null) {

			vi.deleteImageFromListNoComparerOldVsNew();
			if (vi.getListImages().size() == 0) {
				JOptionPane.showMessageDialog(this, "There are not more images, closing this window");
				this.dispose();
			}

		} else {
			panelImage.removeModifyButton();
			if (panelImage.getListImages().size() == 0) {
				JOptionPane.showMessageDialog(this, "There are not more images, closing this window");
				this.dispose();
			}
		}

	}

	/**
	 * Modifies an image of the current view if it has changes. Repaint the modify
	 * image of the current view. The current view could be a viewImageBigger or a
	 * Scrollview
	 */
	public void modifyImageList() {
		if (vi != null) {
			vi.modifyImageFromListNoComparerOldVsNew();
		} else {
			panelImage.removeModifyButton();
		}
	}

}
