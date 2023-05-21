package sudoku;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Backtrack {

  public static boolean isSolved(int[][] M) {
    int n = (int) Math.sqrt(M.length);
    for (int i = 0; i < n * n; i++) {
      for (int j = 0; j < n * n; j++) {
        int k = M[i][j];
        M[i][j] = 0;
        if (!Sudoku.canPlace(M, i, j, k)) {
          return false;
        }
        M[i][j] = k;
      }
    }
    return true;
  }

  public static boolean backtrack(int[][] M, int i, int j) {
    int n = (int) Math.sqrt(M.length);
    // Find an empty square
    int[] ij = Sudoku.findEmptySquare(M, i, j);
    i = ij[0];
    j = ij[1];
    // Check if solved
    if (i == n * n) {
      return true;
    }
    // Find suitable number for square
    for (int k = 1; k <= n * n; k++) {
      if (Sudoku.canPlace(M, i, j, k)) {
        M[i][j] = k;
        ij = Sudoku.findEmptySquare(M, i, j);
        int jj = ij[1], ii = ij[0];
        if (backtrack(M, ii, jj)) {
          return true;
        }
        M[i][j] = 0;
      }
    }
    return false;
  }

  public static int backtrackCount(int[][] M, int i, int j) {
    int count = 0;
    int n = (int) Math.sqrt(M.length);
    // Find an empty square
    int[] ij = Sudoku.findEmptySquare(M, i, j);
    i = ij[0];
    j = ij[1];
    // Check if solved
    if (i == n * n) {
      return 1;
    }
    // Find suitable number for square
    for (int k = 1; k <= n * n; k++) {
      if (Sudoku.canPlace(M, i, j, k)) {
        M[i][j] = k;
        ij = Sudoku.findEmptySquare(M, i, j);
        int ii = ij[0], jj = ij[1];
        count += backtrackCount(M, ii, jj);
        M[i][j] = 0;
      }
    }
    return count;
  }

  public static int[][] generateSudoku(int n, long seed) {
    Random rand = new Random(seed);
    int[][] M = new int[n * n][n * n];
    int count = Sudoku.pow(n, 4);
    // Pre-fill (for large enough input sizes)
    if (n > 2) {
      List<Integer> nums = IntStream.range(1, n * n + 1).boxed().collect(Collectors.toList());
      for (int k = 0; k < n; k++) {
        int idx = 0;
        Collections.shuffle(nums, new Random(seed));
        for (int i = k * n; i < k * n + n; i++) {
          for (int j = k * n; j < k * n + n; j++) {
            M[i][j] = nums.get(idx++);
          }
        }
      }
      count -= Sudoku.pow(n, 3);
    }
    // Fill the board
    int i = 0, j = 0, k = 0;
    while (count-- > 0) {
      int[] ij = Sudoku.findEmptySquare(M, i, j);
      i = ij[0];
      j = ij[1];
      do {
        k = rand.nextInt(n * n) + 1;
        while (!Sudoku.canPlace(M, i, j, k)) {
          k = rand.nextInt(n * n) + 1;
        }
        M[i][j] = k;
      } while (!backtrack(Sudoku.copy(M), i, j));
    }
    // Remove as many as possible
    do {
      do {
        i = rand.nextInt(n * n);
        j = rand.nextInt(n * n);
      } while (M[i][j] == 0);
      k = M[i][j];
      M[i][j] = 0;
    } while (backtrackCount(Sudoku.copy(M), 0, 0) == 1);
    M[i][j] = k;
    return M;
  }
}
