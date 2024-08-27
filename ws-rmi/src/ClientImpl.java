import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import RMI.IDatabase;
import RMI.IMatrix;

import javax.jws.WebService;
 
@WebService(endpointInterface = "hello.HelloWorldServer")
public class ClientImpl implements ClientInterface {
  public void start() {
    try {
      Registry registryMatrix = LocateRegistry.getRegistry("localhost", 6600);
      Registry registryDB = LocateRegistry.getRegistry("localhost", 6677);

      IMatrix matrix_stub = (IMatrix)registryMatrix.lookup("matrix_service");
      IDatabase database_stub =
          (IDatabase)registryDB.lookup("database_service");
      double[][] a = matrix_stub.randfill(100, 100);
      double[][] b = matrix_stub.randfill(100, 100);

      double[][] c = matrix_stub.mult(a, b);

      database_stub.save(a, "a.txt");
      database_stub.save(b, "b.txt");
      database_stub.save(c, "c.txt");

      double[][] na = database_stub.load("a.txt");
      double[][] nb = database_stub.load("b.txt");
      double[][] nc = database_stub.load("c.txt");

      database_stub.save(na, "a.txt.bkp");
      database_stub.save(nb, "b.txt.bkp");
      database_stub.save(nc, "c.txt.bkp");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
