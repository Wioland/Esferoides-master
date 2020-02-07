package interfaces;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

public class JPanelComparer extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel labelImage;
	private JLabel originalImaLb;
	private JPanel panelButtons;
	private JSplitPane splitPanelLabelsImages;
	private JPanel panelLabelsText;
	private JLabel originaText;
	private JLabel newImageText;

	public JPanelComparer() {

		// Add the image
		labelImage = new JLabel();
		originalImaLb = new JLabel();
		labelImage.setVisible(true);
		originalImaLb.setVisible(true);

		// add the buttons to change the image
		JButton backBu = new JButton("<");
		JButton forwardBu = new JButton(">");
		JButton tryAlgoriBu = new JButton("Try other algorithms");
		JButton selectButton = new JButton("Select");
		JButton ExitButton = new JButton("Exit");

		backBu.setName("backBu");
		forwardBu.setName("forwardBu");
		tryAlgoriBu.setName("tryAlgoriBu");
		selectButton.setName("selectButton");
		ExitButton.setName("exitButton");

		panelButtons = new JPanel();

		panelButtons.add(backBu);
		panelButtons.add(forwardBu);
		panelButtons.add(tryAlgoriBu);
		panelButtons.add(selectButton);
		panelButtons.add(ExitButton);

		// Add the split panel that contains the labels with the images (images label)
		splitPanelLabelsImages = new JSplitPane();
		JPanel vi = new JPanel(new GridBagLayout());
		splitPanelLabelsImages.setOrientation(javax.swing.JSplitPane.HORIZONTAL_SPLIT);

		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;

		constraints.weightx = 1;
		constraints.weighty = 1;

		constraints.gridx = 0;
		constraints.gridy = 0;

		splitPanelLabelsImages.setLeftComponent(new JScrollPane(originalImaLb));
		splitPanelLabelsImages.setRightComponent(new JScrollPane(labelImage));

		splitPanelLabelsImages.setVisible(true);

		vi.add(splitPanelLabelsImages, constraints);
		splitPanelLabelsImages.setDividerLocation(500);

		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.gridx = 0;
		constraints.gridy = 1;
		vi.add(panelButtons, constraints);

		this.setLayout(new GridBagLayout());
		// Add the panel with the labels of the type of image(text label)
		panelLabelsText = new JPanel(new GridLayout(0, 2));

		originaText = new JLabel("Original image", SwingConstants.CENTER);
		originaText.setFont(new Font("Arial", Font.BOLD, 12));

		newImageText = new JLabel("New detected esferoid image", SwingConstants.CENTER);
		newImageText.setFont(new Font("Arial", Font.BOLD, 12));

		panelLabelsText.add(originaText);
		panelLabelsText.add(newImageText);

		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 0;
		constraints.weighty = 0;

		constraints.gridx = 0;
		constraints.gridy = 0;

		this.add(panelLabelsText, constraints);

		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1;
		constraints.weighty = 1;

		constraints.gridy = 1;
		constraints.gridx = 0;

		this.add(vi, constraints);

		this.setVisible(true);
	}

	// GETTERS Y SETTERS
	public JLabel getLabelImage() {
		return labelImage;
	}

	public void setLabelImage(JLabel labelImage) {
		this.labelImage = labelImage;
	}

	public JLabel getOriginalImaLb() {
		return originalImaLb;
	}

	public void setOriginalImaLb(JLabel originalImaLb) {
		this.originalImaLb = originalImaLb;
	}

	public Icon getLabelImageIcon() {
		return labelImage.getIcon();
	}

	public void setLabelImageIcon(Icon image) {
		this.labelImage.setIcon(image);
	}

	public Icon getOriginalImaLbIcon() {
		return originalImaLb.getIcon();
	}

	public void setOriginalImaLbIcon(Icon image) {
		this.originalImaLb.setIcon(image);
	}

	public JPanel getPanelButtons() {
		return panelButtons;
	}

	public void setPanelButtons(JPanel panelButtons) {
		this.panelButtons = panelButtons;
	}

	public JButton getBackButton() {
		return (JButton) panelButtons.getComponent(0);
	}

	public JButton getForwarButtonButton() {
		return (JButton) panelButtons.getComponent(1);
	}

	public JButton getTryAlgoButton() {
		if (panelButtons.getComponents().length == 5) {
			return (JButton) panelButtons.getComponent(2);
		} else {
			for (Component c : panelButtons.getComponents()) {
				if (c.getName() == "tryAlgoriBu") {
					return (JButton) c;
				}
			}
		}
		return null;

	}

	public JButton getSelectButton() {
		if (panelButtons.getComponents().length == 5) {
			return (JButton) panelButtons.getComponent(3);
		} else {
			for (Component c : panelButtons.getComponents()) {
				if (c.getName() == "selectButton") {
					return (JButton) c;
				}
			}
		}
		return null;

	}

	public JButton getExitButton() {
		if (panelButtons.getComponents().length == 5) {
			return (JButton) panelButtons.getComponent(4);
		} else {
			for (Component c : panelButtons.getComponents()) {
				if (c.getName() == "exitButton") {
					return (JButton) c;
				}
			}
		}
		return null;
	}

	public JLabel getOriginaText() {
		return originaText;
	}

	public void setOriginaText(JLabel originaText) {
		this.originaText = originaText;
	}

	public JLabel getNewImageText() {
		return newImageText;
	}

	public void setNewImageText(JLabel newImageText) {
		this.newImageText = newImageText;
	}

	public String getOriginaTextTExt() {
		return originaText.getText();
	}

	public void setOriginaTextTExt(String text) {
		this.originaText.setText(text);
	}

	public String getNewImageTextTExt() {
		return newImageText.getText();
	}

	public void setNewImageTextTExt(String text) {
		this.newImageText.setText(text);
	}

	public JSplitPane getSplitPanelLabelsImages() {
		return splitPanelLabelsImages;
	}

	public void setSplitPanelLabelsImages(JSplitPane splitPanelLabelsImages) {
		this.splitPanelLabelsImages = splitPanelLabelsImages;
	}

	public JPanel getPanelLabelsText() {
		return panelLabelsText;
	}

	public void setPanelLabelsText(JPanel panelLabelsText) {
		this.panelLabelsText = panelLabelsText;
	}

}
