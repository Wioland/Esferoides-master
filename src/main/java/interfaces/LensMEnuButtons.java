package interfaces;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import funtions.ShowTiff;

/**
 * JPanel which has the options of a magnifying glass
 * 
 * @author Yolanda
 *
 */
public class LensMEnuButtons extends JPanel {

	private static final long serialVersionUID = 1L;
	private boolean minSizeIma;
	private boolean maxSizeIma;
	private int maximunSize = 900;
	private int minimunSize = 400;
	private int subtractAddSize = 200;
	private Map<String, JButton> listImagesPrev;
	private JButton pluSizeBu;
	private JButton minSizeBu;
	private JLabel imageMaxMin;

	public LensMEnuButtons() {

		minSizeIma = false;
		maxSizeIma = false;

		pluSizeBu = new JButton("+");
		minSizeBu = new JButton("-");
		this.setMaximumSize(new Dimension(200, 200));
		minSizeBu.setMaximumSize(new Dimension(200, 200));
		pluSizeBu.setMaximumSize(new Dimension(200, 200));

		// Adds the actions to perform in each button
		pluSizeBu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				action(e, "plus");

			}
		});

		minSizeBu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				action(e, "minus");

			}
		});

		this.add(pluSizeBu);
		this.add(minSizeBu);

	}

	// GETTERS AND SETTERS
	public int getMaximunSize() {
		return maximunSize;
	}

	public void setMaximunSize(int maximunSize) {
		this.maximunSize = maximunSize;
	}

	public int getMinimunSize() {
		return minimunSize;
	}

	public void setMinimunSize(int minimunSize) {
		this.minimunSize = minimunSize;
	}

	public JButton getPluSizeBu() {
		return pluSizeBu;
	}

	public void setPluSizeBu(JButton pluSizeBu) {
		this.pluSizeBu = pluSizeBu;
	}

	public JButton getMinSizeBu() {
		return minSizeBu;
	}

	public void setMinSizeBu(JButton minSizeBu) {
		this.minSizeBu = minSizeBu;
	}

	public int getSubtractAddSize() {
		return subtractAddSize;
	}

	public void setSubtractAddSize(int subtractAddSize) {
		this.subtractAddSize = subtractAddSize;
	}

	public Map<String, JButton> getListImagesPrev() {
		return listImagesPrev;
	}

	public void setListImagesPrev(Map<String, JButton> listImagesPrev) {
		this.listImagesPrev = listImagesPrev;
	}

	public JLabel getImageMaxMin() {
		return imageMaxMin;
	}

	public void setImageMaxMin(JLabel imageMaxMin) {
		this.imageMaxMin = imageMaxMin;
	}

	public boolean isMinSizeIma() {
		return minSizeIma;
	}

	public void setMinSizeIma(boolean minSizeIma) {
		this.minSizeIma = minSizeIma;
	}

	public boolean isMaxSizeIma() {
		return maxSizeIma;
	}

	public void setMaxSizeIma(boolean maxSizeIma) {
		this.maxSizeIma = maxSizeIma;
	}

	// METHODS
	/**
	 * Action to perform in the case of clicking one button
	 * 
	 * @param e          acction event happened
	 * @param typeAction type of the action plus is '+' button minus if '-' button
	 */
	public void action(ActionEvent e, String typeAction) {
		if (this.listImagesPrev != null) {
			actionMap(typeAction);
		} else {
			actionImage(typeAction);
		}

	}

	/**
	 * Action for minimize or maximize the size of an image depending on the param
	 * given
	 * 
	 * @param typeAction plus for bigger minus for smaller
	 */
	private void actionImage(String typeAction) {
		int heightSize = imageMaxMin.getIcon().getIconHeight();
		int widthSize = imageMaxMin.getIcon().getIconWidth();
		int subtract = subtractAddSize;

		// Takes the quantity of pixels we wants to add or take from the
		// image
		if (typeAction.equals("plus")) {
			if (!maxSizeIma) {
				subtract = -subtractAddSize;
			}
		}
		// if we are not in the max or min size of the image we
		// transform all the images to match the new size an the repaint
		if ((!minSizeIma && typeAction.equals("minus")) || (subtract == -subtractAddSize)) {

			ImageIcon iaux;
			ImageIcon iconoEscala;

			iaux = ShowTiff.showTiffToImageIcon(imageMaxMin.getName());
			iconoEscala = new ImageIcon(iaux.getImage().getScaledInstance(widthSize - subtract, heightSize - subtract,
					java.awt.Image.SCALE_DEFAULT));

			imageMaxMin.setIcon(iconoEscala);
			imageMaxMin.repaint();

			heightSize = imageMaxMin.getIcon().getIconHeight();
			widthSize = imageMaxMin.getIcon().getIconWidth();
			isMAxorMinSizeIma(heightSize);

		}

	}

	/**
	 * Action for minimize or maximize the size of a list of images depending on the
	 * param given
	 * 
	 * @param typeAction plus for bigger minus for smaller
	 */
	public void actionMap(String typeAction) {

		int heightSize = ((JButton) listImagesPrev.values().toArray()[0]).getIcon().getIconHeight();
		int widthSize = ((JButton) listImagesPrev.values().toArray()[0]).getIcon().getIconWidth();
		int subtract = subtractAddSize;

		// Takes the quantity of pixels we wants to add or take from the image
		if (typeAction.equals("plus")) {
			if (!maxSizeIma) {
				subtract = -subtractAddSize;
			}
		}
		// if we are not in the max or min size of the image we
		// transform all the images to match the new size an the repaint
		if ((!minSizeIma && typeAction.equals("minus")) || (subtract == -subtractAddSize)) {

			JButton bu;
			ImageIcon iaux;
			ImageIcon iconoEscala;

			for (String path : listImagesPrev.keySet()) {
				bu = listImagesPrev.get(path);
				iaux = ShowTiff.showTiffToImageIcon(bu.getName());
				iconoEscala = new ImageIcon(iaux.getImage().getScaledInstance(widthSize - subtract,
						heightSize - subtract, java.awt.Image.SCALE_DEFAULT));
				bu.setIcon(iconoEscala);
				bu.repaint();

			}

			heightSize = ((JButton) listImagesPrev.values().toArray()[0]).getIcon().getIconHeight();
			widthSize = ((JButton) listImagesPrev.values().toArray()[0]).getIcon().getIconWidth();

			isMAxorMinSizeIma(heightSize);
			numberOfImagesPerRow(heightSize, (ShowImages) ((JButton) listImagesPrev.values().toArray()[0]).getParent());

		}
	}

	/**
	 * Checks if the image if already in its min or max size
	 * 
	 * @param heightSize the current height of the images
	 */
	public void isMAxorMinSizeIma(int heightSize) {
		if (heightSize < minimunSize) {
			minSizeIma = true;
			minSizeBu.setEnabled(false);
		} else {
			if (heightSize > maximunSize) {
				maxSizeIma = true;
				pluSizeBu.setEnabled(false);
			} else {
				if (maxSizeIma) {
					maxSizeIma = false;
					pluSizeBu.setEnabled(true);
				}

				if (minSizeIma) {
					minSizeIma = false;
					minSizeBu.setEnabled(true);
				}
			}
		}
	}

	/**
	 * GEts the current height of the images taking the height of the first image in
	 * the array of buttons
	 * 
	 * @return the height of the images
	 */
	public int actualImageHeight() {
		return ((JButton) listImagesPrev.values().toArray()[0]).getIcon().getIconHeight();
	}

	/**
	 * Changes the number of images per row shown depending on the height of the
	 * images
	 * 
	 * @param heightSize       current image height
	 * @param buttonParentPane panel that contains the buttons with the images
	 */
	public void numberOfImagesPerRow(int heightSize, ShowImages buttonParentPane) {

		GridLayout grid = (GridLayout) buttonParentPane.getLayout();
		int colums = grid.getColumns();

		if (heightSize >= 700) {
			if (colums != 1) {
				grid.setColumns(1);
			}
		} else {
			if (heightSize > 400 && heightSize < 700) {
				if (colums != 2) {
					grid.setColumns(2);
				}

			} else {
				if (colums != 3) {
					grid.setColumns(3);
				}

			}
		}
	}

	/**
	 * Restores the indicators of max and min size
	 */
	public void restroreMinMAxBooleans() {
		this.maxSizeIma = false;
		this.minSizeIma = false;

		this.minSizeBu.setEnabled(true);
		this.pluSizeBu.setEnabled(true);

	}

}
