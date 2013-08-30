/**
 * 
 */
package com.cardsForest.glue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cardsForest.foundations.Card;
import com.cardsForest.foundations.Stack;
import com.cardsForest.foundations.Stack.Operation;
import com.cardsForest.platform.Game;

/**
 * keeps a log of the operations perform on the stacks <br>
 * runs the operation:<br>
 * 1. logical phase is done immediately  <br>
 * 2. display phase is triggered by {@link Game} (by calling {@code doDisplayOperations})
 * and is performed after all logic is done
 * <p>
 * contains Operation subclasses
 * 
 * @author Amit Portnoy
 *
 * @see Operation
 * 
 */
public final class OperationManager {

	/** log of operations occurred during logic phase
	 * (so we can retrace them at display phase) */
	static List<Operation> log = new ArrayList<Operation>();
	
	/**
	 *  static class behavior:
	 * @throws UnsupportedOperationException on creation
	 */
	OperationManager(){throw new UnsupportedOperationException();}
	
	/**
	 * add a new operation to log
	 * and perform the logic phase immediately 
	 * @param o the operation to add
	 */
	public static void add(Operation o){
		o.doLogic();
		log.add(o);
	}
	
	/**
	 * do the display phase of the operations in the log
	 */
	public static void doDisplayOperations(){
		while (!log.isEmpty()){
			Operation o = log.remove(0);
			o.doDisplay();
		}
	}

	
	
	/************************/
	/* Operation subclasses */
	/************************/
	
	/**
	 * shuffle all the cards in a stack 
	 * (delegate to {@link Collections}' shuffle)
	 */
	public static final class ShuffleOperation extends Operation {

		ArrayList<Card> tempCards;
		
		public ShuffleOperation(Stack src) {
			super(src);
			
			tempCards = new ArrayList<Card>();
		}
		
		@Override
		protected void action(List<Card> cards){
			Collections.shuffle(cards);
			
			//save the shuffled cards
			//TODO: improve this, by remembering random key
			//of the shuffle and trace it to sprite's cards
			//instead of replacing objects
			for(Card card : cards){
				tempCards.add(new Card(card));
			}
		}
		
		@Override
		public void doDisplay(){
			//set the shuffled cards to sprite
			sprite.cards = tempCards;
		}
	}
	
	/**
	 * remove all cards from a stack
	 * (delgate to collection's clear)
	 * @author Amit Portnoy
	 */
	public static final class ClearOperation extends Operation {

		public ClearOperation(Stack src) {
			super(src);
		}
		
		@Override
		protected void action(List<Card> cards){
			cards.clear();
		}
		
	}
	
	/**
	 * flips the top card in a stack
	 * @author Amit Portnoy
	 *
	 */
	public static final class FlipTopOperation extends Operation {

		public FlipTopOperation(Stack src) {
			super(src);
		}
		
		@Override
		protected void action(List<Card> cards){
			if (!cards.isEmpty()){
				cards.get(cards.size()-1).flip();
			}
		}
		
	}
	
	/**
	 * set all the faceUp property of stack's cards
	 * 
	 * @author Amit Portnoy
	 */
	public static final class SetAllFaceUpOperation extends Operation {

		boolean faceUp;
		
		public SetAllFaceUpOperation(Stack src, boolean faceUp) {
			super(src);
			this.faceUp = faceUp;
		}
		
		@Override
		protected void action(List<Card> cards){
			for(Card card : cards){
				card.setFaceUp(faceUp);
			}
		}
	}
	
	/**
	 * move cards from one stack to another 
	 * (creates {@link CardMoveAnimation} on display phase) 
	 * @author Amit Portnoy
	 */
	public static final class MoveToOperation extends Operation {

		Stack dst;
		int num;
		boolean immediate;
		
		List<Card> dstCards;

		
		public MoveToOperation(Stack src, Stack dst, int num, boolean immediate) {
			super(src);
			this.dst = dst;
			this.num = num;
			this.immediate = immediate;
			
			dstCards = getModifiableCards(dst);
			
		}
		
		@Override
		protected void action(List<Card> cards){
			List<Card> view = cards.subList(cards.size()- num, cards.size());
			dstCards.addAll(view);
			view.clear();
		}
		
		@Override
		public void doDisplay(){
			if (immediate){
				dstCards = dst.getSprite().cards;
				action(sprite.cards);
			} else {
				Game.drawMotion(new CardMoveAnimation(
						src,
						dst,
						num));
			}
			sprite.updateBounds();
			dst.getSprite().updateBounds();
		}
	}
}
