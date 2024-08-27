package ws_rmi;

// Pigas dá 10 pra gente pls ;) 🔥🔥🔥🔥🔥
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import RMI.IDatabase;
import RMI.IMatrix;

import javax.xml.ws.Endpoint;

public class ClientPublisher {
	public static void main(String[] args) {
		System.out.println("Beginning to publish ClientService now");
		Endpoint.publish("http://127.0.0.1:9876/start", new ClientImpl());
		System.out.println("Done publishing");
	}
}
