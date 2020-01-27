package interfaces;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import funtions.ShowTiff;

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

	public LensMEnuButtons(Map<String, JButton> listImagesPrev) {

		minSizeIma = false;
		maxSizeIma = false;
		this.listImagesPrev = listImagesPrev;

		pluSizeBu = new JButton("+");
		minSizeBu = new JButton("-");
		this.setMaximumSize(new Dimension(200, 200));
		minSizeBu.setMaximumSize(new Dimension(200, 200));
		pluSizeBu.setMaximumSize(new Dimension(200, 200));

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

	public void action(ActionEvent e, String typeAction) {

		int heightSize = ((JButton) listImagesPrev.values().toArray()[0]).getIcon().getIconHeight();
		int widthSize = ((JButton) listImagesPrev.values().toArray()[0]).getIcon().getIconWidth();
		int subtract = subtractAddSize;

		if (typeAction.equals("plus")) {
			if (!maxSizeIma) {
				subtract = -subtractAddSize;
			}
		}

		if ((!minSizeIma && typeAction.equals("minus")) || (subtract == -subtractAddSize)) {

			JButton bu ;
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

}
