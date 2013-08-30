package com.cardsForest.platform;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import java.util.concurrent.ArrayBlockingQueue;


import static com.cardsForest.platform.Shortcuts.*;


import com.cardsForest.foundations.Stack;
import com.cardsForest.games.klondike.Klondike;
import com.cardsForest.glue.Motion;
import com.cardsForest.glue.CardSpriteStore;
import com.cardsForest.glue.Drag;
import com.cardsForest.glue.OperationManager;
import com.cardsForest.glue.Selection;
import com.cardsForest.logic.GameLogic;

/**
 * the main thread of the game <br><p>
 * responsibilities: <br>
 * - load the game's sprites <br>
 * - create the GameLogic <br>
 * - transfer events to GameLogic<br>
 * - handle animation / dragging
 * - update the GameApplet when needed
 * 
 * @see GameLogic
 * @see GameApplet
 * @see com.cardsForest.glue.Sprite
 * 
 * @author Amit Portnoy
 *
 */
public class Game extends Thread{

	/** animation Frames Per Seconds */
	public final static long FPS = 24;
	/** this is to be nice to other threads in the game <br>
	 * we may be running the animation for NO_DELAYS_PER_YIELD
	 * without yielding <br>(which means we give up our time share to
	 * let another thread run) */
	final static int NO_DELAYS_PER_YIELD = 16;
	/** maximum number of animation updates (frames) <br> 
	 * to skip when failed to draw all frames on
	 * requested FPS  <br>(setting this high will mean animation
	 * will always run at the same speed but may have very 
	 * large jumps) s*/
	final static int MAX_FRAME_SKIPS = 5;

	/** manage the game logic decisions */
	static GameLogic logic;
	/** if true the game is running and responds
	 * to user events  */
	static boolean running;

	/**
	 * used as input to the game <br>
	 * most game event and requests must
	 * through this queue 
	 */
	static public ArrayBlockingQueue<Object> queue;
	
	/**
	 * reference to the game applet <br>
	 * needed when we want to update the display <br>
	 * TODO: consider transferring a more simple object
	 * Game doesn't need all GameApplet
	 */
	static GameApplet applet;
	
	/**
	 * singleton instance of the game
	 */
	static private Game game = null;
	
	/**
	 * create a new game instance: <br>
	 * - create the queue <br>
	 * - load all images <br>
	 * - create the GameLogic <br>
	 * - and start the Game thread 
	 * 
	 * @param applet the game will update this when needed
	 */
	private Game(GameApplet applet){
		if (applet == null){
			throw new NullPointerException();
		}
		Game.applet = applet;
		//load the images
		CardSpriteStore.loadAll();
		
		queue = new ArrayBlockingQueue<Object>(1);		
		logic = new Klondike();
		running = false;
		
		//start the game
		start();
	}
	
	/**
	 * create the singleton instance of the game
	 * @param applet the game will update this when needed
	 */
	static public void create(GameApplet applet){
		if (game == null){
			game = new Game(applet);
		} else {
			print("game already created"); //aka stupid explorer
		}
	}
	
	/**
	 * cyclicly go over the queue and handle incoming events
	 * <p>
	 * 
	 * events are (types are in <b>bold</b>):<br>
	 * <b> String </b> <br>
	 * "start" - game is ready to begin for the first time <br>
	 * "paint" - draw the game to canvas <br>
	 * "deal" - restart the game <br>
	 * <b> Dimension </b> - update screen size <br>
	 * <b> MouseEvent </b> - mouse click (ignored if not running) <br>
	 * <b> Drag </b> - drag handling start (ignored if not running) <br>
	 * <p>
	 * at the end of most events checks if game is done 
	 * (ask GameLogic) and update applet if so 
	 * 
	 */
	@Override
	public void run(){
		while (true){
			Object o = null;
			try {
				//get a new event
				o = queue.take();
			} catch (InterruptedException e) {
				error("interrupted while waiting for game event");
			}
			if (o instanceof String){
				handleStringEvent((String)o);
		    }
			if (o instanceof Dimension){
				handleScreenSizeEvent(((Dimension) o).width,((Dimension) o).height);
			}
			if (running && o instanceof MouseEvent){
				handleClick((MouseEvent)o);
			}
			if (running && o instanceof Drag){
				handleDragEvent((Drag)o);
			}
		}
	}
	
	/** 
	 * handle single string command
	 * 
	 * see run method header */
	private void handleStringEvent(String str){
		if (str.equals("start")){
			running = true;
			logic.deal();
			
			//update the display
			OperationManager.doDisplayOperations();
			applet.canvas.paintGame(null);
			
			checkGameDone();
		}
		if (str.equals("paint")){
			applet.canvas.paintGame(null);
		}
		if (str.equals("deal")){
			running = true;
			applet.setStatus(null);
			logic.redeal();
			
			//update the display
			OperationManager.doDisplayOperations();
			applet.canvas.paintGame(null);
			
			checkGameDone();
		}
	}
	
	/** see run method header */
	private void handleScreenSizeEvent(int w, int h){
		for (Stack stack : logic.getStacks()){
			stack.getSprite().updateScreenSize(w, h);
		}
		Selection.get().updateScreenSize(w, h);
	}
	
	/** 
	 * got a mouse click
	 * delegate to logic
	 * 
	 * see run method header */
	private void handleClick(MouseEvent e){
		if (e.getClickCount() > 1){
			//it's a rapid multi click 
			logic.updateClick(e.getPoint(),true);
		} else {
			//it's a normal click
			logic.updateClick(e.getPoint(),false);
		}
		
		//update the display
		OperationManager.doDisplayOperations();
		applet.canvas.paintGame(null);
		
		checkGameDone();
	}
	
	/** 
	 * got a drag start
	 * (drag object will keep update from MouseInputHandler)
	 * 
	 * see run method header */
	private void handleDragEvent(Drag drag){
		if (Selection.get().isAvailable()
				&&
				! Selection.get().getBounds().contains(drag.getStart())){
			//got a selection but it's not in bounds 
			//end the selection
			Selection.get().selectEnd(null);
			//update the display
			OperationManager.doDisplayOperations();
			applet.canvas.paintGame(null);
			
		}
		
		if (!Selection.get().isAvailable()){
			//don't have a selection (maybe because we just now ended it)
			//so try to select
			logic.updateClick(drag.getStart(),false);
			//update the display with the changes
			OperationManager.doDisplayOperations();
			applet.canvas.paintGame(null);
		}

		if (Selection.get().isAvailable()){
				//we have a selection
				//and it's in bounds of the click
			    //(got to be in bounds otherwise it wouldn't have been selected)
			
				//it's in bounds -> GOT drag
				//draw the drag animation
				drawMotion(drag);
				
				//update the logic with drag end
				//(for logic's concern this is just two clicks
				//start and end ... except end click now use 
				//the bounds of the dragged selection
				//(instead of just a point)
				logic.updateClick(Selection.get().getBounds());
				//update the display with the changes
				OperationManager.doDisplayOperations();
				applet.canvas.paintGame(null);
		}
		
		checkGameDone();
	}
	
	/**
	 * ask logic if game is done
	 * if so update applet's status field
	 *  
	 * see run method header */
	private void checkGameDone(){
		if (logic.checkGameDone()){
			running = false;
			applet.setStatus("GAME DONE");
		}
	}
	
	/**
	 * draw the game <br>
	 * and selection if available <br>
	 * and animation if not null
	 * @param g graphics to draw on
	 * @param anim animation to draw (if not null)
	 */
	static public void draw(Graphics g, Motion anim){
		for (Stack stack : logic.getStacks()){
			stack.draw(g);
		}
		if(Selection.get().isAvailable()){
			Selection.get().draw(g);
		}
        if (anim != null){
        	anim.draw(g);
        }
	}
	
	/**
	 * draw {@link Motion} <br>
	 * draw frame by frame according to FPS until
	 * anim's isDone returns true
	 * @param anim motion to draw
	 */
	static public void drawMotion(Motion anim){
		anim.init();
		if(anim.isDone()){
			
			return;
		}
	
	    long beforeTime, afterTime, timeDiff, sleepTime;
	    long overSleepTime = 0L;
	    int noDelays = 0;
	    long excess = 0L; 
	    long period = 1000000000L/FPS;
		   
	    beforeTime = System.nanoTime();
		   
		while (true) {
			anim.updateFrame();
			applet.canvas.paintGame(anim);
	
			if (anim.isDone()){
				return;
				
			}
			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;
			sleepTime = (period - timeDiff) - overSleepTime;
				
			if (sleepTime > 0) { // some time left in this cycle
				try {
					Thread.sleep(sleepTime / 1000000L); // nano ->
														// ms
				} catch (InterruptedException ex) {
					error("animator was interrupted");
				}
				overSleepTime = (System.nanoTime() - afterTime)
						- sleepTime;
			} else { // sleepTime <= 0; the frame took longer than
						// the period
				excess -= sleepTime; // store excess time value
				overSleepTime = 0L;
	
				if (++noDelays >= NO_DELAYS_PER_YIELD) {
					Thread.yield(); // give another thread a chance
									// to run
					noDelays = 0;
				}
			}
	
			beforeTime = System.nanoTime();
	
			/*
			 * If frame animation is taking too long, update the
			 * game state without rendering it, to get the
			 * updates/sec nearer to the required FPS.
			 */
			int skips = 0;
			while ((excess > period) && (skips < MAX_FRAME_SKIPS)) {
				excess -= period;
				anim.updateFrame(); 
				skips++;
				if (anim.isDone()){
					return;
				}
			}
		}
	}
	
}
