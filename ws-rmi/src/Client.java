package ws_rmi;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class Client {
  public static void main(String[] args) {
    try {
      URL url = new URL("http://localhost:9876/start?wsdl");
      QName qname = new QName("http://ws_rmi/",
              "ClientImplService");
      Service service = Service.create(url, qname);

      ClientInterface server = service.getPort(ClientInterface.class);
      server.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
