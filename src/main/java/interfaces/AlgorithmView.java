package interfaces;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import esferoides.Methods;
import funtions.FileFuntions;
import funtions.RoiFuntions;
import ij.IJ;

public class AlgorithmView extends JFrame {

	private static final long serialVersionUID = 1L;

	private JButton selectedBu;
	private List<ImageIcon> imageIcoList;
	private String directory;
	private File image;
	private ShowImages panelImage;
	private ViewImagesBigger vi;
	private JPanel jSp;

	public AlgorithmView(File image, String dir) {
		// Window parameters

		setExtendedState(MAXIMIZED_BOTH);
		setTitle("Algorithm view selecter");
		this.setVisible(true);
		setMinimumSize(new Dimension(1000, 300));

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				File folder = Methods.getTemporalFolder();
				if (folder != null) {
					folder.delete();
				}

			}
		});

		this.image = image;
		this.directory = dir;
		OurProgressBar pb = new OurProgressBar(this);

		String path = RoiFuntions.getOriginalFilePathFromPredictions(this.image.getAbsolutePath());

		List<String> result = new ArrayList<String>();
		result.add(path);
		new Methods(directory, result);

		JPanel panelButtons = new JPanel(new GridLayout(0, 1));

		panelImage = new ShowImages(dir + "temporal", this);
		imageIcoList = panelImage.getImageIcon();
		panelImage.setAutoscrolls(true);

		if (panelImage.getListImages().size() == 0) {
			pb.dispose();
			JOptionPane.showMessageDialog(null, "Nothing detected in the image given");

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
		jSp = new JPanel(new GridBagLayout());

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
		pb.setVisible(false);
		pb.dispose();
		jSp.setVisible(true);
		getContentPane().add(jSp);
		jSp.repaint();

		IJ.run("Close All");

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

//METHODS

	/**
	 * Action to perform when a button with an image is clicked. -One click select
	 * the button and makes it the selected -Two opens a comparer in the JFRame to
	 * compare with the image in predictions
	 * 
	 * @param me        Mouse event
	 * @param imageIcon ImageIco in the button clicked
	 */
	public void mouseClick(MouseEvent me, ImageIcon imageIcon) {
		if (!me.isConsumed()) {
			switch (me.getClickCount()) {
			case 1:
				selectedBu = (JButton) me.getSource();
				selectedBu.setName(((JButton) me.getSource()).getName());
				break;
			case 2:
				me.consume();
				if (vi == null) {
					vi = new ViewImagesBigger(imageIcon, imageIcoList, this, false);
					addComparer(vi);
				} else {
					vi.getJPComparer().setLabelImageIcon(imageIcon);

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

		JPanel JPaneDad = (JPanel) selectedBu.getParent().getParent().getParent().getParent();
		GridBagConstraints constraints = new GridBagConstraints();

		JScrollPane scrollIma = (JScrollPane) JPaneDad.getComponentAt(1, 0);
		scrollIma.setVisible(false);
		JPaneDad.remove(scrollIma);

		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1;
		constraints.weighty = 1;

		constraints.gridx = 0;
		constraints.gridy = 0;

		JPaneDad.add(vi.getJPComparer(), constraints);
		JPaneDad.updateUI();

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
		File ima = new File(filePath);
		FileFuntions.saveSelectedImage(ima, this.directory + "predictions");

		vi.getJPComparer().setOriginalImaLbIcon(vi.getJPComparer().getLabelImageIcon());

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

		RoiFuntions.showOriginalFilePlusRoi(originalPath, fileRoi);

	}

}
