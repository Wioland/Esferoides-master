package interfaces;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.SwingConstants;

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
				File folder = Methods.getTemporalFolder();
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

		this.image = image;
		this.directory = dir;
		OurProgressBar pb = new OurProgressBar(this);
		// directory=dir+"temporal"+File.separator;

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

		// jSp.setDividerLocation(1100 + jSp.getInsets().left);

		// aniadimos las componentes al jframe
		pb.setVisible(false);
		pb.dispose();
		jSp.setVisible(true);
		getContentPane().add(jSp);
		jSp.repaint();

		IJ.run("Close All");
		// pack();

	}

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
					vi = new ViewImagesBigger(imageIcon, imageIcoList, this);
					addComparer(vi);
				} else {
					vi.getLabelImage().setIcon(imageIcon);
				}

				break;

			default:
				break;
			}

		}

	}

	public void addComparer(ViewImagesBigger vi) {

		JPanel JPaneDad = (JPanel) selectedBu.getParent().getParent().getParent().getParent();
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel panelLabels = new JPanel(new GridLayout(0, 2));

		JLabel originaText = new JLabel("Original image", SwingConstants.CENTER);
		originaText.setFont(new Font("Arial", Font.BOLD, 12));

		JLabel newImageText = new JLabel("New detected esferoid image", SwingConstants.CENTER);
		newImageText.setFont(new Font("Arial", Font.BOLD, 12));

		panelLabels.add(originaText);
		panelLabels.add(newImageText);

		JPanel panelButtons = (JPanel) jSp.getComponent(1);
		jSp.remove(panelButtons);

		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.gridx = 1;
		constraints.gridy = 1;
		jSp.add(panelButtons, constraints);

		JScrollPane scrollIma = (JScrollPane) JPaneDad.getComponentAt(1, 0);
		scrollIma.setVisible(false);

		constraints.weightx = 0;
		constraints.weighty = 0;

		constraints.gridx = 0;
		constraints.gridy = 0;

		JPaneDad.remove(scrollIma);
		JPaneDad.add(panelLabels, constraints);

		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1;
		constraints.weighty = 1;

		constraints.gridy = 1;
		constraints.gridx = 0;

		JPaneDad.add(vi, constraints);
		JPaneDad.updateUI();

	}

	public JButton getButtonFromImage(String imagPath) {

		return panelImage.getListImagesPrev().get(imagPath);

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

	private void SaveImageAndDelete(String filePath) {
		File ima = new File(filePath);
		FileFuntions.saveSelectedImage(ima, this.directory + "predictions");
		// FileFuntions.deleteTemporalFolder(new File(this.directory + "temporal"));
		// this.dispose();
	}

	private void modifySeclection(String filename) {
		String fileRoi = filename.replace("_pred.tiff", ".zip");

		String originalPath = RoiFuntions.getoriginalFilePathFromTempralTiff(filename);

		ij.WindowManager.closeAllWindows();

		RoiFuntions.showOriginalFilePlusRoi(originalPath, fileRoi);

	}

}
