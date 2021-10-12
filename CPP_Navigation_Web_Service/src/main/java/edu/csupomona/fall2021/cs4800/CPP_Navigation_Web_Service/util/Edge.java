package edu.csupomona.fall2021.cs4800.CPP_Navigation_Web_Service.util;

/**
 * An implementation of an edge in a weighted graph.
 * @author Payton Perchez
 */
public class Edge {
	
	private int weight;
	private int row;		//Vertex 1
	private int column;		//Vertex 2
	
	/**
	 * Creates a new edge with the specified weight and connected vertices.
	 * @param edgeWeight The weight of the edge.
	 * @param v1 The first vertex connected to the edge.
	 * @param v2 The first vertex connected to the edge.
	 */
	public Edge(int edgeWeight, int v1, int v2) {
		
		weight = edgeWeight;
		row = v1;
		column = v2;
		
	}//end constructor
	
	/**
	 * Returns the weight of the edge.
	 * @return The weight of the edge.
	 */
	public int getWeight() {
		
		return weight;
		
	}//end getWeight
	
	/**
	 * Returns the first vertex connected to the edge.
	 * @return The first vertex connected to the edge.
	 */
	public int getRow() {
		
		return row;
		
	}//end getRow
	
	/**
	 * Returns the second vertex connected to the edge.
	 * @return The second vertex connected to the edge.
	 */
	public int getColumn() {
		
		return column;
		
	}//end getColumn
	
}//end Edge
