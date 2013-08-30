package com.cardsForest.foundations;


import com.cardsForest.foundations.Face;

/**
 * holds all the information relevant to a specific card
 *  <p>
 *  a card contains a single {@link Face} constant,<br>
 *  two card are considered equal when their faces are equal
 *  
 * @author Amit Portnoy
 */
public class Card {

	private final Face face;
	private boolean faceUp;
	
	/**
	 * @param face the face to set
	 * @param faceUp the faceUp to set
	 * @throws NullPointerException
	 */
	public Card(Face face, boolean faceUp) { 
		if (face == null){
			throw new NullPointerException();
		}
		this.face = face;
		this.faceUp = faceUp;
	}

	/**
	 * copy constructor for card
	 * @param card the card to copy
	 */
	public Card(Card card) {
		this(card.face,card.faceUp);
	}

	/**
	 * @return the faceUp
	 */
	public boolean isFaceUp() {
		return faceUp;
	}

	/**
	 * if faceUp is true and the card is currently viewable
	 * it's face image will display (otherwise it's back image will display)
	 * @param faceUp the faceUp to set
	 */
	public void setFaceUp(boolean faceUp) {
		this.faceUp = faceUp;
	}
	
	/**
	 * flips the card
	 */
	public void flip() {
		faceUp = !faceUp;
	}

	/**
	 * @return the face
	 */
	public Face getFace() {
		return face;
	}
	
	@Override
	public boolean equals(Object obj){
		//cards are considered equal when their faces are equal
		if (obj instanceof Card)
			return face.equals(((Card)obj).face);
		else
			return super.equals(obj);
	}
	
	@Override
	public int hashCode(){
		return face.hashCode();
	}

	@Override
	public String toString(){
		return face.toString();
	}


}
