package Controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import Controllers.BlackWhiteController;
import Controllers.DisjointSetNode;
import Controllers.FlockAnalysis;
import Controllers.InterQuartileManager;
import Controllers.Pixel;
import Controllers.UnionFind;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class BlackWhiteController implements Initializable{
	
	///////////////////////
	/// FXML variables 	///
	///////////////////////
	@FXML 
	private ImageView colourImage;
	@FXML
	private ImageView blackImage = new ImageView();
	@FXML
	private MenuItem birdRecognition;
	@FXML
	private MenuItem birdNumbering;
	@FXML
	private MenuItem flockAnalysis;
	@FXML
	private MenuItem advancedSettings;
	@FXML
	private TextArea text;
	@FXML
	private Pane pane;
	@FXML
	private Label noBirds = new Label();
	@FXML
	private Label maxArea = new Label();
	@FXML
	private Label minArea = new Label();
	@FXML
	private Label interQuartileRange = new Label();
	/////////////////////////////////////////////
	
	private Image image;
	private ImageView imageV;
	
	/*
	 * Instantiates other classes for use in methods.
	 */
	private FlockAnalysis flock = new FlockAnalysis();
	private UnionFind unionFind =  new UnionFind();
	private InterQuartileManager iqManager  = new InterQuartileManager();
	//////////////////////////////////////////////////////////////
	
	// Keeps track of number of black Pixels int the converted image.
	public int numberBlack = 0;
	
	// 2D array for all pixels in the black and white image.
	private DisjointSetNode[][] bwPixels;
	
	/*
	 * HashMap containing disjoint sets as the values, 
	 * where the keys are a combined string of the x and
	 * y coordinates of the sets root.
	 */
	private Map<String, ArrayList<DisjointSetNode>> forest = new HashMap<String, ArrayList<DisjointSetNode>>();
	/*
	 * List that is populated with the different rectangles defined by
	 * the pixel coordinates within the disjoint sets.
	 */
	private ArrayList<Rectangle> rectangleAreas = new ArrayList<Rectangle>();
	/*
	 * Array version of rectangleAreas, to allow the program to sort it at necessary.
	 */
	private Rectangle[] rectArray;
	/*
	 * 1D array of all blackPixels in the converted image.
	 */
	private DisjointSetNode[] blackPixels;
	/*
	 * Final array of rectangles, that has been processed, by removing outliers and
	 * sorting according to y coordinates.
	 */
	private Rectangle[] processedRectangles;

	/*
	 * User defined max and min limits based off interQuartile Range.
	 */
	private static double maxLimit = -1;
	private static double minLimit = -1;
	/*
	 * User defined limits for black & white conversion.
	 */
	private static double redValue = 0.5;
	private static double greenValue = 0.5;
	private static double blueValue = 0.5;
	
	///////////////////////////////////////////////
	/// Variables for interQuartile Calculation ///
	///////////////////////////////////////////////
	private double firstQuartile;
	private double thirdQuartile;
	private double interQuartile;

	/*
	 * Constructor.
	 */
	public BlackWhiteController(Pane pane , ImageView colourImage) {
		this.pane=pane;
		imageV = colourImage;
		image = colourImage.getImage();
	}
	
	/////////////////////////////////////////
	/// Methods for performing black & 	  ///
	/// white conversion pixel by pixel.  ///
	/////////////////////////////////////////
	public Image bwConvertor(ImageView colourImage) {
		numberBlack = 0;
		int width = (int) colourImage.getImage().getWidth();
		int height = (int) colourImage.getImage().getHeight();
		
		bwPixels = new DisjointSetNode[width-1][height-1];

		Color colour;
		Color blackWhite;
		WritableImage write = new WritableImage(width, height);
		
		PixelReader reader = colourImage.getImage().getPixelReader();
		PixelWriter writer = write.getPixelWriter();
		for(int yCoord= 0; yCoord<height -1; yCoord ++) {
			for(int xCoord = 0; xCoord<width - 1; xCoord++) {
				colour = reader.getColor(xCoord, yCoord);
				Pixel pix = new Pixel(xCoord, yCoord, colour);
				
				blackWhite = convertBlackWhite(pix);
				Pixel bWPixel = new Pixel(xCoord, yCoord, blackWhite);
				DisjointSetNode bwNode = new DisjointSetNode(bWPixel);
				
				bwPixels[xCoord][yCoord] = bwNode;

				writer.setColor(xCoord, yCoord, blackWhite);
			}
		}
		
		return write;
	}
		private Color convertBlackWhite(Pixel pix) {
//			Gets the values for the colour at the current pixel.
			Color colour = pix.getRgb();
			double red = colour.getRed();
			double green = colour.getGreen();
			double blue = colour.getBlue();			

			Color blackWhite;
			
			if((red < redValue) && (green < greenValue) && (blue < blueValue)) {
				blackWhite = new Color(0, 0, 0,1);
				numberBlack++;
			}
			else {
				blackWhite = new Color(1, 1, 1, 1);
			}
			return blackWhite;
		}
	
	/////////////////////////////////////////////////////////////
	/// Method that calls the methods which analyze the black ///
	///  & White photo for areas likely to contain birds      ///
	/////////////////////////////////////////////////////////////
	@FXML
	public void recognition() {
		/*
		 * Resets the relevant variables before calling processParents method.
		 */
		pane.getChildren().clear();
		
		if(forest.size()>0) {
			forest.clear();
		}
		if(rectangleAreas.size() > 0) {
			rectangleAreas.clear();
		}
		blackPixels = null;
		rectArray = null;
		processedRectangles = null;
		DisjointSetNode[] rectangleArray = processParents(colourImage);
		setKeys(rectangleArray);
		populateLists();
		populateRectangleAreas();
		drawRects();
	}
	
	///////////////////////////////////////////////////////
	/// Method to analyze the most distant bird/cluster ///
	///////////////////////////////////////////////////////
	@FXML
	public void flockAnalysis() {
		if(processedRectangles != null) {
			Rectangle rectangle = flock.findOutlier(processedRectangles);
			rectangle.setStroke(Color.RED);
			rectangle.setStrokeWidth(3);
			
			/*
			 * Labels the most distant cluster on the pane.
			 */
			Text text = new Text();
			text.setText("Most Distant");
			text.setX((rectangle.getX() - (rectangle.getWidth()/4)));
			text.setY(rectangle.getY() + rectangle.getHeight() + 20);
			text.setFont(new Font(18));
			pane.getChildren().add(text);
			text.setVisible(true);
		}else {
			System.out.println("Must perform bird recogntion before finding outlier");
		}
	}
	
	///////////////////////////////////////////////////////////
	/// Loads the settings Window to define the limits for  ///
	/// area of likely bird clusters						///
	///////////////////////////////////////////////////////////
	@FXML
	private void settingsWindow() {
		try {
			FXMLLoader loader;
			loader = new FXMLLoader(getClass().getResource("/Controllers/AdvancedSettings.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root, 300, 300);
			scene.getStylesheets().add(getClass().getResource(
					"/application/application.css").toExternalForm());
			Stage settingsStage = new Stage();
			settingsStage.setScene(scene);
			settingsStage.show();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	////////////////////////////////////////////////////////////
	/// Method for sequential numbering of the bounded areas ///
	////////////////////////////////////////////////////////////
	@FXML
	public void numbering() {
		calculateNumbers(processedRectangles);
	}
	
	//////////////////////////////////////////////////////////
	/// Iterates over the image and populates the disjoint ///
	/// sets of black pixels							   ///
	//////////////////////////////////////////////////////////
	public DisjointSetNode[] processParents(ImageView colourImage) {
		int width = (int) colourImage.getImage().getWidth();
		int height = (int) colourImage.getImage().getHeight();
		blackPixels = new DisjointSetNode[numberBlack];
		
		int index = 0;
		DisjointSetNode currentNode;
		DisjointSetNode leftNode;
		DisjointSetNode rightNode;
		DisjointSetNode upNode;
		DisjointSetNode downNode;
		/*
		 * Iterates over the pixels of the image and when it finds a black pixel
		 * it checks if the adjacent pixels are black and performs the union operation
		 */
		for(int yCoord= 0; yCoord<height -1; yCoord ++) {
			for(int xCoord = 0; xCoord<width - 1; xCoord++) {
				currentNode = bwPixels[xCoord][yCoord];
				Color currentColour = currentNode.getData().getRgb();
				if((currentColour.getRed() == 0)&&(currentColour.getBlue() == 0)&&(currentColour.getGreen() == 0)) {

					blackPixels[index] = currentNode;
					index++;

					if(currentNode.getData().getxCoord() > 0) {
						leftNode = bwPixels[xCoord -1][yCoord];
						Color leftColour = leftNode.getData().getRgb();
						if((leftColour.getRed() == 0)&&(leftColour.getBlue() == 0)&&(leftColour.getGreen() == 0)) {
								unionFind.unionBySize(currentNode, leftNode);
						}
					}
					if(currentNode.getData().getxCoord() < (width - 2)) {
						rightNode = bwPixels[xCoord + 1][yCoord];
						Color rightColour = rightNode.getData().getRgb();
						if((rightColour.getRed() == 0)&&(rightColour.getBlue() == 0)&&(rightColour.getGreen() == 0)) {
								unionFind.unionBySize(currentNode, rightNode);
						}
					}
					if(currentNode.getData().getyCoord() > 0) {
						upNode = bwPixels[xCoord][yCoord - 1];
						Color upColour = upNode.getData().getRgb();
						if((upColour.getRed() == 0)&&(upColour.getBlue() == 0)&&(upColour.getGreen() == 0)) {
								unionFind.unionBySize(currentNode, upNode);
						}
					}
					if(currentNode.getData().getyCoord() < (height - 2)) {
						downNode = bwPixels[xCoord][yCoord+1];
						Color downColour = downNode.getData().getRgb();
						if((downColour.getRed() == 0)&&(downColour.getBlue() == 0)&&(downColour.getGreen() == 0)) {
								unionFind.unionBySize(currentNode, downNode);
						}
					}
				}
			}
		}
		return blackPixels;
	}
	
	

	public void setKeys(DisjointSetNode[] blackPixels){
		forest = new HashMap<String, ArrayList<DisjointSetNode>>();
		/*
		 * Iterates over all the blackPixels and checks if the pixel is the root
		 * of a set, then adds these roots at the first index of a an arraylist
		 * within the hashmap.
		 */
		for(int i =0; i< blackPixels.length;i++) {
			if(blackPixels[i].getParent() == null) {
				DisjointSetNode root = blackPixels[i];
				Pixel pixel=  root.getData();
				int xCoord = pixel.getxCoord();
				int yCoord = pixel.getyCoord();
				/*
				 * Constructs key based on the x Coordinate and y coordinate of the root
				 * of the set e.g. a root at x = 20, y = 100, will create a key of a 
				 * string = "20,100".
				 */
				String key = String.valueOf(xCoord) + "," + String.valueOf(yCoord);
				ArrayList<DisjointSetNode> list = new ArrayList<DisjointSetNode>();
				list.add(root);
				forest.put(key, list);
			}
		}
	}
	
	/////////////////////////////////////////////////
	/// Method that iterates over the blackPixels ///
	/// finds if the pixel is a child and then    ///
	/// adds it to the relevant entry of the map  ///
	/////////////////////////////////////////////////
	public void populateLists() {
		for(int i =0; i< blackPixels.length;i++) {
			if(blackPixels[i].getParent() != null) {
				DisjointSetNode child = blackPixels[i];
				DisjointSetNode root = unionFind.find(child);
				Pixel rootPixel = root.getData();
				int rootXCoord = rootPixel.getxCoord();
				int rootYCoord = rootPixel.getyCoord();
				String rootKey = String.valueOf(rootXCoord) + "," + String.valueOf(rootYCoord);
				/*
				 * Searches for the relevant entry of the map and then adds the pixel to the array
				 * at this entry.
				 */
				forest.get(rootKey).add(child);
			}
		}
	}
	
	
	public int populateRectangleAreas() {
		/*
		 * Iterates each entry in the tree hash map.
		 */
		for(Entry<String, ArrayList<DisjointSetNode>> entry: forest.entrySet()) {
			int minX = -1;
			int maxX = -1;
			int minY = -1;
			int maxY = -1;
			// Gets the value at the current entry.
			ArrayList<DisjointSetNode> tree = entry.getValue();
			/*
			 * Iterates over the arraylist at each value, finds the minimum and maximum coordinates
			 * along the x and y axis
			 */
			for(DisjointSetNode node: tree) {
				Pixel pixel = node.getData();
				int xCoord = pixel.getxCoord();
				int yCoord = pixel.getyCoord();
				if(minX == -1) {
					minX = xCoord;
					maxX = xCoord;
					minY = yCoord;
					maxY = yCoord;
				}
				else{
					if(xCoord < minX) {
						minX = xCoord;
					}
					if(xCoord > maxX) {
						maxX = xCoord;
					}
					if(yCoord < minY) {
						minY = yCoord;
					}
					if(yCoord > maxY) {
						maxY = yCoord;
					}
				}
			}
			
			int width = maxX - minX;
			int height = maxY - minY;
			
			/*
			 * Converts the coordinates so that they match up with the coordinates in 
			 * the javafx view.
			 */
			if(width > 0 && height> 0) {
				double relativeX =  (minX/(imageV.getImage().getWidth()))*1004;
				int xVal = (int) relativeX;
				double relativeY = (minY/(imageV.getImage().getHeight()))*670;
				int yVal = (int) relativeY;
				double relativeWidth = (width/(imageV.getImage().getWidth()))*1004;
				int widthVal = (int) relativeWidth;
				double relativeHeight = (height/(imageV.getImage().getHeight()))*670;
				int heightVal = (int) relativeHeight;
				
				/*
				 * Creates rectangle based on each of the discovered values then adds
				 * this to the rectangleAreas list.
				 */
				Rectangle rectangle;
				rectangle = new Rectangle();
				rectangle.setX(xVal);
				rectangle.setY(yVal);
				rectangle.setWidth(widthVal);
				rectangle.setHeight(heightVal);
				rectangleAreas.add(rectangle);
			}
		}
		return rectangleAreas.size();
	}
	
	////////////////////////////////////////////////////////////////////
	/// Method that draw the bounded areas as rectangles to the pane ///
	////////////////////////////////////////////////////////////////////
	public void drawRects() {
		int rectIndex = 0;
		/*
		 * Converts the rectangleAreas list to an array.
		 */
		rectArray =  new Rectangle[rectangleAreas.size()];
		for(Rectangle rectangle: rectangleAreas) {
			rectArray[rectIndex] = rectangle;
			rectIndex++;
		}
		/*
		 * Sorts the rectArray area according to area to allow calculation of inter Quartile range.
		 */
		unionFind.quickSortArea(rectArray, 0, rectArray.length -1);
		
		Rectangle largest = rectArray[rectArray.length-1];
		double biggestArea = largest.getWidth() * largest.getHeight();
		/*
		 * Calls method that returns array which contains values of median, first quartile, second quartile
		 * and interquartile range, then sets the variable in this class to these values.
		 */
 		double[] quartiles = iqManager.interQuartileArea(rectArray);
 		firstQuartile = quartiles[1];
 		thirdQuartile = quartiles[2];
 		interQuartile = quartiles[3];
 		
 		/*
 		 * Processes the array and removes outliers as defined by user.
 		 */
		Rectangle[] rects = iqManager.outlierManagement(rectArray, minLimit, maxLimit, interQuartile, firstQuartile,
				thirdQuartile);
		processedRectangles = new Rectangle[rects.length];
		for(int i =0; i<rects.length; i++) {
			processedRectangles[i] = rects[i];
		}
		
		/*
		 * sorts the Rectangles according to their y coordinate for use of the numbering 
		 * method.
		 */
		unionFind.quickSortYVal(processedRectangles, 0, processedRectangles.length -1);
		for(Rectangle rectangle: processedRectangles) {
			pane.getChildren().add(rectangle);
			rectangle.setStroke(Color.BLUE);
			rectangle.setStrokeWidth(1);
			rectangle.setFill(null);
			rectangle.setVisible(true);	
		}
		
		///////////////////////////////////////////////////////////////
		/// Defines the different variables which will be displayed ///
		/// on the screen.                                          ///
		///////////////////////////////////////////////////////////////
		String birds = String .valueOf(processedRectangles.length);
		String max;
		
		
		if(maxLimit == -1) {
			max = String.valueOf(biggestArea);
		}
		else {
			max = String.valueOf(thirdQuartile + (interQuartile * maxLimit));

		}
		double minDouble;
		if(firstQuartile < interQuartile*minLimit) {
			minDouble = 1;
		}
		else if(minLimit == -1) {
			minDouble = 1;
		}
		else {
			minDouble = firstQuartile - (interQuartile * minLimit);
		}
		String min = String.valueOf(minDouble);
		String inter = String.valueOf(interQuartile);
		noBirds.setText(birds);
		maxArea.setText(max);
		minArea.setText(min);
		interQuartileRange.setText(inter);
		
	}
	
	
	public int calculateNumbers(Rectangle[] rectArray) {
		/*
		 * Ensures that bounded areas have been calculated already.
		 */
		int index = 0;
		if(rectArray!= null) {
			/*
			 * Iterates over the rectangles and then adds text of the
			 * current index onto the pane.
			 */
			for(Rectangle rectangle: rectArray) {
				index++;
				Text text = new Text();
				text.setText(String.valueOf(index));
				text.setX((rectangle.getX())+rectangle.getWidth());
				text.setY(rectangle.getY());
				text.setFont(new Font(14));
				pane.getChildren().add(text);
				text.setVisible(true);
			}
		}else {
			System.out.println("Must Calculate Bounded Areas Before Numbering");
		}
		return index;
	}
	////////////////////////////////////////////////////////////////
	
	/////////////////////////
	/// Getters & Setters ///
	/////////////////////////
	public static double getMaxLimit() {
		return maxLimit;
	}
	public static void setMaxLimit(double maxLimit) {
		BlackWhiteController.maxLimit = maxLimit;
	}
	public static double getMinLimit() {
		return minLimit;
	}
	public static double getRedValue() {
		return redValue;
	}

	public static void setRedValue(double redValue) {
		BlackWhiteController.redValue = redValue;
	}

	public static double getGreenValue() {
		return greenValue;
	}

	public static void setGreenValue(double greenValue) {
		BlackWhiteController.greenValue = greenValue;
	}

	public static double getBlueValue() {
		return blueValue;
	}

	public static void setBlueValue(double blueValue) {
		BlackWhiteController.blueValue = blueValue;
	}

	public static void setMinLimit(double minLimit) {
		BlackWhiteController.minLimit = minLimit;
	}
	public Rectangle[] getProcessedRectangles() {
		return processedRectangles;
	}
	public ImageView getColourImage() {
		return colourImage;
	}
	public void setColourImage(ImageView colourImage) {
		this.colourImage = colourImage;
	}
	
	public ImageView getBlackImage() {
		return blackImage;
	}

	public void setBlackImage(ImageView blackImage) {
		this.blackImage = blackImage;
	}
	
	public void setBlack(Image image) {
		blackImage.setImage(image);
	}

	public double getFirstQuartile() {
		return firstQuartile;
	}

	public double getThirdQuartile() {
		return thirdQuartile;
	}

	public double getInterQuartile() {
		return interQuartile;
	}
	
	public DisjointSetNode[] getBlackPixels() {
		return blackPixels;
	}
	
	public Rectangle[] getRectArray() {
		return rectArray;
	}

	public void setRectArray(Rectangle[] rectArray) {
		this.rectArray = rectArray;
	}

	///////////////////////////////////////////////////
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		colourImage.setImage(image);
		blackImage.setImage(bwConvertor(colourImage));
		
	}
	

}

