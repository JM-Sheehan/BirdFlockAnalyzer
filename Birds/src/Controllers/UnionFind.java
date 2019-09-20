package Controllers;

import Controllers.DisjointSetNode;
import javafx.scene.shape.Rectangle;

public class UnionFind {

	public UnionFind() {
	}
	
	//Recursive version of find.
	public DisjointSetNode find(DisjointSetNode node){

		if(node.getParent() != null) {
			node = node.getParent();
			return find(node);
		}
		else {
			return node;
		}
	}
	
	//Union by size of the roots of two disjoint sets given the two disjoint sets.
	public void unionBySize(DisjointSetNode setA, DisjointSetNode setB){
		DisjointSetNode rootA;
		rootA = find(setA);//finds the root of setA
		DisjointSetNode rootB = find(setB);//finds the root of setB.
		if(rootA != rootB) {//Ensures the roots are different.
			DisjointSetNode largerRoot = rootA.getSize()>=rootB.getSize() ? rootA:rootB;//Compares the two roots by size.
			DisjointSetNode smallerRoot = largerRoot == rootA ? rootB:rootA;
		
			smallerRoot.setParent(largerRoot);//Sets the parent of the smaller root to the larger root.
		
			if(largerRoot.getHeight() == smallerRoot.getHeight()) {
				int oldHeight = largerRoot.getHeight();
				int newHeight = oldHeight + 1;
				largerRoot.setHeight(newHeight);
			}
			int largerSize = largerRoot.getSize();
			int smallerSize = smallerRoot.getSize();
			largerRoot.setSize(largerSize + smallerSize);
		}	
	}
	
	/*
	 * Performs the quick sort algorithm on the rectangle array
	 * according to the y-coordinate of the rectangles.
	 * Used for numbering the rectangles.
	 */
	public Rectangle[] quickSortYVal(Rectangle[] inputArray, int start, int end) {
		if(start<end) {
			int pivot = partitionYVal(inputArray, start, end);
			quickSortYVal(inputArray, start, pivot-1);
			quickSortYVal(inputArray, pivot+1, end);
		}
		return inputArray;
	}
	
	/*
	 * Used to find the pivot point of the inputArray for sorting according 
	 * the y coordinate of the rectangles.
	 */
	public static int partitionYVal(Rectangle[] inputArray, int start, int end) {
		Rectangle pivotVal = inputArray[end];
		int i = start-1;
		Rectangle temp;
		for(int j = start; j<end;j++) {
			if(inputArray[j].getY()<pivotVal.getY()) {
				i++;
				temp = inputArray[i];
				inputArray[i] = inputArray[j];
				inputArray[j] = temp;
			}
			else if(inputArray[j].getY() == pivotVal.getY()) {
				if(inputArray[j].getX()<=pivotVal.getX()) {
					i++;
					temp = inputArray[i];
					inputArray[i] = inputArray[j];
					inputArray[j] = temp;
				}
			}
		}
		temp = inputArray[i+1];
		inputArray[i+1] =  pivotVal;
		inputArray[end] =  temp;
		return i+1;
	}
	
	/*
	 * Performs the quick sort algorithm on the rectangle array
	 * according to the area of the rectangles.
	 * Used for outlier management and quartiles.
	 */
	public Rectangle[] quickSortArea(Rectangle[] inputArray, int start, int end) {
		if(start<end) {
			int pivot = partitionArea(inputArray, start, end);
			quickSortArea(inputArray, start, pivot-1);
			quickSortArea(inputArray, pivot+1, end);
		}
		return inputArray;
	}
	
	/*
	 * Used to find the pivot point of the inputArray for sorting by area.
	 */
	public static int partitionArea(Rectangle[] inputArray, int start, int end) {
		Rectangle pivotVal = inputArray[end];
		int i = start-1;
		Rectangle temp;
		for(int j = start; j<end;j++) {
			double width = inputArray[j].getWidth();
			double height = inputArray[j].getHeight();
			double area = width * height;
			
			double pivotWidth = pivotVal.getWidth();
			double pivotHeight = pivotVal.getHeight();
			double pivotArea = pivotWidth * pivotHeight;
			
			if(area<pivotArea) {
				i++;
				temp = inputArray[i];
				inputArray[i] = inputArray[j];
				inputArray[j] = temp;
			}
		}
		temp = inputArray[i+1];
		inputArray[i+1] =  pivotVal;
		inputArray[end] =  temp;
		return i+1;
	}

}
