package controllers.iterativedeepersearch;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created on 2018/5/7.
 * Description:
 *
 * @author iznauy
 */
public class Agent extends AbstractPlayer {

    private LinkedList<Types.ACTIONS> actions = new LinkedList<>();

    private LinkedList<StateObservation> visitedObs = new LinkedList<>();

    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer) {
        dls(so);
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        try {
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(actions.size());
        return actions.removeFirst();
    }

    private void dls(StateObservation so) {
        int maxStep = 1;
        while (!dfs(so, maxStep, 0)) {
            actions.clear();
            visitedObs.clear();
            System.out.println(maxStep);
            maxStep += 1;
        }

    }

    private boolean dfs(StateObservation stateObs, int maxStep, int currentStep) {

        if (maxStep == currentStep)
            return stateObs.isGameOver() && stateObs.getGameWinner() == Types.WINNER.PLAYER_WINS;

        visitedObs.add(stateObs);

        for (Types.ACTIONS action: stateObs.getAvailableActions()) {
            StateObservation stateObsCopy = stateObs.copy();
            stateObsCopy.advance(action);

            if (isVisited(stateObsCopy)) continue; // has visited

            actions.add(action);
            if (dfs(stateObsCopy, maxStep, currentStep + 1))
                return true;

            actions.removeLast();
        }

        return false;
    }

    private boolean isVisited(StateObservation stateObs) {
        return visitedObs.stream().anyMatch(e -> e.equalPosition(stateObs));
    }

}
