import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import RMI.IDatabase;
import RMI.IMatrix;

public class Client {
  public static void main(String[] args) {
    String hostM = args[0];
    String hostDB = args[1];

    try {
      Registry registryMatrix = LocateRegistry.getRegistry(hostM, 6600);
      Registry registryDB = LocateRegistry.getRegistry(hostDB, 6677);

      IMatrix matrix_stub = (IMatrix)registryMatrix.lookup("matrix_service");
      IDatabase database_stub =
          (IDatabase)registryDB.lookup("database_service");
      double[][] a = matrix_stub.randfill(100, 100);
      double[][] b = matrix_stub.randfill(100, 100);
      double[][] c = matrix_stub.mult(a, b);

      database_stub.save(a, "a.txt");
      database_stub.save(b, "b.txt");
      double[][] na = database_stub.load("a.txt");
      double[][] nb = database_stub.load("b.txt");
      database_stub.remove("a.txt");
      database_stub.remove("b.txt");

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
