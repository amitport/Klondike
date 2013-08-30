package com.cardsForest.logic;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.cardsForest.foundations.Stack;
import com.cardsForest.glue.OperationManager;
import com.cardsForest.glue.Selection;


/**
 * contains games's logical behavior <p>
 * classes that inherit GameLogic should contain only game logic <br>
 * they are responsible to define the stacks in the game,
 * assign them with behavior object and
 * position them on screen (relative coordinates). <br>
 * GameLogic base class also contains methods that decide how to activate 
 * each Stack's behavior methods (click/doubleClick) <br>
 * 
 * Game is responsible to update GameLogic of interaction events
 * 
 * @see com.cardsForest.platform.Game
 * @see Stack
 * @see Behavior
 * 
 * 
 * @author Amit Portnoy
 *
 */
public abstract class GameLogic {

	//Principle game elements
	
	/** game's logical stacks */
	public List<Stack> stacks;
	/** game's singleton selection */ 
	public Selection selection;
	
	/** true only if we started handling a click with selection available */
	boolean startedWithSelection;
	
	//for double click management:
	/** had selection before previous click */
	boolean wasSelected; 
	/** the previous selection source */
	Stack prevSelectionSrc; 
	
	/**
	 * check if the game is done (no moves are aloud)
	 * @return true only if the game is done
	 */
	abstract public boolean checkGameDone();
	/**
	 * initial distribution of cards between the stacks 
	 */
	abstract public void deal();
	
	/**
	 * create a new GameLogic instance <br>
	 * create the game's logical stacks
	 */
	public GameLogic(){
		selection = Selection.get();
		stacks = new ArrayList<Stack>();
		
		startedWithSelection = false;
		wasSelected = false;
		prevSelectionSrc = null;
	}
	
	/**
	 * @return the stacks
	 */
	public List<Stack> getStacks() {
		return stacks;
	}
	
	/**
	 * actions that are perform at the start of each click handling
	 */
	protected void startAction(){
		//remember that we start with selection
		//may cause us to unselect it at endAction
		startedWithSelection = selection.isAvailable();
	}

	/**
	 * action that are performed at the end of each click handling
	 */
	protected void endAction(){
		if (selection.isAvailable() && startedWithSelection){
			//had selection from start and didn't find destination -> clear selection
			selection.selectEnd(null);
		}
	}
	
	/**
	 * actions that are perform when no specific stack 
	 * were clicked during click handling 
	 */
	protected void nullClick(){
		//TODO
	}
	
	/**
	 * clears game's logic stack and calls deal
	 */
	public void redeal() {
		if (Selection.get().isAvailable()){
			selection.selectEnd(null);
		}
		for (Stack stack : stacks){
			stack.clear();
		}
		deal();
	}
	
	/**
	 * update the game with a normal click
	 * @param p position of the click
	 * @param multiClick true only this click is last of multiple clicks done rapidly
	 */
	public void updateClick(Point p, boolean multiClick){
		boolean clicked = false;//true if clicked directly on current stack
		Stack clickedStack = null; //reference to clicked stack
		
		//perform start actions
		startAction();
		
		if (multiClick
				&&
				selection.isAvailable()
				&&
				selection.stack.inBounds(p)){
			//got a normal double click:
			//first clicked to select, second clicked do doubleClick action
			//(we still have reverse double click to handle)
			selection.src.doubleClick(p);
			endAction();
			return;
		}

		//get the clicked stack
		for(Stack stack : stacks){
			if (stack.inBounds(p)){
				clickedStack = stack;
				clicked = true;
				break;
			}
		}
		
		if (clicked){
			//we clicked on some stack
			if (multiClick
					&&
					wasSelected
					&&
					prevSelectionSrc == clickedStack){
				//got a reverse selection on this stack
				clickedStack.click(p);
				//not so esthetic, 
				//but we need to update the display
				//with the selection before we can continue
				OperationManager.doDisplayOperations();
				if (selection.isAvailable()){
					//not very likely we won't get here but it's good to check
					selection.src.doubleClick(p);
				}
				wasSelected = false;
				endAction();
				return;
			}
			wasSelected = false;
			if (selection.isAvailable()&&
					clickedStack == selection.src){
				//if this stack was already selected
				//remember that for reverse double click
				wasSelected = true; 
				prevSelectionSrc = selection.src;
			}
			clickedStack.click(p);
		}else{
			//didn't click any regular stack
			if (selection.isAvailable()
					&&
					selection.stack.inBounds(p)){
				//but did clicked the previous selection
				//remember that for reverse double click
				wasSelected = true; 
				prevSelectionSrc = selection.src;
			}else{
				//didn't click anything
				nullClick();
			}
		}
		
		//perform end actions
		endAction();
	}
	
	/**
	 * special click flow to be perform after finished dragging <br>
	 * using dragged selection bounds to decide drag target
	 * @param rect the dragged selection bounds
	 */
	public void updateClick(Rectangle rect) {
		boolean clicked = false; //true if clicked directly on current stack
		Rectangle inter; //intersection rectangle between selection's bounds to current stack's bounds
		
		//perform start actions
		startAction();
		
		Stack maxStack = null; //the stack with maximum intersection size
		int max = -1; 
		for(Stack stack : stacks){
			//get the intersection
			inter = stack.getSprite().getBounds().intersection(rect);
			if (!inter.isEmpty()){
				//got intersection
				//calculate surface size
				int size = inter.height * inter.width;
				if (size > max){
					//get the maximum
					max = size;
					maxStack = stack;
					clicked = true;
				}
			}
		}
		
		if (clicked){
			//got some intersection
			//click the max intersection stack
			maxStack.click(rect.getLocation());
		}else{
			//no intersection do null click
			nullClick();
		}

		//do end actions
		endAction();
	}
}	

