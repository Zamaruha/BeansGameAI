package ai;

import java.util.ArrayList;

public class MyAI extends AI {
    byte LEVEL = 15;
    Board original = new Board();
    boolean firstPlayer = false;
    int bestMoveIndex = -1;

    @Override public String getName() {
        return "StandardAI";
    }




    /**
     * @param enemyIndex The index that refers to the field chosen by the enemy in the last action.If
     *                   this value is 0, than the AI is the starting player and has to specify the first move.
     * @return Return The index that refers to the field of the action chosen by this AI.
     */
    public int getMove(int enemyIndex) {
        int index = 0;
        // This Ai starts the game
        if (enemyIndex < 1) {
            firstPlayer = true;
            System.out.println("Lets play!");
        }

        // represent enemies move on Board
        original.makeStep(enemyIndex);

        // choose own move
        index = getAlphaBeta();

        // represent own move on Board
        original.makeStep(index);

        // send own move
        return index;

    }

    /**
     * Code from Wikipedia for Alpha-Beta-Pruning-Strategy
     *
     * @return move index
     */
    private int getAlphaBeta() {
        bestMoveIndex = -1;
        max(true, LEVEL, 0, -1000, 1000);

        if (bestMoveIndex == -1) {
            System.out.println("END");
            return -1;
        } else {
            return bestMoveIndex;
        }
    }

    /**
     * Code from Wikipedia for Alpha-Beta-Pruning-Strategy
     *
     * @param myTurn
     * @param depth
     * @param move
     * @return
     */
    private int max(boolean myTurn, int depth, int move, int alpha, int beta) {
        if (depth == 0 || !original.isAnyMoveLeft(myTurn, firstPlayer)) {
            return evaluateStep(move);
        }

        int maxWert = alpha;
        ArrayList<Integer> moves = original.getAllPossibleMoves(myTurn, firstPlayer);
        while (!moves.isEmpty()) {
            int currentMove = moves.get(0);
            moves.remove(0);
            Board currentboard = new Board(original);
            makeStep(currentMove);
            int wert = min(!myTurn, depth - 1, currentMove, maxWert, beta);

            if (wert > maxWert) {
                maxWert = wert;
                if (maxWert >= beta)
                    break;
                if (depth == LEVEL)
                    bestMoveIndex = currentMove;
            }
            undoStep(currentboard);
        }
        // System.out.println(maxWert);
        return maxWert;
    }

    /**
     * Code from Wikipedia for Alpha-Beta-Pruning-Strategy
     *
     * @param myTurn
     * @param depth
     * @param move
     * @return
     */
    private int min(boolean myTurn, int depth, int move, int alpha, int beta) {
        if (depth == 0 || !original.isAnyMoveLeft(myTurn, firstPlayer)) {
            return evaluateStep(move);
        }

        int minWert = beta;
        ArrayList<Integer> moves = original.getAllPossibleMoves(myTurn, firstPlayer);
        while (!moves.isEmpty()) {
            int currentMove = moves.get(0);
            moves.remove(0);
            Board currentboard = new Board(original);
            makeStep(currentMove);
            int wert = max(!myTurn, depth - 1, currentMove, alpha, minWert);

            if (wert < minWert) {
                minWert = wert;
                if (minWert <= alpha) {
                    break;
                }
            }
            undoStep(currentboard);
        }
        //  System.out.println(minWert);
        return minWert;
    }

    /**
     * @param index
     */
    private void makeStep(Integer index) {
        original.makeStep(index);
    }

    /**
     * undo the move
     *
     * @param board
     */
    private void undoStep(Board board) {
        original = new Board(board);
    }

    /**
     * check, if the regarded player or the opponent has only empty fields; value-=15 if player has
     * them, value+=15 if opponent has them, return original value if none is the case (or both is)
     *
     * @param value
     * @param k
     * @return
     */
    private int checkEmptyField(int value, int k) {

        // only empty fields on my side is critical
        int count = 0;
        for (int i = 0; i < 6; i++) {
            if (original.getGameState()[i] == 0) {
                count++;
            }
        }
        if (k == 2) {
            if (count == 6) {
                value -= 15;
            }
        } else {
            if (count == 6) {
                value += 15;
            }
        }

        // only empty fields on opponent's side is great
        count = 0;
        for (int i = 6; i < 12; i++) {
            if (original.getGameState()[i] == 0) {
                count++;
            }
        }
        if (k == 2) {
            if (count == 6) {
                value += 15;
            }
        } else {
            if (count == 6) {
                value -= 15;
            }
        }

        return value;
    }

    private int evaluateStep(int move) {

        int value = 0;
        if (firstPlayer) {
            //do I win beans? does the opponent win beans?
            value += 0.5 * original.getPointsRed();
            value -= original.getPointsBlue();

            //attackable own 1, 3 and 5 are problematic
            for (int i = 0; i < 6; i++) {
                if (original.getGameState()[i] == 1 || original.getGameState()[i] == 3
                    || original.getGameState()[i] == 5) {
                    for (int j = 6; j <= 11; j++) {
                        if (original.getGameState()[j] == j - i
                            || original.getGameState()[j] == 12 + j - i) {
                            value -= original.getGameState()[i] * 2;
                        }
                    }
                }
            }

            //attackable 1, 3 and 5 on opponent's side are good
            for (int i = 6; i <= 11; i++) {
                if (original.getGameState()[i] == 1 || original.getGameState()[i] == 3
                    || original.getGameState()[i] == 5) {
                    for (int j = 0; j < 6; j++) {
                        if (original.getGameState()[j] == j - i
                            || original.getGameState()[j] == 12 + j - i) {
                            value += original.getGameState()[i] * 2;
                        }
                    }
                }
            }

            //too many own empty fields are bad
            int count = 0;
            for (int i = 0; i < 6; i++) {
                if (original.getGameState()[i] == 0) {
                    count++;
                }
            }
            switch (count) {
                case 0:
                    value += 2;
                    break;
                case 1:
                    value++;
                    break;
                case 2:
                    break;
                case 3:
                    value--;
                    break;
                case 4:
                    value -= 3;
                    break;
                case 5:
                    value -= 5;
                    break;
                case 6:
                    value -= 7;
                    break;
            }

            //many empty fields on opponent's side are good
            count = 0;
            for (int i = 6; i <= 11; i++) {
                if (original.getGameState()[i] == 0) {
                    count++;
                }
            }
            switch (count) {
                case 0:
                    value -= 2;
                    break;
                case 1:
                    value--;
                    break;
                case 2:
                    break;
                case 3:
                    value++;
                    break;
                case 4:
                    value += 3;
                    break;
                case 5:
                    value += 5;
                    break;
                case 6:
                    value += 7;
                    break;
            }


            //owning fields with a lot beans is good, it's bad, if the opponent has some
            for (int i = 0; i < 6; i++) {
                if (original.getGameState()[i] >= 6) {
                    value++;
                }
            }
            for (int i = 6; i <= 11; i++) {
                if (original.getGameState()[i] >= 6) {
                    value--;
                }
            }

            //using high numbers if one's low on opportunities is good
            if (original.getGameState()[move - 1] >= 10 && move - 1 < 6) {
                value += 3;
            }
            if (original.getGameState()[move - 1] >= 10 && move - 1 >= 6) {
                value -= 3;
            }
        } else {
            //do I win beans? does the opponent win beans?
            //  value += 2*future.getPointsBlue()-copy.getPointsBlue();
            //  value -= 4*future.getPointsRed()-copy.getPointsRed();

            value += 0.5 * original.getPointsBlue();
            value -= original.getPointsRed();

            //attackable own 1, 3 and 5 are problematic
            for (int i = 6; i <= 11; i++) {
                if (original.getGameState()[i] == 1 || original.getGameState()[i] == 3
                    || original.getGameState()[i] == 5) {
                    for (int j = 0; j < 6; j++) {
                        if (original.getGameState()[j] == j - i
                            || original.getGameState()[j] == 12 + j - i) {
                            value -= original.getGameState()[i] * 2;
                        }
                    }
                }
            }

            //attackable 1, 3 and 5 on opponent's side are good
            for (int i = 0; i < 6; i++) {
                if (original.getGameState()[i] == 1 || original.getGameState()[i] == 3
                    || original.getGameState()[i] == 5) {
                    for (int j = 6; j <= 11; j++) {
                        if (original.getGameState()[j] == j - i
                            || original.getGameState()[j] == 12 + j - i) {
                            value += original.getGameState()[i] * 2;
                        }
                    }
                }
            }

            //too many own empty fields are bad
            int count = 0;
            for (int i = 6; i <= 11; i++) {
                if (original.getGameState()[i] == 0) {
                    count++;
                }
            }
            switch (count) {
                case 0:
                    value += 2;
                    break;
                case 1:
                    value++;
                    break;
                case 2:
                    break;
                case 3:
                    value--;
                    break;
                case 4:
                    value -= 2;
                    break;
                case 5:
                    value -= 3;
                    break;
                case 6:
                    value -= 4;
                    break;
            }

            //many empty fields on opponent's side are good
            count = 0;
            for (int i = 0; i < 6; i++) {
                if (original.getGameState()[i] == 0) {
                    count++;
                }
            }
            switch (count) {
                case 0:
                    value -= 2;
                    break;
                case 1:
                    value--;
                    break;
                case 2:
                    break;
                case 3:
                    value++;
                    break;
                case 4:
                    value += 2;
                    break;
                case 5:
                    value += 3;
                    break;
                case 6:
                    value += 4;
                    break;
            }

            //owning fields with a lot beans is good, it's bad, if the opponent has some
            for (int i = 6; i <= 11; i++) {
                if (original.getGameState()[i] >= 6) {
                    value++;
                }
            }
            for (int i = 0; i < 6; i++) {
                if (original.getGameState()[i] >= 6) {
                    value--;
                }
            }

            //using high numbers if one's low on opportunities is good
            if (original.getGameState()[move - 1] >= 10 && move - 1 >= 6) {
                value += 3;
            }
            if (original.getGameState()[move - 1] >= 10 && move - 1 < 6) {
                value -= 3;
            }
        }
        return value;
    }
}
