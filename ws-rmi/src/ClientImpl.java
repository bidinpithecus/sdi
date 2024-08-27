package ws_rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.nio.file.Paths;
import java.nio.file.Files;

import RMI.IDatabase;
import RMI.IMatrix;

import javax.jws.WebService;
@WebService(endpointInterface = "ws_rmi.ClientInterface")
public class ClientImpl implements ClientInterface {
  public void start() {
    try {
      Registry registryMatrix = LocateRegistry.getRegistry("localhost", 6600);
      Registry registryDB = LocateRegistry.getRegistry("localhost", 6677);

      IMatrix matrix_stub = (IMatrix) registryMatrix.lookup("matrix_service");
      IDatabase database_stub = (IDatabase) registryDB.lookup("database_service");
      double[][] a = matrix_stub.randfill(100, 100);
      double[][] b = matrix_stub.randfill(100, 100);

      double[][] c = matrix_stub.mult(a, b);

      Files.createDirectories(Paths.get("matrices")); //matrices

      database_stub.save(a, "matrices/a.txt");
      database_stub.save(b, "matrices/b.txt");
      database_stub.save(c, "matrices/c.txt");

      double[][] na = database_stub.load("matrices/a.txt");
      double[][] nb = database_stub.load("matrices/b.txt");
      double[][] nc = database_stub.load("matrices/c.txt");

      database_stub.save(na, "matrices/a.txt.bkp");
      database_stub.save(nb, "matrices/b.txt.bkp");
      database_stub.save(nc, "matrices/c.txt.bkp");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
