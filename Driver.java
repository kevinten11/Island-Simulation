import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;


public class Driver {
	
	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException 
	{
		//Simulator.runSims(Simulator.SeedType.BIMODAL, Simulator.MateType.CHOOSY_PRECISE, Simulator.MemoryType.NONE, 2000, 10);
		Simulator.runSims(Simulator.SeedType.BIMODAL, Simulator.MateType.CHOOSY_CLOSE, Simulator.MemoryType.NONE, 2000, 4);
		//Simulator.runSims(Simulator.SeedType.BIMODAL, Simulator.MateType.RANDOM, Simulator.MemoryType.NONE, 2000, 10);
		
		//Simulator.runSims(Simulator.SeedType.NORMAL, Simulator.MateType.CHOOSY_PRECISE, Simulator.MemoryType.NONE, 2000, 10);
		//Simulator.runSims(Simulator.SeedType.NORMAL, Simulator.MateType.CHOOSY_CLOSE, Simulator.MemoryType.NONE, 2000, 10);
		//Simulator.runSims(Simulator.SeedType.NORMAL, Simulator.MateType.RANDOM, Simulator.MemoryType.NONE, 2000, 10);
		
		//Simulator.runSims(Simulator.SeedType.UNIFORM, Simulator.MateType.CHOOSY_PRECISE, Simulator.MemoryType.NONE, 2000, 10);
		//Simulator.runSims(Simulator.SeedType.UNIFORM, Simulator.MateType.CHOOSY_CLOSE, Simulator.MemoryType.NONE, 2000, 10);
		//Simulator.runSims(Simulator.SeedType.UNIFORM, Simulator.MateType.RANDOM, Simulator.MemoryType.NONE, 25000, 10);
	}
}
