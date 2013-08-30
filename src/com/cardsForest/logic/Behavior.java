package com.cardsForest.logic;

import com.cardsForest.foundations.Stack;

/**
 * defines the behavior of {@link Stack} object 
 * by supplying methods for stack's behavior on events
 * (click / double click)  
 * 
 * @author Amit Portnoy
 *
 */
public class Behavior {

	static private Behavior defaultBehavior = null;
	
	/** provided as input to click methods, 
	 * signifies that no specific card was clicked */
	static final int NO_INDEX = -1;
	
	/**
	 * create a new instance of behavior<br>
	 * TODO consider <b>protected</b>
	 */
	protected Behavior(){
		//default behavior - do nothing
	}
	
	/**
	 * actions performed on stack being clicked
	 * @param stack the stack that was clicked
	 * @param cardIndex the index of the card clicked (-1 when index is invalid)
	 */
	public void click(Stack stack, int cardIndex){
		//default behavior - do nothing
	}

	/**
	 * actions performed on stack being double clicked
	 * @param stack the stack that was clicked
	 * @param cardIndex the index of the card clicked (-1 when index is invalid)
	 */
	public void doubleClick(Stack stack, int cardIndex){
		//default behavior - delegate to click
		click(stack,cardIndex);
	}

	/**
	 * check if this behavior is of the same group as a given behavior
	 * @param behavior the behavior to compare with
	 * @return true if both behaviors are of the same group
	 */
	public boolean isSameGroup(Behavior behavior) {
		//default behavior - compare by equals
		return equals(behavior);
	}
	
	/**
	 * @return the single instance default behavior object
	 */
	public static Behavior getDefault() {
		if (defaultBehavior == null){
			defaultBehavior = new Behavior();
		}
		return defaultBehavior;
	}
}
