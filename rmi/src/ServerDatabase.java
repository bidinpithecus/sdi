import RMI.IDatabase;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.stream.Collectors;
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

    try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename))) {
      bufferedWriter.write(buffer);

      bufferedWriter.close();
    } catch (RemoteException e) {
      throw e;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public double[][] load(String filename) throws RemoteException {
    try (FileReader fileReader = new FileReader(filename);
         BufferedReader bufferedReader = new BufferedReader(fileReader)) {

        // Read entire file content as a single string
        String content = bufferedReader.lines().collect(Collectors.joining("\n"));

        // Split content into rows using "; " delimiter
        String[] rows = content.split("; ");

        // Determine number of rows and columns
        int rowCount = rows.length;
        int colCount = rows[0].split(", ").length;

        // Initialize matrix
        double[][] matrix = new double[rowCount][colCount];

        // Fill matrix with values
        for (int r = 0; r < rowCount; r++) {
            // Split each row into columns
            String[] values = rows[r].split(", ");
            for (int c = 0; c < colCount; c++) {
                matrix[r][c] = Double.parseDouble(values[c].trim());
            }
        }

        return matrix;
    } catch (NumberFormatException | IOException e) {
        e.printStackTrace();
    }
    return null;
  }

  public void remove(String filename) throws RemoteException {
    Path path = Paths.get(filename);

    if (Files.exists(path)) {
      try {
        Files.delete(path);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      throw new IllegalArgumentException("File does not exist.");
    }
  }

  public static void main(String[] args) {
    try {
      ServerDatabase serverDatabase = new ServerDatabase();
      IDatabase stub =
          (IDatabase)UnicastRemoteObject.exportObject(serverDatabase, 0);

      Registry registry = LocateRegistry.createRegistry(6677);
      registry.bind("database_service", stub);
      System.out.println("ServerDatabase pronto");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
