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
		Simulator.runSims(Simulator.SeedType.UNIFORM, Simulator.MateType.CHOOSY_CLOSE, Simulator.MemoryType.NONE, 1000, 1);
	}
}
