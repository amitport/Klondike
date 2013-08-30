/**
 * 
 */
package com.cardsForest.games.klondike;

import com.cardsForest.foundations.Card;
import com.cardsForest.foundations.Face;
import com.cardsForest.logic.SequenceChecker;

/**
 * @author Amit Portnoy
 *
 */
public class FoundationSequenceChecker extends SequenceChecker {

	/* (non-Javadoc)
	 * @see com.cardsForest.logic.SequenceChecker#checkSequential(com.cardsForest.foundations.Card, com.cardsForest.foundations.Card)
	 */
	@Override
	public boolean checkSequential(Card card1, Card card2) {
		Face face1 = card1.getFace();
		Face face2 = card2.getFace();
		
		return (face1.getSuit() == face2.getSuit()
				&&
				face1.getRank().ordinal()+1 == face2.getRank().ordinal());
	}

	/* (non-Javadoc)
	 * @see com.cardsForest.logic.SequenceChecker#checkStarter(com.cardsForest.foundations.Card)
	 */
	@Override
	public boolean checkStarter(Card card) {
		switch (card.getFace()){
		case C1:
		case D1:
		case H1:
		case S1:
			return true;
		default:
			return false;			
		}
	}

}
