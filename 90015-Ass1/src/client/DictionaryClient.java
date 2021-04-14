/*
 * COMP90015 - Assignment 1
 * Full name: Jiayu Han
 * ID: 1164280
 * 
 */
package client;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.Font;
import javax.swing.JTextArea;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class DictionaryClient extends JFrame {

	private JPanel contentPane;
	private static String inputtext; //input text in textfield
	private static String operation; //operation command pass to server
	private static String result; 	 //result string shown in TextArea
	private static String ip;
	private static int port;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		//get arguments from cmd
		ip = args[0]; //server ip address
		port = Integer.parseInt(args[1]); //port number
	
		//invoke the frame
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DictionaryClient frame = new DictionaryClient();
					frame.setTitle("MyDict by Jiayu Han");
					frame.setVisible(true);
				} catch (Exception e) {
					System.out.println("Cannot invoke Jframe for client GUI.");
				}
			}
		});
		
	}
	//create thread per request
	public static void createThread() {
		//run socket
		try(Socket socket = new Socket(ip, port);)
		{
			//create Output and Input Stream
			DataInputStream input = new DataInputStream(socket.getInputStream());
			DataOutputStream output = new DataOutputStream(socket.getOutputStream());
			
			//retrieve operation and input text in textField
			String Data = operation + ":" + inputtext;	
			
			//write data into output stream and pass to server
			output.writeUTF(Data);
		 	output.flush();
	    	
	    	result=input.readUTF();
		} 
		catch (UnknownHostException e)
		{
			System.out.println("IP address of a host could not be determined.");
		}
		catch (IOException e) 
		{
			System.out.println("Cannot run socket: wrong port number.");
		}
	}
	/**
	 * Create the frame.
	 */
	
	public DictionaryClient() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel lblNewLabel = new JLabel("Welcome to MyDict!");
		lblNewLabel.setFont(new Font("Book Antiqua", Font.ITALIC, 20));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.gridwidth = 11;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		contentPane.add(lblNewLabel, gbc_lblNewLabel);
		
		JTextArea TextArea = new JTextArea();
		TextArea.setToolTipText("Output display");
		TextArea.setFont(new Font("Book Antiqua", Font.PLAIN, 13));
		GridBagConstraints gbc_TextArea = new GridBagConstraints();
		gbc_TextArea.insets = new Insets(0, 0, 0, 5);
		gbc_TextArea.gridwidth = 9;
		gbc_TextArea.fill = GridBagConstraints.BOTH;
		gbc_TextArea.gridx = 1;
		gbc_TextArea.gridy = 4;
		contentPane.add(TextArea, gbc_TextArea);
		
		JTextField TextField = new JTextField();
		TextField.setToolTipText("Input a word or a word with its definition separated by comma");
		TextField.setFont(new Font("Book Antiqua", Font.PLAIN, 13));
		GridBagConstraints gbc_TextField = new GridBagConstraints();
		gbc_TextField.gridwidth = 9;
		gbc_TextField.insets = new Insets(0, 0, 5, 5);
		gbc_TextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_TextField.gridx = 1;
		gbc_TextField.gridy = 1;
		contentPane.add(TextField, gbc_TextField);
		TextField.setColumns(10);
		//query button and event handler
		JButton QueryBtn = new JButton("Query");
		QueryBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//get text in textField and pass it to input stream
				inputtext = TextField.getText();
				//define operation string
				operation = "query";
				//run socket
				createThread();
				//show result
				TextArea.setText(result);
			}
				
		});
		
		QueryBtn.setFont(new Font("Book Antiqua", Font.PLAIN, 13));
		GridBagConstraints gbc_QueryBtn = new GridBagConstraints();
		gbc_QueryBtn.insets = new Insets(0, 0, 5, 5);
		gbc_QueryBtn.gridx = 1;
		gbc_QueryBtn.gridy = 2;
		contentPane.add(QueryBtn, gbc_QueryBtn);
		//add button
		JButton AddBtn = new JButton("Add");
		AddBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//get text in textField and pass it to input stream
				inputtext = TextField.getText();
				//define operation string
				operation = "add";
				//run socket
				createThread();
				//show result
				TextArea.setText(result);
			}
			
		});
		AddBtn.setFont(new Font("Book Antiqua", Font.PLAIN, 13));
		GridBagConstraints gbc_AddBtn = new GridBagConstraints();
		gbc_AddBtn.anchor = GridBagConstraints.WEST;
		gbc_AddBtn.insets = new Insets(0, 0, 5, 5);
		gbc_AddBtn.gridx = 2;
		gbc_AddBtn.gridy = 2;
		contentPane.add(AddBtn, gbc_AddBtn);
		//remove button
		JButton RemoveBtn = new JButton("Remove");
		RemoveBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//get text in textField and pass it to input stream
				inputtext = TextField.getText();
				//define operation string
				operation = "remove";
				//run socket
				createThread();
				//show result in textarea
				TextArea.setText(result);
			}
		});
		RemoveBtn.setFont(new Font("Book Antiqua", Font.PLAIN, 13));
		GridBagConstraints gbc_RemoveBtn = new GridBagConstraints();
		gbc_RemoveBtn.insets = new Insets(0, 0, 5, 5);
		gbc_RemoveBtn.gridx = 8;
		gbc_RemoveBtn.gridy = 2;
		contentPane.add(RemoveBtn, gbc_RemoveBtn);
		//update event handler
		JButton UpdateBtn = new JButton("Update");
		UpdateBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//get text in textField and pass it to input stream
				inputtext = TextField.getText();
				//define operation string
				operation = "update";
				//run socket
				createThread();
				//show result in textarea
				TextArea.setText(result);
			}
		});
		UpdateBtn.setFont(new Font("Book Antiqua", Font.PLAIN, 13));
		GridBagConstraints gbc_UpdateBtn = new GridBagConstraints();
		gbc_UpdateBtn.insets = new Insets(0, 0, 5, 5);
		gbc_UpdateBtn.gridx = 9;
		gbc_UpdateBtn.gridy = 2;
		contentPane.add(UpdateBtn, gbc_UpdateBtn);
		
	}

}
