package ai;

/**
 * This class represents the node of the search tree
 *
 * @author Cirun Zhang
 * @version 1.1
 */
class Node {
    /**
     * X coordinate of the last move
     */
    private int x;

    /**
     * Y coordinate of the last move
     */
    private int y;

    /**
     * Score of the node
     */
    private int score;

    /**
     * Chessboard of the board
     */
    private int[][] chess;

    Node(int x, int y, int score, int[][] chess) {
        this.x = x;
        this.y = y;
        this.score = score;
        this.chess = chess;
    }

    int getX() {
        return this.x;
    }

    int getY() {
        return this.y;
    }

    int getScore() {
        return this.score;
    }

    int[][] getChess() {
        return this.chess;
    }

    void setScore(int score) {
        this.score = score;
    }

    int[] getCoordinates() {
        return new int[] {this.x, this.y};
    }

    int[] getCoordinatesAndScore() {
        return new int[] {this.x, this.y, this.score};
    }
}
