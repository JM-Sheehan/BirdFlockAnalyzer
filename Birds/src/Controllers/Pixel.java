package Controllers;

import javafx.scene.paint.Color;

//////////////////////////////////////////////////////////////////////////////////
/// Class that stores pixel data for the image along with getters and setters. ///
//////////////////////////////////////////////////////////////////////////////////
public class Pixel {

	private int xCoord;
	private int yCoord;
	private Color rgb;
	
	public Pixel(int xCoord, int yCoord, Color rgb) {
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.rgb = rgb;
	}

	public int getxCoord() {
		return xCoord;
	}

	public void setxCoord(int xCoord) {
		this.xCoord = xCoord;
	}

	public int getyCoord() {
		return yCoord;
	}

	public void setyCoord(int yCoord) {
		this.yCoord = yCoord;
	}

	public Color getRgb() {
		return rgb;
	}

	public void setRgb(Color rgb) {
		this.rgb = rgb;
	}
}
