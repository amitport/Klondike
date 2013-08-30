/**
 * 
 */
package com.cardsForest.logic;

import java.util.List;

import com.cardsForest.foundations.Card;

/**
 * defines rules for validity of a list of card <br>
 * and supply methods to check that validity
 * 
 * @author Amit Portnoy
 *
 */
public abstract class SequenceChecker {
	
	/**
	 * check if this card can start a new sequence
	 * @param card card to check
	 * @return true is card is a possible start
	 */
	abstract public boolean checkStarter(Card card);
	
	/**
	 * check if two cards can follow in sequence
	 * @param card1 first card
	 * @param card2 second card
	 * @return true if the cards are sequential
	 */
	abstract public boolean checkSequential(Card card1, Card card2);
	
	/**
	 * check if a given list of cards is compatible with this sequenceChecker
	 * @param list list to check
	 * @param checkStarter if true than first card must be a starter (see checkStarter)
	 * @return true if list is a valid sequence
	 */
	public boolean checkSequance(List<Card> list, boolean checkStarter){
		if (list.size()==0){
			//empty true
			return true;
		}
		
		boolean ans = true;
		
		if (checkStarter){
			//check if first is starter
			ans = checkStarter(list.get(0));
		}
		for(int i = 0; ans && i <list.size()-1; i++){
			//for each two following cards check if they're sequential
			ans = checkSequential(list.get(i), list.get(i+1));
		}
		
		return ans;
	}
}
