
public class Seed 
{
	public int x;
	public int y;
	public double energy;
	public double size;
	
	// for bimodal
	private final double HIGH_MEAN = 7.25;
	private final double LOW_MEAN = 2.75;
	private final double BIMODAL_SD = 1.0;
	
	// for normal (sd for both)
	private final double MID_MEAN = 5.0;
	private final double NORMAL_SD = 2.0;
	
	// for uniform
	private final double MAX_SIZE = 9.0;
	private final double MIN_SIZE = 1.0;
	
	public Seed(double _energy)
	{
		energy = _energy;
		
		// pick seed size based on simulation type
		switch (Simulator.seedType)
		{
			case BIMODAL:
				generateBimodalSize();
				break;
			
			case NORMAL:
				generateNormalSize();
				break;
			
			case UNIFORM:
				generateUniformSize();
				break;
		}
	}
	
	private void generateUniformSize()
	{
		//size =  Math.random() * maxSize + offset;
		size = Randomizer.getRandomEven(MIN_SIZE, MAX_SIZE);
	}
	
	private void generateBimodalSize()
	{
		if (Math.random() > 0.5)
		{
			size = Randomizer.getRandomNormal(HIGH_MEAN, BIMODAL_SD);
		}
		else
		{
			size = Randomizer.getRandomNormal(LOW_MEAN, BIMODAL_SD);
		}
	}
	
	private void generateNormalSize()
	{
		size = Randomizer.getRandomNormal(MID_MEAN, NORMAL_SD);
	}
}
