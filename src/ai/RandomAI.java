package ai;

import java.util.Random;

public class RandomAI extends AI {

    Random rand = new Random();

    public int getMove(int enemyIndex) {
        System.out.println(enemyIndex);
        int index = 0;
        // have to choose the first move
        if (enemyIndex == -1) {

            index = rand.nextInt(6) + 1;
        }
        // enemy acted and i have to react
        else if (enemyIndex > 0 && enemyIndex <= 6) {
            index = rand.nextInt(6) + 7;
        } else if (enemyIndex > 6 && enemyIndex <= 12) {
            index = rand.nextInt(6) + 1;
        }
        return index;
    }

    @Override public String getName() {
        return "RANDOM";
    }

}
