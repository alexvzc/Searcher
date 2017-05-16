/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.avc.searcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 *
 * @author alexv
 */
public class AStarSearcher<S, M> extends AbstractAStarSearcher<S, M> {

    private Queue<StateData<S, M>> statesToInspect;

    private Map<S, StateData<S, M>> generatedStates;

    private Set<S> finalStates;

    public AStarSearcher(StateController<S, M> state_controller) {
        super(state_controller);
    }

    public List<? extends M> search(S initial_state, Set<S> final_states) {
        statesToInspect =
                new PriorityQueue<StateData<S, M>>(11, new StateComparator());
        generatedStates = new HashMap<S, StateData<S, M>>();
        finalStates = final_states;

        StateData<S, M> state_data = new StateData<S, M>(initial_state,
                stateController.getDistance(initial_state, finalStates));
        state_data.fScore = state_data.hScore;
        generatedStates.put(initial_state, state_data);

        statesToInspect.add(state_data);

        while(!statesToInspect.isEmpty()) {
            state_data = statesToInspect.remove();

            S current_state = state_data.state;
            if(finalStates.contains(current_state)) {
                return buildReversePath(state_data);
            }

            state_data.visited = true;

            generateStates(state_data);
        }

        throw new UnreachableStateException();
    }

    private void generateStates(StateData<S, M> state_data) {
        S current_state = state_data.state;
        Set<? extends M> possible_moves = stateController.nextMovements(
                current_state);

        for(M next_move : possible_moves) {
            S next_state = stateController.nextState(current_state, next_move);
            float cost = stateController.getCost(current_state, next_move);
            float g_score = state_data.gScore + cost;

            StateData<S, M> next_state_data;

            if(!generatedStates.containsKey(next_state)) {
                next_state_data = new StateData<S, M>(next_state,
                        stateController.getDistance(next_state,
                        finalStates));
                next_state_data.fromStateData = state_data;
                next_state_data.movement = next_move;
                next_state_data.gScore = g_score;
                next_state_data.fScore = g_score + next_state_data.hScore;
                generatedStates.put(next_state, next_state_data);
                statesToInspect.add(next_state_data);
            } else {
                next_state_data = generatedStates.get(next_state);
                if(!next_state_data.visited) {
                    if(g_score < next_state_data.gScore) {
                        statesToInspect.remove(next_state_data);
                        next_state_data.fromStateData = state_data;
                        next_state_data.movement = next_move;
                        next_state_data.gScore = g_score;
                        next_state_data.fScore =
                                g_score + next_state_data.hScore;
                        statesToInspect.add(next_state_data);
                    }
                }
            }
        }
    }

}
