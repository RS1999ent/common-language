package CommonLanguageAdvertisement;

import protobuf.AdvertisementProtos.ProtoEdgeNodes;

public class EdgePair{
		public Node node1;
		public Node node2;
		
		public EdgePair(){
			
		}
		
		public EdgePair(Node node1, Node node2)
		{
			this.node1 = node1;
			this.node2 = node2;
		}
		
		public EdgePair(ProtoEdgeNodes protoEdgeNodes)
		{
			node1 = new Node(protoEdgeNodes.getNode1());
			node2 = new Node(protoEdgeNodes.getNode2());
		}
		
		public ProtoEdgeNodes toProtoEdgeNodes()
		{
			return ProtoEdgeNodes.newBuilder()
					.setNode1(node1.toProtoNode())
					.setNode2(node2.toProtoNode()).build();
					
		}
		
		public String toString()
		{
			String newString = "<";
			newString = newString.concat(String.valueOf(node1.getASNum()));
			newString = newString.concat(", ");
			newString = newString.concat(String.valueOf(node2.getASNum()));
			newString = newString.concat(">");
			return newString;
		}
	}