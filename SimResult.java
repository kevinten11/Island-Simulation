import java.util.ArrayList;


public class SimResult {
	public ArrayList<ArrayList<Double>> sizes;
	public ArrayList<ArrayList<Double>> colors;
	public ArrayList<ArrayList<Double>> energyDeadSizes;
	public ArrayList<ArrayList<Double>> ageDeadSizes;
	public String name;
	
	public SimResult(String name, ArrayList<ArrayList<Double>> sizes, ArrayList<ArrayList<Double>> colors,
			ArrayList<ArrayList<Double>> energyDeadSizes, ArrayList<ArrayList<Double>> ageDeadSizes)
	{
		this.name = name;
		this.sizes = sizes;
		this.colors = colors;
		this.energyDeadSizes = energyDeadSizes;
		this.ageDeadSizes = ageDeadSizes;
	}
}
