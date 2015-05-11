package CommonLanguageAdvertisement;

import java.util.ArrayList;

import protobuf.AdvertisementProtos.ProtoAdvertisement;
import protobuf.AdvertisementProtos.ProtoAdvertisement.Builder;

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

	public ProtoAdvertisement toProtoAdvertisement(){
		Builder advertisementBuilder = ProtoAdvertisement.newBuilder();
		for(int i = 0; i < taggedClasses.size(); i++)
		{
			advertisementBuilder.setTaggedClasses(i, taggedClasses.get(i).toProtoClass());
		}
		advertisementBuilder.setTopology(graph.toProtoNetworkGraph());
		return advertisementBuilder.build();
	}
	
	
}
