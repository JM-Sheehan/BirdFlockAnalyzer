package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import Controllers.BlackWhiteController;
import Controllers.FlockAnalysis;
import Controllers.InterQuartileManager;
import Controllers.UnionFind;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.embed.swing.JFXPanel;

import javafx.scene.shape.Rectangle;

public class BirdsTest {
	
	JFXPanel fxPanel = new JFXPanel();
	private ImageView blackImageView = new ImageView();
	private Image testImage;
	private Image blackImage;
	private Image whiteImage;
	private ImageView imView = new ImageView();
	
	private FlockAnalysis flock = new FlockAnalysis();
	private UnionFind unionFind = new UnionFind();
	private InterQuartileManager iqManager = new InterQuartileManager();
	private BlackWhiteController bw = new BlackWhiteController(new Pane(), imView);
	
	private Rectangle recA = new Rectangle(1,1,2,2);
	private Rectangle recB = new Rectangle(2,2,3,3);
	private Rectangle recC = new Rectangle(3,3,4,4);
	private Rectangle recD = new Rectangle(100,100,65,5);
	
	private Rectangle[] rectangles = {recA, recB, recC, recD};
	private Rectangle[] quartiles = new Rectangle[10];
	private Rectangle[] outliers = new Rectangle[13];
	private Rectangle[] origOutliers = new Rectangle[13];
	
	
	@Before
	public void setup(){
		testImage = new Image(("./resource/test.jpg"));
		blackImage = new Image(("./resource/black.png"));
		whiteImage = new Image(("./resource/white.png"));
		
		quartiles[0] = new Rectangle(1,1,2,2);
		quartiles[1] = new Rectangle(4,4,6,5);
		quartiles[2] = new Rectangle(5,5,8,10);
		quartiles[3] = new Rectangle(33,43,7,4);
		quartiles[4] = new Rectangle(5,3,3,2);
		quartiles[5] = new Rectangle(7,7,6,6);
		quartiles[6] = new Rectangle(9,3,5.5,5);
		quartiles[7] = new Rectangle(100,11, 9,10);
		quartiles[8] = new Rectangle(22,43,5,5);
		quartiles[9] = new Rectangle(6,33,6.5,5);
		
		outliers[0] = new Rectangle(1,49,2,2);
		outliers[1] = new Rectangle(2,2,100,100);
		outliers[2] = new Rectangle(3,100,101,101);
		outliers[3] = new Rectangle(4,4,102,102);
		outliers[4] = new Rectangle(5,500,103,103);
		outliers[5] = new Rectangle(6,6,104,104);
		outliers[6] = new Rectangle(7,300,105,105);
		outliers[7] = new Rectangle(8,8,106,106);
		outliers[8] = new Rectangle(9,9,107,108);
		outliers[9] = new Rectangle(10,10,110,120.4);
		outliers[10] = new Rectangle(11,121,200,100);
		outliers[11] = new Rectangle(12,12,1000,1000);
		outliers[12] = new Rectangle(14,1,1002,1020);
		
		for(int i =0;i<outliers.length;i++) {
			origOutliers[i] = outliers[i];
		}

	}
	/*
	 * Tests whether the black & white conversion is being performed as expected.
	 */
	@Test
	public void converterTestBlack() {
		boolean converted = true;
		imView.setImage(blackImage);
		
		int width = (int) blackImage.getWidth();
		int height = (int) blackImage.getHeight();
		Image convertedImage = bw.bwConvertor(imView);
		PixelReader reader = convertedImage.getPixelReader();
		for(int yCoord = 0; yCoord < height-1; yCoord++) {
			for(int xCoord = 0; xCoord < width-1; xCoord++) {
				Color colour = reader.getColor(xCoord, yCoord);
				if(!((colour.getBlue()== 0)&&(colour.getRed()==0)&&(colour.getGreen()==0))) {
					converted = false;
				}
			}
		}
		/*
		 * The test passes if all pixels in the image are black.
		 */
		assertEquals(converted, true);
		
	}
	
	@Test
	public void converterTestWhite() {
		boolean converted = true;
		imView.setImage(whiteImage);
		
		int width = (int) whiteImage.getWidth();
		int height = (int) whiteImage.getHeight();
		Image convertedImage = bw.bwConvertor(imView);
		PixelReader reader = convertedImage.getPixelReader();
		for(int yCoord = 0; yCoord < height-1; yCoord++) {
			for(int xCoord = 0; xCoord < width-1; xCoord++) {
				Color colour = reader.getColor(xCoord, yCoord);
				if(!((colour.getBlue()== 1)&&(colour.getRed()==1)&&(colour.getGreen()==1))) {
					converted = false;
				}
			}
		}
		/*
		 * The test passes if all pixels in the image are black.
		 */
		assertEquals(converted, true);
		
	}
	
	/*
	 * Test uses an image with a known number of birds, 10 in this case and
	 * passes when the recognition methods return 10 as the result.
	 */
	@Test
	public void birdRecognitionTest() {
		imView.setImage(testImage);
		bw.setColourImage(imView);
		blackImageView.setImage(bw.bwConvertor(imView));
		bw.setKeys(bw.processParents(bw.getColourImage()));
		bw.populateLists();
		int result = bw.populateRectangleAreas();
		
		assertEquals(result, 10);
	}
	
	/*
	 * Test passes if calculateNumbers method returns the correct number
	 * according to number of rectangles in the array.
	 */
	@Test
	public void numberingTest() {
		int number = bw.calculateNumbers(rectangles);
		assertEquals(number, 4);
	}
	
	/*
	 * Test uses a rectangle array with an obvious most distant rectangle
	 * in the array an passes if this rectangle is the result of the 
	 * findOutlier method.
	 */
	@Test
	public void mostDistantTest() {
		Rectangle rectangle = flock.findOutlier(rectangles);
		Rectangle expected = rectangles[3];
		assertTrue(rectangle.equals(expected));
	}
	
	/*
	 * Passes an array of rectangles with a known figure for median firstQuartile,
	 * thirdQuartile and interQuartileRange, the test will then pass if the
	 * method returns results equal to these knowns.
	 */
	@Test
	public void interQuartileTest() {
		Rectangle[] rects = unionFind.quickSortArea(quartiles, 0, quartiles.length-1);
		double[] results = iqManager.interQuartileArea(rects);
		double median = results[0];
		double first = results[1];
		double third = results[2];
		double interQuartile = results[3];
		assertTrue(median == 29);
		assertTrue(first == 15.5);
		assertTrue(third == 58);
		assertTrue(interQuartile == 42.5);
		}

	/*
	 * Test to confirm whether outlier management is performed correctly according
	 * to the different minimum and maximum range. 
	 */
	@Test
	public void outlierTest() {
		Rectangle[] rects = unionFind.quickSortArea(outliers, 0, outliers.length-1);
		double[] results = iqManager.interQuartileArea(rects);
		double first = results[1];
		double third = results[2];
		double interQuartile = results[3];
		Rectangle[] outliersRemoved = iqManager.outlierManagement(rects,
				1, 1, interQuartile, first, third);
		assertEquals(outliersRemoved.length, 10);
		
		Rectangle[] outliersTwo = iqManager.outlierManagement(rects,
				0.5, 0.5, interQuartile, first, third);
		assertEquals(outliersTwo.length, 9);		
	}
	
	/*
	 * Test to confirm whether the two different sorting algorithms are performed correctly
	 */
	@Test
	public void sorting() {
		Rectangle[] rectsArea = unionFind.quickSortArea(outliers, 0, outliers.length-1);
		Rectangle[] rectsY = unionFind.quickSortYVal(outliers, 0, outliers.length - 1);
		
		assertTrue(areaSort(rectsArea));
		assertTrue(ySort(rectsY));
	}
	
	//Compares the values at the entries of the array match what was expected according to area..
	public boolean areaSort(Rectangle[] rectArray) {
		boolean sorted = false;
		for(int i =0; i<rectArray.length;i++) {
			if(rectEquals(rectArray[i], origOutliers[i])) {
				sorted = true;
			}
		}
		return sorted;
	}
	
	//Compares the values at the entries of the array match what was expected according to y-coordinate..
	public boolean ySort(Rectangle[] rectArray) {
		boolean sorted = true;
		if(!(rectEquals(rectArray[0], origOutliers[12]))) {
			sorted = false;
		}
		if(!(rectEquals(rectArray[1], origOutliers[1]))) {
			sorted = false;
		}
		if(!(rectEquals(rectArray[2], origOutliers[3]))) {
			sorted = false;
		}
		if(!(rectEquals(rectArray[3], origOutliers[5]))) {
			sorted = false;
		}
		if(!(rectEquals(rectArray[4], origOutliers[7]))) {
			sorted = false;
		}
		if(!(rectEquals(rectArray[5], origOutliers[8]))) {
			sorted = false;
		}
		if(!(rectEquals(rectArray[6], origOutliers[9]))) {
			sorted = false;
		}
		if(!(rectEquals(rectArray[7], origOutliers[11]))) {
			sorted = false;
		}
		if(!(rectEquals(rectArray[8], origOutliers[0]))) {
			sorted = false;
		}
		if(!(rectEquals(rectArray[9], origOutliers[2]))) {
			sorted = false;
		}
		if(!(rectEquals(rectArray[10], origOutliers[10]))) {
			sorted = false;
		}
		if(!(rectEquals(rectArray[11], origOutliers[6]))) {
			sorted = false;
		}
		if(!(rectEquals(rectArray[12], origOutliers[4]))) {
			sorted = false;
		}
		return sorted;
	}
	
	/*
	 * Custom equals method to verify if two rectangles are equivalent.
	 */
	public boolean rectEquals(Rectangle rectA, Rectangle rectB) {
		boolean equals = false;
		if((rectA.getX() == rectB.getX())  && (rectA.getY() == rectB.getY()) && 
				(rectA.getWidth() == rectB.getWidth()) && (rectA.getHeight() == rectB.getHeight())) {
			equals = true;
		}
		return equals;
	}
}
