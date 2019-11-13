package interfaces;


import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

public class ViewImagesBigger extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<ImageIcon> listImages;
	private JLabel labelImage;
	private int indexImagenList=0;
	private Icon image;
	private String dir;
	
	

	public ViewImagesBigger(Icon image, List<ImageIcon> listImages,String directory) {

		setExtendedState(MAXIMIZED_BOTH);
		setVisible(true);
		setTitle("Image view");
		setMinimumSize(new Dimension(1000,800));
		
		
		this.listImages=listImages;
		this.image=image;
		this.indexImagenList=listImages.indexOf(image);
		dir= directory;
		

		// Se aniade la imagen
		labelImage = new JLabel();
		labelImage.setIcon(image);
		labelImage.setVisible(true);
		

		// se aniaden los botones para poder pasar las imagenes
		JButton backBu = new JButton();
		JButton forwardBu = new JButton();
		JButton tryAlgoriBu = new JButton();
		backBu.setText("<");
		forwardBu.setText(">");
		tryAlgoriBu.setText("Try other algorithm");

		addlistenerButton(backBu, forwardBu, tryAlgoriBu);

		// contenedor de botones y puesta en orden de estos
		JPanel panelButtons = new JPanel();

		panelButtons.setLayout(new GridLayout(0, 4));
		panelButtons.add(backBu);
		panelButtons.add(forwardBu);
		panelButtons.add(tryAlgoriBu);
		

		JSplitPane jSp = new JSplitPane();

		jSp.setOrientation(SwingConstants.HORIZONTAL);
		jSp.setTopComponent(labelImage);
		jSp.setBottomComponent(panelButtons);
		labelImage.setHorizontalAlignment(JLabel.CENTER);
		labelImage.setVerticalAlignment(JLabel.CENTER);
		jSp.setDividerLocation(900 + jSp.getInsets().top);
		JScrollPane s = new JScrollPane(jSp);
		// aniadimos las componentes al jframe
		jSp.setVisible(true);
		this.pack();
		this.add(s);
		this.setVisible(true);

	}



	private void addlistenerButton(JButton backBu, JButton forwardBu, JButton tryAlgoriBu) {

		backBu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				indexImagenList--;
				if(indexImagenList<0) {
					indexImagenList=listImages.size()-1;
				}
				
				labelImage.setIcon(listImages.get(indexImagenList));
				image=labelImage.getIcon();

			}
		});

		forwardBu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				indexImagenList++;
				if(indexImagenList>listImages.size()-1) {
					indexImagenList=0;
				}
				
				labelImage.setIcon(listImages.get(indexImagenList));
				image=labelImage.getIcon();

			}
		});

		tryAlgoriBu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				ImageIcon i=listImages.get(listImages.indexOf(image));
				File f= new File(i.getDescription());
				
				AlgorithmView alg= new AlgorithmView(f, dir);

			}
		});

	}

}
