package ai;

import java.util.ArrayList;

public class Board {
    private byte[] gameState = new byte[12];
    private byte pointsRed = 0;
    private byte pointsBlue = 0;

    public Board() {
        for (int i = 0; i < gameState.length; i++) {
            gameState[i] = 6;
        }
        pointsBlue = 0;
        pointsRed = 0;
    }

    public Board(Board toCopy) {
        // TODO replace
        for (int i = 0; i < 12; i++) {
            this.gameState[i] = toCopy.gameState[i];
        }
        this.pointsRed = toCopy.pointsRed;
        this.pointsBlue = toCopy.pointsBlue;
    }

    @Override public String toString() {
        return "Board{" + "gameState=" + gameState + ", pointsRed=" + pointsRed + ", pointsBlue="
            + pointsBlue + '}';
    }

    public byte getPointsRed() {
        return pointsRed;
    }

    public byte getPointsBlue() {
        return pointsBlue;
    }

    public void setGameState(byte[] gameState) {
        this.gameState = gameState;
    }

    public void setPointsRed(byte pointsRed) {
        this.pointsRed = pointsRed;
    }

    public void setPointsBlue(byte pointsBlue) {
        this.pointsBlue = pointsBlue;
    }

    public byte[] getGameState() {
        return gameState;
    }

    public void makeStep(int stepIndex) {
        int insideStepIndex = stepIndex - 1;
        // if stepIndex is invalid
        if (stepIndex == -1) {
            return;
        } else if (stepIndex < 1 || stepIndex > 12) {
            // TODO remove
            throw new Error("out ouf bounds");
        }
        // decide on player color
        boolean redPlayer;
        if (stepIndex < 6) {
            redPlayer = true;
        } else {
            redPlayer = false;
        }
        //System.out.println(this);
        // get amount of beans
        int amount = gameState[insideStepIndex];
       // System.out.println(insideStepIndex);
        gameState[insideStepIndex] = 0;

        // TODO remove
        if (amount == 0) {
            throw new Error("amount == 0");
        }

        for (int i = 0; i < amount; i++) {
            gameState[(stepIndex + i) % 12]++;
        }

        int lastPosition = (insideStepIndex + amount) % 12;
        while (gameState[lastPosition] == 2 || gameState[lastPosition] == 4
            || gameState[lastPosition] == 6) {
            int points = gameState[lastPosition];
            gameState[lastPosition] = 0;
            if (redPlayer) {
                pointsRed += points;
            } else {
                pointsBlue += points;
            }
            lastPosition--;
            if (lastPosition == -1) {
                lastPosition = 11;
            }
        }
    }
    
    public boolean isAnyMoveLeft(boolean myTurn, boolean redPlayer) {
        if ((myTurn && redPlayer) || (!myTurn && !redPlayer)) {
            for (int i = 0; i < 6; i++) {
                if (gameState[i] > 0) {
                    return true;
                }
            }
        } else if ((myTurn && !redPlayer) || (!myTurn && redPlayer)) {
            for (int i = 6; i < 12; i++) {
                if (gameState[i] > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public ArrayList<Integer> getAllPossibleMoves(boolean myTurn, boolean redPlayer) {
        ArrayList<Integer> validMoves = new ArrayList<Integer>();
        if ((myTurn && redPlayer) || (!myTurn && !redPlayer)) {
            for (int i = 0; i < 6; i++) {
                if (gameState[i] > 0) {
                    validMoves.add(i + 1);
                }
            }
        } else if ((myTurn && !redPlayer) || (!myTurn && redPlayer)) {
            for (int i = 6; i < 12; i++) {
                if (gameState[i] > 0) {
                    validMoves.add(i + 1);
                }
            }
        }
        return validMoves;
    }

    private int[] getTotalBeans() {
        int[] output = new int[2];
        int beansRed = 0;
        int beansBlue = 0;

        for (int i = 0; i < gameState.length; i++) {
            if (i < 6) {
                beansRed += gameState[i];
            } else {
                beansBlue += gameState[i];
            }
        }
        output[0] = beansRed;
        output[1] = beansBlue;
        return output;
    }

}



