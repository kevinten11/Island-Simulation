
import java.util.ArrayList;
import java.util.List;

/**
 * Island for model
 * @author Kevin
 *
 */
public class Island 
{
	public ArrayList<Bird> birds = new ArrayList<Bird>();
	
	public ArrayList<Bird> newBirds = new ArrayList<Bird>();
	public ArrayList<Seed> seeds = new ArrayList<Seed>();

	public Quadrant[][] grid;
	public int nextBirdId;
	public int size;
	
	public final int POP_SIZE = 100;	
	public final int ISLAND_SIZE = 100;
	public final int MATE_RADIUS = 100;
	public final int MATE_INFLUENCE_RADIUS = 5;
	public final int MOVE_RADIUS = 10;
	public final int SEARCH_RADIUS = 5;
	public final int MAX_SEED_ENERGY = 5;
	public final int DEATH_AGE = 4;
	public final int SEARCH_DAYS = 30;
	public final double MATING_DIFF_THRESHOLD = 1.0;
	public final int TOTAL_ENERGY = ISLAND_SIZE * MAX_SEED_ENERGY * 64;
	public final int MAX_MATINGS_PER_SEASON = 100;
	public final double EDIBLE_DIFF = 1.0;
	
	public static int matings = 0;
	public static int memoryMatings = 0;
	
	/**
	 * Generates a square island
	 * @param _size   width and length of the island
	 * @param _popSize   number of initial birds
	 */
	public Island()
	{
		nextBirdId = POP_SIZE;
		size = ISLAND_SIZE;
		
		grid = new Quadrant[ISLAND_SIZE][ISLAND_SIZE];
		
		// initialize quadrants
		for (int i = 0; i < ISLAND_SIZE; i++)
		{
			for (int j = 0; j < ISLAND_SIZE; j++)
			{
				grid[i][j] = new Quadrant();
			}
		}
		
		generateBirds();
	}
	
	public class Quadrant
	{
		public ArrayList<Bird> birds;
		public ArrayList<Seed> seeds;
		
		public Quadrant()
		{
			birds = new ArrayList<Bird>();
			seeds = new ArrayList<Seed>();
		}
	}
    
	/**
	 * generates initial birds and randomly places them on the island
	 */
	public void generateBirds()
	{		
		for (int i = 0; i < POP_SIZE; i++)
		{
			int x = (int) (Math.random() * size);
			int y = (int) (Math.random() * size);
			Bird bird = new Bird(x, y, i);
			addBird(bird);
		}
	}
	
	/**
	 * Generates seeds
	 */
	public void generateSeeds()
	{
		while (!seeds.isEmpty())
		{
			removeSeed(seeds.get(seeds.size() - 1));
		}
		
		int totalEnergy =  TOTAL_ENERGY;
		while (totalEnergy > 0)
		{
			double seedEnergy = Randomizer.getRandomEven(1, MAX_SEED_ENERGY);
			
			seeds.add(new Seed(seedEnergy));
			totalEnergy -= seedEnergy;
		}
		
		// distribute seeds
		spreadSeedsRandom();
	}

	/**
	 * Randomly distributes seeds
	 */
	public void spreadSeedsRandom()
	{
		for (Seed seed : seeds)
		{
			seed.x = (int)(Math.random() * size);
			seed.y = (int)(Math.random() * size);
			grid[seed.x][seed.y].seeds.add(seed);
		}
	}
	
	/**
	 * Prints out the birds in each quadrant
	 */
	public void checkQuadrantSeeds()
	{	
		int totalEnergy = 0;
		for (int i = 0; i < size; i++)
		{
			for (int j = 0; j < size; j++)
			{				
				System.out.print("Quadrant " + i + "," + j + " has seeds: ");
				for (Seed x: grid[i][j].seeds)
				{
					System.out.print(x.energy + " ");
					totalEnergy += x.energy;
				}
				System.out.print("\n");
			}
		}
		System.out.println("Total: " + totalEnergy);
	}
	
	/**
	 * Prints out where each bird is
	 */
	public void checkBirds()
	{
		for (Bird x : birds)
		{
			//totalEnergy += x.energy;
			System.out.println("Bird ID: " + x.id + " is at " + x.x + "," + x.y + " and is " + x.age);
		}
		System.out.println("Total: " + birds.size()+ "\n");
	}

	/**
	 * Randomly kills 20% of the birds
	 */
	public void doDeaths()
	{
		List<Bird> toRemove = new ArrayList<Bird>();
		for (Bird x: birds)
		{
			if (Math.random() > 0.8)
			{
				toRemove.add(x);
			}
		}
		
		for (Bird x : toRemove)
		{
			removeBird(x);
		}
	}
	
	/**
	 * removes @param bird from the island and grid
	 * @param bird to be removed
	 */
	public void removeBird(Bird bird)
	{
		grid[bird.x][bird.y].birds.remove(bird);
		birds.remove(bird);
	}
	
	/**
	 * removes @param seed from the island and grid
	 * @param seed to be removed
	 */
	public void removeSeed(Seed seed)
	{
		grid[seed.x][seed.y].seeds.remove(seed);
		seeds.remove(seed);
	}

	/**
	 * adds @param bird to the island and grid
	 * @param bird to be added
	 */
	public void addBird(Bird bird)
	{
		grid[bird.x][bird.y].birds.add(bird);
		birds.add(bird);
	}
	
	/**
	 * Calls findAndMate on each female bird
	 */
	public void doMating()
	{
		newBirds.clear();
		ArrayList<Bird> females = new ArrayList<Bird>();
		ArrayList<Bird> males = new ArrayList<Bird>();
		for (Bird bird: birds)
		{
			if (!bird.isMale)
			{
				females.add(bird);
			}
			else
			{
				//reset the matings counter for this season
				bird.matings = 0;
				males.add(bird);
			}
		}
		
		// sort males
		sortBirdsByEnergy(males);
		
		//reset energy
		for (Bird x : birds)
		{
			x.energy = 0;
		}
		
		for (Bird female: females)
		{
			Bird dad = findAndMate(female, males);
			if (dad != null)
			{
				if (dad.matings >= MAX_MATINGS_PER_SEASON)
				{
					males.remove(dad);
				}
				
				if (Simulator.memoryType != Simulator.MemoryType.NONE)
				{
					matingEvent(female, dad);
				}
			}
		}
		
		for (Bird baby: newBirds)
		{
			addBird(baby);
		}
		newBirds.clear();
	}
	
	public void matingEvent(Bird mother, Bird father)
	{
		int x = mother.x;
		int y = mother.y;
		
		// check all grids within radius, excluding out of bounds
		for (int i = Math.max(x - MATE_INFLUENCE_RADIUS, 0); i <= Math.min(x + MATE_INFLUENCE_RADIUS, size - 1); i++)
		{
			for (int j = Math.max(y - MATE_INFLUENCE_RADIUS, 0); j <= Math.min(y + MATE_INFLUENCE_RADIUS, size - 1); j++)
			{
				for (Bird watcher : grid[i][j].birds)
				{
					if (!watcher.isMale && watcher != mother)
					{
						watcher.seeMateEvent(father, mother);
					}
				}
			}
		}
	}
	
	/**
	 * searches for males within mate radius and randomly picks one to mate with
	 * @param mother searching female
	 */
	public Bird findAndMate(Bird mother, ArrayList<Bird> males)
	{
		//int x = mother.x;
		//int y = mother.y;
		
		// find all males within 1 block (including diagonals)
		// ArrayList<Bird> nearbyMales = new ArrayList<Bird>();
		
		/*
		for (int i = x - mateRadius; i <= x + mateRadius; i++)
		{
			if (i < 0 || i >= size)
			{
				continue;
			}
			
			for (int j = y - mateRadius; j <= y + mateRadius; j++)
			{
				if (j < 0 || j >= size)
				{
					continue;
				}
				
				for (Bird found : grid[i][j].birds)
				{
					if (found.isMale)
					{
						nearbyMales.add(found);
					}
				}
			}
		}
		*/
		
		if (!males.isEmpty())
		{
			Bird father = null;
			// chose male (randomly)
			matings++;
					
			double difference = MATING_DIFF_THRESHOLD;
			double preferredSize = mother.getMemorySize();
			
			// if no memory and random behavior, find random mate
			if (Simulator.mateType == Simulator.MateType.RANDOM && preferredSize == -1)
			{
				father = males.get((int)(Math.random() * males.size()));
			}
			else // if there is a memory or not random mating determine mate
			{
				if (preferredSize == -1)
				{
					// to go back to 1 trait system, change this back to .beakSize
					// preferredSize = mother.beakSize;
					preferredSize = mother.prefMateSize;
				}
				Boolean foundMate = false;
				for (Bird potential: males)
				{
					double tempDif = Math.abs(preferredSize - potential.beakSize);
					switch (Simulator.mateType)
					{
						case CHOOSY_PRECISE:
							if (tempDif < difference)
							{
								difference = tempDif;
								father = potential;
							}
							break;
							
						case CHOOSY_CLOSE:
							if (tempDif < difference)
							{
								difference = tempDif;
								father = potential;
								foundMate = true;
							}
							break;
						
						default:
							return null;			
					}
					
					if (foundMate)
					{
						break;
					}
				}
				
				if (difference == MATING_DIFF_THRESHOLD)
				{
					return null;
				}
			}
			
			// create baby
			newBirds.add(new Bird(mother,father, nextBirdId++));	
			return father;
		}
		else return null;
		
	}

	/**
	 * 
	 * @return
	 */
	public void incrementAges()
	{		
		for (Bird bird: birds)
		{
			bird.age++;
		}
	}
	
	public ArrayList<Bird> killOldBirds()
	{
		ArrayList<Bird> toRemove = new ArrayList<Bird>();
		
		for (Bird bird: birds)
		{			
			// bird dies of old age
			if (bird.age >= DEATH_AGE)
			{
				toRemove.add(bird);
			}
		}
		
		for (Bird x : toRemove)
		{
			removeBird(x);
		}
		
		return toRemove;
	}
	
	/** 
	 * moves each bird to a random new location within moveRadius away
	 */
	public void doMove(Bird bird)
	{
			// remove bird from old grid
			grid[bird.x][bird.y].birds.remove(bird);
			
			// generate new location in bounds and in radius
			int newX = (int) Randomizer.getRandomEven(Math.max(0, bird.x - MOVE_RADIUS), Math.min(size, bird.x + MOVE_RADIUS + 1));
			int newY = (int) Randomizer.getRandomEven(Math.max(0, bird.y - MOVE_RADIUS), Math.min(size, bird.y + MOVE_RADIUS + 1));
			
			bird.incurSearchCost();
			assert (newX >= 0 && newX < size);
			assert (newY >= 0 && newY < size);
			
			// move bird to new coordinate
			bird.x = newX;
			bird.y = newY;
			grid[bird.x][bird.y].birds.add(bird);
	}
		
	/**
	 * have all birds look for seeds to survive, returns birds that died of lack of seeds
	 */
	public ArrayList<Bird> doSeedSearching()
	{
		ArrayList<Bird> removed = new ArrayList<Bird>();
		ArrayList<Bird> tempRemove = new ArrayList<Bird>();
		for (int i = 0; i < SEARCH_DAYS; i++)
		{
			sortBirdsByEnergy(birds);
			for (Bird searcher: birds)
			{
				if (!foundSeed(searcher))
				{
					doMove(searcher);
				}
				if (searcher.energy < 0)
				{
					tempRemove.add(searcher);
				}
			}
			for (Bird x : tempRemove)
			{
				removed.add(x);
				removeBird(x);
			}
			tempRemove.clear();
		}
		
		// moved to breeding method
		
		/*
		//reset energy
		for (Bird x : birds)
		{
			x.energy = 0;
		}
		*/
		
		return removed;
	}
	
	/**
	 * Have searcher look for and eat seeds
	 * @param searcher bird searching for seeds
	 * @return true if searcher ate enough to meet the cutoff energy
	 */
	public Boolean foundSeed(Bird searcher)
	{
		int x = searcher.x;
		int y = searcher.y;	
		
		// find a seed within searchRadius
		searcher.incurSearchCost();	
		
		for (int i = Math.max(x - SEARCH_RADIUS, 0); i <= Math.min(x + SEARCH_RADIUS, size - 1); i++)
		{				
			for (int j = Math.max(0, y - SEARCH_RADIUS); j <= Math.min(size - 1, y + SEARCH_RADIUS); j++)
			{				
				for (Seed found : grid[i][j].seeds)
				{
					if (Math.abs(found.size - searcher.beakSize) <= EDIBLE_DIFF)
					{
						searcher.eatSeed(found);
						removeSeed(found);
						return true;
					}
				}
			}
		} // end seed search loop
		
		return false;
	}
	
	/**
	 * Randomly re-arrange the array-list
	 * @param array array to re-arrange
	 */
	public <T> void randomShuffle(ArrayList<T> array)
	{
		for (int i = 0; i < array.size() - 1; i++)
		{
			int newIndex = (int)(Math.random() * (array.size() - i) + i);
			T temp = array.get(i);
			array.set(i, array.get(newIndex));
			array.set(newIndex, temp);	
		}
	}

	public void sortBirdsByEnergy(ArrayList<Bird> list)
	{
		quickSort(list, 0, list.size() - 1);
	}
	
	private static ArrayList<Bird> quickSort(ArrayList<Bird> list, int start, int end)
	{
		if (end - start <= 1)
		{
			return list;
		}
		
		int pivot = split(list, start, end);
		quickSort(list, start, pivot);
		quickSort(list, pivot + 1, end);
		return list;
	}
	
	private static <T> void swap(ArrayList<T> array, int i, int j)
	{
		T temp = array.get(i);
		array.set(i, array.get(j));
		array.set(j, temp);
	}
	
	private static int split(ArrayList<Bird> list, int L, int H)
	{
		Bird pivot = list.get(L);
		int i = L;
		for (int j = L + 1; j <= H; j++)
		{
			Bird current = list.get(j);
			if (current.energy < pivot.energy)
			{
				swap(list, i + 1, j);
				i++;
			}
		}
		swap(list, L, i);
		return i;
	}
	
	public int availableEnergy()
	{
		int islandEnergy = 0;
		for (Seed seed: seeds)
		{
			islandEnergy += seed.energy;
		}
		return islandEnergy;
	}
	
	/**
	 * reports and resets mating counters
	 * @return ratio of attempted matings where the female would use it's memory to decide the beak preference
	 */
	public double reportMatings()
	{
		double mates = matings;
		double memMates = memoryMatings;
		matings = 0;
		memoryMatings = 0;
		
		
		return memMates/mates;
	}
}
