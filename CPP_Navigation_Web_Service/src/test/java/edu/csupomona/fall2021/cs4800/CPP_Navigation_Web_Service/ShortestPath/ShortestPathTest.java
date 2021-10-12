package edu.csupomona.fall2021.cs4800.CPP_Navigation_Web_Service.ShortestPath;

import org.junit.*;

public class ShortestPathTest {
	@Before
	public void setup() {
		System.out.println("Testing method..");
	}
	
	@Test
	public void testShortestPath(){
		int graph[][] = new int[][] { { 0, 4, 0, 0, 0, 0, 0, 8, 0 },
									{ 4, 0, 8, 0, 0, 0, 0, 11, 0 },
									{ 0, 8, 0, 7, 0, 4, 0, 0, 2 },
									{ 0, 0, 7, 0, 9, 14, 0, 0, 0 },
									{ 0, 0, 0, 9, 0, 10, 0, 0, 0 },
									{ 0, 0, 4, 14, 10, 0, 2, 0, 0 },
									{ 0, 0, 0, 0, 0, 2, 0, 1, 6 },
									{ 8, 11, 0, 0, 0, 0, 1, 0, 7 },
									{ 0, 0, 2, 0, 0, 0, 6, 7, 0 } };
		int answers[] = new int[] {0, 4, 12, 19, 21, 11, 9, 8, 14};
		ShortestPath t = new ShortestPath();
		int[] actual = new int[9];
		actual = t.dijkstra(graph, 0);
		Assert.assertArrayEquals(answers, actual);
	}

}
