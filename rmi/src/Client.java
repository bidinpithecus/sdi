import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import RMI.IDatabase;
import RMI.IMatrix;

public class Client {
  public static boolean matricesEqual(double[][] a, double[][] b) {
    if (a.length != b.length) return false;
    for (int i = 0; i < a.length; i++) {
        if (a[i].length != b[i].length) return false;
        for (int j = 0; j < a[i].length; j++) {
            if (a[i][j] != b[i][j]) return false;
        }
    }
    return true;
  }

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
      database_stub.save(c, "c.txt");

      double[][] na = database_stub.load("a.txt");
      double[][] nb = database_stub.load("b.txt");
      double[][] nc = database_stub.load("c.txt");

      database_stub.save(na, "a.txt.bkp");
      database_stub.save(nb, "b.txt.bkp");
      database_stub.save(nc, "c.txt.bkp");

      //database_stub.remove("a.txt");
      //database_stub.remove("b.txt");
      //database_stub.remove("c.txt");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
