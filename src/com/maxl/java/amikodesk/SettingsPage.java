/*
Copyright (c) 2014 Max Lungarella <cybrmx@gmail.com>

This file is part of AmiKoDesk for Windows.

AmiKoDesk is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package com.maxl.java.amikodesk;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.TreeMap;
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
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import com.maxl.java.shared.User;

public class SettingsPage extends JDialog implements java.io.Serializable {

	private static String UpdateID = "update";
	private static String ComparisonID = "update-comp";
	private static String LogoImageID = "logo";
	private static String GLNCodeID = "glncode";
	private static String HumanID = "ishuman";
	private static String UserID = "user";
	private static String TypeID = "type"; 
	private static String BestellAdresseID = "bestelladresse";
	private static String LieferAdresseID = "lieferadresse";
	private static String RechnungsAdresseID = "rechnungsadresse";
	private static String EmailAdresseID = "emailadresse";
	private static String PhoneNumberID = "phonenumber";
	
	private Map<String, User> m_user_map = null;;
	private User m_user = null;
	
	private JFrame mFrame = null;
	private JFileChooser mFc = null;
	private JButton mButtonLogo = null;
	private Preferences mPrefs = null;
	private JTextField mTextFieldGLN = null;	
	private JTextArea mTextAreaBestell = null;
	private JTextArea mTextAreaLiefer = null;
	private JTextArea mTextAreaRechnung = null;
	private JTextField mTextFieldEmail = null;
	private JTextField mTextFieldPhone = null;
	
	private AddressPanel mShippingAddress = null;	// Lieferadresse
	private AddressPanel mBillingAddress = null;	// Rechnungsadresse
	private AddressPanel mOfficeAddress = null;		// Bestelladresse
	
	// Colors
	private static Color color_white = new Color(255,255,255);
	private static Color color_ok = new Color(220,255,220);
	// private static Color color_green = new Color(220,255,220);
	private static Color color_red = new Color(255,220,220);
	
	private static ResourceBundle m_rb;
	
	private Observer m_observer;
	
	/*
	 * This classed is defined as static to allow serialization.
	 * A private class will not be serialized and a ClassNotFoundException would be thrown.
	 */
	static class Address implements java.io.Serializable {
		
		private static final long serialVersionUID = 1L;
		
		String title = "";
		String fname = "";
		String lname = "";
		String street = "";
		String zip = "";
		String city = "";
		String email = "";
		String phone = "";
		boolean isHuman = true;
		
		public Address() {
			// Struct
		}
		
		/**
		 * Always treat de-serialization as a full-blown constructor, by validating
		 * the final state of the de-serialized object.
		 */
		private void readObject(ObjectInputStream ois)
				throws ClassNotFoundException, IOException {
			// always perform the default de-serialization first
			ois.defaultReadObject();
		}

		/**
		 * This is the default implementation of writeObject. Customise if necessary.
		 */
		private void writeObject(ObjectOutputStream oos)
				throws IOException {
			// perform the default serialization for all non-transient, non-static fields
			oos.defaultWriteObject();
		}
	}
	
	private class AddressPanel extends JPanel {
		
		JTextField aTextFieldTitle = null;
		JTextField aTextFieldFName = null;
		JTextField aTextFieldLName = null;
		JTextField aTextFieldAddress = null;
		JTextField aTextFieldZip = null;
		JTextField aTextFieldCity = null;
		JTextField aTextFieldEmail = null;
		JTextField aTextFieldPhone = null;
		
		JLabel aLabelTitle = null;
		JLabel aLabelFName = null;
		JLabel aLabelLName = null;
		
		private int pad_left = 8;
		
		private String m_address_type = "";
		
		public AddressPanel(String address_type) {

			m_address_type = address_type;

			String title = "";
			if (address_type.equals("S"))
				title = "Lieferadresse";
			else if (address_type.equals("B"))
				title = "Rechnungsadresse";
			else if (address_type.equals("O"))
				title = "Bestelladresse";
			
			setLayout(new GridBagLayout());
			setOpaque(false);
			setBorder(new CompoundBorder(
					new TitledBorder(title),
					new EmptyBorder(5, 5, 5, 5)));		
			GridBagConstraints gbc = new GridBagConstraints();		
			gbc.insets = new Insets(5, 5, 5, 5);
			
			/*  NOTE
				weightx is 1.0 for fields, 0.0 for labels
	        	gridwidth is REMAINDER for fields, 1 for labels
			*/
			
			// -----------------------------
			aLabelTitle = new JLabel("Title");
			aLabelTitle.setHorizontalAlignment(JLabel.LEFT);
			gbc = getGbc(0,0, 0.1,1.0, GridBagConstraints.HORIZONTAL);
			gbc.gridwidth = 1;
			this.add(aLabelTitle, gbc);
			
			aTextFieldTitle = new JTextField("");
			aTextFieldTitle.setBorder(new CompoundBorder(new LineBorder(color_white), new EmptyBorder(0,0,0,0)));
			gbc = getGbc(1,0 ,1.0,1.0, GridBagConstraints.HORIZONTAL);	
			gbc.gridwidth = 1;
			this.add(aTextFieldTitle, gbc);
		
			// -----------------------------
			if (!address_type.equals("S")) {
				final JCheckBox jcheckAddress = new JCheckBox("Lieferadresse übernehmen");
				jcheckAddress.setBorder(new EmptyBorder(new Insets(1,1,1,1)));
				jcheckAddress.setHorizontalAlignment(JLabel.LEFT);
				gbc = getGbc(4,0, 0.1,1.0, GridBagConstraints.HORIZONTAL);
				gbc.gridwidth = 2;
				this.add(jcheckAddress, gbc);
				
				jcheckAddress.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent e) {
						if (jcheckAddress.isSelected())
							copyDataFromShippingPanel();
						else
							clearData();
					}
				});
			}
			
			// -----------------------------
			aLabelFName = new JLabel("Vorname");
			aLabelFName.setHorizontalAlignment(JLabel.LEFT);
			gbc = getGbc(0,1, 0.1,1.0, GridBagConstraints.HORIZONTAL);
			gbc.gridwidth = 1;
			this.add(aLabelFName, gbc);
			
			aTextFieldFName = new JTextField("");
			aTextFieldFName.setBorder(new CompoundBorder(new LineBorder(color_white), new EmptyBorder(0,0,0,0)));			
			gbc = getGbc(1,1 ,1.0,1.0, GridBagConstraints.HORIZONTAL);		
			gbc.gridwidth = 2;
			this.add(aTextFieldFName, gbc);

			aLabelLName = new JLabel("Name");
			aLabelLName.setHorizontalAlignment(JLabel.LEFT);	
			aLabelLName.setBorder(new EmptyBorder(0,pad_left,0,0));		
			gbc = getGbc(3,1, 0.1,1.0, GridBagConstraints.HORIZONTAL);
			gbc.gridwidth = 1;
			this.add(aLabelLName, gbc);
			
			aTextFieldLName = new JTextField("");
			aTextFieldLName.setBorder(new CompoundBorder(new LineBorder(color_white), new EmptyBorder(0,0,0,0)));			
			gbc = getGbc(4,1 ,1.0,1.0, GridBagConstraints.HORIZONTAL);	
			gbc.gridwidth = 2;
			this.add(aTextFieldLName, gbc);
			
			// -----------------------------
			JLabel jlabelAddress = new JLabel("Strasse");
			jlabelAddress.setHorizontalAlignment(JLabel.LEFT);
			gbc = getGbc(0,2, 0.1,1.0, GridBagConstraints.HORIZONTAL);
			gbc.gridwidth = 1;			
			this.add(jlabelAddress, gbc);
			
			aTextFieldAddress = new JTextField("");
			aTextFieldAddress.setBorder(new CompoundBorder(new LineBorder(color_white), new EmptyBorder(0,0,0,0)));			
			gbc = getGbc(1,2 ,1.0,1.0, GridBagConstraints.HORIZONTAL);		
			gbc.gridwidth = 5;
			this.add(aTextFieldAddress, gbc);
			
			// -----------------------------
			JLabel jlabelZip = new JLabel("PLZ");
			jlabelZip.setHorizontalAlignment(JLabel.LEFT);
			gbc = getGbc(0,3, 0.1,1.0, GridBagConstraints.HORIZONTAL);
			gbc.gridwidth = 1;			
			this.add(jlabelZip, gbc);
			
			aTextFieldZip = new JTextField("");
			aTextFieldZip.setBorder(new CompoundBorder(new LineBorder(color_white), new EmptyBorder(0,0,0,0)));			
			gbc = getGbc(1,3 ,1.0,1.0, GridBagConstraints.HORIZONTAL);	
			gbc.gridwidth = 2;
			this.add(aTextFieldZip, gbc);

			JLabel jlabelCity = new JLabel("Ort");
			jlabelCity.setBorder(new EmptyBorder(0,pad_left,0,0));		
			jlabelCity.setHorizontalAlignment(JLabel.LEFT);			
			gbc = getGbc(3,3, 0.1,1.0, GridBagConstraints.HORIZONTAL);
			gbc.gridwidth = 1;			
			this.add(jlabelCity, gbc);
			
			aTextFieldCity = new JTextField("");
			aTextFieldCity.setBorder(new CompoundBorder(new LineBorder(color_white), new EmptyBorder(0,0,0,0)));				
			gbc = getGbc(4,3 ,1.0,1.0, GridBagConstraints.HORIZONTAL);	
			gbc.gridwidth = 2;
			this.add(aTextFieldCity, gbc);
			
			// -----------------------------
			JLabel jlabelPhone = new JLabel("Telefon");
			jlabelPhone.setHorizontalAlignment(JLabel.LEFT);
			gbc = getGbc(0,4, 0.1,1.0, GridBagConstraints.HORIZONTAL);
			gbc.gridwidth = 1;			
			this.add(jlabelPhone, gbc);
			
			aTextFieldPhone = new JTextField("");
			aTextFieldPhone.setBorder(new CompoundBorder(new LineBorder(color_white), new EmptyBorder(0,0,0,0)));			
			gbc = getGbc(1,4 ,1.0,1.0, GridBagConstraints.HORIZONTAL);	
			gbc.gridwidth = 2;
			this.add(aTextFieldPhone, gbc);

			JLabel jlabelEmail = new JLabel("Email");
			jlabelEmail.setHorizontalAlignment(JLabel.LEFT);
			jlabelEmail.setBorder(new EmptyBorder(0,pad_left,0,0));		
			gbc = getGbc(3,4, 0.1,1.0, GridBagConstraints.HORIZONTAL);
			gbc.gridwidth = 1;			
			this.add(jlabelEmail, gbc);
			
			aTextFieldEmail = new JTextField("");
			aTextFieldEmail.setBorder(new CompoundBorder(new LineBorder(color_white), new EmptyBorder(0,0,0,0)));			
			gbc = getGbc(4,4 ,1.0,1.0, GridBagConstraints.HORIZONTAL);	
			gbc.gridwidth = 2;
			this.add(aTextFieldEmail, gbc);
		}
		
		void setNameLabels(String title, String fname, String lname) {
			aLabelTitle.setText(title);
			aLabelFName.setText(fname);
			aLabelLName.setText(lname);
		}
		
		void setDataWithUserInfo(User u, boolean is_human) {
			if (is_human) {
				aTextFieldTitle.setText(u.title);
				aTextFieldFName.setText(u.first_name);
				aTextFieldLName.setText(u.last_name);
			} else {
				aTextFieldTitle.setText(u.name1);
				aTextFieldFName.setText(u.name2);
				aTextFieldLName.setText(u.name3);
			}
			aTextFieldAddress.setText(u.street);
			aTextFieldZip.setText(u.zip);
			aTextFieldCity.setText(u.city);
			aTextFieldPhone.setText(u.phone);
			aTextFieldEmail.setText(u.email);
		}
		
		void clearData() {
			aTextFieldTitle.setText("");
			aTextFieldFName.setText("");
			aTextFieldLName.setText("");
			aTextFieldAddress.setText("");
			aTextFieldZip.setText("");
			aTextFieldCity.setText("");
			aTextFieldPhone.setText("");
			aTextFieldEmail.setText("");
		}
		
		void copyDataFromShippingPanel() {
			aTextFieldTitle.setText(mShippingAddress.aTextFieldTitle.getText());
			aTextFieldFName.setText(mShippingAddress.aTextFieldFName.getText());
			aTextFieldLName.setText(mShippingAddress.aTextFieldLName.getText());
			aTextFieldAddress.setText(mShippingAddress.aTextFieldAddress.getText());
			aTextFieldZip.setText(mShippingAddress.aTextFieldZip.getText());
			aTextFieldCity.setText(mShippingAddress.aTextFieldCity.getText());
			aTextFieldPhone.setText(mShippingAddress.aTextFieldPhone.getText());
			aTextFieldEmail.setText(mShippingAddress.aTextFieldEmail.getText());
		}
		void storeDataToPreferences(boolean is_human) {
			Address addr = new Address();
			addr.title = aTextFieldTitle.getText();
			addr.fname = aTextFieldFName.getText();
			addr.lname = aTextFieldLName.getText();
			addr.street = aTextFieldAddress.getText();
			addr.zip = aTextFieldZip.getText();
			addr.city = aTextFieldCity.getText();
			addr.phone = aTextFieldPhone.getText();
			addr.email = aTextFieldEmail.getText();
			addr.isHuman = is_human;
			// Store addr to preferences
			byte[] arr = FileOps.serialize(addr);
			if (m_address_type.equals("S"))
				mPrefs.putByteArray(LieferAdresseID, arr);
			else if (m_address_type.equals("B"))
				mPrefs.putByteArray(RechnungsAdresseID, arr);
			else if (m_address_type.equals("O"))
				mPrefs.putByteArray(BestellAdresseID, arr);
		}
		
		void retrieveDataFromPreferences() {
			Address addr = new Address();
			// Default entries... empty
			byte[] def = FileOps.serialize(addr);	
			if (m_address_type.equals("S")) {
				byte[] arr = mPrefs.getByteArray(LieferAdresseID, def);
				if (arr!=null)
					addr = (Address)FileOps.deserialize(arr);
			} else if (m_address_type.equals("B")) {
				byte[] arr = mPrefs.getByteArray(RechnungsAdresseID, def);
				if (arr!=null)
					addr = (Address)FileOps.deserialize(arr);
			} else if (m_address_type.equals("O")) {
				byte[] arr = mPrefs.getByteArray(BestellAdresseID, def);
				if (arr!=null)
					addr = (Address)FileOps.deserialize(arr);
			}
			// Fill all fields
			aTextFieldTitle.setText(addr.title);
			aTextFieldFName.setText(addr.fname);
			aTextFieldLName.setText(addr.lname);
			aTextFieldAddress.setText(addr.street);
			aTextFieldZip.setText(addr.zip);
			aTextFieldCity.setText(addr.city);
			aTextFieldPhone.setText(addr.phone);
			aTextFieldEmail.setText(addr.email);
			// Set labels
			if (!addr.isHuman) {
				aLabelTitle.setText("Name 1");
				aLabelFName.setText("Name 2");
				aLabelLName.setText("Name 3");
			}
		}
	}
	
	public SettingsPage(JFrame frame, ResourceBundle rb) {
		mFrame = frame;
		m_rb = rb;
		mFc = new JFileChooser();
		// Defines a node in which the preferences can be stored
		mPrefs = Preferences.userRoot().node(this.getClass().getName());
		
		// Load gln codes file and create map
		load_gln_codes();
		/* --- old code ---
		String m_application_data_folder = Utilities.appDataFolder();
		m_user_map = readFromCsvToMap(m_application_data_folder + "\\" + Constants.GLN_CODES_FILE);
		// Read default file
		if (m_user_map==null) {
			m_user_map = readFromCsvToMap("./shop/" + Constants.GLN_CODES_FILE);
			System.out.println("Loading gln codes from default folder...");
		}
		*/
		this.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		add(Box.createRigidArea(new Dimension(0, 10)));		
		JPanel jplInnerPanel1 = globalAmiKoSettings();
		this.add(jplInnerPanel1);
		
		if (Utilities.appCustomization().equals("zurrose")) {
			add(Box.createRigidArea(new Dimension(0, 10)));		
			JPanel jplInnerPanel2 = comparisonSettings();
			this.add(jplInnerPanel2);
		}
		
		add(Box.createRigidArea(new Dimension(0, 10)));		
		if (Utilities.appCustomization().equals("ywesee")) {	// IBSA
			JPanel jplInnerPanel3 = shoppingBasketSettings2();
			this.add(jplInnerPanel3);
		} else {
			JPanel jplInnerPanel3 = shoppingBasketSettings();
			this.add(jplInnerPanel3);
		}
		
		add(Box.createRigidArea(new Dimension(0, 10)));
		
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setTitle(m_rb.getString("settings"));		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		// Centers the dialog
		this.setLocationRelativeTo(null);
		// Set size
		if (Utilities.appCustomization().equals("ywesee"))	// IBSA
			this.setSize(600, 800);		
		else
			this.setSize(512, 680);
		this.setResizable(false);
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				if (Utilities.appCustomization().equals("ywesee")) {	// IBSA
					if (m_user!=null) {
						mShippingAddress.storeDataToPreferences(m_user.is_human);
						mBillingAddress.storeDataToPreferences(m_user.is_human);
						mOfficeAddress.storeDataToPreferences(m_user.is_human);
					}
				} else {
					String address = mTextAreaBestell.getText();
					if (address!=null)
						mPrefs.put(BestellAdresseID, address);
					address = mTextAreaLiefer.getText();
					if (address!=null)
						mPrefs.put(LieferAdresseID, address);
					address = mTextAreaRechnung.getText();
					if (address!=null)
						mPrefs.put(RechnungsAdresseID, address);
				}
			}
		});
	}
	
	public void display() {
		// Visualize
		this.setVisible(true);
	}
	
	public void addObserver(Observer observer) {
		m_observer = observer;
	}
	
	protected void notify(String str) {
		m_observer.update(null, str);
	}	
	
	protected JPanel globalAmiKoSettings() {
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new GridLayout(1, 4));
		
		ButtonGroup bg = new ButtonGroup();
		
		jPanel.setOpaque(false);
		jPanel.setBorder(new CompoundBorder(
				new TitledBorder(m_rb.getString("data-update")),
				new EmptyBorder(5,5,5,5)));		
		
		JCheckBox updateManualCBox = new JCheckBox(m_rb.getString("manual"));
		JCheckBox updateDailyCBox = new JCheckBox(m_rb.getString("daily"));
		JCheckBox updateWeeklyCBox = new JCheckBox(m_rb.getString("weekly"));
		JCheckBox updateMonthlyCBox = new JCheckBox(m_rb.getString("monthly"));		
		
		// Add to buttongroup to ensure that only one box is selected at a time
		bg.add(updateManualCBox);
		bg.add(updateDailyCBox);
		bg.add(updateWeeklyCBox);
		bg.add(updateMonthlyCBox);

		// Retrieve update frequency from preferences...
		// Default: manual update
		switch(mPrefs.getInt(UpdateID, 0)) {
		case 0:
			updateManualCBox.setSelected(true);
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
		
		updateManualCBox.addItemListener(new ItemListener() {
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
		jPanel.add(updateManualCBox);
		jPanel.add(updateDailyCBox);		
		jPanel.add(updateWeeklyCBox);
		jPanel.add(updateMonthlyCBox);
		
		return jPanel;
	}
	
	protected JPanel comparisonSettings() {
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new GridLayout(1, 4));
		
		ButtonGroup bg = new ButtonGroup();
		
		jPanel.setOpaque(false);
		jPanel.setBorder(new CompoundBorder(
				new TitledBorder(m_rb.getString("comp-update")),
				new EmptyBorder(5,5,5,5)));		
		
		JCheckBox updateManualCBox = new JCheckBox(m_rb.getString("manual"));
		JCheckBox updateHalfHourlyCBox = new JCheckBox(m_rb.getString("half-hourly"));
		JCheckBox updateHourlyCBox = new JCheckBox(m_rb.getString("hourly"));	
		JCheckBox updateHalfDailyCBox = new JCheckBox(m_rb.getString("half-daily"));
		
		// Add to buttongroup to ensure that only one box is selected at a time
		bg.add(updateManualCBox);
		bg.add(updateHalfHourlyCBox);
		bg.add(updateHourlyCBox);
		bg.add(updateHalfDailyCBox);

		// Retrieve update frequency from preferences...
		// Default: manual update
		switch(mPrefs.getInt(ComparisonID, 0)) {
		case 0:
			updateManualCBox.setSelected(true);
			break;
		case 1:
			updateHalfHourlyCBox.setSelected(true);
			break;			
		case 2:
			updateHourlyCBox.setSelected(true);
			break;			
		case 3:
			updateHalfDailyCBox.setSelected(true);
			break;
		default:
			break;
		}
		
		updateManualCBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				mPrefs.putInt(ComparisonID, 0);
			}
		});
		updateHalfHourlyCBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				mPrefs.putInt(ComparisonID, 1);
			}
		});	
		updateHourlyCBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				mPrefs.putInt(ComparisonID, 2);
			}
		});	
		updateHalfDailyCBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				mPrefs.putInt(ComparisonID, 3);
			}
		});	
		
		jPanel.add(updateManualCBox);
		jPanel.add(updateHalfHourlyCBox);		
		jPanel.add(updateHourlyCBox);
		jPanel.add(updateHalfDailyCBox);
		
		return jPanel;
	}
	
	private void delete_shopping_carts() {
		System.out.println("User type changed...");
		for (int index=1; index<6; ++index) {
			File file = new File(Utilities.appDataFolder() + "\\shop\\korb" + index + ".ser");
			if (file.exists()) {		
				file.delete();
			    notify("# Deleted shopping cart " + index); 	// Notify GUI   
			}
		}
	}
	
	protected JPanel shoppingBasketSettings() {
		String GLNCodeStr = mPrefs.get(GLNCodeID, "7610000000000");
		String bestellAdrStr = mPrefs.get(BestellAdresseID, m_rb.getString("noaddress1"));
		String lieferAdrStr = mPrefs.get(LieferAdresseID, m_rb.getString("noaddress2"));
		String rechnungsAdrStr = mPrefs.get(RechnungsAdresseID, m_rb.getString("noaddress3"));
		String EmailStr = mPrefs.get(EmailAdresseID, "");
		String PhoneNumberStr = mPrefs.get(PhoneNumberID, "+41-");
		
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();		
		
		jPanel.setOpaque(false);
		jPanel.setBorder(new CompoundBorder(
				new TitledBorder(m_rb.getString("shoppingCart")), new EmptyBorder(5,5,5,5)));		
		
		// -----------------------------
		JLabel jlabelLogo = new JLabel("Logo");
		jlabelLogo.setHorizontalAlignment(JLabel.LEFT);
		gbc = getGbc(0,0, 0.5,1.0, GridBagConstraints.HORIZONTAL);		
		jPanel.add(jlabelLogo, gbc);
		
		String logoImageStr = mPrefs.get(LogoImageID, Constants.IMG_FOLDER + "empty_logo.png");	
		File logoFile = new File(logoImageStr);
		if (!logoFile.exists())
			logoImageStr = Constants.IMG_FOLDER + "empty_logo.png";
		ImageIcon icon = getImageIconFromFile(logoImageStr);
		mButtonLogo = new JButton(icon);
		mButtonLogo.setPreferredSize(new Dimension(128, 128));
		mButtonLogo.setMargin(new Insets(10,10,10,10));
		if (!logoFile.exists())
			mButtonLogo.setBackground(color_red);
		else
			mButtonLogo.setBackground(color_white);
		mButtonLogo.setBorder(new CompoundBorder(
				new LineBorder(color_white), new EmptyBorder(0,3,0,0)));	
		gbc = getGbc(1,0, 2.5,1.0, GridBagConstraints.HORIZONTAL);		
		jPanel.add(mButtonLogo, gbc);
		
		mButtonLogo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createFileChooser();
			}
		});
		
		// -----------------------------
		JLabel jlabelGLN = new JLabel("GLN Code*");
		jlabelGLN.setHorizontalAlignment(JLabel.LEFT);
		gbc = getGbc(0,1, 0.5,1.0, GridBagConstraints.HORIZONTAL);		
		jPanel.add(jlabelGLN, gbc);
		
		mTextFieldGLN = new JTextField(GLNCodeStr);
		if (!GLNCodeStr.matches("[\\d]{13}")) {
			mTextFieldGLN.setBorder(new LineBorder(color_red, 5, false));
			mTextFieldGLN.setBackground(color_red);
		} else {
			mTextFieldGLN.setBorder(new LineBorder(color_ok, 5, false));
			mTextFieldGLN.setBackground(color_ok);
		}
			
		gbc = getGbc(1,1 ,2.5,1.0, GridBagConstraints.HORIZONTAL);		
		jPanel.add(mTextFieldGLN, gbc);
		
		mTextFieldGLN.addKeyListener(new KeyListener() { 
			@Override 
			public void keyPressed(KeyEvent keyEvent) {
				//
			}
			@Override
			public void keyReleased(KeyEvent keyEvent) {
			// public void actionPerformed(ActionEvent e) {
				String mGLNCodeStr = mTextFieldGLN.getText();
				if (mGLNCodeStr.matches("[\\d]{13}")) {
					mPrefs.put(GLNCodeID, mGLNCodeStr);
					m_user = m_user_map.get(mGLNCodeStr);
					if (m_user!=null) {
						m_user.is_human = userIsHuman(m_user.category);
						if (m_user.is_human)
							mPrefs.put(HumanID, "yes");	
						else 
							mPrefs.put(HumanID, "no");
						String old_user_type = mPrefs.get(TypeID, "");
						String new_user_type = m_user.category.toLowerCase();
						mPrefs.put(TypeID, new_user_type);
						mPrefs.putInt(UserID, 17);	// Default
						if (m_user.is_human)
							System.out.println("Person: " + m_user.gln_code + " - " + m_user.category + ", " + m_user.first_name + ", " + m_user.last_name);
						else
							System.out.println("Company: " + m_user.gln_code + " - " + m_user.category + ", " + m_user.name1 + ", " + m_user.name2);
						mTextFieldGLN.setBorder(new LineBorder(color_ok, 5, false));
						mTextFieldGLN.setBackground(color_ok);
						String address = "";
						if (m_user.is_human) {
							if (!m_user.street.isEmpty()) {
								address = "Dr. med. " + m_user.first_name + " " + m_user.last_name + "\n" 
									+ m_user.street + "\n"
									+ m_user.zip + " " + m_user.city + "\n"
									+ "Schweiz";
							} else {
								address = "Dr. med. " + m_user.first_name + " " + m_user.last_name + "\n" 
									+ "***Strasse fehlt***" + "\n"
									+ m_user.zip + " " + m_user.city + "\n"
									+ "Schweiz";
							}
						} else {
							address = m_user.name1 + "\n" 
								+ m_user.name2 + "\n" 
								+ m_user.street + "\n"
								+ m_user.zip + " " + m_user.city + "\n"
								+ "Schweiz";
						}
						mTextAreaBestell.setText(address);
						mTextAreaLiefer.setText(address);
						mTextAreaRechnung.setText(address);
						if (m_user.street.isEmpty()) {
							mTextAreaBestell.setBorder(new LineBorder(color_red, 5, false));
							mTextAreaBestell.setBackground(color_red);
							mTextAreaLiefer.setBorder(new LineBorder(color_red, 5, false));
							mTextAreaLiefer.setBackground(color_red);
							mTextAreaRechnung.setBorder(new LineBorder(color_red, 5, false));
							mTextAreaRechnung.setBackground(color_red);							
						}						
						// If old user and new user do not match, delete ALL shopping carts
						if (!old_user_type.equals(new_user_type))
							delete_shopping_carts();
						return;
					} else {
						// Innendienst identification
						if (Utilities.appCustomization().equals("zurrose")) {
							m_user = new User();
							m_user.gln_code = mGLNCodeStr;
						}
					}
				}
				mTextFieldGLN.setBorder(new LineBorder(color_red, 5, false));
				mTextFieldGLN.setBackground(color_red);
				mTextAreaBestell.setText("");
				mTextAreaLiefer.setText("");
				mTextAreaRechnung.setText("");
			}
			@Override 
			public void keyTyped(KeyEvent keyEvent) {
				//
			}
		});
		
		// -----------------------------
		JLabel jlabelBestell = new JLabel(m_rb.getString("ordaddress") + "*");		
		jlabelBestell.setHorizontalAlignment(JLabel.LEFT);
		gbc = getGbc(0,2,0.5,1.0,GridBagConstraints.HORIZONTAL);		
		jPanel.add(jlabelBestell, gbc);
		
		mTextAreaBestell = new JTextArea(bestellAdrStr);	
		validateAddress(mTextAreaBestell);
		mTextAreaBestell.setPreferredSize(new Dimension(128, 256));
		mTextAreaBestell.setMargin(new Insets(5,5,5,5));
		gbc = getGbc(1,2,3.5,1.0,GridBagConstraints.HORIZONTAL);
		jPanel.add(mTextAreaBestell, gbc);
		
		mTextAreaBestell.addKeyListener(new KeyListener() { 
			@Override 
			public void keyPressed(KeyEvent keyEvent) {
				//
			}
			@Override
			public void keyReleased(KeyEvent keyEvent) {
				// Validate email addres: do a quick sanity check on the email address
				validateAddress(mTextAreaBestell);
			}
			@Override 
			public void keyTyped(KeyEvent keyEvent) {
				//
			}
		});
		
		// -----------------------------
		JLabel jlabelLiefer = new JLabel(m_rb.getString("shipaddress") + "*");
		jlabelLiefer.setHorizontalAlignment(JLabel.LEFT);
		gbc = getGbc(0,3,0.5,1.0,GridBagConstraints.HORIZONTAL);
		jPanel.add(jlabelLiefer, gbc);

		mTextAreaLiefer = new JTextArea(lieferAdrStr);
		validateAddress(mTextAreaLiefer);		
		mTextAreaLiefer.setPreferredSize(new Dimension(128, 128));
		mTextAreaLiefer.setMargin(new Insets(5,5,5,5));
		gbc = getGbc(1,3,2.5,1.0,GridBagConstraints.HORIZONTAL);
		jPanel.add(mTextAreaLiefer, gbc);
		
		mTextAreaLiefer.addKeyListener(new KeyListener() { 
			@Override 
			public void keyPressed(KeyEvent keyEvent) {
				//
			}
			@Override
			public void keyReleased(KeyEvent keyEvent) {
				// Validate email addres: do a quick sanity check on the email address
				validateAddress(mTextAreaLiefer);
			}
			@Override 
			public void keyTyped(KeyEvent keyEvent) {
				//
			}
		});
		
		// -----------------------------
		JLabel jlabelRechnung = new JLabel(m_rb.getString("billaddress") + "*");
		jlabelRechnung.setHorizontalAlignment(JLabel.LEFT);
		gbc = getGbc(0,4,0.5,1.0,GridBagConstraints.HORIZONTAL);
		jPanel.add(jlabelRechnung, gbc);

		mTextAreaRechnung = new JTextArea(rechnungsAdrStr);
		validateAddress(mTextAreaRechnung);	
		mTextAreaRechnung.setPreferredSize(new Dimension(128, 128));
		mTextAreaRechnung.setMargin(new Insets(5,5,5,5));
		gbc = getGbc(1,4,2.5,1.0,GridBagConstraints.HORIZONTAL);
		jPanel.add(mTextAreaRechnung, gbc);
		
		mTextAreaRechnung.addKeyListener(new KeyListener() { 
			@Override 
			public void keyPressed(KeyEvent keyEvent) {
				//
			}
			@Override
			public void keyReleased(KeyEvent keyEvent) {
				// Validate email addres: do a quick sanity check on the email address
				validateAddress(mTextAreaRechnung);
			}
			@Override 
			public void keyTyped(KeyEvent keyEvent) {
				//
			}
		});
		
		// -----------------------------
		JLabel jlabelEmail = new JLabel(m_rb.getString("emailaddress"));
		jlabelEmail.setHorizontalAlignment(JLabel.LEFT);
		gbc = getGbc(0,5, 0.5,1.0, GridBagConstraints.HORIZONTAL);
		jPanel.add(jlabelEmail, gbc);
		
		mTextFieldEmail = new JTextField(EmailStr);
		validateEmail(EmailStr);
		gbc = getGbc(1,5 ,2.5,1.0, GridBagConstraints.HORIZONTAL);		
		jPanel.add(mTextFieldEmail, gbc);		
		
		mTextFieldEmail.addKeyListener(new KeyListener() { 
			@Override 
			public void keyPressed(KeyEvent keyEvent) {
				//
			}
			@Override
			public void keyReleased(KeyEvent keyEvent) {
				// Validate email addres: do a quick sanity check on the email address
				String mEmailStr = mTextFieldEmail.getText();
				if (validateEmail(mEmailStr))
					mPrefs.put(EmailAdresseID, mEmailStr);			
			}
			@Override 
			public void keyTyped(KeyEvent keyEvent) {
				//
			}
		});
		
		// -----------------------------	
		JLabel jlabelPhone = new JLabel(m_rb.getString("telephone"));
		jlabelPhone.setHorizontalAlignment(JLabel.LEFT);
		gbc = getGbc(0,6, 0.5,1.0, GridBagConstraints.HORIZONTAL);		
		jPanel.add(jlabelPhone, gbc);
		
		mTextFieldPhone = new JTextField(PhoneNumberStr);
		// Validate phone number: do a quick sanity check on the phone number
		validatePhone(PhoneNumberStr);
		gbc = getGbc(1,6 ,2.5,1.0, GridBagConstraints.HORIZONTAL);		
		jPanel.add(mTextFieldPhone, gbc);

		mTextFieldPhone.addKeyListener(new KeyListener() {
			@Override 
			public void keyPressed(KeyEvent keyEvent) {
				//
			}
			@Override
			public void keyReleased(KeyEvent keyEvent) {
				String mPhoneStr = mTextFieldPhone.getText();
				if (validatePhone(mPhoneStr))
					mPrefs.put(PhoneNumberID, mPhoneStr);								
			}
			@Override 
			public void keyTyped(KeyEvent keyEvent) {
				//
			}
		});

		JLabel jlabelFootnote = new JLabel("*" + m_rb.getString("medreg"));
		jlabelFootnote.setFont(new Font("Dialog", Font.ITALIC, 11));
		jlabelFootnote.setHorizontalAlignment(JLabel.LEFT);
		gbc = getGbc(0,7, 0.5,1.0, GridBagConstraints.HORIZONTAL);		
		jPanel.add(jlabelFootnote, gbc);
		
		return jPanel;
	}

	protected JPanel shoppingBasketSettings2() {
		String GLNCodeStr = mPrefs.get(GLNCodeID, "7610000000000");
		
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();		
		
		jPanel.setOpaque(false);
		jPanel.setBorder(new CompoundBorder(
				new TitledBorder(m_rb.getString("shoppingCart")), new EmptyBorder(5,5,5,5)));	
		
		// -----------------------------
		JLabel jlabelGLN = new JLabel("GLN Code*");
		jlabelGLN.setHorizontalAlignment(JLabel.LEFT);
		gbc = getGbc(0,0, 0.1,1.0, GridBagConstraints.HORIZONTAL);		
		jPanel.add(jlabelGLN, gbc);
		
		mTextFieldGLN = new JTextField(GLNCodeStr);
		if (!GLNCodeStr.matches("[\\d]{13}")) {
			mTextFieldGLN.setBorder(new LineBorder(color_red, 5, false));
			mTextFieldGLN.setBackground(color_red);
		} else {
			mTextFieldGLN.setBorder(new LineBorder(color_ok, 5, false));
			mTextFieldGLN.setBackground(color_ok);
		}			
		gbc = getGbc(1,0 ,3.0,1.0, GridBagConstraints.HORIZONTAL);		
		jPanel.add(mTextFieldGLN, gbc);
		
		mTextFieldGLN.addKeyListener(new KeyListener() { 
			@Override 
			public void keyPressed(KeyEvent keyEvent) {
				//
			}
			@Override
			public void keyReleased(KeyEvent keyEvent) {
			// public void actionPerformed(ActionEvent e) {
				String mGLNCodeStr = mTextFieldGLN.getText();
				if (mGLNCodeStr.matches("[\\d]{13}")) {
					mPrefs.put(GLNCodeID, mGLNCodeStr);
					m_user = m_user_map.get(mGLNCodeStr+"S");
					if (m_user!=null) {
						m_user.is_human = userIsHuman(m_user.category);
						if (m_user.is_human)
							mPrefs.put(HumanID, "yes");	
						else 
							mPrefs.put(HumanID, "no");
						String old_user_type = mPrefs.get(TypeID, "");
						String new_user_type = m_user.category.toLowerCase();
						mPrefs.put(TypeID, new_user_type);
						mPrefs.putInt(UserID, 17);	// Default
						
						if (m_user.is_human) {
							mShippingAddress.setNameLabels("Titel", "Vorname", "Name");
							mBillingAddress.setNameLabels("Titel", "Vorname", "Name");
							mOfficeAddress.setNameLabels("Titel", "Vorname", "Name");
							System.out.println("Person: " + m_user.gln_code + " - " + m_user.category + ", " + m_user.title + ", " + m_user.first_name + ", " + m_user.last_name);
						} else {
							mShippingAddress.setNameLabels("Name 1", "Name 2", "Name 3");
							mBillingAddress.setNameLabels("Name 1", "Name 2", "Name 3");
							mOfficeAddress.setNameLabels("Name 1", "Name 2", "Name 3");						
							System.out.println("Company: " + m_user.gln_code + " - " + m_user.category + ", " + m_user.name1 + ", " + m_user.name2 + ", " + m_user.name3);
						}
						
						mTextFieldGLN.setBorder(new LineBorder(color_ok, 5, false));
						mTextFieldGLN.setBackground(color_ok);						
					
						mShippingAddress.setDataWithUserInfo(m_user, m_user.is_human);
						if (m_user_map.containsKey(mGLNCodeStr+"B")) {
							m_user = m_user_map.get(mGLNCodeStr+"B");
							if (m_user!=null) {
								m_user.is_human = userIsHuman(m_user.category);
								mBillingAddress.setDataWithUserInfo(m_user, m_user.is_human);
							}
						} 
						if (m_user_map.containsKey(mGLNCodeStr+"O")) {
							m_user = m_user_map.get(mGLNCodeStr+"O");							
							if (m_user!=null) {							
								m_user.is_human = userIsHuman(m_user.category);
								mOfficeAddress.setDataWithUserInfo(m_user, m_user.is_human);							
							}
						}
						
						// If old user and new user do not match, delete ALL shopping carts
						if (!old_user_type.equals(new_user_type))
							delete_shopping_carts();
						return;
					} else {
						// Innendienst identification
						if (Utilities.appCustomization().equals("zurrose")) {
							m_user = new User();
							m_user.gln_code = mGLNCodeStr;
						}
					}
				}
			}
			@Override 
			public void keyTyped(KeyEvent keyEvent) {
				//
			}
		});
		
		// -----------------------------
		JLabel jlabelLogo = new JLabel("Logo");
		jlabelLogo.setHorizontalAlignment(JLabel.LEFT);
		jlabelLogo.setBorder(new EmptyBorder(0,8,0,0));			
		gbc = getGbc(2,0, 0.1,1.0, GridBagConstraints.HORIZONTAL);	
		jPanel.add(jlabelLogo, gbc);
		
		String logoImageStr = mPrefs.get(LogoImageID, Constants.IMG_FOLDER + "empty_logo.png");	
		File logoFile = new File(logoImageStr);
		if (!logoFile.exists())
			logoImageStr = Constants.IMG_FOLDER + "empty_logo.png";
		ImageIcon icon = getImageIconFromFile(logoImageStr);
		mButtonLogo = new JButton(icon);
		mButtonLogo.setPreferredSize(new Dimension(128, 128));
		mButtonLogo.setMargin(new Insets(10,10,10,10));
		if (!logoFile.exists())
			mButtonLogo.setBackground(color_red);
		else
			mButtonLogo.setBackground(color_white);
		mButtonLogo.setBorder(new CompoundBorder(
				new LineBorder(color_white), new EmptyBorder(0,3,0,0)));	
		gbc = getGbc(3,0, 3.0,1.0, GridBagConstraints.HORIZONTAL);		
		jPanel.add(mButtonLogo, gbc);
		
		mButtonLogo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createFileChooser();
			}
		});

		// -----------------------------
		gbc = getGbc(0,1, 1.0,1.0, GridBagConstraints.HORIZONTAL);		
		gbc.gridwidth = 4;
		mShippingAddress = new AddressPanel("S");
		mShippingAddress.retrieveDataFromPreferences();
		jPanel.add(mShippingAddress, gbc);

		// -----------------------------
		gbc = getGbc(0,2, 1.0,1.0, GridBagConstraints.HORIZONTAL);		
		gbc.gridwidth = 4;		
		mBillingAddress = new AddressPanel("B");
		mBillingAddress.retrieveDataFromPreferences();		
		jPanel.add(mBillingAddress, gbc);

		// -----------------------------
		gbc = getGbc(0,3, 1.0,1.0, GridBagConstraints.HORIZONTAL);				
		gbc.gridwidth = 4;		
		mOfficeAddress = new AddressPanel("O");
		mOfficeAddress.retrieveDataFromPreferences();		
		jPanel.add(mOfficeAddress, gbc);

		// -----------------------------
		JLabel jlabelFootnote = new JLabel("*" + m_rb.getString("medreg"));
		jlabelFootnote.setFont(new Font("Dialog", Font.ITALIC, 11));
		jlabelFootnote.setHorizontalAlignment(JLabel.LEFT);
		gbc = getGbc(0,6, 0.5,1.0, GridBagConstraints.HORIZONTAL);		
		jPanel.add(jlabelFootnote, gbc);
		
		return jPanel;
	}
	
	private boolean userIsHuman(String category) {
		return category.equals("arzt");
	}
	
	private boolean validateAddress(JTextArea textArea) {
		if (textArea.getText().contains("***")) {
			textArea.setBorder(new LineBorder(color_red, 5, false));
			textArea.setBackground(color_red);  
			return false;			
		} else {
			textArea.setBorder(new LineBorder(color_ok, 5, false));
			textArea.setBackground(color_ok);  
			return true;			
		}
	}
	
	/**
	 * Validate email addres: do a quick sanity check on the email address
	 * @param emailStr
	 * @return
	 */
	private boolean validateEmail(String emailStr) {
		if (emailStr.matches("^[_\\w-\\+]+(\\.[_\\w-]+)*@[\\w-]+(\\.[\\w]+)*(\\.[A-Za-z]{2,})$")) {
			mTextFieldEmail.setBorder(new LineBorder(color_ok, 5, false));
			mTextFieldEmail.setBackground(color_ok);  
			return true;
		} else {
			mTextFieldEmail.setBorder(new LineBorder(color_red, 5, false));
			mTextFieldEmail.setBackground(color_red); 
			return false;
		}
	}
	
	/**
	 * Validate phone number: do a quick sanity check on the phone number
	 * @param phoneStr
	 * @return
	 */
	private boolean validatePhone(String phoneStr) {
		if (phoneStr.matches("[+][\\d]+-[\\d]+")) {
			mTextFieldPhone.setBorder(new LineBorder(color_ok, 5, false));
			mTextFieldPhone.setBackground(color_ok);
			return true;
		} else {
			mTextFieldPhone.setBorder(new LineBorder(color_red, 5, false));
			mTextFieldPhone.setBackground(color_red);
			return false;
		}
	}
	
	private GridBagConstraints getGbc(int x, int y, double wx, double wy, int fill) {
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.insets = new Insets(8,0,0,0); // Insets(int top, int left, int bottom, int right)		
		gbc.fill = fill;
		gbc.weightx = wx;
		gbc.weighty = wy;
		gbc.gridx = x;
		gbc.gridy = y;
		
		return gbc;
	}
	
	private Map<String, User> readFromCsvToMap(String filename) {
		Map<String, User> map = new TreeMap<String, User>();
		try {
			File file = new File(filename);
			if (!file.exists()) 
				return null;
			FileInputStream fis = new FileInputStream(filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
			String line;
			while ((line = br.readLine()) != null) {
				/*
					Person  - 7601000900487|Bauer|Wibke Cornelia|8596|Münsterlingen||Ärztin/Arzt|Ja|Nein
				 	Company - 7601001059900|Ostschweizer Kinderspital||9006|St. Gallen|Claudiusstrasse 6|Spitalapotheke, Andere|||
				 	Person -> Arzt
				 	Company -> Apotheke, Spital, Wissenschaft, Behörde
				*/
				String token[] = line.split("\\|", -1);	// -1 -> don't discard empty strings at the end				
				User user = new User();
				user.gln_code = token[0];
				user.is_human = !token[7].isEmpty()&&!token[8].isEmpty();
				if (user.is_human) {
					user.first_name = token[1];
					user.last_name = token[2];
				} else {
					user.name1 = token[1];
					user.name2 = token[2];
				}
				user.zip = token[3];
				user.city = token[4];
				user.street = token[5];
				user.category = token[6];
				user.selbst_disp = !token[7].isEmpty();
				user.bet_mittel = !token[8].isEmpty();
				map.put(token[0], user);
			}
			br.close();
		} catch (Exception e) {
			System.err.println(">> Error in reading csv file");
		}
		
		return map;
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
					return "*.png, *.jpg";
				}
			});
					
			mFc.setDialogTitle("Logo einstellen");
			mFc.setApproveButtonText("Wählen");
			int r = mFc.showOpenDialog(mFrame);
			if (r==JFileChooser.APPROVE_OPTION) {
				// File file = fc.getSelectedFile();
				String filename = mFc.getSelectedFile().getPath();	
				ImageIcon icon = getImageIconFromFile(filename);	
				mButtonLogo.setIcon(icon);
				mPrefs.put(LogoImageID, filename);
				System.out.println("SettingsPage - opening " + filename);				
			} else {
				System.out.println("SettingsPage - open command cancelled by the user...");
			}
		}
	}
	
	private void load_gln_codes() {
		byte[] encrypted_msg = FileOps.readBytesFromFile(Utilities.appDataFolder() + "\\gln_codes.ser");
		if (encrypted_msg==null) {
			encrypted_msg = FileOps.readBytesFromFile(Constants.SHOP_FOLDER + "gln_codes.ser");
			System.out.println("Loading gln_codes.ser from default folder...");
		}
		if (encrypted_msg!=null) {
			Crypto crypto = new Crypto();
			byte[] plain_msg = crypto.decrypt(encrypted_msg);	
			m_user_map = (HashMap<String, User>)FileOps.deserialize(plain_msg);
		}		
	}
}
