/**
 * 
 */
package com.cardsForest.platform;

import javax.swing.SwingUtilities;

/**
 * @author Amit Portnoy
 *
 */
public class Shortcuts {
	
	static public void print(Object str){
		System.out.println(str);
	}
	
	static public void exit(){
		System.exit(0);
	}
	static public void error(String str){
		print("Error: " + str);
		exit();
	}
	
	static public void swing(Runnable runnable){
		try {
			SwingUtilities.invokeAndWait(runnable);
		} catch (Exception e) {
			e.printStackTrace();
			error("while trying to invoke swing");
		}
	}
}
