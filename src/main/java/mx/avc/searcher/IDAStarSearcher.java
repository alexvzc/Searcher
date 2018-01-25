/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mx.avc.searcher;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 *
 * @author alexv
 */
public class IDAStarSearcher<S, M> extends AbstractAStarSearcher<S, M> {

    private static final float LOAD_FACTOR = 0.7F;

    private Deque<StateData<S, M>> statesToInspect;
    private Queue<StateData<S, M>> fringeStates;
    private Set<S> generatedStates;
    private Set<S> finalStates;

    public IDAStarSearcher(StateController<S, M> state_controller) {
        super(state_controller);
    }

    @Override
    public List<? extends M> search(S initial_state, Set<S> final_states) {

        statesToInspect = new LinkedList<>();
        fringeStates = new LinkedList<>();
        generatedStates = new HashSet<>(1 << 10, LOAD_FACTOR);
        finalStates = final_states;

        float cost_limit =
                stateController.getDistance(initial_state, finalStates);
        StateData<S, M> state_data = new StateData<>(initial_state, cost_limit);
        state_data.fScore = state_data.hScore;
        statesToInspect.addFirst(state_data);
        generatedStates.add(initial_state);

        do {
            float new_cost_limit = Float.POSITIVE_INFINITY;

            while(!statesToInspect.isEmpty()) {
                state_data = statesToInspect.remove();

                S current_state = state_data.state;
                if(finalStates.contains(current_state)) {
                    return buildReversePath(state_data);
                }

                float state_data_f_score = state_data.fScore;

                if(state_data_f_score <= cost_limit) {
                    generateNextStates(state_data);
                } else {
                    fringeStates.add(state_data);

                    if(state_data_f_score < new_cost_limit) {
                        new_cost_limit = state_data_f_score;
                    }
                }
            }

            while(!fringeStates.isEmpty()) {
                state_data = fringeStates.remove();

                generateNextStates(state_data);
            }

            cost_limit = new_cost_limit;

        } while(cost_limit < Float.POSITIVE_INFINITY);

        throw new UnreachableStateException();
    }

    private void generateNextStates(StateData<S, M> state_data) {

        S current_state = state_data.state;
        Set<? extends M> possible_moves =
                stateController.nextMovements(current_state);

        for(M next_move : possible_moves) {
            S next_state = stateController.nextState(
                    current_state, next_move);
            if(!generatedStates.contains(next_state)) {
                float cost = stateController.getCost(
                        current_state, next_move);
                float h_score = stateController.getDistance(
                        next_state, finalStates);
                float g_score = state_data.gScore + cost;
                float f_score = g_score + h_score;

                StateData<S, M> next_state_data =
                        new StateData<>(next_state, h_score);
                next_state_data.fromStateData = state_data;
                next_state_data.movement = next_move;
                next_state_data.gScore = g_score;
                next_state_data.fScore = f_score;
                statesToInspect.addFirst(next_state_data);
                generatedStates.add(next_state);
            }
        }
    }

}
