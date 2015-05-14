package CommonLanguageAdvertisement;

import java.util.ArrayList;

import protobuf.AdvertisementProtos.ProtoAdvertisement;
import protobuf.AdvertisementProtos.ProtoAdvertisement.Builder;

//top level class that holds the common language advertisement
//consists of a network graph and the tagged class/protocol list

public class CommonLanguageAdvertisement {
	private NetworkGraph graph = new NetworkGraph();
	private ArrayList<Class> taggedClasses = new ArrayList<Class>();
	
	
	
	public CommonLanguageAdvertisement(ProtoAdvertisement advertisement)
	{
		graph = new NetworkGraph(advertisement.getTopology());
		for(int i = 0; i < advertisement.getTaggedClassesCount(); i++)
		{
			taggedClasses.add(new Class(advertisement.getTaggedClasses(i)));
		}
	}
	
	
	
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

	
	public ProtoAdvertisement toProtoAdvertisement(){
		Builder advertisementBuilder = ProtoAdvertisement.newBuilder();
		for(int i = 0; i < taggedClasses.size(); i++)
		{
			advertisementBuilder.addTaggedClasses(taggedClasses.get(i).toProtoClass());
		}
		advertisementBuilder.setTopology(graph.toProtoNetworkGraph());
		return advertisementBuilder.build();
	}
	
	
}
