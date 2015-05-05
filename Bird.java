import java.util.ArrayList;


public class Bird 
{
	//  0.75 = 75% of the time memory will be used
	public final double MEMORY_USE_RATIO = 1.0;
	
	//beak size of baby sd variable or constant? = 
	public final int MAX_ENERGY = 10;
	public final int SEARCH_COST = 1;
	public final double VARIANCE = 0.2; // for reproduction variance on beakSize and prefMateSize
	
	// for initial pop
	public final double beakMean = 5.0;
	public final double colorMean = 5.0;
	public final double beakStandardDev = 0.5; // initial population generation
	public final double colorStandardDev= 0.5;
	
	public final double SEX_RATIO = .5;
	public Boolean isMale;
	public Bird father;
	public Bird mother;
	public long id;
	public int age;
	public double energy;
	public int x;
	public int y;
	public int matings = 0;
	public double beakSize;
	public double color;
	public int birthYear;
	public int deathYear;
	public ArrayList<Bird> children;
	
	public final int MEMORY_SIZE = 50;
    public ArrayList<Double> memory = new ArrayList<Double>();

	
	public Bird(int _x, int _y, long _id)
	{
		age = 0;
		energy = 0;
		isMale = false;
		if (Math.random() > SEX_RATIO)
		{
			isMale = true;
		}
		x = _x;
		y = _y;
		id = _id;
		beakSize = Randomizer.getRandomNormal(beakMean, beakStandardDev);
		color = Randomizer.getRandomNormal(colorMean, colorStandardDev);
		Simulator.allBirds.add(this);
		children = new ArrayList<Bird>();
	}	
	
	public Bird(Bird _mother, Bird _father, int _id)
	{
		age = 0;
		energy = 0;
		isMale = false;
		if (Math.random() > SEX_RATIO)
		{
			isMale = true;
		}
		x = _mother.x;
		y = _mother.y;
		id = _id;
		mother = _mother;
		_mother.children.add(this);
		father = _father;
		_father.children.add(this);
		double aveBeakSize = (_mother.beakSize + _father.beakSize) / 2;
		double range = Math.abs(_mother.beakSize - _father.beakSize);
		
		// calculate size of offspring
		beakSize = Randomizer.getRandomNormal(aveBeakSize, Math.max(range/4, VARIANCE));
		color = Randomizer.getRandomNormal(_mother.color, VARIANCE);
		
		_father.matings++;
		Simulator.allBirds.add(this);
		children = new ArrayList<Bird>();
	}
	
	public void incurSearchCost()
	{
		energy -= SEARCH_COST;
	}
	
	public void eatSeed(Seed seed)
	{
		energy = Math.min(energy + seed.energy, MAX_ENERGY);
	}

	public void seeMateEvent(Bird father, Bird mother)
	{
		if (mother.age > age)
		{
			int ageGap = mother.age - age;
			if (Math.random() * (ageGap + 1) < ageGap)
			{
				memory.add(0, father.color);
				
				// if memory too large, get rid of last memory
				if (memory.size() >= MEMORY_SIZE)
				{
					memory.remove(MEMORY_SIZE);
				}
			}
		}
	}
	
	/**
	 * gets the preferred size based on the memory type
	 * @return size
	 */
	public double getMemoryColor()
	{	
		if (Math.random() > MEMORY_USE_RATIO)
		{
			return -1;
		}
		
		int index = 0;
		switch (Simulator.memoryType)
		{
			// if random get a random from memory
			case RANDOM:
				index = (int)Randomizer.getRandomEven(0, memory.size());
				break;
				
			case WEIGHTED:
				// quadratic weighting method
				double scaledUp = Randomizer.getRandomEven(0, Math.pow(memory.size(), 2));
				index = memory.size() - 1 - (int)(Math.pow(scaledUp, 0.5));
				break;
					
			case MOST_RECENT:
				index = 0;
				break;
			
			case NONE:
				return -1;
		}
		
		// if no memory, do the default behavior
		if (memory.size() < 1)
		{
			return -1;
		}
		
		Island.memoryMatings++;
		return memory.get(index);
	}
}
