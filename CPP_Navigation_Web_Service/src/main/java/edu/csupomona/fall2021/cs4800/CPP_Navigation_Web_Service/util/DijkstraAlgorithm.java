package edu.csupomona.fall2021.cs4800.CPP_Navigation_Web_Service.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * A class that allows the user to find the shortest path from a single node
 * to all other nodes in a weighted, connected, directed graph using Dijkstra's algorithm.
 * @author Payton Perchez
 */
public class DijkstraAlgorithm {

	/**
	 * Responsible for reading the file, prompting the user for a source vertex, and displaying the
	 * shortest path from that source to all other nodes in the graph.
	 * @param args Arguments passed through the command line at runtime.
	 */
	public static void main(String[] args) {
		
		/*
		 * Test file:
		 * 0 1 10 15 20 8
		 * 1 0 6 8 4 3
		 * 10 6 0 5 8 -1
		 * 15 8 5 0 12 -1
		 * 20 4 8 12 0 5
		 * 8 3 -1 -1 5 0
		 */
		String fileName = "input.txt";
		File file = new File(fileName);
		
		//Display an error message if the file cannot be found
		try {
			
			Scanner inputFile = new Scanner(file);
			
			//Display an error message if the file is empty
			if(inputFile.hasNext()) {
				
				//Number of vertices
				int n = 1;
				
				//Get the first row of the matrix
				String currentRow = inputFile.nextLine();
				
				//Count the number of vertices
				for(int index = 0;index < currentRow.length();index++) {
					
					//A space indicates the end of the current weight
					if(currentRow.charAt(index) == ' ') {
						
						n++;
						
					}//end if
					
				}//end for
				
				//If the number of vertices is below 2, the graph is invalid
				if(n < 2) {
					
					System.out.println("Error: Invalid graph size.");
					inputFile.close();
					System.exit(3);
					
				}//end if
				
				int[][] paths = new int[n][n];
				String currentWeight = "";
				int i = 0;
				int j = 0;
				
				//Get all paths from the first row
				for(int index = 0;index < currentRow.length();index++) {
					
					//A space indicates the end of the current weight
					if(currentRow.charAt(index) != ' ') {
						
						currentWeight = currentWeight + currentRow.charAt(index);
						
					}else {
						
						paths[i][j] = Integer.parseInt(currentWeight);
						j++;
						currentWeight = "";
						
					}//end if
					
				}//end for
				
				//Add the last path from the first row
				paths[i][j] = Integer.parseInt(currentWeight);
				
				i++;
				
				//Add the rest of the paths
				while(inputFile.hasNext() && (i < n)) {
					
					j = 0;
					
					//Don't need to get nextLine() since we know there are n paths in a row
					while(j < n) {
						
						paths[i][j] = Integer.parseInt(inputFile.next());
						j++;
						
					}//end while
					
					i++;
					
				}//end while
				
				System.out.println("Please select the source vertex or enter a non-positive integer to exit");
				System.out.print("Available vertices:");
				
				//Print all available vertices
				for(int vertex = 1;vertex <= n;vertex++) {
					
					System.out.print(" " + vertex);
					
				}//end for
				System.out.println();
				
				//Set source vertex
				Scanner keyboard = new Scanner(System.in);
				int sourceVertex = keyboard.nextInt();
				
				//Exit if the user inputs a negative integer
				if(sourceVertex <= 0) {
					
					System.out.println("Exit!");
					inputFile.close();
					keyboard.close();
					System.exit(0);
					
				//Display an error message if the user enter an invalid vertex
				}else if(sourceVertex > n) {
					
					System.out.println("Error: " + sourceVertex + " is larger than the available vertices.");
					inputFile.close();
					keyboard.close();
					System.exit(4);
					
				}//end if
				
				//Initialize final path array
				Edge[] F = new Edge[n-1];
				
				//Find shortest path from source vertex to all others using Dijkstra's algorithm
				dijkstra(n, sourceVertex-1, paths, F);
				
				//Print final paths and their costs
				System.out.println("\nShortest Paths and Cost:");
				for(int index = 0;index < F.length;index++) {
					
					System.out.println("\t<" + (F[index].getRow() + 1) + ", " + (F[index].getColumn() + 1) + ">: " + F[index].getWeight());
					
				}//end for
				
				System.out.println("Done!");
				keyboard.close();
				
			}else {
				
				System.out.println("Error: File is empty!");
				inputFile.close();
				System.exit(2);
				
			}//end if
			
			inputFile.close();
			
		}catch(FileNotFoundException e) {
			
			System.out.println("Error: " + fileName + " not found!");
			System.exit(1);
			
		}//end try-catch
		
	}//end main
	
	/**
	 * Finds the shortest path from s specified source node to all other vertices in a
	 * graph using Dijkstra's algorithm.
	 * @param n The number of vertices.
	 * @param v The source vertex.
	 * @param W The weights of all paths.
	 * @param F The final set of paths from the source vertex to all other vertices.
	 */
	public static void dijkstra(int n, int v, int[][] W, Edge[] F) {
		
		int vnear = v;
		int[] touch = new int[n];
		int[] length = new int[n];
		
		//Initialize touch and length arrays
		for(int index = 0;index < n;index++) {
			
			//The source vertex is already visited so skip it
			if(index != v) {
				
				//Set the source vertex as nearest to all other vertices
				touch[index] = v;
				length[index] = W[v][index];
				
			}else {
				
				touch[index] = -1;
				length[index] = 0;
				
			}//end if
			
		}//end for
		
		//Print the initialization step
		System.out.println("Initialization:");
		System.out.print("\tTouch = ");
		print(touch, -1);
		System.out.print("\tLength = ");
		print(length, -2);
		
		int min;
		int step = 1;
		
		//Add n-1 paths to F
		for(int addedEdges = 0;addedEdges < (n - 1);addedEdges++) {
			
			//Set min to infinity
			min = -1;
			
			//Get the shortest path currently available to the source vertex 
			for(int index = 0;index < n;index++) {
				
				//Compare finite paths only
				if(length[index] > 0) {
					
					//Search for shortest path available to source vertex
					if(min > 0) {
						
						if(length[index] < min) {
							
							min = length[index];
							vnear = index;
							
						}//end if
						
					//If min is infinity, no comparison is needed
					}else {
						
						min = length[index];
						vnear = index;
						
					}//end if
					
				}//end if
				
			}//end for
			
			F[addedEdges] = new Edge(min, touch[vnear], vnear);
			
			//Update paths if shorter ones exist from vnear
			for(int index = 0;index < n;index++) {
				
				//Update length if it's cheaper to go through vnear instead
				if((length[index] > 0) && (W[vnear][index] > 0) && ((length[vnear] + W[vnear][index]) < length[index])) {
					
					length[index] = length[vnear] + W[vnear][index];
					touch[index] = vnear;
					
				//Replace infinity with any finite path
				}else if(length[index] == -1) {
					
					if(W[vnear][index] > 0) {
						
						length[index] = length[index] = length[vnear] + W[vnear][index];
						
					}//end if
					
				}//end if
				
			}//end for
			
			//Set vnear to visited
			length[vnear] = 0;
			
			//Print the result of this step
			System.out.println();
			System.out.println("Step " + step + ":");
			System.out.print("\tTouch = ");
			print(touch, -1);
			System.out.print("\tLength = ");
			print(length, -2);
			step++;
			
		}//end for
		
	}//end dijkstra
	
	/**
	 * Prints the specified array.
	 * @param array The specified array.
	 * @param lowest The cutoff at which elements will not be incremented by 1, will not increment if equal to -2.
	 */
	private static void print(int[] array, int lowest) {
		
		//Note: this only works for a relatively small number of elements.(nothing crazy like 1,000,000)
		int[] printedArray = new int[array.length];
		
		//Copy the array and increment values above cutoff if it exists
		boolean cutoffExists = (lowest != -2);
		if(cutoffExists) {
			
			for(int index = 0;index < array.length;index++) {
				
				if(array[index] > lowest) {
					
					printedArray[index] = array[index] + 1;
					
				}else {
					
					printedArray[index] = array[index];
					
				}//end if
				
			}//end for
			
		}else {
			
			printedArray = array;
			
		}//end if
		
		//Print all elements
		System.out.print("{" + printedArray[0]);
		
		for(int index = 1;index < printedArray.length;index++) {
			
			System.out.print(", " + printedArray[index]);
			
		}//end for
		
		System.out.println("}");
		
	}//end print
	
}//end DijkstraAlgorithm
