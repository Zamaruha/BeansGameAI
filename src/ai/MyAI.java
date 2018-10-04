package ai;

import java.util.ArrayList;

public class MyAI extends AI {
    private Board board = new Board();
    private boolean redSide = false;
    private int bestMoveIndex = -1;
    private final int ALPHA = -1000;
    private final int BETA = 1000;
    private byte LEVEL = 14; // max 0.9, avg .0
    // private byte LEVEL = 15; // max - 4.0, avg - 1.2
    // private byte LEVEL = 16; // max - 7.6, avg - 2.0

    @Override public String getName() {
        return "StandardAI";
    }

    @Override public int getMove(int enemyIndex) {
        redSide = enemyIndex < 1 ? true : redSide;
        makeStep(enemyIndex);
        int index = getAlphaBetaIndex();
        makeStep(index);
        return index;
    }

    private void makeStep(int index) {
        board.makeStep(index);
    }

    private int getAlphaBetaIndex() {
        bestMoveIndex = -1;
        max(true, LEVEL, 0, ALPHA, BETA);
        if (bestMoveIndex == -1) {
            System.out.println("END");
            return -1;
        } else {
            return bestMoveIndex;
        }
    }

    private int min(boolean myTurn, int depth, int moveIndex, int alpha, int beta) {
        if (depth == 0 || !board.isAnyMoveLeft(myTurn, redSide)) {
            return getStepHeuristic(moveIndex);
        }
        ArrayList<Integer> moves = board.getAllPossibleMoves(myTurn, redSide);
        while (!moves.isEmpty()) {
            int currentMove = moves.get(0);
            moves.remove(0);
            Board currentboard = new Board(board);
            makeStep(currentMove);
            int value = max(!myTurn, depth - 1, currentMove, alpha, beta);

            if (value < beta) {
                beta = value;
                if (beta <= alpha) {
                    break;
                }
            }
            this.board = new Board(currentboard);
        }
        return beta;
    }

    private int max(boolean myTurn, int depth, int moveIndex, int alpha, int beta) {
        if (depth == 0 || !board.isAnyMoveLeft(myTurn, redSide)) {
            return getStepHeuristic(moveIndex);
        }
        ArrayList<Integer> moves = board.getAllPossibleMoves(myTurn, redSide);
        while (!moves.isEmpty()) {
            int currentMove = moves.get(0);
            moves.remove(0);
            Board currentboard = new Board(board);
            makeStep(currentMove);
            int value = min(!myTurn, depth - 1, currentMove, alpha, beta);

            if (value > alpha) {
                alpha = value;
                if (alpha >= beta)
                    break;
                if (depth == LEVEL)
                    bestMoveIndex = currentMove;
            }
            this.board = new Board(currentboard);
        }
        return alpha;
    }

    private int getStepHeuristic(int move) {
        int heuristicValue = 0;
        if (redSide) {
            heuristicValue += board.getPointsRed();
            heuristicValue -= 2 * board.getPointsBlue();

            // potentially protect own endangered beans {1,3,5}
            for (int i = 0; i < 6; i++) {
                int endangeredIndex = board.getGameState()[i];
                if (endangeredIndex == 1 || endangeredIndex == 3 || endangeredIndex == 5) {
                    for (int j = 6; j <= 11; j++) {
                        int potentialDangerSource = board.getGameState()[j];
                        if (potentialDangerSource == j - i || potentialDangerSource == 12 + j - i) {
                            heuristicValue -= endangeredIndex * 4;
                        }
                    }
                }
            }

            // potentially attack enemy's endangered beans {1,3,5}
            for (int i = 6; i <= 11; i++) {
                int endangeredIndex = board.getGameState()[i];
                if (endangeredIndex == 1 || endangeredIndex == 3 || endangeredIndex == 5) {
                    for (int j = 0; j < 6; j++) {
                        int potentialDangerSource = board.getGameState()[j];
                        if (potentialDangerSource == j - i || potentialDangerSource == 12 + j - i) {
                            heuristicValue += potentialDangerSource * 4;
                        }
                    }
                }
            }

            // rich fields
            for (int i = 0; i < 6; i++) {
                if (board.getGameState()[i] >= 6) {
                    heuristicValue += 2;
                }
            }
            for (int i = 6; i <= 11; i++) {
                if (board.getGameState()[i] >= 6) {
                    heuristicValue -= 2;
                }
            }

            // rich fields when little opportunities
            if (board.getGameState()[move - 1] >= 10 && move - 1 < 6) {
                heuristicValue += 6;
            }
            if (board.getGameState()[move - 1] >= 10 && move - 1 >= 6) {
                heuristicValue -= 6;
            }

            // empty fields
            int count = 0;
            for (int i = 0; i < 6; i++) {
                if (board.getGameState()[i] == 0) {
                    count++;
                }
            }
            switch (count) {
                case 0:
                    heuristicValue += 4;
                    break;
                case 1:
                    heuristicValue += 2;
                    break;
                case 2:
                    break;
                case 3:
                    heuristicValue -= 2;
                    break;
                case 4:
                    heuristicValue -= 6;
                    break;
                case 5:
                    heuristicValue -= 10;
                    break;
                case 6:
                    heuristicValue -= 14;
                    break;
            }

            // enemy's empty fields
            count = 0;
            for (int i = 6; i <= 11; i++) {
                if (board.getGameState()[i] == 0) {
                    count++;
                }
            }
            switch (count) {
                case 0:
                    heuristicValue -= 4;
                    break;
                case 1:
                    heuristicValue -= 2;
                    break;
                case 2:
                    break;
                case 3:
                    heuristicValue += 2;
                    break;
                case 4:
                    heuristicValue += 6;
                    break;
                case 5:
                    heuristicValue += 10;
                    break;
                case 6:
                    heuristicValue += 14;
                    break;
            }
        } else {
            heuristicValue += board.getPointsBlue();
            heuristicValue -= 2 * board.getPointsRed();

            // potentially attack enemy's endangered beans {1,3,5}
            for (int i = 0; i < 6; i++) {
                int endangeredIndex = board.getGameState()[i];
                if (endangeredIndex == 1 || endangeredIndex == 3 || endangeredIndex == 5) {
                    for (int j = 6; j <= 11; j++) {
                        int potentialDangerSource = board.getGameState()[j];
                        if (potentialDangerSource == j - i || potentialDangerSource == 12 + j - i) {
                            heuristicValue += endangeredIndex * 4;
                        }
                    }
                }
            }

            // potentially protect own endangered beans {1,3,5}
            for (int i = 6; i <= 11; i++) {
                int endangeredIndex = board.getGameState()[i];
                if (endangeredIndex == 1 || endangeredIndex == 3 || endangeredIndex == 5) {
                    for (int j = 0; j < 6; j++) {
                        int potentialDangerSource = board.getGameState()[j];
                        if (potentialDangerSource == j - i || potentialDangerSource == 12 + j - i) {
                            heuristicValue -= potentialDangerSource * 4;
                        }
                    }
                }
            }

            // rich fields
            for (int i = 6; i <= 11; i++) {
                if (board.getGameState()[i] >= 6) {
                    heuristicValue += 2;
                }
            }
            for (int i = 0; i < 6; i++) {
                if (board.getGameState()[i] >= 6) {
                    heuristicValue -= 2;
                }
            }

            // rich fields on low opportunities
            if (board.getGameState()[move - 1] >= 10 && move - 1 >= 6) {
                heuristicValue += 6;
            }
            if (board.getGameState()[move - 1] >= 10 && move - 1 < 6) {
                heuristicValue -= 6;
            }

            // empty fields
            int count = 0;
            for (int i = 6; i <= 11; i++) {
                if (board.getGameState()[i] == 0) {
                    count++;
                }
            }
            switch (count) {
                case 0:
                    heuristicValue += 4;
                    break;
                case 1:
                    heuristicValue += 2;
                    break;
                case 2:
                    break;
                case 3:
                    heuristicValue -= 2;
                    break;
                case 4:
                    heuristicValue -= 4;
                    break;
                case 5:
                    heuristicValue -= 6;
                    break;
                case 6:
                    heuristicValue -= 8;
                    break;
            }

            //  empty fields on enemy's side
            count = 0;
            for (int i = 0; i < 6; i++) {
                if (board.getGameState()[i] == 0) {
                    count++;
                }
            }
            switch (count) {
                case 0:
                    heuristicValue -= 4;
                    break;
                case 1:
                    heuristicValue -= 2;
                    break;
                case 2:
                    break;
                case 3:
                    heuristicValue += 2;
                    break;
                case 4:
                    heuristicValue += 4;
                    break;
                case 5:
                    heuristicValue += 6;
                    break;
                case 6:
                    heuristicValue += 8;
                    break;
            }
        }
        return heuristicValue;
    }
}
