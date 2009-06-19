import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.filters.FilterUtils;
import edu.uci.ics.jung.algorithms.metrics.Metrics;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.algorithms.shortestpath.DistanceStatistics;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.io.PajekNetReader;

/**
 * A class that shows the minimal work necessary to load and visualize a graph.
 */
public class NetworkAnalyzer 
{
			
	private String dataset;
	
	Factory<MyVertex> vertexFactory = new Factory<MyVertex>() {
		int n = 0;
        public MyVertex create() {
        	return new MyVertex(n++, "a"); 
        }
    };
    Factory<MyEdge> edgeFactory = new Factory<MyEdge>()  {
    	int n = 0;
    	public MyEdge create() { 
    		return new MyEdge(n++); 
    	}
    };
	
	/**
	 *  MyVertex and MyEdge classes
	 */
	private class MyVertex {
		
	    private int id; // good coding practice would have this as private
	    private String label;

	    public MyVertex(int id){
	    	this.id = id;
	    }
	    	    
	    public MyVertex(int id, String label) {    	
	        this.id = id;
	        this.label = label;
	    }
	    
	    public String toString() { // Always a good idea for debuging
	        return "V"+id;          // JUNG2 makes good use of these.
	    }
	    
	    public String getLabel(){
	    	return this.label;
	    }
	}
	
	private class MyEdge {
	    
		private Number weight;    // should be private for good practice
	    private int id;
	    	    
	    public MyEdge(int id) {	    	
	        this.id = id; // This is defined in the outer class.
	    }
	    
	    public MyEdge(int id, double weight) {	    	
	        this.id = id; // This is defined in the outer class.
	        this.weight = weight;
	    }
	    
	    public String toString() { // Always good for debugging
	        return "E"+id;
	    }
	    
	    public void setWeight(Number weight){
	    	this.weight = weight;
	    }
	    
	    public Number getWeight(){
	    	return this.weight;
	    }
	}

	
	/**
	 * Basic constructor for SwissKnife class 
	 */
	public NetworkAnalyzer(){
		this.dataset = "test.net";
	}
	
	/**
	 * Constructor for SwissKnife class
	 */
	public NetworkAnalyzer(String dataset){
		
		this.dataset = dataset;
		
	}	
		
	public void runTest() throws IOException {   
    	
        DirectedGraph<MyVertex, MyEdge> graph = getGraph(dataset);
        
        int vertexCount = graph.getVertexCount();
        int edgeCount = graph.getEdgeCount(); 
        
        System.out.println("Vertex count: " + vertexCount);
        System.out.println("Edge count: " + edgeCount);
        
        double[] meandegree = getMeanDegree(graph); // Calculate and print mean indegree and outdegree
        
        double meanindegree = meandegree[0];
        double meanoutdegree = meandegree[1];
     
        getVarianceDegree(graph, meanindegree, meanoutdegree); // Calculate and print variance indegree and variance outdegree
               
    	getNetworkDensity(graph); // Calculate and print network density
    	
    	//getMeanVertexVertexDistance(graph); // Calculate mean vertex-vertex distance NOT WORKING
    	//getReciprocatedMeanVertexVertexDistance(graph); // Calculate mean vertex-vertex distance NOT WORKING
    	
    	getClusteringCoefficient(graph); // Calculate clustering coefficient
       
    }
	
	
    /**
     * Generates a graph
     * @param dataset path to network file
     * @return A sample undirected graph
     */
    public DirectedGraph<MyVertex, MyEdge> getGraph(String dataset) throws IOException {	
    	
    	System.out.println("Importing " + dataset + " network");
    	long start = System.currentTimeMillis();


        PajekNetReader<DirectedGraph<MyVertex, MyEdge>, MyVertex, MyEdge> pnr = new PajekNetReader<DirectedGraph<MyVertex, MyEdge>, MyVertex, MyEdge>(vertexFactory, edgeFactory);
        DirectedGraph<MyVertex, MyEdge> g = new DirectedSparseGraph<MyVertex, MyEdge>(); 
        pnr.load(dataset, g);
        
        for(MyEdge edge : g.getEdges()){
        	edge.setWeight(pnr.getEdgeWeightTransformer().transform(edge));
        }
        
    	long end = System.currentTimeMillis();
        System.out.println("Execution time was "+(end-start)+" ms.\n");
        
        return g;
        
    }
    
    
	/**
	 *  Calculate and print network density1
	 *  @param g the considered graph
	 */
    public double getNetworkDensity(DirectedGraph<MyVertex, MyEdge> g){

        long start = System.currentTimeMillis();
        
        int vertexCount = g.getVertexCount();
        int edgeCount = g.getEdgeCount();
        
        double networkdensity = (double) edgeCount / (double) vertexCount / (double) ( vertexCount - 1 );

    	long end = System.currentTimeMillis();

        System.out.println("\nNetwork density: " + networkdensity);
        System.out.println("Execution time was "+(end-start)+" ms.");
        
        return networkdensity;
    }
    
    /**
     *  Calculate and print variance indegree and variance outdegree
     *  @param g the considered graph
     *  @param meanindegree
     *  @param meanoutdegree
     */
    public void getVarianceDegree(DirectedGraph<MyVertex, MyEdge> g, double meanindegree, double meanoutdegree){
    
    	double invertexvariance = 0;
    	double outvertexvariance = 0;
    	double totalinvariance = 0;
    	double totaloutvariance = 0;
    	
    	long start = System.currentTimeMillis();
    	
    	for(MyVertex v : g.getVertices()) {
    		invertexvariance = g.getInEdges(v).size() - meanindegree;
    		outvertexvariance = g.getOutEdges(v).size() - meanoutdegree;
    		
  	  		totalinvariance += invertexvariance * invertexvariance;
  	  		totaloutvariance += outvertexvariance * outvertexvariance;
    	}
    	
    	int vertexCount = g.getVertexCount();
           
    	double varianceindegree = 1.0 *  (totalinvariance / vertexCount);
    	double varianceoutdegree = 1.0 * (totaloutvariance / vertexCount);

    	long end = System.currentTimeMillis();

    	System.out.println("\nVariance in-degree: " + varianceindegree);
    	System.out.println("Variance out-degree: " + varianceoutdegree);
    	System.out.println("Execution time was "+(end-start)+" ms.");
    }
 
    
    /**
     *  Calculate and print mean indegree and outdegree
     *  @param g the considered graph
     */
    public double[] getMeanDegree(DirectedGraph<MyVertex, MyEdge> g){
        
    	double[] meandegree = new double[2];
    	int outdegree = 0;
    	int indegree = 0;
    	
        long start = System.currentTimeMillis();
                         
        for(MyVertex v : g.getVertices()) {    	        	
      	  	indegree += g.getInEdges(v).size();
      	  	outdegree += g.getOutEdges(v).size();
        }
               
        int vertexCount = g.getVertexCount();
        
        double meanindegree = 1.0 * indegree / vertexCount;
        double meanoutdegree = 1.0 * outdegree / vertexCount;

    	long end = System.currentTimeMillis();

        System.out.println("\nMean in-degree: " + meanindegree);
        System.out.println("Mean out-degree: " + meanoutdegree);
        System.out.println("Execution time was "+(end-start)+" ms.");
        
        meandegree[0] = meanindegree;
        meandegree[1] = meanoutdegree;
        
        return meandegree;
    }
    
    
    /**
     * Calculate the mean vertex-vertex distance in a graph (strongly connected)
     * @param g the considered graph
     */
    /*public void getMeanVertexVertexDistance(DirectedGraph<MyVertex,MyEdge> g){

    	double cluster_distance = 0;
    	double cluster_mean_distance = 0;
    	double total_distance = 0;
		int t = 0; // total number of analyzed clusters
		int cluster_vertices = 0;
    	    	
        long start = System.currentTimeMillis();
        
        Transformer<MyEdge, Number> transfDijkstra = new Transformer<MyEdge, Number>() {
        	public Number transform(MyEdge e){
        		return e.getWeight();
        	}
        };
        
        // StronglyConnectedComponent is NOT present
        StronglyConnectedComponents<MyVertex, MyEdge> scc = new StronglyConnectedComponents<MyVertex, MyEdge>();
        Set<Set<MyVertex>> cluster_set = scc.transform(g);
        Collection<DirectedGraph<MyVertex,MyEdge>> clusters = FilterUtils.createAllInducedSubgraphs(cluster_set, g);
            	
    	for(DirectedGraph<MyVertex, MyEdge> cluster : clusters) {
    		
    		cluster_distance = 0;
    		cluster_mean_distance = 0;
    		cluster_vertices = cluster.getVertexCount();
    		
    		if(cluster_vertices > 1){
    			  		                 			
    			DijkstraShortestPath<MyVertex, MyEdge> dsp = new DijkstraShortestPath<MyVertex, MyEdge>(cluster, transfDijkstra);
    			Transformer<MyVertex, Double> distances = DistanceStatistics.averageDistances(cluster, dsp);
    		
    			for(MyVertex v : cluster.getVertices()) {
    				cluster_distance += distances.transform(v);
    			}

    			cluster_mean_distance += cluster_distance / cluster_vertices;
    			//System.out.println("\nCluster size: " + cluster_vertices + " - Mean cluster vertex vertex distance: " + cluster_mean_distance);
    			total_distance += cluster_mean_distance;
    			t++;
    		}
    		
       	}
  	 
    	double meanvvdistance = total_distance / t;
    	
    	long end = System.currentTimeMillis();
    	
    	System.out.println("\nMean vertex vertex distance: " + meanvvdistance);
    	//System.out.println("Total cluster number: " + clusters.size());
        System.out.println("Execution time was "+(end-start)+" ms.");
        
    }*/
    
    
    /**
     * Calculate the mean vertex-vertex distance in a graph (strongly connected)
     * @param g the considered graph
     */
    /*public void getReciprocatedMeanVertexVertexDistance(DirectedGraph<MyVertex,MyEdge> g){

    	double total_distance = 0;
    	double mean_distance = 0;
    	    	
        long start = System.currentTimeMillis();
        
        Transformer<MyEdge, Number> transfDijkstra = new Transformer<MyEdge, Number>() {
        	public Number transform(MyEdge e){
        		return e.getWeight();
        	}
        };
        
    	DijkstraShortestPath<MyVertex, MyEdge> dsp = new DijkstraShortestPath<MyVertex, MyEdge>(g, transfDijkstra);
    	Transformer<MyVertex, Double> distances = DistanceStatistics.averageDistances(g, dsp);
    	    	  	
    	for(MyVertex v : g.getVertices()) {
    		if(distances.transform(v) != 0){
    			total_distance += ( 1 / distances.transform(v) );
    		}
    	}
    	
    	mean_distance = total_distance / ( ( g.getVertexCount() / 2 ) * ( g.getVertexCount() + 1 ) );
    	System.out.println("\nReciprocated mean vertex vertex distance: " + mean_distance);   	    	
    	
    	long end = System.currentTimeMillis();
    	
        System.out.println("Execution time was "+(end-start)+" ms.");
        
    }*/
    
    /**
     *  Calculate the clustering coefficient of the graph
     * @param g the considered graph 
     */
    public void getClusteringCoefficient(DirectedGraph<MyVertex,MyEdge> g){
    	    	
    	double clustering_coefficient = 0;
    	double ccsum = 0;
        long start = System.currentTimeMillis();
        
        double ccvertex = 0;
           	
    	Map<MyVertex, Double> ccmap = Metrics.clusteringCoefficients(g);
    	
    	for(MyVertex v : g.getVertices()) {
    		
    		ccvertex = ccmap.get(v); // Getting cloustering coefficient for vertex v 
    		
    		if(ccvertex == 1.0) ccvertex = 0; // for vertices with coefficient = 1 we puth a = 0
    		
    		ccsum += ccvertex;
    	}
    	
    	clustering_coefficient = 1.0 * ccsum / g.getVertexCount();
    	
    	System.out.println("\nClustering coefficient: " + clustering_coefficient);   	    	
    	
    	long end = System.currentTimeMillis();
    	
        System.out.println("Execution time was "+(end-start)+" ms.");
    	
    }
    
}
