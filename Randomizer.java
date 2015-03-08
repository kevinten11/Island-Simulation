
public class Randomizer 
{
	
	public static double getRandomNormal(double mean, double sd)
	{	
		double u1 = Simulator.generator.nextDouble();
		double u2 = Simulator.generator.nextDouble();
		double normal = Math.sqrt(-2 * Math.log(u1)) * Math.cos(2 * Math.PI * u2);
		
		return (normal * sd) + mean; 
	}
	
	public static double getRandomEven(double min, double max)
	{
		return (Simulator.generator.nextDouble() * (max - min)) + min;
	}
}
