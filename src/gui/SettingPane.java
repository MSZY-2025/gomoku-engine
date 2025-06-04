package gui;

import ai.Agent;
import game.GameController;
import game.constant.GameConst;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class is a GUI component, user setting is displayed in this panel
 *
 * @author Cirun Zhang
 * @version 1.0
 */
public class SettingPane extends JFrame {
    SettingPane() {
        Box boxLayout = Box.createVerticalBox();

        //Search tree depth controller
        JLabel tip1 = new JLabel("Search Tree Depth");

        JSpinner depthSpinner = new JSpinner(new SpinnerNumberModel(5, 2, 7, 1));
        JPanel tmp1 = new JPanel();
        tmp1.add(tip1);
        tmp1.add(depthSpinner);

        //Ai agent controller
        JPanel tmp2 = new JPanel();
        JComboBox<String> comboBox = new JComboBox<>();
        JLabel tip2 = new JLabel("AI strategy");
        comboBox.addItem("Greedy best-first search");
        comboBox.addItem("Minimax Search");
        comboBox.addItem("Alpha beta pruning");
        comboBox.addItem("Transposition search");
        comboBox.addItem("Killer heuristic");
        comboBox.addItem("Threat space search");
        comboBox.addItem("Standard MCTS");
        comboBox.addItem("MCTS with waning exploration factor");
        comboBox.addItem("MCTS with advantaging fast wins");
        comboBox.addItem("Heuristics based MCTS");
        comboBox.setSelectedIndex(2);
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int aiIndex = comboBox.getSelectedIndex();
                if (aiIndex == GameConst.MONTE_CARLO_TREE_SEARCH_STANDARD ||
                    aiIndex == GameConst.MONTE_CARLO_TREE_SEARCH_WANING_EXPLORATION ||
                    aiIndex == GameConst.MONTE_CARLO_TREE_SEARCH_FAST_WINS ||
                    aiIndex == GameConst.MONTE_CARLO_TREE_SEARCH_HEURISTICS ||
                    aiIndex == GameConst.BEST_FIRST) {
                    depthSpinner.setEnabled(false);
                } else {
                    depthSpinner.setEnabled(true);
                }
            }
        });
        tmp2.add(tip2);
        tmp2.add(comboBox);

        //Human First or Computer First
        JPanel tmp3 = new JPanel();
        JLabel tip3 = new JLabel("Who move first: ");
        JComboBox<String> comboBox2 = new JComboBox<>();
        comboBox2.addItem("Human");
        comboBox2.addItem("Computer");
        comboBox2.setSelectedIndex(0);
        tmp3.add(tip3);
        tmp3.add(comboBox2);

        //Confirm button
        JPanel tmp4 = new JPanel();
        JButton confirmButton = new JButton("ok");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int depth = (int)depthSpinner.getValue();
                int aiIndex = comboBox.getSelectedIndex();
                int firstMove = comboBox2.getSelectedIndex();
                Agent.setMaximumSearchDepth(depth);
                GameController.setAiIndex(aiIndex);
                GameController.setGameInProgress(true);
                setWhichPlayerMoveFirst(firstMove);
                MainFrame.resetGame();
                Background.addMessage("Search tree depth: " + depth);
                Background.addMessage("AI strategy: " + comboBox.getSelectedItem());
                dispose();
            }
        });
        tmp4.add(confirmButton);

        boxLayout.add(tmp1);
        boxLayout.add(tmp2);
        boxLayout.add(tmp3);
        boxLayout.add(tmp4);
        this.setSize(300, 200);
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.add(boxLayout);
        this.setVisible(false);
    }

    /**
     * Sets which player will move first
     *
     * @param firstMove The index of the first player
     */
    private void setWhichPlayerMoveFirst(int firstMove) {
        if (firstMove == GameConst.HUMAN_MOVE_FIRST) {
            GameController.setHumanFirst(true);
        } else {
            GameController.setHumanFirst(false);
        }
    }
}
