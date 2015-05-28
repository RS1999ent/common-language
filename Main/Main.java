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
		EastWestInterface ew_interface = null;
		Configuration config = null;
		Thread  ew_interfaceThread = null;
		System.out.println(args[0]);
		try {
			config = new PropertiesConfiguration(args[0]);
		} catch (ConfigurationException e) {
			System.out.println("error making configuration");
			System.exit(0);
		}

		int myPort = config.getInt("port");
		System.out.println(myPort);
		String myIP = config.getString("IP");
		System.out.println(myIP);
		long asNum = config.getLong("ASNum");
		System.out.println(asNum);
		long supportedClass = config.getLong("SupportedClass");
		System.out.println(supportedClass);
		ew_interface = new EastWestInterface(myPort, myIP);
		ew_interfaceThread = new Thread(ew_interface);
		ew_interfaceThread.start();
		

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
				ew_interface.sendAdvertisement(
						Integer.parseInt(neighborPort[j]), neighborIP[j],
						advert);
			}

		}
		
		while (true) {
			CommonLanguageAdvertisement advert = ew_interface
					.getNextAdvertisement();
			System.out.println(advert.toString());
		}

	}

}
