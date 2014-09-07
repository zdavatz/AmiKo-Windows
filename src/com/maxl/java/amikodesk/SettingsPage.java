package com.maxl.java.amikodesk;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

public class SettingsPage extends JDialog {

	private static String UpdateID = "update";
	private static String LogoImageID = "logo";
	private static String LieferAdresseID = "lieferadresse";
	private static String RechnungsAdresseID = "rechnungsadresse";
	
	private JFrame mFrame = null;
	private JFileChooser mFc = null;
	private JButton mButtonLogo = null;
	private Preferences mPrefs = null;
	
	public SettingsPage(JFrame frame) {		
		
		mFrame = frame;
		mFc = new JFileChooser();
		// Defines a node in which the preferences can be stored
		mPrefs = Preferences.userRoot().node(this.getClass().getName());
		
		this.setSize(128,64);
		
		this.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		add(Box.createRigidArea(new Dimension(0, 10)));
		
		JPanel jplInnerPanel1 = globalAmiKoSettings();
		this.add(jplInnerPanel1);
		
		add(Box.createRigidArea(new Dimension(0, 10)));
		
		JPanel jplInnerPanel2 = shoppingBasketSettings();
		this.add(jplInnerPanel2);
		
		add(Box.createRigidArea(new Dimension(0, 10)));
		
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setTitle("Einstellungen");		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		// Centers the dialog
		this.setLocationRelativeTo(null);
		// Set size
		this.setSize(512,640);		
		this.setResizable(false);
		// Visualize
		this.setVisible(true);
	}
	
	protected JPanel globalAmiKoSettings() {
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new GridLayout(4, 1));
		
		ButtonGroup bg = new ButtonGroup();
		
		jPanel.setOpaque(false);
		jPanel.setBorder(new CompoundBorder(
				new TitledBorder("Update"),
				new EmptyBorder(5,5,5,5)));		
		
		JCheckBox updateNeverCBox = new JCheckBox("manuell");
		JCheckBox updateDailyCBox = new JCheckBox("täglich");
		JCheckBox updateWeeklyCBox = new JCheckBox("wöchentlich");
		JCheckBox updateMonthlyCBox = new JCheckBox("monatlich");		
		
		bg.add(updateNeverCBox);
		bg.add(updateDailyCBox);
		bg.add(updateWeeklyCBox);
		bg.add(updateMonthlyCBox);

		// Retrieve update frequency from preferences...
		// Default: manual update
		switch(mPrefs.getInt(UpdateID, 0)) {
		case 0:
			updateNeverCBox.setSelected(true);
			break;
		case 1:
			updateDailyCBox.setSelected(true);
			break;				
		case 2:
			updateWeeklyCBox.setSelected(true);				
			break;				
		case 3:
			updateMonthlyCBox.setSelected(true);
			break;
		default:
			break;
		}
		
		updateNeverCBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				mPrefs.putInt(UpdateID, 0);
			}
		});
		updateDailyCBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				mPrefs.putInt(UpdateID, 1);
			}
		});
		updateWeeklyCBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				mPrefs.putInt(UpdateID, 2);
			}
		});
		updateMonthlyCBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				mPrefs.putInt(UpdateID, 3);
			}
		});
		jPanel.add(updateNeverCBox);
		jPanel.add(updateDailyCBox);		
		jPanel.add(updateWeeklyCBox);
		jPanel.add(updateMonthlyCBox);
				
		return jPanel;
	}
	
	protected JPanel shoppingBasketSettings() {
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();		
		
		jPanel.setOpaque(false);
		jPanel.setBorder(new CompoundBorder(
				new TitledBorder("Warenkorb"),
				new EmptyBorder(5,5,5,5)));		
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.5;
		gbc.gridx = 0;
		gbc.gridy = 0;
		JLabel jlabelLogo = new JLabel("Logo");
		jlabelLogo.setHorizontalAlignment(JLabel.LEFT);
		jPanel.add(jlabelLogo, gbc);
		
		ImageIcon icon = getImageIconFromFile(Constants.IMG_FOLDER + "desitin_logo.png");
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 2.5;
		gbc.gridx = 1;
		gbc.gridy = 0;
		mButtonLogo = new JButton(icon);
		mButtonLogo.setPreferredSize(new Dimension(128, 128));
		mButtonLogo.setBackground(new Color(255,255,255));
		mButtonLogo.setBorder(new CompoundBorder(
				new LineBorder(new Color(255,255,255)), new EmptyBorder(0,3,0,0)));		
		jPanel.add(mButtonLogo, gbc);
		
		mButtonLogo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createFileChooser();
			}
		});
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		// Insets(int top, int left, int bottom, int right)		
		gbc.insets = new Insets(16,0,0,0);
		gbc.weightx = 0.5;
		gbc.gridx = 0;
		gbc.gridy = 1;
		JLabel jlabelLiefer = new JLabel("Lieferadresse");
		jlabelLiefer.setHorizontalAlignment(JLabel.LEFT);
		jPanel.add(jlabelLiefer, gbc);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 2.5;
		gbc.gridx = 1;
		gbc.gridy = 1;				
		JTextArea jtextFieldLiefer = new JTextArea();
		jtextFieldLiefer.setPreferredSize(new Dimension(128, 128));
		jtextFieldLiefer.setMargin(new Insets(10,10,10,10));
		jPanel.add(jtextFieldLiefer, gbc);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(16,0,0,0);		
		gbc.weightx = 0.5;
		gbc.gridx = 0;
		gbc.gridy = 2;		
		JLabel jlabelRechnung = new JLabel("Rechnungsadresse");
		jlabelRechnung.setHorizontalAlignment(JLabel.LEFT);
		jPanel.add(jlabelRechnung, gbc);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 2.5;
		gbc.gridx = 1;
		gbc.gridy = 2;		
		JTextArea jtextFieldRechnung = new JTextArea();
		jtextFieldRechnung.setPreferredSize(new Dimension(128, 128));
		jtextFieldRechnung.setMargin(new Insets(10,10,10,10));
		jPanel.add(jtextFieldRechnung, gbc);
				
		return jPanel;
	}
	
	private ImageIcon getImageIconFromFile(String filename) {
		ImageIcon imgIcon = null;
		try {
			Image img = null;
			BufferedImage myLogo = ImageIO.read(new File(filename));
			int width = myLogo.getWidth();
			int height = myLogo.getHeight();
			float f = 1.0f;
			if (width>128)
				f = width/128.0f;
			else if (height>128)
				f = height/128.0f;
			img = myLogo.getScaledInstance((int)(width/f), (int)(height/f), java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
			imgIcon = new ImageIcon(img);
		} catch (IOException e) {
			// Do nothing...
		}
		return imgIcon;
	}
	
	private void createFileChooser() {		
		if (mFc!=null) {
			mFc.setFileFilter(new FileFilter() {
				public boolean accept(File f) {
					return (f.getName().toLowerCase().endsWith(".png") ||
							f.getName().toLowerCase().endsWith(".jpg") ||
							f.isDirectory());
				}
				public String getDescription() {
					return "Logos";
				}
			});
					
			int r = mFc.showOpenDialog(mFrame);
			if (r==JFileChooser.APPROVE_OPTION) {
				// File file = fc.getSelectedFile();
				String filename = mFc.getSelectedFile().getPath();	
				ImageIcon icon = getImageIconFromFile(filename);				
				mButtonLogo.setIcon(icon);
				System.out.println("SettingsPage - opening " + filename);				
			} else {
				System.out.println("SettingsPage - open command cancelled by the user...");
			}
		}
	}
}
