package datamining;

public interface Datamining {
	
	public double mean(String filename);
	
	public double[] histogram(String filename);
	
	public double correlations(String filename, int k);
}
