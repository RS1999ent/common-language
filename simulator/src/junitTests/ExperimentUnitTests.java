/**
 * 
 */
package junitTests;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
	
	Tuple sumSim[] = {new Tuple("topo", "astypes", "veri")};
	
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
			try {
				VerificationInformation veriInfo = new VerificationInformation(experiment.veriFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		fail("Not yet implemented"); // TODO
	}

}
