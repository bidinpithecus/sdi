import RMI.IDatabase;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.stream.IntStream;

public class ServerDatabase implements IDatabase {
  public ServerDatabase() {}

  public void save(double[][] a, String filename) throws RemoteException {
    int rows = a.length;
    int cols = a[0].length;

    String buffer =
        IntStream.range(0, rows)
            .mapToObj(r
                      -> IntStream.range(0, cols)
                             .mapToObj(c -> Double.toString(a[r][c]))
                             .collect(Collectors.joining(", ")))
            .collect(Collectors.joining("; "));

    BufferedWriter bufferedWriter =
        new BufferedWriter(new FileWriter(filename));
    bufferedWriter.write(buffer);

    bufferedWriter.close();
  }

  public double[][] load(String filename) throws RemoteException {
    try (BufferedReader bufferedReader =
             new BufferedReader(new FileReader(filename))) {
      String[] rows = bufferedReader.lines().toArray(String[] ::new);

      int rowCount = rows.length;
      int colCount = rows[0].split(", ").length;

      double[][] matrix = new double[rowCount][colCount];

      for (int r = 0; r < rowCount; r++) {
        String[] values = rows[r].split(", ");

        for (int c = 0; c < colCount; c++) {
          matrix[r][c] = Double.parseDouble(values[c]);
        }
      }

      return matrix;
    }
  }

  public void remove(String filename) throws RemoteException {
    Paths path = Paths.get(filename);

    if (Files.exists(path)) {
      Files.delete(path);
    } else {
      throw new IllegalArgumentException("File does not exist.");
    }
  }

  public static void main(String[] args) {
    try {
      ServerDatabase serverDatabase = new ServerDatabase();
      ServerDatabase stub =
          (ServerDatabase)UnicastRemoteObject.exportObject(serverDatabase, 0);

      LocateRegistry registry = LocateRegistry.createRegistry(6677);
      registry.bind("database_service", stub);
      System.out.println("ServerDatabase pronto");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
