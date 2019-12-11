package interfaces;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import funtions.ShowTiff;

public class LensMEnuButtons extends JPanel {

	/**
	 * 
	 */
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

			for (String path : listImagesPrev.keySet()) {
				JButton bu=listImagesPrev.get(path);
				ImageIcon iaux = ShowTiff.showTiffToImageIcon(bu.getName());
				ImageIcon iconoEscala = new ImageIcon(iaux.getImage()
						.getScaledInstance(widthSize - subtract, heightSize - subtract, java.awt.Image.SCALE_DEFAULT));
				bu.setIcon(iconoEscala);
				bu.repaint();

			}
			heightSize = ((JButton) listImagesPrev.values().toArray()[0]).getIcon().getIconHeight();
			widthSize = ((JButton) listImagesPrev.values().toArray()[0]).getIcon().getIconWidth();

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

	}

}
