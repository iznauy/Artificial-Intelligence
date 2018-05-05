import java.util.*;

/**
 * Created on 2018/5/5.
 * Description:
 *
 * @author iznauy
 */

class CurrentState {

    int[][] grid;

    int prior;

    int step;

    CurrentState parentState;

    CurrentState(int[][] grid, int prior, CurrentState parentState, int step) {
        this.grid = grid;
        this.prior = prior;
        this.parentState = parentState;
        this.step = step;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CurrentState that = (CurrentState) o;

        return Arrays.deepEquals(grid, that.grid);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(grid);
    }

    @Override
    public String toString() {
        return Arrays.toString(grid[0]) + "\n" + Arrays.toString(grid[1]) + "\n"
                 + Arrays.toString(grid[2]) + "\n";
    }
}

public class Astar {

    private int manhattanDistance(int[][] grid) {
        int distance = 0;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                if (grid[i][j] == 0)
                    continue;
                int x = grid[i][j] / 3;
                int y = grid[i][j] - 3 * x;
                distance += Math.abs(x - i) + Math.abs(y - j);
            }
        return distance;
    }

    public static void main(String[] args) {

        int[][] grid = {{7, 2, 4},
                        {5, 0, 6},
                        {8, 3, 1}};

        Astar astar = new Astar();

        CurrentState state = astar.aStar(grid);
        if (state == null) {
            System.out.println("No Answer");
        } else {
            List<CurrentState> stateChanges = new ArrayList<>();
            stateChanges.add(state);

            while (state.parentState != null) {
                state = state.parentState;
                stateChanges.add(state);
            }
            for (int i = stateChanges.size() - 1; i >= 0; i--)
                System.out.println((stateChanges.get(i)));
        }

    }

    private CurrentState aStar(int[][] initGrid) {

        PriorityQueue<CurrentState> queue = new PriorityQueue<>((o1, o2) -> {
            int prior1 = o1.prior;
            int prior2 = o2.prior;
            if (prior1 == prior2) return 0;
            else return prior1 > prior2 ? 1 : -1;
        }); // min heap
        HashSet<CurrentState> visitedStates = new HashSet<>();

        CurrentState currentState = new CurrentState(initGrid, manhattanDistance(initGrid), null, 0);
        queue.add(currentState);

        int[][] actions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        while (!queue.isEmpty()) {
            currentState = queue.poll();
            visitedStates.add(currentState);

            if (currentState.step == currentState.prior)
                return currentState;

            int spaceX = 0, spaceY = 0;
            FIND_SPACE:
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    if (currentState.grid[i][j] == 0) {
                        spaceX = i;
                        spaceY = j;
                        break FIND_SPACE;
                    }

            for (int i = 0; i < 4; i++) {
                // avoid out of bound
                int nextX = actions[i][0] + spaceX;
                int nextY = actions[i][1] + spaceY;
                if (nextX < 0 || nextX >= 3 || nextY < 0 || nextY >= 3)
                    continue;

                // copy array
                int[][] nextGrid = new int[3][3];
                for (int a = 0; a < 3; a++)
                    for (int b = 0; b < 3; b++)
                        nextGrid[a][b] = currentState.grid[a][b];
                nextGrid[spaceX][spaceY] = nextGrid[nextX][nextY];
                nextGrid[nextX][nextY] = 0;

                int distance = manhattanDistance(nextGrid);
                int prior = distance + currentState.step + 1;

                if (currentState.step + 1 > 26)
                    System.out.println("Error");

                CurrentState nextState = new CurrentState(nextGrid, prior, currentState, currentState.step + 1);

                if (visitedStates.contains(nextState))
                    continue;

                queue.add(nextState);

            }
        }
        return null;
    }

}
