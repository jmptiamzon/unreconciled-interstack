package com.sprint.interstack.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.sprint.interstack.controller.Controller;

public class View extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String filepath;
	private File file;
	private JTextField filenameField;
	private JButton btnChoose, btnGenerate;
	private JPanel paneCenter, paneBottom;
	private GridBagConstraints gbc;
	private Insets inset;
	private JFileChooser filechooser;
	private Controller controller;
	
	public View() {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		controller = new Controller();
		filechooser = new JFileChooser();
		gbc = new GridBagConstraints();
		inset = new Insets(5, 5, 5, 5);
		
		filenameField = new JTextField("Filepath", 20);
		filenameField.setEnabled(false);
		
		btnChoose = new JButton("Choose File");
		btnChoose.addActionListener(this);
		btnGenerate = new JButton("Generate");
		btnGenerate.addActionListener(this);
		
		setLayout(new BorderLayout());
		paneCenter = new JPanel(new GridBagLayout());
		paneBottom = new JPanel(new GridBagLayout());
		
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Unreconciled Interstack");
		add(paneCenter, BorderLayout.CENTER);
		add(paneBottom, BorderLayout.SOUTH); 
		
		setComponent(filenameField, paneCenter, 0.5, 0, 0, 0, 0);
		setComponent(btnChoose, paneCenter, 0.5, 1, 0, 0, 0);
		setComponent(btnGenerate, paneBottom, 1, 0, 0, 0, 0);
		
		pack();
		setLocationRelativeTo(null);
		
	}
	
	public void setComponent(Component component, JPanel compPanel, double weightx, int gridx, int gridy, int ipady, int ipadx) {
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = inset;
		gbc.weightx = weightx;
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.ipady = ipady;
		compPanel.add(component, gbc);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub
		
		if (event.getSource() == btnChoose) {
			filechooser.setAcceptAllFileFilterUsed(false);
			filechooser.addChoosableFileFilter(new FileNameExtensionFilter("Select xlsx Files", "xlsx"));
			
			int flag = filechooser.showOpenDialog(this);
			
			if (flag == JFileChooser.APPROVE_OPTION) {
				file = filechooser.getSelectedFile();
				filepath = Paths.get(file.getPath()).toString();
				filenameField.setText(filepath);
			}
			
		} else if (event.getSource() == btnGenerate) {
			controller.runApp(filepath);
		}
		
	}

}
