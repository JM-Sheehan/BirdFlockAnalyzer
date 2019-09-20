package Controllers;

import javafx.scene.shape.Rectangle;

public class InterQuartileManager {

	public InterQuartileManager() {
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Uses inter quartile range formula along with a series of conditions ///
	///to calculate the interquartile range of this array of rectangles     ///
	///////////////////////////////////////////////////////////////////////////
	public double[] interQuartileArea(Rectangle[] rects) {
		double iqRange =0;
		int length = rects.length;
		/*
		 * Creates array to contain the results.
		 */
		double[] results = new double[4];
		
		// Checks if the length of the array is odd
		if(length%2 !=0) {
			// Index of median formula for an array of odd size.
			int medianIndex = (length - 1)/2;
			// Width and height of median.
			double width = rects[medianIndex].getWidth();
			double height = rects[medianIndex].getHeight();
			// Area of median.
			double medianArea = width * height;
			// set the first index of results to the median area.
			results[0]  = medianArea;
			// Checks if length of the quartiles is even.
			if(medianIndex%2 == 0) {
				/*
				 * Index of two values needed to calculate first quartile.
				 */
				int qOneFirstIndex = medianIndex/2;
				int qOneSecondIndex = qOneFirstIndex -1;
				
				/*
				 * Calculates width, height and hence area for rectangles at both indexes.
				 */
				double qOneFirstWidth = rects[qOneFirstIndex].getWidth();
				double qOneFirstHeight = rects[qOneFirstIndex].getHeight();
				double qOneFirstArea = qOneFirstWidth * qOneFirstHeight;
				double qOneSecondWidth = rects[qOneSecondIndex].getWidth();				
				double qOneSecondHeight = rects[qOneSecondIndex].getHeight();
				double qOneSecondArea = qOneSecondWidth * qOneSecondHeight;
				
				// calculates first quartile using these two values.
				double qOne = (qOneFirstArea + qOneSecondArea)/2;
				
				/*
				 * Index of two values needed to calculate second quartile.
				 */
				int qThreeFirstIndex = length - qOneFirstIndex;
				int qThreeSecondIndex = qThreeFirstIndex - 1;
				
				/*
				 * Calculates width, height and hence area for rectangles at both indexes.
				 */
				double qThreeFirstWidth = rects[qThreeFirstIndex].getWidth();
				double qThreeSecondWidth = rects[qThreeSecondIndex].getWidth();
				double qThreeFirstHeight = rects[qThreeFirstIndex].getHeight();
				double qThreeSecondHeight = rects[qThreeSecondIndex].getHeight();
				double qThreeFirstArea = qThreeFirstWidth * qThreeFirstHeight;
				double qThreeSecondArea = qThreeSecondWidth * qThreeSecondHeight;
				
				// Calculates second quartile using two area values.
				double qThree = (qThreeFirstArea + qThreeSecondArea)/2;
			
				// Adds the quartile values to the array.
				results[1] = qOne;
				results[2] = qThree;
				
				// Calculates inter quartile range.
				iqRange = qThree -  qOne;
			}
			/*
			 * Respective process when the quartiles are odd in length, which is relatively simpler.
			 */
			else {
				int qOneIndex = (medianIndex -1)/2;
				int qThreeIndex = (length - qOneIndex - 1);
				
				double qOneWidth = rects[qOneIndex].getWidth();
				double qOneHeight = rects[qOneIndex].getHeight();
				double qThreeWidth = rects[qThreeIndex].getWidth();
				double qThreeHeight = rects[qThreeIndex].getHeight();
				
				double qOne = qOneWidth * qOneHeight;
				double qThree = qThreeWidth * qThreeHeight;
				results[1] = qOne;
				results[2] = qThree;
				iqRange  = qThree - qOne;
			}
			
			
		}
		/*
		 * Used when length of array is even.
		 */
		else {
			int firstMedianIndex = (length)/2;
			int thirdMedianIndex = firstMedianIndex -1;
			
			double firstWidth = rects[firstMedianIndex].getWidth();
			double firstHeight = rects[firstMedianIndex].getHeight();
			double firstArea = firstWidth * firstHeight;
			
			double thirdWidth = rects[thirdMedianIndex].getWidth();
			double thirdHeight = rects[thirdMedianIndex].getHeight();
			double thirdArea = thirdWidth * thirdHeight;
			
			double med = (firstArea + thirdArea)/2;
			results[0] = med;
			// Checks if length of quartiles is even.
			if(thirdMedianIndex%2 == 0) {
				int qOneFirstIndex = thirdMedianIndex/2;
				int qOneSecondIndex = qOneFirstIndex -1;
				
				double qOneFirstWidth = rects[qOneFirstIndex].getWidth();
				double qOneSecondWidth = rects[qOneSecondIndex].getWidth();
				double qOneFirstHeight = rects[qOneFirstIndex].getHeight();
				double qOneSecondHeight = rects[qOneSecondIndex].getHeight();
				double qOneFirstArea = qOneFirstWidth * qOneFirstHeight;
				double qOneSecondArea = qOneSecondWidth * qOneSecondHeight;
				
				double qOne = (qOneFirstArea + qOneSecondArea)/2;
				
				
				int qThreeFirstIndex = length - qOneFirstIndex;
				int qThreeSecondIndex = qThreeFirstIndex - 1;

				double qThreeFirstWidth = rects[qThreeFirstIndex].getWidth();
				double qThreeSecondWidth = rects[qThreeSecondIndex].getWidth();
				double qThreeFirstHeight = rects[qThreeFirstIndex].getHeight();
				double qThreeSecondHeight = rects[qThreeSecondIndex].getHeight();
				double qThreeFirstArea = qThreeFirstWidth * qThreeFirstHeight;
				double qThreeSecondArea = qThreeSecondWidth * qThreeSecondHeight;
				
				double qThree = (qThreeFirstArea + qThreeSecondArea)/2;
				results[1] = qOne;
				results[2] = qThree;

				iqRange = qThree -  qOne;
			}
			/*
			 * used when length of quartiles is odd.
			 */
			else {
				int qOneIndex = (thirdMedianIndex -1)/2;
				int qThreeIndex = (length - qOneIndex - 1);
				
				double qOneWidth = rects[qOneIndex].getWidth();
				double qOneHeight = rects[qOneIndex].getHeight();
				double qThreeWidth = rects[qThreeIndex].getWidth();
				double qThreeHeight = rects[qThreeIndex].getHeight();
				
				double qOne = qOneWidth * qOneHeight;
				double qThree = qThreeWidth * qThreeHeight;
				
				results[1] = qOne;
				results[2] = qThree;
				iqRange  = qThree - qOne;
			}
			
		}
		// sets the last index of results array = inter quartile range and returns the array.
		results[3] = iqRange;
		return results;
	}
	
	/////////////////////////////////////////////////
	/// Method that returns a new rectangle array ///
	/// that has removed the outliers as defined  ///
	/// by the user.							  ///
	/////////////////////////////////////////////////
	public Rectangle[] outlierManagement(Rectangle[] rectArray, double minLimit, double maxLimit,
			double interQuartile, double firstQuartile, double thirdQuartile) {
		Rectangle [ ] processedRectangles;
		int numberRects = 0;
		if((minLimit != -1) &&(maxLimit != -1)) {
			double minimum = interQuartile * minLimit;
			if(minimum <= 0) {
				minimum = 1;
			}
			double maximum = interQuartile * maxLimit;
			for(Rectangle rectangle: rectArray) {
				double width = rectangle.getWidth();
				double height = rectangle.getHeight();
				double area = width * height;
				if((area>=(firstQuartile - minimum)) && (area<=(thirdQuartile + maximum))) {
					numberRects++;
				}
			}
			
			 processedRectangles = new Rectangle[numberRects];
			
			int index = 0;
			for(Rectangle rectangle: rectArray) {
				double width = rectangle.getWidth();
				double height = rectangle.getHeight();
				double area = width * height;
				if((area>=(firstQuartile - minimum)) && (area<=(thirdQuartile + maximum))) {
					processedRectangles[index] = rectangle;
					index++;
				}
			}
		}
		else if((minLimit != -1) && (maxLimit == -1)) {
			double minimum = interQuartile * minLimit;
			for(Rectangle rectangle: rectArray) {
				double width = rectangle.getWidth();
				double height = rectangle.getHeight();
				double area = width * height;
				if((area>=(firstQuartile - minimum))) {
					numberRects++;
				}
			}
			
			processedRectangles = new Rectangle[numberRects];
			
			int index = 0;
			for(Rectangle rectangle: rectArray) {
				double width = rectangle.getWidth();
				double height = rectangle.getHeight();
				double area = width * height;
				if((area>=(firstQuartile - minimum))) {
					processedRectangles[index] = rectangle;
					index++;
				}
			}
		}
		else if((minLimit == -1) && (maxLimit != -1)) {
			double maximum = interQuartile * maxLimit;
			for(Rectangle rectangle: rectArray) {
				double width = rectangle.getWidth();
				double height = rectangle.getHeight();
				double area = width * height;
				if((area<=(thirdQuartile + maximum))) {
					numberRects++;
				}
			}
			
			 processedRectangles = new Rectangle[numberRects];
			
			int index = 0;
			for(Rectangle rectangle: rectArray) {
				double width = rectangle.getWidth();
				double height = rectangle.getHeight();
				double area = width * height;
				if((area<=(thirdQuartile + maximum))) {
					processedRectangles[index] = rectangle;
					index++;
				}
			}
		}
		else {
			 processedRectangles= rectArray;
		}
		return processedRectangles;
	}
}
