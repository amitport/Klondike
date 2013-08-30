/**
 * 
 */
package com.cardsForest.games.klondike;

import com.cardsForest.foundations.Card;
import com.cardsForest.foundations.Stack;
import com.cardsForest.glue.Selection;
import com.cardsForest.logic.Behavior;
import com.cardsForest.logic.SequenceChecker;

/**
 * @author Amit Portnoy
 *
 */
public class FoundationBehavior extends Behavior {

	static SequenceChecker sc = new FoundationSequenceChecker();
	
	/* (non-Javadoc)
	 * @see com.cardsForest.foundations.Behavior#Click(com.cardsForest.foundations.Stack, int)
	 */
	@Override
	public void click(Stack stack, int cardIndex) {
		Selection selection = Selection.get();
		if ((selection.isAvailable()) 
				&& (selection.stack.size() == 1)) {
			boolean legal = false;
			if (stack.isEmpty()){
				legal = sc.checkStarter(selection.stack.getCards().get(0));
			} else {
				Card card1 = stack.getTop();
				Card card2 = selection.stack.getTop();
				legal = sc.checkSequential(card1, card2);
			}
			if (legal){
				if (!selection.src.isEmpty() &&
						!selection.src.getTop().isFaceUp() &&
						selection.src.getBehavior() instanceof TableauBehavior){
					selection.src.flipTop();
				}
				selection.selectEnd(stack);
			}
		}
	}
	
}
