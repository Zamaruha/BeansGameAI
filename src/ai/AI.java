package ai;

public abstract class AI {

	/**
	 * This method returns the name of the AI. Notice two things:
	 * -> The name of the AI may not be changed during the game.
	 * -> Two opposing AIs must have different names.
	 * 
	 * @return Name of the AI.
	 */
	public abstract String getName();

	/**
	 * Most important method: Here you get informed about the opponent's moves and
	 * select your own move.
	 * 
	 * @param enemyIndex
	 *            The index that refers to the field chosen by the enemy in the last action. If this value is 0, than you are the starting player and have to specify the first move.
	 * @return Move to be played.
	 */
	public abstract int getMove(int enemyIndex);

}