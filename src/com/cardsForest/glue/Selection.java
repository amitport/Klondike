/**
 * 
 */
package com.cardsForest.glue;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import com.cardsForest.foundations.Stack;


/**
 * manages the current selection got by user (singleton)
 * <p>
 * 
 * - {@link com.cardsForest.logic.Behavior} decides when the select a group of cards <br>
 * (in current implementation that is always a subgroup of a certain {@link Stack}
 * for a specific index to the top of the stack) <br>
 * - {@link com.cardsForest.logic.Behavior} also decides when to end the selection and if it needed
 * to move it to another stack <br>
 * - {@link Drag} is capable of updating the selection display position
 * by calling {@code setOffset} (in case the user is dragging the selection)  <br>
 * - {@link com.cardsForest.platform.Game} is drawing the selection if available by calling {@code draw}
 *  
 * 
 * @author Amit Portnoy
 *
 */
public class Selection {
	
	/** the stack containing the current selection */
	public Stack stack;
	/** the source of current selection (stack is taken from src) */
	public Stack src;
	
	/** start position at selection time */
	int x,y;	 
	/** offset from start position to draw (updated by {@Drag} */
	Point offset;
	
	/** singleton instance */
	static Selection selection;
	
	/**
	 * initialize the selection
	 */
	private Selection(){
		src = null;
		stack = Stack.newEmptyStack();
		stack.getSprite().spread = true;//TODO may have type of sprite extending from stackSprite
		offset = new Point();
	}
	
	/**
	 * get the single instance of the selection
	 * @return singleton instance
	 */
	public static Selection get(){
		if (selection == null){
			selection = new Selection();
		}
		return selection;
	}
	
	/**
	 * select a subgroup of src from startIndex to the end of src
	 * @param src the source for the selection
	 * @param startIndex the first index for the subgroup
	 */
	public void select(Stack src,int startIndex){
		if (src == null){
			throw new NullPointerException();
		}
		if (startIndex < 0 || startIndex >= src.size()){
			throw new IndexOutOfBoundsException();
		}
		
		this.src = src;
		
		//move cards to selection immediately
		src.moveTo(stack,src.getCards().size()-startIndex,true);
		
		//set starting position to be where source cards are
		stack.getSprite().translateTo(src.getSprite(),startIndex);
		
		//remember the starting position
		x = stack.getSprite().x;
		y = stack.getSprite().y;
	}

	/**
	 * end current selection <br>
	 * <b> most have selection available when calling this function </b> <br>
	 * 
	 * @param dst stack to move the selection to or {@code null} to cancel selection
	 */
	public void selectEnd(Stack dst){
		if (! isAvailable()){
			//got nothing to do
			return; 
		}
		
		if (dst != null){
			//move to destination
			stack.moveTo(dst,stack.getCards().size());
		}else{
			//cancel selection
			//move back to source
			stack.moveTo(src,stack.getCards().size());		
		}
		
		//reset globals
		offset.x = 0;
		offset.y = 0;
		src = null;
	}
	
	/**
	 * @return true if selection is available
	 */
	public boolean isAvailable(){
		return selection.src != null;
	}
	
	/**
	 * draw the selection <br>
	 * while using offset updated from {@link Drag} 
	 * 
	 * @param g the graphics to draw on
	 */
	public void draw(Graphics g){
		stack.getSprite().x = x + offset.x;
		stack.getSprite().y = y + offset.y;

		stack.getSprite().draw(g,true);
	}
	
	/**
	 * update the selection with a screen size change
	 * @param w new screen width
	 * @param h new screen height
	 */
	public void updateScreenSize(int w, int h){
		stack.getSprite().updateScreenSize(w, h);
		
		if (isAvailable()){
			//set starting position to be where source cards are
			stack.getSprite().translateTo(src.getSprite(),src.getSprite().cards.size());
			
			//remember the starting position
			x = stack.getSprite().x;
			y = stack.getSprite().y;
		}
		
	}

	/**
	 * set offset to draw the selection in <br>
	 * this method is update by {@link Drag}'s {@code updateFrame}
	 * @param x
	 * @param y
	 */
	public void setOffset(int x, int y) {
		offset.x = x;
		offset.y = y;
	}

	/**
	 * this is used after dragging ended so that we can 
	 * better assign a drag target
	 * @return the bound of the selection sprite
	 */
	public Rectangle getBounds() {
		//call updateBounds since we dragged
		//the selection without updating 
		//the bounds with each setOffset
		stack.getSprite().updateBounds();

		return stack.getSprite().getBounds();
	}
}
