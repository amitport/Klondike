/**
 * 
 */
package com.cardsForest.games.twoStacks;


import com.cardsForest.foundations.Stack;
import com.cardsForest.glue.Selection;
import com.cardsForest.logic.Behavior;

/**
 * @author Amit Portnoy
 *
 */
public class StackBehavior extends Behavior {

	Stack other;
	
	public StackBehavior(Stack other) {
		this.other = other;
	}

	/* (non-Javadoc)
	 * @see com.cardsForest.foundations.Behavior#Click(com.cardsForest.foundations.Stack, int)
	 */
	@Override
	public void click(Stack stack, int cardIndex) {
		Selection selection = Selection.get();
		if (selection.isAvailable()){
			selection.selectEnd(stack);
		}else{
			if (! stack.isEmpty()){
				selection.select(stack, cardIndex);
			}
		}
	}

	@Override
	public void doubleClick(Stack stack, int cardIndex) {
		Selection selection = Selection.get();
		if (! selection.stack.isEmpty()){
			selection.selectEnd(other);
		}
	}

}
