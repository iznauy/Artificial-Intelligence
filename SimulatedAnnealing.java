import java.util.Arrays;
import java.util.Random;

/**
 * Created on 2018/5/6.
 * Description: 模拟退火寻找n皇后问题的解
 *
 * @author iznauy
 */
public class SimulatedAnnealing {

    static class Pair<T, R> {
        T t;
        R r;

        Pair(T t, R r) {
            this.t = t;
            this.r = r;
        }
    }

    private static int evaluate(int[][] grid) {
        int size = grid.length;
        int conflicts = 0;
        for (int i = 0; i < size - 1; i++) { // queen in last row don't need to extra calc
            int pos = -1;
            for (int j = 0; j < size; j++)
                if (grid[i][j] == 1) {
                    pos = j;
                    break;
                }
            for (int j = i + 1; j < size; j++) {
                conflicts += grid[j][pos]; // in the same column
                if (pos - j + i >= 0)
                    conflicts += grid[j][pos - j + i]; // in left diagonal
                if (pos + j - i < size)
                    conflicts += grid[j][pos + j - i];
            }
        }
        return conflicts;
    }

    private static Pair<int[][], Integer> simulatedAnnealing(int n, int maxStep) {
        // you may not found the optimal in one time
        int[][] grid = new int[n][n];

        // init grid
        Random generator = new Random();
        for (int i = 0; i < n; i++)
            grid[i][generator.nextInt(n)] = 1; // each row contains one queen

        int value = evaluate(grid);

        double T = 0.3;

        for (int t = 0; t < maxStep; t++) {
            if (value == 0) {
                return new Pair<>(grid, t);
            }
            T *= 0.99;
            int x = generator.nextInt(n);
            int y = generator.nextInt(n);
            if (grid[x][y] == 1)
                continue;
            int pos = -1;
            for (int j = 0; j < n; j++)
                if (grid[x][j] == 1) {
                    pos = j;
                    break;
                }
            grid[x][y] = 1;
            grid[x][pos] = 0;
            int currentValue = evaluate(grid);
            if (currentValue <= value)
                value = currentValue;
            else {
                double prob = Math.exp((value - currentValue) * 1.0 / T);
                double roll = generator.nextDouble();
                if (roll > prob) {
                    grid[x][y] = 0;
                    grid[x][pos] = 1;
                }
            }

        }
        return null;
    }

    public static void main(String[] args) {
        int count = 0;
        int totalStep = 0;
        for (int i = 0; i < 1000; i++) {
            Pair<int[][], Integer> pair = simulatedAnnealing(8, 1000);
            if (pair != null) {
                count += 1;
                totalStep += pair.r;
            }
        }
        System.out.println("SimulatedAnnealing[success/total]: " + count + "/1000");
        System.out.println("Average Step: " + totalStep * 1.0 / count);
    }

}
