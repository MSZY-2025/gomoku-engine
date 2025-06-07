package test;

import game.constant.GameConst;
import gui.constant.GuiConst;
import ai.MonteCarlo;
import ai.constant.AiConst;
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
        int wins = 0;
        for(int i = 0; i < N; i++) {
            int[][] testChess = new int[GuiConst.TILE_NUM_PER_ROW][GuiConst.TILE_NUM_PER_ROW];
            AiAnalyser.BattleResult result = AiAnalyser.battle(agentA, agentB, testChess);
            if(result.winningId == agentA) {
                wins++;
            }
        }

        return AiUtils.safeDivide(wins, N);
    }

    public static boolean testIfWinsPercentageTimes(double winRatio, double p0, int N, double pvalue)
    {
        double U = (winRatio - p0) / Math.sqrt(AiUtils.safeDivide(winRatio * (1.0 - winRatio), N));
        System.out.print(String.format("Test statistic: %f", U));

        NormalDistribution dist = new NormalDistribution();
        double u = dist.inverseCumulativeProbability(1.0 - pvalue);
        System.out.print(String.format("Quantile u_1-pvalue: %f", u));

        // if and only if u_1-pvalue >= U then winRation > p0
        return U >= u;
    }

    public static void main(String[] args){
        int N = 100;

        System.out.print("H1: ");
        if(testIfWinsPercentageTimes(testWinRatio(GameConst.MONTE_CARLO_TREE_SEARCH_WANING_EXPLORATION,
        GameConst.MONTE_CARLO_TREE_SEARCH_STANDARD, N), 0.7, N, 0.1)) {
            System.out.print("true");
        } else {
            System.out.print("false");
        }

        System.out.print("H2: ");
        MonteCarlo.setCFastWins(0.65);
        if(testIfWinsPercentageTimes(testWinRatio(GameConst.MONTE_CARLO_TREE_SEARCH_FAST_WINS,
        GameConst.MONTE_CARLO_TREE_SEARCH_STANDARD, N), 0.60, N, 0.1)) {
            System.out.print("true");
        } else {
            System.out.print("false");
        }

        // TODO: beta
        System.out.print("H3: ");
        if(testIfWinsPercentageTimes(testWinRatio(GameConst.MONTE_CARLO_TREE_SEARCH_STANDARD,
        GameConst.MONTE_CARLO_TREE_SEARCH_HEURISTICS, N), 0.65, N, 0.1)) {
            System.out.print("true");
        } else {
            System.out.print("false");
        }

        System.out.print("H4: ");
        MonteCarlo.setCFastWins(0.8);
        if(testIfWinsPercentageTimes(testWinRatio(GameConst.MONTE_CARLO_TREE_SEARCH_WANING_EXPLORATION,
        GameConst.MONTE_CARLO_TREE_SEARCH_FAST_WINS, N), 0.7, N, 0.1)) {
            System.out.print("true");
        } else {
            System.out.print("false");
        }
    }
}
