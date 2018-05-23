package controllers.limitdepthfirst;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created on 2018/5/7.
 * Description:
 *
 * @author iznauy
 */
public class Agent extends AbstractPlayer {

    static class Pair<L, R> {
        L l;
        R r;

        Pair() {
        }

        Pair(L l, R r) {
            this.l = l;
            this.r = r;
        }
    }

    private List<StateObservation> visitedStates = new LinkedList<>();

    private List<StateObservation> previousStates = new LinkedList<>();

    private boolean isVisited(StateObservation stateObs) {
        return visitedStates.stream().anyMatch(e -> e.equalPosition(stateObs));
    }

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        previousStates.add(stateObs.copy());
        long totalTime = elapsedTimer.remainingTimeMillis() + elapsedTimer.elapsedMillis();
        Types.ACTIONS action =  dls(stateObs, elapsedTimer, totalTime, 0, 25).l;

        System.out.println(visitedStates.size());
        visitedStates.clear();
        visitedStates.addAll(previousStates);
        System.out.println(visitedStates.size());
        return action;
    }

    private double evaluate(StateObservation stateObs) { // 越小越好
        ArrayList<Observation>[] fixedPositions = stateObs.getImmovablePositions();
        ArrayList<Observation>[] movingPositions = stateObs.getMovablePositions();
  //      int holeCount = movingPositions[1].size();
        Vector2d goalPos = fixedPositions[1].get(0).position;
        Vector2d keyPos = movingPositions[0].get(0).position;
        Vector2d avatarPos = stateObs.getAvatarPosition();

        boolean end = false;

        for (Observation observation: movingPositions[1]) {
            if (observation.position.dist(keyPos) <= 1.0)
                end = true;
        }

        if (end)
            return 214748.0;
  //      boolean fin

        return keyPos.dist(avatarPos) + keyPos.dist(goalPos);// + holeCount; // 按照钥匙和小精灵的位置计算，还要额外加上钥匙和目标的距离；如果
                                                                // 拿到了钥匙，距离事实上是小精灵和目标的距离，此外，如果能消除了洞自然是坠好的
    }

    private Pair<Types.ACTIONS, Double> dls(StateObservation stateObs, ElapsedCpuTimer elapsedCpuTimer,
                                            long totalTime, int step, int maxStep) {

        visitedStates.add(stateObs);

        if (stateObs.isGameOver()) // find a solution or loss the game
            return new Pair<>(null, stateObs.getGameWinner() == Types.WINNER.PLAYER_WINS ? 0.0 : 211048.0);

        double eval = evaluate(stateObs);

        if (elapsedCpuTimer.remainingTimeMillis() * 1.0 / totalTime < 0.2 || step == maxStep) // only 1% time or up to the maxStep
            return new Pair<>(null, eval);

        Pair<Types.ACTIONS, Double> pair = null;

        for (Types.ACTIONS action: stateObs.getAvailableActions()) {
            StateObservation stateObsCopy = stateObs.copy();
            stateObsCopy.advance(action);

            if (isVisited(stateObsCopy)) // 已经访问过了
                continue;

            Pair<Types.ACTIONS, Double> result = dls(stateObs, elapsedCpuTimer, totalTime, step + 1, maxStep);
            result.l = action;

            if (pair == null || pair.r >= result.r) {
                pair = result;
            }

        }
        if (pair == null)
            return new Pair<>(null, 211048.0);
        if (pair.r > eval)
            pair.r = eval;

        return pair;
    }

}
