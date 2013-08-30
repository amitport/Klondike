package com.cardsForest.platform;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;

import com.cardsForest.glue.Motion;

import static com.cardsForest.platform.Shortcuts.*;

/**
 * this is the main graphic component the game is drawn on <br>
 * {@link MouseInputHandler} listens on this
 * and transfer events to {@link Game}
 *  
 * @author Amit Portnoy
 *
 */
public class GameCanvas extends Canvas{

	private static final long serialVersionUID = 0L;

	/** used for enabling double buffer */
	private BufferStrategy strategy = null;
	
	/**
	 * initiate GameCanvas <br>
	 * assign it with {@link MouseInputHandler}
	 */
	public GameCanvas(){
		MouseInputHandler msh = new MouseInputHandler();
		addMouseListener(msh);
		addMouseMotionListener(msh);
	}
	
	/**
	 * create the buffer strategy must be called after 
	 * canvas was created and assigned a peer
	 */
	public void initStrategy(){
		createBufferStrategy(2);
		strategy = getBufferStrategy();
	}
	
	/**
	 * update the canvas with new bounds <br>
	 * overriding method to add update to the Game
	 */
	@Override
	public void setBounds(int x, int y, int width, int height){
		super.setBounds(x, y, width, height);
		try {
			Game.queue.put(new Dimension(getWidth(),getHeight()));
		} catch (InterruptedException e) {
			error("interrupted while trying to update screen size");
		}		
	}
	
	@Override
	public void update(Graphics gt){
		paint(gt);
	}
	
	@Override
	public void paint(Graphics gt){
		try {
			Game.queue.put("paint");
		} catch (InterruptedException e) {
			error("interrupted while trying to update screen size");
		}	
	}
	
	/**
	 * paint the game - double buffer
	 * @param anim additional object to draw canvas delegates handling it back to Game
	 */
	public void paintGame(Motion anim){
		//get the current draw buffer
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		
		//set better quality rendering
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		//draw background
		g.setColor(new Color(38,89,38));
		g.fillRect(0, 0, 
	    		   getWidth() - 1,
	    		   getHeight() - 1);

		//draw frame
		g.setColor(Color.RED);
        g.drawRect(0, 0, 
        	getWidth() - 1,
        	getHeight() - 1);
        
        //draw game
        Game.draw(g,anim);
        
        //end double buffer paint (display to screen)
		Toolkit.getDefaultToolkit().sync();
		g.dispose();
		strategy.show();
	}

}
