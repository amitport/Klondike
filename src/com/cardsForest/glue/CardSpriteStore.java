package com.cardsForest.glue;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.cardsForest.foundations.Face;

import static com.cardsForest.platform.Shortcuts.*;

/**
 * supply the game with card related graphics (sprite)
 * TODO later this will be a much more efficient implementation
 * of image loading (vector graphics / use of one big image)
 * 
 * @see Sprite
 * @author Amit Portnoy
 */
public final class CardSpriteStore {

	/** hold the path to image resources */
	final static String basepath = "classic-cards/";
	
	/** holds the sprites for fast retrieval */
	private static Map<Face,Sprite> sprites = new EnumMap<Face,Sprite>(Face.class);
	/** holds the back card sprite */
	private static Sprite back = null;
	
	/** 
	 * get standard card height
	 * @return card sprite height
	 */
	public static int getHeight(){
		return back.getHeight();
	}
	
	/** 
	 * get standard card width 
	 * @return card sprite width 
	 */
	public static int getWidth(){
		return back.getWidth();
	}
	
	/** 
	 * get the back of a card sprite
	 * @return the back sprite
	 */
	public static Sprite getBack(){
		if (back == null)
			back = loadSprite(basepath + "back.png");
		return back;	
	}
	
	/**
	 * get the sprite corresponding to the face
	 * @param face requested sprite's face
	 * @return sprite with the requested face
	 */
	public static Sprite getSprite(Face face){
		Sprite sprite = sprites.get(face);
		if (sprite == null){
			sprite = loadSprite(basepath + face.name()+".png");
			sprites.put(face,sprite);
		}
		return sprite;
	}
	
	/**
	 * load all card sprites to memory
	 */
	public static void loadAll(){
		getBack();
		for(Face face : Face.values()){
			getSprite(face);
		}
	}
	
	/**
	 * load one card sprite to memory
	 * @param imageResource path to the sprite image file (currently PNG)
	 * @return prepared and loaded sprite
	 */
	private static Sprite loadSprite(String imageResource) {
		Sprite sprite;
		//look for the image file
		URL url = CardSpriteStore.class.getClassLoader().getResource(imageResource);
		
		if (url == null) {
			//file was not found
			error("resource not found");
		}
		
		//read the image to memory
		BufferedImage sourceImage = null;
		try {
			sourceImage = ImageIO.read(url);
		} catch (IOException e) {
			error("couldn't read image");
		}
		
		//create a better buffer for the image
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		BufferedImage image = gc.createCompatibleImage(sourceImage.getWidth(),sourceImage.getHeight(),sourceImage.getColorModel( ).getTransparency( ));

		//copy the image to the better buffer
		Graphics2D g  = (Graphics2D) image.createGraphics();
		g.drawImage(sourceImage,0,0,null);
		g.dispose();
		
		//save the image in cache
		sprite = new Sprite(image);
		
		return sprite;
	}
}
