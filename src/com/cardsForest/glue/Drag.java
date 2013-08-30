/**
 * 
 */
package com.cardsForest.glue;

import java.awt.Graphics;
import java.awt.Point;

/**
 * holds information relevant to current drag event
 * <p>
 * {@link com.cardsForest.platform.MouseInputHandler} creates this event when dragging
 * started, notifies it when mouse moved (by calling {@code calcOffsetToPoint})
 * and when dragging ended (by calling {@code end})
 * <p>
 * it is sent to {@link com.cardsForest.platform.Game}'s queue and
 * used to update {@link Selection}'s location 
 * at constant intervals through 
 * {@link com.cardsForest.platform.Game}'s {@code drawMotion}
 * <p>
 * this a special type of {@link Motion} that does not necessarily change
 * in a periodic manner and is not draw-able, but still, it is useful to treat
 * it as such
 * 
 * @author Amit Portnoy
 *
 */
public class Drag implements Motion{

	/** position of drag start */
	Point start;
	/** position of drag end */
	Point end;
	/** offset between start to current location */
	Point offset;
	
	/** true if drag is done */
	volatile boolean done;
	
	
	/**
	 * initialize a new drag event
	 * @param point the starting point
	 */
	public Drag(Point point) {
		start = point;
		end = null;
		offset = new Point(0,0);
		done = false;
	}

	/**
	 * get the start position
	 * @return start position
	 */
	public Point getStart(){
		return start;
	}
	
	/*******************/
	/* Notify methods  */
	/*******************/
	
	/**
	 * calculate the offset between start position to current position (after position changed) 
	 * @param x x coordinate
	 * @param y y coordinate
	 */
	public synchronized void calcOffsetToPoint(int x, int y) {
		offset.x = x - start.x;
		offset.y = y - start.y;
	}
	

	/**
	 * notifies this object that dragging is finished
	 * @param point the position of dragging finish
	 */
	public void end(Point point) {
		end = point;
		done = true;
	}

	/*******************/
	/* Motion methods  */
	/*******************/
	
	@Override
	public void init() {
		if (!Selection.get().isAvailable()){
			//if we don't have available selection
			//no need to start motion
			done = true;
		}
	}

	@Override
	public synchronized void updateFrame() {
		//update selection with current offset
		Selection.get().setOffset(offset.x,offset.y);
		
	}
	
	@Override
	public void draw(Graphics g) {
	//do nothing
	//drag motion is not draw-able 
	//since game always draws the 
	//selection when available
	}
	

	@Override
	public boolean isDone() {
		return done;
	}
}
