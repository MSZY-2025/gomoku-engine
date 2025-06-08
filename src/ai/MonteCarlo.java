package ai;

import ai.constant.AiConst;
import ai.utility.AiUtils;
import gui.Background;
import gui.constant.GuiConst;
import observer.GameStatusChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class is an AI agent uses Monte Carlo tree search
 *
 * @author Cirun Zhang
 * @version 1.1
 */
public class MonteCarlo extends Agent {
    /**
     * Counter for MCTS
     */
    private static double c = 1.1;
    private static double cFastWins = 0.6;

    public static void setC(double c) {
        MonteCarlo.c = c;
    }

    public static void setCFastWins(double cFastWins) {
        MonteCarlo.cFastWins = cFastWins;
    }

    private static int iteration;

    public static void tester(int[][] chess, SelectionType type) {
        iteration = 0;
        TreeNode root = new TreeNode(true, aiPieceType * -1, -1, -1, chess, null);
        while (iteration < 30000) {
            selection(root, type);
        }

        List<TreeNode> children = root.getChildren();
        int maxVisits = Integer.MIN_VALUE;
        int max_x = -1;
        int max_y = -1;
        for (TreeNode child : children) {
            if (child.getVisitsCount() > maxVisits) {
                maxVisits = child.getVisitsCount();
                max_x = child.getX();
                max_y = child.getY();
            }
        }

        System.out.println(max_x + "===" + max_y);
    }

    /**
     * Entrance of MCTS
     *
     * @param chess 2-dimensional array represents the chessboard
     * @return Position of the next move
     */
    public static int[] monteCarloTreeSearch(int[][] chess, SelectionType type) {
        Background.addMessage("Doing MCTS, please wait..");
        iteration = 0;

        TreeNode root = new TreeNode(true, aiPieceType * -1, -1, -1, chess, null);
        //execute MCTS for 10000 times
        while (iteration < 10000) {
            selection(root, type);
        }

        List<TreeNode> children = root.getChildren();
        int maxVisits = Integer.MIN_VALUE;
        int max_x = -1;
        int max_y = -1;
        for (TreeNode child : children) {
            if (child.getVisitsCount() > maxVisits) {
                maxVisits = child.getVisitsCount();
                max_x = child.getX();
                max_y = child.getY();
            }
        }

        System.err.println(root.getReward() + "-" + root.getVisitsCount());
        System.err.println(max_x + "===" + max_y);
        System.err.println(maxVisits);
        return new int[] {max_x, max_y, aiPieceType};
    }

    /**
     * Selection process of MCTS
     *
     * @param root The node for process selection, initially the node is set to the root
     */
    private static void selection(TreeNode root, SelectionType type) {
        if (root.isLeaf()) {
            if (root.getVisitsCount() == 0 || root.isFinal()) {
                rollout(root);
            } else {
                expansion(root, type);
            }
        } else {
            List<TreeNode> children = root.getChildren();
            TreeNode best = ucbSelection(children, type);
            if (best != null) {
                selection(best, type);
            } else {
                System.out.println("null");
            }

        }
    }

    /**
     * Expansion process of MCTS
     *
     * @param node The leaf node need to be expanded
     */
    private static void expansion(TreeNode node, SelectionType type) {
        List<TreeNode> children = generatesChildren(node);
        node.setChildren(children);
        boolean isLeaf = children == null || children.size() == 0;
        node.setLeaf(isLeaf);
        // If expanded node has no children, then it is final one
        node.setFinal(isLeaf);
        selection(node, type);
    }

    /**
     * Rollout process of MCTS. The rollout only stops when the simulated game is terminated
     *
     * @param node The node need to be simulated
     */
    private static void rollout(TreeNode node) {
        iteration++;
        int numOfMoves = 0;
        int[][] chess = AiUtils.copyArray(node.getChess());
        int lastTurnPlayer = node.getThisTurnPlayer();
        PossibleMove randomMove;

        do {
            lastTurnPlayer *= -1;
            numOfMoves++;
            randomMove = getRandomMove(chess);
            if (randomMove == null) {
                System.err.println("randomMove == null");
                break;
            }
            placePiece(chess, randomMove, lastTurnPlayer);
        } while (!GameStatusChecker.isFiveInLine(chess, randomMove.getX(), randomMove.getY()));

        //back propagation
        //we assume that a fast game takes at most number of moves equal to the half of the size of the board
        backPropagation(node, 1, lastTurnPlayer, numOfMoves <= node.getMaxHeight() / 2);
    }


    /**
     * Back propagation process of MCTS
     *
     * @param node         The back propagated node
     * @param reward       The reward for winning nodes
     * @param winningPiece Indicates which player wins
     */
    private static void backPropagation(TreeNode node, int reward, int winningPiece, boolean fastGame) {
        if (node != null) {
            if (node.getThisTurnPlayer() == winningPiece) {
                node.increaseReward(reward);
                if(fastGame) {
                    node.increaseFastWinsCount();
                }
            } else {
                node.increaseReward(0);
            }
            node.increaseVisitCount();
            backPropagation(node.getParent(), reward, winningPiece, fastGame);
        }
    }

    /**
     * UCB-1 function of MCTS, it is used to balance the visit count and win count
     *
     * @param node Calculates the UCB value for this particular node
     * @return UCB value
     */
    private static double ucb1(TreeNode node, boolean isWaining, boolean isFastWins) {
        double c_local = c;
        if(isWaining) {
            int height = node.getHeight();
            int maxHeight = node.getMaxHeight();
            c_local *= AiUtils.safeDivide(maxHeight - height, maxHeight);
        }
        int reward = node.getReward();
        int visitCount = node.getVisitsCount();
        int parentVisitCount = node.getParent().getVisitsCount();
        double exploration = c_local * Math.sqrt(AiUtils.safeDivide(Math.log(parentVisitCount), visitCount));
        double exploitation;
        if(isFastWins) {
            int fastWinsCount = node.getFastWinsCount();
            cFastWins = AiUtils.Truncate(cFastWins, 0.0, 1.0);
            exploitation =
                 AiUtils.safeDivide(cFastWins * fastWinsCount + (1.0 - cFastWins) * (reward - fastWinsCount), visitCount);
        } else {
            exploitation = AiUtils.safeDivide(reward, visitCount);
        }
        return exploitation + exploration;
    }

    public enum SelectionType {
        STANDARD,
        WANING_EXPLORATION,
        FAST_WINS,
        HEURISTICS
    }

    private static double selectionFactor(TreeNode node, SelectionType type) {
        switch(type) {
            case STANDARD:
                return ucb1(node, false, false);
            case WANING_EXPLORATION:
                return ucb1(node, true, false);
            case FAST_WINS:
                return ucb1(node, false, true);
            case HEURISTICS:
            /* TODO */
            break;
        }

        return 0.0;
    }

    /**
     * Selects the child node with the highest UCB value
     *
     * @param children The child nodes
     * @return The best node
     */
    private static TreeNode ucbSelection(List<TreeNode> children, SelectionType type) {
        double max = Double.NEGATIVE_INFINITY;
        TreeNode best = null;

        for (TreeNode child : children) {
            double ucbVal = selectionFactor(child, type);
            if (ucbVal > max) {
                max = ucbVal;
                best = child;
            }

            if (max == Double.POSITIVE_INFINITY) {
                return best;
            }
        }

        if (best == null) {
            System.out.println(selectionFactor(children.get(0), type));
        }
        return best;
    }

    /**
     * Generates 10 child nodes for a parent node
     * @param node Parent node
     * @return  Child nodes
     */
    private static List<TreeNode> generatesChildren(TreeNode node) {
        List<TreeNode> children = new ArrayList<>();

        int nextTurnPlayer = node.getThisTurnPlayer() * -1;
        int[][] chess = node.getChess();

        //Generates 10 child nodes
        List<int[]> moves = AiUtils.moveGeneratorWithHeuristicSort(chess, 10);

        for (int[] move : moves) {
            int x = move[0];
            int y = move[1];
            int[][] nextChess = AiUtils.nextMoveChessboard(chess, x, y, nextTurnPlayer);
            boolean isTerminal = GameStatusChecker.isFiveInLine(nextChess, x, y);

            children.add(new TreeNode(true, nextTurnPlayer, x, y, nextChess, node));

            if(isTerminal){
                backPropagation(node, 1, nextTurnPlayer, node.getHeight() <= node.getMaxHeight() / 2);
            }
        }

        return children;
    }

    /**
     * Randomly choose a move
     * @param chess The chessboard
     * @return A randomly chosen move
     */
    private static PossibleMove getRandomMove(int[][] chess) {
        List<PossibleMove> possibleMoves = generatesMoves(chess);
        int size = possibleMoves.size();

        if (size == 0) {
            System.err.println("Chess board full");
            return null;
        }

        int randomIndex = ThreadLocalRandom.current().nextInt(0, size);
        return possibleMoves.get(randomIndex);
    }

    /**
     * Generates all possible legal game moves.
     * @param chess The chessboard
     * @return All possible moves.
     */
    private static List<PossibleMove> generatesMoves(int[][] chess) {
        List<PossibleMove> possibleMoves = new ArrayList<>();
        for (int i = 0; i < GuiConst.TILE_NUM_PER_ROW; i++) {
            for (int j = 0; j < GuiConst.TILE_NUM_PER_ROW; j++) {
                if (chess[i][j] == AiConst.EMPTY_STONE) {
                    possibleMoves.add(new PossibleMove(i, j));
                }
            }
        }
        return possibleMoves;
    }

    /**
     * Place piece on the chessboard
     * @param chess The chessboard
     * @param move The location of placing place
     * @param pieceType Type of placed piece
     */
    private static void placePiece(int[][] chess, PossibleMove move, int pieceType) {
        chess[move.getX()][move.getY()] = pieceType;
    }

}

/**
 * This class is used to encapsulate the piece-placing location information
 *
 * @author Cirun Zhang
 * @version 1.0
 */
class PossibleMove {
    private int x;
    private int y;

    PossibleMove(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}

/**
 * This class represents the node of MCT
 *
 * @author Cirun Zhang
 * @version 1.0
 */
class TreeNode {
    private boolean isLeaf;

    private boolean isTerminal;

    private int thisTurnPlayer;

    private int x;

    private int y;

    private int[][] chess;

    private int reward = 0;

    private int visitsCount = 0;

    private TreeNode parent;

    private List<TreeNode> children;

    // extendend fields

    private int height;

    private int fastWinsCount = 0;

    private boolean isFinal = false;

    public TreeNode(int[][] chess) {
        this.chess = chess;
        // extended
        InitExtendedFields(null);
    }

    public TreeNode(boolean isLeaf, int[][] chess) {
        this.isLeaf = isLeaf;
        this.chess = chess;
        // extended
        InitExtendedFields(null);
    }

    public TreeNode(boolean isLeaf, int thisTurnPlayer, int x, int y, int[][] chess, TreeNode parent) {
        this.isLeaf = isLeaf;
        this.thisTurnPlayer = thisTurnPlayer;
        this.x = x;
        this.y = y;
        this.chess = chess;
        this.parent = parent;
        // extended
        InitExtendedFields(parent);
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public int[][] getChess() {
        return chess;
    }

    public void setChess(int[][] chess) {
        this.chess = chess;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public int getVisitsCount() {
        return visitsCount;
    }

    public void setVisitsCount(int visitsCount) {
        this.visitsCount = visitsCount;
    }

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public boolean isTerminal() {
        return isTerminal;
    }

    public void setTerminal(boolean terminal) {
        isTerminal = terminal;
    }

    public int getThisTurnPlayer() {
        return thisTurnPlayer;
    }

    public void setThisTurnPlayer(int thisTurnPlayer) {
        this.thisTurnPlayer = thisTurnPlayer;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void increaseReward(int reward) {
        this.reward += reward;
    }

    public void increaseVisitCount() {
        this.visitsCount += 1;
    }

    // extended

    private void InitExtendedFields(TreeNode parent) {
        this.height = parent == null ? 0 : parent.height + 1;
    }

    public int getHeight() {
        return height;
    }

    public int getMaxHeight() {
        return chess.length * chess[0].length;
    }

    public int getFastWinsCount() {
        return fastWinsCount;
    }

    public void increaseFastWinsCount() {
        this.fastWinsCount++;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

}
