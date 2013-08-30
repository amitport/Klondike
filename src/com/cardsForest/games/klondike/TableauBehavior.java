/**
 * 
 */
package com.cardsForest.games.klondike;

import java.util.ArrayList;
import java.util.List;

import com.cardsForest.foundations.Card;
import com.cardsForest.foundations.Stack;
import com.cardsForest.glue.Selection;
import com.cardsForest.logic.Behavior;
import com.cardsForest.logic.SequenceChecker;

/**
 * @author Amit Portnoy
 *
 */
public class TableauBehavior extends Behavior {

	static SequenceChecker sc = new TableauSequenceChecker();
	
	Stack[] foundation;
	
	public TableauBehavior(Stack[] foundation){
		this.foundation = foundation;
	}
	
	@Override
	public void click(Stack stack, int cardIndex) {
		Selection selection = Selection.get();
		if (selection.isAvailable()){
			boolean legal = false;
			if (stack.isEmpty()){
				legal = sc.checkSequance(selection.stack.getCards(), true);
			}else{
				List<Card> temp = new ArrayList<Card>();
				temp.add(stack.getCards().get(stack.size()-1));
				temp.addAll(selection.stack.getCards());				
				legal = sc.checkSequance(temp, false);
			}

			if (legal){
				//flip the top card in tableau if needed
				if (!selection.src.isEmpty() &&
						!selection.src.getCards().get(selection.src.size()-1).isFaceUp() &&
						selection.src.getBehavior().isSameGroup(this)){
					selection.src.flipTop();
				}
				//end the selection
				selection.selectEnd(stack);
			}
		}else{
			if (!stack.isEmpty() && stack.getCards().get(cardIndex).isFaceUp()){
				List<Card> view = stack.getCards().subList(cardIndex, stack.size());
				if (sc.checkSequance(view, false)){
					selection.select(stack, cardIndex);
				}
			}
		}
	}
	
	@Override
	public void doubleClick(Stack stack, int cardIndex) {
		Selection selection = Selection.get();
		SequenceChecker sc = new FoundationSequenceChecker();
		if (selection.stack.size() == 1){
			for (Stack f : foundation){
				if (f.isEmpty()){
					if (sc.checkStarter(selection.stack.getTop())){
						if (!selection.src.isEmpty() &&
								!selection.src.getTop().isFaceUp()){
							selection.src.flipTop();
						}
						selection.selectEnd(f);
						return;
					}
				}else{
					if (sc.checkSequential(f.getTop(), selection.stack.getTop())){
						if (!selection.src.isEmpty() &&
								!selection.src.getTop().isFaceUp()){
							selection.src.flipTop();
						}
						selection.selectEnd(f);
						return;
					}
				}
			}
		}
	}
}
