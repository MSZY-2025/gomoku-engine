package test;

import game.constant.GameConst;
import gui.constant.GuiConst;
import ai.MonteCarlo;
import ai.utility.AiUtils;
import game.AiAnalyser;

import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * This class is used to analyse the performance of the AI agents
 */
public class AgentAnalysis {
    /**
     * This function runs N times games between agent A and B and returns
     * the precent of games when agent A won.
     */
    public static double testWinRatio(int agentA, int agentB, int N)
    {
        int winsa = 0;
        int winsb = 0;
        for(int i = 0; i < N; i++) {
            int[][] testChess = new int[GuiConst.TILE_NUM_PER_ROW][GuiConst.TILE_NUM_PER_ROW];
            AiAnalyser.BattleResult result = AiAnalyser.battle(agentA, agentB, testChess);

            if(result.winningId == agentA) {
                winsa++;
            }
            else {
                winsb++;
            }

            if(i % 10 == 0)
            {
                System.out.printf("| Iter: %d ", i);
                System.out.printf("Wins A: %d  Wins B: %d | ", winsa, winsb);
            }
        }

        System.out.println();
        System.out.printf("Wins A: %d | Wins B: %d | Remisy: %d | Total games: %d%n", winsa, winsb, N-winsa-winsb, N);
        System.out.println();

        return AiUtils.safeDivide(winsa, N);
    }

    public static boolean testIfWinsPercentageTimes(double winRatio, double p0, int N, double pvalue)
    {
        double U = (winRatio - p0) * Math.sqrt(AiUtils.safeDivide(N, winRatio * (1.0 - winRatio)));
        System.out.println(String.format("Test statistic: %f", U));

        NormalDistribution dist = new NormalDistribution();
        double u = dist.inverseCumulativeProbability(1.0 - pvalue);
        System.out.println(String.format("Quantile u_1-pvalue: %f", u));

        // if and only if u_1-pvalue >= U then winRation > p0
        return U >= u;
    }

    public static void main(String[] args){
        int N = 100;

        System.out.println("H1: ");
        if(testIfWinsPercentageTimes(testWinRatio(GameConst.MONTE_CARLO_TREE_SEARCH_WANING_EXPLORATION,
        GameConst.MONTE_CARLO_TREE_SEARCH_STANDARD, N), 0.7, N, 0.1)) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }

        System.out.println("H2: ");
        MonteCarlo.setCFastWins(0.65);
        if(testIfWinsPercentageTimes(testWinRatio(GameConst.MONTE_CARLO_TREE_SEARCH_FAST_WINS,
        GameConst.MONTE_CARLO_TREE_SEARCH_STANDARD, N), 0.60, N, 0.1)) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }

        // TODO: beta
        System.out.println("H3: ");
        if(testIfWinsPercentageTimes(testWinRatio(GameConst.MONTE_CARLO_TREE_SEARCH_STANDARD,
        GameConst.MONTE_CARLO_TREE_SEARCH_EXPLORATION_BIASED, N), 0.65, N, 0.1)) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }

        System.out.println("H4: ");
        MonteCarlo.setCFastWins(0.8);
        if(testIfWinsPercentageTimes(testWinRatio(GameConst.MONTE_CARLO_TREE_SEARCH_WANING_EXPLORATION,
        GameConst.MONTE_CARLO_TREE_SEARCH_FAST_WINS, N), 0.7, N, 0.1)) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }
    }
}
