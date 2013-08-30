package com.cardsForest.glue;

import java.awt.Graphics;

/**
 * this interface is meant to work with {@link com.cardsForest.platform.Game}'s {@code drawMotion} method.
 * <br>
 * a motion is a description of periodic change on the screen (i.e. animation).
 * <p>
 *  ({@link Drag} is a special type of motion that does not necessarily change
 *  in a periodic manner and is not draw-able, but still, it is useful to treat
 *  it as such)
 *    
 * @see com.cardsForest.platform.Game
 * @see Drag
 * 
 * @author Amit Portnoy
 *
 */
public interface Motion{
	
	/**
	 * initialize the motion
	 */
	public void init();
	
	/**
	 * update the animation in one frame
	 */
	public void updateFrame();

	/**
	 * draw the motion
	 * @param g graphics to draw on
	 */
	public abstract void draw(Graphics g);
	
	
	/**
	 * return true if motion is done
	 * @return true if motion is done
	 */
	public boolean isDone();
	
	
	
}
