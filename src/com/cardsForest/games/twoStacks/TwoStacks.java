/**
 * 
 */
package com.cardsForest.games.twoStacks;

import com.cardsForest.foundations.Stack;
import com.cardsForest.logic.GameLogic;

/**
 * @author Amit Portnoy
 *
 */
public class TwoStacks extends GameLogic {

	Stack stack1;
	Stack stack2;
	
	public TwoStacks(){
		createStacks();
	}
	
	private void createStacks(){
		
		stack1 = Stack.newEmptyStack();
		stack2 = Stack.newEmptyStack();
		Stack stack3 = Stack.newEmptyStack();
		
		
		stack1.setBehavior(new StackBehavior(stack2));
		stack1.getSprite().setRelativePos(0.2, 0.5);
		stack1.getSprite().spread = true;
		stacks.add(stack1);
		
		
		stack2.setBehavior(new StackBehavior(stack1));
		stack2.getSprite().setRelativePos(0.8, 0.5);
		stack2.getSprite().spread = true;
		stacks.add(stack2);
		
		stack3.setBehavior(new StackBehavior(stack1));
		stack3.getSprite().setRelativePos(0.5, 0.6);
		stack3.getSprite().spread = true;
		stacks.add(stack3);
	}
	
	/* (non-Javadoc)
	 * @see com.cardsForest.logic.GameLogic#checkGameDone()
	 */
	@Override
	public boolean checkGameDone() {
		if (selection.isAvailable() && selection.src == stack1){
			return false;
		}
		return stack1.isEmpty();
	}

	/* (non-Javadoc)
	 * @see com.cardsForest.logic.GameLogic#deal()
	 */
	@Override
	public void deal(){
		Stack stock = Stack.newDeck();
		stock.moveTo(stack1, 10,true);
		stack1.setAllFaceUp(true);
	}

}
