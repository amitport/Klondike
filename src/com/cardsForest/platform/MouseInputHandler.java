package com.cardsForest.platform;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.cardsForest.glue.Drag;

/**
 * responsible of getting mouse events targeted at {@link GameCanvas}
 * and transfer them to {@link Game}'s queue
 * <p>
 * relevant events: click, started drag
 * 
 * @author Amit Portnoy
 *
 */
public class MouseInputHandler extends MouseAdapter {
	
	/** marks that dragging has started */
	boolean startedDrag = false;
	//counter is useful to soften the start dragging action
	//we decide that we need to get several drag events before actually decide it's a drag and not
	// a simple click
	int dragCounter = 0;
	final int COUNT_NUM = 5;
	boolean counting = false;
	
	
	/** holds the current drag event information */
	Drag drag;
	
	@Override
	public void mouseClicked(MouseEvent e) {		
		if(MouseEvent.BUTTON1_MASK == (e.getModifiers() & MouseEvent.BUTTON1_MASK)){
			//clicked left mouse button
			//add event to Game's queue
			Game.queue.offer(e);
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e){
		
		if(MouseEvent.BUTTON1_MASK == (e.getModifiers() & MouseEvent.BUTTON1_MASK)){
			//dragging left mouse button
			if (!startedDrag){
				//started dragging
				//create new Drag object and add it to Game's queue
				startedDrag = true;
				drag = new Drag(e.getPoint());	
				counting = true;
				dragCounter = 0;
			}else{
				if (counting){
					if (dragCounter < COUNT_NUM){
						dragCounter++;
					}else{
						counting = false;
						Game.queue.offer(drag);
					}
						
				}

				//continue dragging
				//update current Drag object with the new mouse location
				drag.calcOffsetToPoint(e.getX(),e.getY());
			}
		}
	}	
	
	@Override
	public void mouseReleased(MouseEvent e){
		
		if(MouseEvent.BUTTON1_MASK == (e.getModifiers() & MouseEvent.BUTTON1_MASK)){
			//released left mouse button
					
			if(startedDrag){
				if (counting){
					Game.queue.offer(e);
				}else{
					//finished dragging
					drag.end(e.getPoint());					
				}	

				startedDrag = false;
			}
		}
	}
}


