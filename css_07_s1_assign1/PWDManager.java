/***********************************/
/* Author: Bo CHEN                 */
/* Student ID: 1139520             */
/* Date: 17th, Mar. 2007           */
/***********************************/

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.security.*;
import javax.crypto.*;
import com.crionics.blowfish.*;

public class PWDManager{
	public static void main(String[] args){
       MainFrame frame = new MainFrame();
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setVisible(true);
    }
}


class MainFrame extends JFrame{
    public MainFrame(){
    	// Get screen dimensions
    	Toolkit kit = Toolkit.getDefaultToolkit();
    	Dimension screenSize = kit.getScreenSize();
    	
    	// Add a panel to the frame
    	LoginPanel myPanel = new LoginPanel();
    	add(myPanel);
    	
    	// The frame is located in the center of screen	
        setSize(screenSize.width/2, screenSize.height/2);
        setLocation(screenSize.width/4, screenSize.height/4);
        
        setTitle("Password Management System");
    }
}

/* A panel in the main frame */
class LoginPanel extends JPanel{
	public static final int MESSAGE_X = 50;
	public static final int MESSAGE_Y = 70;
	public static String fileName = null;
	
	public LoginPanel(){
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		final JPanel accPanel = new JPanel();
		final JPanel pwdPanel = new JPanel();
		
		//Place usr lable and passowrd lable in different panel
		JLabel accNameLabel = new JLabel("Account:");
		final JTextField accName = new JTextField(20);
		accPanel.add(accNameLabel);
		add(Box.createHorizontalStrut(5));
		accPanel.add(accName);
		
		JLabel passwordLabel = new JLabel("Password:");
		final JPasswordField pwd = new JPasswordField(20);
		pwdPanel.add(passwordLabel);
		pwdPanel.add(pwd);

		//Place button panel
		final JPanel btnPanel = new JPanel();
		JButton registerButton = new JButton("Register");
		JButton loginButton = new JButton("Login");
		
		btnPanel.add(registerButton);
		btnPanel.add(loginButton);
		
		this.add(accPanel);
		this.add(pwdPanel);
		this.add(btnPanel);
		
		registerButton.addActionListener(new
			ActionListener(){
				public void actionPerformed(ActionEvent e){					
					// Check if the account or the passowrd is null
					if( (accName.getText().length() < 1) || (pwd.getPassword().length < 1) ){
						System.out.println("Error: Please input Account AND Password");
						JOptionPane.showMessageDialog(null, "Please input Account AND Password for registration", "ERROR", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					// Create account file for storing data
					File fileObj = new File(accName.getText() + ".data");
					if(fileObj.exists()){
						JOptionPane.showMessageDialog(null, "The account has been taken!", "ERROR", JOptionPane.ERROR_MESSAGE);
					}else{
						try{
							BufferedWriter dataWriter = new BufferedWriter(new FileWriter(fileObj.getAbsolutePath()));
							// Using SHA-1 to digest the login password
							MessageDigest md = MessageDigest.getInstance("SHA-1");
							md.reset();
							md.update(String.valueOf(pwd.getPassword()).getBytes());
							String strPwd = new String(md.digest());
							// The tokenizer is "\r\n"
							dataWriter.write(strPwd + "\r\n");							
							dataWriter.close();
						}catch(java.io.FileNotFoundException ioe){
							 System.out.println(fileObj.getAbsolutePath() + "' could not be created");
						}catch(IOException ioe){
							ioe.printStackTrace();
						}catch(NoSuchAlgorithmException nsae){
							System.out.println("Error: Digest algorithm is not exist!");
						}
					}
				}
			}
		);
		
		loginButton.addActionListener(new
				ActionListener(){
					public void actionPerformed(ActionEvent e){
						if( (accName.getText().length() < 1) || (pwd.getPassword().length < 1) ){
							System.out.println("Error: Please input Account AND Password");
							JOptionPane.showMessageDialog(null, "Please input Account AND Password to logon", "ERROR", JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						// Check to see if the account is exist
						File fileObj = new File(accName.getText() + ".data");
						if(!fileObj.exists()){
							JOptionPane.showMessageDialog(null, "The account does not exist!", "ERROR", JOptionPane.ERROR_MESSAGE);
						}else{
							try{
								BufferedReader dataReader = new BufferedReader(new FileReader(fileObj.getAbsolutePath()));
								
								File tempObj = new File("temp.data");
								BufferedWriter tempWriter = new BufferedWriter(new FileWriter(tempObj.getAbsolutePath()));
								BufferedReader tempReader = new BufferedReader(new FileReader(tempObj.getAbsolutePath()));
								
								// Verify the login password
								MessageDigest md = MessageDigest.getInstance("SHA-1");
								md.reset();
								md.update(String.valueOf(pwd.getPassword()).getBytes());
								String inputPwd = new String(md.digest());
								
								tempWriter.write(inputPwd + "\r\n");
								tempWriter.close();
								inputPwd = tempReader.readLine();
								String realPwd = dataReader.readLine();
								if(inputPwd.equals(realPwd)){
									JOptionPane.showMessageDialog(null, "Log on successfully", "Correct", JOptionPane.INFORMATION_MESSAGE);
									fileName = accName.getText();
									setVisible(false);
									remove(accPanel);
									remove(btnPanel);
									remove(pwdPanel);
									add(new MainPanel());
									setVisible(true);
									repaint();
									
									// Add new button "ADD" and "Show" 
								}else{
									JOptionPane.showMessageDialog(null, "Wrong password, please try again", "Error", JOptionPane.ERROR_MESSAGE);
								}
							}catch(IOException ioe){
								System.out.println("Error in reading pwd file");
								ioe.printStackTrace();
							}catch(NoSuchAlgorithmException nsae){
								System.out.println("Error: Digest algorithm is not exist!");
							}
						}
					}
				}
		);
	}
}

class MainPanel extends JPanel{
	private String algorithm = "Blowfish";
	private Key key = null;
	private Cipher cipher = null;
	private Blowfish bf = new Blowfish();
	
	public void initCipher() throws Exception{
		key = KeyGenerator.getInstance(algorithm).generateKey();
		cipher = Cipher.getInstance(algorithm);
	}

	
	public MainPanel() {
		try{
			initCipher();
		}catch(Exception e){
			System.out.println("Error: in init Cipher");
		}
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		final JPanel addPanel = new JPanel();
		JButton addBtn = new JButton("Add a new record");
		addPanel.add(addBtn);
		this.add(addPanel);
		
		final JPanel showPanel = new JPanel();
		JButton showBtn = new JButton("Show the records");
		showPanel.add(showBtn);
		this.add(showPanel);
		
		final JPanel changeAccPanel = new JPanel();
		JButton changeAccBtn = new JButton("Change account");
		changeAccPanel.add(changeAccBtn);
		this.add(changeAccPanel);

		
		/* Add a new record to the file */
		addBtn.addActionListener(new
			ActionListener(){
			public void actionPerformed(ActionEvent e){
				remove(addPanel);
				remove(showPanel);
				remove(changeAccPanel);
				setVisible(false);
				
				final JPanel accPanel = new JPanel();
				final JPanel pwdPanel = new JPanel();
				
				//Place usr lable and passowrd lable in different panel
				JLabel accNameLabel = new JLabel("User name: ");
				final JTextField accName = new JTextField(20);
				accPanel.add(accNameLabel);
				add(Box.createHorizontalStrut(5));
				accPanel.add(accName);
				
				JLabel passwordLabel = new JLabel("Password: ");
				final JPasswordField pwd = new JPasswordField(20);
				pwdPanel.add(passwordLabel);
				pwdPanel.add(pwd);

				//Place button panel
				final JPanel btnPanel = new JPanel();
				JButton okButton = new JButton("OK");
				JButton cancelButton = new JButton("Cancel");
				
				btnPanel.add(okButton);
				btnPanel.add(cancelButton);
				
				add(accPanel);
				add(pwdPanel);
				add(btnPanel);
				
				setVisible(true);
				repaint();
				
				okButton.addActionListener(new
					ActionListener(){
						public void actionPerformed(ActionEvent e){
							
							if( (accName.getText().length() < 1) || (pwd.getPassword().length < 1) ){
								JOptionPane.showMessageDialog(null, "Please input both user name AND Password", "ERROR", JOptionPane.ERROR_MESSAGE);
								return;
							}

							File filePairs = new File(LoginPanel.fileName + ".pwd");
							try{							
								// Write the record to relevant file
								String clearPair = accName.getText() + "\r\n" + String.valueOf(pwd.getPassword()) + "\r\n";
								if(!filePairs.exists()){
									BufferedWriter pairsDataWriter = new BufferedWriter(new FileWriter(filePairs.getAbsolutePath()));
									pairsDataWriter.write(bf.cipher(clearPair));
									pairsDataWriter.close();
								}else{
									
									BufferedReader pairsDataReader = new BufferedReader(new FileReader(filePairs.getAbsolutePath()));
									String oriData = bf.decipher(pairsDataReader.readLine());
									String newData = oriData + clearPair;
									pairsDataReader.close();
									BufferedWriter pairsDataWriter = new BufferedWriter(new FileWriter(filePairs.getAbsolutePath()));
									pairsDataWriter.write(bf.cipher(newData));
									pairsDataWriter.close();
								}
							}catch(IOException ioe){
								System.out.println("Error: reading data file");
							}catch(BlowfishException be){
								System.out.println("Blowfish Exception");
							}
							
							// Clear the text 
							accName.setText(null);
							pwd.setText(null);
							JOptionPane.showMessageDialog(null, "Adding successfully. Press cancel to return or add a new record", "ERROR", JOptionPane.INFORMATION_MESSAGE);
						}
					}
				);
				
				
				cancelButton.addActionListener(new
					ActionListener(){
						public void actionPerformed(ActionEvent e){
							add(addPanel);
							add(showPanel);
							add(changeAccPanel);
							remove(accPanel);
							remove(pwdPanel);
							remove(btnPanel);
							setVisible(false);
							setVisible(true);
							repaint();
						}
					}
				);
			}
		}
		);
		
		/* Show the records */
		showBtn.addActionListener(new
			ActionListener(){
				public void actionPerformed(ActionEvent e){
					remove(addPanel);
					remove(showPanel);
					remove(changeAccPanel);
					setVisible(false);
					
//					setLayout(new BoxLayout(null, BoxLayout.Y_AXIS));
					final JPanel okPanel = new JPanel();
					JTextArea textArea = new JTextArea(8, 30);
					final JScrollPane scrollPane = new JScrollPane(textArea);
					
					JButton okButton = new JButton("OK");
					okPanel.add(okButton);
					
					add(scrollPane);
					add(okPanel);
					
					setVisible(true);
					repaint();
					
					okButton.addActionListener(new
							ActionListener(){
								public void actionPerformed(ActionEvent e){
									add(addPanel);
									add(showPanel);
									add(changeAccPanel);
									remove(scrollPane);
									remove(okPanel);
									setVisible(false);
									setVisible(true);
									repaint();
								}
							}
						);
					
					/* Show the records on the text area */
					File fileObj = new File(LoginPanel.fileName + ".pwd");
					try{
						String[] records = null;
						String allRecords = null;
						
						BufferedReader dataReader = new BufferedReader(new FileReader(fileObj.getAbsolutePath()));
						allRecords = bf.decipher(dataReader.readLine());
						records = allRecords.split("\r\n");
						
						for(int i=0; i<(records.length + 1)/2; i++){
							textArea.append("User Name: " + records[2*i] + "\r\n");
							textArea.append("Password: " + records[2*i+1] + "\r\n");
						}
					}catch(IOException ioe){
						System.out.println("Error: reading data file");
					}catch(BlowfishException be){
						System.out.println("Blowfish Exception");
					}
					
				}
			}
		);
		
		
		changeAccBtn.addActionListener(new
			ActionListener(){
				public void actionPerformed(ActionEvent e){
					remove(addPanel);
					remove(showPanel);
					remove(changeAccPanel);
					LoginPanel myPanel = new LoginPanel();
					add(myPanel);
					setVisible(false);
					setVisible(true);
					repaint();		
				}
			}
		);
	}
	
	/* Encryption */
	public String Encrypt(byte[] input) 
		throws InvalidKeyException, BadPaddingException,
		IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException{
		
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipheredBytes = cipher.doFinal(input);
		return new String(cipheredBytes);
	}
	
	/* Decryption */
    public String Decrypt(byte[] encryptedBytes)
    throws InvalidKeyException, 
           BadPaddingException,
           IllegalBlockSizeException {
    
    	cipher.init(Cipher.DECRYPT_MODE, key);
    	byte[] recoveredBytes = cipher.doFinal(encryptedBytes);
    	String recovered = new String(recoveredBytes);
    	return recovered;
  }
}















