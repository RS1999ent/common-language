/**
 * 
 */
package junitTests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.yaml.*;
import org.yaml.snakeyaml.Yaml;

/**
 * @author David
 *
 */
public class VerificationInformation {

	public HashMap<Integer, ASInfo> asMap = new HashMap<Integer, ASInfo>();
	
	public class PathAndMetric
	{
		public ArrayList<Integer> asPath = new ArrayList<Integer>();
		public HashMap<String, Float> metrics = new HashMap<String, Float>();
		
		public void addMetric(String key, float value)
		{
			metrics.put(key, value);
		}
	}
	
	public class ASInfo
	{
		public int asNum;
		HashMap<Integer, ArrayList<PathAndMetric>> rib = new HashMap<Integer, ArrayList<PathAndMetric>>();
		HashMap<Integer, ArrayList<PathAndMetric>> fib = new HashMap<Integer, ArrayList<PathAndMetric>>();
	}
	
	public VerificationInformation(String veriFile) throws FileNotFoundException
	{
		InputStream input = new FileInputStream(new File(veriFile));
		Yaml yaml = new Yaml();
		for (Object data :yaml.loadAll(input) )
		{
			System.out.println(data);
		}
		
	}
	
}
