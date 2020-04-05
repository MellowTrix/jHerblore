package scripts.jay_api.jaysgui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JButton;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.JLabel;
import java.awt.Color;
import scripts.jay_api.handlerXML;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;

import org.apache.commons.io.FilenameUtils;
import org.tribot.api.General;
import org.tribot.util.Util;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JComboBox;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;

public class jaysgui {

	public static boolean startScript = false;
	public static boolean endScript = false;
	
	private JFrame frame;

	/**
	 * Launch the application.
	 */
	
	public static void main(File newFile) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					jaysgui window = new jaysgui(newFile);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws IOException 
	 */
	public jaysgui(File file) throws IOException {
		initialize(file);
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws IOException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initialize(File file) throws IOException {
		
		String[] settings = {"true", "-1", "false", "true", "true", "-1", "28", "true", "-1", "false", "-1", "30", "-49"};
		String[] herblore_settings = {"0"};
		String[] herbs = {"199", "201", "203", "205", "207", "3049", "209", "211", "213", "3051", "215", "2485", "217", "219"};
		
		herblore_settings[0] = handlerXML.get().getSetting(file, "HERBLORE", "herblore_method");
		
		String path = Util.getAppDataDirectory().getPath();
		ImageIcon save_icon = new ImageIcon(path + File.separator + "bin" + File.separator + "scripts" +  File.separator + "jay_api" +
  		  		 File.separator + "jaysgui" + File.separator + "icons" + File.separator + "save_icon.png");
		Image img = save_icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
		ImageIcon newIcon = new ImageIcon(img);
		
		frame = new JFrame();
		frame.setBounds(100, 100, 550, 400);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                frame.dispose();
                endScript = true;
                startScript = true;
            }
        });
		
		JLabel title = new JLabel("jHerblore");
		title.setForeground(new Color(0, 128, 0));
		title.setFont(new Font("Tahoma", Font.BOLD, 33));
		
		JLabel discord = new JLabel("By: Jaywalker#9754");
		discord.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, -49, 100, 1)); // Lower limit is 49% incase item value is 1 GP we do not want to attempt to buy/sell it for 0 gp.
		spinner.setEditor(new JSpinner.DefaultEditor(spinner));
		spinner.setValue(Integer.parseInt(handlerXML.get().getSetting(file, "GE", "restocking_mult_buy")));
		
		JSpinner spinner_1 = new JSpinner(new SpinnerNumberModel(0, -49, 100, 1));
		spinner_1.setEditor(new JSpinner.DefaultEditor(spinner_1));
		spinner_1.setValue(Integer.parseInt(handlerXML.get().getSetting(file, "GE", "restocking_mult_sell")));
		
		JCheckBox skipCheckBox = new JCheckBox("Skip GUI?");
		skipCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 14));
		skipCheckBox.setEnabled(true);
		
		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setModel(new DefaultComboBoxModel(new String[] {"Potion Mixing", "Potion Mixing(unf)", "Herb Cleaning"}));
		DefaultComboBoxModel comboBoxModel_1 = (DefaultComboBoxModel) comboBox_1.getModel();
		comboBoxModel_1.setSelectedItem(comboBoxModel_1.getElementAt(Integer.parseInt(herblore_settings[0])));
		
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Guam leaf(Level 3)", "Marrentill(Level 5)", "Tarromin(Level 11)", "Harralander(Level 20)", "Ranarr weed(Level 25)", "Toadflax(Level 30)", "Irit leaf(Level 40)", "Avantoe(Level 48)", "Kwuarm(Level 54)", "Snapdragon(Level 59)", "Cadantine(Level 65)", "Lantadyme(Level 67)", "Dwarf weed(Level 70)", "Torstol(Level 75)"}));
		DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBox.getModel();
		setComboBoxModel(comboBoxModel_1, comboBox, comboBoxModel);
		setComboBoxModelItem(file, comboBoxModel, herbs);

		comboBox_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setComboBoxModel(comboBoxModel_1, comboBox, comboBoxModel);
			}
		});
		
		JFormattedTextField frmtdtxtfldRestockingAmount = new JFormattedTextField();
	    frmtdtxtfldRestockingAmount.addKeyListener(new KeyAdapter() {
	    	public void keyPressed(KeyEvent ke) {
	            if ((ke.getKeyChar() >= '0' && ke.getKeyChar() <= '9') || ke.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
	            	frmtdtxtfldRestockingAmount.setEditable(true);
	            } else {
	            	frmtdtxtfldRestockingAmount.setEditable(false);
	            	General.println("jHerblore - Only numbers allowed in that field.");
	            }
	        }
	    });
	    
		JCheckBox chckbxNewCheckBox = new JCheckBox("Enable restocking");
		if (handlerXML.get().getSetting(file, "GE", "restocking").equals("true")) {
			chckbxNewCheckBox.setSelected(true);
			frmtdtxtfldRestockingAmount.setEditable(true);
			frmtdtxtfldRestockingAmount.setText(handlerXML.get().getSetting(file, "GE", "restocking_amount"));
			spinner.setEnabled(true);
			spinner_1.setEnabled(true);
		}
		else {
			chckbxNewCheckBox.setSelected(false);
			frmtdtxtfldRestockingAmount.setEditable(false);
			frmtdtxtfldRestockingAmount.setText("");
			spinner.setEnabled(false);
			spinner_1.setEnabled(false);
			
		}
		chckbxNewCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (chckbxNewCheckBox.isSelected()) {
					frmtdtxtfldRestockingAmount.setEditable(true);
					spinner.setEnabled(true);
					spinner_1.setEnabled(true);
				}
				else {
					frmtdtxtfldRestockingAmount.setEditable(false);
					frmtdtxtfldRestockingAmount.setText("");
					spinner.setEnabled(false);
					spinner_1.setEnabled(false);
				}
			}
		});
		
		JButton startButton = new JButton("START");
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (skipCheckBox.isSelected())		
					handlerXML.get().skipGUI(file, true);

				settings[5] = herbs[comboBoxModel.getIndexOf(comboBoxModel.getSelectedItem())];
				if (chckbxNewCheckBox.isSelected() && !frmtdtxtfldRestockingAmount.getText().equals("") && !frmtdtxtfldRestockingAmount.getText().startsWith("0")) {
					settings[9] = "true";
 					settings[10] = frmtdtxtfldRestockingAmount.getText();
				}
				else {
					settings[9] = "false";
					settings[10] = "-1";
				}
				
				settings[11] = spinner.getValue().toString();
				settings[12] = spinner_1.getValue().toString();
				
				herblore_settings[0] = String.valueOf(comboBoxModel_1.getIndexOf(comboBoxModel_1.getSelectedItem()));
				
                handlerXML.get().parseHerblore(file, settings, herblore_settings);
					
				frame.dispose();
				handlerXML.get().setTime(System.currentTimeMillis());
				startScript = true;			
			}
		});
		
		JLabel lblNewLabel_1 = new JLabel("Restocking amount:");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		JLabel lblNewLabel_2 = new JLabel("Price multiplier (%):");
		
		JLabel lblNewLabel_3 = new JLabel("BUY");
		
		JLabel lblNewLabel_4 = new JLabel("SELL");

		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(42)
							.addComponent(chckbxNewCheckBox)
							.addPreferredGap(ComponentPlacement.RELATED, 68, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblNewLabel_1)
							.addPreferredGap(ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
							.addComponent(frmtdtxtfldRestockingAmount, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
							.addGap(22)))
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(10)
							.addComponent(skipCheckBox))
						.addComponent(startButton, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE))
					.addGap(227))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel_2)
					.addGap(20)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(spinner, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
							.addGap(33))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblNewLabel_3, GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)))
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel_4)
						.addComponent(spinner_1, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(301, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(204)
					.addComponent(discord, GroupLayout.PREFERRED_SIZE, 207, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(133, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(192)
					.addComponent(title, GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
					.addGap(19))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(147)
					.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 235, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(162, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(213)
					.addComponent(comboBox_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(219, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(26)
					.addComponent(title, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(discord, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(comboBox_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(16)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(18)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(startButton, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
								.addComponent(chckbxNewCheckBox))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(skipCheckBox, GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
									.addGap(36))
								.addGroup(groupLayout.createSequentialGroup()
									.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblNewLabel_1)
										.addComponent(frmtdtxtfldRestockingAmount, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
									.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblNewLabel_4)
										.addComponent(lblNewLabel_3))
									.addGap(17)))
							.addGap(20))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(121)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(spinner_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblNewLabel_2))
							.addContainerGap())))
		);
		frame.getContentPane().setLayout(groupLayout);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("Settings");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("Load settings");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    JFileChooser chooser = new JFileChooser(new File(Util.getAppDataDirectory().getPath() + File.separator + "jScripts" + File.separator + "jHerblore"));
			    FileNameExtensionFilter filter = new FileNameExtensionFilter(
			        "xml Files", "xml");
			    chooser.setFileFilter(filter);
			    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			    	skipCheckBox.setSelected(handlerXML.get().skipGUI(chooser.getSelectedFile()));
			    	if (handlerXML.get().getSetting(chooser.getSelectedFile(), "GE", "restocking").equals("true")) {
			    		frmtdtxtfldRestockingAmount.setEditable(true);
			    		frmtdtxtfldRestockingAmount.setText(handlerXML.get().getSetting(chooser.getSelectedFile(), "GE", "restocking_amount"));
			    		chckbxNewCheckBox.setSelected(true);
			    	}
			    	else {
			    		frmtdtxtfldRestockingAmount.setEditable(false);
			    		frmtdtxtfldRestockingAmount.setText("");
			    		chckbxNewCheckBox.setSelected(false);
			    	}
			    	spinner.setValue(Integer.parseInt(handlerXML.get().getSetting(chooser.getSelectedFile(), "GE", "restocking_mult_buy")));
			    	spinner_1.setValue(Integer.parseInt(handlerXML.get().getSetting(chooser.getSelectedFile(), "GE", "restocking_mult_sell")));
			    	
			    	comboBoxModel_1.setSelectedItem(comboBoxModel_1.getElementAt(Integer.parseInt(handlerXML.get().getSetting(chooser.getSelectedFile(), "HERBLORE", "herblore_method"))));
			    	setComboBoxModel(comboBoxModel_1, comboBox, comboBoxModel);
			    	setComboBoxModelItem(chooser.getSelectedFile(), comboBoxModel, herbs);
			    }
			}
		});
		mntmNewMenuItem.setIcon(newIcon);
		mnNewMenu.add(mntmNewMenuItem);

		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Save settings");
		mntmNewMenuItem_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    JFileChooser chooser = new JFileChooser(new File(Util.getAppDataDirectory().getPath() + File.separator + "jScripts" + File.separator + "jHerblore"));
			    FileNameExtensionFilter filter = new FileNameExtensionFilter(
			        "xml Files", "xml");
			    chooser.setFileFilter(filter);
			    if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

			    	settings[5] = herbs[comboBoxModel.getIndexOf(comboBoxModel.getSelectedItem())];
			    	
					if (chckbxNewCheckBox.isSelected() && !frmtdtxtfldRestockingAmount.getText().equals("") && !frmtdtxtfldRestockingAmount.getText().equals("0")) {
						settings[9] = "true";
						settings[10] = frmtdtxtfldRestockingAmount.getText();
					}
					else {
						settings[9] = "false";
						settings[10] = "-1";
					}
					
					settings[11] = spinner.getValue().toString();
					settings[12] = spinner_1.getValue().toString();
					herblore_settings[0] = String.valueOf(comboBoxModel_1.getIndexOf(comboBoxModel_1.getSelectedItem()));
			    	
			    	String saved_file = chooser.getSelectedFile().toString();
			    	if (!FilenameUtils.isExtension(saved_file, "xml")) {
			    		saved_file = FilenameUtils.removeExtension(saved_file);
			    		File xml_file = new File(saved_file + ".xml");
			    		handlerXML.get().createFile(xml_file);
						if (skipCheckBox.isSelected())
							handlerXML.get().skipGUI(xml_file, true);
						else
							handlerXML.get().skipGUI(xml_file, false);
						handlerXML.get().parseHerblore(xml_file, settings, herblore_settings);
					}
			    	else {
			    		handlerXML.get().createFile(chooser.getSelectedFile());
			    		handlerXML.get().parseHerblore(chooser.getSelectedFile(), settings, herblore_settings);
						if (skipCheckBox.isSelected())
							handlerXML.get().skipGUI(chooser.getSelectedFile(), true);
						else
							handlerXML.get().skipGUI(chooser.getSelectedFile(), false);
			    	}
			    }
			}
		});
		mntmNewMenuItem_1.setIcon(newIcon);
		mnNewMenu.add(mntmNewMenuItem_1);
	}
	
	@SuppressWarnings("rawtypes")
	public void setComboBoxModelItem(File file, DefaultComboBoxModel comboBoxModel, String[] array) {
		String s = handlerXML.get().getSetting(file, "SETUP", "setup_withdrawing_items");
		for (int i=0; i < array.length; i++) {
			if (array[i].equals(s))
				comboBoxModel.setSelectedItem(comboBoxModel.getElementAt(i));
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setComboBoxModel(DefaultComboBoxModel comboBoxModel_1, JComboBox comboBox, DefaultComboBoxModel comboBoxModel) {
		String[] mix_unf = {"Guam Potion (unf)(Level 3)", "Marrentill potion (unf)(Level 5)", "Tarromin potion (unf)(Level 11)", "Harralander potion (unf)(Level 20)", "Ranarr potion (unf)(Level 25)", "Toadflax potion (unf)(Level 30)", "Irit potion (unf)(Level 40)", "Avantoe potion (unf)(Level 48)", "Kwuarm potion (unf)(Level 54)", "Snapdragon potion (unf)(Level 59)", "Cadantine potion (unf)(Level 65)", "Lantadyme potion (unf)(Level 67)", "Dwarf weed potion (unf)(Level 70)", "Torstol potion (unf)(Level 75)"};
		String[] clean = {"Guam leaf(Level 3)", "Marrentill(Level 5)", "Tarromin(Level 11)", "Harralander(Level 20)", "Ranarr weed(Level 25)", "Toadflax(Level 30)", "Irit leaf(Level 40)", "Avantoe(Level 48)", "Kwuarm(Level 54)", "Snapdragon(Level 59)", "Cadantine(Level 65)", "Lantadyme(Level 67)", "Dwarf weed(Level 70)", "Torstol(Level 75)"};
		
		int val = comboBoxModel_1.getIndexOf(comboBoxModel_1.getSelectedItem());
		comboBox.removeAllItems();
		if (val == 0) {
			for (String s : mix_unf)
				comboBox.addItem(s);
		}
		else if (val == 1) {
			for (String s : mix_unf)
				comboBox.addItem(s);
		}
		else {
			for (String s : clean)
				comboBox.addItem(s);
		}

		comboBoxModel = (DefaultComboBoxModel) comboBox.getModel();
	}
}