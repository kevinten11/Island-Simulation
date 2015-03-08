import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;


public class Simulator {
	
	// affects the Seed(double _energy) constructor in the Seed class
	public static enum SeedType {
		BIMODAL, NORMAL, UNIFORM
	}
	
	// affects the findAndMate method in the Island class
	// this affects the default behavior in the birds
	public static enum MateType {
		RANDOM, CHOOSY_PRECISE, CHOOSY_CLOSE
	}
	
	// affects the getPrefSize() method in the Bird class
	public static enum MemoryType {
		NONE, RANDOM, WEIGHTED, MOST_RECENT
	}
	
	public static HashMap<String, SeedType> seedTypes;
	
	public static SeedType seedType = SeedType.UNIFORM;
	public static MateType mateType = MateType.RANDOM;
	public static MemoryType memoryType = MemoryType.NONE;
	
	public static Random generator = new Random();
	
	// history of all birds in a simulation
	public static Set<Bird> allBirds = new HashSet<Bird>();
	
	public static int num = 0;
	
	// this affects the sim
	private static int years = 100;
	
	
	private static String genName()
	{
		String name = "";
		
		switch (seedType)
		{
			case BIMODAL:
				name += "-BimodalS";
				break;
			
			case NORMAL:
				name += "-NormalS";
				break;
				
			case UNIFORM:
				name += "-UniformS";
				break;		
		}
		
		switch (mateType)
		{
			case CHOOSY_CLOSE:
				name += "-ChoosyClose";
				break;
				
			case RANDOM:
				name += "-RandomMate";
				break;
				
			case CHOOSY_PRECISE:
				name += "-ChoosyPrecise";
				break;				
		}
		
		switch (memoryType)
		{
			case NONE:
				name += "-NoM";
				break;
			
			case MOST_RECENT:
				name += "-RecentM";
				break;
				
			case RANDOM:
				name += "-RandomM";
				break;		
			
			case WEIGHTED:
				name += "-WeightedM";
		}
		
		return name;	
	}
	
	private static double[][] runSim() throws FileNotFoundException, UnsupportedEncodingException
	{
		allBirds.clear();
		String name = genName();
		
		// TODO Auto-generated method stub
		ArrayList<ArrayList<Double>> sizes = new ArrayList<ArrayList<Double>>(years);
		ArrayList<ArrayList<Double>> energyDeadSizes = new ArrayList<ArrayList<Double>>(years);
		ArrayList<ArrayList<Double>> ageDeadSizes = new ArrayList<ArrayList<Double>>(years);
		ArrayList<Double> memMateRatio = new ArrayList<Double>(years);
		
		// initialize arrays
		for (int i = 0; i < years; i++ )
		{
			sizes.add(new ArrayList<Double>());
			energyDeadSizes.add(new ArrayList<Double>());
			ageDeadSizes.add(new ArrayList<Double>());
		}
		
		Island island = new Island();
		
		for (int i = 0; i < years; i++)
		{			
			System.out.println(i);
			// record sizes
			for (Bird bird: island.birds)
			{
				sizes.get(i).add(bird.beakSize);
			}
			
			// System.out.println("  Year:  " + i);
			island.generateSeeds();
			
			ArrayList<Bird> deadBirds = island.doSeedSearching();
			for (Bird dead: deadBirds)
			{
				energyDeadSizes.get(i).add(dead.beakSize);
			}
			
			// island.checkBirds();
			island.incrementAges();
			island.doMating();
			
			for (Bird ageDead : island.killOldBirds())
			{
				ageDeadSizes.get(i).add(ageDead.beakSize);
			}
			
			// check matings
			memMateRatio.add(island.reportMatings());
		}		
		
		int lines = 0;
		int files = 1;
		String fileName = years + "Years" + name + "-" + num + ".txt";
		System.out.println("File Name: " + fileName);
		PrintWriter writer = new PrintWriter("Out\\Raw\\" + fileName, "UTF-8");
		PrintWriter writer2 = new PrintWriter("Out\\" + fileName, "UTF-8");

		String x = "\t";
		String y = "\t";
		String z = "\t";
		
		writer.println("Generation\tData\t\tDeath Generation\tEnergy Deaths\tAge Deaths\tPop Size\tMemory Use Ratio");
		writer2.println("Generation\tData\t\tDeath Generation\tEnergy Deaths\tAge Deaths\tPop Size\tMemory Use Ratio");
		int factor = Math.max(years / 500, 1);
		
		for (int i = 0; i < years; i++)
		{
			int maxSize = Math.max(Math.max(sizes.get(i).size(), energyDeadSizes.get(i).size()), ageDeadSizes.get(i).size());
			for (int j = 0; j < maxSize; j++)
			{
				if (j < sizes.get(i).size())
				{
					x = sizes.get(i).get(j).toString();
				}
				else
				{
					x = "";
				}
				if (j < energyDeadSizes.get(i).size())
				{
					y = energyDeadSizes.get(i).get(j).toString();
				}
				else
				{
					y = "";
				}	
				if (j < ageDeadSizes.get(i).size())
				{
					z = ageDeadSizes.get(i).get(j).toString();
				}
				else
				{
					z = "";
				}
				
				String nextLine = i + "\t" + x + "\t\t" + ((double)i + 0.5) + "\t" + y + "\t" + z + "\t" + sizes.get(i).size() + "\t" + memMateRatio.get(i);
				writer.println(nextLine);
				
				if (i % factor == 0)
				{
					writer2.println(nextLine);
				}
				
				
				lines++;
				
				// if sizes gets too large for excel, make a new file for raw data
				if (lines > 1000000)
				{
					lines = 0;
					files++;
					writer = new PrintWriter("Out\\" + years + "Years" + name + "-" + num + "-cont" + files + ".txt");
				}
			}
		}
		
		writer.close();
		writer2.close();
		
		PrintWriter writer3 = new PrintWriter("Out\\Analysis\\" + fileName, "UTF-8");
		
		double[] speciesCounts = new double[years];
		double[] aveSize = new double[years];
		double[] aveRange = new double[years];
		
		writer3.println("Year\tTotal Population\tSpeciesEst\tFirst\tLast\tSize\t...");
		for (int i = 0; i < years; i++)
		{
			ArrayList<Double> info = doAnalysis(sizes.get(i));
			writer3.print(i + "\t" + sizes.get(i).size());
			double species = info.get(0);
			speciesCounts[i] = species;
			double totalSize = 0;
			double totalRange = 0;
			for (int j = 0; j < info.size(); j++)
			{
				writer3.print("\t" + info.get(j));
				if ((j-1) % 3 == 0)
				{
					totalRange += info.get(j + 1) - info.get(j);
				}
				if ((j-1) % 3 == 2)
				{
					totalSize += info.get(j);
				}
			}
			
			aveRange[i] = totalRange / species;
			aveSize[i] = totalSize / species;
			
			writer3.println();
		}
		writer3.close();
		
		double[][] toReturn = new double[3][years];
		toReturn[0] = speciesCounts;
		toReturn[1] = aveSize;
		toReturn[2] = aveRange;
		
		return toReturn;
	}
	
	private static ArrayList<Double> doAnalysis(ArrayList<Double> sizes)
	{
		ArrayList<Double> results = new ArrayList<Double>();
		double speciesCount = 0;
		
		double speciesGap = 0.1;
		double gap = 0;
		
		int group = 1;
		sort(sizes);
		for (int i = 2; i < sizes.size(); i++)
		{
			gap = sizes.get(i) - sizes.get(i-1);
			
			if (gap < speciesGap && i < sizes.size() - 1)
			{
				group++;
			}
			else if (group > sizes.size() / 6.0)
			{
				speciesCount++;
				results.add(sizes.get(i - group));
				results.add(sizes.get(i - 1));
				results.add((double)group);
				group = 1;
			}
			else
			{
				group = 1;
			}
		}
		
		results.add(0, speciesCount);
		
		return results;
	}
	
	// from http://examples.javacodegeeks.com/core-java/quicksort-algorithm-in-java-code-example/
	private static void sort(ArrayList<Double> array)
	{
		quickSort(array, 0, array.size() - 1);
	}
	
	private static void quickSort(ArrayList<Double> elements, int from, int to)
	{
		if (from < to)
		{
			int p = partition(elements, from, to);
			quickSort(elements, from, p - 1);
			quickSort(elements, p + 1, to);
		}
	}
	
	private static int partition(ArrayList<Double> elements, int from, int to)
	{
		double pivot = elements.get(from);
		int i = from + 1;
		int j = to;
		
		while (i < j)
		{
			while (elements.get(i) < pivot && i < to) i++;
			while (elements.get(j) > pivot) j--;
			if (i >= j)
				break;
			else
			{
				double temp = elements.get(i);
				elements.set(i, elements.get(j));
				elements.set(j, temp);
			}
		}
		elements.set(from, elements.get(j));
		elements.set(j, pivot);
		return j;
	}
	
	public static void runSims(SeedType seed, MateType mate, MemoryType mem, int yearsToRun, int runs) throws FileNotFoundException, UnsupportedEncodingException
	{
		years = yearsToRun;
		seedType = seed;
		mateType = mate;
		memoryType = mem;
		
		double[][] speciesCountsByYear = new double[runs][years];
		double[][] aveSpeciesSizeByYear = new double[runs][years];
		double[][] aveSpeciesRangeByYear = new double[runs][years];
		
		
		num = 0;
		while (num < runs)
		{
			double[][] simResults = runSim();
			speciesCountsByYear[num] = simResults[0];
			aveSpeciesSizeByYear[num] = simResults[1];
			aveSpeciesRangeByYear[num] = simResults[2];
			num++;
		}
		
		PrintWriter writer = new PrintWriter("Out\\Agg\\" + years + genName() + ".txt");
		
		// print key for years
		writer.println("Species Counts by Year matrix");
		printMatrix(writer, speciesCountsByYear);
		writer.println("Average Species Size by Year matrix");
		printMatrix(writer, aveSpeciesSizeByYear);
		writer.println("Average Species Range by Year matrix");
		printMatrix(writer, aveSpeciesRangeByYear);
		
		writer.close();	
	}
	
	private static void printMatrix(PrintWriter writer, double[][] matrix)
	{
		int runs = matrix.length;
		writer.print("\t");
		
		for (int i = 0; i < matrix[0].length; i++)
		{
			writer.print(i + "\t");
		}
		writer.print("\n");
		
		for (int i = 0; i < runs; i++)
		{
			writer.print(i + "\t");
			for (int j = 0; j < matrix[0].length; j++)
			{
				writer.print(matrix[i][j] +"\t");
			}
			writer.print("\n");
		}
	}
}

