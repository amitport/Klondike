/**
 * 
 */
package com.cardsForest.games.klondike;


import com.cardsForest.foundations.Stack;
import com.cardsForest.foundations.Face.Rank;
import com.cardsForest.logic.Behavior;
import com.cardsForest.logic.GameLogic;

/**
 * @author Amit Portnoy
 *
 */
public class Klondike extends GameLogic {

	
	Stack stock;
	Stack foundation[] = null;
	
	public Klondike(){
		createStacks();
	}
	
	private void createStacks(){
		//foundation
		foundation = new Stack[4];
		
		//waste
		Stack waste = Stack.newEmptyStack();
		waste.getSprite().setRelativePos(0.2,0.1);
		waste.setBehavior(new WasteBehavior(foundation));
		stacks.add(waste);
		
		//stock
		stock = Stack.newEmptyStack();
		stock.getSprite().setRelativePos(0.1,0.1);
		stock.setBehavior(new StockBehavior(waste));
		stacks.add(stock);
		
		
		
		Behavior b = new FoundationBehavior();		

		for(int i = 0; i < foundation.length; i++){
			Stack s = Stack.newEmptyStack();
			s.getSprite().setRelativePos(0.6+0.1*i,0.1);
			s.setBehavior(b);
			foundation[i] = s;
			stacks.add(s);
		}
		
		b = new TableauBehavior(foundation);
		
		//tableau
		Stack tableau[] = new Stack[7];
		for(int i = 0; i < tableau.length; i++){
			Stack s = tableau[i];
			s = Stack.newEmptyStack();
			s.getSprite().setRelativePos(0.3+0.1*i,0.4);
			s.getSprite().spread = true;
			s.setBehavior(b);
			stacks.add(s);
		}
		
		
	}
	
	/* (non-Javadoc)
	 * @see com.cardsForest.logic.GameLogic#checkGameDone()
	 */
	@Override
	public boolean checkGameDone() {
		for (Stack stack : foundation){
			if (stack.isEmpty() || stack.getTop().getFace().getRank()!=Rank.KING){
				return false;
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.cardsForest.logic.GameLogic#deal()
	 */
	@Override
	public void deal() {
		Stack offscreen = Stack.newDeck();
		offscreen.moveTo(stock, offscreen.size(),true);

		stock.shuffle();
		
		for(int i=0;i<7;i++){
			stock.moveTo(stacks.get(6+i), 1);
			stacks.get(6+i).flipTop();
			for(int j = i+1;j<7;j++){
				stock.moveTo(stacks.get(6+j), 1);
			}
		}
	}

}
