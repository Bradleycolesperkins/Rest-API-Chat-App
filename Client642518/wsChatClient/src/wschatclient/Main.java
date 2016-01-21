package wschatclient;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

import javax.xml.ws.WebServiceRef;

import wschatserver.ChatServerService;
import wschatserver.ChatServer;
        
/**
 * 
 * @author Braders
 * Main class
 */
public class Main {
    @WebServiceRef(wsdlLocation="http://localhost:8080/WSChatServer/ChatServerService?wsdl")
    
    // Create all objects
    private JFrame frame;
    private JLabel usernameLabel;
    private JPanel usernamePanel;
    private JLabel privateMessageLabel1;
    private JLabel privateMessageLabel2;
    private static JTextArea myText;
    private static JTextArea otherText;
    private static JTextArea privateMessageUser;
    private static JTextArea privateMessageContent;
    private static JTextArea usernameText;
    private JScrollPane myTextScroll;
    private JScrollPane otherTextScroll;
    private static JButton userConfirm;
    private static JRadioButton privateMessageButton;
    private static TextThread otherTextThread;
    private String textString = "";
    private static String user = "";
    private Boolean privateMessageBool = false;
    
    private static final int HOR_SIZE = 350;
    private static final int VER_SIZE = 150;
    
    private ChatServerService service;
    private ChatServer port;
    private int id;


    /**
     * initComponents - set up postions
     * and listeners of button, panels, etc
     * Also calls the startserver method 
     * when confirm is pressed
     * @param host 
     */
    private void initComponents(String host) {
    	frame = new JFrame("Chat Client");
        myText = new JTextArea();
        frame.setResizable(false);
        
        myTextScroll = new JScrollPane(myText);			
        myTextScroll.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		myTextScroll.setVerticalScrollBarPolicy(
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		myTextScroll.setMaximumSize(
		    new java.awt.Dimension(HOR_SIZE, VER_SIZE));
		myTextScroll.setMinimumSize(new java.awt.Dimension(HOR_SIZE, VER_SIZE));
		myTextScroll.setPreferredSize(new java.awt.Dimension(
		    HOR_SIZE, VER_SIZE));
        myText.setEditable(false);
        myText.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyTyped(java.awt.event.KeyEvent evt) {
                textTyped(evt);
            }
        });
        frame.getContentPane().add(myTextScroll, java.awt.BorderLayout.CENTER);
        
        otherText = new JTextArea();
        
        otherTextScroll = new JScrollPane(otherText);
        otherText.setBackground(new java.awt.Color(200, 200, 200));
        otherTextScroll.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        otherTextScroll.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        otherTextScroll.setMaximumSize(
            new java.awt.Dimension(HOR_SIZE, VER_SIZE));
        otherTextScroll.setMinimumSize(
            new java.awt.Dimension(HOR_SIZE, VER_SIZE));
        otherTextScroll.setPreferredSize(new java.awt.Dimension(
		    HOR_SIZE, VER_SIZE));
        otherText.setEditable(false);
               
        frame.getContentPane().add(otherTextScroll,
            java.awt.BorderLayout.SOUTH);
            
        
        usernamePanel = new JPanel();
        usernamePanel.setPreferredSize(new Dimension(90,75));
        usernameText = new JTextArea(1,10);
        usernameText.setPreferredSize(new Dimension(10,10));
        usernameText.setLineWrap(true);
        usernameText.setWrapStyleWord(true);
        
        usernameLabel = new JLabel("Username:");
        
        userConfirm = new JButton("Confirm");
        
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameText);
        usernamePanel.add(userConfirm);
        
        // Action listener for the username confirm button
        userConfirm.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e)
            {
                // if the username contains more than one character
                if(usernameText.getText().length() != 0){
                    // set the textfield editables
                    myText.setEditable(true);
                    privateMessageUser.setEditable(true);
                    usernameText.setEditable(false);
                    // store the username text
                    user = usernameText.getText();
                    // start the server passing the username as a param
                    startServer(user);
                }
            }
        });     
        
        privateMessageLabel1 = new JLabel("PM User:");
        usernamePanel.add(privateMessageLabel1);
        
        privateMessageUser = new JTextArea(1,10);
        privateMessageUser.setPreferredSize(new Dimension(10,10));
        usernamePanel.add(privateMessageUser);    
        privateMessageUser.setEditable(false);
        
        privateMessageButton = new JRadioButton("Turn on PM");
        usernamePanel.add(privateMessageButton);
                
        // Action listener for the private message button
        privateMessageButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                // change the boolean if the radio is enabled/disabled
                if (privateMessageButton.isSelected()){
                     privateMessageBool = true;
                } else if (!privateMessageButton.isSelected()) {
                     privateMessageBool = false;
                }
            }
        });      
   
        frame.getContentPane().add(usernamePanel, java.awt.BorderLayout.NORTH);     
        frame.pack();
        frame.setVisible(true);
        
    }
    
    /**
     * server method
     * @param userString 
     */
    private void startServer(String userString){
        try {
          // Start the server
          service = new wschatserver.ChatServerService();
          port = service.getChatServerPort();
          // add the user and tell users they have joined
          port.join(userString);
          port.talk(userString + " : Has joined");  

          // create a threaded chat
          otherTextThread = new TextThread(otherText, id, port);
          // Start the chat thread
          otherTextThread.start();
          
          // Frame listener for exiting
          frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
          	  try {
                      // Tell other users they have left
          	      port.talk(user + " : Has Left");
                      // Make the user leave
                      port.leave(user); 
          	  }
          	  catch (Exception ex) {
          	      otherText.append("Exit failed.");
          	  }
          	  System.exit(0);
            }
          });
        }
        catch (Exception ex) {
            otherText.append("Failed to connect to server.");
        }
    }
    
    /**
     * 
     * @param evt 
     */
    private void textTyped(java.awt.event.KeyEvent evt) {
        char c = evt.getKeyChar();
        if (c == '\n'){
        	   try {
                       // If the user has selected the private button send to everyone
                       if(privateMessageBool == false){
                           // send to the talk method the user and the string entered
                           port.talk(user + " : " + textString);
                           // deleted the entered text
                           myText.setText("");
                       } 
                       // If the user has selected the private button
                       else if (privateMessageBool == true){
                           // send to the private chat method the user, user to send
                           // to and the strig enetered
                           port.privateMessage(privateMessageUser.getText(), user, textString);
                           // deleted th entered text
                           myText.setText("");
                       }
        	   }
        	   catch (Exception ie) {
        		   otherText.append("Failed to send message.");
        	   }
            textString = "";
        } else {
            textString = textString + c;
        }
    }
    
    /**
     * 
     * @param args 
     */
    public static void main(String[] args) {
    	final String host = "localhost";
    	javax.swing.SwingUtilities.invokeLater(new Runnable() {
    		Main client = new Main();
    		public void run() {
    			client.initComponents(host);
    		}
    	});
    	
    }
        public static String getUsername() {
        return user;
    }
}

// 
class TextThread extends Thread {
    ObjectInputStream in;
    JTextArea otherText;
    int id;
    ChatServer port;
    
    TextThread(JTextArea other, int id, ChatServer port) throws IOException
    {
        otherText = other;
        this.id = id;
        this.port = port;
    }
    
    public void run() {
        while (true) {
            try {    
                // Send the username to the listen method
                String newText = port.listen(Main.getUsername());
                if (!newText.equals("")) {
                    otherText.append(newText + "\n");
                }
                Thread.sleep(1000);
            }
            catch (Exception e) {
                otherText.append("Error reading from server.");
            }  
        }
    }
}