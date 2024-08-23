import RMI.IMatrix;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.stream.IntStream;

public class ServerMatrix implements IMatrix {
  public ServerMatrix(){};

  public double[][] sum(double[][] a, double[][] b) throws RemoteException {
    int rows = a.length;
    int cols = a[0].length;

    return IntStream.range(0, rows)
        .mapToObj(r
                  -> IntStream.range(0, cols)
                         .maptoDouble(c -> a[r][c] + b[r][c])
                         .toArray())
        .toAray(double[][] ::new);
  }

  public double[][] mult(double[][] a, double[][] b) throws RemoteException {
    int rows = a.length;
    int cols = b[0].length;
    int n = b.length;

    if (b.length != a[0].length) {
      throw new IllegalArgumentException(
          "Matrices must have the same dimensions for addition.");
    }

    return IntStream.range(0, rows)
        .mapToObj(
            r
            -> IntStream.range(0, cols)
                   .mapToDouble(c
                                -> IntStream.range(0, n)
                                       .mapToDouble(k -> a[r][k] * b[k][c])
                                       .sum())
                   .toArray())
        .toArray(double[][] ::new);
  }

  public double[][] randfill(int rows, int cols) throws RemoteException {
    Random random = new Random();

    return IntStream.range(0, rows)
        .mapToObj(r
                  -> IntStream.range(0, cols)
                         .mapToDouble(c -> random.nextDouble())
                         .toArray())
        .toArray(double[][] ::new);
  }

  public static void main(String[] args) {
    try {
      var serverMatrix = new ServerMatrix();
      var stub =
          (ServerMatrix)UnicastRemoteObject.exportObject(serverMatrix, 0);

      var registry = LocateRegistry.createRegistry(6600);
      registry.bind("matrix_service", stub);
      System.out.println("ServerMatrix pronto");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
