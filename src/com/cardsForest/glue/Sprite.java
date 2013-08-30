package com.cardsForest.glue;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.RescaleOp;

/**
 * wrapper around an image <br>
 * currently only used for cards face images, <Br>
 * possibly this will contain more data later
 * 
 * @author Amit Portnoy
 *
 */
public class Sprite {
	/** sprite's image */
	private BufferedImage image;
	/** selection variant of the image */
	private BufferedImage selectedImage;
	
	/**
	 * create a new sprite
	 * also creates a selection variant image for the sprite 
	 * @param image the image to be encapsulated within the sprite
	 */
	public Sprite(BufferedImage image) {
		this.image = image;
		float scale[] = {1.f,1.f,0.3f};
		float offset[] = {0,0,-0};
		
		//create the yellow filter
		BufferedImageOp op = new RescaleOp(scale,offset,null);
		
		//create the selection variant
		selectedImage = new BufferedImage(image.getWidth(),image.getHeight()
						,image.getType());
		
		//apply the filter to the variant
		op.filter(image,selectedImage);
	}

	/**
	 * sprite width
	 * @return the width
	 */
	public int getWidth() {
		return image.getWidth();
	}

	/**
	 * sprite's height
	 * @return the height
	 */
	public int getHeight() {
		return image.getHeight();
	}
	
	/**
	 * draw this sprite
	 * @param g graphics to draw on
	 * @param x x coordinate (for left bound) 
	 * @param y y coordinate (for top bound)
	 * @param useSelected (if true draw the selection variant of the sprite)
	 */
	public void draw(Graphics g,int x,int y, boolean useSelected) {
		if (useSelected){
			g.drawImage(selectedImage, x, y, null);
	//		g.drawImage(image, x-5, y+5, null);//TODO shadow
		}
		else
			g.drawImage(image,x,y,null);
	}
}