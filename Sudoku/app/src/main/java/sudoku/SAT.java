package sudoku;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SAT {

  public static String sudokuToSAT(int[][] M) {
    StringBuilder sb = new StringBuilder();
    int n = (int) Math.sqrt(M.length);
    int m = 4 * Sudoku.pow(n, 4) + (Sudoku.pow(n, 8) - Sudoku.pow(n, 6)) / 2;
    // (i)
    for (int i = 0; i < M.length; i++) {
      for (int k = 1; k <= M.length; k++) {
        for (int j = 0; j < M.length; j++) {
          sb.append(Sudoku.index(n, i, j, k) + " ");
        }
        sb.append("0\n");
      }
    }
    // (ii)
    for (int j = 0; j < M.length; j++) {
      for (int k = 1; k <= M.length; k++) {
        for (int i = 0; i < M.length; i++) {
          sb.append(Sudoku.index(n, i, j, k) + " ");
        }
        sb.append("0\n");
      }
    }
    // (iii)
    for (int i0 = 0; i0 < n; i0++) {
      for (int j0 = 0; j0 < n; j0++) {
        for (int k = 1; k <= n * n; k++) {
          for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
              sb.append(Sudoku.index(n, i0 * n + i, j0 * n + j, k) + " ");
            }
          }
          sb.append("0\n");
        }
      }
    }
    // (iv)
    for (int i = 0; i < M.length; i++) {
      for (int j = 0; j < M.length; j++) {
        for (int k = 1; k <= M.length; k++) {
          sb.append(Sudoku.index(n, i, j, k) + " ");
        }
        sb.append("0\n");
      }
    }
    // (v)
    for (int i = 0; i < M.length; i++) {
      for (int j = 0; j < M.length; j++) {
        for (int k = 1; k <= M.length; k++) {
          for (int kk = 1; kk <= M.length; kk++) {
            if (k > kk || k == kk)
              continue;
            sb.append(-Sudoku.index(n, i, j, k) + " ");
            sb.append(-Sudoku.index(n, i, j, kk) + " ");
            sb.append("0\n");
          }
        }
      }
    }
    // (add clues)
    for (int i = 0; i < M.length - 1; i++) {
      for (int j = 0; j < M.length - 1; j++) {
        int k = M[i][j];
        if (k != 0) {
          sb.append(Sudoku.index(n, i, j, k) + " 0\n");
          m++;
        }
      }
    }
    sb.insert(0, "p cnf " + Sudoku.pow(n, 6) + " " + m + "\n");
    return sb.toString();
  }

  public static void evaluteCNF(String cnf) throws IOException {
    BufferedReader reader = new BufferedReader(new StringReader(cnf));
    int m = Integer.parseInt(reader.readLine().split(" ")[3]);
    List<List<Integer>> lines = new ArrayList<>();
    for (int i = 0; i < m; i++) {
      List<Integer> us = Arrays
          .stream(reader.readLine().split(" "))
          .map(Integer::parseInt).collect(Collectors.toList());
      lines.add(us);
    }
    // String s = reader.readLine();
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

  public static void main(String[] args) throws Exception {
    int[][] M = new int[][] {
        { 4, 0, 0, 0 }, // { 4, 1, 2, 3 },
        { 2, 0, 1, 0 }, // { 2, 3, 1, 4 },
        { 0, 0, 3, 0 }, // { 1, 4, 3, 2 },
        { 0, 0, 0, 0 }, // { 3, 2, 4, 1 },
    };
    String cnf = sudokuToSAT(M);
    // cnf += "v -1 -2 -3 4 5 -6 -7 -8 -9 10 -11 -12 -13 -14 15 -16 -17 18 -19 -20
    // -21 -22 23 " +
    // "-24 25 -26 -27 -28 -29 -30 -31 32 33 -34 -35 -36 -37 -38 -39 40 -41 -42 43 "
    // +
    // "-44 -45 46 -47 -48 -49 -50 51 -52 -53 54 -55 -56 -57 -58 -59 60 61 -62 -63
    // -64 0";
    System.out.println(cnf);
    // evaluteCNF(cnf);
  }
}