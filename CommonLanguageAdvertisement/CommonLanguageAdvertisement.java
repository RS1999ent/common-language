package CommonLanguageAdvertisement;

import java.util.ArrayList;

public class CommonLanguageAdvertisement {
	private NetworkGraph graph = new NetworkGraph();
	ArrayList<Class> taggedClasses = new ArrayList<Class>();
	
	public NetworkGraph getGraph() {
		return graph;
	}
	public void setGraph(NetworkGraph graph) {
		this.graph = graph;
	}

	public void addTaggedClass(Class add)
	{
		if(!taggedClasses.contains(add))
		{
			taggedClasses.add(add);
		}
	}
	
	public void removeTaggedClass(Class remove){
		taggedClasses.remove(remove);
	}
	
	public ArrayList<Class> getTaggedClasses() {
		return taggedClasses;
	}
	
}
