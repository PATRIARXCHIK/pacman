public class Maze {
    public static final String[] DATA = {
        "###################",
        "#........#........#",
        "#.##.###.#.###.##.#",
        "#o##.###.#.###.##o#",
        "#.................#",
        "#.##.#.#####.#.##.#",
        "#....#...#...#....#",
        "####.###.#.###.####",
        "#......#...#......#",
        "#.####.#   #.####.#",
        "#......#   #......#",
        "#.####.#####.####.#",
        "#......#...#......#",
        "####.###.#.###.####",
        "#....#...#...#....#",
        "#.##.#.#####.#.##.#",
        "#.................#",
        "#.##.###.#.###.##.#",
        "#o...............o#",
        "#.##.#.#####.#.##.#",
        "#....#...#...#....#",
        "#........#........#",
        "###################"
    };

    public static char[][] createGrid() {
        char[][] grid = new char[GameConfig.ROWS][];
        for (int r = 0; r < GameConfig.ROWS; r++) grid[r] = DATA[r].toCharArray();
        return grid;
    }

    public static int countPellets(char[][] grid) {
        int count = 0;
        for (int r = 0; r < GameConfig.ROWS; r++)
            for (int c = 0; c < GameConfig.COLS; c++)
                if (grid[r][c] == '.' || grid[r][c] == 'o') count++;
        return count;
    }
}
