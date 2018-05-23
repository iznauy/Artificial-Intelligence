package controllers.depthfirst;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created on 2018/5/4.
 * Description:
 *
 * @author iznauy
 */
public class Agent extends AbstractPlayer {

    private LinkedList<Types.ACTIONS> actions = new LinkedList<>();

    private LinkedList<StateObservation> visitedObs = new LinkedList<>();

    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer) {
        dfs(so);
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        try {
            Thread.sleep(80);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(actions.size());
        return actions.removeFirst();
    }

    private boolean dfs(StateObservation stateObs) {
        visitedObs.add(stateObs);

        if (stateObs.isGameOver()) // game over, win or not?
            return stateObs.getGameWinner() == Types.WINNER.PLAYER_WINS;

        for (Types.ACTIONS action: stateObs.getAvailableActions()) {
            StateObservation stateObsCopy = stateObs.copy();
            stateObsCopy.advance(action);

            if (isVisited(stateObsCopy)) continue; // has visited

            actions.add(action);
            if (dfs(stateObsCopy))
                return true;

            actions.removeLast();
        }

        return false;
    }

    private boolean isVisited(StateObservation stateObs) {
        return visitedObs.stream().anyMatch(e -> e.equalPosition(stateObs));
    }

}
