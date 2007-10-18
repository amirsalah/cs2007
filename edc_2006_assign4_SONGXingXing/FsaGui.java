


import javax.swing.*; 
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.DecimalFormat;
//import java.util.Iterator;
import java.io.* ;
import java.awt.* ;
import java.util.*;

/**
 * @author xingxing SONG
 *
 */
public class FsaGui extends JFrame {

	private Fsa m_fsa ; 
	private EventSequence  m_eventSequence ;
	private JMenu m_fileMenu = new JMenu( "File" );
	private JMenu m_editMenu = new JMenu( "Edit" );
	
	private JMenu m_testMenu = new JMenu( "Test" );
	private FsaGuiPanel m_panelDispaly = new FsaGuiPanel();
	private JTextArea m_textArea ;
	
	private String m_strCurEvent = "" ; // current event
	private Iterator<Object> m_IterEventSequence ;
	public JTextArea getTextArea()
	{
		return m_textArea ;
	}
	
	void setFsa(Fsa fsa)
	{
		m_fsa = fsa ;
		m_panelDispaly.setFsa(fsa);
	}
	
	Fsa getFsa()
	{
		return m_fsa ; //m_panelDispaly.GetFsa();
	}
	
	void setEventSequence(EventSequence eventSequence)
	{
		m_eventSequence = eventSequence ;
	}
	
	EventSequence getEventSequence()
	{
		return m_eventSequence ; //m_panelDispaly.GetFsa();
	}
	
	public FsaGui()
	{
		this.setTitle("Xingxing SONG Fsa");
		this.setSize(800,600);
		
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
		JMenuBar mb = new JMenuBar();
		m_fileMenu.add( createFileLoadItem() );
		m_fileMenu.add( createEventFileLoadItem() );
		m_fileMenu.add( createFileSaveItem() );
		m_fileMenu.add( createFileExitItem() );
		mb.add( m_fileMenu );
		
		m_editMenu.add( createEditDelItem() );
		m_editMenu.add( createEditSetInitItem() );
		m_editMenu.add( createEditNewStateItem() );
		m_editMenu.add( createEditNewTransitionItem() );
		mb.add( m_editMenu );
		// song test code --{
		//m_testMenu.add( createTestToStringItem() );
		//mb.add( m_testMenu ) ;
//		 song test code --}
			
		this.setJMenuBar( mb );    
		
		m_textArea = new JTextArea(5, 20);
		JScrollPane scrollPanetextArea = 
		    new JScrollPane(m_textArea,
		                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		                    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		m_textArea.setEditable(true);
		
		//m_panelDispaly.setSize( 1000, 1000 );

		JScrollPane scrollPaneDispaly = 
		    new JScrollPane(m_panelDispaly,
		                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		                    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);   
		//scrollPaneDispaly.setPreferredSize(new Dimension(200, 200));
		
		//JScrollPane scrollPaneDispaly = 
		//    new JScrollPane() ;
		//scrollPaneDispaly.getViewport().add( m_panelDispaly );
		m_panelDispaly.setPreferredSize(new Dimension(1280 , 1024));  //set new panel size
		scrollPaneDispaly.setViewportView(m_panelDispaly);
		m_panelDispaly.setFsaGui(this);
		//scrollPaneDispaly.revalidate();  //revalidate panel

		JPanel controlPanel = new JPanel();
		controlPanel.add( createStepButton() ); 
		controlPanel.add( createResetButton() ); 
		controlPanel.add( createPlayButton() );
		
		
		JPanel items = new JPanel();
		items.setLayout( new BorderLayout() );
		items.add( controlPanel, BorderLayout.NORTH );
		items.add( scrollPaneDispaly, BorderLayout.CENTER );
		items.add( scrollPanetextArea, BorderLayout.SOUTH );
		
		
		this.setContentPane(items);
        //Display the window.
        //this.pack();
        this.setVisible(true);	
        
        output("output : ");
        
	}
	
	private JMenuItem createTestToStringItem()
	{
		JMenuItem item = new JMenuItem("Fsa.toString->output");
		class MenuItemListener implements ActionListener
		{
			public void actionPerformed(ActionEvent event)
			{
				String str = m_fsa.toString();
				output(str) ;
			}
		}
		ActionListener listener  = new MenuItemListener();
		item.addActionListener(listener);
		return item;
	}
	private JMenuItem createFileExitItem()
	{
		JMenuItem item = new JMenuItem("Exit");
		class MenuItemListener implements ActionListener
		{
			public void actionPerformed(ActionEvent event)
			{
				System.exit( 0 );
			}
		}
		ActionListener listener  = new MenuItemListener();
		item.addActionListener(listener);
//		 Setting the accelerator
		item.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		
		return item;
	}	
	
	private JMenuItem createFileLoadItem()
	{
		JMenuItem item = new JMenuItem("Load FSA...");
		class MenuItemListener implements ActionListener
		{
			JFileChooser fc = new JFileChooser( System.getProperty( "user.dir" ) );	
			public void actionPerformed(ActionEvent event) 
			{
				//if ( state == NOCD )
				
					int result = fc.showOpenDialog(FsaGui.this);
					if ( result == JFileChooser.APPROVE_OPTION ) 
					{
						boolean bOK = true ;
						m_panelDispaly.setEnable(false) ;
						try {										    	
				        FileInputStream in = null;				 
				        try {
				        	output("Open file : " + fc.getSelectedFile().getName()) ;
				            in = new FileInputStream( fc.getSelectedFile().getName());
				            getFsa().read(in) ;
				           
				        }
				        catch (IOException e) {
				            System.err.println("Caught IOException: " 
				                                + e.getMessage());
				            output("Caught IOException: " 
	                                + e.getMessage() );

				            bOK = false ;
				        }
				        catch (FsaFileException e) {
				            System.err.println("Caught FsaFileException: " 
				                                + e.toString());
				            output("Caught FsaFileException: " 
	                                + e.toString() );
				            bOK = false ;				            
				        }
				        finally {
				        	if (in != null) {
				                in.close();
				            }	           
				        }					
						}
						catch(IOException e)
						{
							bOK = false ;
						}
						
						m_panelDispaly.setEnable(bOK) ;
						repaint() ;
					}
					
				
				
			}
		}
		ActionListener listener  = new MenuItemListener();
		item.addActionListener(listener);
		// Setting the accelerator
		item.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_F, ActionEvent.CTRL_MASK));

		return item;
	}	

	private JMenuItem createFileSaveItem()
	{
		JMenuItem item = new JMenuItem("Save FSA...");
		class MenuItemListener implements ActionListener
		{
			JFileChooser fc = new JFileChooser( System.getProperty( "user.dir" ) );	
			public void actionPerformed(ActionEvent event) 
			{
				//if ( state == NOCD )
				
					int result = fc.showSaveDialog(FsaGui.this);
					if ( result == JFileChooser.APPROVE_OPTION ) 
					{
						boolean bOK = true ;
						//m_panelDispaly.setEnable(false) ;
						try {										    	
				        //FileOutputStream out = null;	
							BufferedWriter out = new BufferedWriter(new FileWriter(fc.getSelectedFile().getName()));
				        try {
				        	output("Save FSA to file : " + fc.getSelectedFile().getName()) ;	 
				            out.write(getFsa().toString());
				           
				        }
				        catch (IOException e) {
				            System.err.println("Caught IOException: " 
				                                + e.getMessage());
				            output("Caught IOException: " 
	                                + e.getMessage() );

				            bOK = false ;
				        }
				        
				        finally {
				        	if (out != null) {
				        		out.close();
				            }	           
				        }					
						}
						catch(IOException e)
						{
							bOK = false ;
						}
						
						//m_panelDispaly.setEnable(bOK) ;
						//repaint() ;
					}
					
				
				
			}
		}
		ActionListener listener  = new MenuItemListener();
		item.addActionListener(listener);
//		 Setting the accelerator
		item.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		return item;
	}
	
	private JMenuItem createEventFileLoadItem()
	{
		JMenuItem item = new JMenuItem("Load events...");
		class MenuItemListener implements ActionListener
		{
			JFileChooser fc = new JFileChooser( System.getProperty( "user.dir" ) );	
			public void actionPerformed(ActionEvent event) 
			{
				//if ( state == NOCD )
				
					int result = fc.showOpenDialog(FsaGui.this);
					if ( result == JFileChooser.APPROVE_OPTION ) 
					{
						boolean bOK = true ;
						//m_panelDispaly.setEnable(false) ;
						try {										    	
				        FileInputStream in = null;				 
				        try {
				        	output("Open file : " + fc.getSelectedFile().getName()) ;
				            in = new FileInputStream( fc.getSelectedFile().getName());
				            getEventSequence().read(in) ;
				            
				            m_IterEventSequence = getEventSequence().getEvents().iterator() ;
				        }
				        catch (IOException e) {
				            System.err.println("Caught IOException: " 
				                                + e.getMessage());
				            output("Caught IOException: " 
	                                + e.getMessage() );

				            bOK = false ;
				        }
				        /*catch (EventSequenceException e) {
				            System.err.println("Caught FsaFileException: " 
				                                + e.toString());
				            output("Caught FsaFileException: " 
	                                + e.toString() );
				            bOK = false ;				            
				        }*/
				        finally {
				        	if (in != null) {
				                in.close();
				            }	           
				        }					
						}
						catch(IOException e)
						{
							bOK = false ;
						}
						
						//m_panelDispaly.setEnable(bOK) ;
						repaint() ;
					}
					
				
				
			}
		}
		ActionListener listener  = new MenuItemListener();
		item.addActionListener(listener);
//		 Setting the accelerator
		item.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		
		return item;
	}		
	
	private JMenuItem createEditDelItem()
	{
		JMenuItem item = new JMenuItem("Delete selection");
		class MenuItemListener implements ActionListener
		{
			public void actionPerformed(ActionEvent event)
			{
				m_panelDispaly.deleteSelection();
			}
		}
		ActionListener listener  = new MenuItemListener();
		item.addActionListener(listener);
//		 Setting the accelerator
		item.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_D, ActionEvent.CTRL_MASK));
		
		return item;
	}
	
	private JMenuItem createEditSetInitItem()
	{
		JMenuItem item = new JMenuItem("Set initial state");
		class MenuItemListener implements ActionListener
		{
			public void actionPerformed(ActionEvent event)
			{
				if(!m_bPlay)
				{
					if(!m_panelDispaly.setInitialState())
					{
						output("Warning: Initial state selection error");
					}
				}
				else
				{
					output("Warning: Cann't set the initial state during a simulation");
				}
			}
		}
		ActionListener listener  = new MenuItemListener();
		item.addActionListener(listener);
//		 Setting the accelerator
		item.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_I, ActionEvent.CTRL_MASK));
		
		return item;
	}
	
	private JMenuItem createEditNewStateItem()
	{
		JMenuItem item = new JMenuItem("New state");
		class MenuItemListener implements ActionListener
		{
			public void actionPerformed(ActionEvent event)
			{
				m_panelDispaly.newState();
			}
		}
		ActionListener listener  = new MenuItemListener();
		item.addActionListener(listener);
//		 Setting the accelerator
		item.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		
		return item;
	}
	
	private JMenuItem createEditNewTransitionItem()
	{
		JMenuItem item = new JMenuItem("New transition");
		class MenuItemListener implements ActionListener
		{
			public void actionPerformed(ActionEvent event)
			{
				m_panelDispaly.newTranstion();
			}
		}
		ActionListener listener  = new MenuItemListener();
		item.addActionListener(listener);
//		 Setting the accelerator
		item.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		
		return item;
	}
	
	
	private boolean m_bEventEnable = true;//when UnhandledEventException occured, stop.
	private boolean stepEvent() {
		try {
			if (true == m_bEventEnable) {
				if (m_IterEventSequence.hasNext()) {
					String strStep = (String) (m_IterEventSequence.next());
					String strOut = getFsa().step(strStep).toString();

					output(strOut);
					return true;
				} else {
					output("Warning: No events");
					return false;
				}
			}
			else
			{
				return false;
			}
		} catch (UnhandledEventException e) {
			output("Caught UnhandledEventException: " + e.getMessage());
			m_bEventEnable = false;
			return false;
		} catch (NoSuchElementException e) {
			output("Warning: No events have been loaded ");
			return false;
		} catch (NullPointerException e) {
			output("Warning: No events have been loaded ");
			return false;
		}
	}
	private JButton createStepButton()
	{
		JButton item = new JButton("Step");
		class ButtonListener implements ActionListener
		{
			
			public void actionPerformed(ActionEvent event) 
			{
				stepEvent();
				repaint() ;
			}
		}
		ActionListener listener  = new ButtonListener();
		item.addActionListener(listener);
		return item;
	}	
	
	private boolean resetEvent() {
		try {
			getFsa().reset();
			m_IterEventSequence = getEventSequence().getEvents().iterator();
			output("Reset");
			m_bEventEnable = true;
			return true;
		} catch (NullPointerException e) {
			output("Warning: No events have been loaded ");
			return false;
		}
	}
	
	private JButton createResetButton()
	{
		JButton item = new JButton("Reset");
		class ButtonListener implements ActionListener
		{		
			public void actionPerformed(ActionEvent event) 
			{
				resetEvent();
				repaint();
			}
		}
		ActionListener listener  = new ButtonListener();
		item.addActionListener(listener);
		return item;
	}		
	
	/**
	 * createPlayButton
	 * @return PlayButton
	 */
	private javax.swing.Timer m_timer ;
	private boolean m_bPlay = false;
	JButton m_btnPlay = new JButton("Play");
	private JButton createPlayButton()
	{
		//JButton item = new JButton("Play");
//		A timer for the clock
		class TimerListener implements ActionListener
		{
			
			public void actionPerformed(ActionEvent evt) {
				if (false == stepEvent()) {
					m_timer.stop();
					m_btnPlay.setText("Play");
					output("Events are exhausted. Stop.");
					m_bPlay = false;
				}
			
				repaint();
			}
		}
		m_timer = new javax.swing.Timer(1000, new TimerListener() );
		
		class ButtonListener implements ActionListener
		{				
			public void actionPerformed(ActionEvent event) {
				if(false == m_bPlay)// it is stop before, play now
				{
					m_timer.start();
					m_btnPlay.setText("Stop");
					output("Play...");
					m_bPlay = true;
				}
				else // it is playing before, stop now
				{
					m_timer.stop();
					m_btnPlay.setText("Play");
					output("Stop!");
					m_bPlay = false;
				}
				
			}
		}
		ActionListener listener  = new ButtonListener();
		m_btnPlay.addActionListener(listener);
		return m_btnPlay;
	}		
	
	public void output(String str)
	{
		m_textArea.append(str + "\n" ) ;
		m_textArea.setSelectionStart(m_textArea.getText().length()) ;

	}
}



