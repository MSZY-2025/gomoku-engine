package game;

import ai.*;
import ai.MonteCarlo.SelectionType;
import ai.constant.AiConst;
import game.constant.GameConst;
import observer.GameStatusChecker;
import observer.HistoryObserver;

/**
 * This class is used to analyse various AI agents
 *
 * @author Cirun Zhang
 * @version 1.0
 */
public class AiAnalyser {
    private AiAnalyser() {
    }

    public static class BattleResult {
        public int moveCount;
        public int winningId;

        public BattleResult(int moveCount, int winningId) {
            this.moveCount = moveCount;
            this.winningId = winningId;
        }
    }

    /**
     * Starts game battle with AI agent A(black) and AI agent B(white), agent A would move first
     *
     * @param agentA Index of agent A
     * @param agentB Index of agent B
     * @param chess  The chessboard of the battle
     * @return Game moves
     */
    public static BattleResult battle(int agentA, int agentB, int[][] chess) {
        //Agent A move first
        int moveCount = 0;
        int maxIter = chess.length * chess[0].length;
        while (moveCount < maxIter) {
            System.err.println("Move " + moveCount++);
            Agent.aiPieceType = AiConst.BLACK_STONE;
            if (aiMove(agentA, chess)) {
                printBattleInfo(agentA, true);
                return new BattleResult(moveCount, agentA);
            }

            System.err.println("Move " + moveCount++);
            Agent.aiPieceType = AiConst.WHITE_STONE;
            if (aiMove(agentB, chess)) {
                printBattleInfo(agentB, false);
                return new BattleResult(moveCount, agentB);
            }
        }
        return new BattleResult(moveCount, -10);
    }

    /**
     * Simulates AI moves
     *
     * @param agent Index of used agent
     * @param chess The chessboard
     * @return A boolean indicates whether the game is end
     */
    private static boolean aiMove(int agent, int[][] chess) {
        int[] result;
        switch (agent) {
            case GameConst.BENCH_MARKER:
                result = AiBenchMarker.nextMove(chess);
                break;
            case GameConst.BEST_FIRST:
                result = GreedyBestFirst.nextMove(chess);
                break;
            case GameConst.MINIMAX:
                result = MinimaxAbp.startMiniMax(chess);
                break;
            case GameConst.ALPHA_BETA_PRUNING:
                result = MinimaxAbp.startAlphaBetaPruningWithSort(chess);
                break;
            case GameConst.TRANSPOSITION_SEARCH:
                result = Transposition.startTranspositionSearch(chess);
                break;
            case GameConst.KILLER_HEURISTIC:
                result = KillerHeuristic.killerAbp(chess);
                break;
            case GameConst.THREAT_SPACE_SEARCH:
                result = ThreatSpace.startThreatSpaceSearch(chess);
                break;
            case GameConst.MONTE_CARLO_TREE_SEARCH_STANDARD:
                result = MonteCarlo.monteCarloTreeSearch(chess, SelectionType.STANDARD);
                break;
            case GameConst.MONTE_CARLO_TREE_SEARCH_WANING_EXPLORATION:
                result = MonteCarlo.monteCarloTreeSearch(chess, SelectionType.WANING_EXPLORATION);
                break;
            case GameConst.MONTE_CARLO_TREE_SEARCH_FAST_WINS:
                result = MonteCarlo.monteCarloTreeSearch(chess, SelectionType.FAST_WINS);
                break;
            case GameConst.MONTE_CARLO_TREE_SEARCH_HEURISTICS:
                result = MonteCarlo.monteCarloTreeSearch(chess, SelectionType.HEURISTICS);
                break;
            default:
                System.err.println("Invalid Ai Index");
                return false;
        }
        int x = result[0];
        int y = result[1];
        int place = result[2];
        chess[x][y] = place;
        HistoryObserver.addHistory(result);
        return GameStatusChecker.isFiveInLine(chess, x, y);
    }

    /**
     * Print the battle result
     *
     * @param winingAgent Index of wining agent
     * @param isMoveFirst A boolean indicates whether the wining agent moved first
     */
    private static void printBattleInfo(int winingAgent, boolean isMoveFirst) {
        System.out.println("==================================");
        switch (winingAgent) {
            case GameConst.BENCH_MARKER:
                System.out.println("BENCH_MARKER wins");
                System.out.println("First move: " + isMoveFirst);
                break;
            case GameConst.BEST_FIRST:
                System.out.println("BEST_FIRST wins");
                System.out.println("First move: " + isMoveFirst);
                break;
            case GameConst.MINIMAX:
                System.out.println("MINIMAX wins");
                System.out.println("First move: " + isMoveFirst);
                break;
            case GameConst.ALPHA_BETA_PRUNING:
                System.out.println("ABP wins");
                System.out.println("First move: " + isMoveFirst);
                break;
            case GameConst.TRANSPOSITION_SEARCH:
                System.out.println("TRANSPOSITION_SEARCH wins");
                System.out.println("First move: " + isMoveFirst);
                break;
            case GameConst.KILLER_HEURISTIC:
                System.out.println("KILLER_HEURISTIC ABP wins");
                System.out.println("First move: " + isMoveFirst);
                break;
            case GameConst.THREAT_SPACE_SEARCH:
                System.out.println("THREAT_SPACE_SEARCH wins");
                System.out.println("First move: " + isMoveFirst);
                break;
            default:
                System.err.println("Invalid Ai Index");
        }
        System.out.println("==================================");
    }
}
