package eu.fbk.sonet.snatool;

import java.io.IOException;

public class Tester {

	/**
	 * @param args
	 */
	//private static String dataset = "resources/datasets/eml/2009-05-01/graphCurrent.net"; // Emiliana
	private static String dataset = "resources/datasets/la/2008-07-12/graphCurrent.net"; // Latina
	//private static String dataset = "resources/datasets/de/2008-10-11/graphCurrent.net"; // Tedesca
	//private static String dataset = "resources/datasets/vec/2009-05-05/graphCurrent.net"; // Veneta
	//private static String dataset = "/sra0/sra/setti/Desktop/twittergraph.net";
	 
	public static void main(String[] args) throws IOException {
		
    	long start = 0;
    	long end = 0;
    	
        start = System.currentTimeMillis(); // Print vertexes and edges count
        
		SwissKnife swiss = new SwissKnife(dataset);
		
		swiss.runTest();
		
        end = System.currentTimeMillis();
        
        System.out.println("\nTotal operation execution time was "+(end-start)+" ms.");

	}

}
