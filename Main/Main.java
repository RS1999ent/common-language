package Main;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.exception.NestableException;

import CommonLanguageAdvertisement.CommonLanguageAdvertisement;
import CommonLanguageAdvertisement.NetworkGraph;
import CommonLanguageAdvertisement.Node;
import EastWest.EastWestInterface;
import CommonLanguageAdvertisement.Class;

public class Main {
//	private EastWestInterface ex_interface;
//	private Configuration config;

	public static void main(String[] args) {
		EastWestInterface ex_interface;
		Configuration config = null;
		System.out.println(args[0]);
		try {
			config = new PropertiesConfiguration(args[0]);
		} catch (ConfigurationException e) {
			System.out.println("error making configuration");
			System.exit(0);
		}

		int myPort = config.getInt("port");
		String myIP = config.getString("IP");
		long asNum = config.getLong("ASNum");
		long supportedClass = config.getLong("SupportedClass");
		ex_interface = new EastWestInterface(myPort, myIP);

		String[] neighborIP = config.getStringArray("neighbor.IP");
		String[] neighborPort = config.getStringArray("neighbor.port");

		for (int i = 0; i < 10; i++) {
			CommonLanguageAdvertisement advert = new CommonLanguageAdvertisement();
			NetworkGraph graph = new NetworkGraph();

			graph.addNode(new Node(asNum, new Class(supportedClass)));
			advert.setGraph(graph);
			advert.addTaggedClass(new Class(i));

			for (int j = 0; j < neighborIP.length; j++) {
				System.out.println("here");
				ex_interface.sendAdvertisement(
						Integer.parseInt(neighborPort[j]), neighborIP[j],
						advert);
			}

		}
		
		while (true) {
			CommonLanguageAdvertisement advert = ex_interface
					.getNextAdvertisement();
			System.out.println(advert.toString());
		}

	}

}
