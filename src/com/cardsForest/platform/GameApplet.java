package com.cardsForest.platform;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;



import static com.cardsForest.platform.Shortcuts.*;


/**
 * the actual applet class
 * contains GUI initializations
 * 
 * Generally delegates user event to {@link GameCanvas}
 * 
 * @author Amit Portnoy
 *
 */
public class GameApplet extends JApplet {
	
	private static final long serialVersionUID = 0L;
	
	/* GUI elements */
	
	/** the canvas the game is drawn onto */
	GameCanvas canvas;
	/** the status field can be updated by the game */
	private JTextField status;
	
	boolean started = false;
	
	/******************/
	/* Applet methods */
	/******************/
	
	/**
	 * create and initialize the 
	 * GUI and the game <br>
	 * note the order of operations here is very IMPORTANT
	 */
	@Override
    public void init() {
		/* create the game thread */
		Game.create(this);
		
		//TODO currently loading of images is done in Game creation
		//should move it to applet and create proxy graphics while 
		//images are being download
		
		/* create the GUI */
		createGUI();  	
	} 

    /* I don't really have any use for those functions now */
    @Override
    public void start() {
    	if (!started){
		    //initiate the double buffer strategy    
		    canvas.initStrategy();
    		/* start the Game */
    		Game.queue.offer("start");
    		started = true;
    	}
    }
    @Override
    public void stop() {
    }
    @Override
    public void destroy() {
    }
    
	/**********************/
	/* GameApplet methods */
	/**********************/
    
    /**
     * create all the GUI elements <br>
     * <p>
     * at this point, those are: <br>
     * GameCanvas in it's own panel
     * deal button in statusPanel
     * status field in statusPanel
     */
	private void createGUI(){
		//got to initialize the GUI on the event dispatch thread
    	swing(new Runnable(){
    		
			public void run(){
		    	setSize(800,600); //the size will actually be controlled by the HTML
		    	
		    	// create the canvas
			    canvas = new GameCanvas();
			    // create the status field
			    status = new JTextField(15);
			    status.setEditable(false);			    
			    // create the deal button
			    JButton deal = new JButton("Deal");
			    deal.setAction(new dealAction());
			    
			    //wrap in panel because we are using swing
			    JPanel canvasPanel = new JPanel();
			    canvasPanel.setLayout(new BorderLayout());
			    canvasPanel.add(canvas);
			    
			    //add it to the applet
			    add(canvasPanel);
		    	  
			    //create the status and command panel
				JPanel statusPanel = new JPanel();
				statusPanel.setLayout(new FlowLayout(FlowLayout.LEFT));      
				statusPanel.setBackground(Color.LIGHT_GRAY);		    			    
			    //add the deal button
			    statusPanel.add(deal);
			    //add the status field
			    status.setBackground(statusPanel.getBackground());
			    statusPanel.add(status);
			    
			    //add it to the applet
			    add(statusPanel,BorderLayout.NORTH);
			}
		});
	}
	
	/**
	 * set the status bar to a selected string
	 * @param str the string to put as status
	 */
	public void setStatus(final String str){
		swing(new Runnable(){
			public void run(){
				status.setText(str);
			}
		});	
	}
	
	
	
	/**********************/
	/* known game actions */
	/**********************/
	
	private class dealAction extends AbstractAction{

		private static final long serialVersionUID = -816395454571147188L;

		public dealAction(){
			putValue(Action.NAME, "Deal");
			putValue(Action.SHORT_DESCRIPTION, "start a new game");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			Game.queue.offer("deal");
		}	
	}
	
    
}
