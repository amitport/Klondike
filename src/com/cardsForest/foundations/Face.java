package com.cardsForest.foundations;

import static com.cardsForest.foundations.Face.Suit.*;
import static com.cardsForest.foundations.Face.Rank.*;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;


/**
 * Contains constants for all possible faces of a card
 * <p>
 * <b>important ! </b> <br> 
 * do not change the name and order of those constants without reviewing uses of methods name() and ordinal() by other classes
 *  
 * @author Amit Portnoy
 */
public enum Face{
	C1(CLUBS,ACE),
	C2(CLUBS,DEUCE),
	C3(CLUBS,THREE),
	C4(CLUBS,FOUR),
	C5(CLUBS,FIVE),
	C6(CLUBS,SIX),
	C7(CLUBS,SEVEN),
	C8(CLUBS,EIGHT),
	C9(CLUBS,NINE),
	C10(CLUBS,TEN),
	C11(CLUBS,JACK),
	C12(CLUBS,QUEEN),
	C13(CLUBS,KING),
	
	D1(DIAMONDS,ACE),
	D2(DIAMONDS,DEUCE),
	D3(DIAMONDS,THREE),
	D4(DIAMONDS,FOUR),
	D5(DIAMONDS,FIVE),
	D6(DIAMONDS,SIX),
	D7(DIAMONDS,SEVEN),
	D8(DIAMONDS,EIGHT),
	D9(DIAMONDS,NINE),
	D10(DIAMONDS,TEN),
	D11(DIAMONDS,JACK),
	D12(DIAMONDS,QUEEN),
	D13(DIAMONDS,KING),
	
	H1(HEARTS,ACE),
	H2(HEARTS,DEUCE),
	H3(HEARTS,THREE),
	H4(HEARTS,FOUR),
	H5(HEARTS,FIVE),
	H6(HEARTS,SIX),
	H7(HEARTS,SEVEN),
	H8(HEARTS,EIGHT),
	H9(HEARTS,NINE),
	H10(HEARTS,TEN),
	H11(HEARTS,JACK),
	H12(HEARTS,QUEEN),
	H13(HEARTS,KING),
	
	S1(SPADES,ACE),
	S2(SPADES,DEUCE),
	S3(SPADES,THREE),
	S4(SPADES,FOUR),
	S5(SPADES,FIVE),
	S6(SPADES,SIX),
	S7(SPADES,SEVEN),
	S8(SPADES,EIGHT),
	S9(SPADES,NINE),
	S10(SPADES,TEN),
	S11(SPADES,JACK),
	S12(SPADES,QUEEN),
	S13(SPADES,KING);
	
	private Suit suit;
	private Rank rank;

	private Face(Suit suit, Rank rank){
		this.suit = suit;
		this.rank = rank;
	}
	
	/**
	 * @return the face's suit
	 */
	public Suit getSuit() {
		return suit;
	}

	/**
	 * @return the face's rank
	 */
	public Rank getRank() {
		return rank;
	}
	
	@Override
	public String toString() {
		return rank + " of " + suit;
	}
	
	/**
	 * used for a fast implementation of valueOf(Suit,Rank)
	 */
	private static Map<Suit, Map<Rank, Face>> table =
	    new EnumMap<Suit, Map<Rank, Face>>(Suit.class);
	static {
		for(Suit suit : Suit.values()){
			table.put(suit, new EnumMap<Rank, Face>(Rank.class));
		}
		for(Face face : Face.values()){
			Map<Rank, Face> suitTable = table.get(face.getSuit());
			suitTable.put(face.getRank(), face);
		}
	}
	
	/**
	 * @return Face constant of the given suit and rank
	 */
	public static Face valueOf(Suit suit,Rank rank) {
	    return table.get(suit).get(rank);
	}

	/**
	 * Contains constants for all possible suits of a card
	 * <p>
	 * <b>important ! </b> <br>
	 * do not change the name and order of those constants without reviewing
	 * uses of methods name() and ordinal() by other classes
	 */
	public enum Suit {
		CLUBS(Color.BLACK), 
		DIAMONDS(Color.RED),
		HEARTS(Color.RED),
		SPADES(Color.BLACK);

		private Color color;
		
		/**
		 * @param aColor the suit's color
		 */
		private Suit(Color color) {
			this.color = color;
		}
		
		/**
		 * @return the suit's color
		 */
		public Color getColor() {
			return color;
		}
		
		@Override
		public String toString() {
			String str = super.toString();
			assert str!=null && str.length()>1;

			return str.substring(0, 1).concat(
					str.substring(1).toLowerCase(Locale.ENGLISH));

		}
		
		/**
		 * Contains constants for all possible colors of a suit
		 * <p>
		 * <b>important ! </b> <br>
		 * do not change the name and order of those constants without reviewing
		 * uses of methods name() and ordinal() by other classes
		 */
		public enum Color {
			RED, BLACK
		}
	}

	/**
	 * Contains constants for all possible ranks of a card
	 * <p>
	 * <b>important ! </b> <br>
	 * do not change the name and order of those constants without reviewing
	 * uses of methods name() and ordinal() by other classes
	 */
	public enum Rank {
		ACE, DEUCE, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING;
		
		@Override
		public String toString() {
			String str = super.toString();
			assert str!=null && str.length()>1;
			
			return str.substring(0, 1).concat(
					str.substring(1).toLowerCase(Locale.ENGLISH));
		}
	}
}
