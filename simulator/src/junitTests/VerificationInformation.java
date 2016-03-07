/**
 * 
 */
package junitTests;

import integratedAdvertisement.IA;
import integratedAdvertisement.Protocol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.yaml.*;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import simulator.AS;

/**
 * @author David
 *
 */
public class VerificationInformation {

	public static final int TRUE_COST = 1;
	public static final int BNBW = 2;
	public static final int REPLACEMENT= 3;
	
	public HashMap<Integer, ASInfo> asMap = new HashMap<Integer, ASInfo>();
	
	public class PathAndMetric
	{
		@Override
		public String toString() {
			return "PathAndMetric [asPath=" + asPath + ", metric=" + metric
					+ "]";
		}

		public ArrayList<Integer> asPath = new ArrayList<Integer>();
		//public HashMap<String, Float> metrics = new HashMap<String, Float>();
		float metric;
		
		public PathAndMetric(List<Integer> asPath, float metric)
		{
			this.asPath = new ArrayList<Integer>(asPath);
			this.metric = metric;
			
		}
		
	}
	
	public class ASInfo
	{
		@Override
		public String toString() {
			return "ASInfo [rib=" + rib + ", fib=" + fib + "]";
		}
		HashMap<Integer, ArrayList<PathAndMetric>> rib = new HashMap<Integer, ArrayList<PathAndMetric>>();
		HashMap<Integer, ArrayList<PathAndMetric>> fib = new HashMap<Integer, ArrayList<PathAndMetric>>();
	}
	
	public VerificationInformation(String veriFile) throws FileNotFoundException
	{
		InputStream input = new FileInputStream(new File(veriFile));	
		Yaml yaml = new Yaml(new Constructor(yamlObject.class));		
		for (Object data : yaml.loadAll(input))
		{
			deparseYamlObject ((yamlObject) data);
		}
		System.out.println(asMap);
	}
	

	public boolean verifyAS(AS aAS, int veri)
	{
		return false;
		
	}
	
	private boolean verifyAdvert(IA advert, ArrayList<PathAndMetric> pathsMetrics, Integer matchedPaths, int verifyWhat)
	{
		for(PathAndMetric aPath : pathsMetrics)
		{
			if(compLists(advert.getPath(), aPath.asPath))
			{
				matchedPaths++;
				switch (verifyWhat)
				{
				case TRUE_COST:
					float advertTrueCost = advert.getTrueCost();
					float veriTrueCost = aPath.metric;
					if(advertTrueCost != veriTrueCost)
					{
						System.out.println("Unmatched trueCosts for path: " + advert.getPath());
						return false;
					}
					break;
				case BNBW:
					float advertBNBW = advert.bookKeepingInfo.get(IA.BNBW_KEY);
					float veriBNBW = aPath.metric;
					if(advertBNBW != veriBNBW)
					{
						System.out.println("Unmatched bnbw for path: " + advert.getPath());
						return false;
					}
					break;
				case REPLACEMENT:
					String[] numPathsString = AS.getProtoProps(advert, advert.popCosts.keySet().iterator().next(), new Protocol(AS.REPLACEMENT_AS));
					float numPaths = 0;
					float veriNumPaths = aPath.metric;
					if(numPathsString == null)
					{
						numPaths = 1;
					}
					else
					{
						numPaths = Integer.valueOf(numPathsString[0]);
					}
					if(numPaths != veriNumPaths)
					{
						System.out.println("Unmatched numpaths for path: " + advert.getPath());
						return false;
					}

				}

			}//endif 
		} //endfor
		return true;
	}

	private boolean verifyInfoBases(AS theAS, int asNum, int verifyWhat)
	{
		HashMap<Integer, HashMap<Integer,IA>> ribIn = theAS.ribIn;
		HashMap<Integer, IA> fib = theAS.bestPath;
		for (int dst :  ribIn.keySet())
		{
			Integer matchedPaths = 0;
			ArrayList<PathAndMetric> pathsMetrics = asMap.get(asNum).rib.get(dst);
			if (pathsMetrics == null)
			{
				return false;
			}
			for(IA advert : ribIn.get(dst).values())
			{
				if(!verifyAdvert(advert,  pathsMetrics,  matchedPaths, verifyWhat))
				{
					return false;
				}
				
			}//endfor
			if(matchedPaths.intValue() != pathsMetrics.size())
			{
				return false;
			}
		}//endfor
		Integer fibPaths = 0;
		for (int dst :  fib.keySet())
		{			
			ArrayList<PathAndMetric> pathsMetrics = asMap.get(asNum).rib.get(dst);
			if (pathsMetrics == null)
			{
				return false;
			}
			IA advert = fib.get(dst);
			if(!verifyAdvert(advert,  pathsMetrics,  fibPaths, verifyWhat))
			{
				return false;
			}
	
			
		}//endfor
		if(fibPaths.intValue() != asMap.get(asNum).fib.size())
		{
			return false;
		}
		
		return true;
	}
	
	private boolean compLists(Collection<Integer> list1, Collection<Integer> list2)
	{
		if(list1.size() != list2.size())
		{
			return false;
		}
		
		Iterator<Integer> list1Iter = list1.iterator();
		Iterator<Integer> list2Iter = list2.iterator();
		
		while(list1Iter.hasNext())
		{
			int itemL1 = list1Iter.next();
			int itemL2 = list2Iter.next();
			if(itemL1 != itemL2)
			{
				return false;
			}
		}
		
		return true;
	}
	
	private void deparseYamlObject(yamlObject data)
	{
		int asNum = data.AS;
		ASInfo info = new ASInfo();
		for (entry aEntry : data.rib)
		{			
			//info.rib.put(aEntry.dst, new PathAndMetric(aEntry.path, aEntry.metric));
			ArrayList<PathAndMetric> tmp = info.rib.get(aEntry.dst);
			if (tmp == null)
			{
				tmp = new ArrayList<PathAndMetric>();
				info.rib.put(aEntry.dst, tmp);	
			}
			tmp.add(new PathAndMetric(aEntry.path, aEntry.metric));		
			info.rib.put(aEntry.dst, tmp);
		}
		for (entry aEntry : data.fib)
		{
			ArrayList<PathAndMetric> tmp = info.fib.get(aEntry.dst);			
			if(tmp == null)
			{
				tmp = new ArrayList<PathAndMetric>();
				info.fib.put(aEntry.dst, tmp);
			}
			tmp.add(new PathAndMetric(aEntry.path, aEntry.metric));
			info.fib.put(aEntry.dst, tmp);
		}
		asMap.put(asNum, info);
	}
	
}


