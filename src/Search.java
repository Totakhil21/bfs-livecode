import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;

public class Search {

    /**
     * Finds the location of the nearest reachable cheese from the rat's position.
     * Returns a 2‐element int array: [row, col] of the closest 'c'. If there are multiple
     * cheeses tied for the same shortest distance, returns any one of them.
     *
     * 'R' - the rat's starting position (exactly one)
     * 'o' - open space the rat can walk on
     * 'w' - wall the rat cannot pass through
     * 'c' - cheese that the rat wants to reach
     *
     * If no rat is found, throws EscapedRatException.
     * If more than one rat is found, throws CrowdedMazeException.
     * If no cheese is reachable, throws HungryRatException.
     *
     * Example maze:
     *   oooocwco
     *   woowwcwo
     *   ooooRwoo
     *   oowwwooo
     *   oooocooo
     *
     * The method will return [0, 4] as the nearest cheese.
     */
    public static int[] nearestCheese(char[][] maze)
            throws EscapedRatException, CrowdedMazeException, HungryRatException {

        // 1) Find the rat's starting location (and check exception conditions)
        int[] start = ratLocation(maze);

        // 2) Prepare a visited array
        boolean[][] visited = new boolean[maze.length][maze[0].length];

        // 3) Standard BFS queue of (row,col) pairs
        Queue<int[]> queue = new LinkedList<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int curR = current[0];
            int curC = current[1];

            // Skip if already visited
            if (visited[curR][curC]) {
                continue;
            }
            visited[curR][curC] = true;

            // If this cell is cheese, we found the nearest cheese
            if (maze[curR][curC] == 'c') {
                return new int[] { curR, curC };
            }

            // Otherwise, enqueue all valid neighbors
            for (int[] neighbor : getNeighbors(maze, current)) {
                int nr = neighbor[0], nc = neighbor[1];
                if (!visited[nr][nc]) {
                    queue.add(neighbor);
                }
            }
        }

        // If BFS completes without finding any cheese, the rat is hungry
        throw new HungryRatException();
    }


    /**
     * Returns a list of valid neighbor positions (up/down/left/right)
     * that the rat can move into from the current cell. A neighbor is valid if:
     *   - It stays inside the maze bounds
     *   - It is not a wall ('w')
     */
    public static List<int[]> getNeighbors(char[][] maze, int[] current) {
        int curR = current[0];
        int curC = current[1];
        List<int[]> neighbors = new ArrayList<>();

        // Define the four possible directions: up, down, left, right.
        int[][] directions = {
            { -1, 0 }, // up
            {  1, 0 }, // down
            {  0, -1}, // left
            {  0, 1 }  // right
        };

        for (int[] dir : directions) {
            int newR = curR + dir[0];
            int newC = curC + dir[1];

            // Check bounds
            if (newR >= 0 && newR < maze.length && newC >= 0 && newC < maze[0].length) {
                // Only walkable if not a wall
                if (maze[newR][newC] != 'w') {
                    neighbors.add(new int[] { newR, newC });
                }
            }
        }
        return neighbors;
    }


    /**
     * Scans the entire maze to find the rat ('R'). If no rat is present, throws EscapedRatException.
     * If more than one 'R' is present, throws CrowdedMazeException. Otherwise returns the [row, col].
     */
    public static int[] ratLocation(char[][] maze)
            throws EscapedRatException, CrowdedMazeException {

        int[] location = null;

        for (int r = 0; r < maze.length; r++) {
            for (int c = 0; c < maze[0].length; c++) {
                if (maze[r][c] == 'R') {
                    if (location != null) {
                        // We already found one rat before → too many rats
                        throw new CrowdedMazeException();
                    }
                    location = new int[] { r, c };
                }
            }
        }

        if (location == null) {
            // No 'R' found
            throw new EscapedRatException();
        }

        return location;
    }
}
