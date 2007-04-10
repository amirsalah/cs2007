/*
 * put your module comment here
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 */


package  com.crionics.blowfish;

/*
 A basic extension of the com.sun.java.swing.JApplet class
 */
import  javax.swing.*;
import  java.awt.*;


/**
 * put your documentation comment here
 */
public class GUIBlowfish extends JApplet {
    Blowfish MyBlowfish;
    JPanel control_panel;
    JButton Encipher;
    JButton Decipher;
    JButton Passphrase;
    JButton About;
    JPanel text_panel;
    JScrollPane scrollpane;
    JTextArea textarea;

    /**
     * put your documentation comment here
     */
    public void init () {
        getContentPane().setLayout(new BorderLayout(0, 0));
        setSize(400, 300);
        control_panel = new JPanel();
        control_panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        control_panel.setBounds(0, 0, 400, 35);
        control_panel.setFont(new Font("Dialog", Font.PLAIN, 12));
        control_panel.setForeground(new Color(0));
        control_panel.setBackground(new Color(-3355444));
        getContentPane().add("North", control_panel);
        Encipher = new JButton();
        Encipher.setText("Encipher");
        Encipher.setActionCommand("encipher");
        Encipher.setToolTipText("Click me to encipher the text below");
        Encipher.setBounds(21, 5, 85, 25);
        Encipher.setFont(new Font("Dialog", Font.BOLD, 12));
        Encipher.setForeground(new Color(0));
        Encipher.setBackground(new Color(-3355444));
        control_panel.add(Encipher);
        Decipher = new JButton();
        Decipher.setText("Decipher");
        Decipher.setActionCommand("Decipher");
        Decipher.setToolTipText("Click me to decipher the text below");
        Decipher.setBounds(111, 5, 85, 25);
        Decipher.setFont(new Font("Dialog", Font.BOLD, 12));
        Decipher.setForeground(new Color(0));
        Decipher.setBackground(new Color(-3355444));
        control_panel.add(Decipher);
        Passphrase = new JButton();
        Passphrase.setText("PassPhrase");
        Passphrase.setActionCommand("PassPhrase");
        Passphrase.setToolTipText("Click me to change your passphrase");
        Passphrase.setBounds(201, 5, 105, 25);
        Passphrase.setFont(new Font("Dialog", Font.BOLD, 12));
        Passphrase.setForeground(new Color(0));
        Passphrase.setBackground(new Color(-3355444));
        control_panel.add(Passphrase);
        About = new JButton();
        About.setText("About");
        About.setActionCommand("About");
        About.setToolTipText("About the application....");
        About.setBounds(311, 5, 67, 25);
        About.setFont(new Font("Dialog", Font.BOLD, 12));
        About.setForeground(new Color(0));
        About.setBackground(new Color(-3355444));
        control_panel.add(About);
        text_panel = new JPanel();
        text_panel.setLayout(new BorderLayout(0, 0));
        text_panel.setBounds(0, 35, 400, 265);
        text_panel.setFont(new Font("Dialog", Font.PLAIN, 12));
        text_panel.setForeground(new Color(0));
        text_panel.setBackground(new Color(-3355444));
        getContentPane().add("Center", text_panel);
        scrollpane = new JScrollPane();
        scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollpane.setOpaque(true);
        scrollpane.setBounds(0, 0, 400, 265);
        scrollpane.setFont(new Font("Dialog", Font.PLAIN, 12));
        scrollpane.setForeground(new Color(0));
        scrollpane.setBackground(new Color(16777215));
        text_panel.add("Center", scrollpane);
        textarea = new JTextArea();
        textarea.setMargin(new java.awt.Insets(0, 0, 0, 0));
        textarea.setLineWrap(true);
        textarea.setBounds(24, 24, 263, 207);
        textarea.setFont(new Font("Dialog", Font.PLAIN, 14));
        textarea.setForeground(new Color(0));
        textarea.setBackground(new Color(16777215));
        scrollpane.getViewport().add(textarea);
        SymMouse aSymMouse = new SymMouse();
        About.addMouseListener(aSymMouse);
        Passphrase.addMouseListener(aSymMouse);
        Encipher.addMouseListener(aSymMouse);
        Decipher.addMouseListener(aSymMouse);
        MyBlowfish = new Blowfish();
    }

    class SymMouse extends java.awt.event.MouseAdapter {

        /**
         * put your documentation comment here
         * @param event
         */
        public void mousePressed (java.awt.event.MouseEvent event) {
            Object object = event.getSource();
            if (object == Passphrase)
                Passphrase_mousePressed(event);
            else if (object == Encipher)
                Encipher_mousePressed(event);
            else if (object == About)
                About_mouseClicked(event);
            else if (object == Decipher)
                Decipher_mousePressed(event);
        }
    }

    /**
     * put your documentation comment here
     * @param event
     */
    void About_mouseClicked (java.awt.event.MouseEvent event) {
        // Show the JOptionPane
        String msg = "This tiny JFC applet encrypt and decrypt text using the blowfish algorithm.\n I added a 32 bits CRC check and a '64 bits to 11 cars' routines.\nsource code is public, Enjoy!\n\nOlivier Refalo orefalo@yahoo.com\nCreated on: 7th october 1997\nLast updated on: 18th july 2001";
        JOptionPane.showMessageDialog(null, msg, "About the author...", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * put your documentation comment here
     * @param event
     */
    void Passphrase_mousePressed (java.awt.event.MouseEvent event) {
        // Show the JOptionPane
        String newPassPhrase = JOptionPane.showInputDialog("Enter your new passphrase");
        if (newPassPhrase != "")
            MyBlowfish.setPassPhrase(newPassPhrase);
    }

    /**
     * put your documentation comment here
     * @param event
     */
    void Encipher_mousePressed (java.awt.event.MouseEvent event) {
        // Repaint the JPanel
        String newStr = MyBlowfish.cipherWithNewline(textarea.getText(), 40);
        textarea.setText(newStr);
    }

    /**
     * put your documentation comment here
     * @param event
     */
    void Decipher_mousePressed (java.awt.event.MouseEvent event) {
        // Hide the JButton
        try {
            String newStr = MyBlowfish.decipher(textarea.getText());
            textarea.setText(newStr);
        } catch (Exception e) {
            textarea.setText("Error");
        }
    }

    /**
     * put your documentation comment here
     * @param arg[]
     */
    public static void main (String arg[]) {

      JFrame f=new JFrame();
      GUIBlowfish gbf=new GUIBlowfish();
      f.getContentPane().add(gbf);

      gbf.init();
      f.setSize(400,300);
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.show();
      gbf.start();

    }
}



