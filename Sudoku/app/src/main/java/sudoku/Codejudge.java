package sudoku;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;;

public class Codejudge {
  public static void evaluteCNF() throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    int m = Integer.parseInt(reader.readLine().split(" ")[3]);
    List<List<Integer>> lines = new ArrayList<>();
    for (int i = 0; i < m; i++) {
      List<Integer> us = Arrays
          .stream(reader.readLine().split(" "))
          .map(Integer::parseInt).collect(Collectors.toList());
      lines.add(us);
    }
    List<Integer> vs = Arrays
        .stream(reader.readLine().substring(2).split(" "))
        .map(Integer::parseInt)
        .collect(Collectors.toList());
    for (List<Integer> us : lines) {
      boolean tmp = false;
      for (int u : us) {
        if (u == 0) {
          break;
        }
        for (int v : vs) {
          if (v == u) {
            tmp = true;
            break;
          }
        }
        if (tmp) {
          break;
        }
      }
      if (!tmp) {
        System.out.println("FALSE");
        return;
      }
    }
    System.out.println("TRUE");
  }

  public static void sudokuToSAT() throws IOException {
    Scanner scan = new Scanner(System.in);
    int n = scan.nextInt();
    scan.close();
    int m = 4 * Sudoku.pow(n, 4) + (Sudoku.pow(n, 8) - Sudoku.pow(n, 6)) / 2;
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
    writer.write("p cnf " + Sudoku.pow(n, 6) + " " + m + "\n");
    // writer.write("i \n\n");
    for (int i = 0; i < n * n; i++) {
      for (int k = 1; k <= n * n; k++) {
        for (int j = 0; j < n * n; j++) {
          writer.write(Sudoku.index(n, i, j, k) + " ");
        }
        writer.write("0\n");
      }
    }
    // writer.write("ii \n\n");
    for (int j = 0; j < n * n; j++) {
      for (int k = 1; k <= n * n; k++) {
        for (int i = 0; i < n * n; i++) {
          writer.write(Sudoku.index(n, i, j, k) + " ");
        }
        writer.write("0\n");
      }
    }
    // writer.write("iii \n\n");
    for (int i0 = 0; i0 < n; i0++) {
      for (int j0 = 0; j0 < n; j0++) {
        for (int k = 1; k <= n * n; k++) {
          for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
              writer.write(Sudoku.index(n, i0 * n + i, j0 * n + j, k) + " ");
            }
          }
          writer.write("0\n");
        }
      }
    }
    // (iv)
    // writer.write("iv \n\n");
    for (int i = 0; i < n * n; i++) {
      for (int j = 0; j < n * n; j++) {
        for (int k = 1; k <= n * n; k++) {
          writer.write(Sudoku.index(n, i, j, k) + " ");
        }
        writer.write("0\n");
      }
    }
    // (v)
    // writer.write("v \n\n");
    for (int i = 0; i < n * n; i++) {
      for (int j = 0; j < n * n; j++) {
        for (int k = 1; k <= n * n; k++) {
          for (int kk = 1; kk <= n * n; kk++) {
            if (k > kk || k == kk)
              continue;
            writer.write(-Sudoku.index(n, i, j, k) + " ");
            writer.write(-Sudoku.index(n, i, j, kk) + " ");
            writer.write("0\n");
          }
        }
      }
    }
    writer.close();
  }

  public static void canPlace() {
    Scanner scan = new Scanner(System.in);
    int n = scan.nextInt();
    int m = scan.nextInt();
    int[][] M = new int[n * n][n * n];
    for (int i = 0; i < n * n; i++) {
      for (int j = 0; j < n * n; j++) {
        int k = scan.nextInt();
        M[i][j] = k;
      }
    }
    while (m-- > 0) {
      int i = scan.nextInt() - 1;
      int j = scan.nextInt() - 1;
      int k = scan.nextInt();
      if (canPlace(M, i, j, k)) {
        System.out.println("TRUE");
      } else {
        System.out.println("FALSE");
      }
    }
    scan.close();
  }

  public static boolean canPlace(int[][] M, int i, int j, int k) {
    int n = (int) Math.sqrt(M.length);
    // AVAILABILITY
    if (M[i][j] != 0) {
      return false;
    }
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

  public static StringBuilder buildString(StringBuilder sb, int[] cols, int m) {
    for (int col : cols) {
      sb.append("0".repeat(col));
      sb.append("1");
      sb.append("0".repeat(m > col ? m - col - 1 : 0));
    }
    sb.append("\n");
    return sb;
  }

  public static void sudokoToExactCover() throws IOException {
    Scanner scan = new Scanner(System.in);
    int n = scan.nextInt();
    Set<Integer> clues = new HashSet<>();
    for (int i = 0; i < n * n; i++) {
      for (int j = 0; j < n * n; j++) {
        int k = scan.nextInt();
        if (k != 0)
          clues.add(Sudoku.index(n, i, j, k));
      }
    }
    scan.close();
    Map<Integer, int[]> M = new HashMap<>();
    for (int i = 0; i < n * n; i++) {
      for (int j = 0; j < n * n; j++) {
        for (int k = 0; k < n * n; k++) {
          int row = Sudoku.index(n, i, j, k);
          int[] cols = new int[4];
          cols[0] = i * n * n + k;
          cols[1] = j * n * n + k;
          cols[2] = ((i / n) * n + (j / n)) * n * n + k;
          cols[3] = j + i * n * n;
          M.put(row, cols);
        }
      }
    }
    StringBuilder sb = new StringBuilder();
    int m = Sudoku.pow(n, 4);
    for (int i = 0; i < n * n; i++) {
      for (int j = 0; j < n * n; j++) {
        boolean hasClue = false;
        for (int k = 0; k < n * n; k++) {
          if (clues.contains(Sudoku.index(n, i, j, k + 1))) {
            hasClue = true;
            for (int l = 0; l < k; l++) {
              sb.append("0".repeat(4 * m));
              sb.append("\n");
            }
            int[] cols = M.get(Sudoku.index(n, i, j, k));
            sb = buildString(sb, cols, m);
            for (int l = k + 1; l < n * n; l++) {
              sb.append("0".repeat(4 * m));
              sb.append("\n");
            }
            break;
          }
        }
        if (hasClue)
          continue;
        for (int k = 0; k < n * n; k++) {
          int[] cols = M.get(Sudoku.index(n, i, j, k));
          sb = buildString(sb, cols, m);
        }
      }
    }
    System.out.print(sb.toString());
  }

  public static void exactCoverToSudoku() throws NumberFormatException, IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    int n = Integer.parseInt(reader.readLine());
    int N = pow(n, 4);
    int m = N;
    int[][] M = new int[n * n][n * n];
    while (N-- > 0) {
      String[] line = reader.readLine().split(" ");
      int a = Integer.parseInt(line[0]) - 1;
      int b = Integer.parseInt(line[1]) - 1;

      int i = a / (n * n);
      int j = (b - m) / (n * n);
      int k = a % (n * n) + 1;

      M[i][j] = k;
    }
    for (int i = 0; i < M.length; i++) {
      for (int j = 0; j < M.length; j++) {
        System.out.print(M[i][j] + " ");
      }
      System.out.println();
    }
  }

  public static int pow(int n, int p) {
    return (int) Math.pow(n, p);
  }

  public static int index(int n, int i, int j, int k) {
    return i * pow(n, 4) + j * n * n + k;
  }

  public static void main(String[] args) throws IOException {
    // evaluteCNF();
    // sudokuToSAT();
    // canPlace();
    // sudokoToExactCover();
    exactCoverToSudoku();
  }
}