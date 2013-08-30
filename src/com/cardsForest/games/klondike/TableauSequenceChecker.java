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
public class TableauSequenceChecker extends SequenceChecker {

	/* (non-Javadoc)
	 * @see com.cardsForest.logic.SequenceChecker#checkSequential(com.cardsForest.foundations.Card, com.cardsForest.foundations.Card)
	 */
	@Override
	public boolean checkSequential(Card card1, Card card2) {
		Face face1 = card1.getFace();
		Face face2 = card2.getFace();
		
		return (face1.getSuit().getColor() != face2.getSuit().getColor())
				&&
				(face1.getRank().ordinal()-1 == face2.getRank().ordinal());
	}

	/* (non-Javadoc)
	 * @see com.cardsForest.logic.SequenceChecker#checkStarter(com.cardsForest.foundations.Card)
	 */
	@Override
	public boolean checkStarter(Card card) {
		switch (card.getFace()){
		case C13:
		case D13:
		case H13:
		case S13:
			return true;
		default:
			return false;
		}
	}

}
