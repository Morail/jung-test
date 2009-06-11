import java.io.IOException;

public class Tester {

	/**
	 * @param args
	 */
	private static String dataset = "resources/datasets/graphCurrent.net";
	 
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
