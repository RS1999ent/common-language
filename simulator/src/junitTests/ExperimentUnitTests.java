/**
 * 
 */
package junitTests;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import simulator.AS;
import simulator.Simulator;

/**
 * @author David
 *
 */

public class ExperimentUnitTests {

	public class Tuple{
		public String topoFile;
		public String veriFile;
		public String asTypes;
		Tuple(String topoFile, String asTypes, String veriFile){
			this.topoFile = topoFile;
			this.veriFile = veriFile;
			this.asTypes = asTypes;
		}
	}
	
	Tuple sumSim[] = {new Tuple("topo", "astypes", "verificationFiles/testVeri.yml")};
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link simulator.Simulator#iaBasicSimulationAllTests(int, boolean, float, int, int)}.
	 */
	@Test
	public final void testSumExperiment() {

		for(Tuple experiment : sumSim)
		{
			//Simulator simulator = new Simulator(); 
			//			annotatedBrite.txt asTypes.txt ..\results\result.txt --seed 1 --sim 4 --monitorFrom 3 --useBandwidth 0 --forX .1 --metric 1
			String arg = experiment.topoFile + " " + experiment.asTypes + " ../results/result.txt --seed 1 --sim 3 --monitorFrom 3 --useBandwidth 0 -- forX .1 --metric 1";
			String[] args = arg.split("\\s+");
			//String[] args = {experiment.topoFile, experiment.asTypes, "../results/result.txt", "--seed", "1", "--sim", "3",    } 
			try {
				VerificationInformation veriInfo = new VerificationInformation(experiment.veriFile);
				boolean verified = true;
				Simulator.main(args);
				HashMap<Integer, AS> asMap = Simulator.getASMap();
				for (AS aAS : asMap.values())
				{

					verified = veriInfo.verifyAS(aAS, VerificationInformation.TRUE_COST);
				}
				assert(verified == true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} //endfor

	}

}
