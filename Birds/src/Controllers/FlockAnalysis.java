package Controllers;

import javafx.scene.shape.Rectangle;

public class FlockAnalysis {

	public FlockAnalysis() {
	}
	//////////////////////////////////////////////////////////////////////
	/// Iterates over the rectangles and calls the pythagerous method  ///
	/// to discover which cluster is the most removed from all others. ///
	//////////////////////////////////////////////////////////////////////
	public Rectangle findOutlier(Rectangle[] rectangles) {
		Rectangle outlierRect = rectangles[0];
		double minDistance = 0;
		for(int i = 0; i < rectangles.length; i++) {
			Rectangle rectA = rectangles[i];
			Rectangle rectB;
			if(i == 0) {
				rectB = rectangles[1];
			}else {
				rectB = rectangles[0];
			}
			double relativeMin = pythagerous(rectA, rectB);
			
			for(int j = 0; j < rectangles.length; j++) {
				rectB = rectangles[j];
				if( rectA != rectB) {
					double dist = pythagerous(rectA, rectB);
					if(dist < relativeMin) {
						relativeMin = dist;
					}
				}
			}
			if(relativeMin > minDistance) {
				minDistance = relativeMin;
				outlierRect = rectA;
			}
		}
		return outlierRect;
	}
	
	/*
	 * Uses pythagerous theorem to calculate the distance between the center of two rectangles
	 * for use in discovering outliers in terms of distance.
	 */
	private double pythagerous(Rectangle rectA, Rectangle rectB) {
		double xCoordA = rectA.getX();
		double xCoordB = rectB.getX();
		double yCoordA = rectA.getY();
		double yCoordB = rectB.getY();
		
		double cntXA = xCoordA + ((rectA.getWidth())/2);
		double cntYA = yCoordA + ((rectA.getHeight())/2);
		double cntXB = xCoordB + ((rectB.getWidth())/2);
		double cntYB = yCoordB + ((rectB.getHeight())/2);
		
		double xDistance = Math.abs(cntXB - cntXA);
		double yDistance = Math.abs(cntYB - cntYA);
		
		double squaredDistance = (xDistance * xDistance) + (yDistance * yDistance);
		double distance = Math.sqrt(squaredDistance);
		return distance;
	}
}

