package sudoku;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Sudoku {

  public static int pow(int n, int p) {
    return (int) Math.pow(n, p);
  }

  public static int index(int n, int i, int j, int k) {
    return i * pow(n, 4) + j * n * n + k;
  }

  public static int[][] copy(int[][] A) {
    int[][] B = new int[A.length][A.length];
    for (int i = 0; i < B.length; i++) {
      for (int j = 0; j < B.length; j++) {
        B[i][j] = A[i][j];
      }
    }
    return B;
  }

  public static String printSudoku(int[][] M) {
    int n = (int) Math.sqrt(M.length);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < n * n; i++) {
      for (int j = 0; j < n * n; j++) {
        sb.append(M[i][j] + " ");
      }
      sb.append("\n");
    }
    sb.append("\n");
    return sb.toString();
  }

  public static void prettyPrintSudoku(int[][] M) {
    int n = (int) Math.sqrt(M.length);
    StringBuilder sb = new StringBuilder();
    int l = -1;
    for (int i = 0; i < n * n; i++) {
      sb.append("| ");
      for (int j = 0; j < n * n; j++) {
        sb.append(String.format("%2d ", M[i][j]));
        if (j % n == n - 1) {
          sb.append("| ");
        }
      }
      if (l == -1) {
        l = sb.length() - 2;
      }
      if (i % n == n - 1) {
        sb.append("\n ");
        sb.append("- ".repeat(l / 2));
      }
      sb.append("\n");
    }
    sb.insert(0, " ");
    sb.insert(1, "\n");
    sb.insert(1, "- ".repeat(l / 2));
    System.out.println(sb.toString());
  }

  public static boolean canPlace(int[][] M, int i, int j, int k) {
    int n = (int) Math.sqrt(M.length);
    // ROWS
    for (int r = 0; r < n * n; r++) {
      if (M[r][j] == k) {
        return false;
      }
    }
    // COLUMNS
    for (int c = 0; c < n * n; c++) {
      if (M[i][c] == k) {
        return false;
      }
    }
    // SUBGRID
    for (int r = 0; r < n; r++) {
      for (int c = 0; c < n; c++) {
        i = i - i % n + r;
        j = j - j % n + c;
        if (M[i][j] == k) {
          return false;
        }
      }
    }
    return true;
  }

  public static int[] findEmptySquare(int[][] M, int i, int j) {
    int n = (int) Math.sqrt(M.length);
    while (i < n * n && M[i][j] != 0) {
      if (j + 1 == n * n) {
        j = 0;
        i++;
      } else {
        j++;
      }
    }
    return new int[] { i, j };
  }

  public static void writeSudokusToFile(int n) throws IOException, InterruptedException, ExecutionException {
    int count = 1, seed = 42;
    FileWriter writer = new FileWriter("sudokus/sudokus_n=" + n + ".txt");
    while (count <= 100) {
      final int SEED = seed;
      final Duration timeout = Duration.ofSeconds(1000);
      ExecutorService executor = Executors.newSingleThreadExecutor();
      final Future<int[][]> handler = executor.submit(() -> {
        return DLX.generateSudoku(n, SEED);
      });
      try {
        double time = System.currentTimeMillis();
        int[][] sudoku = handler.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        time = (System.currentTimeMillis() - time) / 1000;
        System.out.println("Count: " + (count++) + ". Seed: " + SEED + ". Time: " + time + " s.");
        writer.write(printSudoku(sudoku));
      } catch (TimeoutException e) {
        System.out.println("Timeout: " + SEED);
        handler.cancel(true);
      } finally {
        seed += 42;
      }
      executor.shutdownNow();
    }
    writer.close();
  }

  public static int[][] readSudoku() throws Exception {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    int n = Integer.parseInt(reader.readLine());
    int[][] M = new int[n * n][n * n];
    for (int i = 0; i < M.length; i++) {
      String[] nums = reader.readLine().split(" ");
      for (int j = 0; j < M.length; j++) {
        M[i][j] = Integer.parseInt(nums[j]);
      }
    }
    return M;
  }

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      writeSudokusToFile(2);
    } else {
      int[][] M = readSudoku();
      switch (args[0]) {
        case "backtracking":
          Backtrack.backtrack(M, 0, 0);
          prettyPrintSudoku(M);
          break;
        case "sat":
          String CNF = SAT.sudokuToSAT(M);
          System.out.println(CNF);
          break;
        case "dlx":
          DLX.dlx(M);
          break;
      }
    }

  }
}