/**
 * 
 */
package com.cardsForest.foundations;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cardsForest.glue.OperationManager;
import com.cardsForest.glue.StackSprite;
import com.cardsForest.logic.Behavior;
import static com.cardsForest.glue.OperationManager.*;

/**
 * holds a collection of {@link Card} objects
 * <p>
 * includes static factory methods
 * <p>
 * generally GameLogic will create all stacks,
 * assign them with {@link Behavior} objects
 * and {@link StackSprite} objects.
 * <p>
 * logical action done on stack are delegated to {@link Operation} object
 * and are sent to {@link OperationManager} which perform the operation 
 * in two phases:<br>
 * 1. logical, changes done on stack's cards <br>
 * 2. display, changes on the sprite's cards; it can be mirror of the first phase or more complex (e.g. animation)
 * 
 * @see com.cardsForest.logic.GameLogic
 * @see OperationManager
 * @see com.cardsForest.platform.Game
 * 
 * @author Amit Portnoy
 *
 */
public class Stack {
	
	List<Card> cards;
	List<Card> unmodifiableCards; 
	
	Behavior behavior;
	StackSprite sprite;
	
	/**
	 * Stack objects will be created using static factory methods,
	 * hence the private constructor
	 */
	private Stack(){
		cards = new ArrayList<Card>();
		unmodifiableCards = Collections.unmodifiableList(cards);
		
		//get default behavior and display
		behavior = Behavior.getDefault();
		sprite = new StackSprite(0.5,0.5,false);
	}

	
	/***********************/
	/* game management     */
	/***********************/
	
	
	/**
	 * update that Stack about it being clicked
	 * @param p the position of the clicked card within the stack
	 */
	public void click(Point p){
		behavior.click(this,sprite.getCardIndex(p));
	}

	/**
	 * update that Stack about it being double clicked
	 * @param p the position of the clicked card within the stack
	 */
	public void doubleClick(Point p) {
		behavior.doubleClick(this,sprite.getCardIndex(p));
	}
	
	/**
	 * delegates to {@link StackSprite}'s inBounds
	 * @param p
	 * @return whatever {@link StackSprite} returns
	 */
	public boolean inBounds(Point p) {
		return sprite.inBounds(p);
	}

	/**
	 * delegates to {@link StackSprite}'s draw
	 * @param g
	 */
	public void draw(Graphics g){
		sprite.draw(g,false);
	}

	
	/***********************/
	/* stack operations    */
	/***********************/
	
	/**
	 * shuffle the cards
	 */
	public void shuffle(){
		OperationManager.add(new ShuffleOperation(this));
	}
	
	/**
	 * moves cards from this stack to another
	 * @param dst destination stack to move to
	 * @param num number of cards to move
	 */
	public void moveTo(Stack dst, int num, boolean immediate){
		OperationManager.add(new MoveToOperation(this, dst, num, immediate));
	}
	
	/**
	 * Convenience method for moveTo(dst,num,false)
	 */
	public void moveTo(Stack dst, int num){
		moveTo(dst,num,false);
	}

	/**
	 * flips the top card in the stack
	 */
	public void flipTop(){
		OperationManager.add(new FlipTopOperation(this));
	}
	
	/**
	 * sets all the cards in the stack to face up or face down
	 * @param faceUp all cards in the stack will be set to this parameter value
	 */
	public void setAllFaceUp(boolean faceUp){
		OperationManager.add(new SetAllFaceUpOperation(this,faceUp));
	}
	
	/**
	 * remove all the cards from the stack
	 */
	public void clear() {
		OperationManager.add(new ClearOperation(this));
	}
	
	/*********************************************************/
	/* simple service methods                                */
	/* (methods that will not change the state of the stack) */
	/*********************************************************/

	/**
	 * @return a <b>read only</b> view of the cards
	 */
	public List<Card> getCards() {
		return unmodifiableCards;
	}

	/**
	 * @return the sprite
	 */
	public StackSprite getSprite() {
		return sprite;
	}

	/**
	 * @param sprite the sprite to set
	 */
	public void setSprite(StackSprite sprite) {
		if (sprite == null){
			throw new NullPointerException();
		}

		this.sprite = sprite;
	}

	/**
	 * @return the behavior
	 */
	public Behavior getBehavior() {
		return behavior;
	}
	
	/**
	 * @param behavior the behavior to set
	 */
	public void setBehavior(Behavior behavior) {
		if (behavior == null){
			throw new NullPointerException();
		}
		
		this.behavior = behavior;
	}
	
    /**
     * Returns <tt>true</tt> if this stack contains no cards.
     *
     * @return <tt>true</tt> if this stack contains no cards
     */
	public boolean isEmpty() {
		return cards.isEmpty();
	}
	
	/**
	 * @return number of cards in the stack
	 */
	public int size() {
		return cards.size();
	}
	
	/**
	 * stack's group is defined by it's behavior object
	 * @param other stack to compare with
	 * @return true if this and other stack are of the same group
	 */
	public boolean isSameGroup(Stack other){
		return behavior.isSameGroup(other.behavior);
	}
	
	/*************************/
	/* factory methods       */
	/* (and factory helpers) */
	/*************************/

	/**
	 * adds a card to the stack 
	 * and a copy of the stack to the sprite
	 * @param card the card to add
	 */
	protected void add(Card card){
		cards.add(card);
		sprite.cards.add(new Card(card));
	}
	
	/**
	 * Stack factory method
	 * @return a new empty Stack
	 */
	public static Stack newEmptyStack() {
		return new Stack();
	}
	
	/**
	 * Stack factory method
	 * @return a new stack contain 52 {@link Card} objects
	 */
	public static Stack newDeck() {
		Stack newDeck = newEmptyStack();
		
		for (Face face : Face.values()){
			newDeck.add(new Card(face,false));
		}
		
		return newDeck;
	}

	/*************************/
	/* Operation inner class */
	/*************************/
	
	/**
	 * Encapsulate stack change (i.e. operation) 
	 * <p>
	 * operation is done in two phases:<br>
	 * the first logic phase is done immediately,
	 * the second display phase is done after all logic is done
	 * 
	 * @see OperationManager
	 */
	public static abstract class Operation{
		protected Stack src;
		protected List<Card> cards;
		protected StackSprite sprite;
		
		/**
		 * initialize the new operation object
		 * @param src the stack on which the operation is performed
		 */
		protected Operation(Stack src){
			this.src = src;
			this.cards = src.cards;
			this.sprite = src.getSprite();
		}
		
		/**
		 * default actions to be performed on cards as part of the operation
		 * @param cards the cards to perform the operation on
		 */
		abstract protected void action(List<Card> cards);
		
		/**
		 * do the logical part of the operation (done immediately)
		 */
		final public void doLogic(){
			action(cards);
		}
		
		/**
		 * do the display part of the operation (done after all logic is done)
		 */
		public void doDisplay(){
			action(sprite.cards);
		}
		
		/**
		 * return the stacks cards (only operation objects have access to the modifiable copy)
		 * @param stack the stack to get the cards from
		 * @return stack's cards
		 */
		protected List<Card> getModifiableCards(Stack stack){
			return stack.cards;
		}
	}

	
	
	//new stuff
	
	//caller should check not empty
	public Card getTop() {
		// TODO Auto-generated method stub
		return cards.get(cards.size()-1);
	}


}
