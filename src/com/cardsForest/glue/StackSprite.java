package com.cardsForest.glue;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.cardsForest.foundations.Card;
import com.cardsForest.foundations.Stack;

/**
 * display information and capabilities for a specific {@link Stack} object
 * <p>
 * contains an inner copy of the stacks's {@link Card} collection,
 * generally Game will ask sprite to update its
 * inner copy of cards after logic operations are done 
 * <p> 
 * StackSprite is drawn by absolute position, 
 * the relative position is used only when created by GameLogic
 * it's later updated to absolute position when Game calls StackSprite's updateScreenSize
 * 
 * @see Stack
 * @see com.cardsForest.platform.Game
 * 
 * @author Amit Portnoy
 *
 */
public class StackSprite {
	
	/** inner copy of stack's cards */
	public List<Card> cards;
	
	/**
	 * relative position of the stack <p>
	 * (0.0,0.0) is top-left corner,
	 * (1.0,1.0) is bottom-right corner <p>
	 * position refer to the center of the first card in the stack
	 */
	double relX, relY;
	
	/**
	 * absolute position of the stack (in pixels) <p>
	 * (0,0) is top-left corner,
	 * (display width, display height) is bottom-right corner <p>
	 * position refer to the top left corner of the stack
	 */
	int x, y;
	
	/**
	 * screen size
	 */
	int w, h;
	
	/** specify whether the stack is spread (i.e. any faced up card within the stack is visible) */
	public boolean spread;
	
	/** used to calculate collisions with stack sprite */
	private Rectangle bounds;
	
	/** distance in pixel between two cards in a spread stack */
	private static final int SPREAD_STEP = 15;
	
	/**
	 * create a new empty stackSprite
	 * @param relX relative x coordinate
	 * @param relY relative y coordinate
	 * @param spread spread value to set
	 */
	public StackSprite(double relX, double relY, boolean spread){
		this.relX = relX;
		this.relY = relY;
		this.spread = spread;
		
		cards = new ArrayList<Card>();
		
		bounds = new Rectangle();
	}

	
	/**
	 * @param spread the spread
	 */
	public void setSpread(boolean spread){
		this.spread = spread;
	}
	
	/**
	 * @param relX relative x coordinate
	 * @param relY relative y coordinate
	 */
	public void setRelativePos(double relX, double relY){
		this.relX = relX; this.relY = relY;
	}
	
	/**
	 * check if a point is within the bounds this sprite
	 * @param p point to check
	 * @return true if point is within the sprite
	 */
	public boolean inBounds(Point p){
		return bounds.contains(p);
	}

	/**
	 * find the index of a card at a certain point
	 * <p>
	 * <b>if point in not in bounds of the stack, or if stack
	 * is not spread return the upper card index</b> 
     *
	 * @param p point in question
	 * @return the index of the card at the point (-1 if cards is empty)
	 */
	public int getCardIndex(Point p) {
		if (!inBounds(p) || !spread){
			//point is not in bounds can happen when dragging the selection
			//or stack is not spread
			//in both cases we just take the upper card
			return cards.size()-1;
		}
		
		//point is in bounds
		//stack is spread
		return Math.min(
				// index of the card when not considering number of
				// actual cards in stack
				(p.y - y) / SPREAD_STEP, 
				// since the line above may result in an index that is too large
				// we should always check that the index we return is at most
				// cards' upper card index
				cards.size()-1
				);
		
	}

	/**
	 * draws the stack
	 * @param g graphics to draw the stack on
	 * @param useSelected true if stack's cards are selected 
	 */
	public void draw(Graphics g, boolean useSelected) {
		if (!useSelected){
			//if not selected
			//draw the base of the stack
			g.setColor(Color.RED);
			g.drawRect(x, y, CardSpriteStore.getWidth(), CardSpriteStore.getHeight());
		}
		
		if(cards.isEmpty()){
			//got nothing else to draw
			return;
		}
		
		//reference to current card sprite
		Sprite sprite;
		
		if(spread){
			//stack is spread
			//draw each of the cards in the stack
			int i = 0;
			for(Card card : cards){	
				if(card.isFaceUp()){
					//take the face's sprite
					sprite = CardSpriteStore.getSprite(card.getFace());
				}else{
					//take the back sprite
					sprite = CardSpriteStore.getBack();
				}
				//draw the sprite with spread offset considered 
				sprite.draw(g, x, y+SPREAD_STEP*i,useSelected);
				i++;
			}
		}else{
			//stack is not spread
			//draw only the top card
			Card topCard = cards.get(cards.size()-1); 
			if(topCard.isFaceUp()){
				//it's faceUp -> take the face's sprite
				sprite = CardSpriteStore.getSprite(
						topCard.getFace());
			}else{
				//take the back sprite
				sprite = CardSpriteStore.getBack();
			}
			//draw the sprite
			sprite.draw(g, x, y, useSelected);
		}	
		
	}
	
	/**************************/
	/* management             */
	/**************************/
	
	/**
	 * must be called between creation and first call to draw
	 */
	

	/**
	 * update the sprite with the current screen size <br> 
	 * recalculate absolute positions and bounds<br>
	 *  
	 * <b>must be called between creation and first call to draw</b>
	 * 
	 * @param w width of screen
	 * @param h height of screen
	 */
	public void updateScreenSize(int w, int h) {
		this.w = w;
		this.h = h;
		//get absolute coordinates
		x = (int)(relX * w) - CardSpriteStore.getWidth()/2;
		y = (int)(relY * h) - CardSpriteStore.getHeight()/2;
		
		updateBounds();
	}
	
	/**
	 * recalculate the bounds of this stack <br>
	 * (bounds are calculated using absolute position and card sprite size)
	 */
	public void updateBounds(){
		if ((!spread) || cards.size()<=1){
			//simple stack
			//just use sprite's bounds
			bounds.setBounds(x, y, CardSpriteStore.getWidth(), CardSpriteStore.getHeight());	
		}else{
			//spread
			//factor in spread offset
			bounds.setBounds(x, y, CardSpriteStore.getWidth(), 
					CardSpriteStore.getHeight()+SPREAD_STEP*(cards.size()-1));
		}
	}
	
	
	/**
	 * translate the this sprite to the location of another sprite
	 * 
	 * @param other sprite to translate to
	 * @param index index of a card within the sprite to translate to
	 */
	public void translateTo(StackSprite other,int index){
		if (other == null){
			throw new NullPointerException();
		}
		if (index < 0 || index > other.cards.size()){
			index = 0;
		}
		w = other.w;
		h = other.h;
		
		x = other.x;
		y = (other.spread)?
				(other.y+SPREAD_STEP*index)
				:other.y;
		
		//relative position = center of base point / absolute max length
		relX = ((double)(x + CardSpriteStore.getWidth()/2)) / w;
		relY = ((double)(y + CardSpriteStore.getHeight()/2)) / h;
		
		updateBounds();
	}


	/**
	 * get the position of a specific card in the stack
	 * @param index card's index
	 * @return the card's absolute position point
	 */
	public Point getCardPos(int index) {
		Point p = new Point();
		p.x = x;
		p.y = (spread)?
				(y+SPREAD_STEP*index)
				:y;
		return p;
	}

	/**
	 * get this stack's bounds (absolute)
	 * @return the bounds
	 */
	public Rectangle getBounds() {
		return bounds;
	}
	
}

