package com.cardsForest.glue;

import java.awt.Graphics;
import java.awt.Point;
import java.util.List;

import com.cardsForest.foundations.Card;
import com.cardsForest.foundations.Stack;
import com.cardsForest.platform.Game;

/**
 * contains information relevant to a card move animation 
 * between one stack to another <br>
 * 
 * @see Motion
 * @author Amit Portnoy
 *
 */
public class CardMoveAnimation implements Motion {

	/** speed of the animation is pixel/second */
	final long SPEED = 3000;
	
	/** source stack */
	Stack src;
	/** destination stack */
	Stack dst;
	/** num of cards to move from source to destination */
	int numOfCards;
	
	/** number of animation updates before it will be done */
	double updatesToReach;
	/** true only if animation is done */
	boolean done;
	
	/** temporary sprite corresponding to the moving cards */
	StackSprite sprite;
	/** offset to advance sprite position in every update */
	double dx, dy;
	/** current position of sprite. <br>
	 * if we won't use this, position maybe become constant
	 * due to lack of precision in sprite's position (which is an integer) */
	double cx, cy;
	
	/** short-form for destination sprite cards */
	List<Card> dCards;
	/** short-form for moving sprite cards */
	List<Card> mCards;
	
	/**
	 * create a new card move animation
	 * @param src source stack
	 * @param dst destination stack
	 * @param numOfCards number of cards to move from source to destination
	 */
	public CardMoveAnimation(Stack src,
							 Stack dst,
							 int numOfCards){
		if (src == null
				||
				dst == null){
			throw new NullPointerException();
		}
				
		
		this.numOfCards = numOfCards;
		this.src = src;
		this.dst = dst;
		
		done = false;
	}	
	
	@Override
	public void init(){
		if (src.getSprite().cards.size()<numOfCards){
			//animation is invalid, just quietly end
			done = true;
			return;
		}
		
		//distance between start and end points per axis
		double distX,distY;
		//absolute distance
		double dist;
		//small angle between start and end points 
		double alpha;
		
		//create the moving temporary sprite
		sprite = new StackSprite(0.5,0.5,src.getSprite().spread);
		
		//short-form for source sprite cards
		List<Card> sCards = src.getSprite().cards;
		dCards = dst.getSprite().cards;
		mCards = sprite.cards;
		
		//set starting position to be where source cards are
		sprite.translateTo(src.getSprite(),sCards.size() - numOfCards);
		
		//move cards from source to moving sprite
		List<Card> view = sCards.subList(sCards.size()- numOfCards, sCards.size());
		mCards.addAll(view);
		view.clear();
		
		//set start position
		cx = sprite.x;
		cy = sprite.y;
		
		//get end position
		Point p = dst.getSprite().getCardPos(dCards.size());
		
		//distance
		distX = p.x- cx;
		distY = p.y- cy;
		dist = Math.sqrt(Math.pow(distX,2)+Math.pow(distY,2));
		//angle
		alpha = Math.atan(Math.abs(distY/distX));
		//update step considering speed, frames per seconds, angle and distance
		dx = (SPEED*Math.cos(alpha))/Game.FPS * Math.signum(distX);
		dy = (SPEED*Math.sin(alpha))/Game.FPS * Math.signum(distY);
		//updates to reach destination 
		updatesToReach = (dist/SPEED)*Game.FPS;
	}
	
	@Override
	public void updateFrame() {
		if (!done){
			//only update if animation is still running
			updatesToReach--;
			if(0 >= updatesToReach){
				//cards reached their destination
				//move from moving sprite to destination
				dCards.addAll(mCards);
				mCards.clear();
				done = true;
			}else{
				//continue animation
				//add delta to current position
				cx += dx;
				cy += dy;
				//update sprite
				sprite.x = (int)cx;
				sprite.y = (int)cy;
			}			
		}
	}
	
	@Override
	public void draw(Graphics g){
		if (!done){
			//draw the moving sprite
			sprite.draw(g,true);
		}
	}
	
	@Override
	public boolean isDone() {
		return done;
	}
}
