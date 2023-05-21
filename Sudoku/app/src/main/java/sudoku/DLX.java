package sudoku;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DLX {
  public static int[][] sudokoToExactCover(int[][] sudoku, int n) {
    int m = Sudoku.pow(n, 4);
    int[][] M = new int[Sudoku.pow(n, 6)][4 * Sudoku.pow(n, 4)];
    for (int i = 0; i < n * n; i++) {
      for (int j = 0; j < n * n; j++) {
        if (sudoku[i][j] != 0) {
          int k = sudoku[i][j] - 1;
          int row = Sudoku.index(n, i, j, k);
          M[row][i * n * n + k] = 1;
          M[row][j * n * n + k + m] = 1;
          M[row][((i / n) * n + (j / n)) * n * n + k + 2 * m] = 1;
          M[row][j + i * n * n + 3 * m] = 1;
          continue;
        }
        for (int k = 0; k < n * n; k++) {
          int row = Sudoku.index(n, i, j, k);
          M[row][i * n * n + k] = 1;
          M[row][j * n * n + k + m] = 1;
          M[row][((i / n) * n + (j / n)) * n * n + k + 2 * m] = 1;
          M[row][j + i * n * n + 3 * m] = 1;
        }
      }
    }
    return M;
  }

  public static void printExactCover(int[][] M) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < M.length; i++) {
      for (int j = 0; j < M[0].length; j++) {
        sb.append(M[i][j]);
      }
      sb.append("\n");
    }
    System.out.println(sb.toString());
  }

  public static class Node {
    public Node left = this;
    public Node right = this;
    public Node up = this;
    public Node down = this;
    public ColumnNode col = null;
  }

  public static class ColumnNode extends Node {
    public String name = null;
    public int size = 0;

    public ColumnNode(String name) {
      this.name = name;
      this.col = this;
    }
  }

  public static void unlinkLeftRight(Node x) {
    x.left.right = x.right;
    x.right.left = x.left;
  }

  public static void unlinkUpDown(Node x) {
    x.up.down = x.down;
    x.down.up = x.up;
  }

  public static void linkLeftRight(Node x) {
    x.right.left = x;
    x.left.right = x;
  }

  public static void linkUpDown(Node x) {
    x.down.up = x;
    x.up.down = x;
  }

  public static ColumnNode choose(ColumnNode root) {
    int size = Integer.MAX_VALUE;
    ColumnNode chosenCol = null;
    for (ColumnNode col = (ColumnNode) root.right; col != root; col = (ColumnNode) col.right) {
      if (size > col.size) {
        chosenCol = col;
        size = col.size;
      }
    }
    return chosenCol;
  }

  public static void cover(ColumnNode col) {
    unlinkLeftRight(col);
    for (Node i = col.down; i != col; i = i.down) {
      for (Node j = i.right; j != i; j = j.right) {
        unlinkUpDown(j);
        j.col.size--;
      }
    }
  }

  public static void uncover(ColumnNode col) {
    for (Node i = col.up; i != col; i = i.up) {
      for (Node j = i.left; j != i; j = j.left) {
        j.col.size++;
        linkUpDown(j);
      }
    }
    linkLeftRight(col);
  }

  public static void print(Deque<Node> cover, boolean sout) {
    int n = cover.size();
    StringBuilder sb = new StringBuilder();
    while (cover.size() != 0) {
      Node node = cover.pop();
      Node j = node;
      do {
        sb.append(j.col.name + " ");
        j = j.right;
      } while (j != node);
      sb.append("\n");
    }
    // System.out.println(sb.toString());
    exactCoverToSudoku(sb.toString(), n, sout);
  }

  public static int searchCount(ColumnNode root, Deque<Node> cover) {
    int count = 0;
    if (root.right == root) {
      return 1;
    }
    ColumnNode col = choose(root);
    cover(col);
    for (Node r = col.down; r != col; r = r.down) {
      cover.push(r);
      for (Node j = r.right; j != r; j = j.right) {
        cover(j.col);
      }
      count += searchCount(root, cover);
      if (cover.isEmpty())
        return count;
      cover.pop();
      for (Node j = r.left; j != r; j = j.left) {
        uncover(j.col);
      }
    }
    uncover(col);
    return count;
  }

  public static boolean search(ColumnNode root, Deque<Node> cover, boolean sout) {
    if (root.right == root) {
      print(cover, sout);
      return true;
    }
    ColumnNode col = choose(root);
    cover(col);
    for (Node r = col.down; r != col; r = r.down) {
      cover.push(r);
      for (Node j = r.right; j != r; j = j.right) {
        cover(j.col);
      }
      search(root, cover, sout);
      if (cover.isEmpty())
        return true;
      cover.pop();
      for (Node j = r.left; j != r; j = j.left) {
        uncover(j.col);
      }
    }
    uncover(col);
    return false;
  }

  public static Deque<Node> sudokuToDancingLinks(int[][] M) {
    Deque<Node> nodes = new ArrayDeque<>();
    ColumnNode root = new ColumnNode("root");
    nodes.push(root);
    // Create header nodes
    for (int i = 0; i < M[0].length; i++) {
      ColumnNode col = new ColumnNode("" + (i + 1));
      col.left = root.left;
      col.right = root;
      root.left.right = col;
      root.left = col;
      nodes.add(col);
    }
    // Create matrix nodes
    for (int i = 0; i < M.length; i++) {
      ColumnNode col = (ColumnNode) root.right;
      Node prev = null;
      for (int j = 0; j < M[0].length; j++) {
        if (M[i][j] != 0) {
          Node node = new Node();
          // Hook up/down
          node.col = col;
          node.down = col;
          node.up = col.up;
          col.up.down = node;
          col.up = node;
          // Hook left/right
          if (prev != null) {
            node.left = prev;
            node.right = prev.right;
            prev.right.left = node;
            prev.right = node;
          }
          prev = node;
          col.size++;
        }
        col = (ColumnNode) col.right;
      }
    }
    return nodes;
  }

  public static ColumnNode findRoot(Deque<Node> nodes) {
    ColumnNode root = nodes.peek().col;
    while (!root.name.equals("root")) {
      root = (ColumnNode) root.right;
    }
    return root;
  }

  public static void exactCoverToSudoku(String cover, int n, boolean sout) {
    BufferedReader reader = new BufferedReader(new StringReader(cover));
    int N = n;
    int m = N;
    n = (int) Math.sqrt(n);
    int[][] M = new int[n][n];
    while (N-- > 0) {
      int[] abcd = new int[4];
      try {
        String[] line = reader.readLine().split(" ");
        for (int i = 0; i < line.length; i++) {
          abcd[i] = Integer.parseInt(line[i]);
        }
        Arrays.sort(abcd);
      } catch (IOException e) {
        e.printStackTrace();
      }
      int a = abcd[0] - 1;
      int b = abcd[1] - 1;
      int i = a / n;
      int j = (b - m) / n;
      int k = a % n + 1;
      if (i > M.length - 1 || j > M[0].length - 1) {
        // if (sout)
        // Sudoku.prettyPrintSudoku(M);
        return;
      }
      M[i][j] = k;
    }
    if (sout)
      Sudoku.prettyPrintSudoku(M);
  }

  public static int[][] generateSudoku(int n, int seed) {
    Random rand = new Random(seed);
    int[][] M = new int[n * n][n * n];
    int count = Sudoku.pow(n, 4);
    // Pre-fill (if bigger enough)
    if (n > 2) {
      List<Integer> nums = IntStream.range(1, n * n + 1).boxed().collect(Collectors.toList());
      for (int k = 0; k < n; k++) {
        int idx = 0;
        Collections.shuffle(nums, rand);
        for (int i = k * n; i < k * n + n; i++) {
          for (int j = k * n; j < k * n + n; j++) {
            M[i][j] = nums.get(idx++);
          }
        }
      }
      count -= Sudoku.pow(n, 3);
    }
    // Fill the board
    rand = new Random(seed);
    int i = 0, j = 0, k = 0;
    ColumnNode root = null;
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
        int[][] BM = sudokoToExactCover(M, n);
        Deque<Node> nodes = sudokuToDancingLinks(BM);
        root = findRoot(nodes);
      } while (!search(root, new ArrayDeque<Node>(), false));
    }
    // Remove as many as possible
    do {
      count++;
      do {
        i = rand.nextInt(n * n);
        j = rand.nextInt(n * n);
      } while (M[i][j] == 0);
      k = M[i][j];
      M[i][j] = 0;
      int[][] BM = sudokoToExactCover(M, n);
      Deque<Node> nodes = sudokuToDancingLinks(BM);
      root = findRoot(nodes);
    } while (searchCount(root, new ArrayDeque<Node>()) == 1);
    M[i][j] = k;
    return M;
  }

  public static void dlx(int[][] sudoku) {
    int n = (int) Math.sqrt(sudoku.length);
    int[][] M = sudokoToExactCover(sudoku, n);
    Deque<Node> nodes = sudokuToDancingLinks(M);
    search(findRoot(nodes), new ArrayDeque<Node>(), true);
  }
}
