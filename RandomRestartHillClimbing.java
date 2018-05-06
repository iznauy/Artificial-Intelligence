import java.util.Random;

/**
 * Created on 2018/5/6.
 * Description: 随机重启爬山法寻找n皇后问题的解
 *
 * @author iznauy
 */
public class RandomRestartHillClimbing {

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

    private static Pair<int[][], Integer> randomRestartHillClimbing(int n) {
        // you may not found the optimal in one time

        int step = 0;

        while (true) {
            int[][] grid = new int[n][n];

            // init grid
            Random generator = new Random();
            for (int i = 0; i < n; i++)
                grid[i][generator.nextInt(n)] = 1; // each row contains one queen

            int value = evaluate(grid);

            while (true) {
                if (value == 0)
                    return new Pair<>(grid, step);

                int minX = -1, minY = -1;

                for (int i = 0; i < n; i++) {
                    int pos = -1;
                    for (int j = 0; j < n; j++)
                        if (grid[i][j] == 1) {
                            pos = j;
                            break;
                        }
                    for (int j = 0; j < n; j++) {
                        grid[i][pos] = 0;
                        grid[i][j] = 1;
                        int curr = evaluate(grid);
                        if (curr < value) {
                            value = curr;
                            minX = i;
                            minY = j;
                        }
                        grid[i][j] = 0;
                        grid[i][pos] = 1;
                    }
                }

                if (minX == -1)
                    break; // restart

                step += 1; // count step

                for (int j = 0; j < n; j++)
                    grid[minX][j] = 0;
                grid[minX][minY] = 1;
            }
        }

    }

    public static void main(String[] args) {
        int totalStep = 0;
        for (int i = 0; i < 1000; i++)
            totalStep += randomRestartHillClimbing(8).r;
        System.out.println("RandomRestartHillClimbing Average Step: " + (1.0 * totalStep / 1000));
    }

}
