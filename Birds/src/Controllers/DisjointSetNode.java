package Controllers;

import Controllers.DisjointSetNode;
import Controllers.Pixel;

//////////////////////////////////////////////////////////////////////////
/// Class that constructs disjoint set nodes with pixels as data       ///
/// and size and height variables for use in the union find operations ///
//////////////////////////////////////////////////////////////////////////
public class DisjointSetNode{
	private DisjointSetNode parent = null;
	private Pixel data;
	private int size = 1;
	private int height = 1;
	
	public DisjointSetNode(Pixel data) {
		this.data = data;
	}

	public DisjointSetNode getParent() {
		return parent;
	}

	public void setParent(DisjointSetNode parent) {
		this.parent = parent;
	}

	public Pixel getData() {
		return data;
	}

	public void setData(Pixel data) {
		this.data = data;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	
	
	
}

